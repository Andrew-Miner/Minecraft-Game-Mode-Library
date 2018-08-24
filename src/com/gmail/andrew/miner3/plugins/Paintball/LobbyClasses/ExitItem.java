package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.andrew.miner3.plugins.InventoryMenu.ActionBlock.ActionBlock;
import com.gmail.andrew.miner3.plugins.InventoryMenu.ActionBlock.ActionBlockListener;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;

public class ExitItem extends ActionBlockListener
{
	public static final String EXIT_NAME = ChatColor.RED + "Leave Lobby";
	PluginManager plugin;
	ActionBlock item;
	Lobby lobby;
	
	public ExitItem(PluginManager plugin, Lobby lobby)
	{
		this.plugin = plugin;
		this.lobby = lobby;
		
		item = new ActionBlock(plugin, Material.MINECART, -1, EXIT_NAME, null, this);
	}
	
	public ItemStack getItemStack()
	{
		return item.getItemStack();
	}
	
	public void giveBlock(Player p)
	{
		item.giveBlock(p);
	}
	
	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		if(lobby.findPlayer(e.getPlayer().getUniqueId()) != null)
			lobby.removePlayer(e.getPlayer().getUniqueId());
	}
}
