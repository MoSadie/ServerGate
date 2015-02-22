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
	private String motd = null;
	@EventHandler
    public void onServerPing(ServerListPingEvent event) {
		if (motd == null) {
			motd = event.getMotd();
		}
        if (this.getConfig().getBoolean("open")) {
        	event.setMotd(motd + ": Open");
        } else {        
        	event.setMotd(motd + ": Closed");
        }
    }
	
	@EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if (!(this.getConfig().getBoolean("open"))) {
        	event.disallow(Result.KICK_OTHER, this.getConfig().getString("message.closed-join"));
        	getLogger().info("Kicked player " + event.getPlayer().getName() + " because the server is closed!"); // TODO Check if this is needed.
        }
    }
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
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
					sender.sendMessage("[ServerGate] Server opened!");
				} else {
					sender.sendMessage("[ServerGate] Server is already open!");
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
							player.kickPlayer(this.getConfig().getString("message.kicked-close"));
						}
					}
					sender.sendMessage("[ServerGate] Server closed!");
				} else 
				{
					sender.sendMessage("[ServerGate] Server is already closed!");
				}
				return true;
			} catch (Exception e) {
				getLogger().info("Exception while closing the server: " + e.toString());
			}
		}
		return false;
	}
}
