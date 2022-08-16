package ph.mcmod.cs.mixin;

import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.block.depot.DepotBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(value = DepotBehaviour.class,remap = false)
public interface AccessDepotBehaviour {
    @Accessor
    TransportedItemStack getHeldItem();
    @Accessor
    void setHeldItem(TransportedItemStack value);
}
