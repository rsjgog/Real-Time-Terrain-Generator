package project.noise.fBm;

import project.*;

public class FBm implements Noise{
	Noise base;
	int octaves;
	float decay, scale;

	public FBm(Noise base, int octaves, float decay, float scale){
		this.base = base;
		this.octaves = octaves;
		this.decay = decay;
		this.scale = scale;
	}

	public float noise(float x, float y){
		float f = 0;
		float decayAc = 1;
		float scaleAc = 1;
		for(int i = 0; i < octaves; i++){
//			System.out.println("noise function " + base.noise(scaleAc * x, scaleAc * y));
			f += decayAc * base.noise(scaleAc * x, scaleAc * y);
			decayAc *= decay;
			scaleAc *= scale;
		}
		return f;
	}
}
			
