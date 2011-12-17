package tk.royalcraf.royalchat;

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

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RoyalChat extends JavaPlugin {

	public String version = "0.0.4";

	Logger log = Logger.getLogger("Minecraft");

	public static Permission permission = null;
	public static Chat chat = null;

	private final RoyalChatPListener playerListener = new RoyalChatPListener(
			this);

	public Boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public Boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer()
				.getServicesManager().getRegistration(
						net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}

		return (chat != null);
	}

	public String formatBase = null;
	public String formatMeBase = null;
	public Boolean firstWordCapital = null;
	public Boolean highlightAtUser = null;
	public Boolean highlightUrls = null;

	public void loadConfiguration() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		formatBase = this.getConfig().getString("chat-format")
				.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		formatMeBase = this.getConfig().getString("me-format")
				.replaceAll("(&([a-f0-9]))", "\u00A7$2");
		firstWordCapital = this.getConfig().getBoolean("first-word-capital");
		highlightAtUser = this.getConfig().getBoolean("highlight-at-user");
		highlightUrls = this.getConfig().getBoolean("highlight-urls");
		/*
		 * File file = new File(this.getDataFolder() + "/"); boolean exists =
		 * file.exists(); if (!exists) { try { boolean success = new
		 * File(this.getDataFolder() + "/").mkdir(); if (success) {
		 * log.info("[RoyalChat] Created userdata directory."); } } catch
		 * (Exception e) {
		 * log.severe("[RoyalChat] Failed to make userdata directory!");
		 * log.severe(e.getMessage()); } }
		 */
	}

	protected FileConfiguration config;

	public void onEnable() {

		loadConfiguration();

		if (!this.setupPermissions()) {
			log.info("[RoyalChat] No permissions plugin found! Cannot set group names! Will use default chat formatting.");
		}
		if (!this.setupChat()) {
			log.info("[RoyalChat] No permissions plugin found! Cannot set up prefixes or suffixes! Will use default chat formatting.");
		}

		RoyalChatCommands cmdExec = new RoyalChatCommands(this);

		getCommand("rchat").setExecutor(cmdExec);
		getCommand("me").setExecutor(cmdExec);
		getCommand("clear").setExecutor(cmdExec);

		PluginManager pm = this.getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener,
				Event.Priority.Normal, this);

		log.info("[RoyalChat] Version " + this.version + " initiated.");

	}

	public void onDisable() {

		log.info("[RoyalChat] Version " + this.version + " disabled.");

	}

}