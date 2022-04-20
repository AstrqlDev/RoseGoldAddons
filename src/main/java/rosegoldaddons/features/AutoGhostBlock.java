package rosegoldaddons.features;

import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;

public class AutoGhostBlock {
    private final KeyBinding sneakBind = Minecraft.getMinecraft().gameSettings.keyBindSneak;
    private final KeyBinding jumpBind = Minecraft.getMinecraft().gameSettings.keyBindJump;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(Main.mc.thePlayer == null || Main.mc.theWorld == null) return;
        if(Main.configFile.ghostIndex == 0) {
            if (!Main.configFile.AutoGB || !Main.configFile.AutoGBTopStair) return;
            if (sneakBind.isKeyDown() && Main.configFile.AutoGB) {
                BlockPos playerPos = Main.mc.thePlayer.getPosition();
                Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
                Vec3i vec3i = new Vec3i(3, 1, 3);
                Vec3i vec3i2 = new Vec3i(3, 2, 3);
                for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                    double diffX = Math.abs(blockPos.getX() + 0.5D - playerVec.xCoord);
                    double diffZ = Math.abs(blockPos.getZ() + 0.5D - playerVec.zCoord);
                    double diffY = blockPos.getY() - playerVec.yCoord;
                    if (diffX < 1 && diffZ < 1) {
                        IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                        if (isStair(blockState) && diffY == -0.5) {
                            Main.mc.theWorld.setBlockToAir(blockPos);
                        }
                        if (Main.configFile.AutoGBMisc) {
                            if (blockState.getBlock() == Blocks.skull && diffY == 0 && diffX < 0.5 && diffZ < 0.5) {
                                Main.mc.theWorld.setBlockToAir(blockPos);
                            } else if (blockState.getBlock() == Blocks.hopper && diffY == -0.625) {
                                Main.mc.theWorld.setBlockToAir(blockPos);
                            } else if (isFence(blockState) && diffY <= 0 && diffX < 0.5 && diffZ < 0.5) {
                                Main.mc.theWorld.setBlockToAir(blockPos);
                            }
                        }
                    }
                }
            }
        }
        if(jumpBind.isKeyDown() && Main.configFile.AutoGBTopStair) {
            BlockPos playerPos = Main.mc.thePlayer.getPosition();
            Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
            Vec3i vec3i = new Vec3i(3, 2, 3);
            Vec3i vec3i2 = new Vec3i(3, 0, 3);
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                double diffX = Math.abs(blockPos.getX() + 0.5D - playerVec.xCoord);
                double diffZ = Math.abs(blockPos.getZ() + 0.5D - playerVec.zCoord);
                double diffY = blockPos.getY() - playerVec.yCoord;
                if(diffX < 1 && diffZ < 1) {
                    IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                    if(isStair(blockState) && diffY > 1.2 && diffY < 1.3) {
                        Main.mc.theWorld.setBlockToAir(blockPos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if(Main.mc.thePlayer == null || Main.mc.theWorld == null) return;
        if(Main.configFile.ghostIndex == 0) return;
        if(!Main.configFile.AutoGB || !Main.configFile.AutoGBTopStair) return;
        if(sneakBind.isPressed() && Main.configFile.AutoGB) {
            BlockPos playerPos = Main.mc.thePlayer.getPosition();
            Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
            Vec3i vec3i = new Vec3i(3, 1, 3);
            Vec3i vec3i2 = new Vec3i(3, 2, 3);
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                double diffX = Math.abs(blockPos.getX() + 0.5D - playerVec.xCoord);
                double diffZ = Math.abs(blockPos.getZ() + 0.5D - playerVec.zCoord);
                double diffY = blockPos.getY() - playerVec.yCoord;
                if(diffX < 1 && diffZ < 1) {
                    IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                    if(isStair(blockState) && diffY == -0.5) {
                        Main.mc.theWorld.setBlockToAir(blockPos);
                    }
                    if(Main.configFile.AutoGBMisc) {
                        if(blockState.getBlock() == Blocks.skull && diffY == 0 && diffX < 0.5 && diffZ < 0.5) {
                            Main.mc.theWorld.setBlockToAir(blockPos);
                        }
                        else if(blockState.getBlock() == Blocks.hopper && diffY == -0.625) {
                            Main.mc.theWorld.setBlockToAir(blockPos);
                        }
                        else if(isFence(blockState) && diffY <= 0 && diffX < 0.5 && diffZ < 0.5) {
                            Main.mc.theWorld.setBlockToAir(blockPos);
                        }
                    }
                }
            }
        }
        if(jumpBind.isPressed() && Main.configFile.AutoGBTopStair) {
            BlockPos playerPos = Main.mc.thePlayer.getPosition();
            Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
            Vec3i vec3i = new Vec3i(3, 2, 3);
            Vec3i vec3i2 = new Vec3i(3, 0, 3);
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                double diffX = Math.abs(blockPos.getX() + 0.5D - playerVec.xCoord);
                double diffZ = Math.abs(blockPos.getZ() + 0.5D - playerVec.zCoord);
                double diffY = blockPos.getY() - playerVec.yCoord;
                if(diffX < 1 && diffZ < 1) {
                    IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                    if(isStair(blockState) && diffY > 1.2 && diffY < 1.3) {
                        Main.mc.theWorld.setBlockToAir(blockPos);
                    }
                }
            }
        }
    }

    private boolean isStair(IBlockState blockState) {
        if(blockState.getBlock() == Blocks.acacia_stairs || blockState.getBlock() == Blocks.birch_stairs ||
                blockState.getBlock() == Blocks.brick_stairs || blockState.getBlock() == Blocks.stone_brick_stairs ||
                blockState.getBlock() == Blocks.stone_stairs || blockState.getBlock() == Blocks.dark_oak_stairs ||
                blockState.getBlock() == Blocks.jungle_stairs || blockState.getBlock() == Blocks.spruce_stairs ||
                blockState.getBlock() == Blocks.red_sandstone_stairs || blockState.getBlock() == Blocks.sandstone_stairs ||
                blockState.getBlock() == Blocks.nether_brick_stairs || blockState.getBlock() == Blocks.oak_stairs ||
                blockState.getBlock() == Blocks.quartz_stairs)
            return true;
        return false;
    }

    private boolean isFence(IBlockState blockState) {
        if(blockState.getBlock() == Blocks.acacia_fence || blockState.getBlock() == Blocks.birch_fence ||
                blockState.getBlock() == Blocks.cobblestone_wall || blockState.getBlock() == Blocks.dark_oak_fence ||
                blockState.getBlock() == Blocks.jungle_fence || blockState.getBlock() == Blocks.spruce_fence ||
                blockState.getBlock() == Blocks.oak_fence || blockState.getBlock() == Blocks.nether_brick_fence)
            return true;
        return false;
    }
}
