package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyEvents;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;

public interface LobbyListener 
{
	public void matchStartEvent(Lobby lobby);
	public void matchEndEvent(Lobby lobby);
	public void pregameStartEvent(Lobby lobby);
	public void pregameEndEvent(Lobby lobby);
	public void playerJoinedEvent(PBLobbyJoinEvent e);
	public void playerLeaveEvent(PBLobbyLeaveEvent e);
}
