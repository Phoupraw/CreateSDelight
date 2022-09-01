package ph.mcmod.cs.rei

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry

object MyREIServerPlugin : REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(MyREIClientPlugin.BARBECUE_ID, SingleDisplay.serializer(::BarbecueDisplay))
        registry.register(MyREIClientPlugin.BARBECUE_CAMPFIRE_ID, SingleDisplay.serializer(::BarbecueCampfireDisplay))
        registry.register(MyREIClientPlugin.STEAMING_ID, SingleDisplay.serializer(::SteamingDisplay))
    
    }
    
    override fun registerMenuInfo(registry: MenuInfoRegistry) {
    
    }
}