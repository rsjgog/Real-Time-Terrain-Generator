package project.noise.multiFractal;

import project.*;

public class MFrac implements Noise{
	Noise base;
	int octaves;
	float decay, scale, offset;

	public MFrac(Noise base, int octaves, float decay, float scale, float offset){
		this.base = base;
		this.octaves = octaves;
		this.decay = decay;
		this.scale = scale;
		this.offset = offset;
	}

	public float noise(float x, float y){
		float f = 20;
		float decayAc = 1;
		float scaleAc = 1;
		for(int i = 0; i < octaves; i++){
//			System.out.println("noise function " + base.noise(scaleAc * x, scaleAc * y));
			f *= decayAc * base.noise(scaleAc * x, scaleAc * y) + offset;
			decayAc *= decay;
			scaleAc *= scale;
		}
		return f;
	}
}
			
