package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates;

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

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;

public class LobbyState 
{
	protected final PluginManager plugin;
	protected final Lobby lobby;
	
	protected MapMetadata map;
	
	public LobbyState(PluginManager plugin, Lobby lobby, MapMetadata map)
	{
		this.plugin = plugin;
		this.lobby = lobby;
		
		this.map = map;
	}
	
	public void startState()
	{
		
	}
	
	public void endState()
	{
		
	}
	
	public void addPlayer(PBPlayer player)
	{
		
	}
	
	public void removePlayer(PBPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		
		try
		{
			if(p != null)
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		} 
		catch(IllegalStateException e)
		{
			plugin.getLogger().warning(e.toString());
		}
	}
	
	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		PBPlayer pbp = lobby.findPlayer(player);

		if(pbp == null)
			return;

		Player p = Bukkit.getPlayer(pbp.getID());
		lobby.broadcastMessage(ChatColor.WHITE + "<" + p.getName() + "> " + msg);
	}
	
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		Set<Player> recipients = e.getRecipients();
		recipients.clear();
		
		ArrayList<PBPlayer> newRecips = lobby.getPlayers();

		for(PBPlayer pbp : newRecips)
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
