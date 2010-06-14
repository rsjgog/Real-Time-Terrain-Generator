package project.noise.noiseUtil;
import project.*;

public class NComposite implements Noise{
	Noise n1, n2;

	public NComposite(Noise n1, Noise n2){
		this.n1 = n1;
		this.n2 = n2;
	}

	public float noise(float x, float y){
		return n1.noise(x, y) + n2.noise(x, y);
	}
}
