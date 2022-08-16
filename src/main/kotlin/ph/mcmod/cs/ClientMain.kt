@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.cs

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.screen.PlayerScreenHandler
import ph.mcmod.cs.game.ItemQueueScreen
import ph.mcmod.cs.game.ItemStackScreen
import ph.mcmod.cs.game.ItemStorageScreen

object ClientMain {
    @JvmStatic
    fun init() {
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_STORAGE, ::ItemStorageScreen)
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_QUEUE, ::ItemQueueScreen)
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_STACK, ::ItemStackScreen)
        
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING, SimpleFluidRenderHandler.coloredWater(0x56789A))
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING, SimpleFluidRenderHandler.coloredWater(0xCC0000))
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.SUNFLOWER_OIL, MyRegistries.MyFluids.SUNFLOWER_OIL_FLOWING, SimpleFluidRenderHandler.coloredWater(0xF0D51F))
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING, MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING, MyRegistries.MyFluids.SUNFLOWER_OIL, MyRegistries.MyFluids.SUNFLOWER_OIL_FLOWING)
        
//        BlockEntityRendererRegistry.register(MyRegistries.MyBlockEntityTypes.SHAFT) {
//            @Suppress("UNCHECKED_CAST")//BlockEntityRenderer从语义上说，是逆变的，所以下方转换不会出问题。
//            ShaftBlockEntity.Renderer(it) as BlockEntityRenderer<ShaftBlockEntity>
//        }
    }
}