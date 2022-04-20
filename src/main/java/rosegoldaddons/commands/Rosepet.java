package rosegoldaddons.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

public class Rosepet implements ICommand {
    public static boolean openPetS = false;
    public static String name = "";
    public static int petSlot = 0;

    @Override
    public String getCommandName() {
        return "rosepet";
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
        if (args.length == 1) {
            Main.mc.thePlayer.sendChatMessage("/pets");
            openPetS = true;
            if(isNumeric(args[0])) {
                petSlot = Integer.parseInt(args[0]);
            }
            name = args[0];
        } else {
            ChatUtils.sendMessage("Invalid Arguments");
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

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
