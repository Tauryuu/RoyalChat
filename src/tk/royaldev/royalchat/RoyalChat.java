package tk.royaldev.royalchat;

/*
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 This plugin was written by jkcclemens <jkc.clemens@gmail.com>.
 If forked and not credited, alert him.
*/

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tk.royaldev.royalchat.listeners.RoyalChatPListener;
import tk.royaldev.royalchat.listeners.SpoutListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class RoyalChat extends JavaPlugin {

    public String version;

    Logger log = Logger.getLogger("Minecraft");

    public static Permission permission = null;
    public static Chat chat = null;

    public boolean spout;
    
    public List<Player> acd = new ArrayList<Player>();

    private final RoyalChatPListener playerListener = new RoyalChatPListener(this);

    public Boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public Boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }
        return (chat != null);
    }

    public String formatBase = null;
    public String formatMeBase = null;
    public String formatSay = null;
    public String formatAdmin = null;
    public Boolean firstWordCapital = null;
    public Boolean highlightAtUser = null;
    public Boolean highlightUrls = null;
    public Boolean smokeAtUser = null;
    public Boolean dispCounter = null;
    public Boolean dispNotify = null;
    public Boolean remCaps = null;
    public Boolean useAtSign = null;
    public Float capsPerc = null;
    public Float maxRadius = null;

    public void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        formatBase = getConfig().getString("chat-format").replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        formatMeBase = getConfig().getString("me-format").replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        formatSay = getConfig().getString("say-format").replaceAll("(&([a-f0-9]kK))", "\u00A7$2");
        formatAdmin = getConfig().getString("admin-format").replaceAll("(&([a-f0-9]kK))", "\u00A7$2");
        firstWordCapital = getConfig().getBoolean("first-word-capital");
        highlightAtUser = getConfig().getBoolean("highlight-at-user");
        highlightUrls = getConfig().getBoolean("highlight-urls");
        smokeAtUser = getConfig().getBoolean("smoke-at-user");
        dispCounter = getConfig().getBoolean("display-messages-counter");
        dispNotify = getConfig().getBoolean("display-messages-achievements");
        remCaps = getConfig().getBoolean("remove-all-caps");
        useAtSign = getConfig().getBoolean("use-at-sign");
        capsPerc = (float) this.getConfig().getDouble("caps-removal-percent");
        maxRadius = (float) this.getConfig().getDouble("chat-radius");
    }

    public void onEnable() {

        spout = getServer().getPluginManager().isPluginEnabled("Spout");

        version = this.getDescription().getVersion();

        loadConfiguration();

        setupChat();
        setupPermissions();

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        if (spout) pm.registerEvents(new SpoutListener(this), this);

        RoyalChatCommands cmdExec = new RoyalChatCommands(this);

        getCommand("rchat").setExecutor(cmdExec);
        getCommand("me").setExecutor(cmdExec);
        getCommand("rclear").setExecutor(cmdExec);
        getCommand("say").setExecutor(cmdExec);

        log.info("[RoyalChat] Version " + this.version + " initiated.");

    }

    public void onDisable() {

        log.info("[RoyalChat] Version " + this.version + " disabled.");

    }

}