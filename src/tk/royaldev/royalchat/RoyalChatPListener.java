package tk.royaldev.royalchat;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class RoyalChatPListener implements Listener {

    RoyalChat plugin;

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
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(player, node);
    }

    // The chat processor
    @EventHandler(event = PlayerChatEvent.class, priority = EventPriority.NORMAL)
    public void onPlayerChat(PlayerChatEvent event) {

        // Get sent message
        String message = event.getMessage();

        // Get player object of sender
        Player sender = event.getPlayer();

        if (message.startsWith("&") && message.trim().length() == 2) {
            event.setFormat("");
            event.setCancelled(true);
            return;
        }

        // Test if authorized for color
        if (!isAuthorized(sender, "rchat.color")) {

            // If not, remove color
            message = message.replace("&&", "&");
            message = message.replaceAll("(&([a-f0-9]))", "");

        } else if (isAuthorized(sender, "rchat.color")) {

            // If yes, allow color
            message = message.replaceAll("(&([a-f0-9]))", "\u00A7$2");

        }

        if (message.contains("://")) {
            if (plugin.highlightUrls) {
                message = message
                        .replaceAll(
                                "(http|ftp|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-@?^=%&amp;/~\\+#])?",
                                ChatColor.getByChar("3") + "$0" + ChatColor.WHITE);
            }
        }

        if (message.contains("%")) {
            message = message.replace("%", "%%");
        }

        if (plugin.highlightAtUser) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (message.contains(p.getName())) {
                    message = message.replace(p.getName(), ChatColor.AQUA + "@"
                            + p.getName() + ChatColor.WHITE);
                    if (plugin.smokeAtUser) {
                        Location pLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ());
                        for (int i = 0; i < 8; i++) {
                            if (i != 4) {
                                p.getWorld().playEffect(pLoc,
                                        Effect.SMOKE, i);
                                p.getWorld().playEffect(pLoc,
                                        Effect.SMOKE, i);
                            }
                        }
                    }
                }
            }
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

                        String format = plugin.formatBase;

                        String name = sender.getName().replaceAll("(&([a-f0-9]))",
                                "\u00A7$2");
                        String prefix;
                        try {
                            prefix = RoyalChat.chat.getPlayerPrefix(sender)
                                    .replaceAll("(&([a-f0-9]))", "\u00A7$2");
                        } catch (Exception e) {
                            prefix = "";
                            log.warning("[RoyalCommands] Could not grab prefix for user"
                                    + name);
                        }
                        String suffix;
                        try {
                            suffix = RoyalChat.chat.getPlayerSuffix(sender)
                                    .replaceAll("(&([a-f0-9]))", "\u00A7$2");
                        } catch (Exception e) {
                            suffix = "";
                            log.warning("[RoyalCommands] Could not grab suffix for user"
                                    + name);
                        }
                        String group;
                        try {
                            group = RoyalChat.permission
                                    .getPrimaryGroup(sender).replaceAll(
                                            "(&([a-f0-9]))", "\u00A7$2");
                        } catch (Exception e) {
                            group = "";
                            log.warning("[RoyalCommands] Could not grab group for user"
                                    + name);
                        }
                        String dispname;
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

                        String world = sender.getWorld().getName();

                        format = format.replace("{name}", name);
                        format = format.replace("{dispname}", dispname);
                        format = format.replace("{group}", group);
                        format = format.replace("{suffix}", suffix);
                        format = format.replace("{prefix}", prefix);
                        format = format.replace("{world}", world);
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