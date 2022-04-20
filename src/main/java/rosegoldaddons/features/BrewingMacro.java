package rosegoldaddons.features;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BrewingMacro {
    private Thread thread;
    private BlockPos stand;
    private boolean sell = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Main.brewingMacro || !Main.configFile.openstand || Main.configFile.alchindex != 0) return;
        if (event.phase == TickEvent.Phase.END) {
            if (Main.mc.currentScreen == null && stand != null && !sell) {
                if (Main.mc.playerController.onPlayerRightClick(
                        Main.mc.thePlayer,
                        Main.mc.theWorld,
                        Main.mc.thePlayer.inventory.getCurrentItem(),
                        stand,
                        EnumFacing.fromAngle(Main.mc.thePlayer.rotationYaw),
                        new Vec3(Math.random(), Math.random(), Math.random())
                )) {
                    Main.mc.thePlayer.swingItem();
                }
            }
        }
    }

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Main.brewingMacro) return;
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                if (event.gui instanceof GuiChest) {
                    Container container = ((GuiChest) event.gui).inventorySlots;
                    if (container instanceof ContainerChest) {
                        String chestName = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                        int sleep = Main.configFile.alchsleep;
                        try {
                            if (Main.configFile.alchindex == 0) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if ((slot.getStack().getDisplayName().contains("Speed") || slot.getStack().getDisplayName().contains("Weakness")) && slot.slotNumber < 54) {
                                            clickSlot(slot.slotNumber, 1, 1);
                                            Thread.sleep(sleep);
                                            if (isInventoryFull()) {
                                                sell = true;
                                                Main.mc.thePlayer.sendChatMessage("/sbmenu");
                                            }
                                        }
                                    }
                                }
                                if (sell) {
                                    if (chestName.contains("SkyBlock")) {
                                        Thread.sleep(sleep);
                                        clickSlot(22, 0, 0);
                                    } else if (chestName.contains("Trades")) {
                                        List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                        for (Slot slot : chestInventory) {
                                            if (!slot.getHasStack()) continue;
                                            if ((slot.getStack().getDisplayName().contains("Speed") || slot.getStack().getDisplayName().contains("Weakness")) && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 1, 0);
                                                Thread.sleep(sleep);
                                            }
                                        }
                                        Main.mc.thePlayer.closeScreen();
                                        sell = false;
                                    }
                                }
                            } else if (Main.configFile.alchindex == 1) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(42).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Water Bottle") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 1, 1);
                                                Thread.sleep(sleep);
                                            }
                                        }
                                    }
                                    if (Main.configFile.alchclose) {
                                        Main.mc.thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 2) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Nether Wart") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if (Main.configFile.alchclose) {
                                        Main.mc.thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 3) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if ((slot.getStack().getDisplayName().contains("Sugar") || slot.getStack().getDisplayName().contains("Spider Eye")) && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if (Main.configFile.alchclose) {
                                        Main.mc.thePlayer.closeScreen();
                                    }
                                }
                            } else if (Main.configFile.alchindex == 4) {
                                if (chestName.contains("Brewing Stand")) {
                                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                                    for (Slot slot : chestInventory) {
                                        if (!slot.getHasStack()) continue;
                                        if (!chestInventory.get(13).getHasStack()) {
                                            if (slot.getStack().getDisplayName().contains("Glowstone") && slot.slotNumber >= 54) {
                                                clickSlot(slot.slotNumber, 0, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(13, 1, 0);
                                                Thread.sleep(sleep / 2);
                                                clickSlot(slot.slotNumber, 0, 0);
                                                break;
                                            }
                                        }
                                    }
                                    if (Main.configFile.alchclose) {
                                        Main.mc.thePlayer.closeScreen();
                                    }
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, "brewing");
            thread.start();
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.brewingMacro) return;
        stand = closestStand();
        if (stand != null) {
            RenderUtils.drawBlockBox(stand, Color.YELLOW, Main.configFile.lineWidth, event.partialTicks);
        }
    }

    private boolean isInventoryFull() {
        List<Slot> inventory = Main.mc.thePlayer.inventoryContainer.inventorySlots;
        for (Slot slot : inventory) {
            if (!slot.getHasStack() && slot.slotNumber > 8) {
                return false;
            }
        }
        return true;
    }

    private BlockPos closestStand() {
        int r = 6;
        if (Main.mc.thePlayer == null) return null;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> stands = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                //Main.mc.thePlayer.addChatMessage(new ChatComponentText(blockState.getBlock().toString()));
                if (blockState.getBlock() == Blocks.brewing_stand) {
                    stands.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (int i = 0; i < stands.size(); i++) {
            double dist = stands.get(i).distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = stands.get(i);
            }
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private void clickSlot(int slot, int type, int mode) {
        Main.mc.playerController.windowClick(Main.mc.thePlayer.openContainer.windowId, slot, type, mode, Main.mc.thePlayer);
    }
}
