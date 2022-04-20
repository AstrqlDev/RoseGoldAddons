package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.RenderUtils;

import java.awt.*;
import java.util.ArrayList;

public class EntityReach {
    private static Entity toInteract;
    private static final ArrayList<Entity> solved = new ArrayList<>();

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        if (!Main.configFile.entityReach) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (toInteract != null) {
                if (toInteract instanceof EntityArmorStand) {
                    interactWithEntity2(toInteract);
                }
                interactWithEntity(toInteract);
                toInteract = null;
            }
        } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (toInteract != null) {
                if (toInteract instanceof EntityArmorStand) {
                    interactWithEntity2(toInteract);
                    solved.add(toInteract);
                }
                toInteract = null;
            }
        }
    }

    @SubscribeEvent
    public void clear(WorldEvent.Load event) {
        solved.clear();
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if(Main.mc.thePlayer == null || Main.mc.theWorld == null) return;
        if (!Main.configFile.entityReach) return;
        if (toInteract != null) {
            Entity stand = getClosestArmorStand(toInteract);
            String entityName = "Null";
            if(stand != null) {
                entityName = stand.getCustomNameTag();
            }
            if(entityName.equals("") && stand != null) {
                entityName = stand.getName();
            }
            RenderUtils.drawEntityBox(toInteract, Color.RED, Main.configFile.lineWidth, event.partialTicks);
            RenderUtils.renderWaypointText(entityName, toInteract.posX, toInteract.posY + toInteract.height, toInteract.posZ, event.partialTicks);
        }
        boolean found = false;
        ArrayList<Entity> entities = getAllEntitiesInRange();
        for (Entity entity : entities) {
            if (isLookingAtAABB(entity.getEntityBoundingBox(), event)) {
                toInteract = entity;
                found = true;
            }
            if(entity instanceof EntityArmorStand) {
                ItemStack itemStack = ((EntityArmorStand) entity).getCurrentArmor(3);
                if (itemStack != null && itemStack.getItem() instanceof ItemSkull) {
                    if(itemStack.serializeNBT().getCompoundTag("tag").getCompoundTag("SkullOwner").getCompoundTag("Properties").toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=")) {
                        if(solved.contains(entity)) {
                            RenderUtils.drawEntityBox(entity, Color.YELLOW, Main.configFile.lineWidth, event.partialTicks);
                        } else {
                            RenderUtils.drawEntityBox(entity, Color.MAGENTA, Main.configFile.lineWidth, event.partialTicks);
                        }
                    }
                }
            }
        }
        if (!Main.configFile.sticky && !found) {
            toInteract = null;
        }
    }

    private static Entity getClosestArmorStand(Entity entity) {
        Entity closest = null;
        double smallest = 9999;
        for (Entity entity1 : (Main.mc.theWorld.loadedEntityList)) {
            if (entity1 instanceof EntityArmorStand) {
                double dist = entity.getDistanceToEntity(entity1);
                if(dist < smallest) {
                    smallest = dist;
                    closest = entity1;
                }
            }
        }
        return closest;
    }

    private static boolean isLookingAtAABB(AxisAlignedBB aabb, RenderWorldLastEvent event) {
        Vec3 position = new Vec3(Main.mc.thePlayer.posX, (Main.mc.thePlayer.posY + Main.mc.thePlayer.getEyeHeight()), Main.mc.thePlayer.posZ);
        Vec3 look = Main.mc.thePlayer.getLook(event.partialTicks);
        look = scaleVec(look, 0.2F);
        for (int i = 0; i < 320; i++) {
            if (aabb.minX <= position.xCoord && aabb.maxX >= position.xCoord && aabb.minY <= position.yCoord && aabb.maxY >= position.yCoord && aabb.minZ <= position.zCoord && aabb.maxZ >= position.zCoord) {
                return true;
            }
            position = position.add(look);
        }

        return false;
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

    private static void interactWithEntity(Entity entity) {
        PlayerControllerMP playerControllerMP = Main.mc.playerController;
        playerControllerMP.interactWithEntitySendPacket(Main.mc.thePlayer, entity);
    }

    private static void interactWithEntity2(Entity entity) {
        PlayerControllerMP playerControllerMP = Main.mc.playerController;
        playerControllerMP.isPlayerRightClickingOnEntity(Main.mc.thePlayer, entity, Main.mc.objectMouseOver);
    }


    private static Vec3 scaleVec(Vec3 vec, float f) {
        return new Vec3(vec.xCoord * (double) f, vec.yCoord * (double) f, vec.zCoord * (double) f);
    }

}


