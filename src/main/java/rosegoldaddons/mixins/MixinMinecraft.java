package rosegoldaddons.mixins;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rosegoldaddons.events.ClickEvent;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "rightClickMouse", at = @At("HEAD"), cancellable = true)
    public void rightClickEvent(CallbackInfo ci) {
        if(MinecraftForge.EVENT_BUS.post(new ClickEvent.Right())) ci.cancel();
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    public void leftClickEvent(CallbackInfo ci) {
        if(MinecraftForge.EVENT_BUS.post(new ClickEvent.Left())) ci.cancel();
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"), cancellable = true)
    public void middleClickEvent(CallbackInfo ci) {
        if(MinecraftForge.EVENT_BUS.post(new ClickEvent.Middle())) ci.cancel();
    }
}
