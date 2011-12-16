package tk.royalcraf.royalchat;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RoyalChatCommands implements CommandExecutor {

	RoyalChat plugin;

	public RoyalChatCommands(RoyalChat plugin) {
		this.plugin = plugin;
	}

	Logger log = Logger.getLogger("Minecraft");

	public boolean isAuthorized(final Player player, final String node) {
		if (player.isOp()) {
			return true;
		} else if (plugin.setupPermissions()) {
			if (RoyalChat.permission.has(player, node)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isAuthorized(final CommandSender player, final String node) {
		if (player.isOp()) {
			return true;
		} else if (plugin.setupPermissions()) {
			if (RoyalChat.permission.has((Player) player, node)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean getOnline(final String person) {
		Player player = Bukkit.getServer().getPlayer(person);

		if (player == null) {
			return false;
		} else {
			return true;
		}
	}

	// getFinalArg taken from EssentialsCommand.java - Essentials by
	// EssentialsTeam
	public static String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		if (cmd.getName().equalsIgnoreCase("rchat")) {
			if (!isAuthorized(sender, "rchat.rchat")) {
				sender.sendMessage(ChatColor.RED
						+ "You don't have permission for that!");
				return true;
			} else {
				sender.sendMessage(ChatColor.AQUA + "RoyalChat"
						+ ChatColor.GREEN + " version " + plugin.version
						+ " reloaded.");
				plugin.reloadConfig();
				plugin.formatBase = plugin.getConfig().getString("chat-format")
						.replaceAll("(&([a-f0-9]))", "\u00A7$2");
				plugin.formatMeBase = plugin.getConfig().getString("me-format")
						.replaceAll("(&([a-f0-9]))", "\u00A7$2");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("me")) {
			if (!isAuthorized(sender, "rchat.me")) {
				sender.sendMessage(ChatColor.RED
						+ "You don't have permission for that!");
				return true;
			} else {
				if (args.length < 1) {
					return false;
				} else {
					if (plugin.setupPermissions()) {
						if (RoyalChat.permission.isEnabled()) {
							if (plugin.setupChat()) {
								if (RoyalChat.chat.isEnabled()) {
									String format = plugin.formatMeBase;
									Player splayer = (Player) sender;
									String message = getFinalArg(args, 0);

									String prefix = RoyalChat.chat
											.getPlayerPrefix(splayer)
											.replaceAll("(&([a-f0-9]))",
													"\u00A7$2");
									String suffix = RoyalChat.chat
											.getPlayerSuffix(splayer)
											.replaceAll("(&([a-f0-9]))",
													"\u00A7$2");
									String group = RoyalChat.permission
											.getPrimaryGroup(splayer)
											.replaceAll("(&([a-f0-9]))",
													"\u00A7$2");
									String name = splayer.getName().replaceAll(
											"(&([a-f0-9]))", "\u00A7$2");
									String dispname = splayer.getDisplayName()
											.replaceAll("(&([a-f0-9]))",
													"\u00A7$2");

									format = format.replace("{name}", name);
									format = format.replace("{dispname}",
											dispname);
									format = format.replace("{group}", group);
									format = format.replace("{suffix}", suffix);
									format = format.replace("{prefix}", prefix);
									format = format.replace("{message}",
											message);
									plugin.getServer().broadcastMessage(format);
									return true;
								} else {
									String format = plugin.formatMeBase;
									String message = getFinalArg(args, 0);
									Player splayer = (Player) sender; 
									String name = splayer.getDisplayName();
									format = " * " + name + " " + message;
									plugin.getServer().broadcastMessage(format);
									return true;
								}
							} else {
								String format = plugin.formatMeBase;
								String message = getFinalArg(args, 0);
								Player splayer = (Player) sender; 
								String name = splayer.getDisplayName();
								format = " * " + name + " " + message;
								plugin.getServer().broadcastMessage(format);
								return true;
							}
						} else {
							String format = plugin.formatMeBase;
							String message = getFinalArg(args, 0);
							Player splayer = (Player) sender; 
							String name = splayer.getDisplayName();
							format = " * " + name + " " + message;
							plugin.getServer().broadcastMessage(format);
							return true;
						}
					} else {
						String format = plugin.formatMeBase;
						String message = getFinalArg(args, 0);
						Player splayer = (Player) sender; 
						String name = splayer.getDisplayName();
						format = " * " + name + " " + message;
						plugin.getServer().broadcastMessage(format);
						return true;
					}
				}
			}
		} else if (cmd.getName().equalsIgnoreCase("clear")) {
			if (!isAuthorized(sender, "rchat.clear")) {
				sender.sendMessage(ChatColor.RED
						+ "You don't have permission for that!");
				return true;
			} else {
				for (int i = 0; i < 20;) {
					sender.sendMessage("");
					i = i + 1;
				}
				return true;
			}
		}
		return true;
	}
}