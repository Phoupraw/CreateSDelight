package ph.mcmod.cs

import net.devtech.arrp.api.RRPCallback
import net.devtech.arrp.api.RuntimeResourcePack
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.*
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.PlacedFeature
import ph.mcmod.kum.Identifiable
import ph.mcmod.kum.SIdentifiable
import ph.mcmod.kum.arrp.*
import ph.mcmod.kum.preBlock
import ph.mcmod.kum.preItem

open class RegistryHelper(val namespace: String, itemGroupIcon: (() -> ItemStack)? = null) {
    val itemGroup: ItemGroup = FabricItemGroupBuilder.build(Identifier(namespace, "item_group"), itemGroupIcon ?: { registeredItems.first().defaultStack })
    val registeredBlocks: MutableCollection<Block> = mutableListOf()
    val registeredItems: MutableCollection<Item> = mutableListOf()
    val registeredFluids: MutableCollection<Fluid> = mutableListOf()
    val arrpHelper = ArrpHelper(namespace)
    val blockStateds = mutableSetOf<Identifiable>()
    val modeleds = mutableSetOf<Identifiable>()
    val lootTableds = mutableSetOf<Identifiable>()
    var defualtLang = "zh_cn"
    
    init {
        RRPCallback.AFTER_VANILLA.register {
            for (block in registeredBlocks) {
                if (block !in blockStateds) {
                    arrpHelper.packBefore.addBlockState_single(block.id)
                    blockStateds += block
                }
                if (block !in modeleds) {
                    arrpHelper.packBefore.addModel_block_cubeAll(block.id)
                    modeleds += block
                }
                if (block !in lootTableds) {
                    arrpHelper.packBefore.addLootTable_itself(block)
                    lootTableds += block
                }
            }
            for (item in registeredItems) {
                if (item !in modeleds) {
                    when (item) {
                        is BlockItem -> arrpHelper.packBefore.addModel_item_block(item.id)
                        is ToolItem, is ShearsItem, is FishingRodItem, is FlintAndSteelItem ->
                            arrpHelper.packBefore.addModel_item_handheld(item.id)
                        else -> arrpHelper.packBefore.addModel_item_generated(item.id)
                    }
                    modeleds += item
                }
            }
            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                RuntimeResourcePack.DEFAULT_OUTPUT.deleteRecursively()
                arrpHelper.packBefore.dump()
                arrpHelper.packAfter.dump()
            }
        }
    }
    
    fun <T : Block> T.register(path: String): T {
        registeredBlocks += this
        return ph.mcmod.kum.register(this, Registry.BLOCK, namespace, path)
    }
    
    fun <T : Item> T.register(path: String): T {
        registeredItems += this
        return ph.mcmod.kum.register(this, Registry.ITEM, namespace, path)
    }
    fun <T : Fluid> T.register(path: String): T {
        registeredFluids += this
        return ph.mcmod.kum.register(this, Registry.FLUID, namespace, path)
    }
    fun <T : BlockItem> T.register(): T {
        registeredItems += this
        return ph.mcmod.kum.register(this)
    }
    
    fun <T : ScreenHandler> ScreenHandlerType.Factory<T>.register(path: String): ScreenHandlerType<T> = ph.mcmod.kum.register(ScreenHandlerType(this), Registry.SCREEN_HANDLER, namespace, path)
    
    fun <T : ConfiguredFeature<*, *>> T.register(path: String): T = ph.mcmod.kum.register(this, BuiltinRegistries.CONFIGURED_FEATURE, namespace, path)
    
    fun PlacedFeature.register(path: String): PlacedFeature = ph.mcmod.kum.register(this, BuiltinRegistries.PLACED_FEATURE, namespace, path)
    
    fun PlacedFeature.register(): PlacedFeature {
        val id = this.feature.value().id
        return ph.mcmod.kum.register(this, BuiltinRegistries.PLACED_FEATURE, id.namespace, id.path)
    }
    
    fun <T : BlockEntity> BlockEntityType<T>.register(path: String): BlockEntityType<T> = ph.mcmod.kum.register(this, Registry.BLOCK_ENTITY_TYPE, namespace, path)
    fun <E : Entity, T : EntityType<in E>> T.register(path: String): T = ph.mcmod.kum.register(this, Registry.ENTITY_TYPE, namespace, path)
    
    fun <T : SIdentifiable> T.tag(tagKey: TagKey<in T>): T {
        arrpHelper.getTag(tagKey).add(this)
        return this
    }
    
    fun <T : SIdentifiable> T.lang(value: String, lang: String = defualtLang): T {
        arrpHelper.getLang(lang).entry(this, value)
        return this
    }
    
    fun <T : Block> T.animate(interpolate: Boolean, frameTime: Int): T {
        arrpHelper.packAfter.addAnimation(this.id.preBlock(), interpolate, frameTime)
        return this
    }
    
    fun <T : Item> T.animate(interpolate: Boolean, frameTime: Int): T {
        arrpHelper.packAfter.addAnimation(this.id.preItem(), interpolate, frameTime)
        return this
    }
    
    fun <T : Identifiable> T.lootTabled(): T {
        lootTableds += this
        return this
    }
    
    fun id(path:String):Identifier = Identifier(namespace, path)
    fun ItemSettings(itemGroup: ItemGroup = this.itemGroup): FabricItemSettings = FabricItemSettings().group(itemGroup)
}