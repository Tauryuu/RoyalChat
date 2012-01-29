package tk.royaldev.royalchat;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class RoyalChatPListener implements Listener {

    RoyalChat plugin;

    public RoyalChatPListener(RoyalChat instance) {
        this.plugin = instance;
    }

    private String townyprefix = null;
    private String townysuffix = null;
    private String townytitle = null;
    private String townysurname = null;
    private String townytown = null;
    private String townynation = null;

    // Permissions tester for player object
    public boolean isAuthorized(final Player player, final String node) {
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(player, node);
    }

    // The chat processor
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {

        // Get sent message
        String message = event.getMessage().trim();

        // Get player object of sender
        Player sender = event.getPlayer();

        if (message.startsWith("&") && message.length() == 2) {
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

        if (plugin.highlightUrls) {
            message = message.replaceAll("(?i)((http|ftp|https|gopher)://)?[\\w\\.-]*\\.(com|org|net|tk)(/[\\w/-]*((\\.[\\w-]*)?)|/)?", ChatColor.getByChar("3") + "$0" + ChatColor.WHITE);
        }

        if (message.contains("%")) {
            message = message.replace("%", "%%");
        }

        if (plugin.remCaps) {
            float a = 0;
            String[] msg = message.replaceAll("\\W", "").split("");
            for (String s : msg) if (s.matches("[A-Z]")) a++;
            float percCaps = a / (float) msg.length;
            float pC = plugin.capsPerc / 100F;
            if (percCaps >= pC) message = message.toLowerCase();
        }

        if (plugin.highlightAtUser) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!VanishUtils.isVanished(p)) {
                    if (message.toLowerCase().contains(p.getName().toLowerCase())) {
                        if (plugin.spout) {
                            SpoutMethods.updateNumberOnName(p, plugin);
                        }
                        message = message.replaceAll("(?i)" + p.getName(), ChatColor.AQUA + "@" + p.getName() + ChatColor.WHITE);
                        if (plugin.smokeAtUser) {
                            Location pLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ());
                            for (int i = 0; i < 8; i++) {
                                if (i != 4) {
                                    p.getWorld().playEffect(pLoc, Effect.SMOKE, i);
                                    p.getWorld().playEffect(pLoc, Effect.SMOKE, i);
                                }
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

        String format = plugin.formatBase;

        String name = sender.getName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
        String prefix;
        try {
            prefix = RoyalChat.chat.getPlayerPrefix(sender).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        } catch (Exception e) {
            prefix = "";
        }

        String suffix;
        try {
            suffix = RoyalChat.chat.getPlayerSuffix(sender).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        } catch (Exception e) {
            suffix = "";
        }

        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(sender).replaceAll("(&([a-f0-9]))", "\u00A7$2");
        } catch (Exception e) {
            group = "";
        }

        String dispname;
        try {
            dispname = sender.getDisplayName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
        } catch (Exception e) {
            dispname = "";
        }

        if (format.contains("{towny"))

        {
            Resident resident = TownyUtils.getResident(sender);
            if (resident != null) {
                townyprefix = TownyFormatter.getNamePrefix(resident);
                townysuffix = TownyFormatter.getNamePostfix(resident);
                townytitle = resident.getTitle();
                townysurname = resident.getSurname();
                try {
                    townytown = resident.getTown().getName();
                } catch (Exception e) {
                    townytown = "";
                }
                try {
                    townynation = resident.getTown().getNation().getName();
                } catch (Exception e) {
                    townynation = "";
                }
            } else {
                townyprefix = "";
                townysuffix = "";
                townytitle = "";
                townysurname = "";
                townytown = "";
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
            format = format.replace("{townyprefix}", townyprefix);
            format = format.replace("{townysuffix}", townysuffix);
            format = format.replace("{townytitle}", townytitle);
            format = format.replace("{townysurname}", townysurname);
            format = format.replace("{townytown}", townytown);
            format = format.replace("{townynation}", townynation);
        }

        event.setFormat(format);
    }
}