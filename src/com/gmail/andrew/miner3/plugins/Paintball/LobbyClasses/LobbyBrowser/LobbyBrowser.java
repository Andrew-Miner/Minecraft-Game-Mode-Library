package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyBrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemListener;
import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuBlock;
import com.gmail.andrew.miner3.plugins.InventoryMenu.MenuItem;
import com.gmail.andrew.miner3.plugins.InventoryMenu.PagedMenu.BorderedPagedMenu;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.LobbyTools;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;

public class LobbyBrowser 
{
	// TODO: Lobbies were removed while still existing
	// TODO: Move Lobbies when one is removed
	
	public final static int TEMP_MAX_LOBBIES = 7;
	public final static int MENU_SIZE = 27;
	
	public final static String MENU_NAME = ChatColor.UNDERLINE + "Lobby Browser";
	public final static String LOBBY_PREFIX = ChatColor.WHITE + "Lobby ";
	public final static String PLAYERS_PREFIX = ChatColor.YELLOW + "Player Count: ";
	public final static String BLOCK_NAME = ChatColor.GREEN + ChatColor.stripColor(MENU_NAME);
	
	private PluginManager plugin;
	private LobbyManager manager;
	
	private BorderedPagedMenu menu;
	private MenuBlock block;
	
	private int lobbyCount;
	
	public LobbyBrowser(PluginManager plugin, LobbyManager manager)
	{
		this.plugin = plugin;
		this.manager = manager;
		
		lobbyCount = 0;
		menu = new BorderedPagedMenu(plugin, MENU_NAME, MENU_SIZE, 15);
		block = new MenuBlock(plugin, menu, Material.BOOK, -1, BLOCK_NAME, null);
		
		//menu.addItem(5, block);
	}
	
	public LobbyBrowser(PluginManager plugin, LobbyManager manager, ArrayList<Lobby> lobbies)
	{
		this.plugin = plugin;
		this.manager = manager;
		
		lobbyCount = 0;
		menu = new BorderedPagedMenu(plugin, MENU_NAME, MENU_SIZE, 15);
		block = new MenuBlock(plugin, menu, Material.BOOK, -1, BLOCK_NAME, null);
		
		menu.addItem(5, block);
		
		updateBrowser(lobbies);
	}
	
	public void openBrowser(Player p)
	{
		menu.openMenu(p);
	}
	
	public void giveBrowser(Player p)
	{
		block.giveBlock(p);
	}
	
	public void updateBrowser(ArrayList<Lobby> lobbies)
	{
		menu.clearMenu();
		lobbyCount = 0;
		
		for(Lobby lobby : lobbies)
		{
			if(lobbyCount == TEMP_MAX_LOBBIES)
				return;
			
			MenuItem l = new MenuItem(plugin, Material.CHEST, LOBBY_PREFIX + " #" + lobby.getLobbyNumber(), LobbyTools.buildLobbyLore(lobby), new LobbyItemListener(plugin, manager, lobby.getLobbyId()));
			l.setAmount(lobby.getLobbyNumber());
			menu.addItem(l, getNextSlot());
		}
	}

	public MenuItem getLobbyItem(UUID lobbyId)
	{
		ArrayList<HashMap<Integer, MenuItem>> list = menu.getAllItems();

		for(HashMap<Integer, MenuItem> items : list)
		{
			for(Map.Entry<Integer, MenuItem> item : items.entrySet())
			{
				for(ItemListener l : item.getValue().getListeners())
				{
					if(l instanceof LobbyItemListener)
						if(((LobbyItemListener) l).getLobbyID().equals(lobbyId))
							return item.getValue();
				}
			}
		}
		return null;
	}
	
	public void refreshBrowser()
	{
		menu.rebuildInventory();
	}
	
	public void addLobby(Lobby lobby) 
	{
		if(lobbyCount == TEMP_MAX_LOBBIES)
			return;
		
		if(getLobbyItem(lobby.getLobbyId()) == null)
		{
			MenuItem l = new MenuItem(plugin, Material.CHEST, LOBBY_PREFIX + " #" + lobby.getLobbyNumber(), LobbyTools.buildLobbyLore(lobby), new LobbyItemListener(plugin, manager, lobby.getLobbyId()));
			l.setAmount(lobby.getLobbyNumber());
			//menu.addItem(l, getNextSlot());
			menu.addItem(l);
		}
	}
	
	
	// TODO: Compress Blank Spaces Between Icons
	public void removeLobby(Lobby lobby) 
	{
		MenuItem lobbyItem = getLobbyItem(lobby.getLobbyId());
		
		if(lobbyItem != null)
		{
			menu.removeItem(lobbyItem);
		}
	}

	public MenuBlock getBlock() 
	{
		return block;
	}
	
	private int getNextSlot()
	{
		return 9 + 2*(lobbyCount/7) + 1 + lobbyCount++;
	}
}
