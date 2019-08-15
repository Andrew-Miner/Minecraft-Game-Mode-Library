package SmokyMiner.MiniGames.Lobby.Stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.MGLobbyTools;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Scoreboards.MGInGameScoreboard;
import SmokyMiner.MiniGames.Lobby.Team.MGTeam;
import SmokyMiner.MiniGames.Lobby.Team.MGTeamManager;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerTask;
import SmokyMiner.MiniGames.Lobby.Timer.MGTitleCountEvent;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGGameStage extends MGLobbyStage
{
	protected final MGTimerTask timer;

	protected MGInGameScoreboard board;
	protected MGTeamManager teamManager;
	protected ArrayList<BukkitTask> tasks;

	protected boolean gameOver;
	protected boolean displayingMsg, inCountDown;

	protected int tickLimit;

	protected HashMap<Integer, ItemStack> additionalKitItems;

	public MGGameStage(MGManager manager, MGMapMetadata map)
	{
		super(manager, map);

		this.tickLimit = 60;

		gameOver = false;
		displayingMsg = false;
		inCountDown = false;

		teamManager = new MGTeamManager(map);
		board = null;

		tasks = new ArrayList<BukkitTask>();

		MGTitleCountEvent titleEvent = new MGTitleCountEvent(this);
		titleEvent.setTitleFade(5);

		timer = new MGTimerTask(manager.plugin(), 10, titleEvent);

		additionalKitItems = new HashMap<Integer, ItemStack>();
	}

	public MGGameStage(MGManager manager, MGMapMetadata map, int tickLimit)
	{
		super(manager, map);

		this.tickLimit = tickLimit;

		gameOver = false;
		displayingMsg = false;
		inCountDown = false;

		teamManager = new MGTeamManager(map);
		board = null;

		tasks = new ArrayList<BukkitTask>();

		MGTitleCountEvent titleEvent = new MGTitleCountEvent(this);
		titleEvent.setTitleFade(5);

		timer = new MGTimerTask(manager.plugin(), 10, titleEvent);

		additionalKitItems = new HashMap<Integer, ItemStack>();
	}

	public MGGameStage(MGManager manager, MGLobby lobby, MGMapMetadata map)
	{
		super(manager, lobby, map);

		this.tickLimit = 60;

		gameOver = false;
		displayingMsg = false;
		inCountDown = false;

		teamManager = new MGTeamManager(map);
		board = new MGInGameScoreboard(manager, lobby, this, tickLimit);

		tasks = new ArrayList<BukkitTask>();

		MGTitleCountEvent titleEvent = new MGTitleCountEvent(this);
		titleEvent.setTitleFade(5);

		timer = new MGTimerTask(manager.plugin(), 10, titleEvent);

		additionalKitItems = new HashMap<Integer, ItemStack>();
		additionalKitItems.put(8, lobby.getExitItem().getItemStack());
	}

	public MGGameStage(MGManager manager, MGLobby lobby, MGMapMetadata map, int tickLimit)
	{
		super(manager, lobby, map);

		this.tickLimit = tickLimit;

		gameOver = false;
		displayingMsg = false;
		inCountDown = false;

		teamManager = new MGTeamManager(map);
		board = new MGInGameScoreboard(manager, lobby, this, tickLimit);

		tasks = new ArrayList<BukkitTask>();

		MGTitleCountEvent titleEvent = new MGTitleCountEvent(this);
		titleEvent.setTitleFade(5);

		timer = new MGTimerTask(manager.plugin(), 10, titleEvent);

		additionalKitItems = new HashMap<Integer, ItemStack>();
		additionalKitItems.put(8, lobby.getExitItem().getItemStack());
	}

	@Override
	public void close()
	{
		super.close();

		if (board != null)
			board.close();

		cancelTasks();

		timer.resetTimer();
		map.setInUse(false);
		lobby.setChatLocked(false);
	}

	@Override
	public void setLobby(MGLobby lobby)
	{
		super.setLobby(lobby);
		close();
		board = new MGInGameScoreboard(manager, lobby, this, tickLimit);
		additionalKitItems.put(8, lobby.getExitItem().getItemStack());
	}

	@Override
	public void startStage()
	{
		if (lobby == null)
			throw new IllegalStateException("lobby instance null");

		super.startStage();

		lobby.setChatLocked(true);

		lobby.broadcastLines(8);
		lobby.broadcastMessage(ChatColor.RED + "        Starting Match...");
		lobby.broadcastLines(6);

		inCountDown = true;

		map = manager.getMaps().getRandomMap(true);
		map.setInUse(true);

		// Build Teams
		teamManager.buildTeams(map);
		for (MGPlayer p : lobby.getPlayers())
		{
			p.setInGame();
			teamManager.joinTeam(p);
		}

		board.resetScoreboard();
		board.open();

		teamManager.spawnTeams(map, additionalKitItems);
		teamManager.displayTeamTitles(1 * 20, 20 * 5, 1 * 20);
		
		for(MGPlayer p : lobby.getPlayers())
			giveInvItems(p);

		lobby.setChatLocked(false);

		tasks.add(Bukkit.getScheduler().runTaskLater(manager.plugin(), new Runnable()
		{
			public void run()
			{
				timer.startTimer();
			}
		}, 20L * 7L));
	}

	@Override
	public void endStage()
	{
		super.endStage();

		map.setInUse(false);
		cancelTasks();
		board.resetTimer();
		timer.resetTimer();

		gameOver = false;
		inCountDown = false;

		teamManager.clear();
	}

	public boolean startGame()
	{
		cancelTasks();
		board.startTimer();
		inCountDown = false;
		displayingMsg = false;

		return true;
	}

	protected void endGame(String title, String subtitle, long tickDelay, boolean displayChat)
	{
		cancelTasks();
		timer.stopTimer();
		displayingMsg = true;

		if (tickDelay - 40 > 0)
			teamManager.displayTitles(40, (int) (tickDelay - 40), 0, title, subtitle);
		else
			teamManager.displayTitles(0, (int) tickDelay, 0, title, subtitle);

		if (displayChat)
			endGame(title + ": " + subtitle, tickDelay);
		else
			endGame(null, tickDelay);
	}

	protected void endGame(String reason, long tickDelay)
	{
		cancelTasks();
		timer.stopTimer();
		gameOver = true;

		if (reason != null)
		{
			lobby.broadcastLines(3);
			lobby.broadcastMessage(reason);
			lobby.broadcastLines(3);
		}

		tasks.add(Bukkit.getScheduler().runTaskLater(manager.plugin(), new Runnable()
		{
			@Override
			public void run()
			{
				lobby.nextStage(true);
			}
		}, tickDelay));
	}

	public MGTeam checkForEndGame()
	{
		if (gameOver)
			return null;

		if (!teamManager.playable())
		{
			endGame("GAME OVER", ChatColor.RED + " Not Enough Players", 10L * 20L, true);
			return null;
		}

		ArrayList<MGTeam> alive = teamManager.getAliveTeams();

		if (alive.size() == 1)
		{
			MGTeam winner = alive.get(0);
			endGame("GAME OVER", winner.getTeamName() + " Team Wins!", 10L * 20L, true);
			teamManager.addWinsAndLosses(winner);
			return winner;
		}

		return null;
	}

	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		MGPlayer pbp = lobby.findPlayer(player);

		if (pbp == null)
			return;

		Player p = Bukkit.getPlayer(pbp.getID());
		MGTeam team = teamManager.getTeam(player);

		if (team == null)
			return;

		if (allChat)
			lobby.broadcastMessage(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "]"
					+ ChatColor.WHITE + " <" + p.getName() + "> " + msg);
		else
			lobby.broadcastMessage(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "-Team]"
					+ ChatColor.WHITE + " <" + p.getName() + "> " + msg);
	}	
	
	public void playerKilled(MGPlayer dead, MGPlayer killer, int creditReward)
	{
		playerKilled(dead, 0, killer, 10, creditReward);
	}
	
	public void playerKilled(MGPlayer dead, MGPlayer killer)
	{
		playerKilled(dead, 0, killer, 10, 10);
	}

	public void playerKilled(MGPlayer dead, int deadScore, MGPlayer killer, int killerScore, int creditReward)
	{
		MGTeam homeTeam = teamManager.getTeam(dead.getID());
		MGTeam enemyTeam = teamManager.getTeam(killer.getID());

		if (homeTeam == null)
			throw new IllegalStateException("dead player's team is null");
		if (enemyTeam == null)
			throw new IllegalStateException("killer's team is null");

		killer.addKill();
		killer.addToScore(killerScore);
		killer.addCurrency(creditReward);
		Player kP = Bukkit.getPlayer(killer.getID());
		kP.playSound(kP.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, .5f);

		dead.addDeath();
		dead.setSpectating();
		dead.addToScore(deadScore);
		dead.spawn(map.getSpectatorSpawn(), null);
		homeTeam.setDead(dead.getID());

		board.refreshPlayerIcons();
		checkForEndGame();
	}

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e)
	{
		Player p = Bukkit.getPlayer(e.getMGPlayer().getID());

		MGTeam team = teamManager.joinTeam(e.getMGPlayer());
		team.setDead(e.getMGPlayer().getID());

		board.addPlayer(p);

		e.getMGPlayer().setSpectating();
		e.getMGPlayer().spawn(map.getSpectatorSpawn(), null);
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e)
	{
		Player p = Bukkit.getPlayer(e.getMGPlayer().getID());

		teamManager.leaveTeam(e.getMGPlayer());

		if (this.active)
		{
			checkForEndGame();

			try
			{
				if (p != null)
					p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			} catch (IllegalStateException er)
			{
				manager.plugin().getLogger().warning(er.toString());
			}
		}

		board.removePlayer(p);
	}

	@EventHandler
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		UUID id = p.getUniqueId();

		Set<Player> recipients = e.getRecipients();
		ArrayList<MGPlayer> newRecips;
		MGTeam team = teamManager.getTeam(id);

		if (team == null)
			return;

		newRecips = team.getPlayers();
		e.setFormat(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "-Team]" + ChatColor.WHITE + " <"
				+ e.getPlayer().getName() + "> " + e.getMessage());

		recipients.clear();

		for (MGPlayer pbp : newRecips)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}

	@EventHandler
	public void playerMoved(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		MGPlayer pbp = lobby.findPlayer(p.getUniqueId());

		if (pbp == null)
			return;

		Location loc = e.getTo();

		if (inCountDown)
		{
			Location lastLoc = pbp.getStoredLoc();

			if (lastLoc == null)
			{
				pbp.storeLocation();
				lastLoc = pbp.getStoredLoc();
			}

			if (lastLoc.getBlockX() != loc.getBlockX() || lastLoc.getBlockZ() != loc.getBlockZ()
					|| lastLoc.getBlockY() != loc.getBlockY())
				p.setVelocity(MGLobbyTools.correctVelocity(loc, lastLoc, .2));

			return;
		}

		if (pbp.isSpectating())
		{
			if (!map.contains(loc, true))
				p.setVelocity(MGLobbyTools.correctVelocity(loc, map.getSpectatorSpawn(), .3));
		} else if (pbp.isInGame())
		{
			if (!map.contains(loc, false))
				p.setVelocity(MGLobbyTools.correctVelocity(loc, map.getMapCenter(), .3));
		} else
		{
			// TODO: Correct for pregame set player in a real game
		}
	}

	public MGTeamManager getTeamManager()
	{
		return teamManager;
	}

	public MGInGameScoreboard getScoreboard()
	{
		return board;
	}

	protected void cancelTasks()
	{
		for (BukkitTask task : tasks)
		{
			task.cancel();
		}

		tasks.clear();
	}

	public boolean gameActive()
	{
		return !(!active || displayingMsg || inCountDown);
	}
	
	protected void giveInvItems(MGPlayer p)
	{
		
	}
}
