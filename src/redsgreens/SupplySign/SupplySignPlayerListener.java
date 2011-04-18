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
				if(SupplySign.isAuthorized(event.getPlayer(), "access"))
				{
					ArrayList<String> itemList;

					if(sign.getLine(1).trim().contains("kit:")){
						String[] split = sign.getLine(1).trim().split(":");
						itemList = SupplySign.getKit(split[1]);
						
					}else{
						itemList = new ArrayList<String>();
						if(!sign.getLine(1).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(1).trim());
						if(!sign.getLine(2).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(2).trim());
						if(!sign.getLine(3).trim().equalsIgnoreCase(""))
							itemList.add(sign.getLine(3).trim());
					}
					if(itemList.size() > 0)
						SupplySign.showInventory(event.getPlayer(), itemList);
					
					return;
				}
			}
		}
		catch (Throwable ex)
		{
			if(SupplySign.getConfigShowErrorsInClient())
				event.getPlayer().sendMessage("§cError: " + ex.getMessage());
		}
    }
}

