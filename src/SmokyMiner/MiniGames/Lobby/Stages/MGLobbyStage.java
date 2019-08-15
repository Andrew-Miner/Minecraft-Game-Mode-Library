package SmokyMiner.MiniGames.Lobby.Stages;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.MGLobbyTools;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyJoinEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyLeaveEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyListener;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageEndEvent;
import SmokyMiner.MiniGames.Lobby.Events.MGLobbyStageStartEvent;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGManager;

public class MGLobbyStage implements Listener, MGLobbyListener
{
	protected final MGManager manager;
	
	protected MGLobby lobby;
	protected boolean active;
	protected MGMapMetadata map;
	
	public MGLobbyStage(MGManager manager)
	{
		this.manager = manager;
		this.lobby = null;
		this.active = false;
		this.map = null;
	}
	
	public MGLobbyStage(MGManager manager, MGLobby lobby)
	{
		this.manager = manager;
		this.lobby = lobby;
		this.active = false;
		this.map = null;
	}
	
	public MGLobbyStage(MGManager manager, MGMapMetadata map)
	{
		this.manager = manager;
		this.lobby = null;
		this.active = false;
		this.map = map;
	}
	
	public MGLobbyStage(MGManager manager, MGLobby lobby, MGMapMetadata map)
	{
		this.manager = manager;
		this.lobby = lobby;
		this.active = false;
		this.map = map;
	}
	
	public void close()
	{
		HandlerList.unregisterAll(this);
		
		if(lobby != null)
			lobby.unregisterListener(this);
	}
	
	public void setLobby(MGLobby lobby)
	{
		this.lobby = lobby;
	}
	
	public MGLobby lobby()
	{
		return lobby;
	}
	
	public void startStage()
	{
		active = true;
		
		if(lobby != null)
		{
			manager.plugin().getServer().getPluginManager().registerEvents(this, manager.plugin());
			lobby.registerListener(this);
		}
	}
	
	public void endStage()
	{
		active = false;
		HandlerList.unregisterAll(this);
		
		if(lobby != null)
			lobby.unregisterListener(this);
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public void sendChatMessege(UUID player, String msg, boolean allChat)
	{
		if(lobby == null)
			throw new IllegalStateException("lobby instance null");
		
		MGPlayer pbp = lobby.findPlayer(player);

		if(pbp == null)
			return;

		Player p = Bukkit.getPlayer(pbp.getID());
		lobby.broadcastMessage(ChatColor.WHITE + "<" + p.getName() + "> " + msg);
	}
	
	@EventHandler
	public void playerChatEvent(AsyncPlayerChatEvent e)
	{
		if(lobby == null)
			throw new IllegalStateException("lobby instance null");
		
		if(lobby.findPlayer(e.getPlayer().getUniqueId()) == null)
			return;
		
		Set<Player> recipients = e.getRecipients();
		recipients.clear();
		
		ArrayList<MGPlayer> newRecips = lobby.getPlayers();

		for(MGPlayer pbp : newRecips)
		{
			recipients.add(Bukkit.getServer().getPlayer(pbp.getID()));
		}
	}
	
	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent e)
	{
		if(lobby == null)
			throw new IllegalStateException("lobby instance null");
		
		if(map == null)
			return;
		
		Player p = e.getPlayer();
		if(lobby.findPlayer(p.getUniqueId()) == null)
			return;
		
		if(!map.contains(e.getTo(), false))
			p.setVelocity(MGLobbyTools.correctVelocity(p.getLocation(), map.getMapCenter(), .3));
	}
	
	@EventHandler
	public void entityDamageEntity(EntityDamageByEntityEvent e)
	{
		if(lobby == null)
			throw new IllegalStateException("lobby instance null");
		
		if(lobby.findPlayer(e.getEntity().getUniqueId()) != null || lobby.findPlayer(e.getDamager().getUniqueId()) != null)
			e.setCancelled(true);
	}

	@Override
	public void stageStartEvent(MGLobbyStageStartEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stageEndEvent(MGLobbyStageEndEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerJoinedEvent(MGLobbyJoinEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerLeaveEvent(MGLobbyLeaveEvent e) {
		// TODO Auto-generated method stub
		
	}
}
