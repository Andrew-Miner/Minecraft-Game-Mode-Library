package SmokyMiner.MiniGames.InventoryMenu.PagedMenu;

import org.bukkit.entity.Player;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;

public class MGNextPageListener implements MGItemListener
{	
	@Override
	public void onItemClick(MGItemClickEvent e) 
	{
		if(e.getMenu() instanceof MGPagedMenu && e.getWhoClicked() instanceof Player)
		{
			MGPagedMenu menu = (MGPagedMenu) e.getMenu();
			
			int nextPage = menu.getCurrentPage(e.getWhoClicked().getUniqueId()) + 1;
			
			if(nextPage < menu.pageCount())
				menu.openMenu((Player) e.getWhoClicked(), nextPage);
		}
		else
			throw new IllegalStateException("NextPageListener not in PagedMenu");
	}
}
