package lavamaniareloaded.Blocks;

import lavamaniareloaded.IEnergyStorage;
import lavamaniareloaded.LavaMania;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

public class ElectricGeneratorEntity extends BlockEntity implements IEnergyStorage {

    private int _energyAmount = 0;
    private int _ticksSinceLast = 0;
    private Block _ownerBlock;
    public ElectricGeneratorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.ELECTRIC_GENERATOR_ENTITY, pos, state);

    }
    public void RegisterOwner(Block owner)
    {
        _ownerBlock = owner;
    }
    private SingleVariantStorage<FluidVariant> _inLavaTank = new SingleVariantStorage<>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant fluidVariant) {
            return  (8 * FluidConstants.BUCKET) / 81;
        }
    };;
    private int clicks = 0;
    private int _outLavaMbAmount = 0;
    public int getClicks() {
        return clicks;
    }
    public long GetLavaAmount()
    {
        return _inLavaTank.getAmount();
    }
    public void incrementClicks() {
        clicks++;
        setChanged();
    }

    public TransactionContext.Result FillBlockWithLavaBucket()
    {
        long mbToInsert = BUCKET/81;
        FluidVariant lava = FluidVariant.of(Fluids.LAVA);
        try (Transaction transaction = Transaction.openOuter()) {
            long amountInserted = _inLavaTank.insert(lava, mbToInsert, transaction);
            LavaMania.LOGGER.info(amountInserted +" : "+ BUCKET + " mb of lava inserted");
            if ( amountInserted == mbToInsert) {
                transaction.commit();
                return TransactionContext.Result.COMMITTED;
            }

        }
            return TransactionContext.Result.ABORTED;

    }

    public TransactionContext.Result BurnLava()
    {
        try(Transaction transaction = Transaction.openOuter())
        {
            ElectricGenerator owner = (ElectricGenerator) _ownerBlock;
            if(_inLavaTank.amount >= 125)
            {

                long amount_extracted = _inLavaTank.extract(_inLavaTank.variant,125,transaction);
                if(amount_extracted ==125)
                {
                    _energyAmount+=10;
                    transaction.commit();
                    if(owner!=null) {
                        owner.LitGenerator();
                        LavaMania.LOGGER.error("OWNER SET");
                    }
                    return TransactionContext.Result.COMMITTED;
                }
            }
            else
            {
                if(owner!=null)
                owner.UnlitGenerator(level,getBlockPos());
            }
            return TransactionContext.Result.ABORTED;
        }

    }

    @Override
    protected void saveAdditional(ValueOutput writeView) {
        writeView.putInt("clicks", clicks);
        writeView.putInt("lavaAmount",_outLavaMbAmount);
        super.saveAdditional(writeView);
    }
    @Override
    protected void loadAdditional(ValueInput readView) {
        super.loadAdditional(readView);

        clicks = readView.getIntOr("clicks", 0);
        _outLavaMbAmount = readView.getIntOr("lavaAmount",_outLavaMbAmount);
    }
    public static void tick(Level world, BlockPos blockPos, BlockState blockState, ElectricGeneratorEntity entity) {
        if(world.isClientSide())
        {
            return;
        }
        entity._ticksSinceLast++;
        if(entity._ticksSinceLast >= 20)
        {
            if(entity.BurnLava() == TransactionContext.Result.COMMITTED)
            {
                blockState.setValue(ElectricGenerator.LIT,true);
            }
            LavaMania.LOGGER.error("BURN");
            entity._ticksSinceLast =0;
        }
        for(Direction direction : Direction.values())
        {
            if (world.getBlockEntity(blockPos.offset(direction.getUnitVec3i())) instanceof IEnergyStorage entity1)
            {
                entity.PushEnergy(entity1,10);
            }
        }
    }

    @Override
    public void PushEnergy(IEnergyStorage destination,int EnergyAmount)
    {
        if(destination._isStorageOnly)
        {
            destination.ReceiveEnergy(EnergyAmount);
            _energyAmount -= EnergyAmount;
        }
    }

    @Override
    public int GetStoredEnergyAmount() {
        return _energyAmount;
    }

    @Override
    public void ReceiveEnergy(int energy_amount)
    {
        _energyAmount+=energy_amount;
    }

    public void DisplayEnergyAmount()
    {
        LavaMania.LOGGER.info(this._energyAmount +" : energy stored");
    }

}