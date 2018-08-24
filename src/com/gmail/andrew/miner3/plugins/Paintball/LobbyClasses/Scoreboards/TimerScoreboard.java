package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerEvent;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerTask;

public class TimerScoreboard extends PaintballScoreboard
{	
	protected TimerTask timer;
	
	public TimerScoreboard(JavaPlugin plugin, Lobby lobby, int time, TimerEvent timerEvents)
	{
		super(plugin, lobby);
		timer = new TimerTask(plugin, time, timerEvents);
	}
	
	public int getTime()
	{
		return timer.getTime();
	}
	
	public void stopTimer()
	{
		timer.stopTimer();
	}
	
	public boolean timerActive()
	{
		return timer.timerActive();
	}
	
	public void resumeTimer()
	{
		timer.resumeTimer();
	}
	
	public void startTimer()
	{
		timer.startTimer();
	}
	
	public boolean startTimer(int length)
	{	
		return timer.startTimer(length);
	}
		
	public void resetTimer() 
	{
		timer.resetTimer();
	}
	
	public void setTimerEvent(TimerEvent event)
	{
		timer.setTimerEvent(event);
	}
}


