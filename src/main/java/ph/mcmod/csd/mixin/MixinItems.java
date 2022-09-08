package ph.mcmod.csd.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import ph.mcmod.csd.item.BowlItem;
@Mixin(Items.class)
public class MixinItems {
    //(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;
    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference"})
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=bowl")), at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item newBowl(Item.Settings settings) {
        return new BowlItem(settings);
    }
}
