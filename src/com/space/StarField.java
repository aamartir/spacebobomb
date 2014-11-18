package com.space;

import java.awt.Color;
import java.awt.Graphics2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class StarField
{
	public final double STAR_MAX_VEL = 0.4;
	public final double STAR_MAX_RAD = 5;
	
	private ArrayList<FarStar> starArr;
	private int MAX_STARS;
	private int WIDTH;
	private int HEIGHT;
	
	FarStar thisStar;
	Random randGenerator;
	
	public StarField(int num, int W, int H, Random randGenerator)
	{
		this.randGenerator = randGenerator;
		starArr = new ArrayList<FarStar>();
		MAX_STARS = num;
		WIDTH = W;
		HEIGHT = H;
		
		int x;
		int y;
		for(int i = 0; i < MAX_STARS; i++)
		{
			x = randGenerator.nextInt(W);
			y = randGenerator.nextInt(H);
			
			createAndAddRandomStar(x, y);
		}
	}
	
	public void addStar(FarStar star)
	{
		starArr.add(star);
	}
	
	public FarStar addStar(String name, int x, int y, double vel, double rad, Color c)
	{
		FarStar star = new FarStar(name, x, y, vel, rad, c);
		addStar(star);
		
		return star;
	}
	
	// Randomly chooses velocity and radius
	public FarStar createAndAddRandomStar(int x, int y)
	{
		double vel = (STAR_MAX_VEL - 0.1)*randGenerator.nextDouble() + 0.1;
		double rad = (STAR_MAX_RAD - 0.2)*randGenerator.nextDouble() + 0.2;
	
		int gray = (int) randGenerator.nextInt(200) + 55;
		
		FarStar star = new FarStar("Unknown", x, y, vel, rad, new Color(gray, gray, gray));
		addStar(star);
		
		return star;
	}
	
	public void moveAndDrawStarField(Graphics2D g2d)
	{
		int i = 0;
		while(i < starArr.size())
		{
			thisStar = starArr.get(i);
			
			if(isWithinBoundaries(thisStar) && thisStar.isVisible())
			{
				thisStar.move();
				thisStar.drawStar(g2d);
			}
			else
			{
				starArr.remove(i);
				i--;
			}
			
			i++;
		}
	}
	
	private boolean isWithinBoundaries(FarStar aStar)
	{
		int x = (int) aStar.getPosX();
		int y = (int) aStar.getPosY();

		if(x > 0 && x < WIDTH && y > 0 && y < HEIGHT)
			return true;
		
		return false;
	}
	
	public ArrayList<FarStar> getStarArr()
	{
		return starArr;
	}
}
