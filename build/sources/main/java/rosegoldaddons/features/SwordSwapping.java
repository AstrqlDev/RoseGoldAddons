package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;

import java.lang.reflect.Method;

public class SwordSwapping {
    public static int tickCount = 0;
    private Thread thread;

    public static void rightClick() {
        try {
            Method rightClickMouse;
            try {
                rightClickMouse = Minecraft.class.getDeclaredMethod("rightClickMouse");
            } catch (NoSuchMethodException e) {
                rightClickMouse = Minecraft.class.getDeclaredMethod("func_147121_ag");
            }
            rightClickMouse.setAccessible(true);
            rightClickMouse.invoke(Main.mc);
        } catch (Exception e) {
            e.printStackTrace();
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

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (Main.mc.currentScreen != null) return;
        if (!Main.AOTSMacro && !Main.SoulWhipMacro) {
            tickCount = 0;
            return;
        }
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                try {
                    Thread.sleep(Main.configFile.swapFrequency);
                    int prevItem = Main.mc.thePlayer.inventory.currentItem;
                    int orbSlot = findItemInHotbar("Power Orb");
                    int tubaSlot = findItemInHotbar("Tuba");
                    int wandSlot = findItemInHotbar("Atonement");
                    int whipSlot = -1;
                    int aotsSlot = -1;
                    if(Main.AOTSMacro) {
                        aotsSlot = findItemInHotbar("Shredded");
                    }
                    if(Main.SoulWhipMacro) {
                        whipSlot = findItemInHotbar("Whip");
                    }
                    if(whipSlot != -1) {
                        Main.mc.thePlayer.inventory.currentItem = whipSlot;
                        rightClick();
                    }
                    if(aotsSlot != -1) {
                        Main.mc.thePlayer.inventory.currentItem = aotsSlot;
                        rightClick();
                    }
                    if(Main.configFile.UseUtility) {
                        if(tickCount % Math.round((1000/Main.configFile.swapFrequency*20)) == 1 && tubaSlot != -1) {
                            Thread.sleep(1);
                            Main.mc.thePlayer.inventory.currentItem = tubaSlot;
                            rightClick();
                        }
                        if(tickCount % Math.round((1000/Main.configFile.swapFrequency*59)) == 1 && orbSlot != -1) {
                            Thread.sleep(1);
                            Main.mc.thePlayer.inventory.currentItem = orbSlot;
                            rightClick();
                        }
                        if(tickCount % Math.round((1000/Main.configFile.swapFrequency*7)) == 1 && wandSlot != -1) {
                            Thread.sleep(1);
                            Main.mc.thePlayer.inventory.currentItem = wandSlot;
                            rightClick();
                        }
                    }
                    Main.mc.thePlayer.inventory.currentItem = prevItem;
                    tickCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Sword Swap");
            thread.start();
        }
    }
}
