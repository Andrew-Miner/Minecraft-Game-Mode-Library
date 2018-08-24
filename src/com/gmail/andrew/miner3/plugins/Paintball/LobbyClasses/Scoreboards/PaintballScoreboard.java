package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;

public class PaintballScoreboard 
{
	protected Scoreboard board;
	protected Lobby lobby;
	protected JavaPlugin plugin;
	
	public PaintballScoreboard(JavaPlugin plugin, Lobby lobby)
	{
		this.lobby = lobby;
		this.plugin = plugin;

		board = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void open()
	{
		for(PBPlayer player : this.lobby.getPlayers())
		{
			Player p = Bukkit.getPlayer(player.getID());
			
			if(p != null)
				setScoreboard(p);
		}
	}
	
	public void close()
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		for(PBPlayer player : this.lobby.getPlayers())
		{
			Player p = Bukkit.getPlayer(player.getID());
			if(p != null)
				p.setScoreboard(manager.getNewScoreboard());
		}
	}
	
	public void setScoreboard(Player p)
	{
		p.setScoreboard(board);
	}
	
	public Scoreboard getScoreboard()
	{
		return board;
	}
	
	public void removePlayer(Player p)
	{
		board.resetScores(p.getName());
	}
	
	public void addPlayer(Player p)
	{
		setScoreboard(p);
	}
	
}
