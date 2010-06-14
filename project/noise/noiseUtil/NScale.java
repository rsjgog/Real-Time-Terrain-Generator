package project.noise.noiseUtil;
import project.*;

public class NScale implements Noise{

	float a, b;
	Noise base;

	public NScale(float a, float b, Noise base){
		this.a = a;
		this.b = b;
		this.base = base;
	}

	public float noise(float x, float y){
		return a * base.noise(x, y) + b;
	}
}
