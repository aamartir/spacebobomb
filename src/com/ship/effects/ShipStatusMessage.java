package com.ship.effects;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import spaceGame.SpaceObject;


public class ShipStatusMessage
{
	private String msg;
	private Color c;
	private static int msgCounter;
	private int id;
	private boolean completed;
	int R;
	int G;
	int B;
	double Alpha;
	double yIncr;
	double yLast;
	
	public ShipStatusMessage(String msg, Color c)
	{
		this.msg = msg;
		this.c = c;
		this.completed = false;
		this.Alpha = 255.0;
		id = ++msgCounter;
		yLast = -20;
	}
	
	public double getYincr()
	{
		return yIncr;
	}
	
	public int getMessageID()
	{
		return id;
	}
	
	public boolean isFinished()
	{
		return completed;
	}
	
	public void drawMessage(Graphics2D g2d, int x, int y)
	{
		g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) Alpha));
		g2d.setFont(new Font("ARIAL", Font.BOLD, 20));
		g2d.drawString(msg, x, (int)(y + yIncr));
		
		Alpha -= 0.1;
		
		if(yIncr > yLast)
			yIncr -= 0.05;
		
		if(Alpha <= 0)
		{
			completed = true;
			msgCounter--;
			Alpha = 255;
			yIncr = 0;
		}
		
		
	}
}
