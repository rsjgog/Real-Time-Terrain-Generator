package project.renderer;

import javax.media.opengl.*;

public class Tree implements Entity{

	float[] pos;
	//rotation is always 0
	TexTable tt;
	int treeTx;
	float scale;
	float tcSize;
	float rotation;
	TextureInfo tex; 

	public Tree(float scale, float tcSize, float[] pos, TexTable tt){
		this.pos = pos;
		this.tcSize = tcSize;
		this.scale = scale;
		this.tt = tt;
		tex = new TextureInfo("TREE", "tree.png");
		this.treeTx = tt.getIndex(tex);
	}	
	
	public float[] getPosition(){
		return pos;
	}

	public void setRotation(float rotationN){
		rotation = rotationN;
	}

	public float getRotation(){
		return rotation;
	}

	public void setPosition(float[] position){
		pos = position;
	}

	public void draw(GL gl){
		float scF = 0.002f;
		//System.out.println(pos[0] + ", " + pos[1] + ", " + pos[2]);
		
		gl.glEnable(GL.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0.9f);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glPushMatrix();
		//gl.glLoadIdentity();
		gl.glTranslatef(pos[0] * tcSize, pos[1] * tcSize, pos[2] * tcSize);
		gl.glRotatef(rotation, 0f, 1f, 0f);
		gl.glScalef(scale * tcSize * scF, scale * tcSize * scF, scale * tcSize * scF);
		//gl.glBindTexture(GL.GL_TEXTURE_2D, treeTx);
		//gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, tt.textures[treeTx]);
		gl.glColor3f(1f, 1f, 1f);
		gl.glBegin(GL.GL_QUADS);
			gl.glNormal3f(0f,0f, -1f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(-0.5f, 0f, 0f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(0.5f, 0f, 0f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(0.5f, 1f, 0f);
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(-0.5f, 1f, 0f);
			
			gl.glNormal3f(0f,0f, 1f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(-0.5f, 0f, 0f);
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(-0.5f, 1f, 0f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(0.5f, 1f, 0f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(0.5f, 0f, 0f);
	
			gl.glNormal3f(-1f,0f, 0f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(0f, 0f, -0.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(0f, 0f, 0.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(0f, 1f, 0.5f);
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(0f, 1f, -0.5f);
			
			gl.glNormal3f(1f,0f, 0f);
			gl.glTexCoord2f(0f, 1f);
			gl.glVertex3f(0f, 0f, -0.5f);
			gl.glTexCoord2f(0f, 0f);
			gl.glVertex3f(0f, 1f, -0.5f);
			gl.glTexCoord2f(1f, 0f);
			gl.glVertex3f(0f, 1f, 0.5f);
			gl.glTexCoord2f(1f, 1f);
			gl.glVertex3f(0f, 0f, 0.5f);
		gl.glEnd();
		gl.glPopMatrix();
		gl.glDisable(GL.GL_ALPHA_TEST);
		//gl.glEnable(GL.GL_TEXTURE_2D);
	}
		
	public void setDirection(float[] dir){
	}

	public void free(){
		tt.freeTex(tex.getId());
	}
}
