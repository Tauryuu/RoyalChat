package tk.royaldev.royalchat;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
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

    public boolean isAuthorized(final CommandSender player, final String node) {
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has((Player) player, node);
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
    public boolean onCommand(CommandSender cs, Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("rchat")) {
            if (!isAuthorized(cs, "rchat.rchat")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            } else {
                plugin.reloadConfig();
                plugin.formatBase = plugin.getConfig().getString("chat-format").replaceAll("(&([a-f0-9]))", "\u00A7$2");
                plugin.formatMeBase = plugin.getConfig().getString("me-format").replaceAll("(&([a-f0-9]))", "\u00A7$2");
                plugin.formatSay = plugin.getConfig().getString("say-format").replaceAll("(&([a-f0-9]))", "\u00A7$2");
                plugin.firstWordCapital = plugin.getConfig().getBoolean("first-word-capital");
                plugin.highlightAtUser = plugin.getConfig().getBoolean("highlight-at-user");
                plugin.smokeAtUser = plugin.getConfig().getBoolean("smoke-at-user");
                plugin.highlightUrls = plugin.getConfig().getBoolean("highlight-urls");
                cs.sendMessage(ChatColor.AQUA + "RoyalChat" + ChatColor.GREEN + " version " + plugin.version + " reloaded.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("me")) {
            if (!isAuthorized(cs, "rchat.me")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }

            String message = getFinalArg(args, 0).trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");

            String format = plugin.formatMeBase;
            String name = cs.getName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
            String prefix;
            try {
                prefix = RoyalChat.chat.getPlayerPrefix((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                prefix = "";
            }
            String suffix;
            try {
                suffix = RoyalChat.chat.getPlayerSuffix((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                suffix = "";
            }
            String group;
            try {
                group = RoyalChat.permission.getPrimaryGroup((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                group = "";
            }
            String dispname;
            try {
                dispname = ((Player) cs).getDisplayName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                if (!(cs instanceof Player)) {
                    dispname = cs.getName();
                } else {
                    dispname = "";
                }
            }

            String townyprefix = null;
            String townysuffix = null;
            String townytitle = null;
            String townysurname = null;
            String townytown = null;
            String townynation = null;

            if (format.contains("{towny")) {
                Resident resident;
                try {
                    resident = TownyUtils.getResident((Player) cs);
                } catch (Exception e) {
                    resident = null;
                }
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

            String world;
            try {
                world = ((Player) cs).getWorld().getName();
            } catch (Exception e) {
                world = "";
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
            plugin.getServer().broadcastMessage(format);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("say")) {
            if (!isAuthorized(cs, "rchat.say")) {
                cs.sendMessage("You don't have permission for that!");
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String format = plugin.formatSay;
            String message = getFinalArg(args, 0).trim().replaceAll("(&([a-f0-9]))", "\u00A7$2");
            String name = cs.getName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
            String prefix;
            try {
                prefix = RoyalChat.chat.getPlayerPrefix((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                prefix = "";
            }
            String suffix;
            try {
                suffix = RoyalChat.chat.getPlayerSuffix((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                suffix = "";
            }
            String group;
            try {
                group = RoyalChat.permission.getPrimaryGroup((Player) cs).replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                group = "";
            }
            String dispname;
            try {
                dispname = ((Player) cs).getDisplayName().replaceAll("(&([a-f0-9]))", "\u00A7$2");
            } catch (Exception e) {
                if (!(cs instanceof Player)) {
                    dispname = cs.getName();
                } else {
                    dispname = "";
                }
            }

            String townyprefix = null;
            String townysuffix = null;
            String townytitle = null;
            String townysurname = null;
            String townytown = null;
            String townynation = null;

            if (format.contains("{towny")) {
                Resident resident;
                try {
                    resident = TownyUtils.getResident((Player) cs);
                } catch (Exception e) {
                    resident = null;
                }
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

            String world;
            try {
                world = ((Player) cs).getWorld().getName();
            } catch (Exception e) {
                world = "";
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
            plugin.getServer().broadcastMessage(format);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("rclear")) {
            if (!isAuthorized(cs, "rchat.rclear")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            for (int i = 0; i < 20; ) {
                cs.sendMessage("");
                i++;
            }
            return true;
        }
        return false;
    }
}