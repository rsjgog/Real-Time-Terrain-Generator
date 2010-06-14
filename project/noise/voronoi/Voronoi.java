package project.noise.voronoi;

import project.*;
import project.noise.noiseUtil.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.lang.Math;

public class Voronoi implements Noise{

	float[] coefs;
	float[][] points;
	boolean square = false;
	long seed;
	int x,y;
//	Voronoi north, south, east, west, nEast, sEast, nWest, sWest;

	final float upperD = 2; //asserts an upper limit for distance, if wrong will cause problems
	protected final float maxD = 1; //actual max distance allowed

	public Voronoi(int features, float[] coefs, long seed, int x, int y){
		this.coefs = coefs;
		this.seed = seed;
		this.x = x;
		this.y = y;
		//Random ran = new Random((long) (Math.pow(5, seed) * Math.pow(3, x) * Math.pow(2, y)));
		long ranSeed = UniqueInt.getLong((long) x, (long) y);
		Random ran = new Random(ranSeed);
		ran.nextLong();
		ranSeed = ran.nextLong() + seed;
		ran = new Random(ranSeed);
		ran.nextLong();
		this.points = new float[features][3];

		this.points = new float[features][2]; 
		for(int i = 0; i < features; i++){
			points[i][0] = ran.nextFloat();
			//System.out.println("" + points[i][0]);
			points[i][1] = ran.nextFloat();
		}
	}
	
	public void makeGenable(){
		Vector v = new Vector();
		int features = points.length;
		//float[][] lPoints; //local points
		Voronoi local;
		//System.out.println("Starting Making Genable");
		float[] point;
		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				local = new Voronoi(features, coefs, seed, x + i, y + j);
				for(int k = 0; k < local.points.length; k++){
					point = new float[2];
					point[0] = local.points[k][0] + i;
					point[1] = local.points[k][1] + j;
					//System.out.println("putting " + point[0] + ", " + point[1]);
					v.add(point);
				}
			}
		}
		//System.out.println("Almost finished");
		Iterator it = v.iterator();
		this.points = new float[v.size()][2];
		int i = 0;
		while(it.hasNext()){
			points[i] = (float[]) it.next();
			i++;
		}
		//System.out.println("Done!");
	}

	protected float[] getDists(float x, float y){
		float[] dists = new float[points.length];
		int i;
		float dx, dy;
		for(i = 0; i < points.length; i++){
			dx = points[i][0] - x;
			dy = points[i][1] - y;
			dists[i] = dx * dx + dy * dy;
		}
		Arrays.sort(dists);
		return dists;
	}

	//optimized because not entire list is needs to be sorted
	protected float[] getDists2(float x, float y){
		int n = (coefs.length < points.length) ? coefs.length: points.length;
		float[] retV = new float[n];
		float[] dists = new float[points.length];
		int i, j, k;
		float dx, dy;

		for(i = 0; i < points.length; i++){
				//	System.out.println("loop 0 entered");
			dx = points[i][0] - x;
			dy = points[i][1] - y;
			dists[i] = dx * dx + dy * dy;
//			d = dx * dx + dy * dy;

		}
//		dists = new float[v.size()];
		
		for(i = 0; i < n; i++){
			//System.out.println("loop 0 entered");
			retV[i] = upperD * upperD;
		}
		boolean less = false;
		float swap, next;
		//			System.out.println("loop not entered!");
		for(i = 0; i < dists.length; i++){
			j = 0;
			less = false;
			while(j < n && !less){
				if(dists[i] < retV[j]){
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
		float [] dists = getDists2(x, y);
		int n = (coefs.length < points.length) ? coefs.length: dists.length;
		for(int i = 0; i < n; i ++){
			if(!(coefs[i] == 0) && !square && dists[i] <= maxD) dists[i] = (float) Math.sqrt(dists[i]);
			f += coefs[i] * dists[i];
		}
		return f;
	}
}
