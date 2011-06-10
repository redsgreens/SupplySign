package redsgreens.SupplySign;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.block.CraftSign;

public class SupplySignUtil {
	
	// check to see if this is a chest without a supply sign already on it
	public static boolean isValidChest(Block b){
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++){
			if(adjBlocks[i].getType() == Material.WALL_SIGN){
				Sign sign = new CraftSign(adjBlocks[i]);
				if(sign.getLine(0).equals("§1[Supply]"))
					return false;
			}
		}
		
		return true;
	}

	// check to see if this is a dispenser without a supply sign already on it
	public static boolean isValidDispenser(Block b){
		if(b.getType() != Material.DISPENSER)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++){
			if(adjBlocks[i].getType() == Material.WALL_SIGN){
				Sign sign = new CraftSign(adjBlocks[i]);
				if(sign.getLine(0).equals("§1[Supply]"))
					return false;
			}
		}
		
		return true;
	}

	// check to see if this is a single wide chest
	public static boolean isSingleChest(Block b){
		
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++)
			if(adjBlocks[i].getType() == Material.CHEST)
				return false;
	
		return true;
	}

	// check to see if this is a single wide chest
	public static boolean isDoubleChest(Block b){
		
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++)
			if(adjBlocks[i].getType() == Material.CHEST)
				return true;
	
		return false;
	}

	// find a sign attached to a chest
	public static Sign getAttachedSign(Block b){
		if((b.getType() != Material.CHEST) && (b.getType() != Material.DISPENSER))
			return null;
		
		if(isSingleChest(b) || (b.getType() == Material.DISPENSER)){
			// it's a single chest or dispenser, so check the four adjacent blocks
			if(b.getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.NORTH));
			else if(b.getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.EAST));
			else if(b.getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.SOUTH));
			else if(b.getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.WEST));
			
			// didn't find a sign so return null
			return null;
		}
		else if (isDoubleChest(b)){
			// it's a double, so check the adjacent faces of this block first
			if(b.getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.NORTH));
			else if(b.getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.EAST));
			else if(b.getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.SOUTH));
			else if(b.getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.WEST));

			// didn't find one, so find the other half of the chest and check it's faces
			Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

			for(int i=0; i<adjBlocks.length; i++)
				if(adjBlocks[i].getType() == Material.CHEST){
					if(adjBlocks[i].getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.NORTH));
					else if(adjBlocks[i].getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.EAST));
					else if(adjBlocks[i].getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.SOUTH));
					else if(adjBlocks[i].getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.WEST));
				}

			// still no attached sign, so return null
			return null;
		}
		else
			return null;
	}

	// get the block that has a wall sign on it
	public static Block getBlockBehindWallSign(Sign sign)
	{
		Block blockAgainst = null;
		Block signBlock = sign.getBlock();
		
		if(sign.getType() == Material.WALL_SIGN)
		{
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
			}
		}
		
		return blockAgainst;
	}

}
