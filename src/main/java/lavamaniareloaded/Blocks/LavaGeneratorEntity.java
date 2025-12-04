package lavamaniareloaded.Blocks;

import lavamaniareloaded.IEnergyStorage;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;


public class LavaGeneratorEntity extends BlockEntity implements IEnergyStorage
{
    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int EnergyReceive = 0;
    public boolean _isStorageOnly =  true;
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_COBBLE = 1;
    public static final int SLOT_LAVA = 2;

    public LavaGeneratorEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntity.LAVA_GENERATOR_ENTITY, pos, state);
    }

    public SingleVariantStorage<FluidVariant> LavaTankOutput = new SingleVariantStorage<>()
    {
        @Override
        protected FluidVariant getBlankVariant()
        {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant fluidVariant)
        {
            return  (8 * FluidConstants.BUCKET) / 81;
        }
    };

    private int clicks = 0;
    public int liquidlava = 0;
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
        ContainerHelper.saveAllItems(writeView, items);
        writeView.putInt("clicks", clicks);
        writeView.putInt("ticksSinceLast", ticksSinceLast);

        super.saveAdditional(writeView);
    }
    private int ticksSinceLast = 0;
    @Override
    protected void loadAdditional(ValueInput readView) {
        super.loadAdditional(readView);

        clicks = readView.getIntOr("clicks", 0);
        ContainerHelper.loadAllItems(readView, items);
        ticksSinceLast = readView.getIntOr("ticksSinceLast",0);
    }

    public static void tick(Level world, BlockPos blockPos, BlockState blockState, LavaGeneratorEntity entity)
    {
        if (world.isClientSide()) {
            return; // ne rien faire côté client
        }
        ItemStack fuel = entity.getStack(LavaGeneratorEntity.SLOT_FUEL);
        ItemStack cobble = entity.getStack(LavaGeneratorEntity.SLOT_COBBLE);
        ItemStack output = entity.getStack(LavaGeneratorEntity.SLOT_LAVA);

        if(!fuel.isEmpty() && fuel.is(Items.COAL) && !cobble.isEmpty() && cobble.is(Items.COBBLESTONE))
        {
            entity.ticksSinceLast--;
            if (entity.ticksSinceLast <= 0 && entity.liquidlava < 8)
            {
                fuel.shrink(1);
                cobble.shrink(1);

                try (var transaction = net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter())
                {
                    long inserted = entity.LavaTankOutput.insert(
                            FluidVariant.of(net.minecraft.world.level.material.Fluids.LAVA),
                            1000, // 1000 mB
                            transaction
                    );
                    if (inserted == 1000)
                    {
                        transaction.commit();
                        entity.liquidlava++;

                        System.out.println("Transformation : charbon + cobble → 1000 mB de lave !");
                    } else
                    {
                        transaction.abort();
                        System.out.println("Tank plein, impossible d’ajouter de la lave.");
                    }
                }
                entity.setChanged();
                entity.ticksSinceLast = 200;
            }
            System.out.println(entity.ticksSinceLast);
        }
        else if(entity.EnergyReceive >= 20 && !cobble.isEmpty() && cobble.is(Items.COBBLESTONE))
        {
            entity.ticksSinceLast--;
            if (entity.ticksSinceLast <= 0 && entity.liquidlava < 8) {
                cobble.shrink(1);

                try (var transaction = net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
                    long inserted = entity.LavaTankOutput.insert(
                            FluidVariant.of(net.minecraft.world.level.material.Fluids.LAVA),
                            1000, // 1000 mB
                            transaction
                    );
                    if (inserted == 1000) {
                        transaction.commit();
                        entity.liquidlava++;
                        entity.EnergyReceive = entity.EnergyReceive - 20;
                        System.out.println("Transformation : charbon + cobble → 1000 mB de lave !");
                    } else {
                        transaction.abort();
                        System.out.println("Tank plein, impossible d’ajouter de la lave.");
                    }
                }
                entity.setChanged();
                entity.ticksSinceLast = 200;
            }
        }
    }

    public ItemStack getStack(int slot)
    {
        return items.get(slot);
    }

    public void setStack(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < items.size()) {
            items.set(slot, stack);
            setChanged(); // informe le moteur que l’état a changé
        }
    }

    @Override
    public void PushEnergy(IEnergyStorage destination, int EnergyAmount)
    {

    }

    @Override
    public void ReceiveEnergy(int energy_amount)
    {
        EnergyReceive += energy_amount;
    }

    @Override
    public int GetStoredEnergyAmount()
    {
        return EnergyReceive;
    }

    @Override
    public boolean GetIsStorageOnly()
    {
        return _isStorageOnly;
    }
}
