package panel.skills;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

import panel.FrontPanel;



public class Skill implements Runnable
{
	
	public static final int MISSILE_SKILL 		= 0;
	public static final int AREA_BOMB_SKILL 	= 1;
	public static final int SEEK_MISSILE_SKILL 	= 2;
	public static final int SHIELD_SKILL 		= 3;
	public static final int BOOSTERS_SKILL 		= 4;
	
	public static final int MISSILE_RECHARGE_TIME 		= 0;
	public static final int AREA_BOMB_RECHARGE_TIME 	= 1000;
	public static final int SEEK_MISSILE_RECHARGE_TIME 	= 500;
	public static final int SHIELD_RECHARGE_TIME 		= 2000;
	public static final int BOOSTERS_RECHARGE_TIME 		= 0;
	
	public static final String IMG_PATH 	 	= "/resources/Skills/Images/";
	public static final String MISSILE_IMG 	 	= "missile.png";
	public static final String AREA_BOMB_IMG 	= "a_bomb.png";
	public static final String SEEK_MISSILE_IMG = "f_missile.png";
	public static final String SHIELD_IMG 	 	= "shield.png";
	public static final String BOOSTERS_IMG  	= "boosters.png";
	public static final String BOOSTERS_ACT_IMG = "boosters_active.png";
	
	// Possible Color Masks
	private static final Color SKILL_AVAILABLE_MASK = new Color(0, 0, 0, 0);
	private static final Color SKILL_DEPLETED_MASK  = new Color(255, 255, 255, 120);
	private static final Color SKILL_INFO_MASK 		= new Color(255, 0, 0, 80);
	private static final Color SKILL_ACTIVATED_MASK	= new Color(255, 0, 0, 120);
	
	private int skillType;
	private String skillName;
	private Color SKILL_MASK;
	private Image img;
	private boolean available;
	private boolean recharging;	
	private int rechargeTime; // Once activated, time it takes to become available again
	private int rechargeCounter;
	
	
	private Thread T;
	
	// Arrays
	private static final String[] imgs = {MISSILE_IMG, AREA_BOMB_IMG, 
										  SEEK_MISSILE_IMG, SHIELD_IMG, BOOSTERS_IMG};
	
	private static final String[] skillNames = {"Missile", "AreaBomb", "SeekMissile", "Shield", "Boosters"};
	
	public Skill(int type, int rechargeTime, boolean avail)
	{
		skillType = type;
		skillName = skillNames[type];
		
		this.rechargeTime = rechargeTime;
		available = avail;
		
		SKILL_MASK = (avail) ? SKILL_AVAILABLE_MASK : SKILL_DEPLETED_MASK;
		setImage(type);
	}
	
	public Image getSkillImage()
	{
		assert (img != null);
		return img;
	}
	
	public int getSkillType()
	{
		return skillType;
	}
	
	public void setAvailable(boolean val)
	{
		available = val;
		SKILL_MASK = (val)? SKILL_AVAILABLE_MASK : SKILL_DEPLETED_MASK;
	}
	
	public String getSkillName()
	{
		return skillNames[skillType];
	}
	
	public void setImage(int type)
	{
		try { 
			img = new ImageIcon(getClass().getResource(IMG_PATH + "" + imgs[type])).getImage();
		} catch(Exception e) {
			System.out.println("Error with skill type '" + type + ". " + e.toString());
		}
	}
	
	public void setImage(String IMG_TYPE)
	{
		try { 
			img = new ImageIcon(getClass().getResource(IMG_PATH + "" + IMG_TYPE)).getImage();
		} catch(Exception e) {
			System.out.println("Error with skill type '" + IMG_TYPE + ". " + e.toString());
		}
	}
	
	public Color getSkillMask()
	{
		return SKILL_MASK;
	}
		
	public boolean isAvailable()
	{
		return available;
	}
	
	public boolean isRecharging()
	{
		return recharging;
	}
	
	public static void playSkillSound(String PATH, String soundFilename)
	{
		
	}
		
	public void useSkill()
	{
		if(available && !recharging)
		{
			SKILL_MASK = SKILL_ACTIVATED_MASK;
			available = false;
			recharging = true;
			
			// Start recharging
			//rechargeCounter = rechargeTime;
			
			T = new Thread(this);
			T.start();
		}
	}
	
	public void run()
	{
		//System.out.println("Skill Started...");
		
		//while(rechargeCounter >= 0)
		//{
			try
			{
				//rechargeCounter--;
				Thread.sleep(rechargeTime);
			}
			catch(Exception e)
			{
				
			}
		//}
		
		recharging = false;
		available = true;
		
		SKILL_MASK = SKILL_AVAILABLE_MASK;
		//System.out.println("Skill available again...");
	}
}
