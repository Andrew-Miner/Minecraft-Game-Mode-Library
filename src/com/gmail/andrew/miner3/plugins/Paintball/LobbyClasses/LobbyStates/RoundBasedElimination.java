package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards.RoundBasedScoreboard;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.PaintballTeam;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerEvent;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TitleCountEvent;

public class RoundBasedElimination extends MatchState
{
	private HashMap<Integer, Integer> roundWins;
	private int currentRound;
	private int winLimit;

	public RoundBasedElimination(PluginManager plugin, Lobby lobby, MapMetadata map) 
	{
		super(plugin, lobby, map);

		roundWins = new HashMap<Integer, Integer>();
		currentRound = 0;
		winLimit = 3;
		board = new RoundBasedScoreboard(plugin, lobby, lobby.getRoundTickTime());
	}

	public RoundBasedElimination(PluginManager plugin, Lobby lobby, MapMetadata map, int roundsToWin) 
	{
		super(plugin, lobby, map);

		roundWins = new HashMap<Integer, Integer>();
		currentRound = 0;
		winLimit = roundsToWin;
		board = new RoundBasedScoreboard(plugin, lobby, lobby.getRoundTickTime());
	}

	@Override
	public void startState()
	{
		currentRound = 1;
		super.startState();
		resetRoundWins();
	}

	@Override
	public void endState()
	{
		super.endState();

		roundWins.clear();
		currentRound = 0;

		TimerEvent events = countDownTimer.getTimerEvent();

		if(events instanceof TitleCountEvent)
			((TitleCountEvent) events).setTitles(TitleCountEvent.defCountDownTitle, TitleCountEvent.defStartTitle);
	}
	
	@Override
	public PaintballTeam checkForEndGame()
	{
		if(gameOver)
			return null;
		
		if(!teamManager.playable())
		{
			super.endMatch("GAME OVER", ChatColor.RED + " Not Enough Players", 10L*20L, true);
			return null;
		}
		

		ArrayList<PaintballTeam> alive = teamManager.getAliveTeams();

		if(alive.size() == 1)			// Only 1 Team Is Left Living
		{
			PaintballTeam winner = alive.get(0);
			board.stopTimer();
			int roundsWon = roundWon(winner);

			checkForRoundLimit(winner, roundsWon);

			return winner;
		}
		else if(board.getTime() == 0)	// Round/Game Timer Ended
		{
			PaintballTeam winner = null;

			for(PaintballTeam team : alive)
			{
				if(winner == null || team.size() > winner.size())
					winner = team;
			}

			ArrayList<PaintballTeam> tied = new ArrayList<PaintballTeam>();

			for(PaintballTeam team : alive)
			{
				if(team.size() == winner.size())
					tied.add(team);
			}

			if(tied.size() == 1)
			{
				roundWon(winner);
				int roundsWon = roundTied(tied);
				checkForRoundLimit(winner, roundsWon);
			}
			else
				roundTied(tied);
		}

		return null;
	}

	protected void checkForRoundLimit(PaintballTeam winner, int roundsWon)
	{
		displayingMsg = true;
		
		if(roundsWon >= winLimit)
		{
			endMatch("GAME OVER", winner.getTeamName() + " Team Wins!", 10L * 20L, true);
			teamManager.addWinsAndLosses(winner);
		}
		else
		{
			teamManager.displayTitles(2 * 20, 8 * 20, 0, "ROUND OVER", winner.getTeamName() + " Team Wins!");
			tasks.add(Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { public void run() {startNewRound();}}, 20L*10L));
		}
	}

	protected void checkForGameTiedLimit(ArrayList<PaintballTeam> tiedTeams, int tiedScore)
	{
		displayingMsg = true;
		
		if(tiedTeams.size() == 1)
		{
			checkForRoundLimit(tiedTeams.get(0), tiedScore);
			return;
		}

		String subtitle = null;

		for(PaintballTeam team : tiedTeams)
		{
			if(subtitle == null)
				subtitle = team.getTeamName();
			else
				subtitle += ChatColor.WHITE + " and " + team.getTeamName();
		}

		subtitle += ChatColor.WHITE + " Tied!";

		if(tiedScore >= winLimit)
			endMatch("GAME OVER", subtitle, 10L*20L, true);
		else
		{
			teamManager.displayTitles(2 * 20, 8 * 20, 0, "ROUND OVER", subtitle);
			tasks.add(Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { public void run() {startNewRound();}}, 20L*10L));
		}
	}

	protected int roundTied(ArrayList<PaintballTeam> tiedTeams)
	{
		ArrayList<PaintballTeam> tiedFinalScore = new ArrayList<PaintballTeam>();
		int highestScore = 0;

		for(PaintballTeam team : tiedTeams)
		{
			int newEntry = roundWins.get(team.getId()) + 1;
			roundWins.put(team.getId(), newEntry);

			if(newEntry > highestScore)
				highestScore = newEntry;
		}

		for(PaintballTeam team : tiedTeams)
		{
			if(highestScore == roundWins.get(team.getId()))
				tiedFinalScore.add(team);
		}

		if(highestScore >= winLimit)
		{
			if(tiedFinalScore.size() == 1)
				checkForRoundLimit(tiedFinalScore.get(0), highestScore);
			else
				checkForGameTiedLimit(tiedFinalScore, highestScore);
		}
		else
			checkForGameTiedLimit(tiedTeams, highestScore);


		RoundBasedScoreboard sb = (RoundBasedScoreboard) board;
		int wins[] = new int[roundWins.size()];

		for(Map.Entry<Integer, Integer> entry : roundWins.entrySet())
		{
			wins[entry.getKey()] = entry.getValue();
		}

		sb.refreshRoundWins(wins);

		return highestScore;
	}

	protected int roundWon(PaintballTeam winner)
	{
		int roundsWon = roundWins.get(winner.getId()) + 1;
		roundWins.put(winner.getId(), roundsWon);

		RoundBasedScoreboard sb = (RoundBasedScoreboard) board;
		int wins[] = new int[roundWins.size()];

		for(Map.Entry<Integer, Integer> entry : roundWins.entrySet())
		{
			wins[entry.getKey()] = entry.getValue();
		}

		sb.refreshRoundWins(wins);

		return roundsWon;
	}

	protected void startNewRound() 
	{
		currentRound++;

		lobby.setInCount(true);
		displayingMsg = false;
		
		resetRound();

		TimerEvent events = countDownTimer.getTimerEvent();
		if(events instanceof TitleCountEvent)
		{
			TitleCountEvent titleEvent = (TitleCountEvent) events;
			titleEvent.setTitles("Round " + ChatColor.GOLD + "" + currentRound + ChatColor.WHITE + " Begins In", "Round Start");
		}

		countDownTimer.startTimer(7);
	}

	protected void resetRound()
	{
		cancelTasks();
		board.resetTimer();
		countDownTimer.resetTimer();

		super.refreshPaintedBlocks();
		
		Iterator<PaintballTeam> it = teamManager.getTeams().iterator();

		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			Iterator<PBPlayer> player = team.getPlayers().iterator();

			while(player.hasNext())
			{
				PBPlayer p = player.next();
				p.setInGame();
				p.resetBow();
			}

			team.clearDead();
		}

		teamManager.spawnTeams(map);

		board.refreshPlayerIcons();
	}
	
	protected void resetRoundWins()
	{
		roundWins.clear();
		
		ArrayList<PaintballTeam> teams = teamManager.getTeams();
		Iterator<PaintballTeam> it = teams.iterator();

		while(it.hasNext())
			roundWins.put(it.next().getId(), 0);
	}
}
