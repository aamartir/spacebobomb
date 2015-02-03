package spaceGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * A objectsInQuadrant is a segment, or SubArea of Grid. This will be useful
 * for Collision Detection algorithms since it will reduce the number
 * of comparisons in order to find colliding objects
 */
public class Quadrant 
{
	private Grid parentGrid;
	public HashMap<Integer, SpaceObject> objectsInQuadrant;
	public int objCounter = 0;
	public int quadrantId; // Block Identifier
	
	// Dimensions
	private double x0;
	private double y0;
	private double w;
	private double h;
	
	public Quadrant( Grid parentGrid, int id, double x0, double y0, double w, double h )
	{
		this.parentGrid = parentGrid;
		
		this.x0 = x0;
		this.y0 = y0;
		this.w = w;
		this.h = h;
		
		objectsInQuadrant = new HashMap<Integer, SpaceObject>();
		quadrantId = id;
		
		//System.out.println("New Quadrant\nx = " + x0 + "\ny = " + y0 + "\nhash = " + quadrantId);
	}
	
	public int getQuadrantID()
	{
		return quadrantId;
	}
	
	public SpaceObject[] getObjectsInQuadrant()
	{
		return (SpaceObject[]) objectsInQuadrant.entrySet().toArray();
	}

	public int getNumOfObjectsInQuadrant()
	{
		return objectsInQuadrant.entrySet().size();
	}
	
	public boolean hasSpaceObject( SpaceObject obj )
	{
		// Object ID is the KEY in <KEY, VALUE> HashMap entry set
		if( objectsInQuadrant.containsKey( obj.getObjectID()) )
			return true;
		
		return false;
	}
	
	public synchronized boolean addObjectToQuadrant( SpaceObject obj )
	{
		if( !this.hasSpaceObject(obj) ) // Only add if different
		{
			objectsInQuadrant.put( obj.getObjectID(), obj );
			//obj.assignQuadrant(this);
			
			System.out.println( "New object( obj" + obj.getObjectID() + 
					            " ) at position ( " + obj.getPosX() + ", " + obj.getPosY() + 
					            " ) added to quadrant " + quadrantId );
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean removeObjectFromQuadrant( SpaceObject obj )
	{
		if( this.hasSpaceObject(obj) )
		{
			//System.out.println("Removing object (hash " + obj.getGridID() + " from Quadrant " + getQuadrantHash());
			objectsInQuadrant.remove( obj.getObjectID() );
			return true;
		}

		return false;
	}
	
	public synchronized boolean swapObjectToQuadrant( Quadrant toQuadrant, SpaceObject obj )
	{
		if( removeObjectFromQuadrant(obj) )
			if( toQuadrant.addObjectToQuadrant(obj) )
					return true;
		
		return false;
	}
	
	public void draw( Graphics2D g2d )
	{
		g2d.setColor( Color.GRAY );
		g2d.draw( new Rectangle2D.Double( x0, y0, w, h) );
		//g2d.drawString("Hash: " + getQuadrantHash(), x0 + );
		//g2d.drawString("Objs: " + getNumOfObjectsInQuadrant(), x + Board.WIDTH/(10*Grid.xDiv), y + Board.HEIGHT/(10*Grid.yDiv) + 20);
		
	}
}
