package SmokyMiner.MiniGames.Lobby.Timer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MGTimerTask
{
	private JavaPlugin plugin;
	private int timerLength;
	private BukkitTask timerTask;
	private int time;
	private final long tickInterval;
	private MGTimerEvent timerEvent;
	
	public MGTimerTask(JavaPlugin plugin, int time, MGTimerEvent timerEvents)
	{
		timerLength = time;
		timerTask = null;
		this.timerEvent = timerEvents;
		this.plugin = plugin;
		tickInterval = 20L;
	}	
	
	public MGTimerTask(JavaPlugin plugin, int time, MGTimerEvent timerEvent, long tickInterval)
	{
		timerLength = time;
		timerTask = null;
		this.timerEvent = timerEvent;
		this.plugin = plugin;
		this.tickInterval = tickInterval;
	}
	
	public void setTimerEvent(MGTimerEvent event)
	{
		timerEvent = event;
	}
	
	public MGTimerEvent getTimerEvent()
	{
		return timerEvent;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public void stopTimer()
	{
		if(timerTask != null)
		{
			if(!timerTask.isCancelled())
				timerTask.cancel();
			timerTask = null;
		}
	}
	
	public boolean timerActive()
	{
		return timerTask != null;
	}
	
	public void resumeTimer()
	{
		if(timerTask == null)
			startTimer(time);
	}
	
	public void startTimer()
	{
		startTimer(timerLength);
	}
	
	public boolean startTimer(int length)
	{	
		if(timerTask != null)
			resetTimer();
		
		time = length;
		
		timerTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run() 
			{
				if(time == 0)
				{
					timerEvent.updateTimer(time);
					
					stopTimer();
					
					if(timerEvent != null)
						timerEvent.timerFinished();
				}
				else
				{
					if(timerEvent != null)
						timerEvent.updateTimer(time);
					time--;
				}
			}
			
		}, 0, tickInterval);
		
		return true;
	}
		
	public void resetTimer() 
	{
		stopTimer();
		time = timerLength;
	}
	
	public static String parseTime(int time, boolean addColor)
	{
		int minutes = time / 60;
		int seconds = time % 60;

		if(minutes > 59)
			minutes = 59;

		String rtn = "";

		if(minutes < 10)
			rtn += "0";

		rtn += minutes + ":";

		if(seconds < 10)
			rtn += "0";
		rtn += seconds;

		if(!addColor)
			return rtn;
		
		if(minutes == 0 && seconds <= 30)
			return ChatColor.RED + rtn;

		return ChatColor.GRAY + rtn;
	}
}
