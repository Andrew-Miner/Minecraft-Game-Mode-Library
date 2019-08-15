package SmokyMiner.MiniGames.Items.ItemShop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGMenuBlock;
import SmokyMiner.MiniGames.InventoryMenu.PagedMenu.MGPagedBorderedMenu;

public class MGItemShop extends MGPagedBorderedMenu
{
	public final static int MENU_SIZE = 27;
	public final static String MENU_NAME = ChatColor.UNDERLINE + "Item Shop";
	public final static String BLOCK_NAME = ChatColor.GREEN + ChatColor.stripColor(MENU_NAME);

	private MGMenuBlock block;

	public MGItemShop(JavaPlugin plugin)
	{
		super(plugin, MENU_NAME, MENU_SIZE, Material.BLACK_STAINED_GLASS_PANE);
		
		block = new MGMenuBlock(plugin, this, Material.BIRCH_SIGN, -1, BLOCK_NAME, null);
	}
	
	public MGItemShop(JavaPlugin plugin, Material icon)
	{
		super(plugin, MENU_NAME, MENU_SIZE, Material.BLACK_STAINED_GLASS_PANE);
		
		block = new MGMenuBlock(plugin, this, icon, -1, BLOCK_NAME, null);
	}

	public void giveBlock(Player p)
	{
		block.giveBlock(p);
	}
	
	public MGMenuBlock getBlock()
	{
		return block;
	}
}
