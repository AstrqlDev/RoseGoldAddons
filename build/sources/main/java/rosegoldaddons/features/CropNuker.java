package rosegoldaddons.features;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.PlayerUtils;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class CropNuker {
    private static BlockPos crop = null;
    private static final ArrayList<BlockPos> broken = new ArrayList<>();
    private static int ticks = 0;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if(Main.configFile.farmSpeedIndex == 0 && event.phase == TickEvent.Phase.END) return;
        if (!Main.nukeCrops || Main.mc.thePlayer == null) {
            broken.clear();
            return;
        }
        if(broken.size() > 40) {
            broken.clear();
        }
        crop = closestCrop();
        if (crop != null) {
            Main.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, crop, EnumFacing.DOWN));
            PlayerUtils.swingItem();
            broken.add(crop);
        }

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!Main.nukeCrops) return;
        if(crop != null) {
            RenderUtils.drawBlockBox(crop, new Color(255, 0, 0), Main.configFile.lineWidth, event.partialTicks);
        }
    }

    private BlockPos closestCrop() {
        if(Main.mc.theWorld == null) return null;
        double r = 6;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos = playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        if (Main.configFile.farmShapeIndex == 1) {
            vec3i = new Vec3i(r, 2, r);
        }
        Vec3i vec3iCane = new Vec3i(r, 0, r);
        ArrayList<Vec3> warts = new ArrayList<>();
        if (playerPos != null) {
            switch (Main.configFile.farmNukeIndex) {
                case 0:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.nether_wart || blockState.getBlock() == Blocks.potatoes || blockState.getBlock() == Blocks.wheat || blockState.getBlock() == Blocks.carrots || blockState.getBlock() == Blocks.pumpkin || blockState.getBlock() == Blocks.melon_block || blockState.getBlock() == Blocks.brown_mushroom || blockState.getBlock() == Blocks.red_mushroom || blockState.getBlock() == Blocks.cocoa) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 1:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3iCane), playerPos.subtract(vec3iCane))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.reeds || blockState.getBlock() == Blocks.cactus) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.nether_wart) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.wheat) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 4:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.carrots) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 5:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.potatoes) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 6:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.pumpkin) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 7:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.melon_block) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
                case 8:
                    for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (blockState.getBlock() == Blocks.cocoa) {
                            if (Main.configFile.farmShapeIndex == 0) {
                                if (!broken.contains(blockPos)) {
                                    warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                            } else if (Main.configFile.farmShapeIndex == 1) {
                                EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                                int x = (int) Math.floor(Main.mc.thePlayer.posX);
                                int z = (int) Math.floor(Main.mc.thePlayer.posZ);
                                switch (dir) {
                                    case NORTH:
                                        if (blockPos.getZ() < z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case SOUTH:
                                        if (blockPos.getZ() > z && blockPos.getX() == x) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case WEST:
                                        if (blockPos.getX() < x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                    case EAST:
                                        if (blockPos.getX() > x && blockPos.getZ() == z) {
                                            if (!broken.contains(blockPos)) {
                                                warts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                            }
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    break;
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (Vec3 wart : warts) {
            double dist = wart.distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = wart;
            }
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }
}
