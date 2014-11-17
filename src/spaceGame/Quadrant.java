package spaceGame;

import java.awt.Graphics2D;
import java.util.ArrayList;

/*
 * A objectsInQuadrant is a segment, or SubArea of Grid. This will be useful
 * for Collision Detection algorithms since it will reduce the number
 * of comparisons in order to find colliding objects
 */
public class Quadrant 
{
	// Dimensions
	private int x;
	private int y;
	
	// Keeps track of objects in this Quadrant (Faster object removal).
	private int objCounter = 0;

	ArrayList<SpaceObject> objectsInQuadrant; // Array of objects that belong to that Quadrant
	
	private int hashId; // Block Identifier
	
	public Quadrant(int x, int y, int xDiv, int yDiv)
	{
		this.x = x;
		this.y = y;
		//this.w = w;
		//this.h = h;
		
		objectsInQuadrant = new ArrayList<SpaceObject>();
		
		hashId = Grid.calculateQuadrantHash(x, y, xDiv, yDiv);
		
		System.out.println("New Quadrant\nx = " + x + "\ny = " + y + "\nhash = " + hashId);
	}
	
	public int getQuadrantHash()
	{
		return hashId;
	}
	
	public ArrayList<SpaceObject> getObjectsInQuadrant()
	{
		return objectsInQuadrant;
	}

	public int getNumOfObjectsInQuadrant()
	{
		return objectsInQuadrant.size();
	}
	
	public synchronized void addObjectToQuadrant(SpaceObject obj)
	{
		if(obj.getQuadrant() != this) // Only add if different
		{
			objectsInQuadrant.add(obj);
			obj.assignQuadrant(this);
		}
	}
	
	public synchronized void removeObjectFromQuadrant(SpaceObject obj)
	{
		try
		{
			//System.out.println("Removing object (hash " + obj.getGridID() + " from Quadrant " + getQuadrantHash());
			objectsInQuadrant.remove(obj);
		}
		catch(Exception e)
		{
			System.out.println("Error removing object(hash " + obj.getGridID() + ") from Quadrant " + getQuadrantHash() + ". Error: '" + e.toString() + ".'");
		}
	}
	
	public void draw(Graphics2D g2d)
	{
		g2d.drawRect(x, y, Board.WIDTH/Grid.xDiv, Board.HEIGHT/Grid.yDiv);
		g2d.drawString("Hash: " + getQuadrantHash(), x + Board.WIDTH/(10*Grid.xDiv), y + Board.HEIGHT/(10*Grid.yDiv));
		g2d.drawString("Objs: " + getNumOfObjectsInQuadrant(), x + Board.WIDTH/(10*Grid.xDiv), y + Board.HEIGHT/(10*Grid.yDiv) + 20);
		
	}
}
