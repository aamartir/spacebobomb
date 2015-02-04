package spaceGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Grid 
{
	private ArrayList<Quadrant> grid;

	private int screenWidth;
	private int screenHeight;
	
	private int n;
	private double dx;
	private double dy;
	private int size;

	public Grid( int n, int screenWidth, int screenHeight )
	{
		grid = new ArrayList<Quadrant>();
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.n = n;
		this.size = n*n;
		this.dx = screenWidth/n;
		this.dy = screenHeight/n;
		
		initGrid();
	}
	
	public void initGrid()
	{
		int quadCounter = 0;
		
		for( int row = 0; row < n; row++ )
		{
			for( int col = 0; col < n; col++ )
			{
				grid.add( new Quadrant(this, quadCounter++, dx*col, dy*row, dx, dy) );
			}
		}
	}
	
	public int size()
	{
		return size;
	}
	
	public Quadrant get( int i )
	{
		if( size > 0 && i >= 0 && i < size )
			return grid.get(i);
		
		return null;
	}
	
	public synchronized int getObjectsInGrid()
	{
		int count = 0;
		for( Quadrant q : grid )
			count += q.getNumOfObjectsInQuadrant();
		
		return count;
	}
	
	/*
	public synchronized void refresh()
	{
		
	}
	*/
	
	public synchronized void updateObjectGridQuadrant( SpaceObject obj )
	{
		if( obj != null )
		{
			Quadrant q = getQuadrantPerPoint( obj.getPosX(), obj.getPosY() );
			
			// Swap to different quadrant
			swapObjectToQuadrant( obj.lastQuadrant, q, obj );
		}
	}
	
	public synchronized static void swapObjectToQuadrant( Quadrant fromQuadrant, Quadrant toQuadrant, SpaceObject obj )
	{
		if( fromQuadrant != null )
		{
			// Object is somewhere in the grid
			fromQuadrant.swapObjectToQuadrant( toQuadrant, obj );
		}
		else
		{
			// Object is coming into the grid
			addObjectToQuadrant( toQuadrant, obj );
		}
	}
	
	public synchronized static void addObjectToQuadrant( Quadrant toQuadrant, SpaceObject obj )
	{
		if( toQuadrant == null )
			obj.lastQuadrant = null;
		else
			toQuadrant.addObjectToQuadrant( obj );
	}
	
	public synchronized void putObjectInGrid( SpaceObject obj )
	{
		if( obj != null )
		{
			//System.out.println( "x: " + obj.getPosX() + ". y: " + obj.getPosY() + ". id: " + getQuadrantIDPerPoint(obj.getPosX(), obj.getPosY()) );
			
			// Add object to the right quad (Take the center of the object as the point of reference)
			int quadID = getQuadrantIDPerPoint( obj.getPosX(), 
					                            obj.getPosY() );
			
			// Try to put object into its respective quadrant
			putObjectInQuadrant( quadID, obj );
		}
	}
	
	// TODO
	public synchronized void removeObjectFromGrid( SpaceObject obj )
	{
		if( obj != null )
		{
			// REMOVE obj from every quadrant it is on
			// TODO
		}
	}
	
	public synchronized void putObjectInQuadrant( int quadrantID, SpaceObject obj )
	{
		if( grid != null && obj != null &&
			quadrantID > 0 && quadrantID < grid.size() )
		{
			grid.get( quadrantID ).addObjectToQuadrant( obj );
		}
	}
	
	// Return -1 if outside of bounds of grid
	public int getQuadrantIDPerPoint( double x, double y )
	{
		if( x < 0 || x > screenWidth ||
		    y < 0 || y > screenHeight )
		{
			return -1;
		}
		
		return ( (int)(x/dx) + n*(int)(y/dy) );
	}
	
	public Quadrant getQuadrantPerPoint( double x, double y )
	{
		int id = getQuadrantIDPerPoint( x, y );
		
		if( id >= 0 && id < grid.size() )
			return grid.get(id);
		
		return null;
	}

	public void drawGrid( Graphics g )
	{
		for( Quadrant quad : grid )
			quad.draw( (Graphics2D) g );
	}
}
