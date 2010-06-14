package project.noise.voronoi;

import project.*;

//implemented as a wrapper to Voronoi
//only worthwhile as long as coefs alternate in sign and decay

public class NormVoronoi implements Noise{

	protected ModVoronoi v;
	protected float scale;

	public NormVoronoi(int features, float[] coefs, long seed, int x, int y){
		v = new ModVoronoi(features, coefs, seed, x, y);
		/*float pve = 0;
		float nve = 0;
		for(int i = 0; i < coefs.length; i++){
				if(coefs[i] < 0){
					nve += coefs[i];
				}
				else{
					pve += coefs[i];
				}
		}
		nve = -nve;
		float coSc = (nve < pve) ? pve: nve;*/
		/*
		 * above is safe but exsesive, only the first coeficient maters
		 * bellow is code which works only under the conditions specified
		 */
		int n = (coefs.length < features) ? coefs.length: features;
		float coSc = 0;
		for(int i = 0; i < n; i++){
			coSc += coefs[i];
		}

		if (coSc < 0) coSc = -coSc;
		
		scale = coSc * v.upperD;
	}
	
	public void makeGenable(){
		v.makeGenable();
	}

	public float noise(float x, float y){
		//System.out.println("upper = " + v.upperD);
		//System.out.println("scale = " + scale);
		return v.noise(x, y) / scale;
	}
}
