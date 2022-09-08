package ph.mcmod.csd.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;

import java.util.Iterator;
public class ThrowerStorage implements InsertionOnlyStorage<ItemVariant> {
    private Entity Thrower;

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return null;
    }
}
