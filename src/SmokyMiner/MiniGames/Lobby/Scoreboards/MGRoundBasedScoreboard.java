package SmokyMiner.MiniGames.Lobby.Scoreboards;

import java.util.ArrayList;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Stages.MGGameStage;
import SmokyMiner.MiniGames.Lobby.Team.MGTeamManager;
import SmokyMiner.Minigame.Main.MGManager;

public class MGRoundBasedScoreboard extends MGInGameScoreboard
{
	public MGRoundBasedScoreboard(MGManager manager, MGLobby lobby, MGGameStage stage, int roundLength)
	{
		super(manager, lobby, stage, roundLength);
		info = new ArrayList<MGTeamInfo>();
	}

	@Override
	public void timerFinished()
	{
		stage.checkForEndGame();
	}

	private void rebuildRoundWins(int wins[])
	{
		MGTeamManager teams = stage.getTeamManager();
		int teamCount = teams.getTeamCount();

		for (int i = 0; i < teamCount; i++)
		{
			MGTeamInfo tInfo = info.get(i);

			if (wins == null)
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
		for (MGTeamInfo Tinfo : info)
		{
			board.resetScores(Tinfo.getBoardStrScore());
		}
	}

	@Override
	protected void refreshInfo()
	{
		int count = 0;

		for (MGTeamInfo t : info)
		{
			matchInfo.getScore(t.getBoardStrScore()).setScore(count++);
		}
	}
}
