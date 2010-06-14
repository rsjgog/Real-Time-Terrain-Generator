package project.pathSearch;

import java.lang.Math;
import project.*;
import java.util.Vector;

public class GradLimPath{

	float[] start, goal;
	float g; //gradient	
	final float step = 0.01f;
	Terrain terrain;
	Vector path;
	final float scale = 20f;
	int width;

	public Vector getPath(){
		return path;
	}
	
	public GradLimPath(float[] start2, float[] goal2, float g, Terrain terrain){
		//goal2 and start2 refer to two components (x and z) not a second goal and start
		start = new float[3];
		goal = new float[3];
		width = terrain.getWidth();
		this.terrain = terrain;
		this.start[0] = start2[0];
		this.start[2] = start2[1];
		this.start[1] = terrain.getHeight(width * start2[0], width * start2[1]);
		
		this.goal[0] = goal2[0];
		this.goal[2] = goal2[1];
		this.goal[1] = terrain.getHeight(width * goal2[0], width * goal2[1]);
		this.g = g;
		this.path = new Vector();
		popPath();
	}
	
	protected float[] normal(float[] v1, float[] v2){
		float[] retVec = new float[3];
		retVec[0] = v1[1] * v2[2] - v1[2] * v2[1]; 
		retVec[1] = v1[2] * v2[0] - v1[0] * v2[2]; 
		retVec[2] = v1[0] * v2[1] - v1[1] * v2[0];
		return retVec;
	}

	protected float[] normalize(float[] v){
		float[] rV = new float[3]; 
		float h;
		h = v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
		if(h == 0) h = 1;
		h = (float)Math.sqrt(h);
		rV[0] = v[0] / h;
		rV[1] = v[1] / h;
		rV[2] = v[2] / h;
		return rV;
	}
	
	//returns the four directions with the 
	protected float[][] getGLimDirections(float[] n){
		float[][] retV = new float[4][3];
		float sqrtCo = (float) Math.sqrt(n[0] * n[0] + n[2] * n[2] - g * g * n[1] * n[1]);
		//consider other directions relative to this...
		retV[0][2] = n[2] * sqrtCo - n[0] * n[1] * g; //first part is x second z
		retV[0][1] = n[0] * n[0] * g;
		retV[0][0] = -n[0] * sqrtCo - n[2] * n[1] * g;
		//x and z fliped (y flips with z)
	   	retV[1][0] = - retV[0][0];
		retV[1][1] = - retV[0][1];
	   	retV[1][2] = - retV[0][2];
		//x flipped
		retV[2][2] = -n[2] * sqrtCo - n[0] * n[1] * g; 
		retV[2][1] = retV[0][1];
		retV[2][0] = n[0] * sqrtCo - n[2] * n[1] * g;
		//z flipped
	   	retV[3][0] = - retV[2][0];
		retV[3][1] = - retV[0][1];
	   	retV[3][2] = - retV[2][2];

		//now normalize, all are same length so requires one length to be calculated only
		float l = (float) Math.sqrt(retV[0][0] * retV[0][0] + retV[0][1] * retV[0][1] + retV[0][2] * retV[0][2]);
		//System.out.println("unscaled vector length " + l);
		if(l == 0){
			l = 1;
			//System.out.println("0 vector found");
		}
		for(int i = 0; i < retV.length; i++){

		//	System.out.println("l =  " + l);
			retV[i][0] = retV[i][0] / l;
			retV[i][1] = retV[i][1] / l;
			retV[i][2] = retV[i][2] / l;
		//	lrv = retV[i][0] * retV[i][0]
		//		+ retV[i][1] * retV[i][1]
		//		+ retV[i][2] * retV[i][2];
		//	System.out.println("vector length " + lrv);
		}
		return retV;
	}

	protected float[] getDirection(float[] pos){
		float[] gDir = {goal[0] - pos[0], goal[1] - pos[1], goal[2] - pos[2]};
		int width = terrain.getWidth();
		//System.out.println("requesting normal");
		float[] n = terrain.getNormal(width * pos[0], width * pos[2]);
		//if going straight towards the goal is possible then go there
		//System.out.println("normal recieved " + n[0] + ", " + n[1] + ", " +  n[2] + "\n");
		float yVal = (-n[0] * gDir[0] - n[2] * gDir[2]) / n[1];
		if(yVal * yVal / (gDir[0] * gDir[0] + gDir[2]* gDir[2]) < g * g ){
		//if(true){
			System.out.println("going straight");
			gDir[1] = yVal;
			return normalize(gDir);
		}
		float[][] dirs = getGLimDirections(n);
//		float gDist =  gDir[0] * gDir[0] + gDir[1] * gDir[1] + gDir[2] * gDir[2];
		//latter evaluation will be more than just distance;
		float bestDist = (dirs[0][0] * step - gDir[0]) * (dirs[0][0] * step  - gDir[0])
		   			//	+ (dirs[0][1] * step  - gDir[1]) * (dirs[0][1] * step - gDir[1])
						+ (dirs[0][2] * step - gDir[2]) * (dirs[0][2] * step - gDir[2]);
		int bestDir = 0;
		float dist;
		for(int i = 1; i < dirs.length; i++){
			dist = (dirs[i][0] * step - gDir[0]) * (dirs[i][0] * step - gDir[0])
		   		//	+ (dirs[i][1] * step - gDir[1]) * (dirs[i][1] * step - gDir[1])
					+ (dirs[i][2] * step - gDir[2]) * (dirs[i][2] * step - gDir[2]);
			if(dist < bestDist){
				bestDist = dist;
				bestDir = i;
			}
		}
		return dirs[bestDir];
	}

	protected void copyA3(float[] src, float[] dest){
		dest[0] = src[0];
		dest[1] = src[1];
		dest[2] = src[2];
	}

	protected void popPath(){
		//System.out.println("popping path");
		float[] current = new float[3];
		float[] element = new float[3];
		float[] marked = new float[3];
		copyA3(start, current);
		copyA3(current, element);
		path.add(element);
	//	System.out.println("currently at" + current[0] + ", " + current[1] + ", " + current[2]);
		boolean atGoal = false;
		boolean slack = false;
		float xd, zd, yd;
		int i = 0;
		int count = 0;
		int width = terrain.getWidth();

		float gb = g;
		float slackD;

		//calculate the min number of steps to goal
		float steps = (float) Math.sqrt((goal[0] - start[0]) * (goal[0] - start[0])
								+ (goal[1] - start[1]) * (goal[1] - start[1]) / (width * width * scale * scale)
								+ (goal[2] - start[2]) * (goal[2] - start[2])) / step;

		
		while(!atGoal){
			xd = (current[0] - goal[0]);
			yd = (current[1] - goal[1]) / (width * scale);
			//yd = 0;
			zd = (current[2] - goal[2]);

			element = new float[3];
				
			if(xd * xd + zd * zd + yd * yd < 4 * step * step){
				copyA3(goal, element);
				path.add(element);
				atGoal = true;
				//System.out.println("At Goal! " + i + " steps taken.");
			}
			else{
				float[] dir = getDirection(current);			
	//			System.out.println("moving " + dir[0] + ", " + dir[1] + ", " + dir[2]);
				current[0] += step * dir[0];
				//current[1] += step * dir[1];
				current[2] += step * dir[2];
				current[1] = terrain.getHeight(width * current[0], width * current[2]);
				System.out.println("currently at " + current[0] + ", " + current[1] + ", " + current[2]);
				//System.out.println("goal " + goal[0] + ", " + goal[1] + ", " + goal[2]);
				copyA3(current, element);
				path.add(element);
				i++;
				count++;
			}
			if(count > 3 * steps){
				g *= 1.2;
				if(g > 100) g = 100;
				//System.out.println("g = " + g);
				if(!slack){
					//System.out.println("setting slacken");
					slack = true;
					copyA3(current, marked);
					steps = (float) Math.sqrt((goal[0] - marked[0]) * (goal[0] - marked[0])
								+ (goal[1] - marked[1]) * (goal[1] - marked[1]) / (width * width * scale * scale)
								+ (goal[2] - marked[2]) * (goal[2] - marked[2])) / step;
				}
			}
			
			slackD = ((current[0] - marked[0]) * (current[0] - marked[0])
			+ (current[1] - marked[1]) * (current[1] - marked[1]) / (width * width * scale * scale)
			+ (current[2] - marked[2]) * (current[2] - marked[2]));
			
			if(slack &&
			(slackD > 100 * step * step)){
				//System.out.println("Deslackening at sq distance " + slackD);
				g = gb;
				count = 0;
				slack = false;
			}
//			if(i > 1000) atGoal = true;
		}
		g = gb;
	}
}
