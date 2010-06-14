package project.renderer;

import java.lang.Math;

class Tour{

	float[] posTime;
	Waypoint[] pos;

	float[] lookTime;
	Lookat[] look;

	long startTime;
	TMFactory tmf;

	public Tour(float[] posTime, Waypoint[] pos, float[] lookTime, Lookat[] look, TMFactory tmf){ //for testing
		this.posTime = posTime;
		this.pos = pos;
		this.lookTime = lookTime;
		this.look = look;
		this.tmf = tmf;
	}

	public void delay(long wTime){
		startTime += wTime;
	}
	
	public void start(){
		startTime = System.currentTimeMillis();
	}
	
	float[] getPosition(long time){
		time = time - startTime;
		boolean done = false;
		int j = 0;
		float[] retVal = new float[3];
		float[] vals = new float[3];
		
		for(int i = 0; i < posTime.length - 1 && !done; i++){
			if(posTime[i] <= time && posTime[i + 1] >= time){
				j = i;
				done = true;
			}
		}
		if(!done){
			vals = pos[posTime.length - 1].getPosition();
			retVal[0] = vals[0];  
			retVal[1] = vals[1];  
			retVal[2] = vals[2];  
			if(pos[posTime.length - 1].gFollow){
				retVal[1] += getHeight(retVal[0], retVal[2]);
			}
			return retVal;
		}

		int i = j;
		//System.out.println("time = " + time + ", time 1 = " + posTime[i] +
		//		", time 2 = " + posTime[i + 1]);
		float blend = (time - posTime[i]) / (posTime[i + 1] - posTime[i]);
		//System.out.println("blend = " + blend);
		float[] first = getCordsWay(pos[i]);
		float[] second = getCordsWay(pos[i + 1]);
					
		retVal[0] = blend * second[0] + (1 - blend) * first[0];
		retVal[2] = blend * second[2] + (1 - blend) * first[2];
		
		if(pos[i].gFollow){
			first[1] += getHeight(retVal[0], retVal[2]);
		}
		if(pos[i + 1].gFollow){
			second[1] += getHeight(retVal[0], retVal[2]);
		}
		retVal[1] = blend * second[1] + (1 - blend) * first[1];
		
		return retVal;
	}
	
	float[] getDirection(long time, float[] pos){
		time = time - startTime;
		boolean done = false;
		int j = 0;
		for(int i = 0; i < lookTime.length - 1 && !done; i++){
			if(lookTime[i] <= time && lookTime[i + 1] >= time){
				j = i;
				done = true;
			}
		}
		if(!done){
			//System.out.println("defaulting");
			return look[lookTime.length - 1].getDir(pos);
		}
		int i = j;
		//System.out.println("time = " + time + ", time 1t = " + posTime[i] +
		//		", time 2 = " + posTime[i + 1]);
		float blend = (time - lookTime[i]) / (lookTime[i + 1] - lookTime[i]);
		//System.out.println("blend = " + blend);
		float[] retVal = new float[3];
		float[] first = look[i].getDir(pos);
		float[] second = look[i + 1].getDir(pos);
		retVal[0] = blend * second[0] + (1 - blend) * first[0];
		retVal[1] = blend * second[1] + (1 - blend) * first[1];
		retVal[2] = blend * second[2] + (1 - blend) * first[2];
		return retVal;
	}

	boolean done(){
		return((System.currentTimeMillis() - startTime) > posTime[posTime.length - 1] &&
			(System.currentTimeMillis() - startTime) > posTime[posTime.length - 1]);
	}

	protected float[] getCordsWay(Waypoint point){
		float[] retval = new float[3];
		float[] pos = point.getPosition();
		retval[0] = pos[0];
		retval[1] = pos[1];
		retval[2] = pos[2];
	
		return retval;
	}
		

	protected float getHeight(float x, float y){
		int cX = (int) Math.floor((double) x);
		int cY = (int) Math.floor((double) y);
		float posX = x - cX;
		float posY = y - cY;

		
		float height = tmf.getTerrain(cX, cY).getRHeight(posX, posY) / tmf.getCellSize();

		return height;
	}	
}	   	
