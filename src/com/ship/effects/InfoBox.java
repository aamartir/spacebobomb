package com.ship.effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class InfoBox 
{
	public  static final int    DEFAULT_FONT_SIZE = 20;
	public  static final Font   DEFAULT_FONT      = new Font( "ARIAL", Font.PLAIN, DEFAULT_FONT_SIZE );
	private static final double INFO_BOX_XOFFSET = 10.0;
	
	public InfoBox()	
	{}
	
	public static void draw( Graphics2D g2d, 
			                double x0,
			                double y0, 
			                double lineLength,
			                double lineAngleRadians, 
			                Color  lineColor, 
			                Color  boxBackgroundColor,
			                Color  boxEdgeColor,
			                Color  strColor,
			                Font   strFont,
			                String str )
	{
		double x1 = x0 + lineLength*Math.cos(lineAngleRadians);
		double y1 = y0 - lineLength*Math.sin(lineAngleRadians);
		
		// Draw line
		g2d.setColor( lineColor );
		g2d.draw( new Line2D.Double(x0, y0, x1, y1) );
		g2d.draw( new Line2D.Double(x1, y1, x1 + INFO_BOX_XOFFSET, y1) );
		
		// InfoBox backgrond color
		g2d.setColor( boxBackgroundColor );
		g2d.draw( new Rectangle2D.Double(x1 + INFO_BOX_XOFFSET, 
						                 y1 - strFont.getSize()/2.0,
						                 strFont.getSize()*str.length()     /* WIDTH */,
						                 strFont.getSize() /* HEIGHT */) );
		
		// Draw box with text inside
		g2d.setColor( boxEdgeColor );
		g2d.draw( new Rectangle2D.Double(x1 + INFO_BOX_XOFFSET, 
				                         y1 - strFont.getSize()/2.0,
				                         strFont.getSize()*str.length()      /* WIDTH */,
				                         strFont.getSize() /* HEIGHT */ ) );
	}
}
