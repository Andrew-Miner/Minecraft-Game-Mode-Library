package SmokyMiner.MiniGames.Lobby.States;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGLobbyState 
{
	protected final MGManager manager;
	protected final MGLobby lobby;
	
	protected MGMapMetadata map;
	
	public MGLobbyState(MGManager manager, MGLobby lobby, MGMapMetadata map)
	{
		this.manager = manager;
		this.lobby = lobby;
		
		this.map = map;
	}
	
	public void startState()
	{
		
	}
	
	public void endState()
	{
		
	}
	
	public void addPlayer(MGPlayer player)
	{
		
	}
	
	public void removePlayer(MGPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		
		try
		{
			if(p != null)
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		} 
		catch(IllegalStateException e)
		{
			manager.plugin().getLogger().warning(e.toString());
		}
	}
	
	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		MGPlayer pbp = lobby.findPlayer(player);

		if(pbp == null)
			return;

		Player p = Bukkit.getPlayer(pbp.getID());
		lobby.broadcastMessage(ChatColor.WHITE + "<" + p.getName() + "> " + msg);
	}
	
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		Set<Player> recipients = e.getRecipients();
		recipients.clear();
		
		ArrayList<MGPlayer> newRecips = lobby.getPlayers();

		for(MGPlayer pbp : newRecips)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}
	
	public void playerMoveEvent(PlayerMoveEvent e)
	{
		if(!map.contains(e.getTo(), false))
		{
			Player p = e.getPlayer();
			
			p.setVelocity(correctVelocity(p.getLocation(), map.getMapCenter(), .3));
		}
	}
	
	public void entityDamageEntity(EntityDamageByEntityEvent e)
	{
		e.setCancelled(true);
	}
	
	public static Vector correctVelocity(Location player, Location gravity, double scalar)
	{
		Vector g = new Vector(gravity.getX(), gravity.getY(), gravity.getZ());
		Vector l = new Vector(player.getX(), player.getY(), player.getZ());

		g.subtract(l);
		g.normalize();
		g.multiply(scalar);

		return g;
	}
	
	public void broadcastMessage(String msg)
	{
		lobby.broadcastMessage(msg);
	}
	
}