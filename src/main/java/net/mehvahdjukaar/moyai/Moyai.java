package net.mehvahdjukaar.moyai;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

/**
 * Author: MehVahdJukaar
 */
@Mod(Moyai.MOD_ID)
public class Moyai {
    public static final String MOD_ID = "moyai";

    public static ResourceLocation res(String name) {
        return new ResourceLocation(MOD_ID, name);
    }


    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);


    public static final RegistryObject<Block> MOYAI = BLOCKS.register("moyai", MoyaiBlock::new);
    public static final RegistryObject<BlockItem> MOYAI_ITEM = ITEMS.register("moyai", () ->
            new BlockItem(MOYAI.get(), (new Item.Properties()).rarity(Rarity.RARE).tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public Moyai() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        bus.addListener(ConfiguredFeaturesRegistry::registerFeatures);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, Moyai::addStuffToBiomes);
    }

    public static void addStuffToBiomes(BiomeLoadingEvent event) {

        Biome.BiomeCategory category = event.getCategory();
        if (category != Biome.BiomeCategory.NETHER && category != Biome.BiomeCategory.THEEND && category != Biome.BiomeCategory.NONE) {


            ResourceLocation res = event.getName();
            if (res != null && category != Biome.BiomeCategory.UNDERGROUND) {

                ResourceKey<Biome> key = ResourceKey.create(ForgeRegistries.Keys.BIOMES, res);
                Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);
                if (!types.contains(BiomeDictionary.Type.SNOWY)) {
                    if (category == Biome.BiomeCategory.MUSHROOM) {
                        event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                                ConfiguredFeaturesRegistry.MUSHROOM_MOYAI);
                    } else if (category == Biome.BiomeCategory.BEACH) {
                        event.getGeneration().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION,
                                ConfiguredFeaturesRegistry.BEACH_MOYAI);
                    }
                }
            }
        }
    }

}