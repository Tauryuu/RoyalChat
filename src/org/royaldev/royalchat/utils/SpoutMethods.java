package org.royaldev.royalchat.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.royaldev.royalchat.RoyalChat;

import java.util.HashMap;

public class SpoutMethods {

    public static HashMap<SpoutPlayer, Integer> mess = new HashMap<SpoutPlayer, Integer>();
    public static HashMap<SpoutPlayer, GenericLabel> gls = new HashMap<SpoutPlayer, GenericLabel>();

    public static void updateNumberOnName(Player p, RoyalChat plugin) {
        if (p instanceof SpoutPlayer) {
            SpoutPlayer sp = (SpoutPlayer) p;
            if (!SpoutMethods.gls.containsKey(sp)) return;
            if (plugin.dispCounter) {
                GenericLabel gl = SpoutMethods.gls.get(sp);
                int messages = SpoutMethods.mess.get(sp) + 1;
                SpoutMethods.mess.put(sp, messages);
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
    }

}
