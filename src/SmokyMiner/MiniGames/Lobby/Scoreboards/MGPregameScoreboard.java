package SmokyMiner.MiniGames.Lobby.Scoreboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerEvent;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerTask;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGPregameScoreboard extends MGTimerScoreboard implements MGTimerEvent
{
	private Objective playerList;
	private String lobbyName;
	
	public MGPregameScoreboard(MGManager manager, MGLobby lobby, int lobbyLength)
	{	
		super(manager, lobby, lobbyLength, null);
		setTimerEvent(this);
		
		lobbyName = ChatColor.GOLD + "" + ChatColor.UNDERLINE + "Lobby #" + lobby.getLobbyNumber() + ChatColor.RESET + ChatColor.GOLD + "  -  ";

		playerList = board.registerNewObjective("preGame" + lobby.getLobbyId().toString().substring(0, 7), "dummy");
		playerList.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		refreshPlayerList();
	}
	
	@Override
	public void resetScoreboard()
	{	
		playerList.unregister();
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
			playerList.setDisplayName(lobbyName + MGTimerTask.parseTime(timer.getTime(), true));
	}
	
	@Override
	public void timerFinished()
	{
		lobby.nextStage(true);
		resetTimer();
	}
	
	@Override
	public void updateTimer(int time)
	{
		if(time == 30 || time == 5)
			lobby.broadcastMessage(ChatColor.GOLD + "Match starting in " + MGTimerTask.parseTime(time, true) + ChatColor.GOLD + " second!");
		
		playerList.setDisplayName(lobbyName + MGTimerTask.parseTime(time, true));
	}
	
	public Objective getListObjective()
	{
		return playerList;
	}
	
	public void refreshPlayerList()
	{
		for(MGPlayer player : lobby.getPlayers())
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