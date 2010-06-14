package project.noise.noiseUtil;
import project.*;

public class PertubanceFilter implements Noise{

	float scale;
	Noise base, nFunc1, nFunc2;

	public PertubanceFilter(float scale, Noise base, Noise nFunc1, Noise nFunc2){
		this.scale = scale;
		this.base = base;
		this.nFunc1 = nFunc1;
		this.nFunc2 = nFunc2;
	}

	public float noise(float x, float y){
		float dx = nFunc1.noise(x, y);
		float dy = nFunc2.noise(x, y);
		//System.out.println("" + nFunc.noise(x + scale * dx, y + scale * dy));
		//System.out.println("point perturbed " + x + " + " + dx + ", " + y + " + " + dy);
		return base.noise(x + scale * dx, y + scale * dy);
//		return nFunc.noise(x, y);
	}
}
