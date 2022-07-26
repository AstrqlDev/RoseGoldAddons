package rosegoldaddons.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Backpack implements ICommand {
    public static boolean openBP = false;
    public static int bpSlot = 0;

    @Override
    public String getCommandName() {
        return "backpack";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName();
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>(Arrays.asList("backpack", "bp"));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            Main.mc.thePlayer.sendChatMessage("/storage");
        } else {
            if (isNumeric(args[0])) {
                Main.mc.thePlayer.sendChatMessage("/storage");
                openBP = true;
                bpSlot = Integer.parseInt(args[0]);
            } else {
                ChatUtils.sendMessage("§cInvalid Arguments.");
                return;
            }
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

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }
}
