package SmokyMiner.MiniGames.Lobby.Scoreboards;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerEvent;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerTask;
import SmokyMiner.Minigame.Main.MGManager;

public class MGTimerScoreboard extends MGScoreboard
{	
	protected MGTimerTask timer;
	
	public MGTimerScoreboard(MGManager manager, MGLobby lobby, int time, MGTimerEvent timerEvents)
	{
		super(manager, lobby);
		timer = new MGTimerTask(manager.plugin(), time, timerEvents);
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
	
	public void setTimerEvent(MGTimerEvent event)
	{
		timer.setTimerEvent(event);
	}
}
