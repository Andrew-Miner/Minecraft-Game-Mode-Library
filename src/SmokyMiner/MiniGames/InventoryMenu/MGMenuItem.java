package SmokyMiner.MiniGames.InventoryMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MGMenuItem implements Listener
{
	protected JavaPlugin plugin;
	private ArrayList<MGItemListener> listeners;

	protected Sound clickSound;
	protected ItemStack stack;

	private MGMenuItemAnimator animator;
	private BukkitTask animatorTask;

	public MGMenuItem(JavaPlugin plugin, Material icon, String name)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, String name, MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, String name, List<String> lore, MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, -1, name, lore);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, Sound clickSound,
			MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore,
			MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, lore);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, Sound clickSound, MGItemListener listener)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();
		registerListener(listener);

		animator = null;
		animatorTask = null;

		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, lore);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, Sound clickSound)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();

		animator = null;
		animatorTask = null;

		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, null);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();

		animator = null;
		animatorTask = null;

		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, lore);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public MGMenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, Sound clickSound)
	{
		this.plugin = plugin;

		listeners = new ArrayList<MGItemListener>();

		animator = null;
		animatorTask = null;

		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, lore);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public void close()
	{
		if (animatorTask != null && !animatorTask.isCancelled())
			animatorTask.cancel();
	}

	public ItemStack getItemStack()
	{
		return stack.clone();
	}

	public void giveBlock(Player p)
	{
		p.getInventory().addItem(stack);
	}

	public void removeBlock(Player p)
	{
		p.getInventory().remove(stack);
	}
	
	public void setItemStack(ItemStack stack)
	{
		this.stack = stack.clone();
	}

	public void setItemStack(Material material, String name, List<String> lore)
	{
		setItemStack(material, -1, name, lore);
	}

	public void setItemStack(Material material, int i, String name, List<String> lore)
	{
		if (i == -1)
			stack = new ItemStack(material);
		else
			stack = new ItemStack(material, 1, (byte) i);

		ItemMeta meta = stack.getItemMeta();

		if (name != null)
			meta.setDisplayName(name);

		if (lore != null)
			meta.setLore(lore);

		stack.setItemMeta(meta);
	}

	public ItemMeta getMeta()
	{
		return stack.getItemMeta();
	}

	public void setMeta(ItemMeta meta)
	{
		stack.setItemMeta(meta);
	}

	public void setMeta(String name, List<String> lore)
	{
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		stack.setItemMeta(meta);
	}

	public String getName()
	{
		return stack.getItemMeta().getDisplayName();
	}

	public void setName(String name)
	{
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(name);
		stack.setItemMeta(meta);
	}

	public List<String> getLore()
	{
		return stack.getItemMeta().getLore();
	}

	public void setLore(List<String> lore)
	{
		ItemMeta meta = stack.getItemMeta();
		meta.setLore(lore);
		stack.setItemMeta(meta);
	}

	public Material getMaterial()
	{
		return stack.getType();
	}

	public void setMaterial(Material material)
	{
		stack.setType(material);
	}

	public int getAmount()
	{
		return stack.getAmount();
	}

	public void setAmount(int amount)
	{
		stack.setAmount(amount);
	}

	public void setAnimation(MGMenuItemAnimator animation, long taskDelay)
	{
		close();

		if (animation == null)
		{
			plugin.getLogger().warning("MenuItemAnimator Is Null!");
			return;
		}

		this.animator = animation;
		MGMenuItem temp = this;
		animatorTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
		{
			MGMenuItem item = temp;

			@Override
			public void run()
			{
				if (animator != null)
				{
					Material type = animator.updateMaterial(stack.getType());

					ItemMeta meta = stack.getItemMeta();
					List<String> lore = animator.updateLore(meta.getLore());
					String name = animator.updateName(meta.getDisplayName());

					Bukkit.getScheduler().runTask(plugin, new Runnable()
					{
						@Override
						public void run()
						{
							stack.setType(type);
							meta.setLore(lore);
							meta.setDisplayName(name);
							stack.setItemMeta(meta);
						}

					});

					animator.updateMenuSlot(item);
				} else
					animatorTask.cancel();
			}

		}, 0L, taskDelay);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<MGItemListener> getListeners()
	{
		return (ArrayList<MGItemListener>) listeners.clone();
	}

	public void registerListener(MGItemListener listener)
	{
		listeners.add(listener);
	}

	public void unregisterListener(MGItemListener listener)
	{
		listeners.remove(listener);
	}

	private MGItemClickEvent fireClickEvent(MGInventoryMenu inventoryMenu, ClickType click, HumanEntity player)
	{
		MGItemClickEvent event = new MGItemClickEvent(inventoryMenu, this, click, player, clickSound);

		for (MGItemListener listener : listeners)
			listener.onItemClick(event);

		if (animator != null)
			animator.itemClicked(event);

		return event;
	}
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent e)
	{
		if(!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || 
		   e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			return;
		
		ItemStack temp = stack.clone();
		ItemStack inHand = e.getPlayer().getInventory().getItemInMainHand();
		temp.setAmount(inHand.getAmount());
		
		if(temp.equals(inHand))
		{
			e.setCancelled(true);
			for (MGItemListener listener : listeners)
				listener.onItemInteraction(e);
		}
	}

	public void clicked(MGInventoryMenu inventoryMenu, ClickType click, HumanEntity whoClicked)
	{
		MGItemClickEvent event = fireClickEvent(inventoryMenu, click, whoClicked);

		if (event.getClickSound() != null && whoClicked instanceof Player)
			((Player) whoClicked).playSound(whoClicked.getLocation(), event.getClickSound(), .5f, 1f);
	}
}
