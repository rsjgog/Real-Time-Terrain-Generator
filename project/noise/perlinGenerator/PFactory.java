package project.noise.perlinGenerator;

public class PFactory{

	protected float[][] g;
	long seed;

	public PFactory(long seed){
		this.seed = seed;
		g = PNoise.gradTable(seed);
	}

	public PNoise getPNoise(int size, int gx, int gy){
		PNoise pn = new PNoise(seed, size, gx, gy, g);
		pn.makeGenable();
		return pn;
	}

	public PNoiseV1 getPNoiseV1(int size, int gx, int gy){
		return new PNoiseV1(seed, size, gx, gy, g);
		
	}
}
