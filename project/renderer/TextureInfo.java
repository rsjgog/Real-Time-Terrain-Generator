package project.renderer;

//used to package data read from a text file and passed to the TexTable
public class TextureInfo{
	protected  String id, fileName;

	public TextureInfo(String id, String fileName){
		this.id = id;
		this.fileName = fileName;
	}

	public String getFileName(){
		return fileName;
	}

	public String getId(){
		return id;
	}

}
