package project.renderer;
import javax.media.opengl.*;


class EntAnimator{

	protected Entity ent;
	protected Tour path;
	boolean loop;

	public EntAnimator(Entity ent, Tour path, boolean loop){
		this.ent = ent;
		this.path = path;
		this.loop = loop;
	}

	public void drawEnt(GL gl){
		/*if(loop && path.done()){
			path.start();
			//System.out.println("BOOM!");
		}*/
		//ent.setPosition(path.getPosition(time));
		ent.draw(gl);
	}

	public Entity getEnt(){
		/*if(loop && path.done())
			path.start();
		long time = System.currentTimeMillis();*/
		//ent.setPosition(path.getPosition(time));
		return ent;
	}

	public void position(long time){
		if(loop && path.done()){
			path.start();
			//System.out.println("BOOM!");
		}
		float[] pos = path.getPosition(time);
		ent.setPosition(pos);
		float[] dir3 = path.getDirection(time, pos);
		float[] dir2 = new float[2];
		dir2[0] = dir3[0];
		dir2[1] = dir3[2];
		//System.out.println(dir2[0] +", " + dir2[1] + " in animator");
		ent.setDirection(dir2);
	}

	public void start(){
		path.start();
	}

	public void delay(long wTime){
		path.delay(wTime);
	}

	float[] times = {0f, 30000f, 60000f, 90000f, 120000f};
	float[] pos1 = {0.2f, 0f, 0.2f};
	//float[] pos2 = {0.8f, 0.3f, 0.5f};
	float[] pos2 = {0.2f, 0f, 0.8f};
	float[] pos3 = {0.8f, 0f, 0.8f};
	float[] pos4 = {0.8f, 0f, 0.2f};
	
	float[] lookdir1 = {0f, -0.1f, 1f};
	float[] lookdir2 = {1f, -0.1f, 0f};
	float[] lookdir3 = {0f, -0.1f, -1f};
	float[] lookdir4 = {-1f, -0.1f, 0f};

	Robot hover;

	float[] lookTimes = {0f, 29750f, 30250f, 59750f, 60250f, 89750f, 90250, 119500, 120000};
	
	Waypoint[] poss = {new Waypoint(pos1, true),
						new Waypoint(pos2, true),
						new Waypoint(pos3, true),
						new Waypoint(pos4, true),
						new Waypoint(pos1, true)
	};

	Lookat[] look = {new Lookat(lookdir1),
					new Lookat(lookdir1),
					new Lookat(lookdir2),
					new Lookat(lookdir2),
					new Lookat(lookdir3),
					new Lookat(lookdir3),
					new Lookat(lookdir4),
					new Lookat(lookdir4),
					new Lookat(lookdir1),
					};
	
	public EntAnimator(TMFactory tmf, Entity ent){
		this.path = new Tour(times, poss, lookTimes, look, tmf);
		this.ent = ent;
		this.loop = true;
	}
}	
