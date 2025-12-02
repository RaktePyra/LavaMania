package lavamaniareloaded.Blocks;

import lavamaniareloaded.AddBlockMod;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

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

    @Override
    protected void saveAdditional(ValueOutput writeView)
    {
        writeView.putInt("clicks", clicks);
        writeView.putInt("ticksSinceLast", ticksSinceLast);

        super.saveAdditional(writeView);
    }
    private int ticksSinceLast = 0;
    @Override
    protected void loadAdditional(ValueInput readView) {
        super.loadAdditional(readView);

        clicks = readView.getIntOr("clicks", 0);
        ticksSinceLast = readView.getIntOr("ticksSinceLast",0);
    }
    
    public static void tick(Level world, BlockPos blockPos, BlockState blockState, LavaGeneratorEntity entity)
    {
        if (world.isClientSide()) {
            return; // ne rien faire côté client
        }

        entity.ticksSinceLast++;
        if(entity.ticksSinceLast > 200)
        {
            entity.ticksSinceLast = 0;
        }
        System.out.println(entity.ticksSinceLast);

    }
}
