package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainItemHandler;
import com.simibubi.create.content.contraptions.fluids.actors.ItemDrainTileEntity;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(value = ItemDrainItemHandler.class)
public class MixinItemDrainItemHandler {
    @Shadow(remap = false)
    private ItemDrainTileEntity te;

    @Shadow
    private Direction side;

//    @Redirect(method = "insert(Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;JLnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)J", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
//    private int setCountForCampfireCookable(int intMaxAmount, int maxCount, ItemVariant resource, long maxAmount, TransactionContext transaction) {
//        return MixinDelegates.setCountForCampfireCookable((ItemDrainItemHandler) (Object) this, te, side, intMaxAmount, maxCount, resource,maxAmount,transaction);
//    }
}
