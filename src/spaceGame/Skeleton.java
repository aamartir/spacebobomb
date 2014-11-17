package spaceGame;

import javax.swing.JFrame;

public class Skeleton extends JFrame
{
	public int WIDTH;
	public int HEIGHT;
	
	public Skeleton()
	{
		WIDTH = 700;
		HEIGHT = 700;
		
		setSize(WIDTH, HEIGHT);
		add(new Board(WIDTH, HEIGHT));
		
		setTitle("Window");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		setResizable(false);	
	}
	
	public static void main(String args[])
	{
		new Skeleton();
	}
}
