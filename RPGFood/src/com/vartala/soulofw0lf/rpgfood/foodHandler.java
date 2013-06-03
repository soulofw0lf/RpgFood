package com.vartala.soulofw0lf.rpgfood;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class foodHandler implements CommandExecutor {

	RpgFood Rpgf;

	public foodHandler(RpgFood rpgf) {
		this.Rpgf = rpgf;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		Player player = null;
		if (sender instanceof Player){
		player = (Player) sender;
		}
		if (((sender instanceof Player) && (player.hasPermission("food.give"))) || (!(sender instanceof Player))){
			if (args[0].equalsIgnoreCase("give")){
				Player p = Bukkit.getPlayer(args[1]);
				if (p == null){
					sender.sendMessage("Could not find player");
					return true;
				}
				if (!(this.Rpgf.getConfig().contains("Food." + args[2].replaceAll("_", " ")))){
					sender.sendMessage("that item does not exist!");
					return true;
				}
				if (args.length <= 3){
					sender.sendMessage("Improper usage! please use /food give playername itemname ammount!");
					return true;
				}
				PlayerInventory pi = p.getInventory();
				Material additems = Material.getMaterial(this.Rpgf.getConfig().getInt("Food." + args[2].replaceAll("_", " ") + ".ItemID"));
				Integer number = Integer.parseInt(args[3]);
				ItemStack is = new ItemStack(additems, number); 
				ItemMeta im = is.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("Heals " + this.Rpgf.getConfig().getDouble("Food." + args[2].replaceAll("_", " ") + ".health") + " health over " + this.Rpgf.getConfig().getDouble("Food." + args[2].replaceAll("_", " ") + ".time") + " seconds.");
				im.setLore(lore);
				im.setDisplayName(args[2].replaceAll("_", " "));
				is.setItemMeta(im);
				pi.addItem(is);
				sender.sendMessage("You have given " + p.getName() + " " + number + " " + additems.name() + "'s!");
				return true;
			}
			
		}
		if (player.hasPermission("food.add")){
			if (args[0].equalsIgnoreCase("effect")){
				if (args.length != 5){
					player.sendMessage("Incorrect usage, please use /food effect food_name effectname duration amplifier");
					return true;
				}
				if (!(this.Rpgf.getConfig().contains("Food." + args[1].replaceAll("_", " ")))){
					player.sendMessage("that item does not exist!");
				}
				Integer dura = Integer.parseInt(args[3]);
				Integer amp = Integer.parseInt(args[4]);
				this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".Food Buff", args[2]);
				this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".Buff Duration", dura);
				this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".Buff Amp", amp);
				this.Rpgf.saveConfig();
				player.sendMessage("You have added a " + args[3] + " second " + args[2] + " buff to " + args[1] + " with a strength of " + args[4]);
				return true;
				
			}

			if (args.length != 4){
				player.sendMessage("Improper usage! please use /food ItemID Name_of_item health# timetoeat#");
				return true;
			}
			Integer ItemId = Integer.parseInt(args[0]);
			Double Time = Double.parseDouble(args[3]);
			Integer Health = Integer.parseInt(args[2]);			
			this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".ItemID", ItemId);
			this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".health", Health);
			this.Rpgf.getConfig().set("Food." + args[1].replaceAll("_", " ") + ".time", Time);
			this.Rpgf.saveConfig();
			player.sendMessage("You have saved " + args[1].replaceAll("_", " ") + ", to give " + args[2] + " health, over " + args[3] + " second.");
			return true;	
		} else {
			player.sendMessage("You do not have permission to use this command!");
			return true;
		}
	}
}
