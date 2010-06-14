package project.renderer;

import java.nio.*;
import javax.media.opengl.*;



public class AlphaGenerator{
	RenderingTerrainModel terrain;
	int width;// = terrain.getWidth();
	AlphaFilter[] filters;
	TextureInfo[] textures;
	int[][] indexPairs;
	TexTable tt;

	public AlphaGenerator(RenderingTerrainModel terrain, LowAlphaFilter[] filters, TextureInfo[] textures){
		this.terrain = terrain;
		this.filters = filters;
		this.textures = textures;
		this.width = terrain.getWidth();
		this.tt = terrain.tt;
		popPairs();
	}

	protected ByteBuffer genAlpha(LowAlphaFilter filter){
		ByteBuffer buffer = ByteBuffer.allocateDirect(width * width * 4);
		float f;
		int intVal;
		int width = terrain.getWidth();
		for(int i = 0; i < terrain.vertices.length; i += 3){
			f = filter.getAlpha(terrain.vertices[i + 1], (int) i / (width * 3),(int) (i/3) % width)  * 255;
			//System.out.println("putting alpha " + (int) f);
			intVal = (int) f;
			buffer.put((byte) 200);
			buffer.put((byte) 200);
			buffer.put((byte) 200);
			buffer.put((byte) intVal);
		}
		buffer.rewind();
		return buffer;
	}
	
	protected void popPairs(){
		
		//int texIn, alphaIn
		indexPairs = new int[filters.length][1];
		for(int i = 0; i < filters.length; i++){
			//if(tt == null) System.out.println("TexTable null in Alpha");
		indexPairs[i][0] = tt.getIndex(textures[i]);
		//indexPairs[i][1] = tt.getIndex("ALP_" + terrain.gloX + "_" + terrain.gloY + "_" + i,
//										genAlpha(filters[i]),
//										width, width, GL.GL_RGBA, 4);
		}
	}

	TextureInfo texI1 = new TextureInfo("DEF_ROCK", "rock.bmp");
	TextureInfo texI2 = new TextureInfo("TEST_GRASS", "grass.bmp");

	LowAlphaFilter fil1 = new LowAlphaFilter();
	LowAlphaFilter fil2 = new LowAlphaFilter(0.7f, 0.9f);

	TextureInfo[] sampleTs = {texI1, texI2};
	AlphaFilter[] sampleAs = {fil1, fil2};


	public AlphaGenerator(RenderingTerrainModel terrain){
		this.terrain = terrain;
		this.filters = sampleAs;
		this.textures = sampleTs;
		this.width = terrain.getWidth();
		this.tt = terrain.tt;
		popPairs();
	}

	public void free(){
		for(int i = 0; i < sampleTs.length; i++)
			tt.freeTex(sampleTs[i].getId());
		// TODO Auto-generated method stub
		
	}
}
