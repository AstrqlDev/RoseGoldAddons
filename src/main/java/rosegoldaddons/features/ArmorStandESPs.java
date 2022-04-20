package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class ArmorStandESPs {

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (Main.configFile.nucleusESP) {
            ArrayList<Entity> entities = getAllEntitiesInRange();
            for (Entity entity : entities) {
                if (entity instanceof EntityArmorStand) {
                    ItemStack itemStack = ((EntityArmorStand) entity).getCurrentArmor(3);
                    if (itemStack != null && itemStack.getItem() instanceof ItemSkull) {
                        if (itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODZhZGRiZDVkZWRhZDQwOTk5NDczYmU0YTdmNDhmNjIzNmE3OWEwZGNlOTcxYjVkYmQ3MzcyMDE0YWUzOTRkIn19fQ==")) {
                            RenderUtils.drawEntityBox(entity, Color.GREEN, Main.configFile.lineWidth, event.partialTicks);
                        } else if (itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGRhNDE0ZDI5Y2M5ZWJiZmMxY2JkY2QyMTFlZWU0NzI2ZDA2NzZiZTI2MmU5Y2I4ZWVmZmFmZDFmYzM4MGIxNCJ9fX0=")) {
                            RenderUtils.drawEntityBox(entity, Color.YELLOW, Main.configFile.lineWidth, event.partialTicks);
                        } else if (itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjMxMmE1YTEyZWNiMjRkNjg1MmRiMzg4ZTZhMzQ3MjFjYzY3ZjUyMmNjZGU3ZTgyNGI5Zjc1ZTk1MDM2YWM5MyJ9fX0=")) {
                            RenderUtils.drawEntityBox(entity, Color.WHITE, Main.configFile.lineWidth, event.partialTicks);
                        }
                    }
                }
            }
        }
    }

    private static ArrayList<Entity> getAllEntitiesInRange() {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity entity1 : (Main.mc.theWorld.loadedEntityList)) {
            if (!(entity1 instanceof EntityItem) && !(entity1 instanceof EntityXPOrb) &&!(entity1 instanceof EntityWither) && !(entity1 instanceof EntityPlayerSP)) {
                entities.add(entity1);
            }
        }
        return entities;
    }
}
