package lavamaniareloaded.Blocks;

import com.mojang.serialization.MapCodec;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
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
        return world.isClientSide() ? null : createTickerHelper(type, ModBlockEntity.LAVA_GENERATOR_ENTITY, LavaGeneratorEntity::tick);
    }
    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        System.out.println(itemStack.toString());
        if(level.isClientSide()) // Sinon les events sont déclenchés en double, 1 fois sur le client et une fois sur le serveur
        {
            return InteractionResult.SUCCESS;
        }
        if(interactionHand == InteractionHand.OFF_HAND)
        {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(blockPos) instanceof LavaGeneratorEntity lavaGenerator)) {

            return InteractionResult.FAIL;
        }
        if(itemStack.getItem() == Items.COAL)
        {
            lavaGenerator.setStack(LavaGeneratorEntity.SLOT_FUEL,itemStack);
            player.displayClientMessage(Component.literal(lavaGenerator.getStack(LavaGeneratorEntity.SLOT_FUEL).toString()), true);
            return InteractionResult.SUCCESS;
        }
        if(itemStack.getItem() == Items.COBBLESTONE)
        {
            lavaGenerator.setStack(LavaGeneratorEntity.SLOT_COBBLE,itemStack);
            player.displayClientMessage(Component.literal(lavaGenerator.getStack(LavaGeneratorEntity.SLOT_COBBLE).toString()), true);
            return InteractionResult.SUCCESS;
        }
        if (itemStack.getItem() == Items.BUCKET) {
            try (var transaction = net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
                // Vérifie si le tank contient au moins 1 seau
                if (lavaGenerator.LavaTankOutput.getAmount() >= 1000 && lavaGenerator.LavaTankOutput.getResource().getFluid() == Fluids.LAVA) {

                    // Retire 1000 mB du tank
                    long extracted = lavaGenerator.LavaTankOutput.extract(
                            FluidVariant.of(Fluids.LAVA),
                            1000,
                            transaction
                    );

                    if (extracted == 1000) {
                        transaction.commit();
                        lavaGenerator.liquidlava--;

                        // Remplace le seau vide par un seau de lave
                        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.LAVA_BUCKET));

                        lavaGenerator.setChanged();
                        return InteractionResult.SUCCESS;
                    } else {
                        transaction.abort();
                    }
                }
            }
        }



        return InteractionResult.FAIL;
    }
}
