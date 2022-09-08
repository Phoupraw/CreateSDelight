package ph.mcmod.csd.storage

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import ph.mcmod.kum.KRunnable

interface CommitListenable {
    fun listenFinalCommit(callback: KRunnable)
    interface TStorage<T> : CommitListenable, Storage<T>
    class Helper : CommitListenable {
        val callbacks = mutableListOf<KRunnable>()
        /**
         * 在[CommitListenable.listenFinalCommit]里调用这个方法
         */
        override fun listenFinalCommit(callback: KRunnable) {
            callbacks += callback
        }
        /**
         * 在[SnapshotParticipant.onFinalCommit]里调用这个方法
         */
        fun finalCommit() {
            for (callback in callbacks) callback()
        }
    }
    
    private object EmptyStorage0 : TStorage<ItemVariant>, Storage<ItemVariant> by Storage.empty() {
        override fun listenFinalCommit(callback: KRunnable) {
        
        }
    }
    
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> empty() = EmptyStorage0 as TStorage<T>
    }
}

