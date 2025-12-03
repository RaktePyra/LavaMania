package lavamaniareloaded.Blocks;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMaps;
import lavamaniareloaded.LavaMania;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ElectricGenerator extends BaseEntityBlock {

    public ElectricGenerator(Properties settings) {

        super(settings);
        this.registerDefaultState(defaultBlockState().setValue(POWER, 0));
    }
    public static final IntegerProperty POWER =IntegerProperty.create("power",0,15);
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
        if(itemStack.getItem()==Items.LAVA_BUCKET && electric_generator_entity.FillBlockWithLavaBucket() == TransactionContext.Result.COMMITTED)
        {
            //player.getInventory().setItem(player.getInventory().findSlotMatchingItem(new ItemStack(Items.LAVA_BUCKET)),new ItemStack(Items.BUCKET));
            player.displayClientMessage(Component.literal(electric_generator_entity.GetLavaAmount()+""), true);
            player.setItemInHand(interactionHand,new ItemStack(new ItemLike(){
                @Override
                public Item asItem() {
                        return Items.BUCKET;
                }
            }.asItem()) );

            return InteractionResult.SUCCESS;
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
}
