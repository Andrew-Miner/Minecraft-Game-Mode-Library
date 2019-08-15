package SmokyMiner.MiniGames.Lobby.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGScoreboard 
{
	protected Scoreboard board;
	protected MGLobby lobby;
	protected MGManager manager;
	
	public MGScoreboard(MGManager manager, MGLobby lobby)
	{
		this.lobby = lobby;
		this.manager = manager;

		board = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	public void open()
	{
		for(MGPlayer player : this.lobby.getPlayers())
		{
			Player p = Bukkit.getPlayer(player.getID());
			
			if(p != null)
				setScoreboard(p);
		}
	}
	
	public void close()
	{
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		
		for(MGPlayer player : this.lobby.getPlayers())
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
	
	public void resetScoreboard()
	{
		board = Bukkit.getScoreboardManager().getNewScoreboard();
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