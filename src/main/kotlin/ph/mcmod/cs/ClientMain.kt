@file:Suppress("UNUSED_ANONYMOUS_PARAMETER")

package ph.mcmod.cs

import com.simibubi.create.AllBlocks
import net.devtech.arrp.json.loot.JCondition
import net.devtech.arrp.json.models.JModel
import net.devtech.arrp.json.models.JOverride
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.particle.WaterBubbleParticle
import net.minecraft.client.render.RenderLayer
import net.minecraft.screen.PlayerScreenHandler
import ph.mcmod.cs.game.CopperTunnelRenderer
import ph.mcmod.kum.preBlock
import ph.mcmod.kum.preItem

object ClientMain {
    @JvmStatic
    fun init() {
        
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING, SimpleFluidRenderHandler.coloredWater(0x56789A))
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING, SimpleFluidRenderHandler.coloredWater(0xCC0000))
        FluidRenderHandlerRegistry.INSTANCE.register(MyRegistries.MyFluids.SUNFLOWER_OIL, MyRegistries.MyFluids.SUNFLOWER_OIL_FLOWING, SimpleFluidRenderHandler.coloredWater(0xF0D51F))
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), MyRegistries.MyFluids.MUSHROOM_SOUP_STILL, MyRegistries.MyFluids.MUSHROOM_SOUP_FLOWING, MyRegistries.MyFluids.TOMATO_SAUCE, MyRegistries.MyFluids.TOMATO_SAUCE_FLOWING, MyRegistries.MyFluids.SUNFLOWER_OIL, MyRegistries.MyFluids.SUNFLOWER_OIL_FLOWING)
        
        
        BlockEntityRendererRegistry.register(MyRegistries.MyBlockEntityTypes.COPPER_TUNNEL, ::CopperTunnelRenderer)
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), MyRegistries.MyBlocks.COPPER_TUNNEL)
        
        
        MyRegistries.arrpHelper.packAfter.addModel(JModel.model(AllBlocks.ITEM_DRAIN.id.preBlock())
          .addOverride(JOverride(JCondition().parameter("custom_model_data",1),"$CSD:item/item_drain_water"))
          .addOverride(JOverride(JCondition().parameter("custom_model_data",2),"$CSD:item/item_drain_lava"))
          , AllBlocks.ITEM_DRAIN.id.preItem())
    
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register { atlasTexture, registry ->
            registry.register(MyRegistries.id("particle/oil_bubble"))
        }
        ParticleFactoryRegistry.getInstance().register(MyRegistries.MyParticles.OIL_BUBBLE, WaterBubbleParticle::Factory)
    }
}