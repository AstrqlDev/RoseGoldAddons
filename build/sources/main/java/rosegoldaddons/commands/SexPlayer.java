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

public class SexPlayer implements ICommand {
    @Override
    public String getCommandName() {
        return "sexplayer";
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
            Main.mc.thePlayer.sendChatMessage("/pc !SXAURA!");
            ChatUtils.sendMessage("Successfully sex-arua'd party chat");
            return;
        }
        if(args.length != 1) {
            ChatUtils.sendMessage("Invalid Arguments");
            return;
        }
        Main.mc.thePlayer.sendChatMessage("/msg "+args[0]+" !SXAURA!");
        ChatUtils.sendMessage("Successfully sex-arua'd "+args[0]);
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
}
