package com.ship.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import spaceGame.EnemyShip;
import spaceGame.SpaceShip;


public class Explosion 
{
	private double posX;
	private double posY;
	private boolean visible;
	public final double startRadius = 0;
	public double endRadius = 100;
	private double currRad;
	private double alpha;
	private double explDiff;
	private Color c;
	
	public Explosion()
	{
		visible = false;
		currRad = startRadius;
		posX = 0;
		posY = 0;
		alpha = 255;
		explDiff = 8;
		
		// Default is red
		c = new Color(255, 0, 0);
	}
	
	public Explosion(Color c)
	{
		this();
		this.c = c;
	}
	
	public void setColor(Color c)
	{
		this.c = c;
	}
	
	public void setExplDiff(double val)
	{
		this.explDiff = val;
	}
	
	public void setEndRadius(double val)
	{
		this.endRadius = val;
	}
	
	public Color getColor()
	{
		return c;
	}
	
	public void drawExplotion(Graphics2D g2d)
	{
		g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)alpha));
		g2d.setStroke(new BasicStroke((int)explDiff));
		g2d.drawOval((int)(posX-currRad/3), (int)(posY-currRad/3), (int)currRad, (int)currRad);

		currRad += 0.025;
		if(currRad >= endRadius)
			currRad = endRadius;
			
		alpha -= 0.1;
		if(alpha <= 0)
		{
			alpha = 0;
			visible = false;
		}
		
		explDiff -= 0.001;
		if(explDiff <= 0)
			explDiff = 0;
	}
		
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public boolean isVisible()
	{
		return visible;
	}

	public double getPosX() 
	{
		return posX;
	}

	public void setPosX(double posX) 
	{
		this.posX = posX;
	}

	public double getPosY() 
	{
		return posY;
	}

	public void setPosY(double posY) 
	{
		this.posY = posY;
	}
}
