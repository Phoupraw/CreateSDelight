package ph.mcmod.cs.game

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.cottonmc.cotton.gui.widget.*
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import net.minecraft.text.Text
import ph.mcmod.cs.MyRegistries
import ph.mcmod.cs.api.*
import ph.mcmod.cs.storage.CommitListenable
import ph.mcmod.kum.*

class GuiStorageItem(syncId: Int, playerInventory: PlayerInventory, val storage: CommitListenable.TStorage<ItemVariant>? = null) : SyncedGuiDescription(MyRegistries.MyScreenHandlerTypes.ITEM_STORAGE, syncId, playerInventory, null, null) {
    val wItemSlot: WItemSlot
    
    init {
        blockInventory = SimpleInventory(6 * 8)
        wItemSlot = WItemSlot.of(blockInventory, 0, 8, 6).setModifiable(false)
        val rootPanel = WGridPanel().setInsets(Insets(16, 7, 7, 7))
        rootPanel.add(wItemSlot, 0, 0)
        rootPanel.add(createPlayerInventoryPanel(), 0, 6)
        this.rootPanel = rootPanel
        rootPanel.validate(this)
        updateItems()
        storage?.listenFinalCommit { updateItems() }
    }
    
    fun updateItems() {
        storage ?: return
        var i = 0
        for (view in storage.iterable(null)) {
            if (view.empty) continue
            blockInventory.setStack(i, view.resource.toStack(view.amount.toInt()))
            i++
            if (i >= blockInventory.size()) break
        }
        while (i < blockInventory.size()) {
            blockInventory.setStack(i, ItemStack.EMPTY)
            i++
        }
    }
    
    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType, player: PlayerEntity) {
        super.onSlotClick(slotIndex, button, actionType, player)
        storage ?: return
        if (slotIndex < 0 || slotIndex >= slots.size) return
        val slot = getSlot(slotIndex)
        val slotStack = slot.stack
        if (slot.inventory !== blockInventory) {
            if (actionType == SlotActionType.QUICK_MOVE && !slotStack.isEmpty) {
                StorageUtil.move(slot.asStorage(), storage, always(), slotStack.maxCount.toLong(), null)
            }
            return
        }
        val leftClick = button == 0
        when (actionType) {
            SlotActionType.PICKUP -> {
                if (!cursorStack.isEmpty) {
                    StorageUtil.move(cursorStorage, storage, always(), if (leftClick) cursorStack.count.toLong() else 1, null)
                } else if (!slotStack.isEmpty) {
                    StorageUtil.move(storage, cursorStorage, matching(slotStack), slotStack.count.toLong().let { if (leftClick) it else it / 2 }, null)
                }
            }
            SlotActionType.QUICK_MOVE -> {
                if (!slotStack.isEmpty) {
                    StorageUtil.move(storage, playerInventory.asStorage(), matching(slotStack), slotStack.maxCount.toLong(), null)
                }
            }
            SlotActionType.SWAP -> {
                val temp = ContainerItemContext.withInitial(ItemStack.EMPTY).mainSlot
                StorageUtil.move(storage, temp, matching(slotStack), slotStack.count.toLong(), null)
                StorageUtil.move(playerInventory.asStorage().getSlot(button), storage, always(), Long.MAX_VALUE, null)
                StorageUtil.move(temp, playerInventory.asStorage().getSlot(button), always(), Long.MAX_VALUE, null)
                if (!temp.empty) {
                    StorageUtil.move(temp, storage, always(), Long.MAX_VALUE, null)
                    if (!temp.empty) {
                        Transaction.openOuter().useIt {
                            for (view in temp.iterator(this)) {
                                playerInventory.asStorage().offerOrDrop(view.resource, view.amount, this)
                            }
                            commit()
                        }
                    }
                }
            }
            SlotActionType.CLONE -> {
                if (cursorStack.isEmpty && !slotStack.isEmpty) {
                    cursorStack = slotStack.copy().apply { count = maxCount }
                }
            }
            else -> {}
        }
        
    }
}

class ItemStorageScreen(description: GuiStorageItem?, inventory: PlayerInventory?, title: Text?) : CottonInventoryScreen<GuiStorageItem>(description, inventory, title)