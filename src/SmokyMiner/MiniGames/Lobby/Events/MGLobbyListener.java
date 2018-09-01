package SmokyMiner.MiniGames.Lobby.Events;

import SmokyMiner.MiniGames.Lobby.MGLobby;

public interface MGLobbyListener 
{
	public void matchStartEvent(MGLobby lobby);
	public void matchEndEvent(MGLobby lobby);
	public void pregameStartEvent(MGLobby lobby);
	public void pregameEndEvent(MGLobby lobby);
	public void playerJoinedEvent(MGLobbyJoinEvent e);
	public void playerLeaveEvent(MGLobbyLeaveEvent e);
}