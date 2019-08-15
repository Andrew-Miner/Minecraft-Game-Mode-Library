package SmokyMiner.MiniGames.Lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.UUID;

import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;
import SmokyMiner.MiniGames.Lobby.Browser.MGLobbyBrowser;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageEndEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageStartEvent;
import SmokyMiner.MiniGames.Lobby.Stages.MGEliminationStage;
import SmokyMiner.MiniGames.Lobby.Stages.MGPregameStage;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.MiniGames.Player.MGPlayerManager;
import SmokyMiner.Minigame.Main.MGManager;

public class MGMatchMaker implements MGLobbyListener
{
	private int minLobbies;
	private int maxLobbies = 0;

	private MGManager manager;
	private MGLobbyBrowser browser;

	private MGPlayerManager players;
	private ArrayList<MGLobby> lobbies;

	private MGMapMetadata pregameMap;

	private int maxPlayers = 4;
	private int minPlayers = 2;

	private ArrayList<MGLobbyListener> listeners;

	// TODO: Condence Lobby Task
	// TODO: Close
	// TODO: remove lobby leave in same class it's given

	public MGMatchMaker()
	{
		lobbies = new ArrayList<MGLobby>();
		listeners = new ArrayList<MGLobbyListener>();
		manager = null;
		minLobbies = 1;
		players = new MGPlayerManager(null);
		browser = null;

		createLobby(null);
	}

	public MGMatchMaker(MGManager manager)
	{
		this.manager = manager;
		lobbies = new ArrayList<MGLobby>();
		listeners = new ArrayList<MGLobbyListener>();
		minLobbies = manager.getMaps().getMapCount() - 1;
		players = manager.getPlayerManager();
		pregameMap = null;
		browser = null;

		while (lobbies.size() < minLobbies)
			createLobby(pregameMap);
	}

	public MGMatchMaker(MGManager manager, int minLobbyCount)
	{
		this.manager = manager;
		lobbies = new ArrayList<MGLobby>();
		listeners = new ArrayList<MGLobbyListener>();
		this.minLobbies = minLobbyCount;
		players = manager.getPlayerManager();
		pregameMap = null;
		browser = null;

		while (lobbies.size() < minLobbies)
			createLobby(pregameMap);
	}

	public MGMatchMaker(MGManager manager, MGMapMetadata pregameLobby)
	{
		this.manager = manager;
		lobbies = new ArrayList<MGLobby>();
		listeners = new ArrayList<MGLobbyListener>();
		pregameMap = pregameLobby;
		minLobbies = manager.getMaps().getMapCount() - 1;
		players = manager.getPlayerManager();
		browser = new MGLobbyBrowser(manager, this);

		while (lobbies.size() < minLobbies)
			createLobby(pregameMap);
	}

	public MGMatchMaker(MGManager manager, int minLobbyCount, MGMapMetadata pregameLobby)
	{
		this.manager = manager;
		lobbies = new ArrayList<MGLobby>();
		listeners = new ArrayList<MGLobbyListener>();
		this.pregameMap = pregameLobby;
		this.minLobbies = minLobbyCount;
		players = manager.getPlayerManager();
		browser = new MGLobbyBrowser(manager, this);

		while (lobbies.size() < minLobbies)
			createLobby(pregameMap);
	}

	public MGLobbyBrowser getBrowser()
	{
		return browser;
	}

	public MGPlayer getPlayer(UUID player)
	{
		return players.getMGPlayer(player);
	}

	public MGLobby getLobbyByPlayer(UUID player)
	{
		Iterator<MGLobby> it = lobbies.iterator();

		while (it.hasNext())
		{
			MGLobby lobby = it.next();

			if (lobby.findPlayer(player) != null)
				return lobby;
		}

		return null;
	}

	@SuppressWarnings("unlikely-arg-type")
	public MGLobby getLobbyByUUID(UUID lobby)
	{
		Iterator<MGLobby> it = lobbies.iterator();

		while (it.hasNext())
		{
			MGLobby mgLobby = it.next();

			if (mgLobby.equals(lobby))
				return mgLobby;
		}

		return null;
	}

	public MGLobby getOpenLobby()
	{
		ArrayList<MGLobby> open = MGLobbyTools.getOpenLobbies(lobbies);

		if (open == null)
			return null;

		return MGLobbyTools.getSmallestPlayerCount(open);
	}

	public MGLobby createLobby(MGMapMetadata pregameMap)
	{
		if (maxLobbies != 0 && maxLobbies <= lobbies.size())
			return null;

		MGLobby lobby = new MGLobby(manager, maxPlayers, minPlayers);
		lobby.addStage(new MGPregameStage(manager, lobby, pregameMap, manager.getItemShop()));
		lobby.addStage(new MGEliminationStage(manager, lobby, manager.getMaps().getRandomMap(false), 60, 3));
		lobby.startStage();

		registerLobby(lobby);
		return lobby;
	}

	public void registerLobby(MGLobby lobby)
	{
		if (maxLobbies != 0 && maxLobbies <= lobbies.size())
			return;

		lobby.registerListener(this);
		lobbies.add(lobby);

		if (browser != null)
			browser.addLobby(lobby);
	}

	private void combineLobbies(MGLobby smallL, MGLobby largeL)
	{
		ArrayList<MGPlayer> players = smallL.close();

		if (browser != null)
			browser.removeLobby(smallL);

		for (MGPlayer p : players)
			joinLobby(p.getID(), largeL.getLobbyId());

		lobbies.remove(smallL);
	}

	public void condenceLobbies()
	{
		ArrayList<MGLobby> open = MGLobbyTools.getOpenLobbies(lobbies);

		if (open != null)
		{
			manager.plugin().getLogger().info("Condence Lobbies Called: " + open.size() + " Open Lobbies");
			Collections.sort(open);
			condenceLobbies(open);

			open = MGLobbyTools.getOpenLobbies(lobbies);
			if (open != null)
				manager.plugin().getLogger().info("Condence Lobbies Finished: " + open.size() + " Open Lobbies");
			else
				manager.plugin().getLogger().info("Condence Lobbies Called: No Open Lobbies");
		} else
			manager.plugin().getLogger().info("Condence Lobbies Called: No Open Lobbies");

		while (lobbies.size() < minLobbies)
			createLobby(pregameMap);
	}

	public boolean condenceLobbies(ArrayList<MGLobby> open)
	{
		if (open.size() == 0)
		{
			for (MGLobby l : lobbies)
				if (l.playerCount() != l.getMaxPlayers())
					return false;

			return true;
		} else if (open.size() == 1)
			return false;

		MGLobby largest = open.get(open.size() - 1);
		open.remove(largest);

		int maxPlayers = largest.getMaxPlayers() - largest.playerCount();
		PriorityQueue<MGLobby> possibleMatch = new PriorityQueue<MGLobby>();

		Iterator<MGLobby> it = open.iterator();

		while (it.hasNext())
		{
			MGLobby l = it.next();
			int pCount = l.playerCount();

			if (l.getCurrentStage() instanceof MGPregameStage || !l.getCurrentStage().isActive())
			{
				if (pCount == maxPlayers)
				{
					it.remove();

					combineLobbies(l, largest);

					if (!largest.isFull())
						MGLobbyTools.insertSorted(open, largest);

					return condenceLobbies(open);
				} else if (pCount < maxPlayers)
					possibleMatch.add(l);
			}
		}

		if (!possibleMatch.isEmpty())
		{
			MGLobby possibleLarge = possibleMatch.poll();
			open.remove(possibleLarge);

			combineLobbies(possibleLarge, largest);

			if (!largest.isFull())
				MGLobbyTools.insertSorted(open, largest);
		}

		return condenceLobbies(open);
	}

	@SuppressWarnings("unlikely-arg-type")
	public void unregisterLobby(UUID lobby)
	{
		Iterator<MGLobby> it = lobbies.iterator();

		while (it.hasNext())
		{
			MGLobby mgLobby = it.next();

			if (mgLobby.equals(lobby))
			{
				mgLobby.unregisterListener(this);
				it.remove();
			}
		}
	}

	public boolean joinLobby(UUID player, UUID lobby)
	{
		MGLobby l = getLobbyByUUID(lobby);

		if (l != null && l.playerCount() < l.getMaxPlayers())
		{
			MGPlayer p = players.getMGPlayer(player);

			if (p == null)
				throw new IllegalStateException("loading player data");

			l.addPlayer(p);

			if (getOpenLobby() == null)
				createLobby(pregameMap);

			return true;
		}

		return false;
	}

	public boolean joinLobby(UUID player)
	{
		MGLobby lobby = getOpenLobby();

		if (lobby == null)
			lobby = createLobby(pregameMap);

		if (lobby == null)
			return false;

		MGPlayer p = players.getMGPlayer(player);

		if (p == null)
			throw new IllegalStateException("loading player data");

		lobby.addPlayer(p);

		if (getOpenLobby() == null)
			createLobby(pregameMap);

		return true;
	}

	public void leaveMatchmaking(UUID player)
	{
		MGLobby lobby = getLobbyByPlayer(player);

		if (lobby != null)
			lobby.removePlayer(player);
	}

	// ================================ Events ================================

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e)
	{
		manager.plugin().getLogger().info("MGLobbyJoinEvent Fired!");
		browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(MGLobbyTools.buildLobbyLore(e.getLobby()));
		browser.refreshBrowser();
		fireJoinEvent(e);
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e)
	{
		manager.plugin().getLogger().info("MGLobbyLeaveEvent Fired!");

		MGMenuItem lobbyItem = browser.getLobbyItem(e.getLobby().getLobbyId());
		if (lobbyItem != null)
		{
			browser.getLobbyItem(e.getLobby().getLobbyId()).setLore(MGLobbyTools.buildLobbyLore(e.getLobby()));
			browser.refreshBrowser();
		}

		// Clean up extra lobbies
		if (minLobbies < lobbies.size())
		{
			MGLobby lobby = e.getLobby();
			if (lobby.playerCount() == 0 && MGLobbyTools.getOpenLobbies(lobbies).size() > 1)
			{
				lobby.close();
				lobbies.remove(lobby);
				browser.removeLobby(lobby);
			}
		}

		fireLeaveEvent(e);
	}

	@Override
	public void stageStartEvent(MGLobbyStageStartEvent e)
	{
		manager.plugin().getLogger().info("StageStartEvent Fired!");
		fireStageStartEvent(e);
	}

	@Override
	public void stageEndEvent(MGLobbyStageEndEvent e)
	{
		manager.plugin().getLogger().info("StageEndEvent Fired!");
		fireStageEndEvent(e);
	}

//LOBBY EVENT UTILITY FUNCTIONS ===================================================

	public void registerListener(MGLobbyListener listener)
	{
		if (listener != null)
			listeners.add(listener);
	}

	public void unregisterListener(MGLobbyListener listener)
	{
		if (listener != null)
			listeners.remove(listener);
	}

	private void fireStageEndEvent(MGLobbyStageEndEvent event)
	{
		Iterator<MGLobbyListener> it = listeners.iterator();
		while (it.hasNext())
		{
			((MGLobbyListener) it.next()).stageEndEvent(event);
		}
	}

	private void fireStageStartEvent(MGLobbyStageStartEvent event)
	{
		Iterator<MGLobbyListener> it = listeners.iterator();
		while (it.hasNext())
		{
			((MGLobbyListener) it.next()).stageStartEvent(event);
		}
	}

	private void fireLeaveEvent(MGLobbyLeaveEvent event)
	{
		Iterator<MGLobbyListener> it = listeners.iterator();
		while (it.hasNext())
		{
			((MGLobbyListener) it.next()).playerLeaveEvent(event);
		}
	}

	private void fireJoinEvent(MGLobbyJoinEvent event)
	{
		Iterator<MGLobbyListener> it = listeners.iterator();
		while (it.hasNext())
		{
			((MGLobbyListener) it.next()).playerJoinedEvent(event);
		}
	}
}
