package project.renderer;

import javax.media.opengl.*;
import com.sun.opengl.util.GLUT;
import javax.media.opengl.glu.*;
import java.lang.Math;

class Robot implements Entity{

	protected float[] pos;
	protected float rotation;
	protected GLU glu;
	protected GLUT glut;
	protected float tcSize;
	protected float scale;
	double rotF;
	float[] direction;
	float[] stdDir = {1, 0};

	public Robot(float scale, float tcSize){
		this.scale = scale;
		this.tcSize = tcSize;
		this.glu = new GLU();
		this.glut = new GLUT();
		float[]	pos = {0.5f, 0.3f, 0.5f};
		this.pos = pos;
		this.rotF = Math.toDegrees(Math.atan(2 / Math.sqrt(2)));
		this.direction = stdDir;
		
		//System.out.println("" + rotF);
	}

	public void setDirection(float [] dir){
		float length = (float) Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1]);
		if(length == 0){
			direction = stdDir;
		}
		else{
			dir[0] = dir[0] / length;
			dir[1] = dir[1] / length;
		}
		direction = dir;
	}
		
		
	
	public float[] getPosition(){
		return pos;
	}

	public void setPosition(float[] nPos){
		pos = nPos;
	}

	public float getRotation(){
		return 0;
	}

	public void setRotation(float r){
	}

	public void draw(GL gl){

		gl.glDisable(gl.GL_TEXTURE_2D);

		//System.out.println("drawring robot");
		int wave = (int) (System.currentTimeMillis() % 1000) - 500; 
		wave = (wave < 0) ? -wave : wave;
		wave -= 250;
		float[] eyeGlow= { 0.6f, 0.6f, 0.4f, 1.0f };
		float[] normGlow= { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] specColour = {0.7f, 0.7f, 0.7f, 1.0f};

		//System.out.println(this + " position for draw = " + pos[0] + ", " + pos[1] + ", " + pos[2]);

		//need to position/size
		gl.glPushMatrix();
		gl.glTranslatef(pos[0] * tcSize, pos[1] * tcSize, pos[2] * tcSize);
		//gl.glTranslatef(0f, 500f, 0f);

		float scF = 0.002f;

		float[] dir = direction;
		//System.out.println(dir[0] +", " + dir[1]);
		
		gl.glScalef(scale * tcSize * scF, scale * tcSize * scF, scale * tcSize * scF);
		gl.glRotatef(-90f, 0f, 1f, 0f);
		float[] rotM = {dir[0], 0, dir[1], 0,
						0, 1, 0, 0,
						-dir[1], 0, dir[0], 0,
						0, 0, 0, 1};
		gl.glMultMatrixf(rotM, 0);
		//gl.glScalef(100f, 100f, 100f);


		//draw body (with backpack)
		
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, normGlow, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specColour, 0);	
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 30);
		
		gl.glColor3f(0.6f, 0.6f, 0.6f);
		GLUquadric qobj = glu.gluNewQuadric();
		gl.glPushMatrix();
		gl.glTranslatef(0f, 1.3f, 0f);
		
		gl.glPushMatrix();
			gl.glScalef(1f, 1f, 0.5f);
			glut.glutSolidCube(1.0f);
		gl.glPopMatrix();

		gl.glPushMatrix();
			gl.glScalef(1f, 0.7f, 1f);
		
			gl.glPushMatrix();
	//	
	//	glTranslatef(- terrain->width * RES / 2, 0.0f, - terrain->height * RES / 2);
				gl.glTranslatef(-0.25f, 0.5f, 0.5f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				glut.glutSolidSphere(0.25, 16, 16);
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();
		
			gl.glPushMatrix();
				gl.glTranslatef(0.25f, 0.5f, 0.5f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				glut.glutSolidSphere(0.25, 16, 16);
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();

		gl.glPopMatrix();
		
		//position head
		gl.glPushMatrix();
			gl.glTranslatef(0.0f, 1f, 0.0f);
		//draw head
			gl.glPushMatrix();
				gl.glScalef(1.5f, 1f, 1f);
				glut.glutSolidSphere(0.5, 16, 16);
			gl.glPopMatrix();
		
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, eyeGlow, 0);
			gl.glColor3f(1.0f, 1.0f, 0.0f);

			gl.glPushMatrix();
				gl.glTranslatef(0.25f, 0.1f, -0.4f);
				glut.glutSolidSphere(0.12, 16, 16);
			gl.glPopMatrix();
		
			gl.glPushMatrix();
				gl.glTranslatef(- 0.25f, 0.1f, -0.4f);
				glut.glutSolidSphere(0.12, 16, 16);
			gl.glPopMatrix();

			gl.glColor3f(0.6f, 0.6f, 0.6f);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, normGlow, 0);

			gl.glPushMatrix();
				gl.glTranslatef(0.32f, 0.38f, -0.15f);
				gl.glScalef(0.3f, 0.3f, 0.3f);
				gl.glRotatef(-90f, 0f, 1f, 0f);
				gl.glRotatef(-15f, 1f, 0f, 0f);
				gl.glRotatef(45f, 0f, 1f, 0f);
				gl.glRotatef((float) rotF, 1f, 0f, -1f);
				glut.glutSolidTetrahedron();
			gl.glPopMatrix();
		
			gl.glPushMatrix();
				gl.glTranslatef(-0.32f, 0.38f, -0.15f);
				gl.glScalef(0.3f, 0.3f, 0.3f);
				gl.glRotatef(-90f, 0f, 1f, 0f);
				gl.glRotatef(15f, 1f, 0f, 0f);
				gl.glRotatef(45f, 0f, 1f, 0f);
				gl.glRotatef((float) rotF, 1f, 0f, -1f);
				glut.glutSolidTetrahedron();
			gl.glPopMatrix();
		
			gl.glPushMatrix();
				gl.glTranslatef(0f, 0f, -0.5f);
				gl.glScalef(0.12f, 0.1f, 0.1f);
				//gl.glRotatef(18f, 0f, 0f, 1f);
				gl.glRotatef(18f, 1f, 0f, 0f);
				gl.glRotatef(180f, 0f, 0f, 1f);
				gl.glRotatef(-90f, 0f, 1f, 0f);
				gl.glRotatef(45f, 0f, 1f, 0f);
				gl.glRotatef((float) rotF, 1f, 0f, -1f);
				glut.glutSolidTetrahedron();
	//	
	//	glTranslatef(- terrain->width * RES / 2, 0.0f, - terrain->height * RES / 2);
			gl.glPopMatrix();
		
		//deposition head
		gl.glPopMatrix();

		//draw left arm;
		gl.glPushMatrix();
			gl.glTranslatef(-0.8f, 0.25f, 0.0f);
			gl.glRotatef((float) (360 * wave / 2000), 1f, 0f, 0f);

			gl.glPushMatrix();
				gl.glScalef(0.6f, 0.6f, 0.6f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 0f, 1f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				glut.glutSolidSphere(0.25, 16, 16);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();

		gl.glPopMatrix();

		//draw right arm
		gl.glPushMatrix();
			gl.glRotatef(180f, 0f, 1f, 0f);
			gl.glTranslatef(-0.8f, 0.25f, 0.0f);
			gl.glRotatef((float)(360 * wave / 2000), 1f, 0f, 0f);

			gl.glPushMatrix();
				gl.glScalef(0.6f, 0.6f, 0.6f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 0f, 1f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				glut.glutSolidSphere(0.25, 16, 16);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();

		gl.glPopMatrix();

		//draw left leg
		gl.glPushMatrix();
			gl.glTranslatef(0.25f, -0.4f, 0f);
			gl.glRotatef((float) (360 * wave / 2000), 1f, 0f, 0f);

			gl.glPushMatrix();
				gl.glScalef(0.6f, 0.8f, 0.6f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();

		gl.glPopMatrix();

		//draw right leg
		gl.glPushMatrix();
			gl.glTranslatef(-0.25f, -0.4f, 0f);
			gl.glRotatef((float)(-360 * wave / 2000), 1f, 0f, 0f);

			gl.glPushMatrix();
				gl.glScalef(0.6f, 0.8f, 0.6f);
				gl.glPushMatrix();
					gl.glRotatef(90f, 1f, 0f, 0f);
					glu.gluCylinder(qobj, 0.25, 0.25, 1, 16, 1);
				gl.glPopMatrix();
				gl.glTranslatef(0f, -1.0f, 0f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();

		gl.glPopMatrix();
		
		gl.glPushMatrix();
			gl.glTranslatef(0f, -0.4f, 0.4f);
			gl.glRotatef((float)(0.6 * 360 * wave / 2000), 0f, 1f, 0f);

			gl.glPushMatrix();
				gl.glScalef(0.4f, 0.4f, 0.8f);
				gl.glPushMatrix();
					//glRotatef(90, 0, 0, 0);
					glu.gluCylinder(qobj, 0.25, 0.25, 1d, 16, 1);
				gl.glPopMatrix();
				gl.glTranslatef(0f, 0f, 1f);
				glut.glutSolidSphere(0.25, 16, 16);
			gl.glPopMatrix();
		gl.glPopMatrix();
		
		gl.glPopMatrix();
		gl.glPopMatrix();
	}

	public void free(){
	}
}
