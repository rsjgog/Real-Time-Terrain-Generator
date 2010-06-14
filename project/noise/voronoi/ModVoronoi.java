package project.noise.voronoi;

import project.*;
import project.noise.noiseUtil.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.lang.Math;

public class ModVoronoi implements Noise{

	float[] coefs;
	float[][] points;
	boolean square = false;
	long seed;
	int x,y;
//	Voronoi north, south, east, west, nEast, sEast, nWest, sWest;

	final float upperD = 2; //asserts an upper limit for distance, if wrong will cause problems
	protected final float maxD = 1; //actual max distance allowed
	protected final float fMute = 0.7f;

	public ModVoronoi(int features, float[] coefs, long seed, int x, int y){
		this.coefs = coefs;
		this.seed = seed;
		this.x = x;
		this.y = y;
		//long ranSeed = UniqueInt.getLong((long) x, (long) y);
		//long ranSeed = (long) (Math.pow(5, natural(seed)) + Math.pow(2, natural((long) x)) + y);
		//Random ran = new Random(ranSeed);
		/*Random ran = new Random(ranSeed);
		ran.nextLong();
		ranSeed = ran.nextLong() + seed;
		ran = new Random(ranSeed);
		ran.nextLong();*/
		
		long ranSeed;
		Random ran = new Random(seed);
		ran.nextLong();
		ranSeed = ran.nextLong() + (long) x;
		ran = new Random(ranSeed);
		ran.nextLong();
		ranSeed = ran.nextLong() + (long) y;
		ran = new Random(ranSeed);
		ran.nextLong();
		
		this.points = new float[features][3];
		//System.out.println("Random seed for " + x + ", " + y + " is " + ranSeed);
		ran.nextFloat();
		for(int i = 0; i < features; i++){
			points[i][0] = ran.nextFloat();
			//System.out.println("" + points[i][0]);
			points[i][1] = ran.nextFloat();
			points[i][2] = ran.nextFloat();
			//System.out.println("x = " + points[i][0] + ", y = " + points[i][1]);
		//	System.out.println("points length in constructor " + points[i].length);
		}
		//System.out.println();
	}
	
	protected long natural(long l){
		long retV;
		retV = (l < 0) ? -2 * l - 1 : 2 * l;
		return retV;
	}
	
	public void makeGenable(){
		Vector v = new Vector();
		int features = points.length;
		//float[][] lPoints; //local points
		ModVoronoi local;
		//System.out.println("Starting Making Genable");
		float[] point;
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				local = new ModVoronoi(features, coefs, seed, x + i, y + j);
				for(int k = 0; k < local.points.length; k++){
					point = new float[3];
					point[0] = local.points[k][0] + i;
					point[1] = local.points[k][1] + j;
					point[2] = local.points[k][2];
					//System.out.println("putting " + point[0] + ", " + point[1]);
					v.add(point);
				}
			}
		}
		//System.out.println("Almost finished");
		Iterator it = v.iterator();
		this.points = new float[v.size()][3];
		int i = 0;
		while(it.hasNext()){
			points[i] = (float[]) it.next();
			i++;
		}
		//System.out.println("Done!");
	}

	protected float[][] getDists(float x, float y){
		float[][] dists = new float[points.length][2];
		int i;
		float dx, dy;
		for(i = 0; i < points.length; i++){
			dx = points[i][0] - x;
			dy = points[i][1] - y;
			dists[i][0] = dx * dx + dy * dy;
			dists[i][1] = points[i][2];
		}
		Arrays.sort(dists);
		return dists;
	}

	//optimized because not entire list is needs to be sorted
	protected float[][] getDists2(float x, float y){
		int n = (coefs.length < points.length) ? coefs.length: points.length;
		float[][] retV = new float[n][2];
		float[][] dists = new float[points.length][2];
		int i, j, k;
		float dx, dy;

		for(i = 0; i < points.length; i++){
				//	System.out.println("loop 0 entered");
			dx = points[i][0] - x;
			dy = points[i][1] - y;
			dists[i][0] = dx * dx + dy * dy;
			//System.out.println("points length in sort " + points[i].length);
			dists[i][1] = points[i][2];
//			d = dx * dx + dy * dy;

		}
//		dists = new float[v.size()];
		
		for(i = 0; i < n; i++){
			//System.out.println("loop 0 entered");
			retV[i][0] = upperD * upperD;
			retV[i][1] = 0;
		}
		boolean less = false;
		float[] swap, next;
		//			System.out.println("loop not entered!");
		for(i = 0; i < dists.length; i++){
			j = 0;
			less = false;
			while(j < n && !less){
				if(dists[i][0] < retV[j][0]){
					//System.out.println("smaller found");
					next = dists[i];
					for(k = j; k < n; k++){
						swap = next;
						next = retV[k];
						retV[k] = swap;
					less = true;
					}
				}
				j++;
			}
		}
			
		return retV;
	}

/*	public float getUpper(){
		return upperD;
	}*/	


	public float noise(float x, float y){
		float f = 0;
		float [][] dists = getDists2(x, y);
		int n = (coefs.length < points.length) ? coefs.length: dists.length;
		for(int i = 0; i < n; i ++){
			if(!(coefs[i] == 0) && !square/* && dists[i][0] <= maxD*/){
				if(i != 0 || dists[i][1] < fMute){
					dists[i][0] = (float) Math.sqrt(dists[i][0]);
				}
				else{
					dists[i][0] = (float) Math.sqrt(dists[i + 1][0]);
					//dists[i][0] = dists[i +1][0];
				}
				f += coefs[i] * dists[i][0];
			}
		}
		return f;
	}
}
