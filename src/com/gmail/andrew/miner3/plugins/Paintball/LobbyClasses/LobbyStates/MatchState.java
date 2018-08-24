package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.SnowBowMethods;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards.InGameScoreboard;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.PaintballTeam;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.TeamManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerTask;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TitleCountEvent;

public class MatchState extends LobbyState
{
	protected InGameScoreboard board;
	protected ArrayList<BukkitTask> tasks;
	protected TeamManager teamManager;
	protected final TimerTask countDownTimer;
	protected boolean displayingMsg;
	protected boolean gameOver;
	protected ArrayList<Location> blocksChanged;

	public MatchState(PluginManager plugin, Lobby lobby, MapMetadata map) 
	{
		super(plugin, lobby, map);

		displayingMsg = false;
		gameOver = false;
		
		teamManager = new TeamManager(map);
		
		tasks = new ArrayList<BukkitTask>();
		
		board = new InGameScoreboard(plugin, lobby, lobby.getRoundTickTime());
		
		//countDownTimer = new TimerTask(plugin, 5, new TextCountEvent(this), 20L*5);
		TitleCountEvent titleEvent = new TitleCountEvent(this);
		titleEvent.setTitleFade(5);
		
		countDownTimer = new TimerTask(plugin, 10, titleEvent);
		
		blocksChanged = new ArrayList<Location>();
	}

	public void startState()
	{
		lobby.broadcastLines(8);
		lobby.broadcastMessage(ChatColor.RED + "        Starting Match...");
		lobby.broadcastLines(6);

		setupMatch();

		tasks.add(Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { public void run() { countDownTimer.startTimer();}}, 20L*7L));
	}

	public void endState()
	{
		map.setInUse(false);
		cancelTasks();
		board.resetTimer();
		countDownTimer.resetTimer();

		displayingMsg = false;
		gameOver = false;
		
		refreshPaintedBlocks();
		
		blocksChanged.clear();
		
		Iterator<PaintballTeam> it = teamManager.getTeams().iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			Iterator<PBPlayer> player = team.getPlayers().iterator();

			while(player.hasNext())
			{
				PBPlayer p = player.next();
				p.resetBow();
			}

			team.clearDead();
		}
	}

	public void beginMatch() 
	{
		cancelTasks();
		lobby.setInCount(false);
		board.startTimer();
		displayingMsg = false;
	}
	
	protected void endMatch(String reason, long tickDelay)
	{
		cancelTasks();
		countDownTimer.stopTimer();
		gameOver = true;
		
		if(reason != null)
		{
			lobby.broadcastLines(3);
			lobby.broadcastMessage(reason);
			lobby.broadcastLines(3);
		}
		
		tasks.add(Bukkit.getScheduler().runTaskLater(plugin, new Runnable() 
		{
			@Override
			public void run() 
			{
				lobby.endMatch();
			}
		}, tickDelay));
	}
	
	protected void endMatch(String title, String subtitle, long tickDelay, boolean displayChat)
	{
		cancelTasks();
		countDownTimer.stopTimer();
		displayingMsg = true;
		
		if(tickDelay - 40 > 0)
			teamManager.displayTitles(40, (int) (tickDelay - 40), 0, title, subtitle);
		else
			teamManager.displayTitles(0, (int) tickDelay, 0, title, subtitle);
		
		if(displayChat)
			endMatch(title + ": " + subtitle, tickDelay);
		else
			endMatch(null, tickDelay);
	}
	
	protected void setupMatch()
	{
		lobby.setChatLocked(true);
		lobby.setInCount(true);
		
		map = plugin.getMaps().getRandomMap(true);
		map.setInUse(true);
		
		teamManager.buildTeams(map);

		for(PBPlayer p : lobby.getPlayers())
		{
			p.setInGame();
			PaintballTeam team = teamManager.joinTeam(p);
			lobby.broadcastMessage(ChatColor.WHITE + Bukkit.getPlayer(p.getID()).getName() + ChatColor.YELLOW + " joined the " + team.getTeamName() + ChatColor.YELLOW + " Team!");
		}
		
		board.resetScoreboard();
		board.open();

		lobby.broadcastLines(4);

		teamManager.spawnTeams(map);
		teamManager.displayTeamTitles(1 * 20, 20 * 5, 1 * 20);
		
		for(PBPlayer p : lobby.getPlayers())
			Bukkit.getPlayer(p.getID()).getInventory().setItem(8, lobby.getExitItem().getItemStack());
		
		lobby.setChatLocked(false);
	}

	public void addPlayer(PBPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		PaintballTeam team = teamManager.joinTeam(player);

		team.setDead(player.getID());
		
		board.addPlayer(p);
		
		player.setSpectating();
		player.spawn(map.getSpectatorSpawn(), null);

		lobby.broadcastMessage(ChatColor.WHITE + Bukkit.getPlayer(player.getID()).getName() + ChatColor.YELLOW + " joined the " + team.getTeamName() + ChatColor.YELLOW + " Team!");
	}
	
	public void removePlayer(PBPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		teamManager.leaveTeam(player);
		
		if(lobby.inGame())
		{
			checkForEndGame();
			
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
		
		board.removePlayer(p);
	}
	
	protected void refreshPaintedBlocks()
	{
		Iterator<Location> locIt = blocksChanged.iterator();
		
		while(locIt.hasNext())
		{
			Location loc = locIt.next();
			loc.getBlock().getState().update(true);
		}
	}
	
	public PaintballTeam checkForEndGame()
	{
		if(gameOver)
			return null;
		
		if(!teamManager.playable())
		{
			endMatch("GAME OVER", ChatColor.RED + " Not Enough Players", 10L*20L, true);
			return null;
		}
		
		ArrayList<PaintballTeam> alive = teamManager.getAliveTeams();
		
		if(alive.size() == 1)
		{
			PaintballTeam winner = alive.get(0);
			endMatch("GAME OVER", winner.getTeamName() + " Team Wins!", 10L*20L, true);
			teamManager.addWinsAndLosses(winner);
			return winner;
		}
		
		return null;
	}

	protected void cancelTasks()
	{
		for(BukkitTask task : tasks)
		{
			task.cancel();
		}

		tasks.clear();
	}

	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		PBPlayer pbp = lobby.findPlayer(player);

		if(pbp == null)
			return;

		Player p = Bukkit.getPlayer(pbp.getID());
		PaintballTeam team = teamManager.getTeam(player);

		if(team == null)
			return;

		if(allChat)
			lobby.broadcastMessage(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "]" + ChatColor.WHITE + " <" + p.getName() + "> " + msg);
		else
			lobby.broadcastMessage(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "-Team]" + ChatColor.WHITE + " <" + p.getName() + "> " + msg);
	}

	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		Player p = e.getPlayer();
		UUID id = p.getUniqueId();

		Set<Player> recipients = e.getRecipients();
		ArrayList<PBPlayer> newRecips;
		PaintballTeam team = teamManager.getTeam(id);

		if(team == null)
			return;

		newRecips = team.getPlayers();
		e.setFormat(map.getColor(team.getId()) + "[" + map.getPrefix(team.getId()) + "-Team]" + ChatColor.WHITE + " <" + e.getPlayer().getName() + "> " + e.getMessage());

		recipients.clear();

		for(PBPlayer pbp : newRecips)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}

	public void playerMoved(PlayerMoveEvent e)
	{
		Player p = e.getPlayer();
		Location loc = e.getTo();

		PBPlayer pbp = lobby.findPlayer(p.getUniqueId());
		
		if(lobby.isInCount())
		{
			Location lastLoc = pbp.getStoredLoc();
			
			if(lastLoc == null)
			{
				pbp.storeLocation();
				lastLoc = pbp.getStoredLoc();
			}
			
			if(lastLoc.getBlockX() != loc.getBlockX() || lastLoc.getBlockZ() != loc.getBlockZ() || lastLoc.getBlockY() != loc.getBlockY())
				p.setVelocity(correctVelocity(loc, lastLoc, .2));

			return;
		}


		if(pbp.isSpectating())
		{
			if(!map.contains(loc, true))
				p.setVelocity(correctVelocity(loc, map.getSpectatorSpawn(), .3));
		}
		else if(pbp.isInGame())
		{
			if(!map.contains(loc, false))
				p.setVelocity(correctVelocity(loc, map.getMapCenter(), .3));
		}
		else
		{
			// TODO: Correct for pregame set player in a real game
		}
	}
	
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if(!lobby.isInCount() && !displayingMsg)
			SnowBowMethods.snowBowEvent(e, lobby);
		e.setCancelled(true);
	}
	
	public void entityDamageEntity(EntityDamageByEntityEvent e)
	{
		Entity ent = e.getEntity();
		
		if(!(ent instanceof Player))
			return;
		
		Entity dmgr = e.getDamager();
		
		PBPlayer enemy = null;
		Player enemyP = null;
		PBPlayer player = lobby.findPlayer(ent.getUniqueId());
		
		if(player == null)
			return;
		
		if(dmgr instanceof Snowball)
		{
			Snowball sb = (Snowball) dmgr;
			ProjectileSource shooter = sb.getShooter();
			
			if(shooter instanceof Player)
			{
				enemyP = (Player) shooter;
				enemy = lobby.findPlayer(((Player) shooter).getUniqueId());
			}
		}
		else if(dmgr instanceof Player)
		{
			Player pDmgr = (Player) dmgr;
			
			if(pDmgr.getInventory().getItemInMainHand().getType().equals(Material.WOOD_SWORD))
			{
				enemy = lobby.findPlayer(pDmgr.getUniqueId());
				enemyP = pDmgr;		
			}
		}

		
		if(enemy != null)
			playerKilled(player, enemy, enemyP);
		
		e.setCancelled(true);
	}
	
	public void playerKilled(PBPlayer player, PBPlayer killer, Player killerP)
	{
		PaintballTeam homeTeam = teamManager.getTeam(player.getID());
		PaintballTeam enemyTeam = teamManager.getTeam(killer.getID());
		
		if(homeTeam == null)
			return;

		killer.addKill();
		killer.addToScore(10);
		killerP.playSound(killerP.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, .5f);
		
		player.addDeath();
		player.setSpectating();
		player.spawn(map.getSpectatorSpawn(), null);
		homeTeam.setDead(player.getID());
		
		
		if(enemyTeam != null)
			lobby.broadcastMessage(map.getColor(enemyTeam.getId()) + Bukkit.getPlayer(killer.getID()).getName() + ChatColor.YELLOW + " splattered " + map.getColor(homeTeam.getId()) + Bukkit.getPlayer(player.getID()).getName());
		else
			lobby.broadcastMessage(ChatColor.WHITE + Bukkit.getPlayer(killer.getID()).getName() + ChatColor.YELLOW + " splattered " + map.getColor(homeTeam.getId()) + Bukkit.getPlayer(player.getID()).getName());
		
		board.refreshPlayerIcons();
		checkForEndGame();
	}
	
	public TeamManager getTeamManager()
	{
		return teamManager;
	}
	
	public InGameScoreboard getScoreboard()
	{
		return board;
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
				Block b = e.getHitBlock();
				
				if(b != null)
				{
					SnowBowMethods.sendBlockPaint(lobby, b.getLocation(), teamManager.getTeam(p.getUniqueId()).getTeamColor());
					
					if(!blocksChanged.contains(b.getLocation()))
						blocksChanged.add(b.getLocation());
				}
			}
		}
	}

}
