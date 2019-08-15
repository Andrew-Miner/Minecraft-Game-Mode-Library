package SmokyMiner.MiniGames.InventoryMenu.PagedMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;

public class MGPrevPageListener implements MGItemListener
{
	@Override
	public void onItemClick(MGItemClickEvent e) 
	{
		if(e.getMenu() instanceof MGPagedMenu && e.getWhoClicked() instanceof Player)
		{
			MGPagedMenu menu = (MGPagedMenu) e.getMenu();
			
			int prevPage = menu.getCurrentPage(e.getWhoClicked().getUniqueId()) - 1;
			
			if(!(prevPage < 0))
				menu.openMenu((Player) e.getWhoClicked(), prevPage);
		}
		else
			throw new IllegalStateException("PrevPageListener not in PagedMenu");
	}

	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}