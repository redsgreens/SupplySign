package redsgreens.SupplySign;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;

/**
 * Handle events for all Player related events
 * @author redsgreens
 */
public class SupplySignPlayerListener extends PlayerListener {

	private final SupplySign Plugin;
	
    public SupplySignPlayerListener(SupplySign plugin) 
    {
    	Plugin = plugin;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    // catch player right-click events
    {
    	// return if the event is already cancelled, or if it's not a right-click event
		if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST && block.getType() != Material.CHEST && block.getType() != Material.DISPENSER)
			return;
		
		Sign sign;
		if(block.getType() == Material.CHEST){
			sign = SupplySignUtil.getAttachedSign(block);
			if(sign == null) return;
		}
		else if(block.getType() == Material.DISPENSER){
			sign = SupplySignUtil.getAttachedSign(block);
			if(sign == null) return;
			else
			{ // prevent opening inventory of a dispenser with a supplysign attached
				event.setCancelled(true);
				if(Plugin.Config.ShowErrorsInClient)
					event.getPlayer().sendMessage("§cErr: SupplySign attached to dispenser, inventory unavailable.");
				return;
			}
		}
		else 
			sign = new CraftSign(block);
		
		try
		{
			if (sign.getLine(0).equals("§1[Supply]")){
				event.setCancelled(true);

				// if it's a dispenser cancel right click on sign
				if(sign.getBlock().getType() == Material.WALL_SIGN)
				{
					if(SupplySignUtil.getBlockBehindWallSign(sign).getType() == Material.DISPENSER)
					{
						if(Plugin.Config.ShowErrorsInClient)
							event.getPlayer().sendMessage("§cErr: SupplySign attached to dispenser, inventory unavailable.");
						return;
					}
				}

				ArrayList<Object> itemList = new ArrayList<Object>();
				
				// if it's a kit, test for generic access permission or access to this specific kit
				if(sign.getLine(1).trim().contains("kit:")){
					String[] split = sign.getLine(1).trim().split(":");
					
					if(Plugin.isAuthorized(event.getPlayer(), "access") || Plugin.isAuthorized(event.getPlayer(), "access." + split[1]))
						itemList = Plugin.Kits.getKit(split[1]);
					else if(Plugin.Config.ShowErrorsInClient)
						event.getPlayer().sendMessage("§cErr: you don't have permission to access this SupplySign.");
				}
				else
				{
					if(Plugin.isAuthorized(event.getPlayer(), "access"))
					{
						// it's not a kit, so load the items from the lines on the sign
						if(!sign.getLine(1).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(1).trim());
						if(!sign.getLine(2).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(2).trim());
						if(!sign.getLine(3).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(3).trim());

					}
					else if(Plugin.Config.ShowErrorsInClient)
						event.getPlayer().sendMessage("§cErr: you don't have permission to access this SupplySign.");
				}
				
				if(itemList.size() > 0)
					Plugin.Items.showInventory(event.getPlayer(), itemList);
				
				return;
			}
		}
		catch (Throwable ex)
		{
			if(Plugin.Config.ShowErrorsInClient)
				event.getPlayer().sendMessage("§cErr: " + ex.getMessage());
		}
    }
}

