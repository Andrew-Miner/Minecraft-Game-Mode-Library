package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import org.bukkit.ChatColor;

public class SBTeamInfo 
{
	private final int MAX_ICONS_PER_ROW = 6;
	
	private String name;
	private int score;
	private int playerCount;
	private String boardStr;
	private String boardStrScore;
	private String iconColor;
	
	public SBTeamInfo(String name, int score, int playerCount)
	{
		this.iconColor = "" + ChatColor.WHITE;
		this.name = name;
		this.score = score;
		this.playerCount = playerCount;
		rebuildString();
	}
	
	public SBTeamInfo(String name, int score, int playerCount, String color)
	{
		this.iconColor = color;
		this.name = name;
		this.score = score;
		this.playerCount = playerCount;
		rebuildString();
	}
	
	public void setScore(int score)
	{
		this.score = score;
		rebuildString();
	}
	
	public void setPlayerIcons(int playerCount)
	{
		this.playerCount = playerCount;
		rebuildString();
	}

	private void rebuildString()
	{
		boardStrScore = ChatColor.GRAY + "[" + ChatColor.GOLD + "" + score + ChatColor.GRAY + "] " + name + iconColor;
		boardStr = name + iconColor;
		
		int count = playerCount;
		boolean bold = false;
		
		for(int i = 0; i < MAX_ICONS_PER_ROW; i++)
		{
			if(count > 0)
			{
				boardStr += ((char)176);
				boardStrScore += ((char)176);
				count--;
			}
			else
			{
				if(!bold)
				{
					bold = true;
					boardStr += ChatColor.BOLD;
					boardStrScore += ChatColor.BOLD;
				}
				
				boardStr += " ";
				boardStrScore += " ";
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
		rebuildString();
	}

	public int getPlayerIcons() 
	{
		return playerCount;
	}

	public int getScore() 
	{
		return score;
	}

	public String getBoardStr() 
	{
		return boardStr;
	}
	
	public String getBoardStrScore()
	{
		return boardStrScore;
	}
}
