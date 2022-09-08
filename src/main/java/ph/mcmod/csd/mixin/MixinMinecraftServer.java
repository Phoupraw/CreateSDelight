package ph.mcmod.csd.mixin;

import net.minecraft.command.DataCommandStorage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    private DataCommandStorage dataCommandStorage;
//
//    @Deprecated
//    @Inject(method = "createWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;dataCommandStorage:Lnet/minecraft/command/DataCommandStorage;", shift = At.Shift.AFTER))
//    private void on(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
//        ((FieldingServer) dataCommandStorage).setServer((MinecraftServer) (Object) this);
//    }
//

}
