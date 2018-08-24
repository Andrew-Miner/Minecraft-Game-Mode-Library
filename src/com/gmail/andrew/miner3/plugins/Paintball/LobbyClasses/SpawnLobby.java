package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadataMethods;

public class SpawnLobby 
{
	private ArrayList<PBPlayer> players;
	private final int maxPlayers;
	private Location spawnLoc;
	private MapMetadata spawnMap;
	private final PluginManager plugin;
	
	public SpawnLobby(PluginManager plugin, MapMetadata spawnMap)
	{
		this.plugin = plugin;
		players = new ArrayList<PBPlayer>();
		maxPlayers = plugin.getServer().getMaxPlayers();
		this.spawnLoc = spawnMap.getMapCenter();
		this.spawnMap = spawnMap;
		spawnLoc.setWorld(Bukkit.getWorld(spawnMap.getWorldName()));
		
		plugin.getLogger().info("------------------");
		plugin.getLogger().info("Spawn Bounds: ");
		MapMetadataMethods.printBounds(plugin, spawnMap.getDefaultBounds());
		
	}
	
	public boolean addPlayer(PBPlayer player)
	{
		if(players.size() >= maxPlayers)
			return false;
		
		players.add(player);
		
		player.setPregame();
		player.spawn(spawnLoc, null);
		
		return true;
	}
	
	public PBPlayer removePlayer(UUID player)
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			PBPlayer pb = it.next();
			if(pb.getID() == player)
			{
				it.remove();
				return pb;
			}
		}
		return null;
	}
	
	public PBPlayer findPlayer(UUID player)
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			PBPlayer pb = it.next();
			if(pb.getID() == player)
				return pb;
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	public void playerMoved(PlayerMoveEvent e)
	{
		
		Player p = e.getPlayer();
		
		if(p != null || p == null)
			return;
		
		if(!spawnMap.contains(p.getLocation(), false))
		{
			Location gravity = spawnMap.getMapCenter();
			Location pLoc = p.getLocation();
			
			Vector g = new Vector(gravity.getX(), gravity.getY(), gravity.getZ());
			Vector l = new Vector(pLoc.getX(), pLoc.getY(), pLoc.getZ());
			
			g.subtract(l);
			g.normalize();
			
			try
			{
				g.multiply(.3);
				p.setVelocity(g);
			} catch (Exception exc) { }
		}
	}
	
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		Set<Player> recipients = e.getRecipients();
		recipients.clear();
		
		for(PBPlayer pbp : players)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}
	
	public ArrayList<PBPlayer> getPlayers() { return players; }
}
