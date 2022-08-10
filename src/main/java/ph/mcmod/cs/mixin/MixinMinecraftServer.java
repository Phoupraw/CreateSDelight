package ph.mcmod.cs.mixin;

import net.minecraft.block.FluidBlock;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.game.FieldingServer;

import java.util.ArrayList;
import java.util.HashSet;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Shadow
    private DataCommandStorage dataCommandStorage;

    @Deprecated
    @Inject(method = "createWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/server/MinecraftServer;dataCommandStorage:Lnet/minecraft/command/DataCommandStorage;", shift = At.Shift.AFTER))
    private void on(WorldGenerationProgressListener worldGenerationProgressListener, CallbackInfo ci) {
        ((FieldingServer) dataCommandStorage).setServer((MinecraftServer) (Object) this);
    }


}
