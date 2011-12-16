package tk.royalcraf.royalchat;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class RoyalChatPListener extends PlayerListener {

	RoyalChat plugin;

	private String prefix = null;
	private String suffix = null;
	private String group = null;
	private String name = null;
	private String dispname = null;

	public RoyalChatPListener(RoyalChat plugin) {
		this.plugin = plugin;
	}

	// Init logger
	Logger log = Logger.getLogger("Minecraft");

	// Permissions tester for player object
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

	// Permissions tester for CommandSender object
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

	// Test is player is online method
	public static boolean getOnline(final String person) {
		Player player = Bukkit.getServer().getPlayer(person);

		if (player == null) {
			return false;
		} else {
			return true;
		}

	}

	// The chat processor
	public void onPlayerChat(PlayerChatEvent event) {

		// Get sent message
		String message = event.getMessage();

		// Get player object of sender
		Player sender = event.getPlayer();

		// Test if authorized for color
		if (!isAuthorized(sender, "rchat.color")) {

			// If not, remove color
			message = message.replaceAll("(&([a-f0-9]))", "");

		} else if (isAuthorized(sender, "rchat.color")) {

			// If yes, allow color
			message = message.replaceAll("(&([a-f0-9]))", "\u00A7$2");

		}

		if (message.startsWith("&") && message.length() == 2) {
			message = message.replaceAll("(&([a-f0-9]))", "&f");
		}

		// Replace @<user> with ChatColor.AQUA + @<user>
		message = message.replaceAll("@.[a-zA-Z0-9_-]*", ChatColor.AQUA + "$0"
				+ ChatColor.WHITE);
		if (message.contains("%")) {
			message = message.replace("%", "%%");
		}
		if (plugin.getConfig().getBoolean("first-word-capital")) {
			String firstLetter = message.substring(0, 1);
			firstLetter = firstLetter.toUpperCase();
			message = firstLetter + message.substring(1);
		}

		// If you have permissions, set chat format
		if (plugin.setupPermissions()) {
			if (RoyalChat.permission.isEnabled()) {
				if (plugin.setupChat()) {
					if (RoyalChat.chat.isEnabled()) {
						/*
						 * {name} - sender.getName() {dispname} -
						 * sender.getDisplayName() {group} -
						 * RoyalChat.permission.getPrimaryGroup(sender) {prefix}
						 * - RoyalChat.permission.getPlayerPrefix(sender)
						 * {suffix} -
						 * RoyalChat.permission.getPlayerSuffix(sender)
						 * {message} - message
						 */

						String format = plugin.formatBase;

						name = sender.getName().replaceAll("(&([a-f0-9]))",
								"\u00A7$2");
						try {
							prefix = RoyalChat.chat.getPlayerPrefix(sender)
									.replaceAll("(&([a-f0-9]))", "\u00A7$2");
						} catch (Exception e) {
							prefix = "";
							log.warning("[RoyalCommands] Could not grab prefix for user"
									+ name);
						}
						try {
							suffix = RoyalChat.chat.getPlayerSuffix(sender)
									.replaceAll("(&([a-f0-9]))", "\u00A7$2");
						} catch (Exception e) {
							suffix = "";
							log.warning("[RoyalCommands] Could not grab suffix for user"
									+ name);
						}
						try {
							group = RoyalChat.permission
									.getPrimaryGroup(sender).replaceAll(
											"(&([a-f0-9]))", "\u00A7$2");
						} catch (Exception e) {
							group = "";
							log.warning("[RoyalCommands] Could not grab group for user"
									+ name);
						}
						try {
							dispname = sender.getDisplayName().replaceAll(
									"(&([a-f0-9]))", "\u00A7$2");
						} catch (Exception e) {
							dispname = "";
							log.warning("[RoyalCommands] Could not grab dispname for user"
									+ name);
						}

						format = format.replace("{name}", name);
						format = format.replace("{dispname}", dispname);
						format = format.replace("{group}", group);
						format = format.replace("{suffix}", suffix);
						format = format.replace("{prefix}", prefix);
						format = format.replace("{message}", message);

						event.setFormat(prefix + group + suffix + " " + name
								+ ChatColor.WHITE + ": " + message);
						event.setFormat(format);
					}
				} else {
					/*
					 * String group =
					 * RoyalChat.permission.getPrimaryGroup(sender)
					 * .replaceAll("(&([a-f0-9]))", "\u00A7$2"); String name =
					 * sender.getDisplayName().replaceAll( "(&([a-f0-9]))",
					 * "\u00A7$2"); event.setFormat(group + " " + name +
					 * message);
					 */
					String name = sender.getDisplayName().replaceAll(
							"(&([a-f0-9]))", "\u00A7$2");
					event.setFormat("LEG: " + name + ChatColor.WHITE + ": "
							+ message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
				}
			}
		} else {
			// If no permissions, just format chat to default
			String name = sender.getDisplayName().replaceAll("(&([a-f0-9]))",
					"\u00A7$2");
			event.setFormat("TREG " + name + ChatColor.WHITE + ": "
					+ message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
		}
	}
}