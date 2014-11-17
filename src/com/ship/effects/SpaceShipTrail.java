package com.ship.effects;


import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import spaceGame.SpaceObject;


public class SpaceShipTrail 
{
	private ArrayList<TrailComponent> trailArr;
	private TrailComponent aComp;
	
	private int trailLength;
	
	public SpaceShipTrail(int len)
	{
		trailArr = new ArrayList<TrailComponent>();
		trailLength = len;
	}
	
	public void addTrailComponent(Color c, int x, int y)
	{
		if(trailArr.size() > trailLength)
			trailArr.remove(0);
		
		trailArr.add(new TrailComponent(c, x, y));
	}
	
	public boolean hasTrailComponents()
	{
		if(trailArr.size() > 0)
			return true;
		
		return false;
	}
	
	// Draw the circles on {x,y}
	public void drawTrail(Graphics2D g2d)
	{
		int i = 0;
		while(i < trailArr.size() && i >= 0)
		{
			aComp = trailArr.get(i);
			
			if(aComp.isVisible()) // if visible then draw it
				aComp.drawTrailComp(g2d);
			else // else, it must have been finished. Then delete it
			{
				trailArr.remove(i);
				i--;
			}
			
			i++;
		}
	}
	
	class TrailComponent
	{
		private final int startRadius = 10;
		private final int endRadius = 2;
		private double radius;
		private Color color;
		private double alpha;
		private int x;
		private int y;
		private boolean visible;
		
		
		public TrailComponent(Color c, int x, int y)
		{
			this.color = c;
			alpha = 200.0; // Color opaque
			this.x = x;
			this.y = y;
			visible = true;
			radius = startRadius;
		}
		
		public void drawTrailComp(Graphics2D g2d)
		{
			g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) alpha));
			g2d.fillOval(x, y, (int) radius, (int) radius);
			
			radius -= 0.02;
			alpha -= 1;
			
			if(alpha <= 0)
				visible = false;
			
			if(radius < endRadius)
				radius = endRadius;
		}
		
		public boolean isVisible()
		{
			return visible;
		}
		
		public void setVisible(boolean vis)
		{
			this.visible = vis;
		}
	}
}
