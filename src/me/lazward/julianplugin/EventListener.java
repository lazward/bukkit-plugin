package me.lazward.julianplugin;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
	
	JulianPlugin p ;
	
	public EventListener(JulianPlugin plugin) {
		
		 p = plugin ;
		
	}
	
	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {

		p.playersSleeping.add(event.getPlayer().getUniqueId());

		Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has went to bed.");

		p.testForSleepPercent();

	}
	
	public void onBedLeave(UUID uuid) {

		if (p.playersSleeping.contains(uuid)) {

			p.playersSleeping.remove(uuid);
			long time = ((World) p.getServer().getWorlds().get(0)).getTime();
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
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (p.isTimeStopped() && event.getDamager().getEntityId() != p.getStopper().getEntityId()) {

			event.setCancelled(true);
			return;

		}

		if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)) {

			Player player = (Player) event.getDamager();
			Player target = (Player) event.getEntity();
			World world = (World) p.getServer().getWorlds().get(0);

			if (player.getInventory().getItemInMainHand() != null) {

				ItemStack helditem = player.getInventory().getItemInMainHand();

				if (helditem.equals(p.getWeapon("kickhammer"))) {
					if (player.getName().equals("Juelz0312")) {
						Location loc = target.getLocation();
						target.kickPlayer("YOU HAVE BEEN SMITTEN.");
						world.strikeLightningEffect(loc);
						return;

					}

				}

			}

			if (p.isTimeStopped()) {

				Vector v = p.getVelocities().get(target.getUniqueId());
			    Vector t = target.getLocation().toVector().clone();
			    Vector pl = player.getLocation().toVector().clone();
			    
			    Vector direction = t.subtract(pl).normalize();
			    
			    v.add(direction) ;
			    p.putVelocity(target.getUniqueId(), v) ;

			}

		}

	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		Player player = event.getPlayer();
		
		if (player != p.getStopper()) {
			
			event.setCancelled(true);
			return ;
			
		}

		ItemStack item = event.getItem().getItemStack();

		if (!player.getName().equals("Juelz0312")) {
			if (item.equals(p.getWeapon("kickhammer"))) {
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
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

		Player player = event.getPlayer();

		ItemStack inHand = player.getInventory().getItemInHand();

		if ((inHand.equals(p.getWeapons().get("world").getItemStack())
				|| inHand.equals(p.getWeapons().get("sworld").getItemStack())) && !p.isTimeStopped()) {

				if (p.isTimeStopped()) {
					
					p.resumeTime(p.getServer().getWorlds().get(0), p.getFTime());
					
				} else {
				
				p.stopTime(player, inHand);
				
				}


		}
		
		}

	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) { // && !e.getPlayer().equals(stopper)

			e.setCancelled(true);
			Location location = e.getFrom();
			location.setPitch(e.getTo().getPitch());
			location.setYaw(e.getTo().getYaw());
			e.getPlayer().teleport(location);

		}

	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {

		if (p.isTimeStopped() && !e.getWhoClicked().equals(p.getStopper())) {

			e.setCancelled(true);

		}
		
		if(ChatColor.stripColor(e.getInventory().getName()).equalsIgnoreCase("Legendary Weapons")) {
			
			Player player = (Player) e.getWhoClicked() ;
			e.setCancelled(true);
			
			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR) || !e.getCurrentItem().hasItemMeta()) {
				
				return ;
				
			}
			
			player.getInventory().addItem(e.getCurrentItem());
			player.closeInventory();
			
		}

	}
	

	@EventHandler
	public void onEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent e) {

		if (p.isTimeStopped() && !(e.getEntity() instanceof Player)) {

			e.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onEntityTargetEvent(EntityTargetEvent e) {

		if (p.isTimeStopped() && !(e.getEntity() instanceof Player)) {

			e.setCancelled(true);

		}
		
	}
	
	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}

	@EventHandler
	public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) {

			e.setCancelled(true);

		}

	}
	
	@EventHandler
	public void onPlayerVelocityEvent(PlayerVelocityEvent e) {

		Player player = e.getPlayer();
		EntityDamageEvent last = player.getLastDamageCause();

		if (last == null || !(last instanceof EntityDamageByEntityEvent)) {

			return;

		}

		if (p.isTimeStopped() && ((EntityDamageByEntityEvent) last).getDamager().getEntityId() == p.getStopper().getEntityId()) {

			Vector v = p.getVelocities().get(player.getUniqueId());
			v = v.add(e.getVelocity());
			p.putVelocity(player.getUniqueId(), v);

		}

	}
	
	@EventHandler
	public void onProjectileLaunchEvent(ProjectileLaunchEvent e) {

		if (p.isTimeStopped()) {

			if (!e.getEntity().getShooter().equals(p.getStopper())) {

				e.setCancelled(true);

			}

		}

	}
	
	@EventHandler
	public void onEntityAirChangeEvent(EntityAirChangeEvent e) {
		
		if (p.isTimeStopped() && !e.getEntity().equals(p.getStopper())) {
			
			e.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onEntityTeleportEvent(EntityTeleportEvent e) {
		
		if (p.isTimeStopped() && !e.getEntity().equals(p.getStopper())) {
			
			e.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent e) {
		
		if (p.isTimeStopped() && !e.getEntity().equals(p.getStopper()) && (e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.DROWNING)) {
			
			e.setCancelled(true);
			
		}
		
	}
	
	@EventHandler
	public void log(String x) {

		Bukkit.getConsoleSender().sendMessage(x);

	}

}
