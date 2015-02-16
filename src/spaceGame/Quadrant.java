package spaceGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
 * A objectsInQuadrant is a segment, or SubArea of Grid. This will be useful
 * for Collision Detection algorithms since it will reduce the number
 * of comparisons in order to find colliding objects
 */
public class Quadrant 
{
	private Grid parentGrid;
	public ConcurrentHashMap<Integer, SpaceObject> objectsInQuadrant;
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
		
		objectsInQuadrant = new ConcurrentHashMap<Integer, SpaceObject>();
		quadrantId = id;
		
		//System.out.println("New Quadrant\nx = " + x0 + "\ny = " + y0 + "\nhash = " + quadrantId);
	}
	
	public int getQuadrantID()
	{
		return quadrantId;
	}
	
	public synchronized Object[] getObjectsInQuadrant()
	{
		return objectsInQuadrant.values().toArray();
	}

	public synchronized int getNumOfObjectsInQuadrant()
	{
		return objectsInQuadrant.size(); //.entrySet().size();
	}
	
	public synchronized boolean hasSpaceObject( SpaceObject obj )
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
			obj.lastQuadrant = this;
			objCounter++;

			//System.out.println( "New object( obj" + obj.getObjectID() + 
			//		            " ) at position ( " + obj.getPosX() + ", " + obj.getPosY() + 
			//		            " ) added to quadrant " + quadrantId );
			return true;
		}
		
		return false;
	}
	
	public synchronized boolean removeObjectFromQuadrant( SpaceObject obj )
	{
		if( this.hasSpaceObject(obj) )
		{
			//System.out.println("Removing object (obj" + obj.getObjectID() + ") from Quadrant " + getQuadrantID());

			objectsInQuadrant.remove( obj.getObjectID() );
			obj.lastQuadrant = null;
			objCounter--;
			
			return true;
		}

		return false;
	}
	
	public synchronized boolean swapObjectToQuadrant( Quadrant toQuadrant, SpaceObject obj )
	{
		if( removeObjectFromQuadrant(obj) )
		{
			obj.lastQuadrant = toQuadrant;

			if( toQuadrant != null )
			    toQuadrant.addObjectToQuadrant(obj);
			
			return true;
		}
		
		return false;
	}
	
	public synchronized void removeAllObjectsFromQuadrant()
	{
		if( objectsInQuadrant.size() > 0 )
			objectsInQuadrant.clear();
	}
	
	public void draw( Graphics2D g2d )
	{
		g2d.setColor( Color.GRAY );
		g2d.draw( new Rectangle2D.Double( x0, y0, w, h) );
		g2d.drawString("(" + getQuadrantID() + ")", (float)(x0 + 10.0f), (float)(y0 + 0.1*h) );
		g2d.drawString("Objs (" + getNumOfObjectsInQuadrant() + ")", 
				       (float)(x0 + w - 60), (float)(y0 + 0.1*h));
		
	}
}
