package net.mehvahdjukaar.moyai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class MoyaiBlock extends FallingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected MoyaiBlock() {
        super(BlockBehaviour.Properties.copy(Blocks.BASALT).strength(5, 4));
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    private static long LAST_GREETED_TIME = -24000;

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            long time = pLevel.getDayTime();
            if (Math.abs(time - LAST_GREETED_TIME) >= 12000) {
                LAST_GREETED_TIME = time;
                pPlayer.displayClientMessage(new TranslatableComponent("message.moyai.angelo"), true);
            }
            // return InteractionResult.SUCCESS;
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    //only called by worldgen
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level instanceof WorldGenRegion) {
            //if this is called during world gen
            Direction direction = state.getValue(FACING);
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState s = level.getBlockState(pos.relative(dir));
                if (dir == direction && !s.isAir()) return false;
                else if (s.is(this)) {
                    if (s.getValue(FACING) == dir.getOpposite()) return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getDustColor(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return pState.getMapColor(pReader, pPos).col;
    }

    @Override
    protected void falling(FallingBlockEntity pFallingEntity) {
        pFallingEntity.setHurtsEntities(2.0F, 40);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState p_48795_, BlockState state, FallingBlockEntity blockEntity) {
        if (!blockEntity.isSilent()) {
            level.levelEvent(1045, pos, 0);
        }
    }
}
