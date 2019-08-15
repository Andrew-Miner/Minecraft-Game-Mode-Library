package SmokyMiner.MiniGames.Lobby;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import SmokyMiner.MiniGames.Items.MGExitItem;
import SmokyMiner.MiniGames.Items.ItemShop.MGItemShop;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageEndEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageStartEvent;
import SmokyMiner.MiniGames.Lobby.Stages.MGLobbyStage;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGLobby implements Comparable<MGLobby>, Listener
{
	// TODO: When the game ends because players left during a match, the pregame board doesnt get cleared
	// TODO: when a player leaves the match with the exit item during in game, big errors
	private static int lobbyCount = 0;
	private static int lobbyNumberRange = 0;
	private static TreeSet<Integer> availableNumbers = new TreeSet<Integer>();

	@SuppressWarnings("unused")
	private final MGManager manager;
	
	private UUID lobbyId;
	private int lobbyNumber;
	
	private boolean chatLock;
	
	private int minPlayers;
	private int maxPlayers;
	
	private int curStage;
	private ArrayList<MGLobbyStage> stages;
	
	private MGExitItem exitItem;
	
	private ArrayList<MGPlayer> players;
	private ArrayList<MGLobbyListener> listeners;
	
	public MGLobby(MGManager manager, int maxPlayers, int minPlayers)
	{
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		
		stages = new ArrayList<MGLobbyStage>();
		
		curStage = 0;
		chatLock = false;
		
		exitItem = new MGExitItem(manager.plugin(), this);
		exitItem.setCanDrop(false);
		
		players = new ArrayList<MGPlayer>();
		listeners = new ArrayList<MGLobbyListener>();
		
		lobbyId = UUID.randomUUID();
		lobbyCount++;
		
		lobbyNumber = nextLobbyNumber();
		
		manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	public MGLobby(MGManager manager, int maxPlayers, int minPlayers, MGLobbyStage stage)
	{
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		
		stages = new ArrayList<MGLobbyStage>();
		addStage(stage);
		
		curStage = 0;
		chatLock = false;
		
		exitItem = new MGExitItem(manager.plugin(), this);
		exitItem.setCanDrop(false);
		
		players = new ArrayList<MGPlayer>();
		listeners = new ArrayList<MGLobbyListener>();
		
		lobbyId = UUID.randomUUID();
		lobbyCount++;
		
		lobbyNumber = nextLobbyNumber();
		
		manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	@SuppressWarnings("unchecked")
	public MGLobby(MGManager manager, int maxPlayers, int minPlayers, ArrayList<MGLobbyStage> stages)
	{
		this.manager = manager;
		this.maxPlayers = maxPlayers;
		this.minPlayers = minPlayers;
		
		this.stages = (ArrayList<MGLobbyStage>) stages.clone();
		
		curStage = 0;
		chatLock = false;
		
		exitItem = new MGExitItem(manager.plugin(), this);
		exitItem.setCanDrop(false);
		
		players = new ArrayList<MGPlayer>();
		listeners = new ArrayList<MGLobbyListener>();
		
		lobbyId = UUID.randomUUID();
		
		lobbyNumber = nextLobbyNumber();
		
		manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
	}
	
	

	@SuppressWarnings("unchecked")
	public ArrayList<MGPlayer> close()
	{
		endStage();
		
		for(MGLobbyStage stage : stages)
			stage.close();
		
		HandlerList.unregisterAll(this);
		freeLobbyNumber(lobbyNumber);
		
		return (ArrayList<MGPlayer>) players.clone();
	}
	
	// Static Methods =======================================================
	
	private static int nextLobbyNumber()
	{
		lobbyCount++;					// THIS SHOULDNT BE HERE!!!!
		
		if(availableNumbers.isEmpty())
		{
			lobbyNumberRange++;
			return lobbyNumberRange;
		}
		else
			return availableNumbers.pollFirst();
	}
	
	private static void freeLobbyNumber(int lobbyNumber)
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
	}
	
	public static int lobbyCount()
	{
		return lobbyCount;
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
		if(obj instanceof MGLobby)
		{
			MGLobby other = (MGLobby) obj;
				
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
	public int compareTo(MGLobby other) 
	{    
		return Integer.compare(playerCount(), other.playerCount());
	}
	
	
	// MATCH EVENTS ==========================================================

	public boolean addPlayer(MGPlayer player)
	{
		if(players.size() >= maxPlayers || players.contains(player))
			return false;

		players.add(player);
    
    Player p = Bukkit.getPlayer(player.getID());
    exitItem.giveBlock(p);
    
		fireJoinEvent(player);
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
		MGPlayer pb = findPlayer(player);
			
		if(pb != null && players.remove(pb))
		{
      Player p = Bukkit.getPlayer(player);
      exitItem.removeBlock(p);
      
      fireLeaveEvent(pb);
			return pb;
		}
			
		return null;
	}
	
	
	// MINECRAFT EVENTS ================================================================
	
	@EventHandler
	public void playerLeaveEvent(PlayerQuitEvent e)
	{
		removePlayer(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void playerKickEvent(PlayerKickEvent e)
	{
		removePlayer(e.getPlayer().getUniqueId());
	}
	

	// UTILITY FUNCTIONS =====================================================

	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		if(isChatLocked())
			return;
		
		stages.get(curStage).sendChatMessege(player, msg, allChat);
	}

	public void broadcastMessage(String msg)
	{
		for(MGPlayer p : players)
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
	
	public MGPlayer findPlayer(UUID player)
	{
		int index = players.indexOf(new MGPlayer(player));
		
		if(index == -1)
			return null;
		else
			return players.get(index);
	}
	
	public void playSound(Location location, Sound sound, float volume, float pitch) 
	{
		for(MGPlayer p : players)
		{
			Bukkit.getPlayer(p.getID()).playSound(location, sound, volume, pitch);
		}
	}
	
	
	// Stage Functionality =====================================================

	public boolean addStage(MGLobbyStage stage)
	{
		if(stages.contains(stage))
			return false;
		stages.add(stage);
		return true;
	}
	
	public MGLobbyStage removeStage(int index)
	{
		if(stages.get(index).isActive())
			throw new IllegalStateException("cannot remove an active stage");
			
		if(index <= curStage && --curStage < 0)
			curStage = 0;
			
		return stages.remove(index);
	}
	
	public void nextStage()
	{
		nextStage(true);
	}
	
	public void nextStage(boolean startStage)
	{
		if(stages.get(curStage).isActive())
			endStage();
		
		if(++curStage >= stages.size())
				curStage = 0;
		
		if(startStage)
			startStage();
	}
	
	public void prevStage()
	{
		prevStage(true);
	}
	
	public void prevStage(boolean startStage)
	{
		if(stages.get(curStage).isActive())
			endStage();
		
		if(--curStage < 0)
			curStage = stages.size() - 1;
		
		if(startStage)
			startStage();
	}
	
	public void setStage(int index)
	{	
		if(curStage >= stages.size() || curStage < -1)
			throw new IndexOutOfBoundsException("stage index out of bounds");
		if(stages.get(curStage).isActive())
			throw new IllegalStateException("cannot change an active stage");
		curStage = index;
	}
	
	public void endStage()
	{
		if(curStage >= stages.size())
			throw new IndexOutOfBoundsException("current stage index is too large");
		if(!stages.get(curStage).isActive())
			throw new IllegalStateException("current stage already inactive");
		
		stages.get(curStage).endStage();
		fireStageEndEvent();
	}
	
	public void startStage()
	{
		if(curStage >= stages.size())
			throw new IndexOutOfBoundsException("current stage index is too large");
		if(stages.get(curStage).isActive())
			throw new IllegalStateException("current stage already active");
		
		stages.get(curStage).startStage();
		fireStageStartEvent();
	}
	
	
	// LOBBY EVENT UTILITY FUNCTIONS ===================================================
	
	public void registerListener(MGLobbyListener listener)
	{
		if(listener != null)
			listeners.add(listener);
	}
		
	public void unregisterListener(MGLobbyListener listener)
	{
		if(listener != null)
			listeners.remove(listener);
	}
	
	private void fireStageEndEvent()
	{
		MGLobbyStageEndEvent event = new MGLobbyStageEndEvent(this, stages.get(curStage));
		
		Iterator<MGLobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((MGLobbyListener) it.next()).stageEndEvent(event); }
	}
		
	private void fireStageStartEvent()
	{
		MGLobbyStageStartEvent event = new MGLobbyStageStartEvent(this, stages.get(curStage));
		
		Iterator<MGLobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((MGLobbyListener) it.next()).stageStartEvent(event); }
	}
		
	private void fireLeaveEvent(MGPlayer player)
	{
		MGLobbyLeaveEvent event = new MGLobbyLeaveEvent(this, player);
			
		Iterator<MGLobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((MGLobbyListener) it.next()).playerLeaveEvent(event); }
	}
		
	private void fireJoinEvent(MGPlayer player)
	{
		MGLobbyJoinEvent event = new MGLobbyJoinEvent(this, player);
			
		Iterator<MGLobbyListener> it = listeners.iterator();
		while(it.hasNext()) { ((MGLobbyListener) it.next()).playerJoinedEvent(event); }
	}
	
	
	// GETTERS AND SETTERS ===================================================
	public boolean stageActive()
	{
		return stages.get(curStage).isActive();
	}
	
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

	public int getMaxPlayers() 
	{
		return maxPlayers;
	}

	public boolean isChatLocked() 
	{
		return chatLock;
	}
	
	public void setChatLocked(boolean locked)
	{
		chatLock = locked;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<MGPlayer> getPlayers() 
	{ 
		return (ArrayList<MGPlayer>) players.clone(); 
	}

	public MGLobbyStage getCurrentStage()
	{
		return stages.get(curStage);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<MGLobbyStage> getStages()
	{
		return (ArrayList<MGLobbyStage>) stages.clone();
	}
	
	public boolean isFull()
	{
		return maxPlayers == players.size();
	}
	
	public int playerCount()
	{
		return players.size();
	}

	public int getLobbyNumber() 
	{
		return lobbyNumber;
	}
	
	public MGExitItem getExitItem() 
	{
		return exitItem;
	}
}
