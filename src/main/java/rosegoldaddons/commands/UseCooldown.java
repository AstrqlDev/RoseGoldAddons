package rosegoldaddons.commands;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class UseCooldown implements ICommand {
    public static HashMap<String, Integer> RCitems = new HashMap<String, Integer>();
    public static HashMap<String, Integer> LCitems = new HashMap<String, Integer>();

    @Override
    public String getCommandName() {
        return "usecooldown";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            for (String i : RCitems.keySet()) {
                ChatUtils.sendMessage("§7Right click macro set on " + i + " §7with cooldown of " + RCitems.get(i) + " ms.");
            }
            for (String i : LCitems.keySet()) {
                ChatUtils.sendMessage("§7Left click macro set on " + i + " §7with cooldown of " + LCitems.get(i) + " ms.");
            }
            saveMacros();
            return;
        }
        if (args.length == 1 && isNumeric(args[0])) {
            InventoryPlayer inv = Main.mc.thePlayer.inventory;

            ItemStack curStack = inv.getStackInSlot(Main.mc.thePlayer.inventory.currentItem);
            if (curStack != null) {
                int cd = Integer.parseInt(args[0]);
                if (cd == 0) {
                    RCitems.remove(curStack.getDisplayName());
                    ChatUtils.sendMessage("§aSuccessfully Removed " + curStack.getDisplayName() + "§a.");
                    saveMacros();
                    return;
                }
                if (cd < 100) {
                    ChatUtils.sendMessage("§cInvalid Miliseconds, Minimum delay 100 Milisecond.");
                    saveMacros();
                    return;
                }
                RCitems.put(curStack.getDisplayName(), cd);
                ChatUtils.sendMessage("§aSuccessfully Added " + curStack.getDisplayName() + "§a to right click with a delay of " + cd + " ms.");
                saveMacros();
            } else {
                ChatUtils.sendMessage("§cError getting current held item.");
            }
        } else if (args.length == 2 && isNumeric(args[0]) && args[1].equalsIgnoreCase("left")) {
            InventoryPlayer inv = Main.mc.thePlayer.inventory;

            ItemStack curStack = inv.getStackInSlot(Main.mc.thePlayer.inventory.currentItem);
            if (curStack != null) {
                int cd = Integer.parseInt(args[0]);
                if (cd == 0) {
                    LCitems.remove(curStack.getDisplayName());
                    ChatUtils.sendMessage("§aSuccessfully Removed " + curStack.getDisplayName() + "§a.");
                    saveMacros();
                    return;
                }
                if (cd < 100) {
                    ChatUtils.sendMessage("§cInvalid Miliseconds, Minimum delay 100 Milisecond.");
                    saveMacros();
                    return;
                }
                ChatUtils.sendMessage("§aSuccessfully Added " + curStack.getDisplayName() + "§a to left click with a delay of " + cd + " ms.");
                LCitems.put(curStack.getDisplayName(), cd);
                saveMacros();
            } else {
                ChatUtils.sendMessage("§cError getting current held item.");
            }
        } else {
            ChatUtils.sendMessage("§cInvalid Arguments.");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void saveMacros() {
        try {
            String rcjson = new Gson().toJson(RCitems);
            Files.write(Paths.get("./config/rosegoldaddons/rcmacros.json"), rcjson.getBytes(StandardCharsets.UTF_8));
            String lcjson = new Gson().toJson(LCitems);
            Files.write(Paths.get("./config/rosegoldaddons/lcmacros.json"), lcjson.getBytes(StandardCharsets.UTF_8));
        } catch(Exception error) {
            System.out.println("Error saving config file");
            error.printStackTrace();
        }
        /*File rcfile = new File("./config/rosegoldaddons/rcmacro.txt");
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(rcfile));
            for (HashMap.Entry<String, Integer> entry : RCitems.entrySet()) {
                bf.write(entry.getKey() + ":" + entry.getValue());
                bf.newLine();
            }
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {
            }
        }
        File lcfile = new File("./config/rosegoldaddons/lcmacro.txt");
        BufferedWriter bf2 = null;
        try {
            bf2 = new BufferedWriter(new FileWriter(lcfile));
            for (HashMap.Entry<String, Integer> entry : LCitems.entrySet()) {
                bf2.write(entry.getKey() + ":" + entry.getValue());
                bf2.newLine();
            }
            bf2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf2.close();
            } catch (Exception e) {
            }
        }*/
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
