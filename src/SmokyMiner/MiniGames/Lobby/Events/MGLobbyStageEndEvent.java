package SmokyMiner.MiniGames.Lobby.Events;

import java.util.EventObject;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.Stages.MGLobbyStage;

@SuppressWarnings("serial")
public class MGLobbyStageEndEvent extends EventObject
{
	private MGLobbyStage stage;

	public MGLobbyStageEndEvent(Object source, MGLobbyStage stage) 
	{
		super(source);
		this.stage = stage;
	}
	
	public MGLobbyStage getStage()
	{
		return stage;
	}
	
	public MGLobby getLobby()
	{
		Object obj = super.getSource();
		
		if(obj instanceof MGLobby)
			return (MGLobby) obj;
		else
			return null;
	}
}
