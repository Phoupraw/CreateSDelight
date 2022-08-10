package ph.mcmod.cs.game

import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ph.mcmod.cs.MOD_ID

class ItemQueueScreen(description: ClientItemQueueGuiDescription, inventory: PlayerInventory, title: Text) : ItemPortsScreen<ClientItemQueueGuiDescription>(description, inventory, title) {
    override val texture get() = TEXTURE
    
    companion object {
        val TEXTURE = Identifier(MOD_ID, "textures/gui/item_queue.png")
    }
}