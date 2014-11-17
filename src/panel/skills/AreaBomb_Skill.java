package panel.skills;

import panel.FrontPanel;

public class AreaBomb_Skill extends Skill
{
	public AreaBomb_Skill() 
	{
		super(Skill.AREA_BOMB_SKILL, Skill.AREA_BOMB_RECHARGE_TIME, true);
	}

	public void useSkill()
	{
		if(super.isAvailable() && !super.isRecharging())
		{
			FrontPanel.panelOwner.fireBomb();
			super.useSkill();
		}
		else
			System.out.println("Skill is not available...");
	}
}
