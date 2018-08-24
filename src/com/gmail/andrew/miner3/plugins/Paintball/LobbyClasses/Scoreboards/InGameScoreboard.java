package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.PaintballTeam;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.TeamManager;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerEvent;
import com.gmail.andrew.miner3.plugins.Paintball.TimerClasses.TimerTask;

public class InGameScoreboard extends TimerScoreboard implements TimerEvent
{
	protected Objective matchInfo;
	protected ArrayList<SBTeamInfo> info;
	protected ArrayList<Team> teams;
	
	public InGameScoreboard(JavaPlugin plugin, Lobby lobby, int roundLength)
	{
		super(plugin, lobby, roundLength, null);
		super.setTimerEvent(this);
		
		matchInfo = board.registerNewObjective("inGame" + lobby.getLobbyId().toString().substring(0, 7), "dummy");
		matchInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		resetTimer();
		
		info = new ArrayList<SBTeamInfo>();
		teams = new ArrayList<Team>();
	}
	
	public Objective getMatchObjective()
	{
		return matchInfo;
	}
	
	@Override
	public void timerFinished()
	{
		lobby.endMatch();
		resetTimer();
	}

	@Override
	public void updateTimer(int time)
	{
		matchInfo.setDisplayName(TimerTask.parseTime(time, true));
	}
	
	@Override
	public void resetTimer()
	{
		timer.resetTimer();
		
		matchInfo.setDisplayName(TimerTask.parseTime(getTime(), true));
	}

	@Override
	public void addPlayer(Player p)
	{
		super.addPlayer(p);
		TeamManager teams = lobby.getTeams();
		PBPlayer pbp = lobby.findPlayer(p.getUniqueId());
		
		if(pbp == null)
			return;

		PaintballTeam team = teams.getTeam(pbp);
		
		if(team == null)
			return;
		
		Team sbTeam = this.teams.get(team.getId());
		sbTeam.addEntry(p.getName());
		
		refreshPlayerIcons();
	}
	
	@Override
	public void removePlayer(Player p)
	{
		PaintballTeam team = lobby.getTeams().getTeam(p.getUniqueId());
		
		if(team != null)
		{
			Team sbTeam = teams.get(team.getId());
			sbTeam.removeEntry(p.getName());
		}
		
		super.removePlayer(p);

		refreshPlayerIcons();
		rebuildSBTeams();
	}
	
	public void resetScoreboard()
	{
		resetTimer();
		
		clearRows();
		clearTeams();
		info.clear();
		
		TeamManager teams = lobby.getTeams();
		int tCount = teams.getTeamCount();
		
		for(int i = 0; i < tCount; i++)
		{
			PaintballTeam team = teams.getTeam(i);
			info.add(new SBTeamInfo(team.getTeamName(), 0, team.size(), team.getTeamColor()));
			
			Team sbTeam = board.registerNewTeam(ChatColor.stripColor(team.getTeamName()) + lobby.getLobbyId().toString().substring(0, 7));
			sbTeam.setPrefix(team.getTeamColor());
			sbTeam.setAllowFriendlyFire(false);
			
			ArrayList<PBPlayer> players = team.getPlayers();
			
			for(PBPlayer p : players)
			{
				sbTeam.addEntry(Bukkit.getPlayer(p.getID()).getName());
			}
			
			this.teams.add(sbTeam);
		}
		
		rebuildTeams();
		refreshInfo();
	}
	
	private void rebuildSBTeams()
	{
		clearTeams();
		
		TeamManager teams = lobby.getTeams();
		int tCount = teams.getTeamCount();
		
		for(int i = 0; i < tCount; i++)
		{
			PaintballTeam team = teams.getTeam(i);
			
			Team sbTeam = board.registerNewTeam(ChatColor.stripColor(team.getTeamName()) + lobby.getLobbyId().toString().substring(0, 7));
			sbTeam.setPrefix(team.getTeamColor());
			sbTeam.setAllowFriendlyFire(false);
			
			ArrayList<PBPlayer> players = team.getPlayers();
			
			for(PBPlayer p : players)
			{
				sbTeam.addEntry(Bukkit.getPlayer(p.getID()).getName());
			}
			
			this.teams.add(sbTeam);
		}
	}
	
	private void clearTeams() 
	{
		for(Team team : teams)
		{
			team.unregister();
		}
		teams.clear();
	}

	protected void clearRows()
	{
		for(SBTeamInfo Tinfo : info)
		{
			board.resetScores(Tinfo.getBoardStr());
		}
	}
	
	public void refreshTeams()
	{
		clearRows();
		rebuildTeams();
		refreshInfo();
	}
	
	public void refreshPlayerIcons()
	{
		clearRows();
		rebuildPlayerIcons();
		refreshInfo();
	}
	
	protected void refreshInfo()
	{
		int count = 0;
		
		for(SBTeamInfo t : info)
		{
			matchInfo.getScore(t.getBoardStr()).setScore(count++);
		}
	}
	
	protected void rebuildTeams()
	{
		TeamManager teams = lobby.getTeams();
		int size = info.size();
		
		int longestNameId = getLongestTeamName();
		String longName = ChatColor.stripColor(teams.getTeam(longestNameId).getTeamName()) + ":";
		
		
		for(int i = 0; i < size; i++)
		{
			String teamName = teams.getTeam(i).getTeamName() + ":";
			
			if(i != longestNameId)
			{
				int spaceCount = longName.length() - ChatColor.stripColor(teamName).length();
				teamName += getSpaces(spaceCount + 1);
			}
			else
				teamName += getSpaces(1);
			
			info.get(i).setName(teamName);
		}
	}
	
	protected void rebuildPlayerIcons()
	{
		TeamManager teams = lobby.getTeams();
		int teamCount = teams.getTeamCount();
		
		for(int i = 0; i < teamCount; i++)
		{
			PaintballTeam team = teams.getTeam(i);
			SBTeamInfo tInfo = info.get(i);
			
			tInfo.setPlayerIcons(team.size() - team.getDeadCount());
		}
	}
	
	protected int getLongestTeamName() 
	{
		String longName = null;
		int longestNameId = -1;
		
		TeamManager teams = lobby.getTeams();
		int maxTeams = teams.getTeamCount();
		
		for(int i = 0; i < maxTeams; i++)
		{
			if(longName == null)
			{
				longestNameId = i;
				longName = ChatColor.stripColor(teams.getTeam(i).getTeamName());
			}
			else
			{
				String newName =  ChatColor.stripColor(teams.getTeam(i).getTeamName());
				
				if(newName.length() > longName.length())
				{
					longestNameId = i;
					longName = ChatColor.stripColor(newName);
				}
			}
		}
		return longestNameId;
	}
	
	protected String getSpaces(int spaceCount)
	{
		String spaces = "&l";
		
		for(int i = 0; i < spaceCount; i++)
		{
			spaces += " ";
		}
		
		spaces += "&r";
		
		return ChatColor.translateAlternateColorCodes('&', spaces);
	}
	
	public Team getTeamObject(int teamId)
	{
		if(teamId > teams.size()-1 || teamId < 0)
			return null;
		return teams.get(teamId);
	}
	
}
