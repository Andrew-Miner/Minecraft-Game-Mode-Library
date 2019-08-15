package SmokyMiner.MiniGames.Lobby.Browser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuBlock;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;
import SmokyMiner.MiniGames.InventoryMenu.PagedMenu.MGPagedBorderedMenu;
import SmokyMiner.MiniGames.Lobby.MGLobby;
import SmokyMiner.MiniGames.Lobby.MGLobbyTools;
import SmokyMiner.MiniGames.Lobby.MGMatchMaker;
import SmokyMiner.Minigame.Main.MGManager;

public class MGLobbyBrowser
{
	// TODO: Lobbies were removed while still existing
	// TODO: Move Lobbies when one is removed

	public final static int TEMP_MAX_LOBBIES = 7;
	public final static int MENU_SIZE = 27;

	public final static String MENU_NAME = ChatColor.UNDERLINE + "Lobby Browser";
	public final static String LOBBY_PREFIX = ChatColor.WHITE + "Lobby ";
	public final static String PLAYERS_PREFIX = ChatColor.YELLOW + "Player Count: ";
	public final static String BLOCK_NAME = ChatColor.GREEN + ChatColor.stripColor(MENU_NAME);

	private MGManager mgManager;
	private MGMatchMaker manager;

	private MGPagedBorderedMenu menu;
	private MGMenuBlock block;

	private int lobbyCount;

	public MGLobbyBrowser(MGManager mgManager, MGMatchMaker lbManager)
	{
		this.mgManager = mgManager;
		this.manager = lbManager;

		lobbyCount = 0;
		menu = new MGPagedBorderedMenu(mgManager.plugin(), MENU_NAME, MENU_SIZE, Material.BLACK_STAINED_GLASS_PANE);
		block = new MGMenuBlock(mgManager.plugin(), menu, Material.BOOK, -1, BLOCK_NAME, null);

		// menu.addItem(5, block);
	}

	public MGLobbyBrowser(MGManager mgManager, MGMatchMaker lbManager, ArrayList<MGLobby> lobbies)
	{
		this.mgManager = mgManager;
		this.manager = lbManager;

		lobbyCount = 0;
		menu = new MGPagedBorderedMenu(mgManager.plugin(), MENU_NAME, MENU_SIZE, Material.BLACK_STAINED_GLASS_PANE);
		block = new MGMenuBlock(mgManager.plugin(), menu, Material.BOOK, -1, BLOCK_NAME, null);

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

	public void updateBrowser(ArrayList<MGLobby> lobbies)
	{
		menu.clearMenu();
		lobbyCount = 0;

		for (MGLobby lobby : lobbies)
		{
			if (lobbyCount == TEMP_MAX_LOBBIES)
				return;

			MGMenuItem l = new MGMenuItem(mgManager.plugin(), Material.CHEST,
					LOBBY_PREFIX + " #" + lobby.getLobbyNumber(), MGLobbyTools.buildLobbyLore(lobby),
					new MGLobbyItemListener(mgManager.plugin(), manager, lobby.getLobbyId()));
			l.setAmount(lobby.getLobbyNumber());
			menu.addItem(l, getNextSlot());
		}
	}

	public MGMenuItem getLobbyItem(UUID lobbyId)
	{
		ArrayList<HashMap<Integer, MGMenuItem>> list = menu.getAllItems();

		for (HashMap<Integer, MGMenuItem> items : list)
		{
			for (Map.Entry<Integer, MGMenuItem> item : items.entrySet())
			{
				for (MGItemListener l : item.getValue().getListeners())
				{
					if (l instanceof MGLobbyItemListener)
						if (((MGLobbyItemListener) l).getLobbyID().equals(lobbyId))
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

	public void addLobby(MGLobby lobby)
	{
		if (lobbyCount == TEMP_MAX_LOBBIES)
			return;

		if (getLobbyItem(lobby.getLobbyId()) == null)
		{
			MGMenuItem l = new MGMenuItem(mgManager.plugin(), Material.CHEST,
					LOBBY_PREFIX + " #" + lobby.getLobbyNumber(), MGLobbyTools.buildLobbyLore(lobby),
					new MGLobbyItemListener(mgManager.plugin(), manager, lobby.getLobbyId()));
			l.setAmount(lobby.getLobbyNumber());
			// menu.addItem(l, getNextSlot());
			menu.addItem(l);
		}
	}

	// TODO: Compress Blank Spaces Between Icons
	public void removeLobby(MGLobby lobby)
	{
		MGMenuItem lobbyItem = getLobbyItem(lobby.getLobbyId());

		if (lobbyItem != null)
		{
			menu.removeItem(lobbyItem);
		}
	}

	public MGMenuBlock getBlock()
	{
		return block;
	}

	private int getNextSlot()
	{
		return 9 + 2 * (lobbyCount / 7) + 1 + lobbyCount++;
	}

	public void removeBrowser(Player p)
	{
		block.removeBlock(p);
	}
}
