package com.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import spaceGame.SpaceObject;

public class Star
{
	private static Random random;
	
	private double posX;
	private double posY;
	private double size;
	private double frequency;
	private double distance;
	private String starName;
	private Color color;
	
	public Star( String name, double x, double y, double distance, double size, Color color )
	{
		posX = x;
		posY = y;
		
		this.size  = size;
		this.distance = distance;
		
		this.color = color;
		if( color == null )
			color = createRandomColor();
		
		setStarName(name);
	}
	
	public Star( double x, double y, double distance, double size )
	{
		this( "Unknown", x, y, distance, size, null );
	}
	
	public double geStarSize()
	{
		return size;
	}
	
	public double getPosX()
	{
		return posX;
	}
	
	public double getPosY()
	{
		return posY;
	}
	
	public void setSize( int newSize )
	{
		this.size = newSize;
	}
	
	public void setStarPosition( double newX, double newY )
	{
		posX = newX;
		posY = newY;
	}
	
	public void incrStarPosition( double incrX, double incrY )
	{
		posX += incrX;
		posY += incrY;
	}
	
	public double getStarDistance()
	{
		return distance;
	}
	
	public String getStarName()
	{
		return starName;
	}
	
	public void setStarName(String name)
	{
		if(!name.isEmpty() && name != null)
			this.starName = name;
		else
			this.starName = "Unknown";
	}
	
	public Color getStarColor()
	{
		return color;
	}
	
	public void setStarColor(Color color)
	{
		this.color = color;
	}
	
	public Color createRandomColor()
	{
		if( random == null )
			random = new Random();

		switch( random.nextInt(3) )
		{
			case 0: 
				return Color.WHITE;
			case 1:
				return Color.RED;
			default:
				return Color.BLUE;
		}
	}
	
	public void drawStar( Graphics2D g2d )
	{
		g2d.setColor( color );
		
		// Enable antialiasing
		// g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		// Construct a shape and draw it
		g2d.fill( new Ellipse2D.Double(posX, posY, size, size) );
	}
}
