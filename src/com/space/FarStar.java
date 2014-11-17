package com.space;

import java.awt.Color;
import java.awt.Graphics2D;

import spaceGame.SpaceObject;

public class FarStar extends SpaceObject
{
	private double rad;
	private String starName;
	private Color color;
	
	public FarStar(String name, int x, int y, double vel, double rad, Color color)
	{
		super(x, y, vel, 90);
		
		this.rad = rad;
		this.color = color;
		setStarName(name);
	}
	
	// Static, unknown star (far-away) 
	public FarStar(int x, int y, int rad)
	{
		this("Unknown", 0, x, y, rad, Color.white);
	}
	
	public double getRadius()
	{
		return rad;
	}
	
	public void setRadius(double rad)
	{
		this.rad = rad;
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
	
	public void drawStar(Graphics2D g2d)
	{
		g2d.setColor(color);
		g2d.fillOval((int) getPosX(), (int) getPosY(), (int) rad, (int) rad);
	}
}
