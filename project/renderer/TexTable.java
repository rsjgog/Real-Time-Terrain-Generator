package project.renderer;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.nio.*;

//manages textures. Uses Magic loading.
public class TexTable{
	protected String[] data;
	protected int[] free;
	int[] textures;
	GL gl;
	GLU glu;

	public TexTable(int capacity, GL gl){
		this.data = new String[capacity];
		this.free = new int[capacity];
		this.textures = new int[capacity];
		this.gl = gl;
		gl.glGenTextures(capacity, textures, 0);
		for(int i = 0; i < capacity; i++){
			free[i] = 0;
		}
		glu = new GLU();
	}
	
	public void report(){
		System.out.println("Texture Table Content");
		for(int i = 0; i < data.length; i++){
			System.out.println("name " + data[i] + ", used " + free[i]);
		}
		System.out.println();
	}

	protected int getId(String id){
		for(int i = 0; i < data.length; i++){
			if(!(data[i] == null) && data[i].equals(id)){
				return i;
			}
		}
		return -1;
	}

	protected int getFree(){
		for(int i = 0; i < free.length; i++){
			if(free[i] == 0){
				return i;
			}
		}
		return incCapacity();
	}

	public int getIndex(TextureInfo tex){
		int rVal = getId(tex.getId());
		if(rVal < 0){
			rVal = getFree();
			BufferedImage image = loadIm(new File(tex.getFileName()));
			ByteBuffer buffer = imageToBuffer(image);
			bindTexture(buffer, rVal, image.getWidth(), image.getHeight(), 4, GL.GL_RGBA);
			//System.out.println("binding " + tex.getId() + " to " + rVal);
		}
		free[rVal]++;
		data[rVal] = tex.getId();
		//repport();
		return rVal;
	}

	public int getIndex(String id, ByteBuffer buffer, int width, int height, int format, int components){
		int rVal = getId(id);
		if(rVal < 0){
			rVal = getFree();
			bindTexture(buffer, rVal, width, height, components, format);
		}
		free[rVal]++;
		data[rVal] = id;
		return rVal;
	}

	public void freeTex(String id){
		int index = getId(id);
		if(!(index < 0)) free[index]--;
	}


	protected int incCapacity(){
		
		int retV = textures.length;
		textures = resizeI(textures, textures.length * 2);
		gl.glGenTextures(retV, textures, retV);
		free = resizeI(free, free.length * 2);
		for(int i = retV; i < free.length; i++){
			free[i] = 0;
		}
		data = resizeS(data, data.length * 2);
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

	protected String[] resizeS(String[] source, int capacity){
		String[] retV = new String[capacity];
		int n = (source.length < capacity) ? source.length: capacity;
		for(int i = 0; i < n; i++){
			retV[i] = source[i];
		}
		return retV;
	}

	protected void bindTexture(ByteBuffer buffer, int index, int width, int height, int components, int format){
		

		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);
		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, components, width, height, 0, format, GL.GL_UNSIGNED_BYTE, buffer);

		glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, components, width, height, format, GL.GL_UNSIGNED_BYTE, buffer);
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);	
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
	}
	
	protected BufferedImage loadIm(File imFile){

		BufferedImage image = null;

		try{
			if(imFile != null){
				BufferedImage srcImage = ImageIO.read(imFile);
				if (srcImage != null) {
					image = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
					java.awt.Graphics2D gfx = image.createGraphics();
					gfx.setComposite(java.awt.AlphaComposite.Src);
					gfx.drawImage(srcImage, 0, 0, null);
					gfx.dispose();
					srcImage = null;
				}
			}
		}
		catch (IOException e){
			System.err.println("image load failed");
		}
		return image;	
		}
		
	protected ByteBuffer imageToBuffer(BufferedImage image){
		
		int width = image.getWidth();
		int height = image.getHeight();
		Raster raster = image.getRaster();
		int [] data = null;
		data = raster.getPixels(0, 0, width, height, data);
		ByteBuffer buffer =  ByteBuffer.allocateDirect(width * height * 4);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
			//	System.out.println("" + data[3 * (y * width + x) + 0]);
				buffer.put((byte) data[4 * (y * width + x) + 0]);
				buffer.put((byte) data[4 * (y * width + x) + 1]);
				buffer.put((byte) data[4 * (y * width + x) + 2]);
				buffer.put((byte) data[4 * (y * width + x) + 3]);
				//buffer.put((byte) data[3 * (y * width + x) + 3]);
			}
		}
		buffer.rewind();
		return buffer;
	}
}			
