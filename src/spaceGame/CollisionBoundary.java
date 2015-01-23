package spaceGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

// CollisionBoundary is a rectangle
public class CollisionBoundary 
{
	private double x; // left margin
	private double y;  // top margin
	private double width;
	private double height;
	
	public CollisionBoundary( double x, double y, double w, double h )
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
	
	public void setPositionAndDimensions( double newX, double newY, double newW, double newH )
	{
		x = newX;
		y = newY;
		width = newW;
		height = newH;
	}
	
	public void drawCollisionBoundary( Graphics2D g2d )
	{
		g2d.setColor( Color.YELLOW );
		g2d.draw( new Rectangle2D.Double(x, y, width, height) );
	}
	
	public boolean intersectsWith( CollisionBoundary other )
	{
		if( (this.x + this.width >= other.x && this.x + this.width <= other.x + other.width ||
			 this.x >= other.x && this.x <= other.x + other.width ) &&
	        (this.y + this.height >= other.y && this.y + this.height <= other.y + other.height ||
	         this.y >= other.y && this.y <= other.y + other.height) )
		{
			return true;
		}
		
		return false;
	}
}
