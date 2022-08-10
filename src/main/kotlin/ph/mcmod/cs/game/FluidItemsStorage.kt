package ph.mcmod.cs.game

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage
import net.minecraft.item.ItemStack
import ph.mcmod.kum.ProxyList

class FluidItemsStorage(val items:MutableList<ItemStack>): CombinedStorage<FluidVariant, Storage<FluidVariant>>(ProxyList(items,TODO(),TODO())) {
}