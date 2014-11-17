package panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import panel.skills.Skill;
import spaceGame.FloatingMessageBox;
import spaceGame.SpaceObject;


public class SkillSpace 
{
	private boolean binded;
	private Skill skill;
	
	// Dimensions
	public static int WIDTH = 40;
	public static int HEIGHT = 40;
	
	private int x;
	private int y;
	private boolean mouseClickFlag = false;
	private boolean mouseIsOnTop = false;
	
	private final BasicStroke borderWidth = new BasicStroke(1f);
	private final Color spaceBackground = new Color(255, 255, 255, 30);
	
	public SkillSpace()
	{
		binded = false;
		skill = null;
	}
	
	public SkillSpace(Skill skill)
	{
		bindSkill(skill);
	}
	
	public SkillSpace(int skillType, int rechargeTime, boolean avail)
	{
		bindSkill(new Skill(skillType, rechargeTime, avail));
	}
	
	public void bindSkill(Skill skill)
	{
		assert(skill != null);
		
		this.skill = skill;
		binded = true;
	}
	
	public void unbindSkill()
	{
		this.skill = null;
		binded = false;
	}
	
	public boolean isBinded()
	{
		return binded;
	}
	
	public Skill getSkill()
	{
		return skill;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public void setMouseIsOnTop(boolean val)
	{
		this.mouseIsOnTop = val;
	}
	
	public boolean mouseOnTop()
	{
		return mouseIsOnTop;
	}
	
	// Draws the empty block if it's not binded, or draws the block with the skill image.
	public void draw(Graphics2D g2d)
	{
		// Border
		g2d.setColor(Color.gray);
		g2d.setStroke(borderWidth);
		g2d.drawRoundRect(x, y, WIDTH, HEIGHT, 10, 10);

		if(!binded)
		{
			// Transparent inside
			g2d.setColor(spaceBackground);
			g2d.fillRoundRect(x, y, WIDTH, HEIGHT, 10, 10);
		}
		else  
		{
			// Draw Skill icon with its respective mask
			g2d.drawImage(skill.getSkillImage(), x, y, null);
			
			g2d.setColor(skill.getSkillMask());
			g2d.fillRoundRect(x, y, WIDTH, HEIGHT, 10, 10);
			
			// Draw info msg box
			if(mouseIsOnTop)
			{
				FloatingMessageBox.draw(g2d, skill.getSkillName(), x, y - 40);
				//mouseIsOnTop = false;
			}
		}
	}
	
	public void setMouseClickFlag()
	{
		mouseClickFlag = true;
		
		if(mouseClickFlag && binded)
		{
			getSkill().useSkill();
			mouseClickFlag = false;
		}
	}
	
	public boolean getMouseClickFlag()
	{
		return mouseClickFlag;
	}
	
	public boolean containsMousePointer(Point mousePoint)
	{
		return (new Rectangle(mousePoint.x-5, mousePoint.y-27, 10, 10).intersects(getBounds()));
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
}
