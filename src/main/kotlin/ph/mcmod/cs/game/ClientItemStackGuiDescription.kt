package ph.mcmod.cs.game

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import ph.mcmod.cs.MyRegistries

open class ClientItemStackGuiDescription(syncId: Int, playerInventory: PlayerInventory) : SyncedGuiDescription(MyRegistries.MyScreenHandlerTypes.ITEM_STACK, syncId, playerInventory, SimpleInventory(2), null) {
    init {
        val rootPanel = WGridPanel()
        this.rootPanel = rootPanel
        rootPanel.insets = Insets(17, 7, 7, 7)
        rootPanel.add(WItemSlot.of(blockInventory, 0), 4,0)
        rootPanel.add(WItemSlot.of(blockInventory, 1), 4, 1)
        @Suppress("LeakingThis") rootPanel.add(createPlayerInventoryPanel(), 0, 3)
        @Suppress("LeakingThis") rootPanel.validate(this)
    }
}