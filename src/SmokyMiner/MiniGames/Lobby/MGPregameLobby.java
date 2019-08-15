package SmokyMiner.MiniGames.Lobby;

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

import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Maps.MGMapMethods;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGPregameLobby 
{
	private ArrayList<MGPlayer> players;
	private final int maxPlayers;
	private Location spawnLoc;
	private MGMapMetadata spawnMap;
	private final MGManager manager;
	
	public MGPregameLobby(MGManager manager, MGMapMetadata map)
	{
		this.manager = manager;
		players = new ArrayList<MGPlayer>();
		maxPlayers = this.manager.plugin().getServer().getMaxPlayers();
		this.spawnMap = map;
		this.spawnLoc = map.getMapCenter();
		spawnLoc.setWorld(Bukkit.getWorld(spawnMap.getWorldName()));
		
		this.manager.plugin().getLogger().info("------------------");
		this.manager.plugin().getLogger().info("Spawn Bounds: ");
		MGMapMethods.printBounds(this.manager.plugin(), spawnMap.getDefaultBounds());
	}
	
	public boolean addPlayer(MGPlayer player)
	{
		if(players.size() >= maxPlayers)
			return false;
		
		players.add(player);
		
		player.setPregame();
		player.spawn(spawnLoc, null);
		
		return true;
	}
  
  public boolean removePlayer(MGPlayer player)
  {
    if(removePlayer(player.getID()) == null)
      return false;
    return true;
  }
	
	public MGPlayer removePlayer(UUID player)
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			MGPlayer pb = it.next();
			if(pb.getID() == player)
			{
				it.remove();
				return pb;
			}
		}
		return null;
	}
	
	public MGPlayer findPlayer(UUID player)
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			MGPlayer pb = it.next();
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
		
		for(MGPlayer pbp : players)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}
	
	public ArrayList<MGPlayer> getPlayers() { return players; }
}