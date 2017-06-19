package me.lazward.julianplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command ;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class JulianPlugin extends JavaPlugin implements Listener {
	
	public ArrayList<UUID> playersSleeping = new ArrayList<UUID>() ;
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("Julian's Custom Plugin has been loaded. Hello!") ;
	}
	
	public void onDisable() {
		getServer().getLogger().info("Zzz... Julian's Custom Plugin has been unloaded.") ;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("chatas")) { // chatas command
			
			if (Bukkit.getOnlinePlayers().size() >= 1) {
			
			if (sender.hasPermission("julian.chatAs.use")) {
				
				if (args.length == 0) {
					
					sender.sendMessage("/chatas <player> <message>") ;
					
				} else if (args.length > 1) {
					
					if (args[0].equals("*")) {
						
						String message = "" ;
						for (int i = 1 ; i < args.length ; i++) {
							
							if (i == args.length - 1) {
								
								message = message + args[i] ;
								
							} else {
								
								message = message + args[i] + " " ;
							}
							
						}
						
						List<Player> onlinePlayers = new ArrayList<Player>() ;
						for (Player p : getServer().getOnlinePlayers()) {
							onlinePlayers.add(p) ;
						}
						
						for (int i = 0 ; i < onlinePlayers.size() ; i++) {
							Player all = onlinePlayers.get(i) ;
							all.chat(message);
						}
						return true ;
					}
					
					Player target = Bukkit.getPlayer(args[0]) ;
					if (target != null) {
						
						String message = "" ;
						
						for (int i = 1 ; i < args.length ; i++) {
							
							if (i == args.length - 1) {
								
								message = message + args[i] ;
								
							} else {
								
								message = message + args[i] + " " ;
								
							}
						}
						
						target.chat(message);
						
					} else {
						
						sender.sendMessage("Incorrect player name.");

					}
				} else {
						
						sender.sendMessage("Incorrect arguments!");
				}	
				} else {
						
						sender.sendMessage("You're not allowed to use this command. Especially you, Brian.");
						
					}
				} else {
					sender.sendMessage("Nobody is online, what the hell are you doing?");
				}

				} else if (command.getName().equalsIgnoreCase("ticks")) { // check time
			
			        String s = String.valueOf(getServer().getWorlds().get(0).getFullTime()) ;
			        sender.sendMessage(s);
			        return true ;
			
		}
		
		return true ;
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {
		
		this.playersSleeping.add(event.getPlayer().getUniqueId()) ;
		
		Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has went to bed.") ;
		
		testForSleepPercent() ;
		
	}
	
	public void testForSleepPercent() {
		
		float percent = (float)this.playersSleeping.size() / getServer().getOnlinePlayers().size() * 100.0F ;
		
		if (percent >= 33.3) {
			
			Bukkit.broadcastMessage("A majority of the server has went to bed. See you in the morning!") ;
			
			
			World world = (World)getServer().getWorlds().get(0) ;
			world.setTime(world.getTime() + (23999 - world.getTime() % 23999)) ;
			
			if (world.hasStorm() || world.isThundering()) {
				
				world.setThundering(false);
				world.setStorm(false);
				
			}
			
			this.playersSleeping.clear() ;
			
		}
		
	}
	
	public void onBedLeave(UUID uuid) {
		
		if (this.playersSleeping.contains(uuid)) {
			
			this.playersSleeping.remove(uuid) ;
			long time = ((World)getServer().getWorlds().get(0)).getTime() ;
			if (time >= 12541 && time <= 23458) {
				
				Bukkit.broadcastMessage(Bukkit.getPlayer(uuid).getDisplayName() + " has gotten out of bed.") ;
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {
		
		onBedLeave(event.getPlayer().getUniqueId()) ;
		
	}
	
	@EventHandler
	public void onBedLeave(PlayerQuitEvent event) {
		
		onBedLeave(event.getPlayer().getUniqueId()) ;
		
	}
	
	@EventHandler
	public void onBedLeave(PlayerKickEvent event) {
		
		onBedLeave(event.getPlayer().getUniqueId()) ;
		
	}
	
	
}
