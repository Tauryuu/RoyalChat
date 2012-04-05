package org.royaldev.royalchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
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
                plugin.loadConfiguration();
                cs.sendMessage(ChatColor.AQUA + "RoyalChat" + ChatColor.GREEN + " version " + plugin.version + " reloaded.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("ch")) {
            if (!isAuthorized(cs, "rchat.ch")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            String channelToJoin = args[0];
            String password = null;
            if (args.length > 1) password = args[1];
            ConfigurationSection channels = plugin.getConfig().getConfigurationSection("channels");
            if (channels == null) {
                cs.sendMessage(ChatColor.RED + "There are no channels!");
                return true;
            }
            ConfigurationSection channel = null;
            for (String chan : channels.getValues(true).keySet()) {
                if (!(channels.get(chan) instanceof ConfigurationSection)) continue;
                ConfigurationSection chanc = (ConfigurationSection) channels.get(chan);
                String chanShort = chanc.getString("short");
                if (chanShort == null) continue;
                if (chanShort.equals(channelToJoin)) channel = channels.getConfigurationSection(chan);
            }
            if (channel == null) {
                cs.sendMessage(ChatColor.RED + "No such channel!");
                return true;
            }
            // Channel options
            String name = channel.getString("name");
            boolean usePass = channel.getBoolean("use-password");

            if (!usePass && password != null)
                cs.sendMessage(ChatColor.GRAY + "For future reference, this channel does not require a password.");
            if (usePass) {
                if (password == null) {
                    cs.sendMessage(ChatColor.RED + "This channel requires a password!");
                    return true;
                }
                String chanPass = channel.getString("password");
                if (!password.equals(chanPass)) {
                    cs.sendMessage(ChatColor.RED + "Incorrect password!");
                    return true;
                }
            }
            plugin.c.addToChannel(p, channel.getName());
            cs.sendMessage(ChatColor.BLUE + "Joined " + ChatColor.GRAY + name + ChatColor.BLUE + ".");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("me")) {
            if (!isAuthorized(cs, "rchat.me")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (args.length < 1) {
                cs.sendMessage(cmd.getDescription());
                return false;
            }
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "This command is only available to players!");
                return true;
            }
            Player p = (Player) cs;
            String format = plugin.f.formatChatNoCaps(getFinalArg(args, 0), p, plugin.formatMeBase);
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
            String format = plugin.f.formatChatSay(getFinalArg(args, 0), cs, plugin.formatSay);
            plugin.getServer().broadcastMessage(format);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ac")) {
            if (!isAuthorized(cs, "rchat.ac")) {
                cs.sendMessage(ChatColor.RED + "You don't have permission for that!");
                return true;
            }
            if (args.length < 1) {
                if (!(cs instanceof Player)) {
                    cs.sendMessage(cmd.getDescription());
                    return false;
                }
                Player p = (Player) cs;
                if (plugin.acd.contains(p)) {
                    plugin.acd.remove(p);
                    p.sendMessage(ChatColor.BLUE + "Admin chat " + ChatColor.GRAY + "off" + ChatColor.BLUE + ".");
                } else {
                    plugin.acd.add(p);
                    p.sendMessage(ChatColor.BLUE + "Admin chat " + ChatColor.GRAY + "on" + ChatColor.BLUE + ".");
                }
                return true;
            }
            String message = getFinalArg(args, 0);
            String format = plugin.f.formatChatSay(message, cs, plugin.formatAdmin);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!isAuthorized(p, "rchat.ac")) continue;
                p.sendMessage(format);
            }
            plugin.log.info(ChatColor.stripColor(format));
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