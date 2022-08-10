@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.cs

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.client.render.RenderLayer
import ph.mcmod.cs.game.ItemStorageScreen
import ph.mcmod.cs.game.ItemQueueScreen
import ph.mcmod.cs.game.ItemStackScreen

object ClientMain {
    @JvmStatic
    fun init() {
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_STORAGE, ::ItemStorageScreen)
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_QUEUE, ::ItemQueueScreen)
        HandledScreens.register(MyRegistries.MyScreenHandlerTypes.ITEM_STACK, ::ItemStackScreen)
        
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING, SimpleFluidRenderHandler.coloredWater(0x56789A))
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING)
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING, SimpleFluidRenderHandler.coloredWater(0xCC0000))
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING)
    }
}