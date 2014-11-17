package panel;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import panel.skills.AreaBomb_Skill;
import panel.skills.Booster_Skill;
import panel.skills.Missile_Skill;
import panel.skills.SeekerMissile_Skill;
import panel.skills.Skill;
import spaceGame.SFX_Player;
import spaceGame.SpaceShip;


public class FrontPanel 
{
	private static ArrayList<SkillSpace> skillSpaceArr;
	
	private static int x;
	private static int y;
	private static Point mouseClickLocation;
	private static SkillSpace lastSkillSpaceClicked;
	private static Point lastMouseClickLocation;
	private static SkillSpace tmp;
	private static int spaceCounter;
	
	public static SpaceShip panelOwner;
	public static final int MAX_SKILL_SPACES = 5;
	
	public FrontPanel(int x, int y, SpaceShip ship)
	{
		skillSpaceArr = new ArrayList<SkillSpace>();
		spaceCounter = 0;
		
		// Position
		this.x = x;
		this.y = y;
		
		this.panelOwner = ship; // owner of the panel
		
		// Add default skills
		for(int i = 0; i < MAX_SKILL_SPACES; i++)
			addSkillSpace(new SkillSpace()); // Unbinded
						
		skillSpaceArr.get(0).bindSkill(new Booster_Skill());
		skillSpaceArr.get(1).bindSkill(new Missile_Skill());
		skillSpaceArr.get(2).bindSkill(new AreaBomb_Skill());
		skillSpaceArr.get(3).bindSkill(new SeekerMissile_Skill());
		
		//skillSpaceArr.get(1).bindSkill(new Skill(Skill.AREA_BOMB_SKILL, false));
		//skillSpaceArr.get(2).bindSkill(new Skill(Skill.F_MISSILE_SKILL, false));
		//skillSpaceArr.get(3).bindSkill(new Skill(Skill.BOOSTERS_SKILL, true));
	}
	
	public static void addSkillSpace(SkillSpace space)
	{
		if(skillSpaceArr.size() < MAX_SKILL_SPACES)
		{
			space.setPosition(x + spaceCounter*SkillSpace.WIDTH, y); // Coordinates are decided by the class, not by the user
			
			skillSpaceArr.add(space);
			spaceCounter++;
		}
		else
			System.out.println("Skill Spaces limit exceeded!");
	}
	
	public static Point getMouseClickLocation()
	{
		assert(mouseClickLocation != null);
		
		return mouseClickLocation;
	}
	
	public static boolean setMouseClickLocation(Point loc)
	{
		mouseClickLocation = loc;
		
		//Check if the mouse point falls in any of the skill spaces
		lastSkillSpaceClicked = checkSkillSpaceMouseClickFlag(loc);
		
		if(lastSkillSpaceClicked != null)
		{
			if(!lastSkillSpaceClicked.isBinded()) // Skill is unavailable
			{
				SFX_Player.playSound(SFX_Player.OTHER_SOUND_PATH, SFX_Player.BUTTON_3_SOUND);
			}
			
			return true;
		}
		
		return false;
	}
	
	public static void setPanelOwner(SpaceShip ship)
	{
		panelOwner = ship;
	}
	
	public static SpaceShip getPanelOwner()
	{
		assert(panelOwner != null);
		return panelOwner;
	}
	
	// Returns a skillSpace that contains a skill of type skillType
	public static SkillSpace getSkillSpace(int skillType)
	{
		for(SkillSpace ss : skillSpaceArr)
		{
			if(ss.isBinded())
			{
				if(ss.getSkill().getSkillType() == skillType)
				{
					return ss;
				}
			}
		}
		
		//SFX_Player.playSound(SFX_Player.OTHER_SOUND_PATH, SFX_Player.BUTTON_3_SOUND);
		return null;
	}
	
	public static void deactivatePanel()
	{
		for(SkillSpace ss : skillSpaceArr)
		{
			if(ss.isBinded())
				ss.getSkill().setAvailable(false);
		}
	}
	
	private static SkillSpace checkSkillSpaceMouseClickFlag(Point mousePointer)
	{
		for(int i = 0; i < MAX_SKILL_SPACES; i++)
		{
			if(skillSpaceArr.get(i).containsMousePointer(mousePointer))
			{
				skillSpaceArr.get(i).setMouseClickFlag();
				return skillSpaceArr.get(i);
			}
		}

		return null;
	}
	
	public static void checkSkillSpaceMouseHover(Point mousePointer)
	{ 
		for(int i = 0; i < MAX_SKILL_SPACES; i++)
		{
			if(skillSpaceArr.get(i).containsMousePointer(mousePointer) && !skillSpaceArr.get(i).mouseOnTop())
			{
				skillSpaceArr.get(i).setMouseIsOnTop(true);
				SFX_Player.playSound(SFX_Player.OTHER_SOUND_PATH, SFX_Player.BUTTON_21_SOUND);

				//System.out.println("Mouse is on top");
			}
			else if(!skillSpaceArr.get(i).containsMousePointer(mousePointer))
			{
				skillSpaceArr.get(i).setMouseIsOnTop(false);
			} 
		}
	}
	
	// Draw the panel
	public void draw(Graphics2D g2d)
	{
		//if(mouseClickLocation != null)
		//	g2d.drawRect((int) mouseClickLocation.getX() - 12, (int) mouseClickLocation.getY()- 33, 20, 20);

		for(int i = 0; i < MAX_SKILL_SPACES; i++)
		{
			skillSpaceArr.get(i).draw(g2d);
		}
		
		//mouseClickLocation = null;
		
		//if(lastSkillSpaceClicked != null)
		//	lastSkillSpaceClicked.setMouseClickFlag(false);
	}
}
