package ph.mcmod.cs.storage;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.InsertionOnlyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;
public class ThrowingStorage implements InsertionOnlyStorage<ItemVariant> {
    private World world;
    private Vec3d origin;
    private Vec3d velocity;
private Storage<ItemVariant> pendings;
    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        long amount = maxAmount;
        while (amount > 0) {
            int count = Math.min((int) amount, resource.getItem().getMaxCount());
            ItemEntity itemEntity = new ItemEntity(world, origin.x, origin.y, origin.z, resource.toStack(count), velocity.x, velocity.y, velocity.z);
            amount -= count;
            world.spawnEntity(itemEntity);
        }
        return 0;
    }

    @Override
    public Iterator<? extends StorageView<ItemVariant>> iterator(TransactionContext transaction) {
        return null;
    }
}
