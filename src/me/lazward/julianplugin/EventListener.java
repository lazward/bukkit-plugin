package me.lazward.julianplugin;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class EventListener implements Listener {

	JulianPlugin p;

	public EventListener(JulianPlugin plugin) {

		p = plugin;

	}

	@EventHandler
	public void onBedEnter(PlayerBedEnterEvent event) {

		if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {

			p.playersSleeping.add(event.getPlayer().getUniqueId());

			Bukkit.broadcastMessage(event.getPlayer().getDisplayName() + " has went to bed.");

			p.testForSleepPercent();

		}

	}

	public void onBedLeave(UUID uuid) {

		if (p.playersSleeping.contains(uuid)) {

			p.playersSleeping.remove(uuid);
			// long time = ((World) p.getServer().getWorlds().get(0)).getTime();
			// if (time >= 12541 && time <= 23458) {

			Bukkit.broadcastMessage(Bukkit.getPlayer(uuid).getDisplayName() + " has gotten out of bed.");

			// }

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

				v.add(direction);
				p.putVelocity(target.getUniqueId(), v);

			}

		}

	}

	@EventHandler
	public void onEntityPickupItem(EntityPickupItemEvent event) {

		if (event.getEntity() instanceof Player) {

			Player player = (Player) event.getEntity();

			ItemStack item = event.getItem().getItemStack();

			if (!player.getName().equals("Juelz0312")) {
				if (item.equals(p.getWeapon("kickhammer"))) {
					ItemMeta newmeta = item.getItemMeta();
					newmeta.setUnbreakable(false);
					newmeta.setLore(Arrays.asList("A legendary weapon whose powers have disappeared.",
							"It is pretty much useless now."));
					((Damageable) newmeta).setDamage(32);
					item.setItemMeta(newmeta);
				}
			}

		}

	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {

		if (p.isTimeStopped() && !event.getPlayer().equals(p.getStopper())) {

			event.setCancelled(true);

		}

		Player player = event.getPlayer();
		ItemStack inHand = player.getInventory().getItemInMainHand();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if ((inHand.equals(p.getWeapons().get("world").getItemStack())
					|| inHand.equals(p.getWeapons().get("sworld").getItemStack())) && !p.isTimeStopped()) {

				if (!player.getWorld().equals(p.getServer().getWorlds().get(0))) {

					player.sendMessage("Time does not exist in this world!");

				} else {

					if (p.isTimeStopped()) {

						p.resumeTime(p.getServer().getWorlds().get(0), p.getFTime());

					} else {

						p.stopTime(player, inHand);

					}

				}

			} else if (inHand.equals(p.getWeapons().get("testw").getItemStack())) {

				if (p.cooldowns.containsKey(player.getUniqueId())) {

					long timeLeft = ((p.cooldowns.get(player.getUniqueId()) / 1000) + 5)
							- (System.currentTimeMillis() / 1000);

					if (timeLeft > 0) {

						player.sendMessage("Cooldown: " + timeLeft + " secs");

					}

				} else {

					if (!p.activeAbility.contains(player.getUniqueId())) {

						p.activeAbility.add(player.getUniqueId());

						player.sendMessage("Ready");

						p.getServer().getScheduler().runTaskLater(p, new Runnable() {

							public void run() {

								if (!p.cooldowns.containsKey(player.getUniqueId())) {

									p.activeAbility.remove(player.getUniqueId());

									player.sendMessage("Cancelled");

								}

							}

						}, 80L);

					}

				}

			}

		} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {

			if (inHand.equals(p.getWeapons().get("testw").getItemStack())) {

				if (p.activeAbility.contains(player.getUniqueId())) {

					Block[] bs = player.getLineOfSight(null, 10).toArray(new Block[0]);

					for (Block b : bs) {

						for (Entity e : player.getNearbyEntities(10, 10, 10)) {

							if (e.getLocation().distance(b.getLocation()) < 2) {

								if (e instanceof LivingEntity) {

									e.getWorld().playEffect(e.getLocation(), Effect.ENDER_SIGNAL, 0);
									e.getWorld().playEffect(e.getLocation(), Effect.WITHER_SHOOT, 0);
									((LivingEntity) e).damage(10);
									Vector v = e.getLocation().toVector().subtract(player.getLocation().toVector())
											.normalize();
									e.setVelocity(v);

								}

							}

						}

					}

					player.sendMessage("Activated");

					p.cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

					p.activeAbility.remove(player.getUniqueId());

					p.getServer().getScheduler().runTaskLater(p, new Runnable() {

						public void run() {

							p.cooldowns.remove(player.getUniqueId());
							player.sendMessage("Cooldown finished");

						}

					}, 100L);

				}

			}

		}

	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) {

		if (p.isTimeStopped() && !e.getPlayer().equals(p.getStopper())) { // && !e.getPlayer().equals(stopper)

			e.setCancelled(true);
			Location location = e.getFrom();
			location.setPitch(e.getTo().getPitch());
			location.setYaw(e.getTo().getYaw());
			e.getPlayer().teleport(location);

		} else if (p.fly.contains(e.getPlayer().getUniqueId()) && (e.getTo().getY() > e.getFrom().getY())) {

			p.launch(e.getPlayer());

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

		if (ChatColor.stripColor(e.getView().getTitle()).equalsIgnoreCase("Legendary Weapons")) {

			Player player = (Player) e.getWhoClicked();
			e.setCancelled(true);

			if (e.getCurrentItem() == null || e.getCurrentItem().getType().equals(Material.AIR)
					|| !e.getCurrentItem().hasItemMeta()) {

				return;

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

		if (e.getPlayer().getInventory().getChestplate().getType() == Material.ELYTRA) {

			if (e.isSneaking() && e.getPlayer().isOnGround()) {

				p.getServer().getScheduler().runTaskLater(p, new Runnable() {

					public void run() {

						if (e.isSneaking() && e.getPlayer().getInventory().getChestplate().getType() == Material.ELYTRA
								&& e.getPlayer().getLocation().getPitch() <= -30 && e.getPlayer().isOnGround())

							p.fly.add(e.getPlayer().getUniqueId());
						e.getPlayer().sendMessage("Ready to fly");

					}

				}, 30L);

			} else if (p.fly.contains(e.getPlayer().getUniqueId())
					&& (!e.isSneaking() || !e.getPlayer().isOnGround())) {

				p.fly.remove(e.getPlayer().getUniqueId());
				e.getPlayer().sendMessage("Cancelled");

			}

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

		if (p.isTimeStopped()
				&& ((EntityDamageByEntityEvent) last).getDamager().getEntityId() == p.getStopper().getEntityId()) {

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

		if (p.isTimeStopped() && !e.getEntity().equals(p.getStopper()) && (e.getCause() == DamageCause.FIRE_TICK
				|| e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.DROWNING)) {

			e.setCancelled(true);

		}

		if (e.getEntity() instanceof Player) {

			Player player = (Player) e.getEntity();

			if (player.getHealth() - e.getDamage() < 1) {

				if (player.getInventory().contains(p.getWeapons().get("bookmark").getItemStack())) {

					e.setCancelled(true);
					player.setHealth(20.0);
					player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 2));

				}

			}

		}

	}

	@EventHandler
	public void onPlayerItemBreakEvent(PlayerItemBreakEvent e) {

		ItemStack b = e.getBrokenItem();

		if (b.getItemMeta().getDisplayName().equals(p.getWeapon("yato").getItemMeta().getDisplayName())) {

			Player player = e.getPlayer();
			player.sendMessage("The Omega Yato has run out of energy.");

			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(p, new Runnable() {

				public void run() {

					if (player.getInventory().firstEmpty() == -1) {

						player.sendMessage("You don't have enough space for the Omega Yato.");

					} else {

						player.sendMessage("The Omega Yato has been restored.");
						player.getInventory().addItem(p.getWeapon("yato"));

					}

				}

			}, 300);

		}

	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) {
		
		Player p = e.getEntity() ;
		Bukkit.broadcastMessage(p.getDisplayName() + "has died at " + (int)p.getLocation().getX() + " " + (int)p.getLocation().getY() + " " + (int)p.getLocation().getZ()) ;
		
	}

}
