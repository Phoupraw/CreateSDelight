package ph.mcmod.csd.rei

import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry
import me.shedaniel.rei.api.common.plugins.REIServerPlugin
import me.shedaniel.rei.api.common.transfer.info.MenuInfoRegistry

object MyREIServerPlugin : REIServerPlugin {
    override fun registerDisplaySerializer(registry: DisplaySerializerRegistry) {
        registry.register(MyREIClientPlugin.BARBECUE_ID, SingleDisplay.serializer(::BarbecueDisplay))
        registry.register(MyREIClientPlugin.BARBECUE_CAMPFIRE_ID, SingleDisplay.serializer(::BarbecueCampfireDisplay))
        registry.register(MyREIClientPlugin.STEAMING_ID, SingleDisplay.serializer(::SteamingDisplay))
        registry.register(MyREIClientPlugin.ROASTING_ID, SingleDisplay.serializer(::RoastingDisplay))
    
    }
    
    override fun registerMenuInfo(registry: MenuInfoRegistry) {
    
    }
}