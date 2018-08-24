package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents;

import java.util.EventObject;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;

@SuppressWarnings("serial")
public class PBLobbyJoinEvent extends EventObject
{
	private PBPlayer player;

	public PBLobbyJoinEvent(Object source, PBPlayer player) 
	{
		super(source);
		this.player = player;
	}
	
	public PBPlayer getPBPlayer()
	{
		return player;
	}
	
	public Lobby getLobby()
	{
		Object obj = super.getSource();
		
		if(obj instanceof Lobby)
			return (Lobby) obj;
		else
			return null;
	}
}
