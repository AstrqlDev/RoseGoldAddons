package rosegoldaddons.features;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.RotationUtils;
import rosegoldaddons.utils.ShadyRotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

public class ForagingIslandMacro {
    private Thread thread;
    private final String[] responses = {"wtf??", "hello?", "hi?", "uhhhhhh", "what the", "??????"};

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Main.forageOnIsland) {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(() -> {
                    try {
                        BlockPos furthestDirt = furthestEmptyDirt();
                        //Main.mc.thePlayer.addChatMessage(new ChatComponentText("furthest dirt "+furthestDirt));
                        int sapling = findItemInHotbar("Jungle Sapling");
                        if(sapling == -1) {
                            sapling = findItemInHotbar("Oak Sapling");
                        }
                        int bonemeal = findItemInHotbar("Bone Meal");
                        int treecap = findItemInHotbar("Treecapitator");
                        int rod = findItemInHotbar("Rod");
                        if(sapling == -1) {
                            ChatUtils.sendMessage("§cNo jungle saplings in hotbar");
                        }
                        if(bonemeal == -1) {
                            ChatUtils.sendMessage("§cNo bonemeal in hotbar");
                        }
                        if(treecap == -1) {
                            ChatUtils.sendMessage("§cNo Treecapitator in hotbar");
                        }
                        if(rod == -1) {
                            ChatUtils.sendMessage("§cNo Fishing Rod in hotbar");
                        }
                        if(sapling == -1 || bonemeal == -1 || treecap == -1 || rod == -1) {
                            playAnnoyingAlert();
                            Main.forageOnIsland = false;
                            ChatUtils.sendMessage("§cForaging Macro Deactivated");
                            return;
                        }
                        if (furthestDirt != null) {
                            ShadyRotation.smoothLook(ShadyRotation.vec3ToRotation(new Vec3(furthestDirt.getX() +0.5, furthestDirt.getY() + 1, furthestDirt.getZ() + 0.5)), Main.configFile.smoothLookVelocity, () -> {});
                            Thread.sleep(Main.configFile.smoothLookVelocity * 40L);
                            if (sapling != -1) {
                                if (Main.mc.objectMouseOver.typeOfHit.toString().equals("BLOCK")) {
                                    BlockPos pos = Main.mc.objectMouseOver.getBlockPos();
                                    Block block = Main.mc.theWorld.getBlockState(pos).getBlock();
                                    if (block == Blocks.sapling) {
                                        click();
                                        Thread.sleep(20);
                                    }
                                }
                                Main.mc.thePlayer.inventory.currentItem = sapling;
                                //Main.mc.playerController.onPlayerRightClick(Main.mc.thePlayer, Main.mc.theWorld, Main.mc.thePlayer.inventory.getCurrentItem(), furthestDirt, EnumFacing.NORTH, Main.mc.objectMouseOver.hitVec);
                                rightClick();
                            }
                        } else {
                            BlockPos dirt = closestDirt();
                            if (dirt != null) {
                                ShadyRotation.smoothLook(ShadyRotation.vec3ToRotation(new Vec3(dirt.getX() + 0.5, dirt.getY() + 1, dirt.getZ() + 0.5)), Main.configFile.smoothLookVelocity, () -> {});
                                Thread.sleep(Main.configFile.smoothLookVelocity * 40L);
                                if (bonemeal != -1 && treecap != -1) {
                                    Main.mc.thePlayer.inventory.currentItem = bonemeal;
                                    Random rand = new Random();
                                    int toAdd = 0;
                                    if(Main.configFile.randomizeForaging) {
                                        toAdd = rand.nextInt(20);
                                    }
                                    //ChatUtils.sendMessage("extra delay: "+toAdd+"%");
                                    Thread.sleep(Math.round(150*(1+(toAdd/100F))));
                                    rightClick();
                                    rightClick();
                                    Main.mc.thePlayer.inventory.currentItem = treecap;
                                    Thread.sleep(Math.round(Main.configFile.treecapDelay*(1+(toAdd/100F))));
                                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), true);
                                    Thread.sleep(Math.round(150*(1+(toAdd/100F))));
                                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindAttack.getKeyCode(), false);
                                    Thread.sleep(Math.round(25*(1+(toAdd/100F))));
                                    Main.mc.thePlayer.inventory.currentItem = rod;
                                    Thread.sleep(Math.round(Main.configFile.prerodDelay*(1+(toAdd/100F))));
                                    rightClick();
                                    Thread.sleep(Math.round(Main.configFile.postrodDelay*(1+(toAdd/100F))));
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "Island Foraging Macro");
                thread.start();
            }
        }
    }

    @SubscribeEvent
    public void renderWorld2(RenderWorldLastEvent event) {
        if (!Main.forageOnIsland) return;
        BlockPos e = closestDirt();
        if(e == null) {
            Main.forageOnIsland = false;
            ChatUtils.sendMessage("§cNo dirt in range of player");
            ChatUtils.sendMessage("§cForaging Macro Deactivated");
            playAnnoyingAlert();
            if(Main.configFile.forageantisus) {
                int rand = new Random().nextInt(responses.length);
                int rand2 = new Random().nextInt(2000);
                new Thread(() -> {
                    try {
                        Thread.sleep(rand2);
                        Main.mc.thePlayer.sendChatMessage("/ac "+responses[rand]);
                        Thread.sleep(rand2*2);
                        Main.mc.getNetHandler().getNetworkManager().closeChannel(new ChatComponentText("Antisus activated lets hope you didnt get banned"));
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private BlockPos furthestEmptyDirt() {
        int r = 5;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> dirts = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                IBlockState blockState2 = Main.mc.theWorld.getBlockState(blockPos.add(0, 1, 0));
                //Main.mc.thePlayer.addChatMessage(new ChatComponentText(blockState.getBlock().toString()));
                if (blockState.getBlock() == Blocks.dirt && blockState2.getBlock() == Blocks.air) {
                    dirts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
            }
        }
        double biggest = -1;
        Vec3 furthest = null;
        for (int i = 0; i < dirts.size(); i++) {
            double dist = dirts.get(i).distanceTo(playerVec);
            if (dist > biggest) {
                biggest = dist;
                furthest = dirts.get(i);
            }
        }
        if (furthest != null && biggest < 4) {
            return new BlockPos(furthest.xCoord, furthest.yCoord, furthest.zCoord);
        }
        return null;
    }

    private BlockPos closestDirt() {
        int r = 5;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> dirts = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                //Main.mc.thePlayer.addChatMessage(new ChatComponentText(blockState.getBlock().toString()));
                if (blockState.getBlock() == Blocks.dirt) {
                    dirts.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (int i = 0; i < dirts.size(); i++) {
            double dist = dirts.get(i).distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = dirts.get(i);
            }
        }
        if (closest != null && smallest < 4) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private static int findItemInHotbar(String name) {
        InventoryPlayer inv = Main.mc.thePlayer.inventory;
        for (int i = 0; i < 9; i++) {
            ItemStack curStack = inv.getStackInSlot(i);
            if (curStack != null) {
                if (curStack.getDisplayName().contains(name)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static void rightClick() {
        try {
            Method rightClickMouse;
            try {
                rightClickMouse = Minecraft.class.getDeclaredMethod("func_147121_ag");
            } catch (NoSuchMethodException e) {
                rightClickMouse = Minecraft.class.getDeclaredMethod("rightClickMouse");
            }
            rightClickMouse.setAccessible(true);
            rightClickMouse.invoke(Main.mc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void click() {
        try {
            Method clickMouse;
            try {
                clickMouse = Minecraft.class.getDeclaredMethod("func_147116_af");
            } catch (NoSuchMethodException e) {
                clickMouse = Minecraft.class.getDeclaredMethod("clickMouse");
            }
            clickMouse.setAccessible(true);
            clickMouse.invoke(Main.mc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playAlert() {
        Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
    }

    private static void playAnnoyingAlert() {
        new Thread(() -> {
            try {
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
                Thread.sleep(100);
                Main.mc.thePlayer.playSound("random.orb", 1, 0.5F);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }).start();
    }
}
