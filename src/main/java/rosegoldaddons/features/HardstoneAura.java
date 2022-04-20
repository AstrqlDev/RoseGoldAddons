package rosegoldaddons.features;

import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.util.*;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.events.ReceivePacketEvent;
import rosegoldaddons.utils.*;
import scala.concurrent.impl.CallbackRunnable;

import java.awt.*;
import java.util.ArrayList;

public class HardstoneAura {
    private ArrayList<Vec3> solved = new ArrayList<>();
    private ArrayList<BlockPos> broken = new ArrayList<>();
    private static int currentDamage;
    private static BlockPos closestStone;
    private static Vec3 closestChest;
    private boolean stopHardstone = false;
    private static int ticks = 0;
    private static BlockPos gemstone;
    private static BlockPos lastGem;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!Main.autoHardStone) {
            currentDamage = 0;
            broken.clear();
            return;
        }
        if (!stopHardstone) {
            ticks++;
            if(Main.configFile.hardIndex == 0) {
                if (broken.size() > 10) {
                    broken.clear();
                }
            }
            if(Main.configFile.hardIndex == 1) {
                if (broken.size() > 6) {
                    broken.clear();
                }
            }
            if(ticks > 20) {
                broken.clear();
                ticks = 0;
            }
            closestStone = closestStone();
            if(gemstone != null && Main.mc.thePlayer != null) {
                if(lastGem != null && !lastGem.equals(gemstone)) {
                    currentDamage = 0;
                }
                lastGem = gemstone;
                if (currentDamage == 0) {
                    Main.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, gemstone, EnumFacing.DOWN));
                }
                PlayerUtils.swingItem();
                currentDamage++;
            }
            if (closestStone != null && gemstone == null) {
                currentDamage = 0;
                MovingObjectPosition fake = Main.mc.objectMouseOver;
                fake.hitVec = new Vec3(closestStone);
                EnumFacing enumFacing = fake.sideHit;
                if (enumFacing != null && Main.mc.thePlayer != null) {
                    Main.mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, closestStone, enumFacing));
                }
                PlayerUtils.swingItem();
                broken.add(closestStone);
            }
        }
    }

    @SubscribeEvent
    public void receivePacket(ReceivePacketEvent event) {
        if (!Main.autoHardStone) return;
        if (event.packet instanceof S2APacketParticles) {
            S2APacketParticles packet = (S2APacketParticles) event.packet;
            if (packet.getParticleType().equals(EnumParticleTypes.CRIT)) {
                Vec3 particlePos = new Vec3(packet.getXCoordinate(), packet.getYCoordinate(), packet.getZCoordinate());
                if (closestChest != null) {
                    stopHardstone = true;
                    double dist = closestChest.distanceTo(particlePos);
                    if (dist < 1) {
                        ShadyRotation.smoothLook(ShadyRotation.vec3ToRotation(particlePos), Main.configFile.smoothLookVelocity, () -> {});
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(Main.configFile.guilag) {
            Main.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FRAMERATE_LIMIT, 1);
        }
        if(!Main.autoHardStone) return;
        if (event.gui instanceof GuiChest) {
            Container container = ((GuiChest) event.gui).inventorySlots;
            if (container instanceof ContainerChest) {
                String chestName = ((ContainerChest) container).getLowerChestInventory().getDisplayName().getUnformattedText();
                if (chestName.contains("Treasure")) {
                    solved.add(closestChest);
                    stopHardstone = false;
                    Main.mc.thePlayer.closeScreen();
                }
            }
        }
    }

    @SubscribeEvent
    public void renderWorld(RenderWorldLastEvent event) {
        if (!Main.autoHardStone) return;
        closestStone = closestStone();
        closestChest = closestChest();
        if (closestStone != null) {
            RenderUtils.drawBlockBox(closestStone, new Color(128, 128, 128), Main.configFile.lineWidth, event.partialTicks);
        }
        if (closestChest != null) {
            RenderUtils.drawBlockBox(new BlockPos(closestChest.xCoord, closestChest.yCoord, closestChest.zCoord), new Color(255, 128, 0), Main.configFile.lineWidth, event.partialTicks);
        } else {
            stopHardstone = false;
        }
        if (gemstone != null) {
            IBlockState blockState = Main.mc.theWorld.getBlockState(gemstone);
            EnumDyeColor dyeColor = null;
            Color color = Color.BLACK;
            if (blockState.getBlock() == Blocks.stained_glass) {
                dyeColor = blockState.getValue(BlockStainedGlass.COLOR);
            }
            if (blockState.getBlock() == Blocks.stained_glass_pane) {
                dyeColor = blockState.getValue(BlockStainedGlassPane.COLOR);
            }
            if (dyeColor == EnumDyeColor.RED) {
                color = new Color(188, 3, 29);
            } else if (dyeColor == EnumDyeColor.PURPLE) {
                color = new Color(137, 0, 201);
            } else if (dyeColor == EnumDyeColor.LIME) {
                color = new Color(157, 249, 32);
            } else if (dyeColor == EnumDyeColor.LIGHT_BLUE) {
                color = new Color(60, 121, 224);
            } else if (dyeColor == EnumDyeColor.ORANGE) {
                color = new Color(237, 139, 35);
            } else if (dyeColor == EnumDyeColor.YELLOW) {
                color = new Color(249, 215, 36);
            } else if (dyeColor == EnumDyeColor.MAGENTA) {
                color = new Color(214, 15, 150);
            }
            RenderUtils.drawBlockBox(gemstone, color, Main.configFile.lineWidth, event.partialTicks);
        }
    }

    @SubscribeEvent
    public void clear(WorldEvent.Load event) {
        solved.clear();
    }

    private BlockPos closestStone() {
        if(Main.mc.theWorld == null) return null;
        if(Main.mc.thePlayer == null) return null;
        int r = 5;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, 1 + Main.configFile.hardrange, r);
        Vec3i vec3i2 = new Vec3i(r, Main.configFile.hardrangeDown, r);
        ArrayList<Vec3> stones = new ArrayList<Vec3>();
        ArrayList<Vec3> gemstones = new ArrayList<Vec3>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i2))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                if(Main.configFile.hardIndex == 0) {
                    if (!Main.configFile.includeExcavatable && blockState.getBlock() == Blocks.stone && !broken.contains(blockPos)) {
                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                    }
                    if (Main.configFile.includeOres) {
                        if ((blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore
                                || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore
                                || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore
                                || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack)
                                && !broken.contains(blockPos)) {
                            stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                        }
                    }
                    if(Main.configFile.includeExcavatable) {
                        if ((blockState.getBlock() == Blocks.gravel || blockState.getBlock() == Blocks.sand) && !broken.contains(blockPos)) {
                            stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                        }
                    }
                }
                if(Main.configFile.hardIndex == 1) {
                    EnumFacing dir = Main.mc.thePlayer.getHorizontalFacing();
                    int x = (int) Math.floor(Main.mc.thePlayer.posX);
                    int z =  (int) Math.floor(Main.mc.thePlayer.posZ);
                    switch (dir) {
                        case NORTH:
                            if(blockPos.getZ() <= z && blockPos.getX() == x) {
                                if(isSlow(blockState)) {
                                    gemstones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                else if (!Main.configFile.includeExcavatable && blockState.getBlock() == Blocks.stone && !broken.contains(blockPos)) {
                                    stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                if (Main.configFile.includeOres) {
                                    if ((blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore
                                            || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore
                                            || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore
                                            || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack)
                                            && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                                if(Main.configFile.includeExcavatable) {
                                    if ((blockState.getBlock() == Blocks.gravel || blockState.getBlock() == Blocks.sand) && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                            }
                            break;
                        case SOUTH:
                            if(blockPos.getZ() >= z && blockPos.getX() == x) {
                                if(isSlow(blockState)) {
                                    gemstones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                else if (!Main.configFile.includeExcavatable && blockState.getBlock() == Blocks.stone && !broken.contains(blockPos)) {
                                    stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                if (Main.configFile.includeOres) {
                                    if ((blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore
                                            || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore
                                            || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore
                                            || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack)
                                            && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                                if(Main.configFile.includeExcavatable) {
                                    if ((blockState.getBlock() == Blocks.gravel || blockState.getBlock() == Blocks.sand) && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                            }
                            break;
                        case WEST:
                            if(blockPos.getX() <= x && blockPos.getZ() == z) {
                                if(isSlow(blockState)) {
                                    gemstones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                else if (!Main.configFile.includeExcavatable && blockState.getBlock() == Blocks.stone && !broken.contains(blockPos)) {
                                    stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                if (Main.configFile.includeOres) {
                                    if ((blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore
                                            || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore
                                            || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore
                                            || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack)
                                            && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                                if(Main.configFile.includeExcavatable) {
                                    if ((blockState.getBlock() == Blocks.gravel || blockState.getBlock() == Blocks.sand) && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                            }
                            break;
                        case EAST:
                            if(blockPos.getX() >= x && blockPos.getZ() == z) {
                                if(isSlow(blockState)) {
                                    gemstones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                else if (!Main.configFile.includeExcavatable && blockState.getBlock() == Blocks.stone && !broken.contains(blockPos)) {
                                    stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                }
                                if (Main.configFile.includeOres) {
                                    if ((blockState.getBlock() == Blocks.coal_ore || blockState.getBlock() == Blocks.diamond_ore
                                            || blockState.getBlock() == Blocks.gold_ore || blockState.getBlock() == Blocks.redstone_ore
                                            || blockState.getBlock() == Blocks.iron_ore || blockState.getBlock() == Blocks.lapis_ore
                                            || blockState.getBlock() == Blocks.emerald_ore || blockState.getBlock() == Blocks.netherrack)
                                            && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                                if(Main.configFile.includeExcavatable) {
                                    if ((blockState.getBlock() == Blocks.gravel || blockState.getBlock() == Blocks.sand) && !broken.contains(blockPos)) {
                                        stones.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (Vec3 stone : stones) {
            double dist = stone.distanceTo(playerVec);
            if (dist < smallest) {
                smallest = dist;
                closest = stone;
            }
        }

        double smallestgem = 9999;
        Vec3 closestgem = null;
        for (Vec3 gem : gemstones) {
            double dist = gem.distanceTo(playerVec);
            if (dist < smallestgem) {
                smallestgem = dist;
                closestgem = gem;
            }
        }
        if (closestgem != null) {
            gemstone = new BlockPos(closestgem.xCoord, closestgem.yCoord, closestgem.zCoord);
        } else {
            gemstone = null;
        }
        if (closest != null && smallest < 5) {
            return new BlockPos(closest.xCoord, closest.yCoord, closest.zCoord);
        }
        return null;
    }

    private Vec3 closestChest() {
        if(Main.mc.theWorld == null) return null;
        if(Main.mc.thePlayer == null) return null;
        int r = 6;
        BlockPos playerPos = Main.mc.thePlayer.getPosition();
        playerPos.add(0, 1, 0);
        Vec3 playerVec = Main.mc.thePlayer.getPositionVector();
        Vec3i vec3i = new Vec3i(r, r, r);
        ArrayList<Vec3> chests = new ArrayList<>();
        if (playerPos != null) {
            for (BlockPos blockPos : BlockPos.getAllInBox(playerPos.add(vec3i), playerPos.subtract(vec3i))) {
                IBlockState blockState = Main.mc.theWorld.getBlockState(blockPos);
                //Main.mc.thePlayer.addChatMessage(new ChatComponentText(blockState.getBlock().toString()));
                if (blockState.getBlock() == Blocks.chest) {
                    chests.add(new Vec3(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
                }
            }
        }
        double smallest = 9999;
        Vec3 closest = null;
        for (Vec3 chest : chests) {
            if (!solved.contains(chest)) {
                double dist = chest.distanceTo(playerVec);
                if (dist < smallest) {
                    smallest = dist;
                    closest = chest;
                }
            }
        }
        return closest;
    }

    private boolean isSlow(IBlockState blockState) {
        if(blockState.getBlock() == Blocks.prismarine) {
            return true;
        } else if(blockState.getBlock() == Blocks.wool) {
            return true;
        } else if(blockState.getBlock() == Blocks.stained_hardened_clay) {
            return true;
        } else if(!Main.configFile.ignoreTitanium && blockState.getBlock() == Blocks.stone && blockState.getValue(BlockStone.VARIANT) == BlockStone.EnumType.DIORITE_SMOOTH) {
            return true;
        } else if(blockState.getBlock() == Blocks.gold_block) {
            return true;
        } else if(blockState.getBlock() == Blocks.stained_glass_pane || blockState.getBlock() == Blocks.stained_glass) {
            return true;
        }
        return false;
    }
}
