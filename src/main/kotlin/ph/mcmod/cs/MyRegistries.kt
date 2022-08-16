@file:Suppress("UNUSED_ANONYMOUS_PARAMETER", "unused")

package ph.mcmod.cs

import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPointType
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Blocks
import net.minecraft.block.FluidBlock
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.client.particle.BubblePopParticle
import net.minecraft.client.particle.FlameParticle
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.fluid.Fluids
import net.minecraft.item.*
import net.minecraft.particle.DefaultParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.PlayerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.command.CommandManager
import net.minecraft.tag.BlockTags
import net.minecraft.tag.ItemTags
import net.minecraft.text.LiteralText
import net.minecraft.util.math.Direction
import ph.mcmod.cs.MyRegistries.MyItems.MUSHROOM_SOUP
import ph.mcmod.cs.MyRegistries.MyItems.WATER_BOWL
import ph.mcmod.cs.fluid.AcidFluid
import ph.mcmod.cs.fluid.TomatoSauceFluid
import ph.mcmod.cs.game.*
import ph.mcmod.cs.item.BowlFoodItem
import ph.mcmod.cs.item.WaterBowlItem
import ph.mcmod.kum.*
import ph.mcmod.kum.arrp.addRecipe_craftingShaped
import ph.mcmod.kum.arrp.addRecipe_craftingShapeless

object MyRegistries : RegistryHelper(MOD_ID, { MyItems.VAULT.defaultStack }) {
    object MyBlocks {
        val MUSHROOM_SOUP = FluidBlock(MyFluids.MUSHROOM_SOUP_STILL, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("mushroom_soup")
          .lang("蘑菇汤")
        val TOMATO_SAUCE = FluidBlock(MyFluids.TOMATO_SAUCE, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("tomato_sauce")
          .lang("番茄酱")
        val SUNFLOWER_OIL = FluidBlock(MyFluids.SUNFLOWER_OIL, FabricBlockSettings.copyOf(Blocks.WATER))
          .register("sunflower_oil")
          .lang("葵花籽油")
        //以下都是旧项目的，以后要删掉
        @JvmField
        val VERY_LARGE_BARREL = VeryLargeBarrel.TBlock(FabricBlockSettings.copyOf(Blocks.BARREL)
          .allowsSpawning(Blocks::never))
          .register("very_large_barrel")
          .tag(BlockTags.AXE_MINEABLE)
          .lang("甚大木桶")
        @JvmField
        val ITEM_REDIRECTOR = RedirectorBlock(FabricBlockSettings.copyOf(Blocks.DROPPER).allowsSpawning(Blocks::never), ItemStorage.SIDED)
          .register("item_redirector")
          .tag(BlockTags.PICKAXE_MINEABLE)
          .lang("物品重定向器")
        @JvmField
        val FLUID_REDIRECTOR = RedirectorBlock(FabricBlockSettings.copyOf(Blocks.COPPER_BLOCK).allowsSpawning(Blocks::never), FluidStorage.SIDED)
          .register("fluid_redirector")
          .tag(BlockTags.PICKAXE_MINEABLE)
          .lang("流体重定向器")
        @JvmField
        @Deprecated("现在只是没有配方和创造模式物品栏里找不到，以后正式删除")
        val OBSERVER_DROPPER = PipeItemDropperObserver.Block(FabricBlockSettings.copyOf(Blocks.DROPPER)
          .allowsSpawning(Blocks::never))
          .register("observer_dropper")
          .tag(BlockTags.PICKAXE_MINEABLE)
          .lang("受激投掷器")
        @JvmField
        val ITEM_SET = ItemSetBlock(FabricBlockSettings.copyOf(Blocks.BARREL)
          .allowsSpawning(Blocks::never))
          .register("item_set")
          .tag(BlockTags.AXE_MINEABLE)
          .lang("物品集合")
        @JvmField
        val ITEM_QUEUE = ItemQueueBlock(FabricBlockSettings.copyOf(Blocks.BARREL)
          .allowsSpawning(Blocks::never))
          .register("item_queue")
          .tag(BlockTags.AXE_MINEABLE)
          .lang("物品队列")
        @JvmField
        val ITEM_STACK = ItemStackBlock(FabricBlockSettings.copyOf(Blocks.BARREL)
          .allowsSpawning(Blocks::never))
          .register("item_stack")
          .tag(BlockTags.AXE_MINEABLE)
          .lang("物品栈")
    }
    
    object MyBlockEntityTypes {
//        @JvmField
//        val SHAFT: BlockEntityType<ShaftBlockEntity> = FabricBlockEntityTypeBuilder.create(::ShaftBlockEntity).addBlock(AllBlocks.SHAFT.get()).build().register("shaft")
        
        //以下都是旧项目的，以后要删掉
        @JvmField
        val VERY_LARGE_BARREL: BlockEntityType<VeryLargeBarrel.TBlockEntity> = FabricBlockEntityTypeBuilder.create(VeryLargeBarrel::TBlockEntity).addBlock(MyBlocks.VERY_LARGE_BARREL).build().register("very_large_barrel")
        @JvmField
        val OBSERVER_DROPPER: BlockEntityType<PipeItemDropperObserver.BlockEntity> = FabricBlockEntityTypeBuilder.create(PipeItemDropperObserver::BlockEntity).addBlock(MyBlocks.OBSERVER_DROPPER).build().register("observer_dropper")
        @JvmField
        val ITEM_SET: BlockEntityType<ItemSetBlockEntity> = FabricBlockEntityTypeBuilder.create(::ItemSetBlockEntity).addBlock(MyBlocks.ITEM_SET).build().register("item_set")
        @JvmField
        val ITEM_QUEUE: BlockEntityType<ItemQueueBlockEntity> = FabricBlockEntityTypeBuilder.create(::ItemQueueBlockEntity).addBlock(MyBlocks.ITEM_QUEUE).build().register("item_queue")
        @JvmField
        val ITEM_STACK: BlockEntityType<ItemStackBlockEntity> = FabricBlockEntityTypeBuilder.create(::ItemStackBlockEntity).addBlock(MyBlocks.ITEM_STACK).build().register("item_stack")
    }
    
    object MyItems {
        val WATER_BOWL = WaterBowlItem(ItemSettings())
          .register("water_bowl")
          .lang("碗装水")
        val CHOPPING_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPING_MUSHROOM))
          .register("chopping_red_mushroom")
          .lang("切着的红蘑菇")
        val CHOPPING_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPING_MUSHROOM))
          .register("chopping_brown_mushroom")
          .lang("切着的棕蘑菇")
        val CHOPPED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPED_MUSHROOM))
          .register("chopped_red_mushroom")
          .lang("切好的红蘑菇")
        val CHOPPED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.CHOPPED_MUSHROOM))
          .register("chopped_brown_mushroom")
          .lang("切好的棕蘑菇")
        val MUSHROOM_SOUP = BowlFoodItem(ItemSettings().food(MyFoodComponents.MUSHROOM_SOUP))
          .register("mushroom_soup")
          .lang("碗装蘑菇汤")
        val MUSHROOM_STEW = BowlFoodItem(ItemSettings().food(MyFoodComponents.MUSHROOM_STEW))
          .register("mushroom_stew")
          .lang("精致蘑菇煲")
        val SMOKED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("smoked_red_mushroom")
          .lang("烟熏红蘑菇")
        val SMOKED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("smoked_brown_mushroom")
          .lang("烟熏棕蘑菇")
        val BAKED_RED_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("baked_red_mushroom")
          .lang("烘烤红蘑菇")
        val BAKED_BROWN_MUSHROOM = Item(ItemSettings().food(MyFoodComponents.SMOKED_MUSHROOM))
          .register("baked_brown_mushroom")
          .lang("烘烤棕蘑菇")
        val TOMATO_SAUCE_BUCKET = BucketItem(MyFluids.TOMATO_SAUCE, ItemSettings().maxCount(1))
          .register("tomato_sauce_bucket")
          .lang("番茄酱桶")
        val MUSHROOM_SOUP_BUCKET = BucketItem(MyFluids.MUSHROOM_SOUP_STILL, ItemSettings().maxCount(1))
          .register("mushroom_soup_bucket")
          .lang("蘑菇汤桶")
        val SUNFLOWER_OIL_BUCKET = BucketItem(MyFluids.SUNFLOWER_OIL, ItemSettings().maxCount(1))
          .register("sunflower_oil_bucket")
          .lang("葵花籽油桶")
        
        //以下都是旧项目的，以后要删掉
        @JvmField
        val VAULT = VeryLargeBarrel.TItem(MyBlocks.VERY_LARGE_BARREL, FabricItemSettings()).register()
        @JvmField
        val ITEM_REDIRECTOR = DescriptedBlockItem(MyBlocks.ITEM_REDIRECTOR, FabricItemSettings()).register()
        @JvmField
        val FLUID_REDIRECTOR = DescriptedBlockItem(MyBlocks.FLUID_REDIRECTOR, FabricItemSettings()).register()
        @JvmField
        val OBSERVER_DROPPER = DescriptedBlockItem(MyBlocks.OBSERVER_DROPPER, FabricItemSettings()).register()
        @JvmField
        val ITEM_SET = DescriptedBlockItem(MyBlocks.ITEM_SET, FabricItemSettings()).register()
        @JvmField
        val ITEM_QUEUE = DescriptedBlockItem(MyBlocks.ITEM_QUEUE, FabricItemSettings()).register()
        @JvmField
        val ITEM_STACK = DescriptedBlockItem(MyBlocks.ITEM_STACK, FabricItemSettings()).register()
    }
    
    object MyFluids {
        //        val MUSHROOM_SOUP = MushroomSoupFluid().register("mushroom_soup")
        val MUSHROOM_SOUP_STILL = AcidFluid.Still().register("mushroom_soup")
        val MUSHROOM_SOUP_FLOWING = AcidFluid.Flowing().register("mushroom_soup_flowing")
        val TOMATO_SAUCE = TomatoSauceFluid.Still().register("tomato_sauce")
        val TOMATO_SAUCE_FLOWING = TomatoSauceFluid.Flowing().register("tomato_sauce_flowing")
        val SUNFLOWER_OIL = SunflowerOilFluid.Still()
          .register("sunflower_oil")
          .lang("葵花籽油")
        val SUNFLOWER_OIL_FLOWING = SunflowerOilFluid.Flowing().register("sunflower_oil_flowing")
        
    }
    
    object MyBlockTags {
    }
    
    object MyItemTags {
    }
    
    object MyScreenHandlerTypes {
        @Deprecated("写得太烂了，不写了")
        val ITEM_STORAGE = ScreenHandlerType.Factory(::GuiStorageItem).register("item_storage")
        val ITEM_QUEUE = ScreenHandlerType.Factory(::ClientItemQueueGuiDescription).register("item_queue")
        val ITEM_STACK = ScreenHandlerType.Factory(::ClientItemStackGuiDescription).register("item_stacl")
    }
    
    object MyFoodComponents {
        val CHOPPING_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0.5f).build()
        val CHOPPED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0.5f).snack().build()
        val MUSHROOM_SOUP: FoodComponent = FoodComponent.Builder().hunger(6).saturationModifier(0.5f).build()
        val MUSHROOM_STEW: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(0f).statusEffect(StatusEffectInstance(StatusEffects.SATURATION, 1, 20), 1f).build()
        val SMOKED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(2).saturationModifier(0.5f).snack().build()
        val BAKED_MUSHROOM: FoodComponent = FoodComponent.Builder().hunger(1).saturationModifier(2f).snack().build()
    }
    
    object MyParticles {
        val OIL_BUBBLE: DefaultParticleType = FabricParticleTypes.simple().register("oil_bubble")
    }
    
    init {
        MyBlocks.loadClass()
        MyItems.loadClass()
        MyBlockEntityTypes.loadClass()
        MyScreenHandlerTypes.loadClass()
        arrpHelper.packAfter.addRecipe_craftingShaped(MyItems.VAULT)("#", "@", "#")("#", ItemTags.WOODEN_SLABS)("@", Items.BARREL)()
        arrpHelper.packAfter.addRecipe_craftingShapeless(MyItems.ITEM_REDIRECTOR, 1, Items.DROPPER, ConventionalItemTags.COPPER_INGOTS, Items.AMETHYST_SHARD)
        arrpHelper.packAfter.addRecipe_craftingShapeless(MyItems.FLUID_REDIRECTOR, 1, Items.DROPPER, ConventionalItemTags.COPPER_INGOTS, Items.AMETHYST_SHARD, Items.GLASS_PANE)
//        arrpHelper.packAfter.addRecipe_craftingShapeless(MyItems.OBSERVER_DROPPER, 1, Items.DROPPER as Identifiable, Items.OBSERVER, Items.AMETHYST_SHARD)
        arrpHelper.packAfter.addRecipe_craftingShaped(MyItems.ITEM_SET)(" # ", "#@#", " # ")("#", ConventionalItemTags.COPPER_INGOTS)("@", Items.BARREL)()
        arrpHelper.packAfter.addRecipe_craftingShaped(MyItems.ITEM_QUEUE)("&@#")("#", Items.REDSTONE)("@", ConventionalItemTags.COPPER_INGOTS)("&", Items.BARREL)()
        arrpHelper.packAfter.addRecipe_craftingShaped(MyItems.ITEM_STACK)("#", "@", "&")("#", Items.REDSTONE)("@", ConventionalItemTags.COPPER_INGOTS)("&", Items.BARREL)()
        ItemStorage.SIDED.registerForBlocks({ world, blockPos, blockState, blockEntity, direction -> MyBlocks.ITEM_REDIRECTOR.find(world, blockPos, blockState) }, MyBlocks.ITEM_REDIRECTOR)
        FluidStorage.SIDED.registerForBlocks({ world, blockPos, blockState, blockEntity, direction -> MyBlocks.FLUID_REDIRECTOR.find(world, blockPos, blockState) }, MyBlocks.FLUID_REDIRECTOR)
        ItemStorable.register(MyBlockEntityTypes.VERY_LARGE_BARREL)
        ItemStorable.register(MyBlockEntityTypes.ITEM_SET)
        ItemStorable.register(MyBlockEntityTypes.ITEM_QUEUE)
        ItemStorable.register(MyBlockEntityTypes.ITEM_STACK)
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(Fluids.WATER), FluidConstants.BOTTLE) }, WATER_BOWL)
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(MyFluids.MUSHROOM_SOUP_STILL), FluidConstants.BOTTLE) }, MUSHROOM_SOUP)
        FluidStorage.ITEM.registerForItems({ itemStack, context -> FullItemFluidStorage(context, Items.BOWL, FluidVariant.of(MyFluids.TOMATO_SAUCE), FluidConstants.BOTTLE) }, ItemsRegistry.TOMATO_SAUCE.get())
        ArmInteractionPointType.register(ShaftArmInteractionPointType(id("shaft")))
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            CommandRegistrationCallback.EVENT.register { dispatcher, dedicated ->
                dispatcher.register(CommandManager.literal("placeVault")
                  .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                    .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                      .then(CommandManager.argument("axis", AxisArgumentType)
                        .executes {
                            val pos1 = BlockPosArgumentType.getBlockPos(it, "pos1")
                            val pos2 = BlockPosArgumentType.getBlockPos(it, "pos2")
                            val axis = it.getArgument("axis", Direction.Axis::class.java)
                            val result = VeryLargeBarrel.place(it.source.world, pos1, pos2, axis)
                            if (result.isEmpty()) {
                                it.source.sendFeedback(LiteralText("$pos1,$pos2"), true)
                                1
                            } else {
                                it.source.sendError(LiteralText(result.toString()))
                                0
                            }
                        }))))
            }
        }
        BlockSpoutingBehaviour.addCustomSpoutInteraction(id("depot"), SpoutingOil())
        
        runAtClient {
            ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register { atlasTexture, registry ->
                registry.register(id("particle/oil_bubble"))
            }
            ParticleFactoryRegistry.getInstance().register(MyParticles.OIL_BUBBLE, BubblePopParticle::Factory)
        }
    }
}

