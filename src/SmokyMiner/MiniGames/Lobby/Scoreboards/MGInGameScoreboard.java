package SmokyMiner.MiniGames.Lobby.Scoreboards;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Team.MGTeam;
import SmokyMiner.MiniGames.Lobby.Team.MGTeamManager;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerEvent;
import SmokyMiner.MiniGames.Lobby.Timer.MGTimerTask;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGInGameScoreboard extends MGTimerScoreboard implements MGTimerEvent
{
	protected Objective matchInfo;
	protected ArrayList<MGTeamInfo> info;
	protected ArrayList<Team> teams;
	
	public MGInGameScoreboard(MGManager manager, MGLobby lobby, int roundLength)
	{
		super(manager, lobby, roundLength, null);
		super.setTimerEvent(this);
		
		matchInfo = board.registerNewObjective("inGame" + lobby.getLobbyId().toString().substring(0, 7), "dummy");
		matchInfo.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		resetTimer();
		
		info = new ArrayList<MGTeamInfo>();
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
		matchInfo.setDisplayName(MGTimerTask.parseTime(time, true));
	}
	
	@Override
	public void resetTimer()
	{
		timer.resetTimer();
		
		matchInfo.setDisplayName(MGTimerTask.parseTime(getTime(), true));
	}

	@Override
	public void addPlayer(Player p)
	{
		super.addPlayer(p);
		MGTeamManager teams = lobby.getTeams();
		MGPlayer pbp = lobby.findPlayer(p.getUniqueId());
		
		if(pbp == null)
			return;

		MGTeam team = teams.getTeam(pbp);
		
		if(team == null)
			return;
		
		Team sbTeam = this.teams.get(team.getId());
		sbTeam.addEntry(p.getName());
		
		refreshPlayerIcons();
	}
	
	@Override
	public void removePlayer(Player p)
	{
		MGTeam team = lobby.getTeams().getTeam(p.getUniqueId());
		
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
		
		MGTeamManager teams = lobby.getTeams();
		int tCount = teams.getTeamCount();
		
		for(int i = 0; i < tCount; i++)
		{
			MGTeam team = teams.getTeam(i);
			info.add(new MGTeamInfo(team.getTeamName(), 0, team.size(), team.getTeamColor()));
			
			Team sbTeam = board.registerNewTeam(ChatColor.stripColor(team.getTeamName()) + lobby.getLobbyId().toString().substring(0, 7));
			sbTeam.setPrefix(team.getTeamColor());
			sbTeam.setAllowFriendlyFire(false);
			
			ArrayList<MGPlayer> players = team.getPlayers();
			
			for(MGPlayer p : players)
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
		
		MGTeamManager teams = lobby.getTeams();
		int tCount = teams.getTeamCount();
		
		for(int i = 0; i < tCount; i++)
		{
			MGTeam team = teams.getTeam(i);
			
			Team sbTeam = board.registerNewTeam(ChatColor.stripColor(team.getTeamName()) + lobby.getLobbyId().toString().substring(0, 7));
			sbTeam.setPrefix(team.getTeamColor());
			sbTeam.setAllowFriendlyFire(false);
			
			ArrayList<MGPlayer> players = team.getPlayers();
			
			for(MGPlayer p : players)
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
		for(MGTeamInfo Tinfo : info)
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
		
		for(MGTeamInfo t : info)
		{
			matchInfo.getScore(t.getBoardStr()).setScore(count++);
		}
	}
	
	protected void rebuildTeams()
	{
		MGTeamManager teams = lobby.getTeams();
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
		MGTeamManager teams = lobby.getTeams();
		int teamCount = teams.getTeamCount();
		
		for(int i = 0; i < teamCount; i++)
		{
			MGTeam team = teams.getTeam(i);
			MGTeamInfo tInfo = info.get(i);
			
			tInfo.setPlayerIcons(team.size() - team.getDeadCount());
		}
	}
	
	protected int getLongestTeamName() 
	{
		String longName = null;
		int longestNameId = -1;
		
		MGTeamManager teams = lobby.getTeams();
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