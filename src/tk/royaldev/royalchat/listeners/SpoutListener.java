package tk.royaldev.royalchat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.event.screen.ScreenOpenEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import tk.royaldev.royalchat.RoyalChat;
import tk.royaldev.royalchat.utils.SpoutMethods;

public class SpoutListener implements Listener {

    RoyalChat plugin;

    public SpoutListener(RoyalChat instance) {
        this.plugin = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        if (!plugin.spout || !plugin.dispCounter) return;
        Player p = e.getPlayer();
        if (!(p instanceof SpoutPlayer)) return;
        SpoutPlayer sp = (SpoutPlayer) p;
        GenericLabel gl = new GenericLabel();
        if (!SpoutMethods.mess.containsKey(sp)) {
            SpoutMethods.mess.put(sp, 0);
        }
        int messages = SpoutMethods.mess.get(sp);
        gl.setText(messages + " new messages.").setTextColor(new Color(255, 255, 255)).setX(3).setY(0).setAnchor(WidgetAnchor.BOTTOM_LEFT);
        gl.setAlign(WidgetAnchor.BOTTOM_LEFT);
        SpoutMethods.gls.put(sp, gl);
        sp.getMainScreen().attachWidget(plugin, gl);
    }

    @EventHandler()
    public void scrOpen(ScreenOpenEvent e) {
        if (!plugin.spout || !plugin.dispCounter) return;
        SpoutPlayer sp = e.getPlayer();
        ScreenType st = e.getScreenType();
        if (st != ScreenType.CHAT_SCREEN) return;
        SpoutMethods.mess.put(sp, 0);
        SpoutMethods.gls.get(sp).setText("0 new messages.").setTextColor(new Color(255, 255, 255)).setVisible(false);
    }

    @EventHandler()
    public void scrClose(ScreenCloseEvent e) {
        if (!plugin.spout || !plugin.dispCounter) return;
        SpoutPlayer sp = e.getPlayer();
        ScreenType st = e.getScreenType();
        if (st != ScreenType.CHAT_SCREEN) return;
        SpoutMethods.gls.get(sp).setVisible(true);
    }

}
