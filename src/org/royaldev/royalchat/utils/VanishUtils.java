package org.royaldev.royalchat.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.vanish.VanishPlugin;

public class VanishUtils {

    public static boolean isVanished(Player p) {
        VanishPlugin vp;
        vp = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");
        return !(vp == null) && vp.getManager().isVanished(p.getName());
    }

}
