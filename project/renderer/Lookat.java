package project.renderer;

class Lookat{
	protected Entity ent;
	protected float[] dir;
	boolean entFlag;

	public Lookat(Entity ent){
		this.ent = ent;
		entFlag = true;
	}

	public Lookat(float[] dir){
		this.dir = dir;
		entFlag = false;
	}

	public float[] getDir(float pos[]){
		if(entFlag){
			float[] entP = ent.getPosition();
			//System.out.println(ent + " position = " + entP[0] + ", " + entP[1] + ", " + entP[2]);
			float[] retval = new float[3];
			
			retval[0] = entP[0] - pos[0];
			retval[1] = entP[1] - pos[1];
			retval[2] = entP[2] - pos[2];
			/*System.out.println("pos (x, y, z) = (" +
				pos[0] + ", " +
				pos[1] + ", " +
				pos[2] + ")");*/

			return retval;
		}
		else{
			return dir;
		}
	}
}
