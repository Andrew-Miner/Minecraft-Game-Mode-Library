package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.MatchState;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates.RoundBasedElimination;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.PaintballTeam;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team.TeamManager;

public class RoundBasedScoreboard extends InGameScoreboard
{
	public RoundBasedScoreboard(JavaPlugin plugin, Lobby lobby, int roundLength) 
	{
		super(plugin, lobby, roundLength);
		info = new ArrayList<SBTeamInfo>();
	}	

	@Override
	public void timerFinished()
	{
		MatchState state = lobby.getMatchState();

		if(state instanceof RoundBasedElimination)
			state.checkForEndGame();
		else
			lobby.endMatch();
	}
	
	private void rebuildRoundWins(int wins[])
	{
		TeamManager teams = lobby.getTeams();
		int teamCount =  teams.getTeamCount();
		
		for(int i = 0; i < teamCount; i++)
		{
			SBTeamInfo tInfo = info.get(i);
			
			if(wins == null)
				tInfo.setScore(0);
			else
				tInfo.setScore(wins[i]);
		}
	}
	
	public void refreshRoundWins(int wins[]) 
	{
		clearRows();
		rebuildRoundWins(wins);
		refreshInfo();
	}
	
	@Override
	protected void clearRows()
	{
		for(SBTeamInfo Tinfo : info)
		{
			board.resetScores(Tinfo.getBoardStrScore());
		}
	}
	
	@Override
	protected void refreshInfo()
	{
		int count = 0;
		
		for(SBTeamInfo t : info)
		{
			matchInfo.getScore(t.getBoardStrScore()).setScore(count++);
		}
	}
}
