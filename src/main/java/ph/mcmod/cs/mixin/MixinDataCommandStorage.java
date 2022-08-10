package ph.mcmod.cs.mixin;

import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ph.mcmod.cs.MixinDelegates;
import ph.mcmod.cs.game.FieldingServer;

@Mixin(DataCommandStorage.class)
public class MixinDataCommandStorage implements FieldingServer {
    private MinecraftServer server;

    public void setServer(@NotNull MinecraftServer server) {
        this.server = server;
    }

    public @NotNull MinecraftServer getServer() {
        return server;
    }

    @Inject(method = "get", at = @At("RETURN"))
    private void listenGet(Identifier id, CallbackInfoReturnable<NbtCompound> cir) {
        MixinDelegates.listenGet((DataCommandStorage) (Object) this, id, cir.getReturnValue());
    }

    @Inject(method = "set", at = @At("HEAD"))
    private void listenSet(Identifier id, NbtCompound nbt, CallbackInfo ci) {
        MixinDelegates.listenSet((DataCommandStorage) (Object) this, id, nbt);
    }
}
