package rosegoldaddons.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.ItemCloth;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.events.ClickEvent;
import rosegoldaddons.utils.ChatUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoArrowAlign {
    private static final ArrayList<Entity> itemFrames = new ArrayList<>();
    private static final ArrayList<Entity> arrowItemFrames = new ArrayList<>();
    private static final Map<BlockPos, Integer> clicksPerFrame = new HashMap<>();
    private static final Map<BlockPos, Integer> toClickMap = new HashMap<>();
    private static boolean init = false;
    private static final BlockPos topLeft = new BlockPos(196, 125, 278);

    /*@SubscribeEvent
    public void debugging(TickEvent.ClientTickEvent event) {
        if (!Main.configFile.autoArrowAlign) return;
        if (init) {
            for (int y = 126; y > 119; y--) {
                String line = "";
                for (int z = 278; z < 283; z++) {
                    BlockPos BP = new BlockPos(topLeft.getX(), y, z);
                    int toClick = -1;
                    if (toClickMap.containsKey(BP)) {
                        toClick = toClickMap.get(BP);
                    }
                    line += (toClick + " ");
                }
                ChatUtils.sendMessage(y + ": " + line);
            }
            itemFrames.forEach(itemFrame -> {
                BlockPos BP = new BlockPos(itemFrame.getPosition().getX(), itemFrame.getPosition().getY(), itemFrame.getPosition().getZ());
                ChatUtils.sendMessage(BP+"");
            });
        }
    }*/


    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (!Main.configFile.autoArrowAlign) return;
        itemFrames.clear();
        for (Entity entity1 : (Main.mc.theWorld.loadedEntityList)) {
            if (entity1 instanceof EntityItemFrame) {
                itemFrames.add(entity1);
            }
        }
        arrowItemFrames.clear();
        itemFrames.forEach(itemFrame -> {
            ItemStack itemStack = ((EntityItemFrame) itemFrame).getDisplayedItem();
            if (itemStack != null) {
                if (itemStack.getItem() == Items.arrow) {
                    arrowItemFrames.add(itemFrame);
                }
            }
        });
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        init = false;
        toClickMap.clear();
        clicksPerFrame.clear();
    }

    public static void cheat() {
        initFrame();
        arrowItemFrames.forEach(itemFrame -> {
            BlockPos BP = new BlockPos(topLeft.getX(), itemFrame.getPosition().getY(), itemFrame.getPosition().getZ());
            int endRotationAmount = 0;
            if (clicksPerFrame.containsKey(BP)) {
                endRotationAmount = clicksPerFrame.get(BP);
            }
            int currRotationAmount = ((EntityItemFrame) itemFrame).getRotation();
            int toClick = 0;
            if (currRotationAmount < endRotationAmount) {
                toClick = endRotationAmount - currRotationAmount;
            } else if (currRotationAmount > endRotationAmount) {
                currRotationAmount = currRotationAmount - 8;
                toClick = endRotationAmount - currRotationAmount;
            }
            toClickMap.put(BP, toClick);
        });
        if (init) {
            int j = 0;
            Entity save = null;
            for (Entity itemFrame : arrowItemFrames) {
                BlockPos BP = new BlockPos(topLeft.getX(), itemFrame.getPosition().getY(), itemFrame.getPosition().getZ());
                if (toClickMap.containsKey(BP)) {
                    j++;
                    int toClick2 = toClickMap.get(BP);
                    if (j == arrowItemFrames.size()) {
                        save = itemFrame;
                        toClick2 -= 1;
                    }
                    for (int i = 0; i < toClick2; i++) {
                        interactWithEntity(itemFrame);
                    }
                }
            }
            Entity finalSave = save;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                    if(finalSave != null) {
                        interactWithEntity(finalSave);
                        PlayerControllerMP playerControllerMP = Main.mc.playerController;
                        playerControllerMP.sendUseItem(Main.mc.thePlayer, Main.mc.theWorld, Main.mc.thePlayer.getHeldItem());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static String findPattern() {
        ArrayList<Entity> redWools = new ArrayList<>();
        ArrayList<Entity> greenWools = new ArrayList<>();

        itemFrames.forEach(itemFrame -> {
            ItemStack itemStack = ((EntityItemFrame) itemFrame).getDisplayedItem();
            if (itemStack != null) {
                if (itemStack.getItem() instanceof ItemCloth && itemStack.getItemDamage() == 14) {
                    redWools.add(itemFrame);
                }
                if (itemStack.getItem() instanceof ItemCloth && itemStack.getItemDamage() == 5) {
                    greenWools.add(itemFrame);
                }
            }
        });
        if (redWools.size() != 0 && greenWools.size() != 0) {
            int relativeR1 = topLeft.getY() - (((Entity) redWools.toArray()[0]).getPosition()).getY();
            switch (redWools.size()) {
                case 1:
                    switch (greenWools.size()) {
                        case 1:
                            if (relativeR1 == 4) {
                                return "legs";
                            }
                            if (relativeR1 == 0) {
                                return "N";
                            }
                            return "spiral";
                        case 2:
                            if (relativeR1 == 2) {
                                return "W";
                            }
                            return "bottleneck";
                    }
                    break;
                case 2:
                    if (greenWools.size() > 1) return "zigzag";
                    return "S";
                case 3:
                    return "lines";
            }
        }
        return "Unrecognized";
    }

    private static void initFrame() {
        if (init) return;
        String pattern = findPattern();
        ChatUtils.sendMessage("Pattern Found: " + pattern);
        switch (pattern) {
            case "legs":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 1), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 1), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 3), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 1), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 3), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 1), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 3), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 3), 7);
                init = true;
                break;
            case "N":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 4), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 5);
                init = true;
                break;
            case "spiral":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 4), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ()), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 7);
                init = true;
                break;
            case "W":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 4), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 4), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ()), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 4), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ()), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 4), 5);
                init = true;
                break;
            case "bottleneck":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ()), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 1), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 4), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 3), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 4), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 1), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 3), 7);
                init = true;
                break;
            case "S":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 1), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 1);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 4), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 4), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ()), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 5);
                break;
            case "lines":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 3), 5);
                init = true;
                break;
            case "zigzag":
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 2), 3);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY(), topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 1, topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 2, topLeft.getZ() - 3), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 1), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 3, topLeft.getZ() - 2), 5);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 2), 7);
                clicksPerFrame.put(new BlockPos(topLeft.getX(), topLeft.getY() - 4, topLeft.getZ() - 3), 5);
                init = true;
                break;
        }

    }

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

    private static void interactWithEntity(Entity entity) {
        PlayerControllerMP playerControllerMP = Main.mc.playerController;
        playerControllerMP.interactWithEntitySendPacket(Main.mc.thePlayer, entity);
    }

    private static void interactWithEntity2(Entity entity) {
        PlayerControllerMP playerControllerMP = Main.mc.playerController;
        playerControllerMP.isPlayerRightClickingOnEntity(Main.mc.thePlayer, entity, Main.mc.objectMouseOver);
    }

    private static boolean isInSection3() {
        int x = Main.mc.thePlayer.getPosition().getX();
        int z = Main.mc.thePlayer.getPosition().getZ();
        return x < 218 && z > 251 && x > 196 && z < 319;
    }
}
