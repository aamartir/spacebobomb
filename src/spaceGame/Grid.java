package spaceGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Grid 
{
	private static ArrayList<Quadrant> grid;
	private static Quadrant thisQuad;
	
	public static int xDiv;
	public static int yDiv;
	
	public Grid(int xDiv, int yDiv)
	{
		grid = new ArrayList<Quadrant>();
		
		this.xDiv = xDiv;
		this.yDiv = yDiv;
		
		initGrid();
	}
	
	public void initGrid()
	{
		for(int y = 0; y < yDiv; y++)
		{
			for(int x = 0; x < xDiv; x++)
			{
				grid.add(new Quadrant(x*Board.WIDTH/xDiv, y*Board.HEIGHT/yDiv, xDiv, yDiv));
			}
		}
	}
	
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
	
	public static int calculateQuadrantHash(int x, int y, int xDiv, int yDiv)
	{
		return (int) (Math.floor((x+1)*xDiv/Board.WIDTH) + Math.floor((y+1)*yDiv/Board.HEIGHT)*yDiv);
	}
	
	public static void draw(Graphics2D g2d)
	{
		g2d.setColor(Color.gray);
		
		for(Quadrant quad : grid)
		{
			quad.draw(g2d);
		}
	}
}
