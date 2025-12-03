package lavamaniareloaded.Blocks;

import lavamaniareloaded.ModBlockEntity;
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


public class LavaGeneratorEntity extends BlockEntity
{
    private final NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_COBBLE = 1;
    public static final int SLOT_LAVA = 2;

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

        entity.ticksSinceLast--;
        if(entity.ticksSinceLast <= 0)
        {
            if (!fuel.isEmpty() && fuel.is(Items.COAL) && !cobble.isEmpty() && cobble.is(Items.COBBLESTONE))
            {
                fuel.shrink(1);
                cobble.shrink(1);

                if (output == ItemStack.EMPTY)
                {
                    entity.setStack(LavaGeneratorEntity.SLOT_LAVA, new ItemStack(Items.LAVA_BUCKET));
                    output = entity.getStack(LavaGeneratorEntity.SLOT_LAVA);
                }
                else if (output.is(Items.LAVA_BUCKET))
                {
                    // Si déjà de la lave, on peut stacker (selon ton design)
                    output.grow(1);
                }
                entity.setChanged();
                System.out.println("Transformation : charbon + cobble → lave !");
            }
            entity.ticksSinceLast = 200;
        }
        System.out.println(entity.ticksSinceLast);
    }

    public ItemStack getItem(int slot)
    {
        return items.get(slot);
    }

    public void setItem(int slot, ItemStack stack)
    {
        items.set(slot, stack);
        setChanged(); // indique que l’état a changé
    }

    public void removeItem(int slot, int amount)
    {
        ItemStack stack = items.get(slot);
        if (!stack.isEmpty()) {
            stack.shrink(amount);
            if (stack.getCount() <= 0) {
                items.set(slot, ItemStack.EMPTY);
            }
            setChanged();
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
}
