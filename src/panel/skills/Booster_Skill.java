package panel.skills;

import java.awt.Image;

import panel.FrontPanel;

public class Booster_Skill extends Skill 
{
	private boolean active;
	
	public Booster_Skill() 
	{
		super(Skill.BOOSTERS_SKILL, Skill.BOOSTERS_RECHARGE_TIME, true);
		active = false;
	}
	
	public void useSkill()
	{
		if(super.isAvailable())
		{
			if(active)
			{
				FrontPanel.panelOwner.stopBoosters();
			}
			else
			{
				FrontPanel.panelOwner.useBoosters();
			}
			
			active = !active;
		}
		else
			System.out.println("Skill is not available...");
	}
}
