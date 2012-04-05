package org.royaldev.royalchat.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.royaldev.royalchat.RoyalChat;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class Channeler {

    RoyalChat plugin;

    public Channeler(RoyalChat instance) {
        plugin = instance;
    }

    public static HashMap<Player, String> playerChans = new HashMap<Player, String>();

    public void addToChannel(Player toAdd, String channel) {
        playerChans.put(toAdd, channel);
    }

    public void removeFromChannel(Player toRemove) {
        playerChans.remove(toRemove);
    }

    private void chatRadiusSet(Player sender, double chatRadius, PlayerChatEvent event) {
        List<Entity> ents = sender.getNearbyEntities(chatRadius, chatRadius, chatRadius);
        event.getRecipients().clear();
        for (Entity e : ents) {
            if (!(e instanceof Player)) continue;
            Player t = (Player) e;
            if (!playerChans.containsKey(t) || !playerChans.get(t).equals(playerChans.get(sender))) continue;
            event.getRecipients().add(t);
        }
    }

    public String channelChat(Player sender, String message, PlayerChatEvent event) {
        if (!playerChans.containsKey(sender)) return message;
        String channel = playerChans.get(sender);
        ConfigurationSection cs = plugin.getConfig().getConfigurationSection("channels." + channel);
        if (cs == null) return message;

        // Channel options
        double chatRadius = cs.getDouble("chat-radius");
        boolean global = cs.getBoolean("global");
        boolean interworld = cs.getBoolean("interworld");
        Boolean snoop = cs.getBoolean("snoop");
        String chatFormat = cs.getString("chat-format");

        if (chatRadius > 0) chatRadiusSet(sender, chatRadius, event);
        if (!global) {
            event.getRecipients().clear();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!playerChans.containsKey(p) || !playerChans.get(p).equals(playerChans.get(sender))) continue;
                if (!interworld && !p.getWorld().equals(sender.getWorld())) continue;
                event.getRecipients().add(p);
            }
        }
        if (!interworld)
            for (Player p : event.getRecipients())
                if (!p.getWorld().equals(sender.getWorld())) event.getRecipients().remove(p);

        if (event.getRecipients().isEmpty()) sender.sendMessage(ChatColor.GRAY + "You feel lonely.");
        event.getRecipients().add(sender);
        return plugin.f.formatChat(message, sender, chatFormat);
    }

}
