package org.royaldev.royalchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.royaldev.royalchat.RoyalChat;

import java.util.List;

public class RoyalChatPListener implements Listener {

    RoyalChat plugin;

    public RoyalChatPListener(RoyalChat instance) {
        this.plugin = instance;
    }

    public boolean isAuthorized(final Player player, final String node) {
        return player.isOp() || plugin.setupPermissions() && RoyalChat.permission.has(player, node);
    }

    // The chat processor
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) return;

        // Get sent message
        String message = event.getMessage().trim();

        // Get player object of sender
        Player sender = event.getPlayer();

        if (plugin.acd.contains(sender)) {
            String format = plugin.f.formatChat(message, sender, plugin.formatAdmin);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!isAuthorized(p, "rchat.ac")) continue;
                p.sendMessage(format);
            }
            plugin.log.info(format);
            event.setCancelled(true);
            event.setFormat("");
            return;
        }

        if (!plugin.interWorld) {
            event.getRecipients().clear();
            event.getRecipients().addAll(sender.getWorld().getPlayers());
        }

        if (plugin.maxRadius > 0) {
            double radius = plugin.maxRadius;
            List<Entity> ents = sender.getNearbyEntities(radius, radius, radius);
            event.getRecipients().clear();
            for (Entity e : ents) {
                if (!(e instanceof Player)) continue;
                Player t = (Player) e;
                event.getRecipients().add(t);
            }
            if (event.getRecipients().isEmpty()) sender.sendMessage(ChatColor.GRAY + "You feel lonely.");
            event.getRecipients().add(sender);
        }

        String format = plugin.f.formatChat(message, sender, plugin.formatBase);
        if (format.equals("")) event.setCancelled(true);
        event.setFormat(format);
    }
}