package tk.royaldev.royalchat;

import com.palmergames.bukkit.towny.TownyFormatter;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;

public class TownyUtils {

    public static Resident getResident(Player player) {
        try {
            return TownyUniverse.plugin.getTownyUniverse().getResident(player.getName());
        } catch (Exception ex) {
            return null;
        }
    }

}
