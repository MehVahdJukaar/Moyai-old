package net.mehvahdjukaar.moyai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    public static final RegistryObject<SoundEvent> MOYAI_BOOM_SOUND = SOUNDS.register("moyai_boom", () -> new SoundEvent(res("record.moyai_boom")));
    public static final RegistryObject<SoundEvent> MOYAI_ROTATE = SOUNDS.register("moyai_rotate", () -> new SoundEvent(res("block.moyai_rotate")));
    public static final RegistryObject<Block> MOYAI = BLOCKS.register("moyai", MoyaiBlock::new);
    public static final RegistryObject<BlockItem> MOYAI_ITEM = ITEMS.register("moyai", () ->
            new BlockItem(MOYAI.get(), (new Item.Properties()).rarity(Rarity.RARE).tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static GameEvent MOYAI_BOOM_EVENT = null;

    public Moyai() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        SOUNDS.register(bus);
        bus.addListener(ConfiguredFeaturesRegistry::registerFeatures);
        bus.addListener(Moyai::init);
        bus.addGenericListener(Item.class, Moyai::registerAdditionalStuff);

        MinecraftForge.EVENT_BUS.register(this);

    }

    public static void registerAdditionalStuff(final RegistryEvent.Register<Item> event) {
        MOYAI_BOOM_EVENT = Registry.register(Registry.GAME_EVENT, res("moyai_boom"), new GameEvent("moyai_boom", 16));
    }

    public static void init(final FMLCommonSetupEvent event) {
        Item i = ForgeRegistries.ITEMS.getValue(new ResourceLocation("supplementaries:soap"));
        if (i != Items.AIR && i != null) {
            DispenserBlock.registerBehavior(i, new DefaultDispenseItemBehavior() {
                @Override
                protected ItemStack execute(BlockSource source, ItemStack stack) {
                    BlockPos pos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                    BlockState state = source.getLevel().getBlockState(pos);
                    if (state.is(MOYAI.get())) {
                        if (MoyaiBlock.maybeEatSoap(stack, state, pos, source.getLevel(), null)) {
                            return stack;
                        }
                    }
                    return super.execute(source, stack);
                }
            });
        }
    }


    @SubscribeEvent
    public void addStuffToBiomes(BiomeLoadingEvent event) {

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

    @SubscribeEvent
    public void onNotePlayed(NoteBlockEvent.Play event) {
        if (event.getInstrument() == NoteBlockInstrument.BASEDRUM) {
            LevelAccessor level = event.getWorld();
            BlockPos pos = event.getPos();
            BlockState below = level.getBlockState(pos.below());
            if (below.getBlock() instanceof MoyaiBlock && level instanceof ServerLevel serverLevel) {
                level.gameEvent(MOYAI_BOOM_EVENT, pos);
                event.setCanceled(true);
                int i = event.getState().getValue(NoteBlock.NOTE);
                float f = (float) Math.pow(2.0D, (double) (i - 12) / 12.0D);
                level.playSound(null, pos, MOYAI_BOOM_SOUND.get(), SoundSource.RECORDS, 0.5F, f);
                //  serverLevel.sendParticles(ParticleTypes.NOTE, (double) pos.getX() + 0.5D, (double) pos.getY() + 1.2D, (double) pos.getZ() + 0.5D, 1, 0.0D, 0.0D,0,(double) i / 24.0D);
                serverLevel.blockEvent(pos.below(), below.getBlock(), 0, i);
            }
        }
    }


}