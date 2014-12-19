package Mathematics;

public class MyMath 
{
	// Function does a linear mapping from (xmin, xmax) to (ymin, ymax) and returns the 
	// interpolated value.
	public static double map( double x, double xmin, double xmax, double ymin, double ymax )
	{
		return (ymax-ymin)/(xmax-xmin) * (x - xmin) + ymin;
	}
	
	// This function assumes xmin = ymin = 0
	public static double map( double x, double xmax, double ymax )
	{
		return (ymax/xmax) * x;
	}
	
	// This function assumes xmin = ymin = 0, and ymax = 1;
	// Essentially, it will scale a value from a (0, to xmax) range to (0, 1) range
	public static double map( double x, double xmax )
	{
		return (x/xmax);
	}
}
