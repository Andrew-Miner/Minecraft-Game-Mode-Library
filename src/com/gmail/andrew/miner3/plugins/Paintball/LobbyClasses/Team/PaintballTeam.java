package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;

public class PaintballTeam 
{
	private ArrayList<PBPlayer> players;
	private int teamId;
	private int deadCount;
	private String teamName;
	private String teamColor;
	
	
	public PaintballTeam()
	{
		players = null;
		teamId = -1;
		deadCount = -1;
	}
	
	public PaintballTeam(int teamId, String teamName)
	{
		this.teamId = teamId;
		players = new ArrayList<PBPlayer>();
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor("" + ChatColor.WHITE);
	}
	
	public PaintballTeam(int teamId, String teamName, String color)
	{
		this.teamId = teamId;
		players = new ArrayList<PBPlayer>();
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor(color);
	}

	public PaintballTeam(int teamId, ArrayList<PBPlayer> players, String teamName, String teamColor)
	{
		this.teamId = teamId;
		this.players = players;
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor(teamColor);
	}
	
	public void addPlayer(PBPlayer player)
	{
		players.add(player);
	}
	
	public void removePlayer(PBPlayer player)
	{
		removePlayer(player.getID());
	}
	
	public PBPlayer removePlayer(UUID player)
	{
		Iterator<PBPlayer> it = players.iterator();
		while(it.hasNext())
		{
			PBPlayer p = it.next();
			
			if(p.getID() == player)
			{
				if(p.isDead())
					deadCount--;
				
				it.remove();
				return p;
			}
		}
		return null;
	}
	
	public PBPlayer getPlayer(UUID player)
	{
		Iterator<PBPlayer> it = players.iterator();
		while(it.hasNext())
		{
			PBPlayer p = it.next();
			
			if(p.getID() == player)
				return p;
		}
		return null;
	}
	
	public int getDeadCount()
	{
		return deadCount;
	}
	
	public boolean isDead()
	{
		return deadCount == players.size();
	}
	
	public void clear()
	{
		players.clear();
		deadCount = 0;
	}
	
	public void clearDead()
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().setDead(false);
		}
		
		deadCount = 0;
	}
	
	public boolean setDead(UUID player)
	{
		PBPlayer p = getPlayer(player);
		
		if(p == null)
			return false;
		
		p.setDead(true);
		deadCount++;
		
		return true;
	}
	
	public boolean setAlive(UUID player)
	{
		PBPlayer p = getPlayer(player);
		
		if(p == null)
			return false;
		
		p.setDead(false);
		deadCount--;
		
		return true;
	}
	
	public int getId()
	{
		return teamId;
	}
	
	public int size()
	{
		return players.size();
	}
	
	public ArrayList<PBPlayer> getPlayers()
	{
		return players;
	}

	public String getTeamName() 
	{
		return teamName;
	}

	public void setTeamName(String teamName) 
	{
		this.teamName = teamName;
	}
	
	public void addWin()
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().addWin();
		}
	}
	
	public void addLoss()
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().addLoss();
		}
	}
	
	public void displayTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle)
	{
		Iterator<PBPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			Bukkit.getPlayer(it.next().getID()).sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		}
	}
	
	public void displayTeamTitle(int fadeIn, int stay, int fadeOut)
	{
		String title = ChatColor.GOLD + "Welcome To The " + teamName + " " + ChatColor.GOLD + " Team!";
		String subtitle = "";
		
		
		Iterator<PBPlayer> it = players.iterator();
		
		if(it.hasNext())
		{
			subtitle = Bukkit.getPlayer(it.next().getID()).getName();
		
			while(it.hasNext())
			{
				subtitle += ", " + Bukkit.getPlayer(it.next().getID()).getName();
			}
			
			displayTitle(fadeIn, stay, fadeOut, title, subtitle);
		}
	}

	public String getTeamColor() 
	{
		return teamColor;
	}

	public void setTeamColor(String color) 
	{
		this.teamColor = color;
	}
}
