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

    public SupplySignPlayerListener(SupplySign instance) { }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    // catch player right-click events
    {
    	// return if the event is already cancelled, or if it's not a right-click event
		if(event.isCancelled() || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST && block.getType() != Material.CHEST)
			return;
		
		Sign sign;
		if(block.getType() == Material.CHEST){
			sign = SupplySign.getAttachedSign(block);
			if(sign == null) return;
		} 
		else 
			sign = new CraftSign(block);

		try
		{
			if (sign.getLine(0).equals("§1[Supply]")){
				event.setCancelled(true);

				ArrayList<String> itemList = new ArrayList<String>();
				
				// if it's a kit, test for generic access permission or access to this specific kit
				if(sign.getLine(1).trim().contains("kit:")){
					String[] split = sign.getLine(1).trim().split(":");
					
					if(SupplySign.isAuthorized(event.getPlayer(), "access") || SupplySign.isAuthorized(event.getPlayer(), "access." + split[1]))
						itemList = SupplySign.getKit(split[1]);
					else if(SupplySign.getConfigShowErrorsInClient())
					{
						event.getPlayer().sendMessage("Error: you don't have permission to access this SupplySign.");
						return;
					}
				}
				else
				{
					if(SupplySign.isAuthorized(event.getPlayer(), "access"))
					{
						// it's not a kit, so load the items from the lines on the sign
						if(!sign.getLine(1).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(1).trim());
						if(!sign.getLine(2).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(2).trim());
						if(!sign.getLine(3).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(3).trim());

					}
					else if(SupplySign.getConfigShowErrorsInClient())
					{
						event.getPlayer().sendMessage("Error: you don't have permission to access this SupplySign.");
						return;
					}
				}
				if(itemList.size() > 0)
					SupplySign.showInventory(event.getPlayer(), itemList);
				
				return;
			}
		}
		catch (Throwable ex)
		{
			if(SupplySign.getConfigShowErrorsInClient())
				event.getPlayer().sendMessage("§cError: " + ex.getMessage());
		}
    }
}

