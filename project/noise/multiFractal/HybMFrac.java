package project.noise.multiFractal;

import project.*;
import java.lang.Math;

public class HybMFrac implements Noise{

	int octaves;
	float h, lac, offset, gain, smooth;
	float[] exponents;
	Noise base;
	Noise base2;
	boolean flag = false;

	public HybMFrac(float h, float lac, int octaves, float offset, float gain, Noise base, float smooth){
		this.h =h;
		this.lac =lac;
		this.octaves =octaves;
		this.offset = offset;
		this.gain = gain;
		this.base = base;
		this.base2 = base;
		this.smooth = smooth;
		popExp();
	}

	public HybMFrac(float h, float lac, int octaves, float offset, float gain, Noise base, Noise base2){
		this.h =h;
		this.lac =lac;
		this.octaves =octaves;
		this.offset = offset;
		this.gain = gain;
		this.base = base;
		this.base2 = base2;
		this.smooth = 1;
		popExp();
		flag = true;
	}

	public HybMFrac(float h, float lac, int octaves, float offset, float gain, Noise base){
		this.h =h;
		this.lac =lac;
		this.octaves =octaves;
		this.offset = offset;
		this.gain = gain;
		this.base = base;
		this.smooth = 1;
		popExp();
	}

	protected void popExp(){
		exponents = new float[octaves + 1];
		float frequency = 1;
		for(int i = 0; i <= octaves; i++){
			exponents[i] = (float) Math.pow(frequency, -h);
			frequency *= lac;
			//System.out.println("exp " + exponents[i]);
		}
	}

	protected float bNoise(float x, float y){
		return (base.noise(x, y) / smooth + 1f); 
	}

	public float noise(float x, float y){
		float signal, weight, f;
		signal = bNoise(x, y) + 1f;
	//	System.out.println("pre signal " + signal);
		if(signal < 0) signal = -signal;
		signal = offset -signal;
		signal *= signal;
		f = signal;
	//	System.out.println("post signal " + signal);
		weight = 1f;
		if(flag){
			signal = base2.noise(x, y) * 2;
			signal = offset + signal;
			signal *= signal;
			f = signal;
		}
		   		
		
		for(int i = 0; i < octaves; i++){
			x *= lac;
			y *= lac;

			weight = signal * gain;
			if (weight > 1) weight = 1;
			if (weight < 0) weight = 0;
			signal = bNoise(x, y);
			if (signal < 0) signal = -signal;
			signal = offset - signal;
			signal *= signal;
		//	System.out.println(i + ", " + weight);
			signal *= weight;
			f += signal * exponents[i];
		}

		return f * smooth;
	}
}			
