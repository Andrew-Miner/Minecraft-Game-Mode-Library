package com.gmail.andrew.miner3.plugins.InventoryMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class MenuItem 
{
	private JavaPlugin plugin;
	private ArrayList<ItemListener> listeners;
	
	private Sound clickSound;
	private ItemStack stack;
	
	private MenuItemAnimator animator;
	private BukkitTask animatorTask;
	
	public MenuItem(JavaPlugin plugin, Material icon, String name)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, name, null);
	}

	public MenuItem(JavaPlugin plugin, Material icon, String name, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, name, null);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, null);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, String name, List<String> lore, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, -1, name, lore);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, Sound clickSound, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, null);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, lore);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, Sound clickSound, ItemListener listener)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		registerListener(listener);
		
		animator = null;
		animatorTask = null;
		
		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, lore);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, null);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, Sound clickSound)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		
		animator = null;
		animatorTask = null;
		
		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, null);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		
		animator = null;
		animatorTask = null;
		
		clickSound = Sound.UI_BUTTON_CLICK;
		setItemStack(icon, iconData, name, lore);
	}
	
	public MenuItem(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, Sound clickSound)
	{
		this.plugin = plugin;
		
		listeners = new ArrayList<ItemListener>();
		
		animator = null;
		animatorTask = null;
		
		this.clickSound = clickSound;
		setItemStack(icon, iconData, name, lore);
	}
	
	public void close()
	{
		if(animatorTask != null && !animatorTask.isCancelled())
			animatorTask.cancel();
	}

	public ItemStack getItemStack()
	{
		return stack;
	}
	
	public void setItemStack(Material material, String name, List<String> lore)
	{
		setItemStack(material, -1, name, lore);
	}
	
	public void setItemStack(Material material, int i, String name, List<String> lore)
	{
		if(i == -1)
			stack = new ItemStack(material);
		else
			stack = new ItemStack(material, 1, (byte)i);
		
		ItemMeta meta = stack.getItemMeta();
		
		if(name != null)
			meta.setDisplayName(name);
		
		if(lore != null)
		{
			meta.setLore(lore);
		}
		
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
	
	public void setAnimation(MenuItemAnimator animation, long taskDelay) 
	{
		close();
		
		if(animation == null)
		{
			plugin.getLogger().warning("MenuItemAnimator Is Null!");
			return;
		}
			
		this.animator = animation;
		MenuItem temp = this;
		animatorTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable()
		{
			MenuItem item = temp;
			
			@Override
			public void run() 
			{
				if(animator != null)
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
				}
				else
					animatorTask.cancel();
			}
			
		}, 0L, taskDelay);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<ItemListener> getListeners()
	{
		return (ArrayList<ItemListener>) listeners.clone();
	}
	
	public void registerListener(ItemListener listener)
	{
		listeners.add(listener);
	}
	
	public void unregisterListener(ItemListener listener)
	{
		listeners.remove(listener);
	}
	
	private ItemClickEvent fireClickEvent(InventoryMenu inventoryMenu, ClickType click, HumanEntity player) 
	{
		ItemClickEvent event = new ItemClickEvent(inventoryMenu, this, click, player, clickSound);
		
		for(ItemListener listener : listeners)
			listener.onItemClick(event);
		
		if(animator != null)
			animator.itemClicked(event);
		
		return event;
	}

	public void clicked(InventoryMenu inventoryMenu, ClickType click, HumanEntity whoClicked) 
	{
		ItemClickEvent event = fireClickEvent(inventoryMenu, click, whoClicked);
		
		if(event.getClickSound() != null && whoClicked instanceof Player)
			((Player) whoClicked).playSound(whoClicked.getLocation(), event.getClickSound(), .5f, 1f);
	}
}
