package project.renderer;

class Tours{

	public static Tour tour1(Entity hover, TMFactory tmf){
		float[] times = {0f, 5000f, 6000f, 10000f, 18000f};
		float[] pos1 = {0.2f, 0.3f, 0.5f};
		float[] pos2 = {0.5f, 0.3f, 0.5f};
		float[] pos3 = {0.8f, 0.2f, 0.8f};
		float[] pos4 = {0.2f, 0.2f, 0.2f};
	
		float[] lookp1a ={0.5f, 0.25f, 0.8f};
		Waypoint lookp1 = new Waypoint(lookp1a);
		float[] lookdir = {0f, -0.1f, -1f};

		float[] lookTimes = {0f, 2000f, 16000f, 18000f};	

		Waypoint[] poss = {new Waypoint(pos1),
						new Waypoint(pos2),
						new Waypoint(pos3, true),
						new Waypoint(pos4, true),
						new Waypoint(pos1)
		};

		Lookat[] look = {new Lookat(lookp1),
					//new Lookat(lookdir),
					new Lookat(hover),
					new Lookat(hover),
					new Lookat(lookp1)
		};

		return new Tour(times, poss, lookTimes, look, tmf);
	}

	public static Tour tour2(Entity hover, TMFactory tmf){
		float[] times = {0f, 8000f};
		float[] pos1 = {0.5f, 0.2f, 0.5f};
	
		float[] lookp1a ={0.5f, 0.25f, 0.8f};
		Waypoint lookp1 = new Waypoint(lookp1a);
		
		float[] lookdir1 = {0f, -0.1f, 1f};
		float[] lookdir2 = {1f, -0.1f, 0f};
		float[] lookdir3 = {0f, -0.1f, -1f};
		float[] lookdir4 = {-1f, -0.1f, 0f};

		float[] lookTimes = {0f, 16000f, 32000f};	

		Waypoint[] poss = {new Waypoint(pos1, true),
							new Waypoint(pos1, true)
		};

		Lookat[] look = {new Lookat(lookdir1),
						new Lookat(lookdir2),
						new Lookat(lookdir3),
		};

		return new Tour(times, poss, lookTimes, look, tmf);
	}
	
	public static Tour tour3(Entity hover, TMFactory tmf){
		float[] times = {0f, 6000f, 12000f, 15000f, 25000f, 50000f};
		float[] pos1 = {0.5f, 0.25f, 0.5f};
		float[] pos2 = {-0.639f, 0.1f, 0.491f};
		float[] pos3 = {-0.639f, 0.15f, 0.45f};
		float[] pos4 = {-1.573f, 0.179f, 1.291f};
	
		float[] lookp1a ={-0.353f, 0.0658f, 0.462f};
		float[] lookp2a ={-1.265f, 0.157f, 2.534f};
		Waypoint lookp1 = new Waypoint(lookp1a);
		Waypoint lookp2 = new Waypoint(lookp2a);
		float[] lookdir = {0f, -0.1f, 1f};

		float[] lookTimes = {0f, 6000f, 15000f, 25000f, 35000f, 50000f};	

		Waypoint[] poss = {new Waypoint(pos1),
						new Waypoint(pos2),
						new Waypoint(pos2),
						new Waypoint(pos3),
						new Waypoint(pos3),
						new Waypoint(pos4)
		};

		Lookat[] look = {new Lookat(lookp1),
						new Lookat(lookp1),
						new Lookat(hover),
						new Lookat(hover),
						new Lookat(lookp2),
						new Lookat(lookp2)
		};

		return new Tour(times, poss, lookTimes, look, tmf);
	}

	public static EntAnimator entTour1(Entity hover, TMFactory tmf){
		float[][] path = {	//{0.164f, 0f, 0.383f},
							{-0.19f, 0f, 0.443f},
							{-0.353f, 0f, 0.462f},
							{-0.8f, 0f, 0.515f}};

		float[] times = {0f, 8000f, 30000f};

		float[] looktimes = {0f, 7950f, 8050f, 68000f};

		Waypoint[] poss ={
							new Waypoint(path[0], true),
							new Waypoint(path[1], true),
							new Waypoint(path[2], true)
		};

		Lookat[] look = {
							new Lookat(new Waypoint(path[1])),
							new Lookat(new Waypoint(path[1])),
							new Lookat(new Waypoint(path[2])),
							new Lookat(new Waypoint(path[2]))
		};
		Tour pathT = new Tour(times, poss, looktimes, look, tmf);
		EntAnimator anim = new EntAnimator(hover, pathT, false);
		return anim;
		
	}
}
