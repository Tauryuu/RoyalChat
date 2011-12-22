package tk.royalcraf.royalchat;

import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class RoyalChatPListener extends PlayerListener {

	RoyalChat plugin;

	private String prefix = null;
	private String suffix = null;
	private String group = null;
	private String name = null;
	private String dispname = null;
	private String townyprefix = null;
	private String townysuffix = null;
	private String townytitle = null;
	private String townysurname = null;
	private String townytown = null;
	private String townynation = null;

	public RoyalChatPListener(RoyalChat plugin) {
		this.plugin = plugin;
	}

	private Resident getResident(Player player) {
		try {
			return TownyUniverse.plugin.getTownyUniverse().getResident(
					player.getName());
		} catch (Exception ex) {
			return null;
		}
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
			message = message.replace("&&", "&");
			message = message.replaceAll("(&([a-f0-9]))", "");

		} else if (isAuthorized(sender, "rchat.color")) {

			// If yes, allow color
			message = message.replaceAll("(&([a-f0-9]))", "\u00A7$2");

		}

		if (message.startsWith("&") && message.length() == 2) {
			message = message.replaceAll("(&([a-f0-9]))", "&f");
		}

		if (message.contains("://")) {
			if (plugin.highlightUrls) {
				message = message
						.replaceAll(
								"(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?",
								ChatColor.getByCode(3) + "$0" + ChatColor.WHITE);
			}
		}

		// Replace @<user> with ChatColor.AQUA + @<user>
		/*
		 * if (message.contains("@")) { if (plugin.highlightAtUser) { message =
		 * message.replaceAll("@.[a-zA-Z0-9_-]*", ChatColor.AQUA + "$0" +
		 * ChatColor.WHITE); }
		 */

		if (plugin.highlightAtUser) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				if (message.contains(p.getName())) {
					message = message.replace(p.getName(), ChatColor.AQUA + "@"
							+ p.getName() + ChatColor.WHITE);
					if (plugin.smokeAtUser) {
						for (int i = 0; i < 8; i++) {
							if (i != 4) {
								p.getWorld().playEffect(p.getLocation(),
										Effect.SMOKE, i);
							}
						}
					}
				}
			}
		}

		if (message.contains("%")) {
			message = message.replace("%", "%%");
		}
		if (plugin.firstWordCapital) {
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

						if (format.contains("{towny")) {
							Resident resident = getResident(sender);
							townyprefix = TownyFormatter
									.getNamePrefix(resident);
							townysuffix = TownyFormatter
									.getNamePostfix(resident);
							townytitle = resident.getTitle();
							townysurname = resident.getSurname();
							try {
								townytown = resident.getTown().getName();
							} catch (Exception e) {
								townytown = "";
							}
							try {
								townynation = resident.getTown().getNation()
										.getName();
							} catch (Exception e) {
								townynation = "";
							}
						}

						format = format.replace("{name}", name);
						format = format.replace("{dispname}", dispname);
						format = format.replace("{group}", group);
						format = format.replace("{suffix}", suffix);
						format = format.replace("{prefix}", prefix);
						format = format.replace("{message}", message);
						if (format.contains("{towny")) {
							format = format.replace("{townyprefix}",
									townyprefix);
							format = format.replace("{townysuffix}",
									townysuffix);
							format = format.replace("{townytitle}", townytitle);
							format = format.replace("{townysurname}",
									townysurname);
							format = format.replace("{townytown}", townytown);
							format = format.replace("{townynation}",
									townynation);
						}

						event.setFormat(format);
					}
				} else {
					String name = sender.getDisplayName().replaceAll(
							"(&([a-f0-9]))", "\u00A7$2");
					event.setFormat(name + ChatColor.WHITE + ": "
							+ message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
				}
			}
		} else {
			// If no permissions, just format chat to default
			String name = sender.getDisplayName().replaceAll("(&([a-f0-9]))",
					"\u00A7$2");
			event.setFormat(name + ChatColor.WHITE + ": "
					+ message.replaceAll("(&([a-f0-9]))", "\u00A7$2"));
		}
	}
}