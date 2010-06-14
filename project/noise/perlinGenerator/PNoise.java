package project.noise.perlinGenerator;

import project.*;
//import project.noise.noiseUtil.*;
import java.util.Random;
import java.lang.Math;

//generates 2D perlin noise
public class PNoise implements Noise{
	
	protected static int repeat = 256;
	
	Random r;
	long seed; //the global seed
	int gx, gy; //global x
	int size; //reseloution of white noise
//	int rez; //reseloution of a cell
	int[] p;
	float[][] g;
	boolean genable; //flags that area is generatable
	PNoise south, east, sEast;

	PNoise(long seed, int size, int gx, int gy, float[][] g){

		this.seed = seed;
		this.size = size;
		this.gx = gx;
		this.gy = gy;
		this.g = g;
		//long ranSeed = (long) (Math.pow(5, natural(seed)) * Math.pow(3, natural((long) gx)) * Math.pow(2, natural((long) gy)));
		/*long ranSeed = UniqueInt.getLong((long) gx, (long) gy);
		r = new Random(ranSeed);
		r.nextLong();
		ranSeed = r.nextLong() + seed;
		r = new Random(ranSeed);
		//System.out.println("Hello?  " + r + "\n \n");
		r.nextLong();

		//r = new Random(ranSeed);*/
		
		long ranSeed;
		Random ran = new Random(seed);
		ran.nextLong();
		ranSeed = ran.nextLong() + (long) gx;
		ran = new Random(ranSeed);
		ran.nextLong();
		ranSeed = ran.nextLong() + (long) gy;
		ran = new Random(ranSeed);
		ran.nextLong();
		
		r = ran;
		
		popP();
		genable = false;
	}

	protected long natural(long l){
		long retV;
		retV = (l < 0) ? -2 * l - 1 : 2 * l;
		return retV;
	}
	
	public void makeGenable(){
		this.south = new PNoise(seed, size, gx +1, gy, g);
		this.east = new PNoise(seed, size, gx, gy + 1, g);
		this.sEast = new PNoise(seed, size, gx + 1, gy + 1, g);
		genable = true;
	}
		
	protected static float[] normalize(float x, float y){
		float[] rV = new float[2]; 
		float h;
		h = x * x + y * y;
		h = (float)Math.sqrt(h);
		if(h == 0)
			h = 1;
		rV[0] = x / h;
		rV[1] = y / h;
		return rV;
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
			retVec[i] = normalize(x, y);
			//System.out.println("g " + i + " = " + x + ", " + y);
		}
		return retVec;
	}

	protected void popP(){
		int i;
		int sVal;
		p = new int[repeat];
		for(i = 0; i < repeat; i++){
			p[i] = i;
		}
		//System.out.println("random numbers for " + gx + ", " +gy);
		for(i = 0; i < repeat; i++){
			//System.out.println("\n\n" + r + "\n\n");
			sVal = r.nextInt(repeat);
			p[i] = p[sVal];
			if(i < 5){
				//System.out.println("Random number is " + sVal);
			}
		}
		//System.out.println("");
	}

	int fold(int x, int y){
		
		int mx = x % size;
		int my = y % size;

		if(mx == 0 && x != 0){
			if(my == 0 && y != 0){
				return sEast.fold(0, 0);
			}
			return south.fold(0, y);
		}

		if(my == 0 && y != 0){
			return east.fold(x, 0);
		}

		x = mx;
		y = my;
		
		int n = x % repeat;
		if(n < 0) n = repeat + n;
		n = p[n];
		int m = (n + y) % repeat;
		if(m < 0) m = repeat + m;
		n = p[m];
		return n;
	}

	protected float drop(float t){
		if(t > 1 || t < -1){
			return 0f;
		}
		float f;
		if (t < 0) t = -t;
	//	f = 1 - (3 * t * t) + (2 * t * t *t);
		f = 1 - 6 * (t * t * t * t * t)
		   	+ 15 * (t * t * t * t)
			- 10 * (t * t * t);
		return f;
	}

	public float noise(float x, float y){
		float f = 0;
		float omega, u, v;
		
		//x = x % 1;
		//y = y % 1;

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
				index = fold((i + di), (j + dj));
				f += omega * (u * g[index][0] + v * g[index][1]);
			}
		}
//		System.out.println("noise says " + f);
		return f;
	}						
}
