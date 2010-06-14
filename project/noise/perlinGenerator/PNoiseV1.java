package project.noise.perlinGenerator;

import project.*;
import java.util.Random;
import java.lang.Math;

//variant of perlin noise, uses un-normalized gradients
public class PNoiseV1 implements Noise{
	
	protected static int repeat = 256;
	
	Random r;
	long seed; //the global seed
	int gx, gy; //global x
	int size; //reseloution of white noise
//	int rez; //reseloution of a cell
	int[] p;
	float[][] g;

	PNoiseV1(long seed, int size, int gx, int gy, float[][] g){

		this.seed = seed;
		this.size = size;
		this.gx = gx;
		this.gy = gy;
		this.g = g;
		
		r = new Random((long)(seed * (Math.pow(2, gx) * Math.pow(3, gy))));
		popP();
	}
		
	
	//static because it doesn't matter if it is repeated
	//cache once and use for all cells of terrain
	protected static float[][] gradTable(long seedGlo){
		float[][] retVec = new float[repeat][2];
		Random ran = new Random(seedGlo);
		boolean in = false; //this flags accept of a result
		float x = 1;
		float y = 1;
		for(int i = 0; i < repeat; i++){
			while(!in){
				x = 2 * ran.nextFloat() - 1;
				y = 2 * ran.nextFloat() - 1;
				if(!(x == 0 && y == 0))
					in = ((x * x) + (y * y) < 1);
			}
			in = false;
			retVec[i][0] = x;
			retVec[i][1] = y;
			//System.out.println("g " + i + " = " + x + ", " + y);
		}
		return retVec;
	}

	protected void popP(){
		int i;
		p = new int[repeat];
		for(i = 0; i < repeat; i++){
			p[i] = i;
		}
		for(i = 0; i < repeat; i++){
			p[i] = p[r.nextInt(repeat)];
		}
	}

	protected int fold(int x, int y){
	   int n = p[x % repeat]; 
	   n = p[(n + y) % repeat];
	   return n;
	}

	protected float drop(float t){
		if(t > 1 || t < -1){
			return 0f;
		}
		float f;
		t = Math.abs(t);
		f = 1 - (3 * t * t) + (2 * t * t *t);
		return f;
	}

	public float noise(float x, float y){
		float f = 0;
		float omega, u, v;
		
		x = x % 1;
		y = y % 1;

		x *= size;
		y *= size;
		int index;

		int i = (int) Math.floor(x);		
		int j = (int) Math.floor(y);
		
		for(int di = 0; di < 2; di++){
			for(int dj = 0; dj < 2; dj++){
				u = x - (i + di);
				v = y - (j + dj);

				omega = drop(u) * drop(v);
				index = fold((i + di) % (size), (j + dj) % (size));
				f += omega * (u * g[index][0] + v * g[index][1]);
			}
		}
//		System.out.println("noise says " + f);
		return f;
	}						
}
