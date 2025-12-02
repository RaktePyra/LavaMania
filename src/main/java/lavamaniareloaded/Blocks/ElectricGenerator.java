package lavamaniareloaded.Blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ElectricGenerator extends BaseEntityBlock {

    public ElectricGenerator(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ElectricGenerator::new);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ElectricGeneratorEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof ElectricGeneratorEntity counterBlockEntity)) {
            return super.useWithoutItem(state, world, pos, player, hit);
        }

        counterBlockEntity.incrementClicks();
        player.displayClientMessage(Component.literal("You've clicked the block for the " + counterBlockEntity.getClicks() + "th time."), true);

        return InteractionResult.SUCCESS;
    }
    
}
