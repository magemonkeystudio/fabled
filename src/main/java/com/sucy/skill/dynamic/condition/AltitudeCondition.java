package com.sucy.skill.dynamic.condition;

import org.bukkit.entity.LivingEntity;

import com.sucy.skill.dynamic.DynamicSkill;

import mc.promcteam.engine.mccore.config.parse.DataSection;

public class AltitudeCondition extends ConditionComponent{

	private static final String MIN = "min";
	private static final String MAX = "max";
	
	private int min, max;
	
	@Override
	boolean test(LivingEntity caster, int level, LivingEntity target) {
		
		return target.getLocation().getY() >= min && target.getLocation().getY() <= max;
	}
	
	@Override
	public void load(DynamicSkill skill, DataSection config) {
		super.load(skill, config);
		
		min = settings.getInt(MIN);
		max = settings.getInt(MAX);
	}

	@Override
	public String getKey() {
		return "Altitude";
	}
	
	

}
