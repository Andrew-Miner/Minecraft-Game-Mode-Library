package SmokyMiner.MiniGames.Lobby.Timer;

import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Lobby.States.MGMatchState;

public class MGTitleCountEvent implements MGTimerEvent
{
	public static final String defCountDownTitle = "Match Begins In";
	public static final String defStartTitle = "Match Start";
	
	private final MGMatchState state;
	private ChatColor wrapperColor, countColor;
	private int tickInterval, titleFade;
	private String countDownTitle, startTitle;
	
	public MGTitleCountEvent(MGMatchState state)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.WHITE;
		this.countColor = ChatColor.YELLOW;
		
		this.tickInterval = 20;
		
		countDownTitle = defCountDownTitle;
		startTitle = defStartTitle;
		this.titleFade = -1;
	}
	
	public MGTitleCountEvent(MGMatchState state, int tickInterval)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.WHITE;
		this.countColor = ChatColor.YELLOW;
		
		this.tickInterval = tickInterval;
		
		countDownTitle = defCountDownTitle;
		startTitle = defStartTitle;
		this.titleFade = -1;
	}
	
	public MGTitleCountEvent(MGMatchState state, int tickInterval, int titleFade)
	{
		this.state = state;
		
		this.wrapperColor = ChatColor.WHITE;
		this.countColor = ChatColor.YELLOW;
		
		this.tickInterval = tickInterval;
		
		countDownTitle = defCountDownTitle;
		startTitle = defStartTitle;
		
		this.titleFade = titleFade;
	}
	
	public MGTitleCountEvent(MGMatchState state, int tickInterval, ChatColor wrapperColor, ChatColor countColor)
	{
		this.state = state;
		
		this.wrapperColor = wrapperColor;
		this.countColor = countColor;
		
		this.tickInterval = tickInterval;
		this.titleFade = -1;
	}
	
	public MGTitleCountEvent(MGMatchState state, int tickInterval, ChatColor wrapperColor, ChatColor countColor, int titleFade)
	{
		this.state = state;
		
		this.wrapperColor = wrapperColor;
		this.countColor = countColor;
		
		this.tickInterval = tickInterval;
		this.titleFade = titleFade;
	}
	
	public void setWrapperColor(ChatColor color)
	{
		this.wrapperColor = color;
	}
	
	public void setCountColor(ChatColor color)
	{
		countColor = color;
	}
	
	public void setTitles(String countDownTitle, String startTitle)
	{
		this.countDownTitle = countDownTitle;
		this.startTitle = startTitle;
	}
	
	public void setTitleFade(int time)
	{
		titleFade = time;
	}
	
	@Override
	public void timerFinished() 
	{
		state.getTeamManager().displayTitles(0, tickInterval+1, 0, ""/*wrapperColor + "Match Start"*/, countColor + startTitle);
		state.beginMatch();
	}

	@Override
	public void updateTimer(int time) 
	{
		if(time <= titleFade && titleFade != -1)
			state.getTeamManager().displayTitles(0, tickInterval+1, 0, "", countColor + "" + time);
		else
			state.getTeamManager().displayTitles(0, tickInterval+1, 0, wrapperColor + countDownTitle, countColor + "" + time);
	}
}