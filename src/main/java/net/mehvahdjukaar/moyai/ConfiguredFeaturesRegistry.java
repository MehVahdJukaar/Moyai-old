package net.mehvahdjukaar.moyai;

import net.minecraft.core.*;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ConfiguredFeaturesRegistry {

    //helper
    private static RandomPatchConfiguration makeRandomPatch(int tries, int xzSpread, int ySpread, ConfiguredFeature<?, ?> feature, BlockPredicate placementRule) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, PlacementUtils.inlinePlaced(Holder.direct(feature),
                BlockPredicateFilter.forPredicate(placementRule)));
    }


    private static Holder<PlacedFeature> makeSimpleMoyaiFeature(Direction dir) {
        return PlacementUtils.filtered(
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(SimpleStateProvider.simple(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, dir))),
                BlockPredicate.wouldSurvive(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, dir), Vec3i.ZERO));
    }

    private static final BlockPredicate BEACH_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.matchesTag(BlockTags.SAND, BlockPos.ZERO.below())
    );


    private static final BlockPredicate MUSHROOM_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.anyOf(
                    BlockPredicate.matchesTag(BlockTags.SAND, BlockPos.ZERO.below()),
                    BlockPredicate.matchesBlock(Blocks.MYCELIUM, BlockPos.ZERO.below())
            )
    );


    private static final BlockStateProvider MOYAI_PROVIDER = new WeightedStateProvider(
            new SimpleWeightedRandomList.Builder<BlockState>()
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.MODE, MoyaiBlock.RotationMode.STATIC)
                            .setValue(MoyaiBlock.FACING, Direction.NORTH), 1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.MODE, MoyaiBlock.RotationMode.STATIC)
                            .setValue(MoyaiBlock.FACING, Direction.SOUTH), 1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.MODE, MoyaiBlock.RotationMode.STATIC)
                            .setValue(MoyaiBlock.FACING, Direction.EAST), 1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.MODE, MoyaiBlock.RotationMode.STATIC)
                            .setValue(MoyaiBlock.FACING, Direction.WEST), 1));


    private static final ConfiguredFeature<SimpleRandomFeatureConfiguration, ?> MOYAI_FEATURE = new ConfiguredFeature<>(Feature.SIMPLE_RANDOM_SELECTOR,
            new SimpleRandomFeatureConfiguration(HolderSet.direct(
                    makeSimpleMoyaiFeature(Direction.NORTH),
                    makeSimpleMoyaiFeature(Direction.SOUTH),
                    makeSimpleMoyaiFeature(Direction.EAST),
                    makeSimpleMoyaiFeature(Direction.WEST))));

    //configured features


    //placed features


    public static final ResourceKey<PlacedFeature> BEACH_MOYAI_KEY = ResourceKey.create(BuiltinRegistries.PLACED_FEATURE.key(),
            Moyai.res("beach_moyai"));
    public static final ResourceKey<PlacedFeature> MUSHROOM_MOYAI_KEY = ResourceKey.create(BuiltinRegistries.PLACED_FEATURE.key(),
            Moyai.res("mushroom_moyai"));

    protected static void registerFeatures(FMLCommonSetupEvent event) {
    }

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> MUSHROOM_MOYAI_PATCH = FeatureUtils.register("moyai:mushroom_moyai", Feature.RANDOM_PATCH,
            makeRandomPatch(40, 9, 3,
                    MOYAI_FEATURE,
                    MUSHROOM_PLACEMENT));

    public static Holder<ConfiguredFeature<RandomPatchConfiguration, ?>> BEACH_MOYAI_PATCH = FeatureUtils.register("moyai:beach_moyai", Feature.RANDOM_PATCH, makeRandomPatch(35, 10, 3,
            MOYAI_FEATURE,
            BEACH_PLACEMENT));

    public static Holder<PlacedFeature> BEACH_MOYAI = PlacementUtils.register("moyai:beach_moyai", BEACH_MOYAI_PATCH,
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            RarityFilter.onAverageOnceEvery(45),
            InSquarePlacement.spread(),
            BiomeFilter.biome());

    public static Holder<PlacedFeature> MUSHROOM_MOYAI = PlacementUtils.register("moyai:mushroom_moyai", MUSHROOM_MOYAI_PATCH,
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(63 + 64), VerticalAnchor.aboveBottom(64 + 64 + 9)),
            RarityFilter.onAverageOnceEvery(6),
            InSquarePlacement.spread(),
            BiomeFilter.biome());


}
