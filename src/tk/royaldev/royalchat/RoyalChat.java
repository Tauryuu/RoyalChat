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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tk.royaldev.royalchat.listeners.RoyalChatPListener;
import tk.royaldev.royalchat.listeners.SpoutListener;

import java.util.logging.Logger;

public class RoyalChat extends JavaPlugin {

    public String version;

    Logger log = Logger.getLogger("Minecraft");

    public static Permission permission = null;
    public static Chat chat = null;

    public Boolean spout;

    //public HashMap<String, UUID> sObj = new HashMap<String, UUID>();

    private final RoyalChatPListener playerListener = new RoyalChatPListener(this);
    private final SpoutListener spoutListener = new SpoutListener(this);

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
    public Boolean firstWordCapital = null;
    public Boolean highlightAtUser = null;
    public Boolean highlightUrls = null;
    public Boolean smokeAtUser = null;
    public Boolean dispCounter = null;
    public Boolean dispNotify = null;
    public Boolean remCaps = null;
    public Boolean useAtSign = null;
    public Float capsPerc = null;

    public void loadConfiguration() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        formatBase = this.getConfig().getString("chat-format").replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        formatMeBase = this.getConfig().getString("me-format").replaceAll("(&([a-f0-9kK]))", "\u00A7$2");
        formatSay = this.getConfig().getString("say-format").replaceAll("(&([a-f0-9]kK))", "\u00A7$2");
        firstWordCapital = this.getConfig().getBoolean("first-word-capital");
        highlightAtUser = this.getConfig().getBoolean("highlight-at-user");
        highlightUrls = this.getConfig().getBoolean("highlight-urls");
        smokeAtUser = this.getConfig().getBoolean("smoke-at-user");
        dispCounter = this.getConfig().getBoolean("display-messages-counter");
        dispNotify = this.getConfig().getBoolean("display-messages-achievements");
        remCaps = this.getConfig().getBoolean("remove-all-caps");
        useAtSign = this.getConfig().getBoolean("use-at-sign");
        capsPerc = (float) this.getConfig().getInt("caps-removal-percent");
    }

    public void onEnable() {

        spout = getServer().getPluginManager().isPluginEnabled("Spout");

        version = this.getDescription().getVersion();

        loadConfiguration();

        setupChat();
        setupPermissions();

        PluginManager pm = this.getServer().getPluginManager();

        pm.registerEvents(playerListener, this);
        if (spout) pm.registerEvents(spoutListener, this);

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