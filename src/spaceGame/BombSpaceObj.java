package spaceGame;

import java.util.Random;

import javax.swing.ImageIcon;

public class BombSpaceObj extends SpaceObject
{
	private int bombContent;
	private static Random randGenerator = new Random();
	
	public static final String PATH 	= "/resources/";
	public static final String BOMB_IMG = "bomb.png";
	
	public BombSpaceObj(int x, int y, double vel, double angle)
	{
		super(PATH, BOMB_IMG, x, y, vel, angle);
	
		bombContent = randGenerator.nextInt(2) + 1;
	}
	
	public int getBombContent()
	{
		return bombContent;
	}
	
	public void setBombContent(int val)
	{
		bombContent = val;
	}
}
