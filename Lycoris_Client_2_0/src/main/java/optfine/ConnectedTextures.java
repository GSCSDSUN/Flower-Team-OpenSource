/*
 * Decompiled with CFR 0.150.
 */
package optfine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import optfine.Config;
import optfine.ConnectedProperties;
import optfine.RenderEnv;
import optfine.ResourceUtils;

public class ConnectedTextures {
	private static Map[] spriteQuadMaps = null;
	private static ConnectedProperties[][] blockProperties = null;
	private static ConnectedProperties[][] tileProperties = null;
	private static boolean multipass = false;
	private static final int Y_NEG_DOWN = 0;
	private static final int Y_POS_UP = 1;
	private static final int Z_NEG_NORTH = 2;
	private static final int Z_POS_SOUTH = 3;
	private static final int X_NEG_WEST = 4;
	private static final int X_POS_EAST = 5;
	private static final int Y_AXIS = 0;
	private static final int Z_AXIS = 1;
	private static final int X_AXIS = 2;
	private static final String[] propSuffixes = new String[] { "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };
	private static final int[] ctmIndexes = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 0, 0, 0, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 0, 0, 0, 0, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 0, 0, 0,
			36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 0, 0, 0, 0, 0 };
	public static final IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
	private static TextureAtlasSprite emptySprite = null;

	public static synchronized BakedQuad getConnectedTexture(IBlockAccess p_getConnectedTexture_0_,
			IBlockState p_getConnectedTexture_1_, BlockPos p_getConnectedTexture_2_, BakedQuad p_getConnectedTexture_3_,
			RenderEnv p_getConnectedTexture_4_) {
		IBlockState iblockstate;
		TextureAtlasSprite textureatlassprite = p_getConnectedTexture_3_.getSprite();
		if (textureatlassprite == null) {
			return p_getConnectedTexture_3_;
		}
		Block block = p_getConnectedTexture_1_.getBlock();
		EnumFacing enumfacing = p_getConnectedTexture_3_.getFace();
		if (block instanceof BlockPane && textureatlassprite.getIconName().startsWith("minecraft:blocks/glass_pane_top")
				&& (iblockstate = p_getConnectedTexture_0_.getBlockState(p_getConnectedTexture_2_
						.offset(p_getConnectedTexture_3_.getFace()))) == p_getConnectedTexture_1_) {
			return ConnectedTextures.getQuad(emptySprite, block, p_getConnectedTexture_1_, p_getConnectedTexture_3_);
		}
		TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureMultiPass(
				p_getConnectedTexture_0_, p_getConnectedTexture_1_, p_getConnectedTexture_2_, enumfacing,
				textureatlassprite, p_getConnectedTexture_4_);
		return textureatlassprite1 == textureatlassprite ? p_getConnectedTexture_3_
				: ConnectedTextures.getQuad(textureatlassprite1, block, p_getConnectedTexture_1_,
						p_getConnectedTexture_3_);
	}

	private static BakedQuad getQuad(TextureAtlasSprite p_getQuad_0_, Block p_getQuad_1_, IBlockState p_getQuad_2_,
			BakedQuad p_getQuad_3_) {
		if (spriteQuadMaps == null) {
			return p_getQuad_3_;
		} else {
			int i = p_getQuad_0_.getIndexInMap();

			if (i >= 0 && i < spriteQuadMaps.length) {
				Map map = spriteQuadMaps[i];

				if (map == null) {
					map = new IdentityHashMap(1);
					spriteQuadMaps[i] = map;
				}

				BakedQuad bakedquad = (BakedQuad) map.get(p_getQuad_3_);

				if (bakedquad == null) {
					bakedquad = makeSpriteQuad(p_getQuad_3_, p_getQuad_0_);
					map.put(p_getQuad_3_, bakedquad);
				}

				return bakedquad;
			} else {
				return p_getQuad_3_;
			}
		}
	}

	private static BakedQuad makeSpriteQuad(BakedQuad p_makeSpriteQuad_0_, TextureAtlasSprite p_makeSpriteQuad_1_) {
		int[] aint = (int[]) p_makeSpriteQuad_0_.getVertexData().clone();
		TextureAtlasSprite textureatlassprite = p_makeSpriteQuad_0_.getSprite();
		for (int i = 0; i < 4; ++i) {
			ConnectedTextures.fixVertex(aint, i, textureatlassprite, p_makeSpriteQuad_1_);
		}
		BakedQuad bakedquad = new BakedQuad(aint, p_makeSpriteQuad_0_.getTintIndex(), p_makeSpriteQuad_0_.getFace(),
				p_makeSpriteQuad_1_);
		return bakedquad;
	}

	private static void fixVertex(int[] p_fixVertex_0_, int p_fixVertex_1_, TextureAtlasSprite p_fixVertex_2_,
			TextureAtlasSprite p_fixVertex_3_) {
		int i = 7 * p_fixVertex_1_;
		float f = Float.intBitsToFloat(p_fixVertex_0_[i + 4]);
		float f1 = Float.intBitsToFloat(p_fixVertex_0_[i + 4 + 1]);
		double d0 = p_fixVertex_2_.getSpriteU16(f);
		double d1 = p_fixVertex_2_.getSpriteV16(f1);
		p_fixVertex_0_[i + 4] = Float.floatToRawIntBits(p_fixVertex_3_.getInterpolatedU(d0));
		p_fixVertex_0_[i + 4 + 1] = Float.floatToRawIntBits(p_fixVertex_3_.getInterpolatedV(d1));
	}

	private static TextureAtlasSprite getConnectedTextureMultiPass(IBlockAccess p_getConnectedTextureMultiPass_0_,
			IBlockState p_getConnectedTextureMultiPass_1_, BlockPos p_getConnectedTextureMultiPass_2_,
			EnumFacing p_getConnectedTextureMultiPass_3_, TextureAtlasSprite p_getConnectedTextureMultiPass_4_,
			RenderEnv p_getConnectedTextureMultiPass_5_) {
		TextureAtlasSprite textureatlassprite2;
		TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureSingle(
				p_getConnectedTextureMultiPass_0_, p_getConnectedTextureMultiPass_1_, p_getConnectedTextureMultiPass_2_,
				p_getConnectedTextureMultiPass_3_, p_getConnectedTextureMultiPass_4_, true,
				p_getConnectedTextureMultiPass_5_);
		if (!multipass) {
			return textureatlassprite;
		}
		if (textureatlassprite == p_getConnectedTextureMultiPass_4_) {
			return textureatlassprite;
		}
		TextureAtlasSprite textureatlassprite1 = textureatlassprite;
		for (int i = 0; i < 3
				&& (textureatlassprite2 = ConnectedTextures.getConnectedTextureSingle(p_getConnectedTextureMultiPass_0_,
						p_getConnectedTextureMultiPass_1_, p_getConnectedTextureMultiPass_2_,
						p_getConnectedTextureMultiPass_3_, textureatlassprite1, false,
						p_getConnectedTextureMultiPass_5_)) != textureatlassprite1; ++i) {
			textureatlassprite1 = textureatlassprite2;
		}
		return textureatlassprite1;
	}

	public static TextureAtlasSprite getConnectedTextureSingle(IBlockAccess p_getConnectedTextureSingle_0_,
			IBlockState p_getConnectedTextureSingle_1_, BlockPos p_getConnectedTextureSingle_2_,
			EnumFacing p_getConnectedTextureSingle_3_, TextureAtlasSprite p_getConnectedTextureSingle_4_,
			boolean p_getConnectedTextureSingle_5_, RenderEnv p_getConnectedTextureSingle_6_) {
		ConnectedProperties[] aconnectedproperties1;
		int j1;
		ConnectedProperties[] aconnectedproperties;
		int i;
		Block block = p_getConnectedTextureSingle_1_.getBlock();
		if (tileProperties != null && (i = p_getConnectedTextureSingle_4_.getIndexInMap()) >= 0
				&& i < tileProperties.length && (aconnectedproperties = tileProperties[i]) != null) {
			int j = p_getConnectedTextureSingle_6_.getMetadata();
			int k = ConnectedTextures.getSide(p_getConnectedTextureSingle_3_);
			for (int l = 0; l < aconnectedproperties.length; ++l) {
				TextureAtlasSprite textureatlassprite;
				int i1;
				ConnectedProperties connectedproperties = aconnectedproperties[l];
				if (connectedproperties == null
						|| !connectedproperties.matchesBlock(i1 = p_getConnectedTextureSingle_6_.getBlockId())
						|| (textureatlassprite = ConnectedTextures.getConnectedTexture(connectedproperties,
								p_getConnectedTextureSingle_0_, p_getConnectedTextureSingle_1_,
								p_getConnectedTextureSingle_2_, k, p_getConnectedTextureSingle_4_, j,
								p_getConnectedTextureSingle_6_)) == null)
					continue;
				return textureatlassprite;
			}
		}
		if (blockProperties != null && p_getConnectedTextureSingle_5_
				&& (j1 = p_getConnectedTextureSingle_6_.getBlockId()) >= 0 && j1 < blockProperties.length
				&& (aconnectedproperties1 = blockProperties[j1]) != null) {
			int k1 = p_getConnectedTextureSingle_6_.getMetadata();
			int l1 = ConnectedTextures.getSide(p_getConnectedTextureSingle_3_);
			for (int i2 = 0; i2 < aconnectedproperties1.length; ++i2) {
				TextureAtlasSprite textureatlassprite1;
				ConnectedProperties connectedproperties1 = aconnectedproperties1[i2];
				if (connectedproperties1 == null || !connectedproperties1.matchesIcon(p_getConnectedTextureSingle_4_)
						|| (textureatlassprite1 = ConnectedTextures.getConnectedTexture(connectedproperties1,
								p_getConnectedTextureSingle_0_, p_getConnectedTextureSingle_1_,
								p_getConnectedTextureSingle_2_, l1, p_getConnectedTextureSingle_4_, k1,
								p_getConnectedTextureSingle_6_)) == null)
					continue;
				return textureatlassprite1;
			}
		}
		return p_getConnectedTextureSingle_4_;
	}

	public static int getSide(EnumFacing p_getSide_0_) {
		if (p_getSide_0_ == null) {
			return -1;
		}
		switch (p_getSide_0_) {
		case DOWN: {
			return 0;
		}
		case UP: {
			return 1;
		}
		case EAST: {
			return 5;
		}
		case WEST: {
			return 4;
		}
		case NORTH: {
			return 2;
		}
		case SOUTH: {
			return 3;
		}
		}
		return -1;
	}

	private static EnumFacing getFacing(int p_getFacing_0_) {
		switch (p_getFacing_0_) {
		case 0: {
			return EnumFacing.DOWN;
		}
		case 1: {
			return EnumFacing.UP;
		}
		case 2: {
			return EnumFacing.NORTH;
		}
		case 3: {
			return EnumFacing.SOUTH;
		}
		case 4: {
			return EnumFacing.WEST;
		}
		case 5: {
			return EnumFacing.EAST;
		}
		}
		return EnumFacing.UP;
	}

	private static TextureAtlasSprite getConnectedTexture(ConnectedProperties p_getConnectedTexture_0_,
			IBlockAccess p_getConnectedTexture_1_, IBlockState p_getConnectedTexture_2_,
			BlockPos p_getConnectedTexture_3_, int p_getConnectedTexture_4_,
			TextureAtlasSprite p_getConnectedTexture_5_, int p_getConnectedTexture_6_,
			RenderEnv p_getConnectedTexture_7_) {
		int i = p_getConnectedTexture_3_.getY();
		if (i >= p_getConnectedTexture_0_.minHeight && i <= p_getConnectedTexture_0_.maxHeight) {
			if (p_getConnectedTexture_0_.biomes != null) {
				BiomeGenBase biomegenbase = p_getConnectedTexture_1_.getBiomeGenForCoords(p_getConnectedTexture_3_);
				boolean flag = false;
				for (int j = 0; j < p_getConnectedTexture_0_.biomes.length; ++j) {
					BiomeGenBase biomegenbase1 = p_getConnectedTexture_0_.biomes[j];
					if (biomegenbase != biomegenbase1)
						continue;
					flag = true;
					break;
				}
				if (!flag) {
					return null;
				}
			}
			int l = 0;
			int i1 = p_getConnectedTexture_6_;
			Block block = p_getConnectedTexture_2_.getBlock();
			if (block instanceof BlockRotatedPillar) {
				l = ConnectedTextures.getWoodAxis(p_getConnectedTexture_4_, p_getConnectedTexture_6_);
				i1 = p_getConnectedTexture_6_ & 3;
			}
			if (block instanceof BlockQuartz) {
				l = ConnectedTextures.getQuartzAxis(p_getConnectedTexture_4_, p_getConnectedTexture_6_);
				if (i1 > 2) {
					i1 = 2;
				}
			}
			if (p_getConnectedTexture_4_ >= 0 && p_getConnectedTexture_0_.faces != 63) {
				int j1 = p_getConnectedTexture_4_;
				if (l != 0) {
					j1 = ConnectedTextures.fixSideByAxis(p_getConnectedTexture_4_, l);
				}
				if ((1 << j1 & p_getConnectedTexture_0_.faces) == 0) {
					return null;
				}
			}
			if (p_getConnectedTexture_0_.metadatas != null) {
				int[] aint = p_getConnectedTexture_0_.metadatas;
				boolean flag1 = false;
				for (int k = 0; k < aint.length; ++k) {
					if (aint[k] != i1)
						continue;
					flag1 = true;
					break;
				}
				if (!flag1) {
					return null;
				}
			}
			switch (p_getConnectedTexture_0_.method) {
			case 1: {
				return ConnectedTextures.getConnectedTextureCtm(p_getConnectedTexture_0_, p_getConnectedTexture_1_,
						p_getConnectedTexture_2_, p_getConnectedTexture_3_, p_getConnectedTexture_4_,
						p_getConnectedTexture_5_, p_getConnectedTexture_6_, p_getConnectedTexture_7_);
			}
			case 2: {
				return ConnectedTextures.getConnectedTextureHorizontal(p_getConnectedTexture_0_,
						p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, l,
						p_getConnectedTexture_4_, p_getConnectedTexture_5_, p_getConnectedTexture_6_);
			}
			case 3: {
				return ConnectedTextures.getConnectedTextureTop(p_getConnectedTexture_0_, p_getConnectedTexture_1_,
						p_getConnectedTexture_2_, p_getConnectedTexture_3_, l, p_getConnectedTexture_4_,
						p_getConnectedTexture_5_, p_getConnectedTexture_6_);
			}
			case 4: {
				return ConnectedTextures.getConnectedTextureRandom(p_getConnectedTexture_0_, p_getConnectedTexture_3_,
						p_getConnectedTexture_4_);
			}
			case 5: {
				return ConnectedTextures.getConnectedTextureRepeat(p_getConnectedTexture_0_, p_getConnectedTexture_3_,
						p_getConnectedTexture_4_);
			}
			case 6: {
				return ConnectedTextures.getConnectedTextureVertical(p_getConnectedTexture_0_, p_getConnectedTexture_1_,
						p_getConnectedTexture_2_, p_getConnectedTexture_3_, l, p_getConnectedTexture_4_,
						p_getConnectedTexture_5_, p_getConnectedTexture_6_);
			}
			case 7: {
				return ConnectedTextures.getConnectedTextureFixed(p_getConnectedTexture_0_);
			}
			case 8: {
				return ConnectedTextures.getConnectedTextureHorizontalVertical(p_getConnectedTexture_0_,
						p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, l,
						p_getConnectedTexture_4_, p_getConnectedTexture_5_, p_getConnectedTexture_6_);
			}
			case 9: {
				return ConnectedTextures.getConnectedTextureVerticalHorizontal(p_getConnectedTexture_0_,
						p_getConnectedTexture_1_, p_getConnectedTexture_2_, p_getConnectedTexture_3_, l,
						p_getConnectedTexture_4_, p_getConnectedTexture_5_, p_getConnectedTexture_6_);
			}
			}
			return null;
		}
		return null;
	}

	private static int fixSideByAxis(int p_fixSideByAxis_0_, int p_fixSideByAxis_1_) {
		switch (p_fixSideByAxis_1_) {
		case 0: {
			return p_fixSideByAxis_0_;
		}
		case 1: {
			switch (p_fixSideByAxis_0_) {
			case 0: {
				return 2;
			}
			case 1: {
				return 3;
			}
			case 2: {
				return 1;
			}
			case 3: {
				return 0;
			}
			}
			return p_fixSideByAxis_0_;
		}
		case 2: {
			switch (p_fixSideByAxis_0_) {
			case 0: {
				return 4;
			}
			case 1: {
				return 5;
			}
			default: {
				return p_fixSideByAxis_0_;
			}
			case 4: {
				return 1;
			}
			case 5:
			}
			return 0;
		}
		}
		return p_fixSideByAxis_0_;
	}

	private static int getWoodAxis(int p_getWoodAxis_0_, int p_getWoodAxis_1_) {
		int i = (p_getWoodAxis_1_ & 0xC) >> 2;
		switch (i) {
		case 1: {
			return 2;
		}
		case 2: {
			return 1;
		}
		}
		return 0;
	}

	private static int getQuartzAxis(int p_getQuartzAxis_0_, int p_getQuartzAxis_1_) {
		switch (p_getQuartzAxis_1_) {
		case 3: {
			return 2;
		}
		case 4: {
			return 1;
		}
		}
		return 0;
	}

	private static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties p_getConnectedTextureRandom_0_,
			BlockPos p_getConnectedTextureRandom_1_, int p_getConnectedTextureRandom_2_) {
		if (p_getConnectedTextureRandom_0_.tileIcons.length == 1) {
			return p_getConnectedTextureRandom_0_.tileIcons[0];
		}
		int i = p_getConnectedTextureRandom_2_ / p_getConnectedTextureRandom_0_.symmetry
				* p_getConnectedTextureRandom_0_.symmetry;
		int j = Config.getRandom(p_getConnectedTextureRandom_1_, i) & Integer.MAX_VALUE;
		int k = 0;
		if (p_getConnectedTextureRandom_0_.weights == null) {
			k = j % p_getConnectedTextureRandom_0_.tileIcons.length;
		} else {
			int l = j % p_getConnectedTextureRandom_0_.sumAllWeights;
			int[] aint = p_getConnectedTextureRandom_0_.sumWeights;
			for (int i1 = 0; i1 < aint.length; ++i1) {
				if (l >= aint[i1])
					continue;
				k = i1;
				break;
			}
		}
		return p_getConnectedTextureRandom_0_.tileIcons[k];
	}

	private static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties p_getConnectedTextureFixed_0_) {
		return p_getConnectedTextureFixed_0_.tileIcons[0];
	}

	private static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties p_getConnectedTextureRepeat_0_,
			BlockPos p_getConnectedTextureRepeat_1_, int p_getConnectedTextureRepeat_2_) {
		if (p_getConnectedTextureRepeat_0_.tileIcons.length == 1) {
			return p_getConnectedTextureRepeat_0_.tileIcons[0];
		}
		int i = p_getConnectedTextureRepeat_1_.getX();
		int j = p_getConnectedTextureRepeat_1_.getY();
		int k = p_getConnectedTextureRepeat_1_.getZ();
		int l = 0;
		int i1 = 0;
		switch (p_getConnectedTextureRepeat_2_) {
		case 0: {
			l = i;
			i1 = k;
			break;
		}
		case 1: {
			l = i;
			i1 = k;
			break;
		}
		case 2: {
			l = -i - 1;
			i1 = -j;
			break;
		}
		case 3: {
			l = i;
			i1 = -j;
			break;
		}
		case 4: {
			l = k;
			i1 = -j;
			break;
		}
		case 5: {
			l = -k - 1;
			i1 = -j;
		}
		}
		i1 %= p_getConnectedTextureRepeat_0_.height;
		if ((l %= p_getConnectedTextureRepeat_0_.width) < 0) {
			l += p_getConnectedTextureRepeat_0_.width;
		}
		if (i1 < 0) {
			i1 += p_getConnectedTextureRepeat_0_.height;
		}
		int j1 = i1 * p_getConnectedTextureRepeat_0_.width + l;
		return p_getConnectedTextureRepeat_0_.tileIcons[j1];
	}

	private static TextureAtlasSprite getConnectedTextureCtm(ConnectedProperties p_getConnectedTextureCtm_0_,
			IBlockAccess p_getConnectedTextureCtm_1_, IBlockState p_getConnectedTextureCtm_2_,
			BlockPos p_getConnectedTextureCtm_3_, int p_getConnectedTextureCtm_4_,
			TextureAtlasSprite p_getConnectedTextureCtm_5_, int p_getConnectedTextureCtm_6_,
			RenderEnv p_getConnectedTextureCtm_7_) {
		boolean[] aboolean = p_getConnectedTextureCtm_7_.getBorderFlags();
		switch (p_getConnectedTextureCtm_4_) {
		case 0: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.south(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 1: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.south(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 2: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 3: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 4: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.south(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 5: {
			aboolean[0] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.south(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
		}
		}
		int i = 0;
		if (aboolean[0] & !aboolean[1] & !aboolean[2] & !aboolean[3]) {
			i = 3;
		} else if (!aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
			i = 1;
		} else if (!aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
			i = 12;
		} else if (!aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
			i = 36;
		} else if (aboolean[0] & aboolean[1] & !aboolean[2] & !aboolean[3]) {
			i = 2;
		} else if (!aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
			i = 24;
		} else if (aboolean[0] & !aboolean[1] & aboolean[2] & !aboolean[3]) {
			i = 15;
		} else if (aboolean[0] & !aboolean[1] & !aboolean[2] & aboolean[3]) {
			i = 39;
		} else if (!aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
			i = 13;
		} else if (!aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
			i = 37;
		} else if (!aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
			i = 25;
		} else if (aboolean[0] & !aboolean[1] & aboolean[2] & aboolean[3]) {
			i = 27;
		} else if (aboolean[0] & aboolean[1] & !aboolean[2] & aboolean[3]) {
			i = 38;
		} else if (aboolean[0] & aboolean[1] & aboolean[2] & !aboolean[3]) {
			i = 14;
		} else if (aboolean[0] & aboolean[1] & aboolean[2] & aboolean[3]) {
			i = 26;
		}
		if (i == 0) {
			return p_getConnectedTextureCtm_0_.tileIcons[i];
		}
		if (!Config.isConnectedTexturesFancy()) {
			return p_getConnectedTextureCtm_0_.tileIcons[i];
		}
		switch (p_getConnectedTextureCtm_4_) {
		case 0: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 1: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 2: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 3: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().down(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.east().up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.west().up(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 4: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up().south(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up().north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			break;
		}
		case 5: {
			aboolean[0] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down().north(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[1] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.down().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			aboolean[2] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_, p_getConnectedTextureCtm_1_,
					p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up().north(), p_getConnectedTextureCtm_4_,
					p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
			boolean bl = aboolean[3] = !ConnectedTextures.isNeighbour(p_getConnectedTextureCtm_0_,
					p_getConnectedTextureCtm_1_, p_getConnectedTextureCtm_2_, p_getConnectedTextureCtm_3_.up().south(),
					p_getConnectedTextureCtm_4_, p_getConnectedTextureCtm_5_, p_getConnectedTextureCtm_6_);
		}
		}
		if (i == 13 && aboolean[0]) {
			i = 4;
		} else if (i == 15 && aboolean[1]) {
			i = 5;
		} else if (i == 37 && aboolean[2]) {
			i = 16;
		} else if (i == 39 && aboolean[3]) {
			i = 17;
		} else if (i == 14 && aboolean[0] && aboolean[1]) {
			i = 7;
		} else if (i == 25 && aboolean[0] && aboolean[2]) {
			i = 6;
		} else if (i == 27 && aboolean[3] && aboolean[1]) {
			i = 19;
		} else if (i == 38 && aboolean[3] && aboolean[2]) {
			i = 18;
		} else if (i == 14 && !aboolean[0] && aboolean[1]) {
			i = 31;
		} else if (i == 25 && aboolean[0] && !aboolean[2]) {
			i = 30;
		} else if (i == 27 && !aboolean[3] && aboolean[1]) {
			i = 41;
		} else if (i == 38 && aboolean[3] && !aboolean[2]) {
			i = 40;
		} else if (i == 14 && aboolean[0] && !aboolean[1]) {
			i = 29;
		} else if (i == 25 && !aboolean[0] && aboolean[2]) {
			i = 28;
		} else if (i == 27 && aboolean[3] && !aboolean[1]) {
			i = 43;
		} else if (i == 38 && !aboolean[3] && aboolean[2]) {
			i = 42;
		} else if (i == 26 && aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
			i = 46;
		} else if (i == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && aboolean[3]) {
			i = 9;
		} else if (i == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
			i = 21;
		} else if (i == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
			i = 8;
		} else if (i == 26 && aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
			i = 20;
		} else if (i == 26 && aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
			i = 11;
		} else if (i == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && aboolean[3]) {
			i = 22;
		} else if (i == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && aboolean[3]) {
			i = 23;
		} else if (i == 26 && aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
			i = 10;
		} else if (i == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
			i = 34;
		} else if (i == 26 && !aboolean[0] && aboolean[1] && aboolean[2] && !aboolean[3]) {
			i = 35;
		} else if (i == 26 && aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3]) {
			i = 32;
		} else if (i == 26 && !aboolean[0] && aboolean[1] && !aboolean[2] && !aboolean[3]) {
			i = 33;
		} else if (i == 26 && !aboolean[0] && !aboolean[1] && aboolean[2] && !aboolean[3]) {
			i = 44;
		} else if (i == 26 && !aboolean[0] && !aboolean[1] && !aboolean[2] && aboolean[3]) {
			i = 45;
		}
		return p_getConnectedTextureCtm_0_.tileIcons[i];
	}

	private static boolean isNeighbour(ConnectedProperties p_isNeighbour_0_, IBlockAccess p_isNeighbour_1_,
			IBlockState p_isNeighbour_2_, BlockPos p_isNeighbour_3_, int p_isNeighbour_4_,
			TextureAtlasSprite p_isNeighbour_5_, int p_isNeighbour_6_) {
		IBlockState iblockstate = p_isNeighbour_1_.getBlockState(p_isNeighbour_3_);
		if (p_isNeighbour_2_ == iblockstate) {
			return true;
		}
		if (p_isNeighbour_0_.connect == 2) {
			if (iblockstate == null) {
				return false;
			}
			if (iblockstate == AIR_DEFAULT_STATE) {
				return false;
			}
			TextureAtlasSprite textureatlassprite = ConnectedTextures.getNeighbourIcon(p_isNeighbour_1_,
					p_isNeighbour_3_, iblockstate, p_isNeighbour_4_);
			return textureatlassprite == p_isNeighbour_5_;
		}
		return p_isNeighbour_0_.connect == 3
				? (iblockstate == null ? false
						: (iblockstate == AIR_DEFAULT_STATE ? false
								: iblockstate.getBlock().getMaterial() == p_isNeighbour_2_.getBlock().getMaterial()))
				: false;
	}

	private static TextureAtlasSprite getNeighbourIcon(IBlockAccess p_getNeighbourIcon_0_,
			BlockPos p_getNeighbourIcon_1_, IBlockState p_getNeighbourIcon_2_, int p_getNeighbourIcon_3_) {
		p_getNeighbourIcon_2_ = p_getNeighbourIcon_2_.getBlock().getActualState(p_getNeighbourIcon_2_,
				p_getNeighbourIcon_0_, p_getNeighbourIcon_1_);
		IBakedModel ibakedmodel = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes()
				.getModelForState(p_getNeighbourIcon_2_);
		if (ibakedmodel == null) {
			return null;
		}
		EnumFacing enumfacing = ConnectedTextures.getFacing(p_getNeighbourIcon_3_);
		List<BakedQuad> list = ibakedmodel.getFaceQuads(enumfacing);
		if (list.size() > 0) {
			BakedQuad bakedquad1 = list.get(0);
			return bakedquad1.getSprite();
		}
		List<BakedQuad> list1 = ibakedmodel.getGeneralQuads();
		for (int i = 0; i < list1.size(); ++i) {
			BakedQuad bakedquad = list1.get(i);
			if (bakedquad.getFace() != enumfacing)
				continue;
			return bakedquad.getSprite();
		}
		return null;
	}

	private static TextureAtlasSprite getConnectedTextureHorizontal(
			ConnectedProperties p_getConnectedTextureHorizontal_0_, IBlockAccess p_getConnectedTextureHorizontal_1_,
			IBlockState p_getConnectedTextureHorizontal_2_, BlockPos p_getConnectedTextureHorizontal_3_,
			int p_getConnectedTextureHorizontal_4_, int p_getConnectedTextureHorizontal_5_,
			TextureAtlasSprite p_getConnectedTextureHorizontal_6_, int p_getConnectedTextureHorizontal_7_) {
		boolean flag = false;
		boolean flag1 = false;
		block0: switch (p_getConnectedTextureHorizontal_4_) {
		case 0: {
			switch (p_getConnectedTextureHorizontal_5_) {
			case 0:
			case 1: {
				return null;
			}
			case 2: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 3: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 4: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 5: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
			}
			}
			break;
		}
		case 1: {
			switch (p_getConnectedTextureHorizontal_5_) {
			case 0: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 1: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.west(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.east(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 2:
			case 3: {
				return null;
			}
			case 4: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 5: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
			}
			}
			break;
		}
		case 2: {
			switch (p_getConnectedTextureHorizontal_5_) {
			case 0: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 1: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.north(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.south(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 2: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 3: {
				flag = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.up(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureHorizontal_0_,
						p_getConnectedTextureHorizontal_1_, p_getConnectedTextureHorizontal_2_,
						p_getConnectedTextureHorizontal_3_.down(), p_getConnectedTextureHorizontal_5_,
						p_getConnectedTextureHorizontal_6_, p_getConnectedTextureHorizontal_7_);
				break block0;
			}
			case 4:
			case 5: {
				return null;
			}
			}
		}
		}
		int i = 3;
		i = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
		return p_getConnectedTextureHorizontal_0_.tileIcons[i];
	}

	private static TextureAtlasSprite getConnectedTextureVertical(ConnectedProperties p_getConnectedTextureVertical_0_,
			IBlockAccess p_getConnectedTextureVertical_1_, IBlockState p_getConnectedTextureVertical_2_,
			BlockPos p_getConnectedTextureVertical_3_, int p_getConnectedTextureVertical_4_,
			int p_getConnectedTextureVertical_5_, TextureAtlasSprite p_getConnectedTextureVertical_6_,
			int p_getConnectedTextureVertical_7_) {
		boolean flag = false;
		boolean flag1 = false;
		switch (p_getConnectedTextureVertical_4_) {
		case 0: {
			if (p_getConnectedTextureVertical_5_ == 1 || p_getConnectedTextureVertical_5_ == 0) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.down(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
			flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.up(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
			break;
		}
		case 1: {
			if (p_getConnectedTextureVertical_5_ == 3 || p_getConnectedTextureVertical_5_ == 2) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.south(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
			flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.north(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
			break;
		}
		case 2: {
			if (p_getConnectedTextureVertical_5_ == 5 || p_getConnectedTextureVertical_5_ == 4) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.west(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
			flag1 = ConnectedTextures.isNeighbour(p_getConnectedTextureVertical_0_, p_getConnectedTextureVertical_1_,
					p_getConnectedTextureVertical_2_, p_getConnectedTextureVertical_3_.east(),
					p_getConnectedTextureVertical_5_, p_getConnectedTextureVertical_6_,
					p_getConnectedTextureVertical_7_);
		}
		}
		int i = 3;
		i = flag ? (flag1 ? 1 : 2) : (flag1 ? 0 : 3);
		return p_getConnectedTextureVertical_0_.tileIcons[i];
	}

	private static TextureAtlasSprite getConnectedTextureHorizontalVertical(
			ConnectedProperties p_getConnectedTextureHorizontalVertical_0_,
			IBlockAccess p_getConnectedTextureHorizontalVertical_1_,
			IBlockState p_getConnectedTextureHorizontalVertical_2_, BlockPos p_getConnectedTextureHorizontalVertical_3_,
			int p_getConnectedTextureHorizontalVertical_4_, int p_getConnectedTextureHorizontalVertical_5_,
			TextureAtlasSprite p_getConnectedTextureHorizontalVertical_6_,
			int p_getConnectedTextureHorizontalVertical_7_) {
		TextureAtlasSprite[] atextureatlassprite = p_getConnectedTextureHorizontalVertical_0_.tileIcons;
		TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureHorizontal(
				p_getConnectedTextureHorizontalVertical_0_, p_getConnectedTextureHorizontalVertical_1_,
				p_getConnectedTextureHorizontalVertical_2_, p_getConnectedTextureHorizontalVertical_3_,
				p_getConnectedTextureHorizontalVertical_4_, p_getConnectedTextureHorizontalVertical_5_,
				p_getConnectedTextureHorizontalVertical_6_, p_getConnectedTextureHorizontalVertical_7_);
		if (textureatlassprite != null && textureatlassprite != p_getConnectedTextureHorizontalVertical_6_
				&& textureatlassprite != atextureatlassprite[3]) {
			return textureatlassprite;
		}
		TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureVertical(
				p_getConnectedTextureHorizontalVertical_0_, p_getConnectedTextureHorizontalVertical_1_,
				p_getConnectedTextureHorizontalVertical_2_, p_getConnectedTextureHorizontalVertical_3_,
				p_getConnectedTextureHorizontalVertical_4_, p_getConnectedTextureHorizontalVertical_5_,
				p_getConnectedTextureHorizontalVertical_6_, p_getConnectedTextureHorizontalVertical_7_);
		return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4]
				: (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5]
						: (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6]
								: textureatlassprite1));
	}

	private static TextureAtlasSprite getConnectedTextureVerticalHorizontal(
			ConnectedProperties p_getConnectedTextureVerticalHorizontal_0_,
			IBlockAccess p_getConnectedTextureVerticalHorizontal_1_,
			IBlockState p_getConnectedTextureVerticalHorizontal_2_, BlockPos p_getConnectedTextureVerticalHorizontal_3_,
			int p_getConnectedTextureVerticalHorizontal_4_, int p_getConnectedTextureVerticalHorizontal_5_,
			TextureAtlasSprite p_getConnectedTextureVerticalHorizontal_6_,
			int p_getConnectedTextureVerticalHorizontal_7_) {
		TextureAtlasSprite[] atextureatlassprite = p_getConnectedTextureVerticalHorizontal_0_.tileIcons;
		TextureAtlasSprite textureatlassprite = ConnectedTextures.getConnectedTextureVertical(
				p_getConnectedTextureVerticalHorizontal_0_, p_getConnectedTextureVerticalHorizontal_1_,
				p_getConnectedTextureVerticalHorizontal_2_, p_getConnectedTextureVerticalHorizontal_3_,
				p_getConnectedTextureVerticalHorizontal_4_, p_getConnectedTextureVerticalHorizontal_5_,
				p_getConnectedTextureVerticalHorizontal_6_, p_getConnectedTextureVerticalHorizontal_7_);
		if (textureatlassprite != null && textureatlassprite != p_getConnectedTextureVerticalHorizontal_6_
				&& textureatlassprite != atextureatlassprite[3]) {
			return textureatlassprite;
		}
		TextureAtlasSprite textureatlassprite1 = ConnectedTextures.getConnectedTextureHorizontal(
				p_getConnectedTextureVerticalHorizontal_0_, p_getConnectedTextureVerticalHorizontal_1_,
				p_getConnectedTextureVerticalHorizontal_2_, p_getConnectedTextureVerticalHorizontal_3_,
				p_getConnectedTextureVerticalHorizontal_4_, p_getConnectedTextureVerticalHorizontal_5_,
				p_getConnectedTextureVerticalHorizontal_6_, p_getConnectedTextureVerticalHorizontal_7_);
		return textureatlassprite1 == atextureatlassprite[0] ? atextureatlassprite[4]
				: (textureatlassprite1 == atextureatlassprite[1] ? atextureatlassprite[5]
						: (textureatlassprite1 == atextureatlassprite[2] ? atextureatlassprite[6]
								: textureatlassprite1));
	}

	private static TextureAtlasSprite getConnectedTextureTop(ConnectedProperties p_getConnectedTextureTop_0_,
			IBlockAccess p_getConnectedTextureTop_1_, IBlockState p_getConnectedTextureTop_2_,
			BlockPos p_getConnectedTextureTop_3_, int p_getConnectedTextureTop_4_, int p_getConnectedTextureTop_5_,
			TextureAtlasSprite p_getConnectedTextureTop_6_, int p_getConnectedTextureTop_7_) {
		boolean flag = false;
		switch (p_getConnectedTextureTop_4_) {
		case 0: {
			if (p_getConnectedTextureTop_5_ == 1 || p_getConnectedTextureTop_5_ == 0) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_,
					p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.up(), p_getConnectedTextureTop_5_,
					p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
			break;
		}
		case 1: {
			if (p_getConnectedTextureTop_5_ == 3 || p_getConnectedTextureTop_5_ == 2) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_,
					p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.south(), p_getConnectedTextureTop_5_,
					p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
			break;
		}
		case 2: {
			if (p_getConnectedTextureTop_5_ == 5 || p_getConnectedTextureTop_5_ == 4) {
				return null;
			}
			flag = ConnectedTextures.isNeighbour(p_getConnectedTextureTop_0_, p_getConnectedTextureTop_1_,
					p_getConnectedTextureTop_2_, p_getConnectedTextureTop_3_.east(), p_getConnectedTextureTop_5_,
					p_getConnectedTextureTop_6_, p_getConnectedTextureTop_7_);
		}
		}
		if (flag) {
			return p_getConnectedTextureTop_0_.tileIcons[0];
		}
		return null;
	}

	public static void updateIcons(TextureMap p_updateIcons_0_) {
		blockProperties = null;
		tileProperties = null;
		if (Config.isConnectedTextures()) {
			IResourcePack[] airesourcepack = Config.getResourcePacks();
			for (int i = airesourcepack.length - 1; i >= 0; --i) {
				IResourcePack iresourcepack = airesourcepack[i];
				ConnectedTextures.updateIcons(p_updateIcons_0_, iresourcepack);
			}
			ConnectedTextures.updateIcons(p_updateIcons_0_, Config.getDefaultResourcePack());
			ResourceLocation resourcelocation = new ResourceLocation("mcpatcher/ctm/default/empty");
			emptySprite = p_updateIcons_0_.registerSprite(resourcelocation);
			spriteQuadMaps = new Map[p_updateIcons_0_.getCountRegisteredSprites() + 1];
		}
	}

	private static void updateIconEmpty(TextureMap p_updateIconEmpty_0_) {
	}

	public static void updateIcons(TextureMap p_updateIcons_0_, IResourcePack p_updateIcons_1_) {
		String[] astring = ConnectedTextures.collectFiles(p_updateIcons_1_, "mcpatcher/ctm/", ".properties");
		Arrays.sort(astring);
		List list = ConnectedTextures.makePropertyList(tileProperties);
		List list1 = ConnectedTextures.makePropertyList(blockProperties);
		for (int i = 0; i < astring.length; ++i) {
			String s = astring[i];
			Config.dbg("ConnectedTextures: " + s);
			try {
				ResourceLocation resourcelocation = new ResourceLocation(s);
				InputStream inputstream = p_updateIcons_1_.getInputStream(resourcelocation);
				if (inputstream == null) {
					Config.warn("ConnectedTextures file not found: " + s);
					continue;
				}
				Properties properties = new Properties();
				properties.load(inputstream);
				ConnectedProperties connectedproperties = new ConnectedProperties(properties, s);
				if (!connectedproperties.isValid(s))
					continue;
				connectedproperties.updateIcons(p_updateIcons_0_);
				ConnectedTextures.addToTileList(connectedproperties, list);
				ConnectedTextures.addToBlockList(connectedproperties, list1);
				continue;
			} catch (FileNotFoundException var11) {
				Config.warn("ConnectedTextures file not found: " + s);
				continue;
			} catch (IOException ioexception) {
				ioexception.printStackTrace();
			}
		}
		blockProperties = ConnectedTextures.propertyListToArray(list1);
		tileProperties = ConnectedTextures.propertyListToArray(list);
		multipass = ConnectedTextures.detectMultipass();
		Config.dbg("Multipass connected textures: " + multipass);
	}

	private static List makePropertyList(ConnectedProperties[][] p_makePropertyList_0_) {
		ArrayList<ArrayList<ConnectedProperties>> list = new ArrayList<ArrayList<ConnectedProperties>>();
		if (p_makePropertyList_0_ != null) {
			for (int i = 0; i < p_makePropertyList_0_.length; ++i) {
				ConnectedProperties[] aconnectedproperties = p_makePropertyList_0_[i];
				ArrayList<ConnectedProperties> list1 = null;
				if (aconnectedproperties != null) {
					list1 = new ArrayList<ConnectedProperties>(Arrays.asList(aconnectedproperties));
				}
				list.add(list1);
			}
		}
		return list;
	}

	private static boolean detectMultipass() {
		ArrayList<ConnectedProperties> list = new ArrayList<ConnectedProperties>();
		for (int i = 0; i < tileProperties.length; ++i) {
			ConnectedProperties[] aconnectedproperties = tileProperties[i];
			if (aconnectedproperties == null)
				continue;
			list.addAll(Arrays.asList(aconnectedproperties));
		}
		for (int k = 0; k < blockProperties.length; ++k) {
			ConnectedProperties[] aconnectedproperties2 = blockProperties[k];
			if (aconnectedproperties2 == null)
				continue;
			list.addAll(Arrays.asList(aconnectedproperties2));
		}
		ConnectedProperties[] aconnectedproperties1 = list.toArray(new ConnectedProperties[list.size()]);
		HashSet<TextureAtlasSprite> set1 = new HashSet<TextureAtlasSprite>();
		HashSet<TextureAtlasSprite> set = new HashSet<TextureAtlasSprite>();
		for (int j = 0; j < aconnectedproperties1.length; ++j) {
			ConnectedProperties connectedproperties = aconnectedproperties1[j];
			if (connectedproperties.matchTileIcons != null) {
				set1.addAll(Arrays.asList(connectedproperties.matchTileIcons));
			}
			if (connectedproperties.tileIcons == null)
				continue;
			set.addAll(Arrays.asList(connectedproperties.tileIcons));
		}
		set1.retainAll(set);
		return !set1.isEmpty();
	}

	private static ConnectedProperties[][] propertyListToArray(List p_propertyListToArray_0_) {
		ConnectedProperties[][] aconnectedproperties = new ConnectedProperties[p_propertyListToArray_0_.size()][];

		for (int i = 0; i < p_propertyListToArray_0_.size(); ++i) {
			List list = (List) p_propertyListToArray_0_.get(i);

			if (list != null) {
				ConnectedProperties[] aconnectedproperties1 = (ConnectedProperties[]) ((ConnectedProperties[]) list
						.toArray(new ConnectedProperties[list.size()]));
				aconnectedproperties[i] = aconnectedproperties1;
			}
		}

		return aconnectedproperties;
	}

	private static void addToTileList(ConnectedProperties p_addToTileList_0_, List p_addToTileList_1_) {
		if (p_addToTileList_0_.matchTileIcons != null) {
			for (int i = 0; i < p_addToTileList_0_.matchTileIcons.length; ++i) {
				TextureAtlasSprite textureatlassprite = p_addToTileList_0_.matchTileIcons[i];
				if (!(textureatlassprite instanceof TextureAtlasSprite)) {
					Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureatlassprite + ", name: "
							+ textureatlassprite.getIconName());
					continue;
				}
				int j = textureatlassprite.getIndexInMap();
				if (j < 0) {
					Config.warn("Invalid tile ID: " + j + ", icon: " + textureatlassprite.getIconName());
					continue;
				}
				ConnectedTextures.addToList(p_addToTileList_0_, p_addToTileList_1_, j);
			}
		}
	}

	private static void addToBlockList(ConnectedProperties p_addToBlockList_0_, List p_addToBlockList_1_) {
		if (p_addToBlockList_0_.matchBlocks != null) {
			for (int i = 0; i < p_addToBlockList_0_.matchBlocks.length; ++i) {
				int j = p_addToBlockList_0_.matchBlocks[i];
				if (j < 0) {
					Config.warn("Invalid block ID: " + j);
					continue;
				}
				ConnectedTextures.addToList(p_addToBlockList_0_, p_addToBlockList_1_, j);
			}
		}
	}

	private static void addToList(ConnectedProperties p_addToList_0_, List p_addToList_1_, int p_addToList_2_) {
		while (p_addToList_2_ >= p_addToList_1_.size()) {
			p_addToList_1_.add(null);
		}
		ArrayList<ConnectedProperties> list = (ArrayList<ConnectedProperties>) p_addToList_1_.get(p_addToList_2_);
		if (list == null) {
			list = new ArrayList<ConnectedProperties>();
			p_addToList_1_.set(p_addToList_2_, list);
		}
		list.add(p_addToList_0_);
	}

	private static String[] collectFiles(IResourcePack p_collectFiles_0_, String p_collectFiles_1_,
			String p_collectFiles_2_) {
		if (p_collectFiles_0_ instanceof DefaultResourcePack) {
			return ConnectedTextures.collectFilesDefault(p_collectFiles_0_);
		}
		if (!(p_collectFiles_0_ instanceof AbstractResourcePack)) {
			return new String[0];
		}
		AbstractResourcePack abstractresourcepack = (AbstractResourcePack) p_collectFiles_0_;
		File file1 = ResourceUtils.getResourcePackFile(abstractresourcepack);
		return file1 == null ? new String[0]
				: (file1.isDirectory()
						? ConnectedTextures.collectFilesFolder(file1, "", p_collectFiles_1_, p_collectFiles_2_)
						: (file1.isFile()
								? ConnectedTextures.collectFilesZIP(file1, p_collectFiles_1_, p_collectFiles_2_)
								: new String[0]));
	}

	private static String[] collectFilesDefault(IResourcePack p_collectFilesDefault_0_) {
		ArrayList<String> list = new ArrayList<String>();
		String[] astring = ConnectedTextures.getDefaultCtmPaths();
		for (int i = 0; i < astring.length; ++i) {
			String s = astring[i];
			ResourceLocation resourcelocation = new ResourceLocation(s);
			if (!p_collectFilesDefault_0_.resourceExists(resourcelocation))
				continue;
			list.add(s);
		}
		String[] astring1 = list.toArray(new String[list.size()]);
		return astring1;
	}

	private static String[] getDefaultCtmPaths() {
		ArrayList<String> list = new ArrayList<String>();
		String s = "mcpatcher/ctm/default/";
		if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
			list.add(s + "glass.properties");
			list.add(s + "glasspane.properties");
		}
		if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
			list.add(s + "bookshelf.properties");
		}
		if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
			list.add(s + "sandstone.properties");
		}
		String[] astring = new String[] { "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
				"silver", "cyan", "purple", "blue", "brown", "green", "red", "black" };
		for (int i = 0; i < astring.length; ++i) {
			String s1 = astring[i];
			if (!Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + s1 + ".png")))
				continue;
			list.add(s + i + "_glass_" + s1 + "/glass_" + s1 + ".properties");
			list.add(s + i + "_glass_" + s1 + "/glass_pane_" + s1 + ".properties");
		}
		String[] astring1 = list.toArray(new String[list.size()]);
		return astring1;
	}

	private static String[] collectFilesFolder(File p_collectFilesFolder_0_, String p_collectFilesFolder_1_,
			String p_collectFilesFolder_2_, String p_collectFilesFolder_3_) {
		ArrayList<String> list = new ArrayList<String>();
		String s = "assets/minecraft/";
		File[] afile = p_collectFilesFolder_0_.listFiles();
		if (afile == null) {
			return new String[0];
		}
		for (int i = 0; i < afile.length; ++i) {
			File file1 = afile[i];
			if (file1.isFile()) {
				String s3 = p_collectFilesFolder_1_ + file1.getName();
				if (!s3.startsWith(s) || !(s3 = s3.substring(s.length())).startsWith(p_collectFilesFolder_2_)
						|| !s3.endsWith(p_collectFilesFolder_3_))
					continue;
				list.add(s3);
				continue;
			}
			if (!file1.isDirectory())
				continue;
			String s1 = p_collectFilesFolder_1_ + file1.getName() + "/";
			String[] astring = ConnectedTextures.collectFilesFolder(file1, s1, p_collectFilesFolder_2_,
					p_collectFilesFolder_3_);
			for (int j = 0; j < astring.length; ++j) {
				String s2 = astring[j];
				list.add(s2);
			}
		}
		String[] astring1 = list.toArray(new String[list.size()]);
		return astring1;
	}

	private static String[] collectFilesZIP(File p_collectFilesZIP_0_, String p_collectFilesZIP_1_,
			String p_collectFilesZIP_2_) {
		ArrayList<String> list = new ArrayList<String>();
		String s = "assets/minecraft/";
		try {
			ZipFile zipfile = new ZipFile(p_collectFilesZIP_0_);
			Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry zipentry = enumeration.nextElement();
				String s1 = zipentry.getName();
				if (!s1.startsWith(s) || !(s1 = s1.substring(s.length())).startsWith(p_collectFilesZIP_1_)
						|| !s1.endsWith(p_collectFilesZIP_2_))
					continue;
				list.add(s1);
			}
			zipfile.close();
			String[] astring = list.toArray(new String[list.size()]);
			return astring;
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			return new String[0];
		}
	}

	public static int getPaneTextureIndex(boolean p_getPaneTextureIndex_0_, boolean p_getPaneTextureIndex_1_,
			boolean p_getPaneTextureIndex_2_, boolean p_getPaneTextureIndex_3_) {
		return p_getPaneTextureIndex_1_ && p_getPaneTextureIndex_0_
				? (p_getPaneTextureIndex_2_ ? (p_getPaneTextureIndex_3_ ? 34 : 50)
						: (p_getPaneTextureIndex_3_ ? 18 : 2))
				: (p_getPaneTextureIndex_1_ && !p_getPaneTextureIndex_0_
						? (p_getPaneTextureIndex_2_ ? (p_getPaneTextureIndex_3_ ? 35 : 51)
								: (p_getPaneTextureIndex_3_ ? 19 : 3))
						: (!p_getPaneTextureIndex_1_ && p_getPaneTextureIndex_0_
								? (p_getPaneTextureIndex_2_ ? (p_getPaneTextureIndex_3_ ? 33 : 49)
										: (p_getPaneTextureIndex_3_ ? 17 : 1))
								: (p_getPaneTextureIndex_2_ ? (p_getPaneTextureIndex_3_ ? 32 : 48)
										: (p_getPaneTextureIndex_3_ ? 16 : 0))));
	}

	public static int getReversePaneTextureIndex(int p_getReversePaneTextureIndex_0_) {
		int i = p_getReversePaneTextureIndex_0_ % 16;
		return i == 1 ? p_getReversePaneTextureIndex_0_ + 2
				: (i == 3 ? p_getReversePaneTextureIndex_0_ - 2 : p_getReversePaneTextureIndex_0_);
	}

	public static TextureAtlasSprite getCtmTexture(ConnectedProperties p_getCtmTexture_0_, int p_getCtmTexture_1_,
			TextureAtlasSprite p_getCtmTexture_2_) {
		if (p_getCtmTexture_0_.method != 1) {
			return p_getCtmTexture_2_;
		}
		if (p_getCtmTexture_1_ >= 0 && p_getCtmTexture_1_ < ctmIndexes.length) {
			int i = ctmIndexes[p_getCtmTexture_1_];
			TextureAtlasSprite[] atextureatlassprite = p_getCtmTexture_0_.tileIcons;
			return i >= 0 && i < atextureatlassprite.length ? atextureatlassprite[i] : p_getCtmTexture_2_;
		}
		return p_getCtmTexture_2_;
	}
}
