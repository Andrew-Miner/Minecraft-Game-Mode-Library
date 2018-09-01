package SmokyMiner.MiniGames.Lobby;

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

import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;
import SmokyMiner.MiniGames.Lobby.Browser.MGLobbyBrowser;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Maps.MGBound;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Maps.MGMapMethods;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGLobbyManager implements MGLobbyListener
{
	private final MGManager manager;
	private MGLobbyBrowser browser;
	
	private MGSpawnLobby spawnLobby;
	private ArrayList<MGPlayer> allPlayers;
	private ArrayList<MGLobby> lobbies;
	MGMapMetadata spawnMap;
	
	BukkitTask condenceTask;
	
	public MGLobbyManager(MGManager manager)
	{
		this.manager = manager;
		browser = new MGLobbyBrowser(manager, this);
		
		spawnLobby = createSpawnLobby();
		allPlayers = new ArrayList<MGPlayer>();
		lobbies = new ArrayList<MGLobby>();
		
		condenceTask = Bukkit.getScheduler().runTaskTimer(manager.plugin(), new Runnable()
		{

			@Override
			public void run() 
			{
				if(lobbies.size() > 1)
					condenceLobbies();
			}
			
		}, 20L * 60L * 2L, 20L * 60L * 2L);
		
		condenceTask = Bukkit.getScheduler().runTaskTimer(manager.plugin(), new Runnable()
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
		
		for(MGLobby lobby : lobbies)
		{
			ArrayList<MGPlayer> players = lobby.close();
		}
		
		for(MGPlayer p : allPlayers)
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
		
		for(MGPlayer p : allPlayers)
		{
			Player player = Bukkit.getPlayer(p.getID());
			
			if(player != null)
			{
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
			}
		}
	}
	
	private MGSpawnLobby createSpawnLobby() 
	{
		String dir = "Spawn";
		
		ArrayList<Double> point = (ArrayList<Double>) manager.getConfig().getDoubleList(dir + ".Point");
		Location spawn = new Location(null, point.get(0), point.get(1), point.get(2));
		
		ArrayList<MGBound> bounds = MGMapMethods.loadBounds(manager.getConfig(), dir + ".Bounds");
		
		spawnMap = new MGMapMetadata(manager, "Spawn", "Spawn Lobby", 
				manager.plugin().getServer().getMaxPlayers(), 1, manager.getConfig().getString(dir + ".World"), bounds, spawn, spawn);
		
		return new MGSpawnLobby(manager, spawnMap);
	}

	public MGPlayer PlayerJoined(UUID player)
	{
		MGPlayer info = manager.getPlayerManager().getMGPlayer(player);
		
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
		
		Iterator<MGPlayer> it = allPlayers.iterator();
		
		while(it.hasNext())
		{
			MGPlayer p = it.next();
			
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
			MGLobby lobby = getLobbyByPlayer(player);
			
			if(lobby != null)
				lobby.removePlayer(player);
		}
	}
	
	public boolean JoinMatch(UUID player, UUID lobby)
	{
		MGLobby l = getLobby(lobby);
		
		if(l != null && l.playerCount() < l.getMaxPlayerCount())
		{
			MGPlayer p = findPlayer(player);
			
			if(p == null)
			{
				try
				{
					p = PlayerJoined(player);
				}
				catch(IllegalStateException e)
				{
					Bukkit.getPlayer(player).sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.YELLOW + " " + e.getMessage().substring(0, e.getMessage().indexOf(':') + 1) + ChatColor.RED + e.getMessage().substring(e.getMessage().indexOf(':') + 1, e.getMessage().length()));
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
		MGLobby lobby = findOpenMatch();
		
		if(lobby == null)
			lobby = createMatch();
		
		if(lobby == null)
			return false;
		
		MGPlayer p = findPlayer(player);
		
		if(p == null)
		{
			try
			{
				p = PlayerJoined(player);
			}
			catch(IllegalStateException e)
			{
				Bukkit.getPlayer(player).sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.YELLOW + " " + e.getMessage().substring(0, e.getMessage().indexOf(':') + 1) + ChatColor.RED + e.getMessage().substring(e.getMessage().indexOf(':') + 1, e.getMessage().length()));
				return false;
			}
		}
		
		lobby.addPlayer(p);
		spawnLobby.removePlayer(player);
		
		if(findOpenMatch() == null)
			createMatch();
		
		return true;
	}
	
	private MGLobby findOpenMatch()
	{
		ArrayList<MGLobby> open = MGLobbyTools.getOpenLobbies(lobbies);
		
		if(open == null)
			return null;
		
		return MGLobbyTools.getSmallestPlayerCount(open);
	}
	
	private MGLobby createMatch() 
	{
		MGLobby lobby = new MGLobby(manager, this, 4, 120, 3, 2, spawnMap);
		lobby.registerListener(this);
		lobbies.add(lobby);
		
		browser.addLobby(lobby);
		
		return lobby;
	}

	public void condenceLobbies()
	{
		ArrayList<MGLobby> open = MGLobbyTools.getOpenLobbies(lobbies);
		
		if(open != null)
		{
			manager.plugin().getLogger().info("Condence Lobbies Called: " + open.size() + " Open Lobbies");
			Collections.sort(open);
			condenceLobbies(open);
			
			open = MGLobbyTools.getOpenLobbies(lobbies);
			if(open != null)
				manager.plugin().getLogger().info("Condence Lobbies Finished: " + open.size() + " Open Lobbies");
			else
				manager.plugin().getLogger().info("Condence Lobbies Called: No Open Lobbies");
		}
		else
			manager.plugin().getLogger().info("Condence Lobbies Called: No Open Lobbies");
	}

	public boolean condenceLobbies(ArrayList<MGLobby> open)
	{
		if(open.size() == 0)
		{
			for(MGLobby l : lobbies)
				if(l.playerCount() != l.getMaxPlayerCount())
					return false;
			
			return true;
		}
		else if(open.size() == 1)
			return false;
		
		MGLobby largest = open.get(open.size()-1);
		open.remove(largest);
		
		int maxPlayers = largest.getMaxPlayerCount() - largest.playerCount();
		PriorityQueue<MGLobby> possibleMatch = new PriorityQueue<MGLobby>();
		
		Iterator<MGLobby> it = open.iterator();
		
		while(it.hasNext())
		{
			MGLobby l = it.next();
			int pCount = l.playerCount();
			
			if(!l.inGame())
			{
				if(pCount == maxPlayers)
				{
					it.remove();
					
					combineLobbies(l, largest);
					
					if(!largest.isFull())
						MGLobbyTools.insertSorted(open, largest);
					
					return condenceLobbies(open);
				}
				else if(pCount < maxPlayers)
					possibleMatch.add(l);
			}
		}
		
		if(!possibleMatch.isEmpty())
		{
			MGLobby possibleLarge = possibleMatch.poll();
			open.remove(possibleLarge);
			
			combineLobbies(possibleLarge, largest);
			
			if(!largest.isFull())
				MGLobbyTools.insertSorted(open, largest);
		}
		
		return condenceLobbies(open);
	}
	
	private void combineLobbies(MGLobby smallL, MGLobby largeL) 
	{
		ArrayList<MGPlayer> players = smallL.close();
		browser.removeLobby(smallL);
		
		for(MGPlayer p : players)
			JoinMatch(p.getID(), largeL.getLobbyId());
		
		lobbies.remove(smallL);
	}

	public MGLobby getLobbyByPlayer(UUID player)
	{
		Iterator<MGLobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			MGLobby lobby = it.next();
			
			if(lobby.findPlayer(player) != null)
				return lobby;
		}
		
		return null;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public MGLobby getLobby(UUID lobbyId)
	{
		Iterator<MGLobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			MGLobby lobby = it.next();
			
			if(lobby.equals(lobbyId))
				return lobby;
		}
		
		return null;
	}
	
	public MGPlayer findPlayer(UUID player)
	{
		Iterator<MGPlayer> it = allPlayers.iterator();
		
		while(it.hasNext())
		{
			MGPlayer p = it.next();
			
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
	
    public MGLobbyBrowser getBrowser()
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
			MGLobby lobby = getLobbyByPlayer(id);
			
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
			MGLobby lobby = getLobbyByPlayer(id);
		
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
			MGLobby lobby = getLobbyByPlayer(id);
			
			if(lobby != null)
				lobby.entityDamagEntityEvent(e);
		}
	}

	public void playerInteractEvent(PlayerInteractEvent e) 
	{
		UUID id = e.getPlayer().getUniqueId();
		
		if(!isInSpawn(id))
		{			
			MGLobby lobby = getLobbyByPlayer(id);
		
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
				MGLobby lobby = getLobbyByPlayer(id);
				
				if(lobby != null)
					lobby.projectileHitEvent(e);
			}
		}
	}
	
	// LobbyEvents ==============================

	@Override
	public void matchStartEvent(MGLobby lobby) 
	{
		// TODO Auto-generated method stub
		manager.plugin().getLogger().info("MatchStartEvent Fired!");
	}

	@Override
	public void matchEndEvent(MGLobby lobby) 
	{
		// TODO Auto-generated method stub
		manager.plugin().getLogger().info("MatchEndEvent Fired!");
	}

	@Override
	public void pregameStartEvent(MGLobby lobby) 
	{
		manager.plugin().getLogger().info("PregameStartEvent Fired!");
		condenceLobbies();
	}

	@Override
	public void pregameEndEvent(MGLobby lobby) 
	{
		// TODO Auto-generated method stub
		manager.plugin().getLogger().info("PregameEndEvent Fired!");
	}

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e) 
	{
		browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(MGLobbyTools.buildLobbyLore(e.getLobby()));
		browser.refreshBrowser();
		
		manager.plugin().getLogger().info("PBLobbyJoinEvent Fired!");
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e) 
	{
		MGMenuItem lobbyItem = browser.getLobbyItem(e.getLobby().getLobbyId());
		
		if(lobbyItem != null)
		{
			browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(MGLobbyTools.buildLobbyLore(e.getLobby()));
			browser.refreshBrowser();
		}
		
		if(allPlayers.contains(e.getPBPlayer()))
		{
			spawnLobby.addPlayer(e.getPBPlayer());
			browser.giveBrowser(Bukkit.getPlayer(e.getPBPlayer().getID()));
		}
		
		MGLobby lobby = e.getLobby();
		
		if(lobby.playerCount() == 0 && MGLobbyTools.getOpenLobbies(lobbies).size() > 1)
		{
			lobby.close();
			lobbies.remove(lobby);
			browser.removeLobby(lobby);
		}
		
		manager.plugin().getLogger().info("PBLobbyLeaveEvent Fired!");
	}
}
