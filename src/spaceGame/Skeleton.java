package spaceGame;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

public class Skeleton extends JFrame
{
	public int WIDTH;
	public int HEIGHT;
	
	public Skeleton()
	{
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		WIDTH = gd.getDisplayMode().getWidth();
		HEIGHT = gd.getDisplayMode().getHeight()-40; //-40 to avoid going behind the task manager bar of windows/linux systems
		/*WIDTH = 700;
		HEIGHT = 700;*/
		
		setSize(WIDTH, HEIGHT);
		add(new Board(WIDTH, HEIGHT));
		
		setTitle("Game Window");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);	
	}
	
	public static void main(String args[])
	{
		new Skeleton();
	}
}
