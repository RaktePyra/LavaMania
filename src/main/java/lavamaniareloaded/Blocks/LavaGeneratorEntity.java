package lavamaniareloaded.Blocks;

import lavamaniareloaded.AddBlockMod;
import lavamaniareloaded.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LavaGeneratorEntity extends BlockEntity
{
    public LavaGeneratorEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntity.LAVA_GENERATOR_ENTITY, pos, state);
    }

    private int clicks = 0;
    public int getClicks()
    {
        return clicks;
    }

    public void incrementClicks()
    {
        clicks++;
        setChanged();
    }
}
