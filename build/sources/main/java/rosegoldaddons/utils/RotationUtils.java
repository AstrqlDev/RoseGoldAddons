package rosegoldaddons.utils;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;

public class RotationUtils {
    static boolean working = false;
    static boolean snek = false;

    public static void shootEman() {
        if (Main.mc.currentScreen != null) {
            if (Main.mc.currentScreen instanceof GuiIngameMenu || Main.mc.currentScreen instanceof GuiChat) {
            } else {
                return;
            }
        }
        if (snek) return;
        new Thread(() -> {
            try {
                snek = true;
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                Thread.sleep(50);
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                Thread.sleep(50);
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                Thread.sleep(50);
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), false);
                snek = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void facePos(Vec3 vector) {
        if (Main.mc.currentScreen != null) {
            if (Main.mc.currentScreen instanceof GuiIngameMenu || Main.mc.currentScreen instanceof GuiChat) {
            } else {
                return;
            }
        }
        if (working) return;
        new Thread(() -> {
            try {
                working = true;
                double diffX = vector.xCoord - Main.mc.thePlayer.posX;
                double diffY = vector.yCoord - (Main.mc.thePlayer.posY + Main.mc.thePlayer.getEyeHeight());
                double diffZ = vector.zCoord - Main.mc.thePlayer.posZ;
                double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

                float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
                float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI - 90.0D);
                pitch = MathHelper.wrapAngleTo180_float(pitch - Main.mc.thePlayer.rotationPitch);
                yaw = MathHelper.wrapAngleTo180_float(yaw - Main.mc.thePlayer.rotationYaw);

                for (int i = 0; i < Main.configFile.smoothLookVelocity; i++) {
                    Main.mc.thePlayer.rotationYaw += yaw / Main.configFile.smoothLookVelocity;
                    Main.mc.thePlayer.rotationPitch += pitch / Main.configFile.smoothLookVelocity;
                    Thread.sleep(1);
                }
                //setup(new Rotation(yaw, pitch), (long) Main.configFile.smoothLookVelocity);
                working = false;
                if (Main.endermanMacro) {
                    shootEman();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void faceEntity(Entity en) {
        if (en instanceof EntityCreeper) {
            facePos(new Vec3(en.posX, en.posY, en.posZ));
        } else {
            facePos(new Vec3(en.posX, en.posY + Main.mc.thePlayer.getEyeHeight(), en.posZ));
        }
    }
}
