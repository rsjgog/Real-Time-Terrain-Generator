package project.renderer;

import project.noise.fBm.*;
import project.noise.multiFractal.*;
import project.noise.noiseUtil.*;
import project.noise.perlinGenerator.*;
import project.noise.voronoi.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;
import project.*;
import java.nio.*;
//import java.nio.FloatBuffer;
import java.lang.Math;
import java.io.*;
import javax.imageio.*;
import project.pathSearch.*;
import java.util.*;

public class RenderingLoop implements GLEventListener, MouseListener, MouseMotionListener, KeyListener{

	//todo: refactor camera and movement to a seperate class,
	
	RenderingTerrainModel terrain, terrainN, terrainS, terrainE, terrainW, terrainNE, terrainNW, terrainSE, terrainSW;
	/* north south east west based on matrix position convention
	 * so x + 1 is south, -1 north, y + 1 east, -1 west
	 */
	float tcSize; //terrain cell size 
	GLU glu;
	GL gl;
	boolean move = false;
	boolean moveBack = false;
	float moveRate = 0;

	private float theta = -1.0f, thi = 30.0f;
	private float angle = 0.0f;

	private int prevMouseX, prevMouseY;
	private boolean mouseRButtonDown = false;
	
	float lightAmbient[]= { 0.0f, 0.0f, 0.0f, 1.0f };
	float lightDiffuse[]= { 0.6f, 0.6f, 0.6f, 1.0f };
	float lightSpecular[]= { 1f, 1f, 1f, 1.0f };
	float lightPosition[]= { 200.0f, 300.0f, 200.0f, 1.0f };
	float cameraPosition[] = { 2000.0f, 1000.0f, 2000.0f };
	float cameraLookAt[] = { 0.0f, -1.0f, 0.0f };

	float fogColour[] = {0.7f, 0.4f, 0.4f, 1.0f};

	int[] textures = new int[1];
	FloatBuffer texCoordsB, alpCoordsB;
	TexTable texT;
	int defTindex0, defTindex1;
	TMFactory tmf;
	long seed;
	int[] cCell;
	int cameraMode = 0;
	int lists;

	Robot hover;	
	protected Tour tour;
	EntAnimator anim;
	Vector anims;

	boolean[] listDone = {	false, false, false,
							false, false, false,
							false, false, false};

	boolean drawTrees = false;
	
	public static void main(String[] args){
                
		//seed = 0l;
		//if( args.length != 0) seed = Long.longValue(parseLong(args[0]));
		Frame frame = new Frame("Multi Terrain");

		//GLCapabilities cap = new GLCapabilities();
		//GLCanvas canvas =
			//GLDrawableFactory.getFactory().createGLCanvas(cap);
		GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(new RenderingLoop());
		frame.add(canvas);

		frame.setSize(800, 600);
		final Animator animator = new Animator(canvas);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
		  
        		new Thread(new Runnable() {
        		 	public void run() {
                		animator.stop();
						System.exit(0);
					}
            	}).start();
			}
    	});

		frame.setVisible(true);
		animator.start();
	}

	protected void popTextureCoords(int size, float stretch, float tile){
		
		float[] texCoords = new float[size * size * 2];
		for(int y = 0; y < size; y++){
			for(int x = 0; x < size; x++){
		//		texCoords[2 * (y * size + x) + 0] = x / stretch;
		//		texCoords[2 * (y * size + x) + 1] = y / stretch;
				texCoords[2 * (y * size + x) + 0] = (float) tile * x / ((size - 1));
				texCoords[2 * (y * size + x) + 1] = (float) tile * y / ((size - 1));
			}
		}
		texCoordsB = BufferUtil.newFloatBuffer(texCoords.length);
		texCoordsB.put(texCoords);
		texCoordsB.rewind();
	}

	protected void popAlphaCoords(int size){
		
		float[] alpCoords = new float[size * size * 2];
		for(int y = 0; y < size; y++){
			for(int x = 0; x < size; x++){
				alpCoords[2 * (y * size + x) + 0] = x / (float)size;
				//System.out.println("x alp coord = " + x /size);
				alpCoords[2 * (y * size + x) + 1] = y / (float)size;
			}
		}
		alpCoordsB = BufferUtil.newFloatBuffer(alpCoords.length);
		alpCoordsB.put(alpCoords);
		alpCoordsB.rewind();
	}

	protected int[] getCell(){
		int[] cell= new int[2];
		cell[0] = (int) Math.floor(cameraPosition[0] / tcSize);
		cell[1] = (int) Math.floor(cameraPosition[2] / tcSize);
//		System.out.println("at cell" + cell[0] + ", " + cell[1]);
//		System.out.println("Cell size " + tcSize + " and Camera position " + cameraPosition[0] + ", " + cameraPosition[2]);
		return cell;
	}
	
	protected int[] getCell(float x, float y){
		int[] cell= new int[2];
		cell[0] = (int) Math.floor(x / tcSize);
		cell[1] = (int) Math.floor(y / tcSize);
//		System.out.println("at cell" + cell[0] + ", " + cell[1]);
//		System.out.println("Cell size " + tcSize + " and Camera position " + cameraPosition[0] + ", " + cameraPosition[2]);
		return cell;
	}
		
	protected float[] getPosInCell(float x, float y){
		float[] retVal = new float[2];
		retVal[0] = (x % tcSize) / tcSize;
		retVal[1] = (y % tcSize) / tcSize;
//		System.out.println("x = " + retVal[0] + ", y = " + retVal[1]);
		if(retVal[0] < 0 )
			retVal[0] = 1 + retVal[0];
		if(retVal[1] < 0 )
			retVal[1] = 1 + retVal[1];
		return retVal;
	}
	
	protected float[] placeInCell(float x, float y, RenderingTerrainModel ter){//x,y from 0 to 1
		float[] retVal = new float[3];
		retVal[0] = x * tcSize;
		retVal[1] = ter.getRHeight(x, y);
		retVal[2] = y * tcSize;
		return retVal;
	}
	
	protected void genTerrains(int x, int y){

		tmf.free(cCell[0], cCell[1]);
		tmf.free(cCell[0] + 1, cCell[1]);
		tmf.free(cCell[0] - 1, cCell[1]);
		tmf.free(cCell[0], cCell[1] + 1);
		tmf.free(cCell[0], cCell[1] - 1);
		tmf.free(cCell[0] + 1, cCell[1] + 1);
		tmf.free(cCell[0] - 1, cCell[1] + 1);
		tmf.free(cCell[0] + 1, cCell[1] - 1);
		tmf.free(cCell[0] - 1, cCell[1] - 1);


		cCell[0] = x;
		cCell[1] = y;
		
		terrain = tmf.getTerrain(x, y);
		terrainN = tmf.getTerrain(x - 1, y);
		terrainS = tmf.getTerrain(x + 1, y);
		terrainE = tmf.getTerrain(x, y + 1);
		terrainW = tmf.getTerrain(x, y - 1);
		terrainNE = tmf.getTerrain(x - 1, y + 1);
		terrainSE = tmf.getTerrain(x + 1, y + 1);
		terrainSW = tmf.getTerrain(x + 1, y - 1);
		terrainNW = tmf.getTerrain(x - 1, y - 1);


		terrain.clampN(terrainN.vertices);
		terrainS.clampN(terrain.vertices);
		terrain.clampW(terrainW.vertices);
		terrainE.clampW(terrain.vertices);

		//gl.glDeleteLists(lists, 9);	
		//lists = gl.glGenLists(9);

		for(int i = 0; i < 9; i++)
			listDone[i] = false;

/*		gl.glNewList(lists + 0, GL.GL_COMPILE);
		drawEntIt(gl, terrain.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 1, GL.GL_COMPILE);
		drawEntIt(gl, terrainS.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 2, GL.GL_COMPILE);
		drawEntIt(gl, terrainN.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 3, GL.GL_COMPILE);
		drawEntIt(gl, terrainE.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 4, GL.GL_COMPILE);
		drawEntIt(gl, terrainW.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 5, GL.GL_COMPILE);
		drawEntIt(gl, terrainNE.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 6, GL.GL_COMPILE);
		drawEntIt(gl, terrainNW.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 7, GL.GL_COMPILE);
		drawEntIt(gl, terrainSE.getTrees().iterator());
		gl.glEndList();

		gl.glNewList(lists + 8, GL.GL_COMPILE);
		drawEntIt(gl, terrainSW.getTrees().iterator());
		gl.glEndList();*/

	}
	
	boolean initDone = false;
	
	public void init(GLAutoDrawable drawable){
		
		glu = new GLU();

		gl = drawable.getGL();

		seed = 0l;
		texT = new TexTable(1, gl);
		
		tmf = new TMFactory(seed, texT);
		tcSize = tmf.getCellSize();
		//System.out.println("in loop " + tmf);
		//System.out.println("is This even getting called?");
		cCell = getCell();
		genTerrains(cCell[0], cCell[1]);
		if(terrain.testPath != null){
			drawPath(gl, terrain.testPath.getPath().iterator());
		}
		
		popTextureCoords(terrain.getWidth(), 11.51f, 50);
//		System.out.println("Width = " + terrain.getWidth());
		popAlphaCoords(terrain.getWidth());
		
		hover = new Robot(1f, tcSize); //remove me
		Lookat[] look = {new Lookat(lookp1),
					//new Lookat(lookdir),
					new Lookat(hover),
					new Lookat(hover),
					new Lookat(lookp1)
		};	
		//tour = new Tour(times, poss, lookTimes, look, tmf);
		tour = Tours.tour2(hover, tmf);
		anim = new EntAnimator(tmf, hover);
		anims = new Vector();
		anims.add(anim);

		//System.out.println("is This even getting called?");

		lists = gl.glGenLists(10);

		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glClearColor(fogColour[2], fogColour[1], fogColour[0], fogColour[3]);
//		gl.glClearColor(1, 0, 1, 1);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_NORMALIZE);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable (GL.GL_COLOR_MATERIAL );
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(gl.GL_LIGHT1, GL.GL_SPECULAR, lightSpecular, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION,lightPosition, 0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
		gl.glFogf(GL.GL_FOG_MODE, GL.GL_EXP2);
		gl.glFogf(GL.GL_FOG_DENSITY, 0.001f);
		gl.glFogfv(GL.GL_FOG_COLOR, fogColour, 0);
		//gl.glEnable(gl.GL_FOG);
//		gl.glAlphaFunc(GL.GL_GREATER,0.1f);
//		gl.glEnable(GL.GL_ALPHA_TEST);	
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL.GL_BLEND);
		
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);

		//System.out.println("lists " + lists);
		
//		gl.glClientActiveTexture(gl.GL_TEXTURE1);
//		gl.glClientActiveTexture(gl.GL_TEXTURE0);
//		gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY);
//		if(gl.isExtensionAvailable("GL_ARB_multitexture")) System.out.println("Multi texturing found");
//		if(gl.isFunctionAvailable("glClientActiveTextureARB")) System.out.println("function found");
//		gl.glClientActiveTexture(gl.GL_TEXTURE1);
		
//		gl.glClientActiveTexture(gl.GL_TEXTURE1);
//		gl.glEnable(gl.GL_TEXTURE_2D);
//		gl.glEnableClientState(gl.GL_TEXTURE_COORD_ARRAY); //causes crash
//		gl.glClientActiveTexture(gl.GL_TEXTURE0);

		TextureInfo texI = new TextureInfo("DEF_ROCK", "rock.bmp");
		TextureInfo texItest = new TextureInfo("TEST_GRASS", "grass.bmp");
	  	defTindex0 = texT.getIndex(texI);	
	  	defTindex1= texT.getIndex(texItest);
	//	System.out.println(defTindex1 + " (grass)");	

		drawable.addMouseListener(this);
		drawable.addMouseMotionListener(this);
		drawable.addKeyListener(this);
		
		initDone=true;
	}

	protected void drawTerrain(GL gl, RenderingTerrainModel terrain, int list){

		float[] specColour = {0.6f, 0.6f, 0.6f, 1f};
		
//		glMaterialfv(GL.GL_FRONT, GL_EMISSION, normGlow);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specColour, 0);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 30);  

		gl.glPushMatrix();
		gl.glTranslatef(terrain.gloX * tcSize, 0, terrain.gloY *tcSize);

		gl.glEnable(gl.GL_TEXTURE_2D);
		gl.glVertexPointer(3, GL.GL_FLOAT, 0, terrain.verticesB);
		gl.glNormalPointer(GL.GL_FLOAT, 0, terrain.normalsB);
		gl.glColor4f(1f, 1f, 1f, 1f);
		
		gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, texCoordsB);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texT.textures[terrain.alpha.indexPairs[0][0]]);
				
//		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
//		gl.glColorPointer(4, GL.GL_FLOAT, 0, terrain.alphas[0]);
			

		gl.glDrawElements(gl.GL_TRIANGLE_STRIP, terrain.indicesB.capacity(), gl.GL_UNSIGNED_INT, terrain.indicesB);
		
		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glDepthMask(false);
		for(int i = 0; i < terrain.alphas.length; i++){
			gl.glBindTexture(GL.GL_TEXTURE_2D, texT.textures[terrain.alpha.indexPairs[i + 1][0]]);
			gl.glColorPointer(4, GL.GL_FLOAT, 0, terrain.alphas[i]);
			gl.glDrawElements(gl.GL_TRIANGLE_STRIP, terrain.indicesB.capacity(), gl.GL_UNSIGNED_INT, terrain.indicesB);
		}
		gl.glDisableClientState(GL.GL_COLOR_ARRAY);
		gl.glDepthMask(true);
		
		if(terrain.testPath != null){
			drawPath(gl, terrain.testPath.getPath().iterator());
		}
		//drawEntIt(gl, terrain.getTrees().iterator());
		
		if(!listDone[list]){
			//System.out.println("compiling list " + list + ", listDone = " + listDone[list]);
			gl.glNewList(lists + list, GL.GL_COMPILE);
			drawEntIt(gl, terrain.getTrees().iterator());
			gl.glEndList();
			listDone[list] = true;
			//System.out.println("list " + list + " compiled = " + listDone[list]);
		}
		//drawEntIt(gl, terrain.getTrees().iterator());
		
		if(drawTrees)
			gl.glCallList(lists + list);
		gl.glPopMatrix();
	}

	protected void drawMarker(GL gl){

	//	System.out.println("drawing a marker");	
		gl.glDisable(gl.GL_TEXTURE_2D);
		gl.glColor4f(1f, 0f, 0f, 0.6f);
		float scale = (float) terrain.getWidth() / 500;
		//System.out.println("scale " + scale);
		gl.glPushMatrix();
		gl.glScalef(scale, scale, scale);
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 10, 2);
			gl.glVertex3f(0, 10, -2);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(2, 10, 0);
			gl.glVertex3f(-2, 10, 0);
			gl.glVertex3f(0, 0, 0);
		gl.glEnd();
		gl.glPopMatrix();
	}
	
	boolean first = true;
	
	protected void drawPath(GL gl, Iterator it){

		float[] coord;
		float width = (float) terrain.getWidth();
		int count = 0;
		float scale = terrain.scale;
		float x, y, z;
		while(it.hasNext()){
			coord = (float[]) it.next();
			gl.glPushMatrix();
			x = coord[0];
			y = coord[1];
			z = coord[2];
			//if(first)
				//System.out.println("drawin marker at " + x + ", " + y + ", " + z + ".");
			gl.glTranslatef(coord[0] * width * scale, coord[1], coord[2] * width * scale);
			gl.glScalef(scale, scale, scale);
			drawMarker(gl);
			gl.glPopMatrix();
			count++;
		}
		first = false;
		//System.out.println("scale = " + scale + ", width = " + width);
		//System.out.println("drawn " + count + " markers.");
	}

	protected void drawEntIt(GL gl, Iterator it){
		Entity ent;
		while(it.hasNext()){
			ent = (Entity) it.next();
			ent.draw(gl);
		}
	}
			

	protected void drawWP(GL gl, Waypoint wp){
	//	System.out.println("\n\n\nDrawing WP\n\n\n");
		float scale = tcSize;
		float[] pos = wp.getPosition();
	//	System.out.println("\n\n\nDrawing WP" + pos +"\n\n\n");
		gl.glPushMatrix();
			gl.glTranslatef(pos[0] * tcSize, pos[1] * tcSize, pos[2] * tcSize);
			gl.glScalef(scale, scale, scale);
			drawMarker(gl);
		gl.glPopMatrix();
	}
		

	float[] times = {0f, 5000f, 6000f, 10000f, 18000f};
	float[] pos1 = {0.2f, 0.3f, 0.5f};
	//float[] pos2 = {0.8f, 0.3f, 0.5f};
	float[] pos2 = {0.5f, 0.3f, 0.5f};
	float[] pos3 = {0.8f, 0.2f, 0.8f};
	float[] pos4 = {0.2f, 0.2f, 0.2f};
	
	float[] lookp1a ={0.5f, 0.25f, 0.8f};
	Waypoint lookp1 = new Waypoint(lookp1a);
	float[] lookdir = {0f, -0.1f, -1f};


	//float[] lookTimes = {0f, 6000f, 18000f};
	float[] lookTimes = {0f, 2000f, 16000f, 18000f};
	

	Waypoint[] poss = {new Waypoint(pos1),
						new Waypoint(pos2),
						new Waypoint(pos3, true),
						new Waypoint(pos4, true),
						new Waypoint(pos1)
	};

	protected void cameraTour(long time){	
		
		float[] pos = tour.getPosition(time);
		float[] dir = tour.getDirection(time, pos);
		
		cameraPosition[0] = tcSize * pos[0];
		cameraPosition[1] = tcSize * pos[1];
		cameraPosition[2] = tcSize * pos[2];

		if((dir[0] * dir[0]) + (dir[2] * dir[2]) == 0)
			dir[0] = 0.01f;

		/*System.out.println("dir (x, y, z) = (" +
				dir[0] + ", " +
				dir[1] + ", " +
				dir[2] + ")");
		System.out.println("time " + time);
		System.out.println("pos (x, y, z) = (" +
				pos[0] + ", " +
				pos[1] + ", " +
				pos[2] + ")");*/

		glu.gluLookAt(
			cameraPosition[0], cameraPosition[1], cameraPosition[2],
		/*	cameraPosition[0] + Math.cos(theta) * Math.sin(thi),
			cameraPosition[1] + Math.sin(theta),
			cameraPosition[2] + Math.cos(theta) * Math.cos(thi),*/
			cameraPosition[0] + dir[0],
			cameraPosition[1] + dir[1],
			cameraPosition[2] + dir[2],
			0.0f, 0.1f, 0.0f);
	}

	protected void cameraControl(){

		if(move){
			moveRate += 20;
		}
		if(moveBack){
			moveRate -= 20;
		}

		moveRate = (float) 0.7 * moveRate;
//		System.out.println("move rate " + moveRate);

		cameraPosition[0] += Math.cos(theta) * Math.sin(thi) * moveRate;
		cameraPosition[1] += Math.sin(theta) * moveRate;
		cameraPosition[2] += Math.cos(theta) * Math.cos(thi) * moveRate;
		
		float[] pos = getPosInCell(cameraPosition[0], cameraPosition[2]);
		float height = terrain.getRHeight(pos[0], pos[1]) + tcSize / 50;
		if(height > cameraPosition[1])
			cameraPosition[1] = height;

		glu.gluLookAt(
			cameraPosition[0], cameraPosition[1], cameraPosition[2],
			cameraPosition[0] + Math.cos(theta) * Math.sin(thi),
			cameraPosition[1] + Math.sin(theta),
			cameraPosition[2] + Math.cos(theta) * Math.cos(thi),
			0.0f, 0.1f, 0.0f);
	}
	
	protected void mode(int i){
		tmf.setMode(i);
		genTerrains(cCell[0], cCell[1]);
	}

	GLUT glut = new GLUT();
	
	public void display(GLAutoDrawable drawable){
		if(initDone){
		GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		
		
		gl.glLoadIdentity();
//		glu.gluLookAt(cameraPosition[0], cameraPosition[1], cameraPosition[2],
//				cameraLookAt[0], cameraLookAt[1], cameraLookAt[2],
//				0, 1, 0);
//				
		long time = System.currentTimeMillis();
		Iterator it = anims.iterator();
		EntAnimator cAnim;
		
		while(it.hasNext()){
			cAnim = (EntAnimator) it.next();
			cAnim.position(time);
		}
			
		//anim.position(time);
		
		if(cameraMode == 0)
			cameraControl();
		if(cameraMode == 1)
			cameraTour(time);

		int[] cell = getCell();
		if(cCell == null || cell[0] != cCell[0] || cell[1] != cCell[1]){
			long time1 = System.currentTimeMillis();
			genTerrains(cell[0], cell[1]);
			long time2 = System.currentTimeMillis();
			tour.delay(time2 - time1);
			it = anims.iterator();
			while(it.hasNext()){
				cAnim = (EntAnimator) it.next();
				cAnim.delay(time2 - time1);
			}
			//anim.delay(time2 - time1);
		}

		drawTerrain(gl, terrain, 0);
		drawTerrain(gl, terrainN, 1);
		drawTerrain(gl, terrainS, 2);
		drawTerrain(gl, terrainE, 3);
		drawTerrain(gl, terrainW, 4);
		drawTerrain(gl, terrainNE, 5);
		drawTerrain(gl, terrainSE, 6);
		drawTerrain(gl, terrainSW, 7);
		drawTerrain(gl, terrainNW, 8);

	//	drawEntIt(gl, terrain.getTrees().iterator());
		/*gl.glCallList(lists + 0);
		gl.glCallList(lists + 1);
		gl.glCallList(lists + 2);
		gl.glCallList(lists + 3);
		gl.glCallList(lists + 4);
		gl.glCallList(lists + 5);
		gl.glCallList(lists + 6);
		gl.glCallList(lists + 7);
		gl.glCallList(lists + 8);*/


		//gl.glPushMatrix();
		//gl.glTranslatef(0f, 500f, 0f);
		//gl.glPushMatrix();
			//gl.glScalef(100f, 100f, 100f);
			anim.drawEnt(gl);
		//gl.glPopMatrix();
		//gl.glPopMatrix();

		//drawWP(gl, poss[0]);
		//drawWP(gl, poss[1]);


//Test Triangle		
/*		gl.glEnable(gl.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glBegin(GL.GL_TRIANGLE_STRIP);
		 
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0, 2000, 0);
	}
			gl.glTexCoord2f(1, 0);
			gl.glVertex3f(0, 2000, 2000);
			gl.glTexCoord2f(0, 1);
			gl.glVertex3f(2000, 2000, 0);
			gl.glTexCoord2f(0, 0);
			gl.glVertex3f(0, 2000, 0);
		gl.glEnd();*/
//		}
		
	}

	public void displayChanged(GLAutoDrawable drawable,
			boolean modeChanged, boolean deviceChanged){
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height){
		GL gl = drawable.getGL();

		float h = (float)height / (float)width;
            
		gl.glMatrixMode(GL.GL_PROJECTION);

//		System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
//		System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
//		System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));
		gl.glLoadIdentity();
		// draw distance
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 90000.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -40.0f);
	}

  // Methods required for the implementation of MouseListener
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
			mouseRButtonDown = true;
		}
	}
    
	public void mouseReleased(MouseEvent e) {
		if ((e.getModifiers() & e.BUTTON3_MASK) != 0) {
			mouseRButtonDown = false;
		}
	}
    
  public void mouseClicked(MouseEvent e) {}
    
  // Methods required for the implementation of MouseMotionListener
  public void mouseDragged(MouseEvent e) {
//	  System.out.println("dragging");
    int x = e.getX();
    int y = e.getY();
    Dimension size = e.getComponent().getSize();

    float thetaY = 12.0f * ( (float)(prevMouseX-x)/(float)size.width);
    float thetaX = 12.0f * ( (float)(y-prevMouseY)/(float)size.height);
    
    prevMouseX = x;
    prevMouseY = y;

    theta += thetaX;
    thi += thetaY;
//	  System.out.println("dragging done " + thetaX + " " + thetaY);
  }
    
	public void mouseMoved(MouseEvent e) {}

	public void keyTyped(KeyEvent e){
	}
  
	public void keyPressed(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_E){
			move = true;
		if (e.getKeyCode() == KeyEvent.VK_T){
			cameraMode = 1;
			tour.start();
		}
		}	  	
		if (e.getKeyCode() == KeyEvent.VK_D){
			moveBack = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_P){
			terrain.genPath();
		}
		if (e.getKeyCode() == KeyEvent.VK_EQUALS){
			tmf.setSeed(tmf.seed + 1l);
		}
		if (e.getKeyCode() == KeyEvent.VK_MINUS){
			tmf.setSeed(tmf.seed - 1l);
		}
		if (e.getKeyCode() == KeyEvent.VK_1){
			mode(1);
		}
		if (e.getKeyCode() == KeyEvent.VK_2){
			mode(2);
		}
		if (e.getKeyCode() == KeyEvent.VK_3){
			mode(3);
		}
		if (e.getKeyCode() == KeyEvent.VK_4){
			mode(4);
		}
		if (e.getKeyCode() == KeyEvent.VK_5){
			mode(5);
		}
		if (e.getKeyCode() == KeyEvent.VK_6){
			mode(6);
		}
		if (e.getKeyCode() == KeyEvent.VK_9){
			mode(9);
		}
		if (e.getKeyCode() == KeyEvent.VK_0){
			mode(0);
		}
		if (e.getKeyCode() == KeyEvent.VK_R){
			drawTrees = !drawTrees;
			System.out.println("" + drawTrees);
		}
		if (e.getKeyCode() == KeyEvent.VK_T){
			cameraMode = 1;
			tour.start();
		}
		if (e.getKeyCode() == KeyEvent.VK_F){
			cameraMode = 0;
		}
		if (e.getKeyCode() == KeyEvent.VK_V){
			mode(0);
			drawTrees = true;
			tour = Tours.tour3(hover, tmf);
			anims = new Vector();
			anim = Tours.entTour1(hover, tmf);
			anims.add(anim);
			cameraMode = 1;
			tour.start();
			EntAnimator first = (EntAnimator) anims.elementAt(0);
			first.start();
		}
		if (e.getKeyCode() == KeyEvent.VK_B){
			mode(0);
			drawTrees = true;
			tour = Tours.tour1(hover, tmf);
			cameraMode = 1;
			tour.start();
		}
		if (e.getKeyCode() == KeyEvent.VK_O){
			System.out.println(	"x = " + cameraPosition[0] / tcSize +
								", y = " + cameraPosition[1] / tcSize +
								", z = " + cameraPosition[2] / tcSize);
		}
	}
  
	public void keyReleased(KeyEvent e){
		if (e.getKeyCode() == KeyEvent.VK_E){
			move = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D){
			moveBack = false;
		}	  	
	}
}
