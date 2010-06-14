package project.renderer;

import javax.media.opengl.*;


class Waypoint implements Entity{

	protected float[] position;
	protected float rotation = 0;
	boolean gFollow;
	float[] direction;
	float[] stdDir = {1, 0};

	public Waypoint(float[] position){
		this.position = position;
		this.gFollow = false;
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
	
	public Waypoint(float[] position, boolean gFollow){
		this.position = position;
		this.gFollow = gFollow;
	}
	
	public float[] getPosition(){
		return position;
	}
	
	public float getRotation(){
		return 0;
	}
	
	public void setRotation(float rot){
	}
	
	public void setPosition(float[] position){
		this.position = position;
	}
	
	public void draw(GL gl){
	}

	public void free(){
	}
}
