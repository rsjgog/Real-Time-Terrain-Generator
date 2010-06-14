package project.noise.noiseUtil;
import project.*;

public class NTerrain implements Terrain{
	int width;
	Noise noiseF;
	protected float scale;
	protected final float seper = 20; //seperation

	public NTerrain(int width, Noise noiseF){
		this.noiseF = noiseF;
		this.width = width;
		this.scale = 1;
	}

	public NTerrain(int width, Noise noiseF, float scale){
		this.noiseF = noiseF;
		this.width = width;
		this.scale = scale;
	} 

	public int getWidth(){
		return width;
	}

	public float getHeight(float x, float y){
		float xf = (float) x / width;
		float yf = (float) y / width;
		
		return noiseF.noise(xf, yf) * scale;
	}

	protected float[] normal(float[] v1, float[] v2){
		float[] retVec = new float[3];
		retVec[0] = v1[1] * v2[2] - v1[2] * v2[1]; 
		retVec[1] = v1[2] * v2[0] - v1[0] * v2[2]; 
		retVec[2] = v1[0] * v2[1] - v1[1] * v2[0];
		return retVec;
	}

	protected float[] normalize(float[] v){
		float[] rV = new float[3]; 
		float h;
		h = v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
		h = (float)Math.sqrt(h);
		rV[0] = v[0] / h;
		rV[1] = v[1] / h;
		rV[2] = v[2] / h;
		return rV;
	}

	
	//could be used to gen normals for lighting, probably only
	//worthwhile for perimeter. Main purpose is for finding paths.
	public float[] getNormal(float x, float y){

		float xf = (float) x / width;
		float yf = (float) y / width;
		
		float d = 0.01f;
		float height = noiseF.noise(xf, yf) * scale;
		//System.out.println("height = " + height);
		//slightly terse but makes code more compact
		float[] heights = {noiseF.noise(xf + d, yf) * scale - height,
						   noiseF.noise(xf - d, yf) * scale + height,
						   noiseF.noise(xf, yf + d) * scale - height,
						   noiseF.noise(xf, yf - d) * scale + height};
		float[] v1 = new float[3];
		float[] v2 = new float[3];
		v1[0] = 0;
		v1[1] = heights[2];
		v1[2] = d * seper * width;
		v2[0] = d * seper * width;
		v2[1] = heights[0];
		v2[2] = 0;
		float[] n = normalize(normal(v1, v2));
		float[] retV = n;
		v1[1] = heights[2];
		v2[1] = heights[1];
		n = normalize(normal(v1, v2));
		retV[0] += n[0];
		retV[1] += n[1];
		retV[2] += n[2];
		v1[1] = heights[3];
		v2[1] = heights[0];
		n = normalize(normal(v1, v2));
		retV[0] += n[0];
		retV[1] += n[1];
		retV[2] += n[2];
		v1[1] = heights[3];
		v2[1] = heights[1];
		n = normalize(normal(v1, v2));
		//System.out.println("normal from terrain " + n[0] + ", " + n[1] + ", " +  n[2]);
		retV[0] += n[0];
		retV[1] += n[1];
		retV[2] += n[2];
		retV = normalize(retV);
		return retV;
	}		
}
