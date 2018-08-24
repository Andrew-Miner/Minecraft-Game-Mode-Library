package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuItem;
import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadataMethods;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.LobbyTools;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyBrowser.LobbyBrowser;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.LobbyListener;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.PBLobbyJoinEvent;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.PBLobbyLeaveEvent;

public class LobbyManager implements LobbyListener
{
	private final PluginManager plugin;
	private LobbyBrowser browser;
	
	private SpawnLobby spawnLobby;
	private ArrayList<PBPlayer> allPlayers;
	private ArrayList<Lobby> lobbies;
	MapMetadata spawnMap;
	
	BukkitTask condenceTask;
	
	public LobbyManager(PluginManager plugin)
	{
		this.plugin = plugin;
		browser = new LobbyBrowser(plugin, this);
		
		spawnLobby = createSpawnLobby();
		allPlayers = new ArrayList<PBPlayer>();
		lobbies = new ArrayList<Lobby>();
		
		condenceTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
		{

			@Override
			public void run() 
			{
				if(lobbies.size() > 1)
					condenceLobbies();
			}
			
		}, 20L * 60L * 2L, 20L * 60L * 2L);
		
		condenceTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
		{

			@Override
			public void run() 
			{
				if(lobbies.size()  < 18)
					createMatch();
			}
			
		}, 20L * 1L, 20L * 1L);
		
		createMatch();
	}
	
	public void close(Location loc)
	{
		condenceTask.cancel();
		
		for(Lobby lobby : lobbies)
		{
			ArrayList<PBPlayer> players = lobby.close();
		}
		
		for(PBPlayer p : allPlayers)
		{
			Player player = Bukkit.getPlayer(p.getID());
			
			if(player != null)
			{
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				player.teleport(loc);
			}
		}
	}
	
	public void close()
	{
		condenceTask.cancel();
		
		for(PBPlayer p : allPlayers)
		{
			Player player = Bukkit.getPlayer(p.getID());
			
			if(player != null)
			{
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
	
	private SpawnLobby createSpawnLobby() 
	{
		String dir = "Spawn";
		
		ArrayList<Double> point = (ArrayList<Double>) plugin.getConfig().getDoubleList(dir + ".Point");
		Location spawn = new Location(null, point.get(0), point.get(1), point.get(2));
		
		ArrayList<Bound> bounds = MapMetadataMethods.loadBounds(plugin.getConfig(), dir + ".Bounds");
		
		spawnMap = new MapMetadata(plugin, "Spawn", "Spawn Lobby", 
				plugin.getServer().getMaxPlayers(), 1, plugin.getConfig().getString(dir + ".World"), bounds, spawn, spawn);
		
		return new SpawnLobby(plugin, spawnMap);
	}

	public PBPlayer PlayerJoined(UUID player)
	{
		PBPlayer info = plugin.getPlayerManager().getPBPlayer(player);
		
		if(info == null)
			throw new IllegalStateException("Failed To Join Lobby: Loading player data...");
		
		Player p = Bukkit.getPlayer(player);
		p.setHealth(20);
		p.setFoodLevel(100);
		allPlayers.add(info);
		spawnLobby.addPlayer(info);
		browser.giveBrowser(p);
		return info;
	}
	
	public void PlayerLeft(UUID player)
	{
		
		Iterator<PBPlayer> it = allPlayers.iterator();
		
		while(it.hasNext())
		{
			PBPlayer p = it.next();
			
			if(p.getID().equals(player))
			{
				it.remove();
				break;
			}
		}
		
		if(isInSpawn(player))
			spawnLobby.removePlayer(player);
		else
		{
			Lobby lobby = getLobbyByPlayer(player);
			
			if(lobby != null)
				lobby.removePlayer(player);
		}
	}
	
	public boolean JoinMatch(UUID player, UUID lobby)
	{
		Lobby l = getLobby(lobby);
		
		if(l != null && l.playerCount() < l.getMaxPlayerCount())
		{
			PBPlayer p = findPlayer(player);
			
			if(p == null)
			{
				try
				{
					p = PlayerJoined(player);
				}
				catch(IllegalStateException e)
				{
					Bukkit.getPlayer(player).sendMessage(ChatColor.GOLD + plugin.logPrefix + ChatColor.YELLOW + " " + e.getMessage().substring(0, e.getMessage().indexOf(':') + 1) + ChatColor.RED + e.getMessage().substring(e.getMessage().indexOf(':') + 1, e.getMessage().length()));
					return false;
				}
			}
			
			spawnLobby.removePlayer(player);
			l.addPlayer(p);

			if(findOpenMatch() == null)
				createMatch();
			
			return true;
		}
		
		return false;
	}
	
	public boolean JoinMatch(UUID player)
	{
		Lobby lobby = findOpenMatch();
		
		if(lobby == null)
			lobby = createMatch();
		
		if(lobby == null)
			return false;
		
		PBPlayer p = findPlayer(player);
		
		if(p == null)
		{
			try
			{
				p = PlayerJoined(player);
			}
			catch(IllegalStateException e)
			{
				Bukkit.getPlayer(player).sendMessage(ChatColor.GOLD + plugin.logPrefix + ChatColor.YELLOW + " " + e.getMessage().substring(0, e.getMessage().indexOf(':') + 1) + ChatColor.RED + e.getMessage().substring(e.getMessage().indexOf(':') + 1, e.getMessage().length()));
				return false;
			}
		}
		
		lobby.addPlayer(p);
		spawnLobby.removePlayer(player);
		
		if(findOpenMatch() == null)
			createMatch();
		
		return true;
	}
	
	private Lobby findOpenMatch()
	{
		ArrayList<Lobby> open = LobbyTools.getOpenLobbies(lobbies);
		
		if(open == null)
			return null;
		
		return LobbyTools.getSmallestPlayerCount(open);
	}
	
	private Lobby createMatch() 
	{
		Lobby lobby = new Lobby(plugin, this, 4, 120, 3, 2, spawnMap);
		lobby.registerListener(this);
		lobbies.add(lobby);
		
		browser.addLobby(lobby);
		
		return lobby;
	}

	public void condenceLobbies()
	{
		ArrayList<Lobby> open = LobbyTools.getOpenLobbies(lobbies);
		
		if(open != null)
		{
			plugin.getLogger().info("Condence Lobbies Called: " + open.size() + " Open Lobbies");
			Collections.sort(open);
			condenceLobbies(open);
			
			open = LobbyTools.getOpenLobbies(lobbies);
			if(open != null)
				plugin.getLogger().info("Condence Lobbies Finished: " + open.size() + " Open Lobbies");
			else
				plugin.getLogger().info("Condence Lobbies Called: No Open Lobbies");
		}
		else
			plugin.getLogger().info("Condence Lobbies Called: No Open Lobbies");
	}

	public boolean condenceLobbies(ArrayList<Lobby> open)
	{
		if(open.size() == 0)
		{
			for(Lobby l : lobbies)
				if(l.playerCount() != l.getMaxPlayerCount())
					return false;
			
			return true;
		}
		else if(open.size() == 1)
			return false;
		
		Lobby largest = open.get(open.size()-1);
		open.remove(largest);
		
		int maxPlayers = largest.getMaxPlayerCount() - largest.playerCount();
		PriorityQueue<Lobby> possibleMatch = new PriorityQueue<Lobby>();
		
		Iterator<Lobby> it = open.iterator();
		
		while(it.hasNext())
		{
			Lobby l = it.next();
			int pCount = l.playerCount();
			
			if(!l.inGame())
			{
				if(pCount == maxPlayers)
				{
					it.remove();
					
					combineLobbies(l, largest);
					
					if(!largest.isFull())
						LobbyTools.insertSorted(open, largest);
					
					return condenceLobbies(open);
				}
				else if(pCount < maxPlayers)
					possibleMatch.add(l);
			}
		}
		
		if(!possibleMatch.isEmpty())
		{
			Lobby possibleLarge = possibleMatch.poll();
			open.remove(possibleLarge);
			
			combineLobbies(possibleLarge, largest);
			
			if(!largest.isFull())
				LobbyTools.insertSorted(open, largest);
		}
		
		return condenceLobbies(open);
	}
	
	private void combineLobbies(Lobby smallL, Lobby largeL) 
	{
		ArrayList<PBPlayer> players = smallL.close();
		browser.removeLobby(smallL);
		
		for(PBPlayer p : players)
			JoinMatch(p.getID(), largeL.getLobbyId());
		
		lobbies.remove(smallL);
	}

	public Lobby getLobbyByPlayer(UUID player)
	{
		Iterator<Lobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			Lobby lobby = it.next();
			
			if(lobby.findPlayer(player) != null)
				return lobby;
		}
		
		return null;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public Lobby getLobby(UUID lobbyId)
	{
		Iterator<Lobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			Lobby lobby = it.next();
			
			if(lobby.equals(lobbyId))
				return lobby;
		}
		
		return null;
	}
	
	public PBPlayer findPlayer(UUID player)
	{
		Iterator<PBPlayer> it = allPlayers.iterator();
		
		while(it.hasNext())
		{
			PBPlayer p = it.next();
			
			if(p.getID().equals(player))
				return p;
		}
		
		return null;
	}
	
	private boolean isInSpawn(UUID player)
	{
		if(spawnLobby.findPlayer(player) != null)
			return true;
		return false;
	}
	
    public LobbyBrowser getBrowser()
    {
    	return browser;
    }
	
// ================================ Events ================================
	
	public void playerMoveEvent(PlayerMoveEvent e)
	{
		UUID id = e.getPlayer().getUniqueId();
		
		if(isInSpawn(id))
			spawnLobby.playerMoved(e);
		else
		{
			Lobby lobby = getLobbyByPlayer(id);
			
			if(lobby != null)
				lobby.playerMoved(e);
		}
	}
	
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		UUID id = e.getPlayer().getUniqueId();
		
		if(isInSpawn(id))
			spawnLobby.playerChatEvent(e);
		else
		{			
			Lobby lobby = getLobbyByPlayer(id);
		
			if(lobby != null)
				lobby.playerChatEvent(e);
			
		}
	}
	
	public void entityDamageEntityEvent(EntityDamageByEntityEvent e)
	{
		UUID id = e.getEntity().getUniqueId();
		
		if(isInSpawn(id))
		{
			
		}
		else
		{
			Lobby lobby = getLobbyByPlayer(id);
			
			if(lobby != null)
				lobby.entityDamagEntityEvent(e);
		}
	}

	public void playerInteractEvent(PlayerInteractEvent e) 
	{
		UUID id = e.getPlayer().getUniqueId();
		
		if(!isInSpawn(id))
		{			
			Lobby lobby = getLobbyByPlayer(id);
		
			if(lobby != null)
				lobby.playerInteractEvent(e);
			
		}
	}
	
	public void foodLevelChangeEvent(FoodLevelChangeEvent e)
	{
		if(findPlayer(e.getEntity().getUniqueId()) != null)
			e.setCancelled(true);
	}
	
	public void entityDamageEvent(EntityDamageEvent e)
	{
		Entity ent = e.getEntity();
		
		if(ent instanceof Player)
		{
			Player p = (Player) ent;
			
			if(p.getHealth() - e.getDamage() <= 0)
				e.setCancelled(true);
		}
	}
	
	public void projectileHitEvent(ProjectileHitEvent e)
	{
		ProjectileSource source = e.getEntity().getShooter();
		
		if(source instanceof Player)
		{
			UUID id = ((Player) source).getUniqueId();
			
			if(isInSpawn(id))
			{
				
			}
			else
			{
				Lobby lobby = getLobbyByPlayer(id);
				
				if(lobby != null)
					lobby.projectileHitEvent(e);
			}
		}
	}
	
	// LobbyEvents ==============================

	@Override
	public void matchStartEvent(Lobby lobby) 
	{
		// TODO Auto-generated method stub
		plugin.getLogger().info("MatchStartEvent Fired!");
	}

	@Override
	public void matchEndEvent(Lobby lobby) 
	{
		// TODO Auto-generated method stub
		plugin.getLogger().info("MatchEndEvent Fired!");
	}

	@Override
	public void pregameStartEvent(Lobby lobby) 
	{
		plugin.getLogger().info("PregameStartEvent Fired!");
		condenceLobbies();
	}

	@Override
	public void pregameEndEvent(Lobby lobby) 
	{
		// TODO Auto-generated method stub
		plugin.getLogger().info("PregameEndEvent Fired!");
	}

	@Override
	public void playerJoinedEvent(PBLobbyJoinEvent e) 
	{
		browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(LobbyTools.buildLobbyLore(e.getLobby()));
		browser.refreshBrowser();
		
		plugin.getLogger().info("PBLobbyJoinEvent Fired!");
	}

	@Override
	public void playerLeaveEvent(PBLobbyLeaveEvent e) 
	{
		MenuItem lobbyItem = browser.getLobbyItem(e.getLobby().getLobbyId());
		
		if(lobbyItem != null)
		{
			browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(LobbyTools.buildLobbyLore(e.getLobby()));
			browser.refreshBrowser();
		}
		
		if(allPlayers.contains(e.getPBPlayer()))
		{
			spawnLobby.addPlayer(e.getPBPlayer());
			browser.giveBrowser(Bukkit.getPlayer(e.getPBPlayer().getID()));
		}
		
		Lobby lobby = e.getLobby();
		
		if(lobby.playerCount() == 0 && LobbyTools.getOpenLobbies(lobbies).size() > 1)
		{
			lobby.close();
			lobbies.remove(lobby);
			browser.removeLobby(lobby);
		}
		
		plugin.getLogger().info("PBLobbyLeaveEvent Fired!");
	}
}
