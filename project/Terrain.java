//Terrain appears as a data structure which contain the terrain's data
//for the purpose of prototyping it is implemented through an interface so
//test code can simulate the back end code and be more compact.

package project;

public interface Terrain{

	public int getWidth(); //terrain cells must be square so only width needed

	public float getHeight(float x, float y);

	public float[]getNormal(float x, float y);
}
