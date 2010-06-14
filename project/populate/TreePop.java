package project.populate;

import project.*;
import java.util.Random;
import java.util.Vector;

public class TreePop{
	protected Terrain terrain;
	protected float[][][] normals;
	protected Vector trees;
	protected Random r = new Random(18l);


	final float offset = 0;
	final float hSize = 0.25f;

	protected TreePop(Terrain terrain){
		this.terrain = terrain;
		//genNormals();
		popTrees();
		r.nextFloat();
		r.nextFloat();
		r.nextFloat();
	}
		

	/*protected float[] normal(float[] v1, float[] v2){
		float[] retVec = new float[3];
		retVec[0] = v1[1] * v2[2] - v1[2] * v2[1]; 
		retVec[1] = v1[2] * v2[0] - v1[0] * v2[2]; 
		retVec[2] = v1[0] * v2[1] - v1[1] * v2[0];
		return retVec;
	}

	protected float[] normalize(float x, float y, float z){
		float[] rV = new float[3]; 
		float h;
		h = x * x + y * y + z * z;
		h = (float)Math.sqrt(h);
		rV[0] = x / h;
		rV[1] = y / h;
		rV[2] = z / h;
		return rV;
	}

	protected void genNormals(){
		
		int size = terrain.getWidth();
		//int size = 10;
		float scale = 1 / size;

		normals = new float[size][size][3];
		
		float[] v1 = new float[3];
		float[] v2 = new float[3];
		float[] norm = new float[3];
		//System.out.println("starting to calulate unscaled normals.");
		for(int y = 0; y + 1 < size; y++){
			//System.out.println("y = " + y);
			for(int x = 0; x + 1 < size; x++){
				v1[0] = 0;
				v1[1] = terrain.getHeight(x * scale, (y + 1) * scale)
					- terrain.getHeight(x * scale, y * scale);
				v1[2] = scale;

				v2[0] = scale;
				v2[1] = terrain.getHeight((x + 1) * scale, y * scale)
					- terrain.getHeight(x * scale, y * scale);
				v2[2] = 0;

				norm = normal(v1, v2);
				norm = normalize(norm[0], norm[1], norm[2]);

				normals[x][y][0] += norm[0];
				normals[x][y][1] += norm[1];
				normals[x][y][2] += norm[2];
				normals[x][y + 1][0] += norm[0];
				normals[x][y + 1][1] += norm[1];
				normals[x][y + 1][2] += norm[2];
				normals[x + 1][y][0] += norm[0];
				normals[x + 1][y][1] += norm[1];
				normals[x + 1][y][2] += norm[2];
				
				v1[0] = 0;
				v1[1] = terrain.getHeight((x + 1) * scale, (y + 1) * scale)
					- terrain.getHeight((x + 1) * scale, y * scale);
				v1[2] = scale;
				
				v2[0] = scale;
				v2[1] = terrain.getHeight((x + 1) * scale, (y + 1) * scale)
					- terrain.getHeight(x * scale, (y + 1) * scale);
				v2[2] = 0;

				norm = normal(v1, v2);
				norm = normalize(norm[0], norm[1], norm[2]);

				normals[x][y + 1][0] += norm[0];
				normals[x][y + 1][1] += norm[1];
				normals[x][y + 1][2] += norm[2];
				normals[x + 1][y][0] += norm[0];
				normals[x + 1][y][1] += norm[1];
				normals[x + 1][y][2] += norm[2];
				normals[x + 1][y + 1][0] += norm[0];
				normals[x + 1][y + 1][1] += norm[1];
				normals[x + 1][y + 1][2] += norm[2];
			}
		}
		//System.out.println("unscaled normals, ok!");
		float[] nized;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				//System.out.println("i = " + i + ", j = " + j + ", size = " + size);
				nized = normalize(normals[i][j][0],
						normals[i][j][1],
						normals[i][j][2]);
				normals[i][j][0] = nized[0];
				normals[i][j][1] = nized[1];
				normals[i][j][2] = nized[2];
			}
		}
	}*/

	protected boolean eval(float height, float normal){
		float threshhold = 0.8f;
		float ranF = r.nextFloat();
		//ranF = ranF * 1f + 0f;
		float heightV = ((height - offset) / (hSize - offset));
		if(height < offset)
			heightV = 0;
		//heightV *= heightV;
		heightV = 1 - heightV;
		float evalV = 0.4f * normal + 0.2f * heightV + 0.05f + 0.35f * ranF;  
		//System.out.println("Normal = " + normal + ", Random = " + ranF);
		return (evalV > threshhold);
	}
	
	public void popTrees(){
		int size = terrain.getWidth();
		//int size = 10;
		float scale = 1f / size;
		trees = new Vector();
		float xD;
		float yD;
		
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				xD = r.nextFloat() / 2;
				yD = r.nextFloat() / 2;
				float height = terrain.getHeight((i + xD) * scale, (j + yD) * scale);
				float[] norm = terrain.getNormal((i + xD) * scale, (j + yD) * scale);
				//System.out.println("height " + height);
				if(eval(height, norm[1])){
					float[] element = new float[5];
					element[0] = (i + xD) * scale;
					element[1] = height;
					element[2] = (j + yD) * scale;
					element[3] = r.nextFloat();
					element[4] = 0.8f + 0.4f * r.nextFloat();
					//System.out.println("TreePop " + element[1]);
				   	trees.add(element);
				}
			}
		}
	}

	public static Vector getTrees(Terrain terrain){
		TreePop tp = new TreePop(terrain);
		return	tp.trees;
	}
}
