package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.ScoreboardUtils;

import java.util.List;

public class AutoSlayer {
    private static String lastMaddoxCommand = "/cb placeholder";
    private static boolean openMaddox = false;
    private static boolean startSlayer = false;
    private static boolean waitingForMaddox = false;
    private static int slayerSlot = 0;
    private static int slotLevel = 0;
    private static int debounce = 0;

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        if (!Main.configFile.autoSlayer) return;
        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (message.contains(":")) return;
        if (message.contains("[OPEN MENU]")) {
            List<IChatComponent> siblings = event.message.getSiblings();
            for (IChatComponent sibling : siblings) {
                if (sibling.getUnformattedText().contains("[OPEN MENU]")) {
                    lastMaddoxCommand = sibling.getChatStyle().getChatClickEvent().getValue();
                    openMaddox = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(InputEvent.KeyInputEvent event) {
        if (!Main.configFile.autoSlayer || !Main.configFile.clickMaddox || waitingForMaddox || debounce != 0) return;
        List<String> scoreboard = ScoreboardUtils.getSidebarLines();
        for (String line : scoreboard) {
            String cleanedLine = ScoreboardUtils.cleanSB(line);
            if (cleanedLine.contains("Boss slain!")) {
                int maddox = findItemInHotbar("Batphone");
                if (maddox != -1) {
                    ItemStack item = Main.mc.thePlayer.inventory.getStackInSlot(maddox);
                    int save = Main.mc.thePlayer.inventory.currentItem;
                    Main.mc.thePlayer.inventory.currentItem = maddox;
                    Main.mc.playerController.sendUseItem(Main.mc.thePlayer, Main.mc.theWorld, item);
                    Main.mc.thePlayer.inventory.currentItem = save;
                    waitingForMaddox = true;
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Main.configFile.autoSlayer) return;
        if (debounce > 0) {
            debounce--;
        }
        if (!openMaddox) return;
        Main.mc.thePlayer.sendChatMessage(lastMaddoxCommand);
        switch (Main.configFile.slayerTypeIndex) {
            case 1:
                slayerSlot = 10; //zombie
                slotLevel = 13; //tier 3
                break;
            case 2:
                slayerSlot = 10;
                slotLevel = 14; //tier 4
                break;
            case 3:
                slayerSlot = 10;
                slotLevel = 15; //tier 5
                break;
            case 4:
                slayerSlot = 11; //spider
                slotLevel = 13; //tier 3
                break;
            case 5:
                slayerSlot = 11;
                slotLevel = 14; //tier 4
                break;
            case 6:
                slayerSlot = 12; //sven
                slotLevel = 13; //tier 3
                break;
            case 7:
                slayerSlot = 12;
                slotLevel = 14; //tier 4
                break;
            case 8:
                slayerSlot = 13; //eman
                slotLevel = 12; //tier 2
                break;
            case 9:
                slayerSlot = 13;
                slotLevel = 13; //tier 3
                break;
            case 10:
                slayerSlot = 13;
                slotLevel = 14; //tier 4
                break;
        }
        openMaddox = false;
        startSlayer = true;
    }

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Main.configFile.autoSlayer) return;
        if (!startSlayer) return;
        if (slayerSlot == 0 || slotLevel == 0) return;
        if (event.gui instanceof GuiChest) {
            Container container = ((GuiChest) event.gui).inventorySlots;
            if (container instanceof ContainerChest) {
                String chestName = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                if (chestName.contains("Slayer")) {
                    List<Slot> chestInventory = ((GuiChest) Main.mc.currentScreen).inventorySlots.inventorySlots;
                    if (!chestInventory.get(13).getHasStack()) return;
                    if (chestInventory.get(13).getStack().getDisplayName().contains("Ongoing")) {

                    } else if (chestInventory.get(13).getStack().getDisplayName().contains("Complete")) {
                        clickSlot(13, 2, 0);
                        clickSlot(slayerSlot, 2, 1);
                        clickSlot(slotLevel, 2, 2);
                        clickSlot(11, 2, 3); //confirm
                        debounce = 80;
                    } else {
                        clickSlot(slayerSlot, 2, 0);
                        clickSlot(slotLevel, 2, 1);
                        clickSlot(11, 2, 2); //confirm
                        debounce = 80;
                    }
                    slayerSlot = 0;
                    slotLevel = 0;
                    startSlayer = false;
                    waitingForMaddox = false;
                }
            }
        }
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

    private void clickSlot(int slot, int type, int windowAdd) {
        Main.mc.playerController.windowClick(Main.mc.thePlayer.openContainer.windowId + windowAdd, slot, type, 0, Main.mc.thePlayer);
    }
}
