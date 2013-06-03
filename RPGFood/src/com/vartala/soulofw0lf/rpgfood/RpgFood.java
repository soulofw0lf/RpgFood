package com.vartala.soulofw0lf.rpgfood;




import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;



public class RpgFood extends JavaPlugin implements Listener {
	private RpgFood plugin;
	public ArrayList<CustomFood> foodList = new ArrayList<CustomFood>();
	public List<String> playerEatting = new ArrayList<String>();
	@Override
	public void onEnable(){
		plugin = this;
		getCommand("food").setExecutor(new foodHandler(this));
		getLogger().info("onEnable has been invoked!");
		//load food from config here or in another method like
		/*
		 * 1. read from file
		 * 2. food f = new CustomFood(name,id,health,time,b, buffDur,buffAmp)
		 * 3. foodList.add(f)
		 */
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
	}
	@Override
	public void onDisable(){
	}
	@EventHandler
	public void onRestore(EntityRegainHealthEvent event)
	{
		if ((event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) || 
				(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED))
		{
			if ((event.getEntity() instanceof Player))
			{  

				event.setCancelled(true);

			} 
		} 
	} 

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event){
		if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() == DamageCause.STARVATION) {
        event.setCancelled(true);
        return;
        }
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onFoodLevelChange(FoodLevelChangeEvent event){
		Player p = (Player) event.getEntity();
		event.setCancelled(true);	  
		p.setSaturation(5.0F);
	}
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerUse(PlayerInteractEvent event){
		Player p = event.getPlayer();
		ItemStack item = p.getItemInHand();
		if(item == null || item.getTypeId() == 0){
			return;
		} else {
			if (p.getItemInHand() != null){
				ItemMeta im = p.getItemInHand().getItemMeta();
				int health = p.getHealth();
				int maxhealth = p.getMaxHealth();
				if (im.hasDisplayName()){
					String iname = im.getDisplayName();
					if (foodList.contains(iname)){
						event.setCancelled(true);
						if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)){
							if (health == maxhealth){
								p.sendMessage("you cannot eat while your health is full!");
								return;
							} else 
							{
								if (playerEatting.contains(p.getName())){
									p.sendMessage("You are already eatting!");
									return;
								} else {
									playerEatting.add(p.getName());
									Integer inhand = item.getAmount() -1;
									ItemStack newitem = new ItemStack(item.getType(), inhand);
									newitem.setItemMeta(im);
									p.setItemInHand(newitem);
									saveConfig();
									this.foodConsumption(p, iname);
								}
							}
						} else 
						{
							return;
						}
					} else 
					{
						return;
					}
				} else 
				{
					return;
				}
			} else 
			{
				return;
			}
		}
	}
	public void foodConsumption(Player player, String iname){
		final Player p = player;
		final String name = iname;
		player.setFoodLevel(0);
		final Double addHunger = 20 / getConfig().getDouble("Food." + name + ".time"); 
		Integer addhealth = getConfig().getInt("Food." + name + ".health");
		final Integer smallAdd = (int) (addhealth / getConfig().getDouble("Food." + name + ".time"));
		new BukkitRunnable(){
			Integer count = getConfig().getInt("Food." + name + ".time");
			Boolean locset = false;
			Double addFood = 0.00; 
			Double oldX = 0.00;
			Double oldZ = 0.00;
			@Override
			public void run(){
				if (locset == false){
					oldX = p.getLocation().getX();
					oldZ = p.getLocation().getZ();
				}
				locset = true;
				double newFood = addFood + addHunger;
				Integer currentFood = (int) newFood;
				if (currentFood <= 1){
					currentFood = 1;
				}
				if (count <=0){
					p.setFoodLevel(20);
					if (getConfig().contains("Food." + name + ".Food Buff")){
						String buff = getConfig().getString("Food." + name + ".Food Buff");
						Integer dura = getConfig().getInt("Food." + name + ".Buff Duration") * 20;
						Integer amp = getConfig().getInt("Food." + name + ".Buff Amp");
						if (buff.equalsIgnoreCase("Blindness")){
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, dura, amp), true);
						}
						if (buff.equalsIgnoreCase("Confusion")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Damage_Resistance")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Fast_Digging")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Fire_Resistance")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Harm")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.HARM, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Heal")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Increase_Damage")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Invisibility")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Jump")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Night_Vision")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Poison")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Regeneration")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Slow")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Slow_Digging")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Speed")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Water_Breathing")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Weakness")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, dura, amp), true);
							}
						if (buff.equalsIgnoreCase("Wither")){
							p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, dura, amp), true);
							}
					}
					getConfig().set(p.getName(), null);
					locset = false;
					saveConfig();
					cancel();
				} else{
					if (p.getFoodLevel() == 20){
						p.setFoodLevel(20);
						getConfig().set(p.getName(), null);
						saveConfig();
						locset = false;
						cancel();
					} else {
						if ((p.getLocation().getX() != oldX) || (p.getLocation().getZ() != oldZ)){
							p.sendMessage("You must stand still to eat!");
							p.setFoodLevel(20);
							getConfig().set(p.getName(), null);
							saveConfig();
							locset = false;
							cancel();
						} else {
							if (p.getHealth() + smallAdd >= p.getMaxHealth()){
								p.setHealth(p.getMaxHealth());
								p.setFoodLevel(currentFood);
								addFood = newFood;
								count--;
							} else {
								p.setFoodLevel(currentFood);
								p.setHealth(p.getHealth() + smallAdd);
								addFood = newFood;
								count--;
							}
						}
					}
				}
			}	
		}.runTaskTimer(plugin, 0, 20);
	}
}
