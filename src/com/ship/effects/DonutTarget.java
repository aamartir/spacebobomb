package com.ship.effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class DonutTarget 
{
	public DonutTarget()
	{}
	
	public static void draw( Graphics2D g2d, double centerX, double centerY, double r, double dr, Color color )
	{
		Area outer = new Area( new Ellipse2D.Double(centerX-r, centerY-r, 2*r, 2*r) );
		Area hole = new Area( new Ellipse2D.Double(centerX-(r-dr), centerY-(r-dr), 2*(r-dr), 2*(r-dr)) );
		
		// Subtract the center to make a donut
		outer.subtract(hole);
		
		// Draw it
		g2d.setColor( color );
		g2d.fill( outer );
	}
}
