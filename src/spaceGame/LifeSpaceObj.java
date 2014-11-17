package spaceGame;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Random;
import javax.swing.ImageIcon;

public class LifeSpaceObj extends SpaceObject 
{
	private int lifeContent;
	private static Random randGenerator = new Random();
	
	public static final String PATH 	= "/resources/";
	public static final String LIFE_IMG = "lifeHeart.png";
	
	public LifeSpaceObj(int x, int y, double vel, double angle)
	{
		super(PATH, LIFE_IMG, x, y, vel, angle);

		lifeContent = randGenerator.nextInt(25) + 20; // 25 to 50 HP
	}
	
	public int getLifeContent()
	{
		return this.lifeContent;
	}
	
	public void setLifeContent(int val)
	{
		this.lifeContent = val;
	}
}
