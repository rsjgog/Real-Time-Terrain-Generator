package project.renderer;

import javax.media.opengl.*;


interface Entity{
	public abstract float[] getPosition();
	public abstract void setRotation(float rotation);
	public abstract float getRotation();
	public abstract void setPosition(float[] position);
	public abstract void draw(GL gl);
	public abstract void free();

	public abstract void setDirection(float [] dir);

}
