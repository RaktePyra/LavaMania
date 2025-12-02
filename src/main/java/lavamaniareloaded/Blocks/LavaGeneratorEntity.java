package lavamaniareloaded.Blocks;

import lavamaniareloaded.AddBlockMod;
import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
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
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_COBBLE = 1;


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

        entity.ticksSinceLast++;
        if(entity.ticksSinceLast > 200)
        {
            entity.ticksSinceLast = 0;
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

    public void removeItem(int slot, int amount) {
        ItemStack stack = items.get(slot);
        if (!stack.isEmpty()) {
            stack.shrink(amount);
            if (stack.getCount() <= 0) {
                items.set(slot, ItemStack.EMPTY);
            }
            setChanged();
        }
    }

}
