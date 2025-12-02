package lavamaniareloaded.Blocks;

import com.mojang.serialization.MapCodec;
import lavamaniareloaded.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LavaGenerator extends BaseEntityBlock
{
    public LavaGenerator(Properties settings)
    {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return simpleCodec(LavaGenerator::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new LavaGeneratorEntity(pos, state);
    }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (!(world.getBlockEntity(pos) instanceof LavaGeneratorEntity BlockEntity))
        {
            return super.useWithoutItem(state, world, pos, player, hit);
        }

        BlockEntity.incrementClicks();
        player.displayClientMessage(Component.literal("You've clicked the block for the " + BlockEntity.getClicks() + "th time."), true);

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type)
    {
        return createTickerHelper(type, ModBlockEntity.LAVA_GENERATOR_ENTITY, LavaGeneratorEntity::tick);
    }
}
