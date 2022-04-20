package rosegoldaddons.features;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.PlayerUtils;
import rosegoldaddons.utils.RenderUtils;
import rosegoldaddons.utils.RotationUtils;
import rosegoldaddons.utils.ShadyRotation;

import java.awt.*;
import java.util.ArrayList;

public class MithrilNuker {
    private static int currentDamage;
    private static byte blockHitDelay = 0;
    private static BlockPos blockPos;
    private BlockPos lastBlockPos = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(Main.mc.thePlayer == null || Main.mc.theWorld == null) return;
        if (!Main.mithrilNuker) {
            currentDamage = 0;
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            if(PlayerUtils.pickaxeAbilityReady && Main.mc.playerController != null) {
                if(Main.mc.thePlayer.inventory.getStackInSlot(Main.mc.thePlayer.inventory.currentItem) != null) {
                    Main.mc.playerController.sendUseItem(Main.mc.thePlayer, Main.mc.theWorld, Main.mc.thePlayer.inventory.getStackInSlot(Main.mc.thePlayer.inventory.currentItem));
                    PlayerUtils.pickaxeAbilityReady = false;
                }
            }
            if(currentDamage > 100) {
                currentDamage = 0;
            }
            if(blockPos != null && Main.mc.theWorld != null) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.bedrock || blockState.getBlock() == Blocks.air) {
                    currentDamage = 0;
                }
            }
            if(currentDamage == 0) {
                lastBlockPos = blockPos;
                blockPos = closestMithril();
            }
            if (blockPos != null) {
                if (blockHitDelay > 0) {
                    blockHitDelay--;
                    return;
                }
                if (currentDamage == 0) {
                    Main.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
                    if(Main.configFile.mithrilLook) {
                        ShadyRotation.smoothLook(ShadyRotation.getRotationToBlock(blockPos), Main.configFile.smoothLookVelocity, () -> {});
                    }
                }
                PlayerUtils.swingItem();

                currentDamage += 1; //finally used after all
            }
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.mithrilNuker || Main.mc.theWorld == null) return;
        if (blockPos != null) {
            IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
            if(blockState.getBlock() == Blocks.stone) {
                RenderUtils.drawBlockBox(blockPos, Color.WHITE, Main.configFile.lineWidth, event.partialTicks);
            } else {
                RenderUtils.drawBlockBox(blockPos, Color.BLUE, Main.configFile.lineWidth, event.partialTicks);
            }
        }
    }

    private BlockPos closestMithril() {
        int r = 6;
        if (Main.mc.thePlayer == null || Main.mc.theWorld == null) return null;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos = playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> chests = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                if (isMithril(blockState)) {
                    chests.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
                if(Main.configFile.includeOres) {
                    if (blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack ) {
                        chests.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                    }
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (int i = 0; i < chests.size(); i++) {
            double dist = chests.get(i).distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = chests.get(i);
            }
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private boolean isMithril(IBlockState blockState) {
        if(blockState.getBlock() == Blocks.prismarine) {
            return true;
        } else if(blockState.getBlock() == Blocks.wool) {
            return true;
        } else if(blockState.getBlock() == Blocks.stained_hardened_clay) {
            return true;
        } else if(!Main.configFile.ignoreTitanium && blockState.getBlock() == Blocks.stone && blockState.getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE_SMOOTH) {
            return true;
        } else if(blockState.getBlock() == Blocks.gold_block) {
            return true;
        }
        return false;
    }
}
