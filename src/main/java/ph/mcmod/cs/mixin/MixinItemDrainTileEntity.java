package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ph.mcmod.cs.MixinDelegates;
@Mixin(value = ItemDrainTileEntity.class, remap = false)
public class MixinItemDrainTileEntity {
    @Shadow
    TransportedItemStack heldItem;

    @Shadow
    protected int processingTicks;

    @Inject(method = "tick", at = @At("HEAD"))
    private void particle(CallbackInfo ci) {
        MixinDelegates.particle((ItemDrainTileEntity) (Object) this, heldItem);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/EmptyingByBasin;canItemBeEmptied(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean cook(World world, ItemStack stack) {
        return MixinDelegates.cook((ItemDrainTileEntity) (Object) this, world, stack, heldItem);
    }

    @Redirect(method = "tryInsertingFromSide", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/EmptyingByBasin;canItemBeEmptied(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean check(World world, ItemStack stack) {
        return MixinDelegates.check((ItemDrainTileEntity) (Object) this, world, stack);
    }

    @Inject(method = "continueProcessing", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/processing/EmptyingByBasin;canItemBeEmptied(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"), cancellable = true)
    private void steam(CallbackInfoReturnable<Boolean> cir) {
        Integer processingTicks = MixinDelegates.steam((ItemDrainTileEntity) (Object) this, heldItem, cir);
        if (processingTicks != null) {
            this.processingTicks = processingTicks;
        }
    }
}
