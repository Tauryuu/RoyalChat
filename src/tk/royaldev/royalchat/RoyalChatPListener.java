package tk.royaldev.royalchat;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

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

    /*@EventHandler()
    public void onButton(ButtonClickEvent e) {
        Button b = e.getButton();
        if (!(b.getId() == plugin.sObj.get("chatButton"))) return;
        SpoutPlayer sp = e.getPlayer();
        sp.openScreen(ScreenType.CHAT_SCREEN);
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if (!plugin.dispCounter) return;
        Player p = e.getPlayer();
        if (p instanceof SpoutPlayer) {
            SpoutPlayer sp = (SpoutPlayer) p;
            GenericLabel gl = new GenericLabel();
            if (!plugin.mess.containsKey(sp)) {
                plugin.mess.put(sp, 0);
            }
            int messages = plugin.mess.get(sp);
            gl.setText(messages + " new messages.").setTextColor(new Color(255, 255, 255)).setX(3).setY(0).setAnchor(WidgetAnchor.BOTTOM_LEFT);
            gl.setAlign(WidgetAnchor.BOTTOM_LEFT);
            plugin.gls.put(sp, gl);
            sp.getMainScreen().attachWidget(plugin, gl);
        }
    }

    @EventHandler()
    public void scrOpen(ScreenOpenEvent e) {
        if (!plugin.dispCounter) return;
        SpoutPlayer sp = e.getPlayer();
        ScreenType st = e.getScreenType();
        if (st != ScreenType.CHAT_SCREEN) return;
        plugin.mess.put(sp, 0);
        plugin.gls.get(sp).setText("0 new messages.").setTextColor(new Color(255, 255, 255)).setVisible(false);
    }

    @EventHandler()
    public void scrClose(ScreenCloseEvent e) {
        if (!plugin.dispCounter) return;
        SpoutPlayer sp = e.getPlayer();
        ScreenType st = e.getScreenType();
        if (st != ScreenType.CHAT_SCREEN) return;
        plugin.gls.get(sp).setVisible(true);
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
            message = message.replaceAll("((http|ftp|https|gopher)://)?[a-zA-Z0-9\\._-]*\\.(com|org|net|tk)(/[a-zA-Z0-9_-]*(\\.[a-zA-Z0-9_-]*)|/)?", ChatColor.getByChar("3") + "$0" + ChatColor.WHITE);
        }

        if (message.contains("%")) {
            message = message.replace("%", "%%");
        }

        if (plugin.highlightAtUser) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!plugin.isVanished(p)) {
                    if (message.contains(p.getName())) {
                        if (p instanceof SpoutPlayer) {
                            SpoutPlayer sp = (SpoutPlayer) p;
                            if (!plugin.gls.containsKey(sp)) return;
                            if (plugin.dispCounter) {
                                GenericLabel gl = plugin.gls.get(sp);
                                int messages = plugin.mess.get(sp) + 1;
                                plugin.mess.put(sp, messages);
                                if (messages == 1) {
                                    gl.setText(messages + " new message.").setTextColor(new Color(255, 0, 0));
                                } else {
                                    gl.setText(messages + " new messages.").setTextColor(new Color(255, 0, 0));
                                }
                            }
                            if (plugin.dispNotify) {
                                sp.sendNotification("Name Mention!", "Your name was mentioned", Material.APPLE);
                            }
                        }
                        message = message.replace(p.getName(), ChatColor.AQUA + "@" + p.getName() + ChatColor.WHITE);
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
        event.setFormat(format);
    }
}