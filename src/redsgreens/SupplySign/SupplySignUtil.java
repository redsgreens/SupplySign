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

		Block[] adjBlocks;

		if(isSingleChest(b) || (b.getType() == Material.DISPENSER))
			// it's a single chest or dispenser, so check the four adjacent blocks
			adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};
			
		else if (isDoubleChest(b)){
			// it's a double, so find the other half and check faces of both blocks
			Block b2 = findOtherHalfofChest(b);
			adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST), b2.getFace(BlockFace.NORTH), b2.getFace(BlockFace.EAST), b2.getFace(BlockFace.SOUTH), b2.getFace(BlockFace.WEST)};
		}
		else
			return null;

		for(int i=0; i<adjBlocks.length; i++)
			if(isSupplySign(adjBlocks[i]))
				return new CraftSign(adjBlocks[i]);
		
		return null;
	}

	public static Block findOtherHalfofChest(Block b)
	{
		// didn't find one, so find the other half of the chest and check it's faces
		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};
		for(int i=0; i<adjBlocks.length; i++)
			if(adjBlocks[i].getType() == Material.CHEST)
				return adjBlocks[i]; 
		
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

	public static String stripColorCodes(String str)
	{
		return str.replaceAll("\u00A7[0-9a-fA-F]", "");
	}

	public static Boolean isSupplySign(Sign sign)
	{
		if(sign.getLine(0).equals("§1[Supply]"))
			return true;
		else
			return false;
	}
	
	public static Boolean isSupplySign(Block b)
	{
		if(b.getType() != Material.SIGN && b.getType() != Material.WALL_SIGN)
			return false;
		else
			return isSupplySign(new CraftSign(b));

	}
}
