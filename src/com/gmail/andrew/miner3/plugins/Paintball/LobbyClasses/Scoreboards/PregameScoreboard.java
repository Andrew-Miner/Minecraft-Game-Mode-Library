package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerEvent;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerTask;

public class PregameScoreboard extends TimerScoreboard implements TimerEvent
{
	private Objective playerList;
	private String lobbyName;
	
	public PregameScoreboard(JavaPlugin plugin, Lobby lobby, int lobbyLength)
	{	
		super(plugin, lobby, lobbyLength, null);
		setTimerEvent(this);
		
		lobbyName = ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Lobby #" + lobby.getLobbyNumber() + ChatColor.RESET + ChatColor.GOLD + "  -  ";

		playerList = board.registerNewObjective("preGame" + lobby.getLobbyId().toString().substring(0, 7), "dummy");
		playerList.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		refreshPlayerList();
	}
	
	@Override
	public void close()
	{
		timer.resetTimer();
		super.close();
	}
	
	@Override
	public void removePlayer(Player p)
	{
		super.removePlayer(p);
		updatePlayerCount();
	}
	
	@Override
	public void addPlayer(Player p)
	{
		super.setScoreboard(p);
		refreshPlayerList();
		
		if(!updatePlayerCount() && !timer.timerActive())
			timer.startTimer(60);
	}
	
	@Override
	public void resetTimer()
	{
		timer.resetTimer();
		
		if(playerList != null)
			playerList.setDisplayName(lobbyName + TimerTask.parseTime(timer.getTime(), true));
	}
	
	@Override
	public void timerFinished()
	{
		lobby.startMatch();
		resetTimer();
	}
	
	@Override
	public void updateTimer(int time)
	{
		if(time == 30 || time == 5)
			lobby.broadcastMessage(ChatColor.GOLD + "Match starting in " + TimerTask.parseTime(time, true) + ChatColor.GOLD + " second!");
		
		playerList.setDisplayName(lobbyName + TimerTask.parseTime(time, true));
	}
	
	public Objective getListObjective()
	{
		return playerList;
	}
	
	public void refreshPlayerList()
	{
		for(PBPlayer player : lobby.getPlayers())
		{
			playerList.getScore(Bukkit.getPlayer(player.getID()).getName()).setScore(player.getCurrentScore());
		}
	}
	
	public boolean updatePlayerCount()
	{
		if(lobby.getPlayers().size() < lobby.getMinPlayers())
		{
			resetTimer();
			playerList.setDisplayName(lobbyName + ChatColor.GRAY + lobby.getPlayers().size() + "/" + lobby.getMinPlayers());
			return true;
		}
		
		return false;
	}
}