package tk.royaldev.royalchat.utils;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import tk.royaldev.royalchat.RoyalChat;

public class Formatter {

    static RoyalChat plugin;

    public Formatter(RoyalChat instance) {
        plugin = instance;
    }

    public static boolean isAuthorized(CommandSender player, String node) {
        if (player instanceof ConsoleCommandSender) return true;
        if (!(player instanceof Player)) return false;
        Player p = (Player) player;
        return p.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(p, node);
    }

    // Permissions tester for player object
    public static boolean isAuthorized(final Player player, final String node) {
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(player, node);
    }

    public String formatChat(String message, Player sender, String base) {
        String townyprefix = null;
        String townysuffix = null;
        String townytitle = null;
        String townysurname = null;
        String townytown = null;
        String townynation = null;
        if (message.startsWith("&") && message.length() == 2) {
            return "";
        }

        // Test if authorized for color
        if (!isAuthorized(sender, "rchat.color")) {

            // If not, remove color
            message = message.replace("&&", "&");
            message = message.replaceAll("(&([a-f0-9kK]))", "");

        } else if (isAuthorized(sender, "rchat.color")) {

            // If yes, allow color
            message = message.replaceAll("(&([a-f0-9kK]))", "\u00A7$2");

        }

        if (plugin.highlightUrls) {
            message = message.replaceAll("(?i)((http|ftp|https|gopher)://)?[\\w\\.-]*\\.(com|org|net|tk|us|co.uk)(/[\\w/-]*((\\.[\\w-]*)?)|/)?", ChatColor.getByChar("3") + "$0" + ChatColor.WHITE);
        }

        if (message.contains("%")) {
            message = message.replace("%", "%%");
        }

        if (plugin.remCaps) {
            if (!isAuthorized(sender, "rchat.caps")) {
                float a = 0;
                String[] msg = message.replaceAll("\\W", "").split("");
                for (String s : msg) if (s.matches("[A-Z]")) a++;
                float percCaps = a / (float) msg.length;
                float pC = plugin.capsPerc / 100F;
                if (percCaps >= pC) message = message.toLowerCase();
            }
        }

        if (plugin.highlightAtUser) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!VanishUtils.isVanished(p)) {
                    if (message.toLowerCase().contains(p.getName().toLowerCase())) {
                        if (plugin.spout) {
                            SpoutMethods.updateNumberOnName(p, plugin);
                        }
                        if (plugin.useAtSign)
                            message = message.replaceAll("(?i)" + p.getName(), ChatColor.AQUA + "@" + p.getName() + ChatColor.WHITE);
                        else
                            message = message.replaceAll("(?i)" + p.getName(), ChatColor.AQUA + p.getName() + ChatColor.WHITE);
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

        String format = base;

        String name = sender.getName().replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        String prefix;
        try {
            prefix = RoyalChat.chat.getPlayerPrefix(sender).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        } catch (Exception e) {
            prefix = "";
        }

        String suffix;
        try {
            suffix = RoyalChat.chat.getPlayerSuffix(sender).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        } catch (Exception e) {
            suffix = "";
        }

        String group;
        try {
            group = RoyalChat.permission.getPrimaryGroup(sender).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        } catch (Exception e) {
            group = "";
        }

        String dispname;
        try {
            dispname = sender.getDisplayName().replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        } catch (Exception e) {
            dispname = "";
        }

        if (format.contains("{towny")) {
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
        return format;
    }

    public String formatChatSay(String message, CommandSender sender, String base) {
        String townyprefix = null;
        String townysuffix = null;
        String townytitle = null;
        String townysurname = null;
        String townytown = null;
        String townynation = null;
        if (message.startsWith("&") && message.length() == 2) {
            return "";
        }

        // Test if authorized for color
        if (!isAuthorized(sender, "rchat.color")) {

            // If not, remove color
            message = message.replace("&&", "&");
            message = message.replaceAll("(&([a-f0-9kK]))", "");

        } else if (isAuthorized(sender, "rchat.color")) {

            // If yes, allow color
            message = message.replaceAll("(&([a-f0-9kK]))", "\u00A7$2");

        }

        if (plugin.highlightUrls) {
            message = message.replaceAll("(?i)((http|ftp|https|gopher)://)?[\\w\\.-]*\\.(com|org|net|tk|us|co.uk)(/[\\w/-]*((\\.[\\w-]*)?)|/)?", ChatColor.getByChar("3") + "$0" + ChatColor.WHITE);
        }

        if (message.contains("%")) {
            message = message.replace("%", "%%");
        }

        if (plugin.remCaps) {
            if (!isAuthorized(sender, "rchat.caps")) {
                float a = 0;
                String[] msg = message.replaceAll("\\W", "").split("");
                for (String s : msg) if (s.matches("[A-Z]")) a++;
                float percCaps = a / (float) msg.length;
                float pC = plugin.capsPerc / 100F;
                if (percCaps >= pC) message = message.toLowerCase();
            }
        }

        /*if (plugin.highlightAtUser) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!VanishUtils.isVanished(p)) {
                    if (message.toLowerCase().contains(p.getName().toLowerCase())) {
                        if (plugin.spout) {
                            SpoutMethods.updateNumberOnName(p, plugin);
                        }
                        if (plugin.useAtSign)
                            message = message.replaceAll("(?i)" + p.getName(), ChatColor.AQUA + "@" + p.getName() + ChatColor.WHITE);
                        else
                            message = message.replaceAll("(?i)" + p.getName(), ChatColor.AQUA + p.getName() + ChatColor.WHITE);
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
        }*/

        if (plugin.firstWordCapital) {
            String firstLetter = message.substring(0, 1);
            firstLetter = firstLetter.toUpperCase();
            message = firstLetter + message.substring(1);
        }

        // If you have permissions, set chat format
        String format = base;
        String name = sender.getName().replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        String prefix = "";
        String suffix = "";
        String group = "";
        String dispname = "";
        String world = "";

        if (sender instanceof Player) {
            Player p = (Player) sender;
            try {
                prefix = RoyalChat.chat.getPlayerPrefix(p).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
            } catch (Exception e) {
                prefix = "";
            }

            try {
                suffix = RoyalChat.chat.getPlayerSuffix(p).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
            } catch (Exception e) {
                suffix = "";
            }

            try {
                group = RoyalChat.permission.getPrimaryGroup(p).replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
            } catch (Exception e) {
                group = "";
            }

            try {
                dispname = p.getDisplayName().replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
            } catch (Exception e) {
                dispname = "";
            }

            if (format.contains("{towny")) {
                Resident resident = TownyUtils.getResident(p);
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

            world = p.getWorld().getName();
        }

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
        return format;
    }

}