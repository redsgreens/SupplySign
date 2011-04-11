package redsgreens.SupplySign;

import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;

import org.bukkit.block.*;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

/**
 * SupplySign block listener
 * @author redsgreens
 */
public class SupplySignBlockListener extends BlockListener {

    public SupplySignBlockListener(final SupplySign plugin) {}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) 
	// this prevents a block from being placed against a SupplySign 
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;

		if(event.getBlockAgainst().getType() == Material.WALL_SIGN || event.getBlockAgainst().getType() == Material.SIGN_POST)
		{
			Sign sign = new CraftSign(event.getBlockAgainst());
			if (sign.getLine(0).equals("§1[Supply]")){
				event.setCancelled(true);
				return;
			}
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event)
	// only allow players with permission to break a SupplySign
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;
		
		if(event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST)
		{
			Sign sign = new CraftSign(event.getBlock());
			if (sign.getLine(0).equals("§1[Supply]") && !SupplySign.isAuthorized(event.getPlayer(), "destroy")){
				event.setCancelled(true);
				return;
			}
		}

	}

	
	@Override
	public void onSignChange(SignChangeEvent event)
	// looks for a new SupplySign and tests it for validity
	{
		// return if the event is already cancelled
		if (event.isCancelled()) return;

		Block signBlock = event.getBlock();

		if(!SupplySign.getConfigFixSignOnSignGlitch().equalsIgnoreCase("Disabled")){
			// delete this sign if it's against another sign
			Block blockAgainst = null;

			if(signBlock.getType() == Material.SIGN_POST){
				if(signBlock.getFace(BlockFace.DOWN).getType() == Material.SIGN_POST || signBlock.getFace(BlockFace.DOWN).getType() == Material.WALL_SIGN)
					blockAgainst = signBlock.getFace(BlockFace.DOWN);
			}
			else if(signBlock.getType() == Material.WALL_SIGN){
				
				switch(signBlock.getData()){ // determine sign direction and get block behind it
				case 2: // facing east
					blockAgainst = signBlock.getFace(BlockFace.WEST);
					break;
				case 3: // facing west
					blockAgainst = signBlock.getFace(BlockFace.EAST);
					break;
				case 4: // facing north
					blockAgainst = signBlock.getFace(BlockFace.SOUTH);
					break;
				case 5: // facing south
					blockAgainst = signBlock.getFace(BlockFace.NORTH);
					break;
				default:
					return;			
				}
			}

			if(blockAgainst != null){
				if(blockAgainst.getType() == Material.SIGN_POST || blockAgainst.getType() == Material.WALL_SIGN){
					// the new sign is against another sign
					Sign signAgainst = new CraftSign(blockAgainst);
					
					// check the config file to make sure the sign should be deleted
					if((SupplySign.getConfigFixSignOnSignGlitch().equalsIgnoreCase("SupplySignOnly") && signAgainst.getLine(0).equals("§1[Supply]")) || SupplySign.getConfigFixSignOnSignGlitch().equalsIgnoreCase("Global")){
						signBlock.setType(Material.AIR);
						ItemStack signStack = new ItemStack(Material.SIGN, 1);
						event.getPlayer().setItemInHand(signStack);
						return;
					}
				}
			}
		}
		
		// done checking sign-on-sign bs, now on with setting up new signs
		try
		{
			// only proceed if it's a new sign
			if (event.getLine(0).equalsIgnoreCase("[Supply]"))
			{
				// and they have create permission
				if (SupplySign.isAuthorized(event.getPlayer(), "create")){
					
					// they are allowed, so set the first line to blue
					event.setLine(0, "§1[Supply]");

					// if there is a single-wide chest nearby, then create a wallsign against it
					if(SupplySign.isValidChest(signBlock.getFace(BlockFace.NORTH)) ||
							SupplySign.isValidChest(signBlock.getFace(BlockFace.EAST)) ||
							SupplySign.isValidChest(signBlock.getFace(BlockFace.SOUTH)) ||
							SupplySign.isValidChest(signBlock.getFace(BlockFace.WEST))){

						String[] lines = event.getLines();

						signBlock.setType(Material.WALL_SIGN);
						Sign sign = new CraftSign(signBlock);
						
						if(SupplySign.isValidChest(signBlock.getFace(BlockFace.NORTH)))
							signBlock.setData((byte)5);
						else if(SupplySign.isValidChest(signBlock.getFace(BlockFace.EAST)))
							signBlock.setData((byte)3);
						else if(SupplySign.isValidChest(signBlock.getFace(BlockFace.SOUTH)))
							signBlock.setData((byte)4);
						else if(SupplySign.isValidChest(signBlock.getFace(BlockFace.WEST)))
							signBlock.setData((byte)2);

						for(int i=0; i<lines.length; i++)
							sign.setLine(i, lines[i]);
					}

				}
				else{
					// not allowed
					event.setLine(0, "§c[Error]");
					event.setLine(1, "§cNot Allowed");
				}
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