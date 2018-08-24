package com.gmail.andrew.miner3.plugins.Paintball.TimerClasses;

import org.bukkit.ChatColor;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.MatchState;

public class TextCountEvent implements TimerEvent
{
	private final MatchState state;
	private ChatColor wrapperColor, countColor;
	
	public TextCountEvent(MatchState state)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.GOLD;
		this.countColor = ChatColor.RED;
	}
	
	public TextCountEvent(MatchState state, ChatColor wrapperColor, ChatColor countColor)
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
