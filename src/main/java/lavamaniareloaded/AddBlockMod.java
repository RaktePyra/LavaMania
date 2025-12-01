package lavamaniareloaded;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class AddBlockMod
{
    public static Block register(String name, Function<BlockBehaviour.Properties, Block> BlockFactory, BlockBehaviour.Properties settings, boolean shouldRegisterItem)
    {
        ResourceKey<Block> _blockKey = keyOfBlock(name);
        Block _block = BlockFactory.apply(settings.setId(_blockKey));
        if (shouldRegisterItem) {
            ResourceKey<Item> itemKey = keyOfItem(name);
            BlockItem blockItem = new BlockItem(_block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix());
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, _blockKey, _block);
    }

    private static ResourceKey<Block> keyOfBlock(String name) {
        return ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(LavaMania.MOD_ID, name));
    }

    private static ResourceKey<Item> keyOfItem(String name) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(LavaMania.MOD_ID, name));
    }

    public static final Block lava_generator = register(
            "lava_generator",
            Block::new,
            BlockBehaviour.Properties.of().sound(SoundType.STONE),
            true
    );

    public static void Initialise()
    {

    }
}
