package rosegoldaddons.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rosegoldaddons.Main;

import java.util.Map;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
    @Inject(method = "getDisplayName", at = @At(value = "RETURN"), cancellable = true)
    public void getFormattedText(CallbackInfoReturnable<IChatComponent> cir) {
        if(Main.pauseCustom) return;
        if (Main.init && Main.configFile.alchsleep != 63 || Main.configFile.skiblock != 263) {
            IChatComponent ict = new ChatComponentText("");
            ict.setChatStyle(cir.getReturnValue().getChatStyle());
            String text = cir.getReturnValue().getFormattedText();
            for (Map.Entry<String, String> entry : Main.nameCache.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                text = text.replace(key, value);
            }
            ict.appendText(text);
            cir.setReturnValue(ict);
        }
    }
}
