package servergate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerGate extends JavaPlugin implements Listener {
	@EventHandler
    public void onServerPing(ServerListPingEvent event) {
        if (this.getConfig().getBoolean("open")) {
        	event.setMotd(this.getConfig().getString("motd-start") + ": Open");
        } else {        
        	event.setMotd(this.getConfig().getString("motd-start") + ": Closed");
        }
    }
	
	@EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!(this.getConfig().getBoolean("open"))) {
        	event.disallow(Result.KICK_OTHER, this.getConfig().getString("closed-join"));
        }
    }
	/*
	@Override
	public void onEnable() {
		//Something
	}
	*/
	@Override
	public void onDisable() {
		this.saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("open")) {
			try {
				if (!(this.getConfig().getBoolean("open"))) {
					this.getConfig().set("open", true);
					this.saveConfig();
				} else {
					sender.sendMessage("The server is already open!");
				}
			} catch (Exception e) {
				getLogger().info("Exception while opening the server: " + e.toString());
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("close")) {
			try {
				if (this.getConfig().getBoolean("open")) {
					this.getConfig().set("open", false);
					this.saveConfig();
				
					for (Player player: Bukkit.getOnlinePlayers()) {
						if (!(player.hasPermission("ServerGate.bypass"))) {
							player.kickPlayer(this.getConfig().getString("kicked-close"));
						}
					}
				}
				} catch (Exception e) {
					getLogger().info("Exception while closing the server: " + e.toString());
				}
		}
		return false;
	}
}
