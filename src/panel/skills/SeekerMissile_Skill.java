package panel.skills;

import panel.FrontPanel;

public class SeekerMissile_Skill extends Skill
{
	public SeekerMissile_Skill()
	{
		super(Skill.SEEK_MISSILE_SKILL, Skill.SEEK_MISSILE_RECHARGE_TIME, true);
	}
	
	public void useSkill()
	{
		if(super.isAvailable() && !super.isRecharging())
		{
			FrontPanel.panelOwner.fireSeekingMissile();
			super.useSkill();
		}
		else
			System.out.println("Skill is not available...");
	}
}
