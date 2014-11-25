package spaceGame;

import java.awt.Graphics;

public class SpaceCamera 
{
	private int viewportWidth;
	private int viewportHeight;
	
	private int posX;
	private int posY;
	
	private SpaceObject targetToFollow;
	
	public SpaceCamera( int x, int y, int dx, int dy )
	{
		posX = x;
		posY = y;
		
		viewportWidth = dx;
		viewportHeight = dy;
		
		targetToFollow = null;
	}
	
	public SpaceCamera( int x, int y, int dx, int dy, SpaceObject target )
	{
		this( x, y, dx, dy );
		targetToFollow = target;
	}
	
	public int getPosX()
	{
		return posX;
	}
	
	public int getPoxY()
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
