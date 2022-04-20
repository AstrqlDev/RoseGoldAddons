package rosegoldaddons.features;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.RenderUtils;
import rosegoldaddons.utils.ShadyRotation;

import java.awt.*;

public class EndermanMacro {
    private static Entity enderman;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Main.configFile.EndermanESP && !Main.endermanMacro) return;
        if(event.phase == TickEvent.Phase.END) return;
        enderman = getClosestEnderman();
        if(enderman != null && Main.endermanMacro && !ShadyRotation.running) {
            ShadyRotation.smoothLook(ShadyRotation.getRotationToEntity(enderman), Main.configFile.smoothLookVelocity, () -> {
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindSneak.getKeyCode(), false);

                if(!Main.mc.thePlayer.movementInput.sneak) {
                    Main.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(Main.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    Main.mc.thePlayer.movementInput.sneak = true;
                } else {
                    Main.mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(Main.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    Main.mc.thePlayer.movementInput.sneak = false;
                }
            });
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.configFile.EndermanESP) return;
        if (enderman == null) return;
        RenderUtils.drawEntityBox(enderman, Color.RED, Main.configFile.lineWidth, event.partialTicks);
    }

    private static Entity getClosestEnderman() {
        Entity eman = null;
        double closest = 9999;
        if(Main.mc.theWorld == null) return null;
        for (Entity entity1 : (Main.mc.theWorld.loadedEntityList)) {
            if (entity1 instanceof EntityEnderman && !(((EntityEnderman) entity1).getHealth() == 0) && Main.mc.thePlayer.canEntityBeSeen(entity1)) {
                double dist = entity1.getDistance(Main.mc.thePlayer.posX, Main.mc.thePlayer.posY, Main.mc.thePlayer.posZ);
                if (dist < closest) {
                    if(Main.configFile.macroRadius != 0 && dist < Main.configFile.macroRadius) {
                        closest = dist;
                        eman = entity1;
                    } if(Main.configFile.macroRadius == 0) {
                        closest = dist;
                        eman = entity1;
                    }
                }
            }
        }
        return eman;
    }
}
