package project.renderer;

import project.noise.fBm.*;
import project.noise.multiFractal.*;
import project.noise.noiseUtil.*;
import project.noise.perlinGenerator.*;
import project.noise.voronoi.*;

public class TMFactory{

	long seed;
	PFactory pf;
	PFactory pf2;
	TexTable tt;
	final int size = 150;
	int mode = 1;
	final int defaultCacheSize = 20;

	protected RenderingTerrainModel[] data;
	protected int[] free;
	protected int[] used; //shows most recent used

	TMFactory(long seed, TexTable tt){
		this.seed = seed;
		pf = new PFactory(seed);
		pf2 = new PFactory(2 * seed);
		this.tt = tt;

		int cacheSize = defaultCacheSize;
		free = new int[cacheSize];
		used = new int[cacheSize];
		data = new RenderingTerrainModel[cacheSize];

		for(int i = 0; i < cacheSize; i++){
			free[i] = 0;
			used[i] = 0;
		}
	}

	void setSeed(long l){
		seed = l;
		pf = new PFactory(seed);
		pf2 = new PFactory(2 * seed);
	}

	int getCellSize(){
		return (size - 1) * RenderingTerrainModel.scale;
	}
	
	void setMode(int i){
		if(i != mode){
			mode = i;
			data = new RenderingTerrainModel[defaultCacheSize];
			for(int j = 0; i < data.length; i++){
				free[i] = 0;
				used[i] = 0;
			}
		}
	}
	
	protected RenderingTerrainModel genTerrain(int x, int y){
		switch(mode){
			case 0: return genTerrain0(x, y);
			case 1: return genTerrain1(x, y);
			case 2: return genTerrain2(x, y);
			case 3: return genTerrain3(x, y);
			case 4: return genTerrain4(x, y);
			case 5: return genTerrain5(x, y);
			case 6: return genTerrain6(x, y);
			case 9: return genTerrain9(x, y);
			default: return genTerrain1(x, y);
		}
	}
	
	protected RenderingTerrainModel genTerrain1(int x, int y){
		
			PNoise pn = pf.getPNoise(5, x, y);
			
			NScale finalN = new NScale((float) size / 256, 0f, pn);

			
			return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
	}
	
	protected RenderingTerrainModel genTerrain2(int x, int y){
		

			PNoise pn = pf.getPNoise(4, x, y);
			
			FBm fBm = new FBm(pn, 10, 0.5f, 2);
			NScale finalN = new NScale((float) size / 256, 0f, fBm);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
			
			return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
		//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}
	
	protected RenderingTerrainModel genTerrain3(int x, int y){

			PNoise pn = pf.getPNoise(5, x, y);

			HybMFrac hMF = new HybMFrac(0.1f, 2f, 10, 1.6f, 0.6f, pn, 1);
			NScale finalN = new NScale((float) size / 256, 0f, hMF);
			
			return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);

	}
	
	protected RenderingTerrainModel genTerrain4(int x, int y){//voronoi4
		
		
		float[] coefs = {-6, 4, -1, 0.5f};
		Voronoi v = new Voronoi(2, coefs, seed, x, y);
		v.makeGenable();
		NScale finalN = new NScale((float) size / 256, 0f, v);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
		
		return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
	//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}

	protected RenderingTerrainModel genTerrain5(int x, int y){//voronoi4
		
		
		float[] coefs = {-6, 4, -1, 0.5f};
		ModVoronoi v = new ModVoronoi(2, coefs, seed, x, y);
		v.makeGenable();
		NScale finalN = new NScale((float) size / 256, 0f, v);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
		
		return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
	//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}
	
	protected RenderingTerrainModel genTerrain6(int x, int y){//voronoi4
		
		PNoise pn1 = pf2.getPNoise(6, x, y);
		PNoise pn2 = pf2.getPNoise(6, x + 3, y + 2);
		
		float[] coefs = {-6, 4, -1, 0.5f};
		ModVoronoi v = new ModVoronoi(2, coefs, seed, x, y);
		v.makeGenable();
		PertubanceFilter pFilter = new PertubanceFilter(0.06f, v, pn1, pn2);
		NScale finalN = new NScale((float) size / 256, 0f, pFilter);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
//		NScale finalN = new NScale((float) size / 256, 0f, multi);
		
		return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
	//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}
	
	protected RenderingTerrainModel genTerrain9(int x, int y){
		
		//	PNoise pn = pf.getPNoise(12, x, y);
			PNoise pn = pf.getPNoise(5, x, y);
			PNoise pn1 = pf2.getPNoise(6, x, y);
			PNoise pn2 = pf2.getPNoise(6, x + 3, y + 2);
			
			float[] coefs = {-6, 4, -1, 0.5f};
			NormVoronoi v = new NormVoronoi(2, coefs, seed, x, y);
			//ModVoronoi v = new ModVoronoi(2, coefs, seed, x, y);
			v.makeGenable();
			//System.out.println("genable done");
			NScale scaledV = new NScale(0.7f, 0f, v);
//			PertubanceFilter pFilter = new PertubanceFilter(0.06f, scaledV, pn1, pn2);
			HybMFrac hMF = new HybMFrac(1f, 2f, 10, 1.0f, 0.6f, pn, scaledV);
//			HybMFrac hMF = new HybMFrac(0.1f, 2f, 20, 1.6f, 0.6f, pn, 1);
//			MFrac multi = new MFrac(pn, 4, 0.5f, 2, 0.3f);
//			FBm fBm = new FBm(pn, 10, 0.5f, 2);
			NScale finalN = new NScale((float) size / 256, 0f, hMF);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
			
			return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
		//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}
	
	protected RenderingTerrainModel genTerrain0(int x, int y){
		
		//	PNoise pn = pf.getPNoise(12, x, y);
			PNoise pn = pf.getPNoise(5, x, y);
			PNoise pn1 = pf2.getPNoise(6, x, y);
			PNoise pn2 = pf2.getPNoise(6, x + 3, y + 2);
			
			float[] coefs = {-6, 4, -1, 0.5f};
			NormVoronoi v = new NormVoronoi(2, coefs, seed, x, y);
			//ModVoronoi v = new ModVoronoi(2, coefs, seed, x, y);
			v.makeGenable();
			//System.out.println("genable done");
			NScale scaledV = new NScale(0.7f, 0f, v);
			PertubanceFilter pFilter = new PertubanceFilter(0.06f, scaledV, pn1, pn2);
			HybMFrac hMF = new HybMFrac(1f, 2f, 10, 1.0f, 0.6f, pn, pFilter);
//			HybMFrac hMF = new HybMFrac(0.1f, 2f, 20, 1.6f, 0.6f, pn, 1);
//			MFrac multi = new MFrac(pn, 4, 0.5f, 2, 0.3f);
//			FBm fBm = new FBm(pn, 10, 0.5f, 2);
			NScale finalN = new NScale((float) size / 256, 0f, hMF);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
//			NScale finalN = new NScale((float) size / 256, 0f, multi);
			
			return new RenderingTerrainModel(new NTerrain(size, finalN, 500), tt, x, y);
		//	return new RenderingTerrainModel(new NTerrain(size, pFilter, 150), tt, x, y);
	}
	
	protected int getFree(){
		for(int i = 0; i < free.length; i++){
			if(free[i] == 0){
				int rVal = i;
//				return i;
				for(int j = i + 1; j < free.length; j++){
					if(free[j] == 0 && used[j] < used[rVal])rVal = j;
				}
				if(data[rVal] != null){
					data[rVal].free();
				}
				return rVal;
			}
		}
		return incCapacity();
	}

	protected int incCapacity(){
		
		int retV = free.length;
		free = resizeI(free, free.length * 2);
		for(int i = retV; i < free.length; i++){
			free[i] = 0;
		}
		used = resizeI(used, used.length * 2);
		for(int i = retV; i < used.length; i++){
			used[i] = 0;
		}
		data = resizeTE(data, data.length * 2);
		return retV;
	}

	protected int[] resizeI(int[] source, int capacity){
		int[] retV = new int[capacity];
		int n = (source.length < capacity) ? source.length: capacity;
		for(int i = 0; i < n; i++){
			retV[i] = source[i];
		}
		return retV;
	}

	protected RenderingTerrainModel[] resizeTE(RenderingTerrainModel[] source, int capacity){
		RenderingTerrainModel[] retV = new RenderingTerrainModel[capacity];
		int n = (source.length < capacity) ? source.length: capacity;
		for(int i = 0; i < n; i++){
			retV[i] = source[i];
		}
		return retV;
	}

	protected int getCord(int x, int y){
		for(int i = 0; i < data.length; i++){
			if(!(data[i] == null) && data[i].gloX == x && data[i].gloY == y){
				return i;
			}
		}
		return -1;
	}

	public RenderingTerrainModel getTerrain(int x, int y){
		int rVal = getCord(x, y);
		if(rVal < 0){
			rVal = getFree();
			data[rVal] = genTerrain(x, y);
			//System.out.println("binding " + tex.getId() + " to " + rVal);
		}
		free[rVal]++;
		if(used[rVal] != used.length){
			for(int i = 0; i < used.length; i++)used[i]--;
			used[rVal] = used.length;
		}
		return data[rVal];
	}

	public void free(int x, int y){
		int index = getCord(x, y);
		if(index != -1 && free[index] != 0) free[index] -= 1;
	}

	public void report(){
		for(int i = 0; i < data.length; i++){
			System.out.println("" + data[i]);
			System.out.println(
					"\n" +
					"Content of " + i);
			if(data[i] != null){
					System.out.println("Global x, y " + data[i].gloX + ", " + data[i].gloY);
			}
			System.out.println(
					"Free = " + free[i] + "\n" +
					"Used = " + used[i]);
		}
	}
}

