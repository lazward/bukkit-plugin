package me.lazward.julianplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class JulianPlugin extends JavaPlugin {

	public ArrayList<UUID> playersSleeping = new ArrayList<UUID>();
	HashMap<String, CustomWeapon> weaponslist = new HashMap<String, CustomWeapon>();
	// HashMap<String, Player> ts = new HashMap<String, Player>() ;
	private Player stopper;

	private boolean isTimeStopped = false;

	public int count = 0;

	HashMap<UUID, Location> entities = new HashMap<UUID, Location>();

	int cd;

	HashMap<UUID, Vector> velocities = new HashMap<UUID, Vector>();
	
	Long t ;

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
		weaponslist.put("kickhammer", new CustomWeapon(ChatColor.GOLD + "The Kickhammer", Arrays.asList("A legendary weapon made for gods.", "Will instantly smite down anyone it hits."), Material.GOLD_AXE, true));
		weaponslist.put("world", new CustomWeapon(ChatColor.GOLD + "The World", Arrays.asList("Gives you the ability to stop time for ten seconds."), Material.WATCH, true));
		weaponslist.put("sworld", new CustomWeapon(ChatColor.GOLD + "Star Platinum: The World", Arrays.asList("Stop time for five seconds."), Material.WATCH, true));
		getServer().getLogger().info("Julian's Custom Plugin v0.2.3 has been loaded. Hello!");
	}

	public void onDisable() {
		
		getServer().getLogger().info("Zzz... Julian's Custom Plugin has been unloaded.");
		
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (command.getName().equalsIgnoreCase("chatas")) { // chatas command

			if (Bukkit.getOnlinePlayers().size() >= 1) {

				if (sender.hasPermission("julian.chatAs.use")) {

					if (args.length == 0) {

						sender.sendMessage("/chatas <player> <message>");

					} else if (args.length > 1) {

						if (args[0].equals("*")) {

							String message = "";
							for (int i = 1; i < args.length; i++) {

								if (i == args.length - 1) {

									message = message + args[i];

								} else {

									message = message + args[i] + " ";
								}

							}

							List<Player> onlinePlayers = new ArrayList<Player>();
							for (Player p : getServer().getOnlinePlayers()) {
								onlinePlayers.add(p);
							}

							for (int i = 0; i < onlinePlayers.size(); i++) {
								Player all = onlinePlayers.get(i);
								all.chat(message);
							}
							return true;
						}

						@SuppressWarnings("deprecation")
						Player target = Bukkit.getPlayer(args[0]);
						if (target != null) {

							String message = "";

							for (int i = 1; i < args.length; i++) {

								if (i == args.length - 1) {

									message = message + args[i];

								} else {

									message = message + args[i] + " ";

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

			String s = String.valueOf(getServer().getWorlds().get(0).getFullTime());
			sender.sendMessage("Full ticks: " + s);
			return true;

		} else if (command.getName().equalsIgnoreCase("ci")) {

			if ((sender instanceof Player)) {

				Player player = (Player) sender;

				if (sender.hasPermission("julian.ci.use")) {

					if (args.length == 0) {

						openGUI(player) ;

					} else if (args.length >= 1) {

						if (this.checkInventorySpace(player)) {

							if (weaponslist.containsKey(args[0])) {

								player.getInventory().addItem(weaponslist.get(args[0]).getItemStack());

							} else {

								sender.sendMessage("Invalid item.");

							}

						} else {

							sender.sendMessage("You do not have enough inventory space.");

						}

					}

				} else {

					sender.sendMessage("You're not allowed to use this.");

				}

				return true;
				
			} else {

				sender.sendMessage("Only players can use that command!");

			}
			
		} else if (command.getName().equalsIgnoreCase("wolf")) {
			
			if ((sender instanceof Player)) {
			Player s = (Player) sender ;
			
			Block[] bs = s.getLineOfSight(null, 10).toArray(new Block[0]);
			
			boolean found = false ;
			
			for (Block b : bs) {
				
				for (Entity e : s.getNearbyEntities(10, 10, 10)) {
				
				if (e.getLocation().distance(b.getLocation()) < 2) {
					
					if (e instanceof Wolf) {
						
						if (((Wolf) e).isTamed()) {
							
							sender.sendMessage("Owner: " + ((Wolf) e).getOwner().getName());
							found = true ;
							
						} else {
							
							sender.sendMessage("This wolf is not tamed!") ;
							found = true ;
						}
						
					} else {
						
						sender.sendMessage("This is not a wolf!") ;
						found = true ;
						
					}
					
				}
				
			}
			
			}
			
			if (!found) {
			
				sender.sendMessage("Wolf not found!") ;
			
			}
			
			return true ;
							
			} else {
				
				sender.sendMessage("Only players can use this command!") ;
				return true ;
				
			}
			
		}

		return true;

	}
	
	public boolean isTimeStopped() {
		
		return isTimeStopped ;
		
	}
	
	public Player getStopper() {
		
		return stopper ;
		
	}
	
	public void setStopper(Player p) {
		
		stopper = p ;
		
		
	}
	
	public HashMap<UUID, Vector> getVelocities() {
		
		return velocities ;
		
	}
	
	public void putVelocity(UUID u, Vector v) {
		
		velocities.put(u, v) ;
		
	}
	
	public HashMap<String, CustomWeapon> getWeapons() {
		
		return weaponslist ;
		
	}

	public void testForSleepPercent() {

		float percent = (float) this.playersSleeping.size() / getServer().getOnlinePlayers().size() * 100.0F;

		if (percent >= 33.3) {

			Bukkit.broadcastMessage("A portion of the server has went to bed. See you in the morning!");

			World world = (World) getServer().getWorlds().get(0);
			world.setTime(world.getTime() + (23999 - world.getTime() % 23999));

			if (world.hasStorm() || world.isThundering()) {

				world.setThundering(false);
				world.setStorm(false);

			}

			this.playersSleeping.clear();

		}

	}

	public boolean checkInventorySpace(Player player) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {

				return true;

			}

		}

		return false;

	}

	public ItemStack getWeapon(String name) {

		if (weaponslist.containsKey(name)) {

			return weaponslist.get(name).getItemStack();

		}

		return null;

	}

	public void stopTime(Player p, ItemStack h) {

		World world = (World) getServer().getWorlds().get(0);
		setFTime(world.getFullTime());
		long howLong = 0;
		
		if (h.equals(weaponslist.get("sworld").getItemStack())) {
			
			howLong = 80L;
			count = 1;
			
		} else {
			
			howLong = 160L;
			count = 5;
		}

		for (Player j : Bukkit.getOnlinePlayers()) {

			velocities.put(j.getUniqueId(), j.getVelocity());

		}

		for (LivingEntity k : world.getLivingEntities()) {

			velocities.put(k.getUniqueId(), k.getVelocity());

		}

		isTimeStopped = true;

		List<LivingEntity> e = world.getLivingEntities();

		for (LivingEntity i : e) {

			if (i instanceof Player) {
				
				i.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) 60L, 10));

			} else {
				
				i.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) howLong, 10));

			}
			
			if (i.getFireTicks() != 0) {
				
				i.setFireTicks(i.getFireTicks() + count + 3);
				
			}

			i.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) 60L, 10));

			if (i instanceof Player) {

				world.playSound(i.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 10, 1);

			}

		}

		Bukkit.broadcastMessage(p.getName() + " has stopped time at " + t);

		JulianPlugin plugin = this;

		plugin.getServer().getScheduler().cancelAllTasks();

		cd = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {

				stopper = p;
				stopper.setVelocity(velocities.get(stopper.getUniqueId()));
				countdown(world);

			}

		}, 60L);

		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {

				resumeTime(world, getFTime());

			}

		}, howLong);

	}

	public void resumeTime(World w, Long t) {
		
		if (isTimeStopped == false) {
			
			return ;
			
		}

		isTimeStopped = false;

		w.setFullTime(t);
		Bukkit.broadcastMessage("Time has resumed! " + w.getFullTime());
		for (Player i : Bukkit.getOnlinePlayers()) {

			w.playSound(i.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 10, 1);

			if (!i.getName().equals(stopper.getName())) {

				i.setVelocity(velocities.get(i.getUniqueId()));

			}

		}

		stopper = null;
		setFTime(null) ;

	}

	public void countdown(World w) {

		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {

				if (count != 0) {

					for (Player i : Bukkit.getOnlinePlayers()) {

						w.playSound(i.getLocation(), Sound.BLOCK_LEVER_CLICK, 10, 1);

					}

					Bukkit.broadcastMessage(count + " seconds left!");

					count--;

				}

			}

		}, 0L, 20L);

	}
	/*

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		e.getPlayer().sendMessage(ChatColor.GOLD
				+ "Hello! Congrats on getting through the first year of college. I just wanted to mention that after starting up the server and looking around the world we all built I couldn't help but have a huge grin on my face. I truly missed this place. Let's keep it going. \n(July 27th 2016-August 9th 2017) ~ (May 12th 2018 - ???)");

	}
	
	*/
	
	
	@EventHandler
    public void openGUI(Player p) {
		
		Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Legendary Weapons") ;
		
		int i = 0 ;
		for (String j : weaponslist.keySet()) {
			
			inv.setItem(i, weaponslist.get(j).getItemStack()) ;
			i++ ;
			
		}
		
		p.openInventory(inv) ;
		
	}
	
	public void setFTime(Long l) {
		
		t = l ;
		
	}
	
	public long getFTime() {
		
		return t ;
		
	}

}