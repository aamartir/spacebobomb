package panel.skills;

import panel.FrontPanel;

public class Missile_Skill extends Skill
{	
	public static final String SKILL_INFO = "10HP Missile attack. Unlimited Resource";
	
	public Missile_Skill() 
	{
		super(Skill.MISSILE_SKILL, Skill.MISSILE_RECHARGE_TIME, true);
	}

	public void useSkill()
	{
		if(super.isAvailable() && !super.isRecharging())
		{
			FrontPanel.panelOwner.fireMissile();
			super.useSkill();
		}
		else
			System.out.println("Skill is not available...");
	}
}
