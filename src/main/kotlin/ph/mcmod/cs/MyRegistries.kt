@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "unused")

package ph.mcmod.cs

import com.nhoryzon.mc.farmersdelight.item.Foods
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry
import com.simibubi.create.*
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPointType
import com.simibubi.create.foundation.networking.AllPackets
import com.simibubi.create.foundation.networking.SimplePacketBase
import net.devtech.arrp.api.RuntimeResourcePack
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage
import net.fabricmc.fabric.impl.tag.convention.TagRegistration
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.fluid.Fluids
import net.minecraft.item.*
import net.minecraft.particle.DefaultParticleType
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import org.jetbrains.annotations.ApiStatus
import ph.mcmod.cs.MyRegistries.MyItems.MUSHROOM_SOUP
import ph.mcmod.cs.MyRegistries.MyItems.WATER_BOWL
import ph.mcmod.cs.fluid.AcidFluid
import ph.mcmod.cs.fluid.TomatoSauceFluid
import ph.mcmod.cs.game.*
import ph.mcmod.cs.item.BowlFoodItem
import ph.mcmod.cs.item.WaterBowlItem
import ph.mcmod.kum.*
import ph.mcmod.kum.arrp.addRecipe_craftingShapeless

object MyRegistries : RegistryHelper(CSD, { MyItems.STEAMED_BUNS.defaultStack }) {
    
    object MyBlocks {
        @JvmField
        val MUSHROOM_SOUP = FluidBlock(MyFluids.MUSHROOM_SOUP_STILL, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("mushroom_soup")
          .lang("蘑菇汤")
        @JvmField
        val TOMATO_SAUCE = FluidBlock(MyFluids.TOMATO_SAUCE, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("tomato_sauce")
          .lang("番茄酱")
        @JvmField
        val SUNFLOWER_OIL = FluidBlock(MyFluids.SUNFLOWER_OIL, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("sunflower_oil")
          .lang("葵花籽油")
        @JvmField
        val COPPER_TUNNEL = CopperTunnelBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK))
          .register("copper_tunnel")
          .lang("铜隧道")
        
    }
    
    object MyBlockEntityTypes {
        @JvmField
        val COPPER_TUNNEL: BlockEntityType<CopperTunnelBlockEntity> = FabricBlockEntityTypeBuilder.create(::CopperTunnelBlockEntity).addBlock(MyBlocks.COPPER_TUNNEL).build().register("copper_tunnel")
        
    }
    
    object MyItems {
        //方块
        @JvmField
        val COPPER_TUNNEL = BlockItem(MyBlocks.COPPER_TUNNEL, ItemSettings()).register()
        
        //物品
        @JvmField
        val RAW_CHICKEN_STICK = Item(ItemSettings().food(Foods.CHICKEN_CUTS.get()))
          .register("raw_chicken_stick")
          .lang("生鸡肉串")
        @JvmField
        val CHICKEN_STICK = Item(ItemSettings().food(MyFoodComponents.CHICKEN_STICK))
          .register("chicken_stick")
          .lang("熟鸡肉串")
        @JvmField
        val STEAMED_BUNS = Item(ItemSettings().food(MyFoodComponents.STEAMED_BUNS))
          .register("steamed_buns")
          .lang("馒头")
        @JvmField
        val FULL_CHICKEN = Item(ItemSettings())
          .register("full_chicken")
          .lang("全鸡")
        @JvmField
        val CUT_FULL_CHICKEN = Item(ItemSettings())
          .register("cut_full_chicken")
          .lang("切开的全鸡")
        @JvmField
        val RAW_ROAST_CHICKEN_BLOCK = Item(ItemSettings())
          .register("raw_roast_chicken_block")
          .lang("涂料全鸡")
        @JvmField
        val ROAST_CHICKEN_BLOCK = Item(ItemSettings())
          .register("roast_chicken_block")
          .lang("烤全鸡")
        @JvmField
        val CHOPPED_VEGETABLES = Item(ItemSettings())
          .register("chopped_vegetables")
          .lang("蔬菜碎")
        
        @JvmField
        val WATER_BOWL = WaterBowlItem(ItemSettings())
          .register("water_bowl")
          .lang("碗装水")
        @JvmField
        val CHOPPING_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPING_MUSHROOM))
          .register("chopping_red_mushroom")
          .lang("切着的红蘑菇")
        @JvmField
        val CHOPPING_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPING_MUSHROOM))
          .register("chopping_brown_mushroom")
          .lang("切着的棕蘑菇")
        @JvmField
        val CHOPPED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPED_MUSHROOM))
          .register("chopped_red_mushroom")
          .lang("切好的红蘑菇")
        @JvmField
        val CHOPPED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPED_MUSHROOM))
          .register("chopped_brown_mushroom")
          .lang("切好的棕蘑菇")
        @JvmField
        val MUSHROOM_SOUP = BowlFoodItem(ItemSettings().food(MyFoodComponents.MUSHROOM_SOUP))
          .register("mushroom_soup")
          .lang("碗装蘑菇汤")
        @JvmField
        val MUSHROOM_STEW = BowlFoodItem(ItemSettings().food(MyFoodComponents.MUSHROOM_STEW))
          .register("mushroom_stew")
          .lang("精致蘑菇煲")
        @JvmField
        val SMOKED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("smoked_red_mushroom")
          .lang("烟熏红蘑菇")
        @JvmField
        val SMOKED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("smoked_brown_mushroom")
          .lang("烟熏棕蘑菇")
        @JvmField
        val BAKED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("baked_red_mushroom")
          .lang("烘烤红蘑菇")
        @JvmField
        val BAKED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("baked_brown_mushroom")
          .lang("烘烤棕蘑菇")
        @JvmField
        val TOMATO_SAUCE_BUCKET = BucketItem(MyFluids.TOMATO_SAUCE, ItemSettings().maxCount(1))
          .register("tomato_sauce_bucket")
          .lang("番茄酱桶")
        @JvmField
        val MUSHROOM_SOUP_BUCKET = BucketItem(MyFluids.MUSHROOM_SOUP_STILL, ItemSettings().maxCount(1))
          .register("mushroom_soup_bucket")
          .lang("蘑菇汤桶")
        @JvmField
        val SUNFLOWER_OIL_BUCKET = BucketItem(MyFluids.SUNFLOWER_OIL, ItemSettings().maxCount(1))
          .register("sunflower_oil_bucket")
          .lang("葵花籽油桶")
        
    }
    
    object MyFluids {
        @JvmField
        val MUSHROOM_SOUP_STILL = AcidFluid.Still().register("mushroom_soup")
        @JvmField
        val MUSHROOM_SOUP_FLOWING = AcidFluid.Flowing().register("mushroom_soup_flowing")
        @JvmField
        val TOMATO_SAUCE = TomatoSauceFluid.Still().register("tomato_sauce")
        @JvmField
        val TOMATO_SAUCE_FLOWING = TomatoSauceFluid.Flowing().register("tomato_sauce_flowing")
        @JvmField
        val SUNFLOWER_OIL = SunflowerOilFluid.Still()
          .register("sunflower_oil")
          .lang("葵花籽油")
        @JvmField
        val SUNFLOWER_OIL_FLOWING = SunflowerOilFluid.Flowing().register("sunflower_oil_flowing")
        
    }
    
    object MyBlockTags {
    }
    
    object MyItemTags {
        @JvmField
        val DOUGHS: TagKey<Item> = TagRegistration.ITEM_TAG_REGISTRATION.registerCommon("doughs")
        @JvmField
        val ANGLE_ON_DRAIN = newItemTag("angle_on_drain")
        
    }
    
    object MyScreenHandlerTypes {
    
    }
    
    object MyFoodComponents {
        @JvmField
        val CHICKEN_STICK: FoodComponent = FoodComponent.Builder()
          .hunger(1)
          .saturationModifier(0.5f)
          .statusEffect(StatusEffectInstance(StatusEffects.SATURATION, 1, 6), 1f)
          .build()
        @JvmField
        val STEAMED_BUNS: FoodComponent = FoodComponent.Builder()
          .hunger(1)
          .saturationModifier(0.5f)
          .statusEffect(StatusEffectInstance(StatusEffects.SATURATION, 1, 7), 1f)
          .build()
        
        @JvmField
        val CHOPPING_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0.5f).build()
        @JvmField
        val CHOPPED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0.5f).snack().build()
        @JvmField
        val MUSHROOM_SOUP: FoodComponent = FoodComponent.Builder().hunger(6).saturationModifier(0.5f).build()
        @JvmField
        val MUSHROOM_STEW: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0f).statusEffect(StatusEffectInstance(StatusEffects.SATURATION, 1, 20), 1f).build()
        @JvmField
        val SMOKED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(2).saturationModifier(0.5f).snack().build()
        @JvmField
        val BAKED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(2f).snack().build()
    }
    
    object MyParticles {
        @JvmField
        val OIL_BUBBLE: DefaultParticleType = FabricParticleTypes.simple().register("oil_bubble")
    }
    
    object MyPackets {
        @JvmField
        val COPPER_TUNNEL_FLAP = MyLoadedPacket(CopperTunnelFlapPacket::class.java, ::CopperTunnelFlapPacket, SimplePacketBase.NetworkDirection.PLAY_TO_CLIENT)
        
        init {
            var id = AllPackets.values().size
            for (packet in arrayOf(COPPER_TUNNEL_FLAP)) {
                var registered = false
                if (packet.direction == SimplePacketBase.NetworkDirection.PLAY_TO_SERVER) {
                    AllPackets.channel.registerC2SPacket(packet.type, id++)
                    registered = true
                }
                if (packet.direction == SimplePacketBase.NetworkDirection.PLAY_TO_CLIENT) {
                    AllPackets.channel.registerS2CPacket(packet.type, id++)
                    registered = true
                }
                if (!registered) {
                    LOGGER.warning("Could not register packet with type " + packet.type)
                }
            }
        }
    }
    
    object MyRecipeTypes {
        @JvmField
        val STEAMING = registerRecipeType("steaming", SteamingRecipe.Serializer)
        @JvmField
        val BARBECUE = registerRecipeType("barbecue", BarbecueRecipe.Serializer)
        @JvmField
        val ROASTING = registerRecipeType("roasting", RoastingRecipe.Serializer)
    }
    
    init {
        MyBlocks.loadClass()
        MyItems.loadClass()
        MyBlockEntityTypes.loadClass()
        MyScreenHandlerTypes.loadClass()
        MyRecipeTypes.loadClass()
        
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE) }, WATER_BOWL)
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(MyFluids.MUSHROOM_SOUP_STILL), FluidConstants.BOTTLE) }, MUSHROOM_SOUP)
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(MyFluids.TOMATO_SAUCE), FluidConstants.BOTTLE) }, ItemsRegistry.TOMATO_SAUCE.get())
        ArmInteractionPointType.register(ShaftArmInteractionPointType(id("shaft")))
        
        BlockSpoutingBehaviour.addCustomSpoutInteraction(id("depot"), SpoutingOil)
        arrpHelper.getLang()
          .entry("$namespace.ponder.item_drain_barbecue.header", "使用分液池烧烤")
          .entry("$namespace.ponder.item_drain_barbecue.text_1", "装有高温液体的分液池可以烧烤")
          .entry("$namespace.ponder.item_drain_barbecue.text_2", "液体越多，烧烤越快，1.5桶液体的速度是1桶的5倍")
          .entry("$namespace.ponder.item_drain_barbecue.text_3", "物品越多，烧烤越慢，1个物品的速度是64个物品的4倍")
        
        Create.registrate()
          .addRegisterCallback(Registry.BLOCK.key) {
              AllMovementBehaviours.registerBehaviour(AllBlocks.DEPOT.get(), DepotMovementBehaviour)
          }
          .addRegisterCallback(Registry.ITEM.key) {
          
          }
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register { server, resourceManager, success ->

        }
        arrpHelper.getTag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
          .add(Items.BOWL)
        arrpHelper.getTag(MyItemTags.DOUGHS)
          .add(AllItems.DOUGH.id)
        arrpHelper.getTag(MyItemTags.ANGLE_ON_DRAIN)
          .add(Items.STICK)
          .add(Items.BLAZE_ROD)
          .add(MyItems.RAW_CHICKEN_STICK)
          .add(MyItems.CHICKEN_STICK)
        arrpHelper.packAfter.addRecipe_barbecue(MyItems.RAW_CHICKEN_STICK, MyItems.CHICKEN_STICK)
        arrpHelper.packAfter.addRecipe_steaming(MyItemTags.DOUGHS, MyItems.STEAMED_BUNS)
//        arrpHelper.packAfter.addRecipe_roasting()
        runAtClient {
        
        }
    }
    @JvmStatic
    @ApiStatus.Internal
    fun afterFDInit() {
        arrpHelper.getTag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag)
          .add(ItemsRegistry.FRUIT_SALAD.get())
        arrpHelper.getTag(MyItemTags.DOUGHS)
          .add(ItemsRegistry.WHEAT_DOUGH.get())
        arrpHelper.getTag(MyItemTags.ANGLE_ON_DRAIN)
          .add(ItemsRegistry.BARBECUE_STICK.get())
        arrpHelper.packAfter.addRecipe_craftingShapeless(MyItems.CHICKEN_STICK, 1, Items.STICK, ItemsRegistry.CHICKEN_CUTS.get())
        arrpHelper.packAfter.addRecipe_roasting(MyItems.RAW_ROAST_CHICKEN_BLOCK, MyItems.ROAST_CHICKEN_BLOCK)
    }
    
    fun RuntimeResourcePack.addRecipe_barbecue(ingredient: Identifiable, result: Identifiable) = addRecipe_barbecue(ingredient, result, recipeId = id(ingredient.id.path).pre("barbecue/"))
    
}

fun RuntimeResourcePack.addRecipe_single(recipeType: Identifiable, ingredient: Identifiable, result: Identifiable, duration: Double, recipeId: Identifier): ByteArray {
    val isTag = ingredient is TagKey<*>
    return addData(recipeId.preRecipes().json(), """
        {
          "type": "$recipeType",
          "ingredient": {
            "${if (isTag) "tag" else "item"}": "${ingredient.id}"
          },
          "result": {
            "id": "${result.id}"
          },
          "duration": $duration
        }
    """.trimIndent().printL().toByteArray())
}

fun RuntimeResourcePack.addRecipe_barbecue(ingredient: Identifiable, result: Identifiable, duration: Double = SingleRecipe.DEFUALT_DURATION, recipeId: Identifier = result.id.pre("barbecue/")): ByteArray {
    return addRecipe_single(Identifier(CSD, "barbecue"), ingredient, result, duration, recipeId)
}

fun RuntimeResourcePack.addRecipe_steaming(ingredient: Identifiable, result: Identifiable, duration: Double = SingleRecipe.DEFUALT_DURATION, recipeId: Identifier = result.id.pre("steaming/")): ByteArray {
    return addRecipe_single(Identifier(CSD, "steaming"), ingredient, result, duration, recipeId)
}

fun RuntimeResourcePack.addRecipe_roasting(ingredient: Identifiable, result: Identifiable, duration: Double = SingleRecipe.DEFUALT_DURATION, recipeId: Identifier = result.id.pre("roasting/")): ByteArray {
    return addRecipe_single(Identifier(CSD, "roasting"), ingredient, result, duration, recipeId)
}

//fun RuntimeResourcePack.addRecipe_mixing(result: Identifiable, count: Int, vararg ingredients: Identifiable, duration: Double = SingleRecipe.DEFUALT_DURATION, recipeId: Identifier = result.id.pre("roasting/")): ByteArray {
//    return addRecipe_single(Identifier(MOD_ID, "roasting"), ingredient, result, duration, recipeId)
//}