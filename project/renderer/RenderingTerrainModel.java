/* This class is part of the renderer, it is used to generate and  hold
 * renderer specific elements of data about a Terrain and the terrain
 * itself. It wraps calls to the terrain to get data so it is asumed that
 * the implementation of Terrain can return these things at suficient
 * speed.
 */

package project.renderer;
import project.Terrain;
import project.pathSearch.*;
import project.populate.*;

import java.lang.Math;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import com.sun.opengl.util.*;
import java.util.Vector;
import java.util.Iterator;

public class RenderingTerrainModel{
	
	//int[] indices;
	//float[] normals, vertices;
	
	int gloX, gloY;
	IntBuffer indicesB;
	FloatBuffer normalsB, verticesB;
	float[] vertices, normals;
	public final static int scale = 20;
	public final static int zScale = 1;//this and above variable may be changed to being set at construction.
//	public final int zScale = 0;
	protected Terrain terrain;
	AlphaGenerator alpha;
	TexTable tt;
	FloatBuffer[] alphas;
	GradLimPath testPath;
	Vector trees;
	
	public RenderingTerrainModel(Terrain terrain, TexTable tt, int gloX, int gloY){
		this.terrain = terrain;
		int size = terrain.getWidth();
		indicesB = BufferUtil.newIntBuffer(size * 2 * (size - 1) + (size - 1));
		normalsB = BufferUtil.newFloatBuffer(size * size * 3);
		verticesB = BufferUtil.newFloatBuffer(size * size * 3);
		popVertexArray();
		calcTerrainNormals();
		indexArray();
		this.gloX = gloX;
		this.gloY = gloY;
		this.tt = tt;
		//if(this.tt == null) System.out.println("TexTable null in Terrain");
		alpha = new AlphaGenerator(this);
		popAlphas();
		float[] start = {0.2f, 0.2f};
		float[] goal = {0.8f, 0.8f};
		PseudoTerrain pTerr = new PseudoTerrain(this, scale * terrain.getWidth(), 35);
		//System.out.print("\n\n\nWidth in loop" + pTerr.getWidth() + "\n\n\n");
		popTrees(TreePop.getTrees(pTerr).iterator());
		//popTrees(TreePop.getTrees(terrain).iterator());
		
		//testPath = new GradLimPath(start, goal, (float) 1/6, terrain);
	}

	protected void popTrees(Iterator it){
		trees = new Vector();
		Tree tree;
		float tcSize = scale * terrain.getWidth();
		float tScale = 20f;
		float[] coord;
		
		while(it.hasNext()){
			float[] data = (float[]) it.next();
			coord = new float[3];
			coord[0] = data[0];
			coord[1] = data[1];
			coord[2] = data[2];
			//coord[0] +=  gloX;
			//coord[2] +=  gloY;
			tree = new Tree(tScale * data[4], tcSize, coord, tt);
			tree.setRotation(360f * data[3]);
			trees.add(tree);
		}
	}	
			

	public void genPath(){
		if (testPath == null){
			float[] start = {0.2f, 0.2f};
			float[] goal = {0.8f, 0.8f};
			testPath = new GradLimPath(start, goal, (float) 1, terrain);
		}
		else
			testPath = null;
	}


	public int getWidth(){
		return terrain.getWidth();
	}

	public float getHeight(int x, int y){
		float scale = (float) terrain.getWidth() / (terrain.getWidth() - 1);
		//System.out.println("scale = " + scale);
		float xf = x * scale;
		float yf = y * scale;
		return terrain.getHeight(xf, yf);
	}
	
	public float getRHeight(float x, float y){//terrain cell in 0 to 1
		//System.out.println("In height calculation, x = " + x + ", y = " + y);
		int xi, yi, size;
		float xa, ya;
		size = terrain.getWidth();
		xa = x * (size - 1);
		ya = y * (size - 1);
		//System.out.println("xa = " + xa + ", ya = " + ya);
		xi = (int)xa;
		yi = (int)ya;
		//System.out.println("xi = " + xi + ", yi = " + yi);
		xa -= xi;
		ya -= yi;

		if(xi >= size - 1){
			xi = size - 2;
			xa = 0.9f;
		}
		
		if(yi >= size - 1){
			yi = size - 2;
			ya = 0.9f;
		}
			
		float v00, v01, v10, v11;
		v00 = vertices[3 * (yi * size + xi) + 1];
		v01 = vertices[3 * (yi * size + xi + 1) + 1];
		v10 = vertices[3 * ((yi + 1) * size + xi) + 1];
		v11 = vertices[3 * ((yi + 1) * size + xi + 1) + 1];
		return (xa * ((ya * v11) + ((1 - ya) * v01))) +
				((1 - xa) * ((ya * v10) + ((1 - ya) * v00)));
	}

	public float getRNormal(float x, float y){//terrain cell in 0 to 1
		//System.out.println("In height calculation, x = " + x + ", y = " + y);
		int xi, yi, size;
		float xa, ya;
		size = terrain.getWidth();
		xa = x * (size - 1);
		ya = y * (size - 1);
		//System.out.println("xa = " + xa + ", ya = " + ya);
		xi = (int)xa;
		yi = (int)ya;
		//System.out.println("xi = " + xi + ", yi = " + yi);
		xa -= xi;
		ya -= yi;

		if(xi >= size - 1){
			xi = size - 2;
			xa = 0.9f;
		}
		
		if(yi >= size - 1){
			yi = size - 2;
			ya = 0.9f;
		}
		
		float v00, v01, v10, v11;
		v00 = normals[3 * (yi * size + xi) + 1];
		v01 = normals[3 * (yi * size + xi + 1) + 1];
		v10 = normals[3 * ((yi + 1) * size + xi) + 1];
		v11 = normals[3 * ((yi + 1) * size + xi + 1) + 1];
		return (xa * ((ya * v11) + ((1 - ya) * v01))) +
				((1 - xa) * ((ya * v10) + ((1 - ya) * v00)));
	}


	protected float[] normal(float[] v1, float[] v2){
		float[] retVec = new float[3];
		retVec[0] = v1[1] * v2[2] - v1[2] * v2[1]; 
		retVec[1] = v1[2] * v2[0] - v1[0] * v2[2]; 
		retVec[2] = v1[0] * v2[1] - v1[1] * v2[0];
		return retVec;
	}

	protected float[] normalize(float x, float y, float z){
		float[] rV = new float[3]; 
		float h;
		h = x * x + y * y + z * z;
		h = (float)Math.sqrt(h);
		rV[0] = x / h;
		rV[1] = y / h;
		rV[2] = z / h;
		return rV;
	}
	
		
	protected void popVertexArray(){

		int size = terrain.getWidth();
		vertices = new float[size * size * 3];
		
		for(int y = 0; y < size; y++){
			for(int x = 0; x < size; x++){
				vertices[3 * (y * size + x) + 0] = x * scale;
				vertices[3 * (y * size + x) + 1] = getHeight(x, y) * zScale;
				vertices[3 * (y * size + x) + 2] = y * scale;
				//System.out.println("" + vertices[3 * (y * size + x) + 1]);
			}
		}
		//System.out.println("Final float is " + vertices[vertices.length - 1]);
		verticesB.put(vertices);
		verticesB.rewind();
//		System.out.println("vertices ok " + (verticesB.hasRemaining() ? "#t" : "#f"));
	}

	protected void popAlphas(){
		int n = alpha.filters.length;
		alphas = new FloatBuffer[n - 1];
		for(int i = 0; i < n - 1; i++){
			//System.out.println("popping colours " + i);
			alphas[i] = popColourArray(alpha.filters[i + 1]);
		}
	}
		
	
	protected FloatBuffer popColourArray(AlphaFilter filter){

		int size = terrain.getWidth();
		float[] colours = new float[size * size * 4];
		for(int i = 0; i < size * size; i++){
			//System.out.println(filter.getAlpha(vertices[3 * i + 1]));
			colours[4 * i + 0] = 1f;
			//colours[4 * i + 0] = filter.getAlpha(vertices[3 * i + 1]);
			colours[4 * i + 1] = 1f;
			//colours[3 * i + 1] = filter.getAlpha(vertices[3 * i + 1]);
			colours[4 * i + 2] = 1f;
			//colours[3 * i + 2] = filter.getAlpha(vertices[3 * i + 1]);
			//colours[4 * i + 3] = 1f;
			colours[4 * i + 3] = filter.getAlpha(normals[3 * i + 1], (int)i/size, i % size);
		}
		FloatBuffer coloursB = BufferUtil.newFloatBuffer(size * size * 4);
		coloursB.put(colours);
		coloursB.rewind();
		return coloursB;
	}
				
				
	
	/* will give quick aproximate, prototype sugests it is good enough
	 * will likely cause lighting artifacts along terrain boundries
	 */
	protected void calcTerrainNormals(){
		
		int size = terrain.getWidth();
		normals = new float[size * size * 3];
		
		float[] v1 = new float[3];
		float[] v2 = new float[3];
		float[] norm = new float[3];
		//System.out.println("starting to calulate unscaled normals.");
		for(int y = 0; y + 1 < size; y++){
			//System.out.println("y = " + y);
			for(int x = 0; x + 1 < size; x++){
				v1[0] = 0;
				v1[1] = vertices[3 * (size * (y + 1) + x) + 1] - vertices[3 * (size * y  + x) + 1];
				v1[2] = scale;

				v2[0] = scale;
				v2[1] = vertices[3 * (size * y + x + 1) + 1] - vertices[3 * (size * y  + x) + 1];
				v2[2] = 0;

				norm = normal(v1, v2);
				norm = normalize(norm[0], norm[1], norm[2]);

				normals[3 * (y * size + x) + 0] += norm[0];
				normals[3 * (y * size + x) + 1] += norm[1];
				normals[3 * (y * size + x) + 2] += norm[2];
				normals[3 * ((y + 1) * size + x) + 0] += norm[0];
				normals[3 * ((y + 1) * size + x) + 1] += norm[1];
				normals[3 * ((y + 1) * size + x) + 2] += norm[2];
				normals[3 * (y * size + (x + 1)) + 0] += norm[0];
				normals[3 * (y * size + (x + 1)) + 1] += norm[1];
				normals[3 * (y * size + (x + 1)) + 2] += norm[2];
				
				v1[0] = 0;
				v1[1] = vertices[3 * (size * (y + 1) + x + 1) + 1] - vertices[3 * (size * y  + x + 1) + 1];
				v1[2] = scale;
				
				v2[0] = scale;
				v2[1] = vertices[3 * (size * (y + 1) + x + 1) + 1] - vertices[3 * (size * (y + 1) + x) + 1];
				v2[2] = 0;

				norm = normal(v1, v2);
				norm = normalize(norm[0], norm[1], norm[2]);

				normals[3 * ((y + 1) * size + x) + 0] += norm[0];
				normals[3 * ((y + 1) * size + x) + 1] += norm[1];
				normals[3 * ((y + 1) * size + x) + 2] += norm[2];
				normals[3 * (y * size + (x + 1)) + 0] += norm[0];
				normals[3 * (y * size + (x + 1)) + 1] += norm[1];
				normals[3 * (y * size + (x + 1)) + 2] += norm[2];
				normals[3 * ((y + 1) * size + (x + 1)) + 0] += norm[0];
				normals[3 * ((y + 1) * size + (x + 1)) + 1] += norm[1];
				normals[3 * ((y + 1) * size + (x + 1)) + 2] += norm[2];
			}
		}
		//System.out.println("unscaled normals, ok!");
		float[] nized;
		for(int i = 0; i < size * size; i++){
			nized = normalize(normals[3 * i],
					normals[3 * i + 1],
					normals[3 * i + 2]);
			normals[3 * i + 0] = nized[0];
			normals[3 * i + 1] = nized[1];
			normals[3 * i + 2] = nized[2];
			//System.out.println("Sample normal = " + nized[1]);
		}
		normalsB.put(normals);
		normalsB.rewind();
	}
	
/*	protected void cp3fv(float[] dest, float[] source){
			dest[0] = source[0];
			dest[1] = source[1];
			dest[2] = source[2];
	}*/

	protected void indexArray(){
		int size = this.getWidth();
		int [] indices = new int[size * 2 * (size - 1) + (size - 1)];
		int index = 0;
		int row = 0;
		while(row + 1 < size){
			index = popRight(indices, index, row);
			row++;
			if(row + 1 < size){
			   index = popLeft(indices, index, row);
			}
	 		row++;
		}
		//System.out.println("index, ok!");
		indicesB.put(indices);
		indicesB.rewind();

	}		

	protected int popRight(int[] acum, int index, int row){
		int width = this.getWidth();
		for(int i = 0; i < width; i++){
			acum[index] = row * width + i;
			index++;
			acum[index] = (row + 1) * width + i;
			index++;
		}
		acum[index] = (row + 2) * width - 1;
		index++;
		return index;
	}
		       	
	protected int popLeft(int[] acum, int index, int row){
		int width = this.getWidth();
		for(int i = 0; i < width; i++){
			acum[index] = (row + 1) * width - 1 -i;
			index++;
			acum[index] = (row + 2) * width - 1 - i;
			index++;
		}
		acum[index] = (row + 1) * width;
		index++;
		return index;
	}
	
	void clampN(float[] data){
		int size = terrain.getWidth();
		//vertices[3 * (y * size + x) + 0];
		for(int y = 0; y < size; y++){
			vertices[3 * (y * size + 0) + 1] = data[3 * (y * size + size - 1) + 1];
		}
		verticesB.put(vertices);
		verticesB.rewind();
	}

	void clampW(float[] data){
		int size = terrain.getWidth();
		//vertices[3 * (y * size + x) + 0];
		for(int x = 0; x < size; x++){
			vertices[3 * (0 + x) + 1] = data[3 * ((size - 1) * size + x) + 1];
		}
		verticesB.put(vertices);
		verticesB.rewind();
	}

	public Vector getTrees(){
		return trees;
	}
	
	public void free(){
		alpha.free();
		Entity ent;
		Iterator it = trees.iterator();
		while(it.hasNext()){
			ent = (Entity) it.next();
			ent.free();
		}
		
	}

/*	public static void main(String[] panda){
		new RenderingTerrainModel(new TestTerrain());
	}*/
}
