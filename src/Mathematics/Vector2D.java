package Mathematics;

public class Vector2D 
{
	public static double getMagnitude( double x1, double x2 )
	{
		return Math.sqrt( x1*x1 + x2*x2 );
	}
	
	public static double getDistanceBetween2Points( double x1, double y1, double x2, double y2 )
	{
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
}
