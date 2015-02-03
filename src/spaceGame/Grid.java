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

	public Grid( int n, int screenWidth, int screenHeight )
	{
		grid = new ArrayList<Quadrant>();
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.n = n;
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
	
	public void putSpaceObject( SpaceObject obj )
	{
		if( obj != null )
		{
			//System.out.println( "x: " + obj.getPosX() + ". y: " + obj.getPosY() + ". id: " + getQuadrantIDPerPoint(obj.getPosX(), obj.getPosY()) );
			
			// Add object to the right quad (Take the center of the object as the point of reference)
			int quadID = getQuadrantIDPerPoint( obj.getPosX() + obj.getImgWidth()/2.0, 
					                            obj.getPosY() + obj.getImgHeight()/2.0 );
			
			if( quadID >= 0 && quadID < grid.size() )
			{
				grid.get( quadID ).addObjectToQuadrant( obj ); 
			}
			else
			{
				
			}
		}
	}
	
	public void removeSpaceObject( SpaceObject obj )
	{
		if( obj != null )
		{
			// TODO 
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
	
	/*
	public static void assignObjectToQuadrant(int x, int y, SpaceObject obj)
	{
		int hash = calculateQuadrantHash(x, y, xDiv, yDiv);
		
		if(!Board.isWithinBounds(obj) || hash >= xDiv*yDiv || hash < 0)
		{
			if(obj.getQuadrant() != null)
				obj.getQuadrant().removeObjectFromQuadrant(obj);
		}
		else
		{			
			if(obj.getQuadrant() != null)
			{
				if(hash != obj.getQuadrant().getQuadrantHash()) // Re-assign only if the new Quadrant isb  different than the old one
				{
					obj.getQuadrant().removeObjectFromQuadrant(obj);

					thisQuad = grid.get(hash); // Get the new Quadrant
					thisQuad.addObjectToQuadrant(obj); // Relate the object with its Quadrant (2-way, see function)
				}
			}
			else
			{
				thisQuad = grid.get(hash);
				thisQuad.addObjectToQuadrant(obj);
			}
		}
		
		//return thisQuad;
	}
	*/
	
	/*
	public static int calculateQuadrantHash(int x, int y, int xDiv, int yDiv)
	{
		return (int) (Math.floor((x+1)*xDiv/Board.WIDTH) + Math.floor((y+1)*yDiv/Board.HEIGHT)*yDiv);
	}
	*/
	
	public void drawGrid( Graphics g )
	{
		for( Quadrant quad : grid )
			quad.draw( (Graphics2D) g );
	}
}
