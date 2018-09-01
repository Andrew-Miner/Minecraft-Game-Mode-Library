package SmokyMiner.MiniGames.Lobby.Events;

import java.util.EventObject;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Player.MGPlayer;

@SuppressWarnings("serial")
public class MGLobbyJoinEvent extends EventObject
{
	private MGPlayer player;

	public MGLobbyJoinEvent(Object source, MGPlayer player) 
	{
		super(source);
		this.player = player;
	}
	
	public MGPlayer getMGPlayer()
	{
		return player;
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