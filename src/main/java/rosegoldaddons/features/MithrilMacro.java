package rosegoldaddons.features;

import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class MithrilMacro {
    private Vec3 vec = null;
    private int currentDamage;
    private BlockPos blockPos = null;
    private Vec3 lastVec = null;
    private BlockPos lastBlockPos = null;
    private final KeyBinding lc = Main.mc.gameSettings.keyBindAttack;
    private boolean holdingLeft = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) return;
        if(Main.mc.thePlayer == null || Main.mc.theWorld == null) return;
        if (Main.mc.currentScreen != null) return;
        if (!Main.mithrilMacro) {
            if (holdingLeft) {
                KeyBinding.setKeyBindState(lc.getKeyCode(), false);
                holdingLeft = false;
            }
            currentDamage = 0;
            return;
        }
        if (PlayerUtils.pickaxeAbilityReady) {
            KeyBinding.setKeyBindState(lc.getKeyCode(), false);
            if(Main.mc.thePlayer.inventory.getStackInSlot(Main.mc.thePlayer.inventory.currentItem) != null) {
                Main.mc.playerController.sendUseItem(Main.mc.thePlayer, Main.mc.theWorld, Main.mc.thePlayer.inventory.getStackInSlot(Main.mc.thePlayer.inventory.currentItem));
                PlayerUtils.pickaxeAbilityReady = false;
            }
        }
        if (currentDamage > 100) {
            KeyBinding.setKeyBindState(lc.getKeyCode(), false);
            currentDamage = 0;
        }
        lastBlockPos = blockPos;
        blockPos = closestMithril();
        if (lastBlockPos != null && blockPos != null && !lastBlockPos.equals(blockPos)) {
            currentDamage = 0;
        }
        if (blockPos != null) {
            ArrayList<Vec3> vec3s = BlockUtils.whereToMineBlock(blockPos);
            if (vec3s.size() > 0) {
                vec = vec3s.get(0);
                if (vec != null) {
                    ShadyRotation.smoothLook(ShadyRotation.vec3ToRotation(vec), Main.configFile.smoothLookVelocity, () -> {});
                    lastVec = vec;
                    KeyBinding.setKeyBindState(lc.getKeyCode(), true);
                    holdingLeft = true;
                }

                currentDamage += 1;
            }
        }

    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!Main.mithrilMacro) return;
        if (vec != null) {
            RenderUtils.drawPixelBox(vec, Color.RED, 0.01, event.partialTicks);
        }
        if (blockPos != null) {
            RenderUtils.drawBlockBox(blockPos, Color.CYAN, Main.configFile.lineWidth, event.partialTicks);
        }
    }

    private BlockPos closestMithril() {
        int r = 6;
        if (Main.mc.thePlayer == null || Main.mc.theWorld == null) return null;
        BlockPos playerPos = Main.mc.thePlayer.getPosition().add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> blocks = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                if (isMithril(blockState)) {
                    if (BlockUtils.whereToMineBlock(blockPos).size() > 0) {
                        blocks.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                    }
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (Vec3 block : blocks) {
            double dist = block.distanceTo(playerVec);
            if (lastBlockPos != null) {
                dist = block.distanceTo(new Vec3(lastBlockPos.getX() + 0.5, lastBlockPos.getY() + 0.5, lastBlockPos.getZ() + 0.5));
            }
            if (dist < smallest) {
                smallest = dist;
                closest = block;
            }
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private boolean isMithril(IBlockState blockState) {
        if (!Main.configFile.onlyOres) {
            if (blockState.getBlock() == Blocks.prismarine) {
                return true;
            } else if (blockState.getBlock() == Blocks.wool && (blockState.getValue(BlockStainedGlass.COLOR) == EnumDyeColor.LIGHT_BLUE || blockState.getValue(BlockStainedGlass.COLOR) == EnumDyeColor.GRAY)) {
                return true;
            } else if (blockState.getBlock() == Blocks.stained_hardened_clay && blockState.getValue(BlockStainedGlass.COLOR) == EnumDyeColor.CYAN) {
                return true;
            } else if (!Main.configFile.ignoreTitanium && blockState.getBlock() == Blocks.stone && blockState.getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE_SMOOTH) {
                return true;
            } else if (blockState.getBlock() == Blocks.gold_block) {
                return true;
            }
        }

        if (Main.configFile.includeOres || Main.configFile.onlyOres) {
            return blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack;
        }

        return false;
    }
}
