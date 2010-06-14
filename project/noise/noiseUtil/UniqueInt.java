package project.noise.noiseUtil;
import java.lang.Math;


public class UniqueInt{
	public static long getLong (long x, long y){
		long ring = (Math.abs(x) < Math.abs(y)) ? Math.abs(y) : Math.abs(x);
		long rVal;
		long local = 0l;
		if (ring == 0l){
			return 0l;
		}
		if (y == ring){
			local = x + ring;
		}//counted 2ring + 1
		if (y == - ring){
			local = x + 3l * ring + 1l;
		}//counted 4ring + 2
		if (x == ring && Math.abs(y) != ring){
			local = y + 5l * ring + 1l;
		}//counted 6ring + 1
		if (x == -ring && Math.abs(y) != ring){
			local = y + 7l * ring;
		}
		rVal = local + (long) Math.pow(2l * ring - 1l, 2);
		return rVal;
	}
}
		
