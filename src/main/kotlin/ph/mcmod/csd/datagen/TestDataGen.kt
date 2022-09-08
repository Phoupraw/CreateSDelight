package ph.mcmod.csd.datagen

import net.minecraft.block.Blocks
import net.minecraft.data.client.BlockStateModelGenerator
import net.minecraft.util.Identifier
import ph.mcmod.kum.preBlock

object TestDataGen {
    init {
        BlockStateModelGenerator.createSingletonBlockState(Blocks.BONE_BLOCK,Blocks.BONE_BLOCK.id.preBlock())
        
    }
}