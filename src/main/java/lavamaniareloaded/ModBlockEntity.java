package lavamaniareloaded;

import lavamaniareloaded.Blocks.ElectricGeneratorEntity;
import lavamaniareloaded.Blocks.LavaGeneratorEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntity
{
    public static final BlockEntityType<ElectricGeneratorEntity> ELECTRIC_GENERATOR_ENTITY =
            register("electric_generator", ElectricGeneratorEntity::new, AddBlockMod.electric_generator);

    public static final BlockEntityType<LavaGeneratorEntity> LAVA_GENERATOR_ENTITY =
            register("lava_generator", LavaGeneratorEntity::new, AddBlockMod.lava_generator);

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name,
            FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
            Block... blocks
    ) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LavaMania.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static void Initialize()
    {

    }

}
