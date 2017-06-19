package me.lazward.julianplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class JulianPlugin extends JavaPlugin implements Listener {

	public ArrayList<UUID> playersSleeping = new ArrayList<UUID>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("Julian's Custom Plugin has been loaded. Hello!");
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

						sender.sendMessage("/ci <item name>");

					} else if (args.length >= 1) {

						if (this.checkInventorySpace(player)) {

							if (args[0].equals("kickhammer")) {

								player.getInventory().addItem(kickhammer());

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

	public static ItemStack kickhammer() {

		ItemStack stack = new ItemStack(Material.GOLD_AXE, 1);
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "The Kickhammer");
		im.setLore(Arrays.asList("A legendary weapon made for gods.", "Will instantly smite down anyone it hits."));
		im.setUnbreakable(true);
		stack.setItemMeta(im);

		return stack;

	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)) {

			Player player = (Player) event.getDamager();
			Player target = (Player) event.getEntity();
			World world = (World) getServer().getWorlds().get(0);

			if (player.getInventory().getItemInMainHand() != null) {

				ItemStack helditem = player.getInventory().getItemInMainHand();

				if (helditem.equals(kickhammer())) {
					if (player.getName().equals("Juelz0312")) {
						Location loc = target.getLocation();
						target.kickPlayer("YOU HAVE BEEN SMITTEN.");
						world.strikeLightningEffect(loc);

					}
				}

			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {

		Player player = event.getPlayer();

		ItemStack item = event.getItem().getItemStack();

		if (!player.getName().equals("Juelz0312")) {
			if (item.equals(kickhammer())) {
				ItemMeta newmeta = item.getItemMeta();
				newmeta.setUnbreakable(false);
				newmeta.setLore(Arrays.asList("A legendary weapon whose powers have disappeared.","It is pretty much useless now."));
				item.setItemMeta(newmeta);
				item.setDurability((short) 32);
			}
		}
	}
}