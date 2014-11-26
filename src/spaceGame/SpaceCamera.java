package spaceGame;

import java.awt.Graphics;

public class SpaceCamera 
{
	private int viewportWidth;
	private int viewportHeight;
	
	private double posX;
	private double posY;
	
	private SpaceObject targetToFollow;
	
	public SpaceCamera( double x, double y, int dx, int dy )
	{
		posX = x;
		posY = y;
		
		viewportWidth = dx;
		viewportHeight = dy;
		
		targetToFollow = null;
	}
	
	public SpaceCamera( double x, double y, int dx, int dy, SpaceObject target )
	{
		this( x, y, dx, dy );
		targetToFollow = target;
	}
	
	public double getPosX()
	{
		return posX;
	}
	
	public double getPosY()
	{
		return posY;
	}
	
	public void setPosX( int newX )
	{
		posX = newX;
	}
	
	public void setPosY( int newY )
	{
		posY = newY;
	}
	
	public void updatePosition()
	{
		if( targetToFollow != null )
		{
			posX = targetToFollow.getPosX();
			posY = targetToFollow.getPosY();
		}
	}
	
	public int getViewportWidth()
	{
		return viewportWidth;
	}
	
	public int getViewportHeight()
	{
		return viewportHeight;
	}
	
	public void setViewportWidth( int w )
	{
		viewportWidth = w;
	}
	
	public void setViewportHeight( int h )
	{
		viewportHeight = h;
	}
	
	public void followSpaceObject( SpaceObject obj )
	{
		targetToFollow = obj;
	}
	
	public SpaceObject getTargetSpaceObject()
	{
		return targetToFollow;
	}
	
	public void drawPerspectiveBox( Graphics g )
	{
		
	}
	
	public void drawPerspective( Graphics g )
	{
		
	}
}
