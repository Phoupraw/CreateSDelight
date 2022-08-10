package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainItemHandler;
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import ph.mcmod.cs.MixinDelegates;
@Mixin(value = ItemDrainItemHandler.class, remap = false)
public class MixinItemDrainItemHandler {
    @Shadow
    private ItemDrainTileEntity te;

    @Shadow
    private Direction side;

    @Redirect(method = "insert(Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int setCountForCampfireCookable(int intMaxAmount, int maxCount, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return MixinDelegates.setCountForCampfireCookable((ItemDrainItemHandler) (Object) this, te, side, intMaxAmount, maxCount, resource,maxAmount,transaction);
    }
}
