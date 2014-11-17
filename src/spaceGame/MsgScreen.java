package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JTextField;

public class MsgScreen implements Runnable
{
	private Image img;
	private boolean isShowing;
	private boolean opening;
	private Thread T;
	private ArrayList<String> msgArr;
	private ArrayList<String> userArr;

	private double currX;
	private static double xIncr;
	private static int HEIGHT;
	private static int WIDTH;
	private int tmp;
	private static final Color baseColor = new Color(255, 255, 255, 30);
	private static final BasicStroke baseStroke = new BasicStroke(1);
	private static final Font textFont = new Font("Arial", Font.PLAIN, 12);
	private static final Font userFont = new Font("Arial", Font.BOLD, 12);
	
	
	public static final long UPDATE_MS = 1;
	public static final int MSG_ARR_MAX_SIZE = 10;
	public static final int SCREEN_HEIGHT = 150;
	
	public static int SCREEN_WIDTH;
	public static int USER_INPUT_HEIGHT = 25;
	

	public MsgScreen(int WIDTH, int HEIGHT)
	{
		//img = new ImageIcon(getClass().getResource("/resources/" + imgFilename)).getImage();
		msgArr = new ArrayList<String>();
		userArr = new ArrayList<String>();
		
		xIncr = 10;
		currX = -SCREEN_WIDTH;
		SCREEN_WIDTH = WIDTH - 25;
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		
		opening = false;
		isShowing = false;
	}
	
	public void run()
	{
		while(true)
		{
			if(opening && currX > 0)
			{
				break;
			}
			else if(!opening && currX < -SCREEN_WIDTH)
			{
				isShowing = false;
				break;	
			}
			else
				isShowing = true;
			
			try
			{
				currX += xIncr;
				Thread.sleep(UPDATE_MS);
			} 
			catch(Exception e) 
			{
				
			}
		}
	}
	
	public void call()
	{
		if(opening)
		{
			xIncr = -Math.abs(xIncr);
			opening = false;
		}
		else
		{
			xIncr = Math.abs(xIncr);
			opening = true;
		}
		
		T = new Thread(this);
		T.start();
	}
	
	public boolean isShowing()
	{
		return isShowing;
	}
	
	public void newMsg(String sender, String msg)
	{
		if(msgArr.size() >= MSG_ARR_MAX_SIZE - 1)
		{
			msgArr.remove(0);
			userArr.remove(0);
			
			msgArr.add(msg);
			userArr.add(sender);
		}
		else
		{
			msgArr.add(msg);
			userArr.add(sender);
		}
	}
	
	public void draw(Graphics2D g2d)
	{
		if(isShowing)
		{
			drawTextArea(g2d, (int) currX, (int) (HEIGHT - SCREEN_HEIGHT - 80), SCREEN_WIDTH, SCREEN_HEIGHT);
			drawTextArea(g2d, (int) currX, (int) (HEIGHT - 70), SCREEN_WIDTH, USER_INPUT_HEIGHT);

			// Draw the text
			tmp = 0;
			
			for(int i = 0; i < msgArr.size(); i++)
			{
				g2d.setFont(textFont);
				g2d.setColor(new Color(255, 255, 255, 255 - 2*tmp));
				g2d.drawString("<" + userArr.get(msgArr.size() - i - 1) + ">   " +  
						             msgArr.get(msgArr.size() - i - 1), 
						             (int) currX + 10, HEIGHT - 90 - tmp);
				
				tmp += 15;
			}
		}
		else
		{
			
		}
	}
	
	public void drawTextArea(Graphics2D g2d, int x, int y, int width, int height)
	{
		g2d.setColor(Color.gray);
		g2d.setStroke(baseStroke);
		g2d.drawRect(x, y, width, height);
		
		g2d.setColor(baseColor);
		g2d.fillRect(x, y, width, height);
	}
	
	public void mouseClicked(Point p)
	{
		int x = p.x;
		int y = p.y;
		
		if(x >= 0 && x <= 20 && y >= HEIGHT - SCREEN_HEIGHT - 80 && y <= HEIGHT - 80 && !isShowing)
		{
			call();
		}
	}
}
