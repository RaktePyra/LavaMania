package lavamaniareloaded.Blocks;

import com.mojang.serialization.MapCodec;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ElectricGenerator extends BaseEntityBlock {

    private boolean _isLit = false;
    public ElectricGenerator(Properties settings) {

        super(settings);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, false));
    }
    public static final BooleanProperty LIT = BlockStateProperties.LIT;;
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(ElectricGenerator::new);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LIT});
    }
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        ElectricGeneratorEntity entity = new ElectricGeneratorEntity(pos, state);
        entity.RegisterOwner(this);
        return entity ;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {

        if (!(world.getBlockEntity(pos) instanceof ElectricGeneratorEntity electric_generator_entity)) {
            return InteractionResult.PASS;
        }
        electric_generator_entity.DisplayEnergyAmount();


        return InteractionResult.SUCCESS;
    }
    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult)
    {
        if(level.isClientSide()) // Sinon les events sont déclenchés en double, 1 fois sur le client et une fois sur le serveur
        {
            return InteractionResult.FAIL;
        }
        if(interactionHand == InteractionHand.OFF_HAND)
        {
            return InteractionResult.PASS;
        }
        if (!(level.getBlockEntity(blockPos) instanceof ElectricGeneratorEntity electric_generator_entity)) {

            return InteractionResult.FAIL;
        }
        if(itemStack.getItem()==Items.LAVA_BUCKET)
        {
            if(electric_generator_entity.FillBlockWithLavaBucket() == TransactionContext.Result.COMMITTED) {
                player.displayClientMessage(Component.literal(electric_generator_entity.GetLavaAmount() + ""), true);
                player.setItemInHand(interactionHand, new ItemStack(new ItemLike() {
                    @Override
                    public Item asItem() {
                        return Items.BUCKET;
                    }
                }.asItem()));
                electric_generator_entity.RegisterOwner(this);
                level.setBlock(blockPos, (BlockState) blockState.setValue(LIT, true), 3);
                return InteractionResult.SUCCESS;
            }
        }

        if(itemStack == ItemStack.EMPTY)
        {
            useWithoutItem(blockState,level,blockPos,player,blockHitResult);
        }
        return InteractionResult.FAIL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntity.ELECTRIC_GENERATOR_ENTITY, ElectricGeneratorEntity::tick);
    }

    @Override
    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return (Boolean)blockState.getValue(LIT) ? 15 : 0;
    }

    public void LitGenerator(){
      this.getStateDefinition().any().setValue(LIT,true);
    }
    public void UnlitGenerator(Level level,BlockPos blockPos)
    {
        this.getStateDefinition().any().setValue(LIT,false);
        level.setBlock(blockPos, (BlockState) getStateDefinition().any().setValue(LIT, false), 3);
    }
}
