package spaceGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class SpaceObjectSelector 
{
	public static final double SELECTOR_SIZE = 20.0;
	
	private CollisionBoundary collisionBoundary;
	private boolean visible;
	
	public SpaceObjectSelector()
	{
		collisionBoundary = new CollisionBoundary( 0, 0, SELECTOR_SIZE, SELECTOR_SIZE );
		visible = false;
	}
	
	public void setCoordinates( double newX, double newY )
	{
		collisionBoundary.setPositionAndDimensions( newX, newY, SELECTOR_SIZE, SELECTOR_SIZE );
	}
	
	public void setVisible( boolean val )
	{
		visible = val;
	}
	
	public boolean isVisible()
	{
		return visible;
	}
	
	public boolean isSelecting( SpaceObject spaceObject )
	{
		return collisionBoundary.intersectsWith(spaceObject.getCollisionBoundary() );
	}
	
	public void drawSelector( Graphics2D g2d )
	{
		collisionBoundary.drawCollisionBoundary( g2d );
	}
}
