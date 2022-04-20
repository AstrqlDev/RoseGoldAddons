package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;

import java.util.ArrayList;

public class BloodTriggerBot {
    private static final String[] names = {"Bonzo", "Revoker", "Psycho", "Reaper", "Cannibal", "Mute", "Ooze", "Putrid", "Freak", "Leech", "Tear", "Parasite", "Flamer", "Skull", "Mr. Dead", "Vader", "Frost", "Walker", "WanderingSoul", "Shadow Assassin", "Lost Adventurer", "Livid", "Professor", "Spirit Bear"};

    @SubscribeEvent
    public void renderWorld(TickEvent.ClientTickEvent event) {
        if (!Main.bloodTriggerBot) return;
        if (event.phase == TickEvent.Phase.END) return;
        for (Entity entity : getAllBloodMobs()) {
            if (isLookingAtAABB(entity.getEntityBoundingBox(), 1F)) {
                Main.mc.thePlayer.swingItem();
            }
        }
    }

    private static boolean isLookingAtAABB(AxisAlignedBB aabb, float partialTicks) {
        Vec3 position = new Vec3(Main.mc.thePlayer.posX, (Main.mc.thePlayer.posY + Main.mc.thePlayer.getEyeHeight()), Main.mc.thePlayer.posZ);
        Vec3 look = Main.mc.thePlayer.getLook(partialTicks);
        look = scaleVec(look, 0.5F);
        for (int i = 0; i < 64; i++) {
            if (aabb.minX <= position.xCoord && aabb.maxX >= position.xCoord && aabb.minY <= position.yCoord && aabb.maxY >= position.yCoord && aabb.minZ <= position.zCoord && aabb.maxZ >= position.zCoord) {
                return true;
            }
            position = position.add(look);
        }

        return false;
    }

    private static ArrayList<Entity> getAllBloodMobs() {
        ArrayList<Entity> bloodMobs = new ArrayList<>();
        if (Main.mc.theWorld == null) return bloodMobs;
        for (Entity entity1 : (Main.mc.theWorld.loadedEntityList)) {
            if (entity1 instanceof EntityOtherPlayerMP && !entity1.isDead) {
                for (String name : names) {
                    if (entity1.getName().contains(name)) {
                        bloodMobs.add(entity1);
                    }
                }
            }
            if (entity1 instanceof EntityWither) {
                bloodMobs.add(entity1);
            }
        }
        return bloodMobs;
    }

    private static Vec3 scaleVec(Vec3 vec, float f) {
        return new Vec3(vec.xCoord * (double) f, vec.yCoord * (double) f, vec.zCoord * (double) f);
    }
}
