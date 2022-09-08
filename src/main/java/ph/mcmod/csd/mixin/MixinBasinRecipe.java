package ph.mcmod.csd.mixin;

import com.simibubi.create.content.contraptions.processing.BasinRecipe;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.content.contraptions.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ph.mcmod.csd.game.InjectBasinRecipe;

import java.util.List;
@Mixin(value = BasinRecipe.class,remap = false)
public class MixinBasinRecipe {
    @Inject(method = "apply(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;Lnet/minecraft/recipe/Recipe;Z)Z",at = @At(value = "INVOKE",target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void removeContainer(BasinTileEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir, boolean isBasinRecipe, Storage<ItemVariant>  availableItems, Storage<FluidVariant> availableFluids, BlazeBurnerBlock.HeatLevel heat, List <ItemStack> recipeOutputItems, List <FluidStack>recipeOutputFluids, List <Ingredient>ingredients){
        InjectBasinRecipe.removeContainer(basin,recipe,test,isBasinRecipe,availableItems,availableFluids,heat,recipeOutputItems,recipeOutputFluids,ingredients);
    }
    @Inject(method = "apply(Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;Lnet/minecraft/recipe/Recipe;Z)Z",at = @At(value = "INVOKE",target = "Lcom/simibubi/create/content/contraptions/processing/BasinTileEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Lnet/fabricmc/fabric/api/transfer/v1/transaction/TransactionContext;)Z"),locals = LocalCapture.CAPTURE_FAILHARD)
    private static void modifyResult(BasinTileEntity basin, Recipe<?> recipe, boolean test, CallbackInfoReturnable<Boolean> cir, boolean isBasinRecipe, Storage<ItemVariant>  availableItems, Storage<FluidVariant> availableFluids, BlazeBurnerBlock.HeatLevel heat, List <ItemStack> recipeOutputItems, List <FluidStack>recipeOutputFluids, List <Ingredient>ingredients, List<FluidIngredient> fluidIngredients, Transaction t, boolean fluidsAffected){
        InjectBasinRecipe.modifyResult(basin,recipe,test,isBasinRecipe,availableItems,availableFluids,heat,recipeOutputItems,recipeOutputFluids,ingredients,fluidIngredients,t,fluidsAffected);
    }
}
