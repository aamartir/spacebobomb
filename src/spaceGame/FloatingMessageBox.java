package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class FloatingMessageBox 
{
	private static int WIDTH;
	private static int HEIGHT;
	
	public static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 40);
	public static final BasicStroke bStroke = new BasicStroke(1f);
	public static final Font bFont = new Font("Arial", Font.PLAIN, 12);
	
	public static void draw(Graphics2D g2d, String str, int x, int y)
	{
		WIDTH = (int) Math.round(8*str.length());
		HEIGHT = 30;
		
		g2d.setColor(Color.gray);
		g2d.setStroke(bStroke);
		g2d.drawRect(x, y, WIDTH, HEIGHT);
		
		g2d.setColor(TRANSPARENT_COLOR);
		g2d.fillRect(x, y, WIDTH, HEIGHT);
		
		g2d.setFont(bFont);
		g2d.drawString(str, x + 5, y + HEIGHT/2 + 4);
	}
}
