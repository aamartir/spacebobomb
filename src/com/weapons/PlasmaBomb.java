package com.weapons;

import java.awt.Color;

public class PlasmaBomb extends Weapon
{
	public PlasmaBomb(int x, int y, double vel, double angle)
	{
		super(Weapon.PLASMA_BOMB_IMG, x, y, vel + Weapon.PLASMA_BOMB_VEL, angle, Weapon.PLASMA_BOMB_DMG);

		super.getExplObj().setColor(Color.yellow);
		super.getExplObj().setEndRadius(Weapon.PLASMA_BOMB_EXPL_RAD);
	}
}
