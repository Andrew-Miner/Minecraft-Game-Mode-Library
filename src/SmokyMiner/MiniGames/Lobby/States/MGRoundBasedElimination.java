package SmokyMiner.MiniGames.Lobby.States;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;
import SmokyMiner.MiniGames.Lobby.Scoreboards.MGRoundBasedScoreboard;
import SmokyMiner.MiniGames.Lobby.Team.MGTeam;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerEvent;
import SmokyMiner.MiniGames.Lobby.Timer.MGTitleCountEvent;

public class MGRoundBasedElimination extends MGMatchState
{
	private HashMap<Integer, Integer> roundWins;
	private int currentRound;
	private int winLimit;

	public MGRoundBasedElimination(MGManager manager, MGLobby lobby, MGMapMetadata map) 
	{
		super(manager, lobby, map);

		roundWins = new HashMap<Integer, Integer>();
		currentRound = 0;
		winLimit = 3;
		board = new MGRoundBasedScoreboard(manager, lobby, lobby.getRoundTickTime());
	}

	public MGRoundBasedElimination(MGManager manager, MGLobby lobby, MGMapMetadata map, int roundsToWin) 
	{
		super(manager, lobby, map);

		roundWins = new HashMap<Integer, Integer>();
		currentRound = 0;
		winLimit = roundsToWin;
		board = new MGRoundBasedScoreboard(manager, lobby, lobby.getRoundTickTime());
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

		MGTimerEvent events = countDownTimer.getTimerEvent();

		if(events instanceof MGTitleCountEvent)
			((MGTitleCountEvent) events).setTitles(MGTitleCountEvent.defCountDownTitle, MGTitleCountEvent.defStartTitle);
	}
	
	@Override
	public MGTeam checkForEndGame()
	{
		if(gameOver)
			return null;
		
		if(!teamManager.playable())
		{
			super.endMatch("GAME OVER", ChatColor.RED + " Not Enough Players", 10L*20L, true);
			return null;
		}
		

		ArrayList<MGTeam> alive = teamManager.getAliveTeams();

		if(alive.size() == 1)			// Only 1 Team Is Left Living
		{
			MGTeam winner = alive.get(0);
			board.stopTimer();
			int roundsWon = roundWon(winner);

			checkForRoundLimit(winner, roundsWon);

			return winner;
		}
		else if(board.getTime() == 0)	// Round/Game Timer Ended
		{
			MGTeam winner = null;

			for(MGTeam team : alive)
			{
				if(winner == null || team.size() > winner.size())
					winner = team;
			}

			ArrayList<MGTeam> tied = new ArrayList<MGTeam>();

			for(MGTeam team : alive)
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

	protected void checkForRoundLimit(MGTeam winner, int roundsWon)
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
			tasks.add(Bukkit.getScheduler().runTaskLater(manager.plugin(), new Runnable() { public void run() {startNewRound();}}, 20L*10L));
		}
	}

	protected void checkForGameTiedLimit(ArrayList<MGTeam> tiedTeams, int tiedScore)
	{
		displayingMsg = true;
		
		if(tiedTeams.size() == 1)
		{
			checkForRoundLimit(tiedTeams.get(0), tiedScore);
			return;
		}

		String subtitle = null;

		for(MGTeam team : tiedTeams)
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
			tasks.add(Bukkit.getScheduler().runTaskLater(manager.plugin(), new Runnable() { public void run() {startNewRound();}}, 20L*10L));
		}
	}

	protected int roundTied(ArrayList<MGTeam> tiedTeams)
	{
		ArrayList<MGTeam> tiedFinalScore = new ArrayList<MGTeam>();
		int highestScore = 0;

		for(MGTeam team : tiedTeams)
		{
			int newEntry = roundWins.get(team.getId()) + 1;
			roundWins.put(team.getId(), newEntry);

			if(newEntry > highestScore)
				highestScore = newEntry;
		}

		for(MGTeam team : tiedTeams)
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


		MGRoundBasedScoreboard sb = (MGRoundBasedScoreboard) board;
		int wins[] = new int[roundWins.size()];

		for(Map.Entry<Integer, Integer> entry : roundWins.entrySet())
		{
			wins[entry.getKey()] = entry.getValue();
		}

		sb.refreshRoundWins(wins);

		return highestScore;
	}

	protected int roundWon(MGTeam winner)
	{
		int roundsWon = roundWins.get(winner.getId()) + 1;
		roundWins.put(winner.getId(), roundsWon);

		MGRoundBasedScoreboard sb = (MGRoundBasedScoreboard) board;
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

		MGTimerEvent events = countDownTimer.getTimerEvent();
		if(events instanceof MGTitleCountEvent)
		{
			MGTitleCountEvent titleEvent = (MGTitleCountEvent) events;
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
		
		Iterator<MGTeam> it = teamManager.getTeams().iterator();

		while(it.hasNext())
		{
			MGTeam team = it.next();
			Iterator<MGPlayer> player = team.getPlayers().iterator();

			while(player.hasNext())
			{
				MGPlayer p = player.next();
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
		
		ArrayList<MGTeam> teams = teamManager.getTeams();
		Iterator<MGTeam> it = teams.iterator();

		while(it.hasNext())
			roundWins.put(it.next().getId(), 0);
	}
}