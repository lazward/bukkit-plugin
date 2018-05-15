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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class JulianPlugin extends JavaPlugin implements Listener {

	public ArrayList<UUID> playersSleeping = new ArrayList<UUID>();
	HashMap<String, CustomWeapon> weaponslist = new HashMap<String, CustomWeapon>();
	// HashMap<String, Player> ts = new HashMap<String, Player>() ;
	Player stopper;

	boolean isTimeStopped = false;

	public int count = 0;

	HashMap<UUID, Location> entities = new HashMap<UUID, Location>();

	int cd;

	HashMap<UUID, Vector> velocities = new HashMap<UUID, Vector>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		weaponslist.put("kickhammer", new CustomWeapon(ChatColor.GOLD + "The Kickhammer", Arrays.asList("A legendary weapon made for gods.", "Will instantly smite down anyone it hits."), Material.GOLD_AXE, true));
		weaponslist.put("world", new CustomWeapon(ChatColor.GOLD + "The World", Arrays.asList("Gives you the ability to stop time for ten seconds."), Material.WATCH, true));
		weaponslist.put("sworld", new CustomWeapon(ChatColor.GOLD + "Star Platinum: The World", Arrays.asList("Stop time for five seconds."), Material.WATCH, true));
		getServer().getLogger().info("Julian's Custom Plugin v0.2.1 has been loaded. Hello!");
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
			sender.sendMessage(s);
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
		}

		return true;

	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {

		this.playersSleeping.add(event.getPlayer().getUniqueId());

		Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has went to bed.");

		testForSleepPercent();

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

	public void onBedLeave(UUID uuid) {

		if (this.playersSleeping.contains(uuid)) {

			this.playersSleeping.remove(uuid);
			long time = ((World) getServer().getWorlds().get(0)).getTime();
			if (time >= 12541 && time <= 23458) {

				Bukkit.broadcastMessage(Bukkit.getPlayer(uuid).getDisplayName() + " has gotten out of bed.");

			}

		}

	}

	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent event) {

		onBedLeave(event.getPlayer().getUniqueId());

	}

	@EventHandler
	public void onBedLeave(PlayerQuitEvent event) {

		onBedLeave(event.getPlayer().getUniqueId());

	}

	@EventHandler
	public void onBedLeave(PlayerKickEvent event) {

		onBedLeave(event.getPlayer().getUniqueId());

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

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (isTimeStopped && event.getDamager().getEntityId() != stopper.getEntityId()) {

			event.setCancelled(true);
			return;

		}

		if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)) {

			Player player = (Player) event.getDamager();
			Player target = (Player) event.getEntity();
			World world = (World) getServer().getWorlds().get(0);

			if (player.getInventory().getItemInMainHand() != null) {

				ItemStack helditem = player.getInventory().getItemInMainHand();

				if (helditem.equals(getWeapon("kickhammer"))) {
					if (player.getName().equals("Juelz0312")) {
						Location loc = target.getLocation();
						target.kickPlayer("YOU HAVE BEEN SMITTEN.");
						world.strikeLightningEffect(loc);
						return;

					}

				}

			}

			if (isTimeStopped) {

				Vector v = velocities.get(target.getUniqueId());
			    Vector t = target.getLocation().toVector().clone();
			    Vector p = player.getLocation().toVector().clone();
			    
			    Vector direction = t.subtract(p).normalize();
			    
			    v.add(direction) ;
			    velocities.put(target.getUniqueId(), v) ;

			}

		}

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		Player player = event.getPlayer();

		ItemStack item = event.getItem().getItemStack();

		if (!player.getName().equals("Juelz0312")) {
			if (item.equals(getWeapon("kickhammer"))) {
				ItemMeta newmeta = item.getItemMeta();
				newmeta.setUnbreakable(false);
				newmeta.setLore(Arrays.asList("A legendary weapon whose powers have disappeared.",
						"It is pretty much useless now."));
				item.setItemMeta(newmeta);
				item.setDurability((short) 32);
			}
		}
	}

	@EventHandler
	public void toggle(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		ItemStack inHand = player.getInventory().getItemInHand();

		if ((inHand.equals(weaponslist.get("world").getItemStack())
				|| inHand.equals(weaponslist.get("sworld").getItemStack())) && !isTimeStopped) {

			if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
					|| event.getAction().RIGHT_CLICK_BLOCK == Action.RIGHT_CLICK_BLOCK) {

				stopTime(player, inHand);

			}

		}

	}

	public void stopTime(Player p, ItemStack h) {

		World world = (World) getServer().getWorlds().get(0);
		final Long t = world.getFullTime();
		long howLong = 0;
		
		if (h.equals(weaponslist.get("sworld").getItemStack())) {
			
			howLong = 160L;
			count = 5;
			
		} else {
			
			howLong = 260L;
			count = 10;
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

			i.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (int) 60L, 10));

			if (i instanceof Player) {

				world.playSound(i.getLocation(), Sound.BLOCK_PORTAL_TRIGGER, 10, 1);

			}

		}

		Bukkit.broadcastMessage("Time has stopped at " + t);
		log(p.getName() + "has stopped time at " + t);

		JulianPlugin plugin = this;

		plugin.getServer().getScheduler().cancelAllTasks();

		cd = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {

				stopper = p;
				countdown(world);

			}

		}, 60L);

		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {

				resumeTime(world, t);

			}

		}, howLong);

	}

	public void resumeTime(World w, Long t) {

		isTimeStopped = false;

		w.setFullTime(t);
		Bukkit.broadcastMessage("Time has resumed! " + w.getFullTime());
		log("Time has resumed! " + w.getFullTime());
		for (Player i : Bukkit.getOnlinePlayers()) {

			w.playSound(i.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 10, 1);

			if (!i.getName().equals(stopper.getName())) {

				i.setVelocity(velocities.get(i.getUniqueId()));

			}

		}

		stopper = null;

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

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) { // && !e.getPlayer().equals(stopper)

			e.setCancelled(true);
			Location location = e.getFrom();
			location.setPitch(e.getTo().getPitch());
			location.setYaw(e.getTo().getYaw());
			e.getPlayer().teleport(location);

		}

	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

		if (isTimeStopped && !e.getWhoClicked().equals(stopper)) {

			e.setCancelled(true);

		}
		
		if(ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Legendary Weapons")) {
			
			Player p = (Player) e.getWhoClicked() ;
			e.setCancelled(true);
			
			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta()) {
				
				return ;
				
			}
			
			p.getInventory().addItem(e.getCurrentItem());
			p.closeInventory();
			
		}

	}

	@EventHandler
	public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent e) {

		if (isTimeStopped && e.getEntity().getEntityId() == stopper.getEntityId()) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent e) {

		if (isTimeStopped && e.getEntity().getEntityId() == stopper.getEntityId()) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent e) {

		if (isTimeStopped && !e.getPlayer().equals(stopper)) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerVelocityEvent(PlayerVelocityEvent e) {

		Player p = e.getPlayer();
		EntityDamageEvent last = p.getLastDamageCause();

		if (last == null || !(last instanceof EntityDamageByEntityEvent)) {

			return;

		}

		if (isTimeStopped && ((EntityDamageByEntityEvent) last).getDamager().getEntityId() == stopper.getEntityId()) {

			Vector v = velocities.get(p.getUniqueId());
			v = v.add(e.getVelocity());
			velocities.put(p.getUniqueId(), v);

		}

	}

	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {

		if (isTimeStopped) {

			if (!e.getEntity().getShooter().equals(stopper)) {

				e.setCancelled(true);

			}

		}

	}
	
	@EventHandler
	public void onEntityAirChangeEvent(EntityAirChangeEvent e) {
		
		if (isTimeStopped && !e.getEntity().equals(stopper)) {
			
			e.setCancelled(true);
			
		}
		
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		e.getPlayer().sendMessage(ChatColor.GOLD
				+ "Hello! Congrats on getting through the first year of college. I just wanted to mention that after starting up the server and looking around the world we all built I couldn't help but have a huge grin on my face. I truly missed this place. Let's keep it going. \n(July 27th 2016-August 9th 2017) ~ (May 12th 2018 - ???)");

	}

	@EventHandler
	public void log(String x) {

		Bukkit.getConsoleSender().sendMessage(x);

	}
	
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

}