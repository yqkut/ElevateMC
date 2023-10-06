package com.elevatemc.elib.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class BlockUtils {

    private static final Set<Material> INTERACTABLE;

    public static boolean isInteractable(Block block) {
        return isInteractable(block.getType());
    }

    public static boolean isInteractable(Material material) {
        return INTERACTABLE.contains(material);
    }

    public static boolean setBlockFast(World world,int x,int y,int z,int blockId,byte data) {
        net.minecraft.server.v1_8_R3.World w = ((CraftWorld)world).getHandle();

        final Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        BlockPosition position = new BlockPosition(x, y, z);
        return chunk.a(position, chunk.getBlockData(position)) != null;
        //return a(chunk, x & 15, y, z & 15, net.minecraft.server.v1_8_R3.Block.getById(blockId), data);
    }

    private static void queueChunkForUpdate(Player player,int cx,int cz) {
        ((CraftPlayer)player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    /*
    private static boolean a(Chunk chunk,int xPos,int yPos,int zPos,net.minecraft.server.v1_8_R3.Block block,int data) {
        int i1 = zPos << 4 | xPos;

        if (yPos >= chunk.b[i1] - 1) {
            chunk.b[i1] = -999;
        }

        int j1 = chunk.heightMap[i1];
        net.minecraft.server.v1_8_R3.Block block1 = chunk.getType(xPos, yPos, zPos);
        IBlockData blockData = chunk.getBlockData(new BlockPosition(xPos, yPos, zPos));
        if (block1 == block) {
            return false;
        } else {
            boolean flag = false;
            ChunkSection chunksection = chunk.getSections()[yPos >> 4];
            if (chunksection == null) {
                if (block == Blocks.AIR) {
                    return false;
                }

                chunksection = chunk.getSections()[yPos >> 4] = new ChunkSection(yPos >> 4 << 4, !chunk.world.worldProvider.g);
                flag = yPos >= j1;
            }

            int xLoc = chunk.locX * 16 + xPos;
            int yLoc = chunk.locZ * 16 + zPos;
            if (!chunk.world.isClientSide) {
                block1.remove(chunk.world, new BlockPosition(xLoc, yPos, yLoc), blockData);
            }

            if (!(block1 instanceof IContainer)) {
                chunksection.setType(xPos, yPos & 15, zPos, blockData);
            }

            if (!chunk.world.isClientSide) {
                block1.remove(chunk.world, new BlockPosition(xLoc, yPos, yLoc), blockData);
            } else if (block1 instanceof IContainer) {
                chunk.world.t(new BlockPosition(xLoc, yPos, yLoc));
            }

            if (block1 instanceof IContainer) {
                chunksection.setTypeId(xPos, yPos & 15, zPos, block);
            }

            if (chunksection.getTypeId(xPos, yPos & 15, zPos) != block) {
                return false;
            } else {
                chunksection.setData(xPos, yPos & 15, zPos, data);
                if (flag) {
                    chunk.initLighting();
                }

                TileEntity tileentity;
                if (block1 instanceof IContainer) {
                    tileentity = chunk.e(xPos, yPos, zPos);
                    if (tileentity != null) {
                        tileentity.u();
                    }
                }

                if (!chunk.world.isStatic && (!chunk.world.captureBlockStates || block instanceof BlockContainer)) {
                    block.onPlace(chunk.world, xLoc, yPos, yLoc);
                }

                if (block instanceof IContainer) {
                    if (chunk.getType(xPos, yPos, zPos) != block) {
                        return false;
                    }

                    tileentity = chunk.e(xPos, yPos, zPos);
                    if (tileentity == null) {
                        tileentity = ((IContainer)block).a(chunk.world, data);
                        chunk.world.setTileEntity(xLoc, yPos, yLoc, tileentity);
                    }

                    if (tileentity != null) {
                        tileentity.u();
                    }
                }

                chunk.n = true;
                return true;
            }
        }
    }

     */
    static {
        INTERACTABLE = ImmutableSet.of(Material.FENCE_GATE, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST, Material.HOPPER, new Material[]{Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON});
    }


}
