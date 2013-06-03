package com.vartala.soulofw0lf.rpgfood;

public class CustomFood {
	public int id, health, time, buffDur, buffAmp;
	String name, b;
	public CustomFood(String name, int id, int health, int time, String b, int buffDur, int buffAmp)
	{
	 this.id = id;
	 this.name = name;
	 this.health = health;
	 this.time = time;
	 this.b = b;
	 this.buffDur = buffDur;
	 this.buffAmp = buffAmp;
	}
	
}
