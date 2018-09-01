package SmokyMiner.MiniGames.Lobby.Timer;

import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Lobby.States.MGMatchState;

public class MGTextCountEvent implements MGTimerEvent
{
	private final MGMatchState state;
	private ChatColor wrapperColor, countColor;
	
	public MGTextCountEvent(MGMatchState state)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.GOLD;
		this.countColor = ChatColor.RED;
	}
	
	public MGTextCountEvent(MGMatchState state, ChatColor wrapperColor, ChatColor countColor)
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
		state.broadcastMessage(wrapperColor + " ===== " + countColor + "GO!" + wrapperColor + " ===== ");
		state.beginMatch();
	}

	@Override
	public void updateTimer(int time) 
	{
		state.broadcastMessage(wrapperColor + " ===== " + countColor + " " + time + " " + wrapperColor + " ===== ");
	}
}