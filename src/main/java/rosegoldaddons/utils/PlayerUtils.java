package rosegoldaddons.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;

public class PlayerUtils {
    public static boolean pickaxeAbilityReady = false;

    public static void swingItem() {
        MovingObjectPosition movingObjectPosition = Main.mc.objectMouseOver;
        if (movingObjectPosition != null && movingObjectPosition.entityHit == null) {
            Main.mc.thePlayer.swingItem();
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Load event) {
        pickaxeAbilityReady = false;
    }

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent event) {
        try {
            String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
            if (message.contains(":") || message.contains(">")) return;
            if(message.startsWith("You used your")) {
                pickaxeAbilityReady = false;
            } else if(message.endsWith("is now available!")) {
                pickaxeAbilityReady = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
