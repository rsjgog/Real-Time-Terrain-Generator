package project.renderer;
import project.Terrain;

public class PseudoTerrain implements Terrain{

	int width;
	float sFactor;
	RenderingTerrainModel rtm;

	PseudoTerrain(RenderingTerrainModel rtm, float sFactor, int width){
		this.rtm = rtm;
		this.sFactor = sFactor;
		this.width = width;
	}
	
	public int getWidth(){
		//System.out.print("\n\n\nWidth " + width + "\n\n\n");
		return width;
	}

	public float getHeight(float x, float y){
		return rtm.getRHeight(x, y) / sFactor;
	}

	public float[] getNormal(float x, float y){
		float[] ret =  {0f, 1f, 0f};
		ret[1] = rtm.getRNormal(x, y);
		return ret;
	}
}
