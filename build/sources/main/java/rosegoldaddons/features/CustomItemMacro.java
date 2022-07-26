package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.commands.UseCooldown;

import java.lang.reflect.Method;

public class CustomItemMacro {
    private Thread thread;
    private int milis = 0;
    private boolean working = false;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END || working) return;
        if (!Main.autoUseItems) return;
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                try {
                    working = true;
                    int prevItem = Main.mc.thePlayer.inventory.currentItem;
                    for (String i : UseCooldown.RCitems.keySet()) {
                        if (milis % Math.floor(UseCooldown.RCitems.get(i)/100) == 0) {
                            int slot = findItemInHotbar(i);
                            if (slot != -1) {
                                Main.mc.thePlayer.inventory.currentItem = slot;
                                Main.mc.playerController.sendUseItem(Main.mc.thePlayer, Main.mc.theWorld, Main.mc.thePlayer.inventory.getStackInSlot(slot));
                            }
                        }
                    }
                    for (String i : UseCooldown.LCitems.keySet()) {
                        if (milis % Math.floor(UseCooldown.LCitems.get(i)/100) == 0) {
                            int slot = findItemInHotbar(i);
                            if (slot != -1) {
                                Main.mc.thePlayer.inventory.currentItem = slot;
                                Thread.sleep(100);
                                click();
                            }
                        }
                    }
                    Main.mc.thePlayer.inventory.currentItem = prevItem;
                    milis++;
                    Thread.sleep(100);
                    working = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "Custom Item Use");
            thread.start();
        }
    }

    public static void click() {
        /*try {
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
        }*/
        Main.mc.thePlayer.swingItem();
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
}
