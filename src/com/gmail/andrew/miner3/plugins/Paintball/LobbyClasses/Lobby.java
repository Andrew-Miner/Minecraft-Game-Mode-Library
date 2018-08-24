package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.SpawnKit;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.LobbyListener;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.PBLobbyJoinEvent;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents.PBLobbyLeaveEvent;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.MatchState;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.PregameState;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.RoundBasedElimination;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.TeamManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;


public class Lobby implements Comparable<Lobby>
{
	private static int lobbyCount = 0;
	private static int lobbyNumberRange = 0;
	private static TreeSet<Integer> availableNumbers = new TreeSet<Integer>();

	private final PluginManager plugin;
	private ArrayList<LobbyListener> listeners = new ArrayList<LobbyListener>();

	private ArrayList<PBPlayer> players;

	public static SpawnKit defSpectKit;
	public static SpawnKit defInGameKit;
	public static SpawnKit defPregameKit;

	private int maxPlayerCount;
	private int roundTickTime;
	int maxRounds;
	private int minPlayers;

	private boolean inGame, inCount;

	private UUID lobbyId;
	private int lobbyNumber;
	
	private boolean chatLock;
	
	private MatchState matchState;
	private PregameState pregameState;
	
	private ExitItem exitItem;

	public Lobby(PluginManager plugin, LobbyListener listener, int maxPlayerCount, int roundTickTime, int maxRounds, int minimumPlayers, MapMetadata pregameMap)
	{
		this.plugin = plugin;
		players = new ArrayList<PBPlayer>();

		this.maxPlayerCount = maxPlayerCount;
		this.roundTickTime = roundTickTime;
		this.maxRounds = maxRounds;
		setMinPlayers(minimumPlayers);

		inGame = false;
		setInCount(false);

		lobbyId = UUID.randomUUID();
		lobbyCount++;
		
		if(availableNumbers.isEmpty())
		{
			lobbyNumberRange++;
			lobbyNumber = lobbyNumberRange;
		}
		else
			lobbyNumber = availableNumbers.pollFirst();
		
		chatLock = false;
		
		exitItem = new ExitItem(plugin, this);
		
		pregameState = new PregameState(plugin, this, pregameMap);
		matchState = new RoundBasedElimination(plugin, this, plugin.getMaps().getRandomMap(true), 3);
		
		pregameState.startState();
	}

	public ArrayList<PBPlayer> close()
	{
		if(lobbyNumberRange != lobbyNumber)
			availableNumbers.add(lobbyNumber);
		else
			lobbyNumberRange--;
		
		lobbyCount--;
		
		if(lobbyCount == 0)
		{
			lobbyNumberRange = 0;
			availableNumbers.clear();
		}
		
		if(inGame)
		{
			matchState.endState();
			pregameState.startState();
		}
		
		pregameState.endState();

		ArrayList<PBPlayer> playerBU = (ArrayList<PBPlayer>) players.clone();
		
		for(PBPlayer player : playerBU)
		{
			removePlayer(player.getID());
		}
		
		return playerBU;
	}

	
	// Object Overrides =====================================================

	@Override
	public int hashCode()
	{
		return lobbyId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Lobby)
		{
			Lobby other = (Lobby) obj;
			
			if(other.lobbyId.equals(lobbyId))
				return true;
		}
		else if(obj instanceof UUID)
		{
			if(lobbyId.equals((UUID) obj))
				return true;
		}
		else
			return super.equals(obj);
		
		return false;
	}

	@Override
	public int compareTo(Lobby other) 
	{    
		return Integer.compare(playerCount(), other.playerCount());
	}
	
	public PBPlayer findPlayer(UUID player)
	{
		int index = players.indexOf(new PBPlayer(player));
		
		if(index == -1)
			return null;
		else
			return players.get(index);
	}

	// UTILITY FUNCTIONS =====================================================

	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		if(isChatLocked())
			return;
		
		if(inGame)
			matchState.sendChatMessege(player, msg, allChat);
		else
			pregameState.sendChatMessege(player, msg, allChat);
	}

	public void broadcastMessage(String msg)
	{
		for(PBPlayer p : players)
		{
			Player player = Bukkit.getPlayer(p.getID());
			
			if(player != null)
				player.sendMessage(msg);
			else
				removePlayer(p.getID());
		}
	}

	public void broadcastLines(int lineCount)
	{
		for(int i = 0; i < lineCount; i++)
			broadcastMessage("");
	}
	
	// MATCH EVENTS ==========================================================

	public boolean addPlayer(PBPlayer player)
	{
		if(players.size() >= getMaxPlayerCount() || players.contains(player))
			return false;

		players.add(player);
		
		if(inGame)
			matchState.addPlayer(player);
		else
			pregameState.addPlayer(player);

		fireJoinEvent(player);
		
		return true;
	}

	public PBPlayer removePlayer(UUID player)
	{
		PBPlayer pb = findPlayer(player);
		
		if(pb != null && players.remove(pb))
		{
			broadcastMessage(ChatColor.YELLOW + Bukkit.getPlayer(pb.getID()).getName() + " left the lobby!");
			
			pregameState.removePlayer(pb);
			
			if(inGame)
				matchState.removePlayer(pb);
			
			Player p = Bukkit.getPlayer(player);

			try
			{
				if(p != null)
				{
					p.getInventory().clear();
					p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
				}
			} 
			catch(IllegalStateException e)
			{
				plugin.getLogger().warning(e.toString());
			}
			
			fireLeaveEvent(pb);
			
			return pb;
		}
		
		return null;
	}

	public void startMatch()
	{
		if(inGame)
			endMatch();
		
		pregameState.endState();
		firePregameEndEvent();
		
		inGame = true;
		matchState.startState();
		fireMatchStartEvent();
	}

	public void endMatch()
	{
		if(!inGame)
			return;
		
		inGame = false;
		
		matchState.endState();
		fireMatchEndEvent();
		
		pregameState.startState();
		firePregameStartEvent();
	}
	
	// LOBBY EVENT UTILITY FUNCTIONS ===================================================
	public void registerListener(LobbyListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(LobbyListener listener)
	{
		listeners.remove(listener);
	}
	
	private void fireMatchEndEvent()
	{
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).matchEndEvent(this); }
	}
	
	private void fireMatchStartEvent()
	{
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).matchStartEvent(this); }
	}
	
	private void firePregameEndEvent()
	{
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).pregameEndEvent(this); }
	}
	
	private void firePregameStartEvent()
	{
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).pregameStartEvent(this); }
	}
	
	private void fireLeaveEvent(PBPlayer player)
	{
		PBLobbyLeaveEvent event = new PBLobbyLeaveEvent(this, player);
		
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).playerLeaveEvent(event); }
	}
	
	private void fireJoinEvent(PBPlayer player)
	{
		PBLobbyJoinEvent event = new PBLobbyJoinEvent(this, player);
		
		Iterator<LobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((LobbyListener) it.next()).playerJoinedEvent(event); }
	}
	// MINECRAFT EVENTS ================================================================

	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		PBPlayer player = findPlayer(e.getPlayer().getUniqueId());
		
		if(player == null)
			return;
		
		if(isChatLocked())
		{
			e.getRecipients().clear();
			return;
		}
		
		if(inGame)
			matchState.playerChatEvent(e);
		else
			pregameState.playerChatEvent(e);
	}

	public void playerMoved(PlayerMoveEvent e)
	{
		if(findPlayer(e.getPlayer().getUniqueId()) == null)
			return;
		
		if(!inGame)
			pregameState.playerMoveEvent(e);
		else
			matchState.playerMoved(e);
	}
	
	public void entityDamagEntityEvent(EntityDamageByEntityEvent e)
	{
		Entity ent = e.getEntity();
		
		if(ent instanceof Player)
		{
			if(findPlayer(ent.getUniqueId()) == null)
				return;
			
			if(inGame)
				matchState.entityDamageEntity(e);
			else
				pregameState.entityDamageEntity(e);
		}
	}

	public void projectileHitEvent(ProjectileHitEvent e)
	{
		Entity ent = e.getEntity();
		
		if(ent instanceof Snowball)
		{
			ProjectileSource source = ((Snowball) ent).getShooter();
			
			if(source instanceof Player)
			{
				Player p = (Player) source;
				
				PBPlayer pbp = findPlayer(p.getUniqueId());
				
				if(inGame)
					matchState.projectileHitEvent(e);
			}
		}
	}
	
	// GETTERS AND SETTERS ===================================================
	
	public int getMinPlayers() 
	{
		return minPlayers;
	}

	public void setMinPlayers(int minPlayers) 
	{
		this.minPlayers = minPlayers;
	}

	public UUID getLobbyId() 
	{
		return lobbyId;
	}

	public int getMaxPlayerCount() 
	{
		return maxPlayerCount;
	}
	
	public boolean isInCount() 
	{
		return inCount;
	}

	public void setInCount(boolean inCount) 
	{
		this.inCount = inCount;
	}

	public boolean isChatLocked() 
	{
		return chatLock;
	}
	
	public void setChatLocked(boolean locked)
	{
		chatLock = locked;
	}

	public int getRoundTickTime() 
	{
		return roundTickTime;
	}
	
	public ArrayList<PBPlayer> getPlayers() 
	{ 
		return players; 
	}
	
	public boolean inGame() 
	{ 
		return inGame; 
	}
	
	public TeamManager getTeams()
	{
		return matchState.getTeamManager();
	}

	public MatchState getMatchState()
	{
		return matchState;
	}
	
	public boolean isFull()
	{
		return maxPlayerCount == players.size();
	}
	
	public int playerCount()
	{
		return players.size();
	}

	public void playSound(Location location, Sound sound, float volume, float pitch) 
	{
		for(PBPlayer p : players)
		{
			Bukkit.getPlayer(p.getID()).playSound(location, sound, volume, pitch);
		}
		
	}

	public void playerInteractEvent(PlayerInteractEvent e) 
	{
		matchState.playerInteractEvent(e);
	}

	public int getLobbyNumber() 
	{
		return lobbyNumber;
	}
	
	public static int lobbyCount()
	{
		return lobbyCount;
	}

	public ExitItem getExitItem() 
	{
		return exitItem;
	}
}
