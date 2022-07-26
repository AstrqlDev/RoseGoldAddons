package rosegoldaddons.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;
import rosegoldaddons.commands.Rosedrobe;
import rosegoldaddons.commands.Rosepet;
import scala.Int;
import tv.twitch.chat.Chat;

import java.util.List;

public class OpenSkyblockGui {
    private static boolean openTrades = false;

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(!Rosedrobe.openWardrobe && !openTrades && !Rosepet.openPetS) return;
        if (event.gui instanceof GuiChest) {
            Container container = ((GuiChest) event.gui).inventorySlots;
            if (container instanceof ContainerChest) {
                String chestName = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                if (Rosedrobe.openWardrobe) {
                    if (chestName.contains("Pets")) {
                        clickSlot(48, 0, 0);
                        clickSlot(32, 0, 1);
                        if (Rosedrobe.slot > 0) {
                            clickSlot(Rosedrobe.slot + 35, 0, 2);
                            Main.mc.thePlayer.closeScreen();
                        }
                        Rosedrobe.openWardrobe = false;
                    }
                } else if (openTrades) {
                    if (chestName.contains("Pets")) {
                        clickSlot(48, 0, 0);
                        clickSlot(22, 0, 1);
                        openTrades = false;
                    }
                } else if (Rosepet.openPetS) {
                    if (chestName.contains("Pets")) {
                        int currPage = 1;
                        int lastPage = 1;
                        if(chestName.startsWith("(")) {
                            currPage = Integer.parseInt(chestName.substring(chestName.indexOf("(")+1, chestName.indexOf("/")));
                            lastPage = Integer.parseInt(chestName.substring(chestName.indexOf("/")+1, chestName.indexOf(")")));
                        }
                        String petName = Rosepet.name;
                        int petSlot = Rosepet.petSlot;
                        if(petSlot != 0) {
                            clickSlot(petSlot - 1, 0, 0);
                            Rosepet.petSlot = 0;
                            Rosepet.openPetS = false;
                            return;
                        }
                        if (!petName.equals("")) {
                            List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                            for (Slot slot : chestInventory) {
                                if (!slot.getHasStack()) continue;
                                if (slot.getStack().getDisplayName().contains(petName)) {
                                    clickSlot(slot.slotNumber, 0, 0);
                                    Main.mc.thePlayer.closeScreen();
                                    Rosepet.openPetS = false;
                                    return;
                                }
                            }
                            if (currPage < lastPage) {
                                clickSlot(53, 0, 0);
                            } else {
                                Rosepet.openPetS = false;
                                Main.mc.thePlayer.closeScreen();
                                ChatUtils.sendMessage("No pet named "+petName+" found.");
                            }
                        } else {
                            ChatUtils.sendMessage("Invalid Pet Name");
                            Rosepet.openPetS = false;
                        }

                    }
                }
            }
        }
    }

    public static void openTradesMenu() {
        openTrades = true;
        Main.mc.thePlayer.sendChatMessage("/pets");
    }

    private void clickSlot(int slot, int type, int windowAdd) {
        Main.mc.playerController.windowClick(Main.mc.thePlayer.openContainer.windowId + windowAdd, slot, type, 0, Main.mc.thePlayer);
    }
}
