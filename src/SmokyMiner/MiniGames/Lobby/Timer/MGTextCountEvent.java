package SmokyMiner.MiniGames.Lobby.Timer;

import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Lobby.Stages.MGGameStage;

public class MGTextCountEvent implements MGTimerEvent
{
	private final MGGameStage state;
	private ChatColor wrapperColor, countColor;
	
	public MGTextCountEvent(MGGameStage state)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.GOLD;
		this.countColor = ChatColor.RED;
	}
	
	public MGTextCountEvent(MGGameStage state, ChatColor wrapperColor, ChatColor countColor)
	{
		this.state = state;
		
		this.wrapperColor = wrapperColor;
		this.countColor = countColor;
	}
	
	public void setWrapperColor(ChatColor color)
	{
		this.wrapperColor = color;
	}
	
	public void setCountColor(ChatColor color)
	{
		countColor = color;
	}
	
	@Override
	public void timerFinished() 
	{
		state.lobby().broadcastMessage(wrapperColor + " ===== " + countColor + "GO!" + wrapperColor + " ===== ");
		state.startGame();
	}

	@Override
	public void updateTimer(int time) 
	{
		state.lobby().broadcastMessage(wrapperColor + " ===== " + countColor + " " + time + " " + wrapperColor + " ===== ");
	}
}