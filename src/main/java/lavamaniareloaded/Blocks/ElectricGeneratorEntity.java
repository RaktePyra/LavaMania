package lavamaniareloaded.Blocks;

import lavamaniareloaded.ModBlockEntity;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import static net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;

public class ElectricGeneratorEntity extends BlockEntity {
    public ElectricGeneratorEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntity.ELECTRIC_GENERATOR_ENTITY, pos, state);
    }
    private Storage<FluidVariant> _outLavaTank;
    private Storage<FluidVariant> _inLavaTank;
    private int clicks = 0;
    private int _outLavaMbAmount = 0;
    public int getClicks() {
        return clicks;
    }

    public void incrementClicks() {
        clicks++;
        setChanged();
    }

    public void GenerateLava() {
        FluidVariant lava = FluidVariant.of(Fluids.LAVA);
// Pour transférer du liquide d'un contenaire à un autre, on effectue une transaction que l'on peut décider d'appliquer ou non en fonction
        //de son résultat.
        try (Transaction transaction = Transaction.openOuter()) {
            if (_inLavaTank.extract(lava, 8100, transaction) == 8100 && _outLavaTank.insert(lava, 10125, transaction) == 10125) {
                transaction.commit();
            }
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
}