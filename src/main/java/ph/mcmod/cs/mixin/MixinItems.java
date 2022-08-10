package ph.mcmod.cs.mixin;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ph.mcmod.cs.item.BowlItem;
@Mixin(Items.class)
public class MixinItems {
    //(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;
    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "InvalidInjectorMethodSignature", "UnresolvedMixinReference"})
    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=bowl")), at = @At(value = "NEW", target = "(Lnet/minecraft/item/Item$Settings;)Lnet/minecraft/item/Item;", ordinal = 0))
    private static Item newBowl(Item.Settings settings) {
        return new BowlItem(settings);
    }
}
