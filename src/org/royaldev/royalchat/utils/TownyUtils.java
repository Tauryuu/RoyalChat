package org.royaldev.royalchat.utils;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TownyUtils {

    public static Resident getResident(Player player) {
        Resident r;
        Towny t;
        t = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
        if (t == null) return null;
        try {
            r = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (Exception ex) {
            return null;
        }
        return r;
    }

}
