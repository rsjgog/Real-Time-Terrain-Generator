package project.renderer;

public class LowAlphaFilter implements AlphaFilter{
	float lowerA, lowerB;
	boolean noLow;
	
	public LowAlphaFilter(float lowerA, float lowerB){
		this.lowerA = lowerA;
		this.lowerB = lowerB;
		this.noLow = false;
	}

	public LowAlphaFilter(){
		this.noLow = true;
		this.lowerA = 0;
		this.lowerB = 0;
	}


	public float getAlpha(float height, int x, int y){
	//	System.out.print("alpha filter" + height + ", "); 
		if(height > lowerB || noLow){
	//		System.out.println("1");
			return 1f;
		}
		if(height < lowerB && height > lowerA){
	//		System.out.println("" + ((height - lowerA)/(lowerB - lowerA)));
			return (height - lowerA)/(lowerB - lowerA);
		}
	//	System.out.println("0");
		return 0;
	}
}
