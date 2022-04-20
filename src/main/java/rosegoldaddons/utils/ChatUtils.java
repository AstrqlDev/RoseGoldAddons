package rosegoldaddons.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.NotNull;
import rosegoldaddons.Main;

import java.util.Iterator;
import java.util.List;


public class ChatUtils {

    /* § */

    public static void sendMessage(String message) {
        if (Main.mc.thePlayer != null) {
            Main.mc.thePlayer.addChatMessage(new ChatComponentText("§f[§aRoseGoldAddons§f] " + message));
        }
    }

    public static ChatStyle createClickStyle(ClickEvent.Action action, String value) {
        ChatStyle style = new ChatStyle();
        style.setChatClickEvent(new ClickEvent(action, value));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (IChatComponent)new ChatComponentText(EnumChatFormatting.YELLOW + value)));
        return style;
    }
}
