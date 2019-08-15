package SmokyMiner.MiniGames.Lobby.Events;

public interface MGLobbyListener 
{
	public void stageStartEvent(MGLobbyStageStartEvent e);
	public void stageEndEvent(MGLobbyStageEndEvent e);
	public void playerJoinedEvent(MGLobbyJoinEvent e);
	public void playerLeaveEvent(MGLobbyLeaveEvent e);
}