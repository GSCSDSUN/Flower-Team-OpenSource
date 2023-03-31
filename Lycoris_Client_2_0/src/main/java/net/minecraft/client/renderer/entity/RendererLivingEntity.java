/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 */
package net.minecraft.client.renderer.entity;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import digital.rbq.core.Autumn;
import digital.rbq.events.render.RenderNametagEvent;
import digital.rbq.module.impl.visuals.ChamsMod;
import digital.rbq.module.impl.visuals.DamageColorMod;
import digital.rbq.module.impl.visuals.GlowMod;
import digital.rbq.utils.render.FrameBufferUtils;
import digital.rbq.utils.render.Palette;
import digital.rbq.utils.render.StencilUtils;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
	private static final Logger logger = LogManager.getLogger();
	private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
	protected ModelBase mainModel;
	protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
	protected List<LayerRenderer<T>> layerRenderers = new ArrayList<LayerRenderer<T>>();
	protected boolean renderOutlines = false;

	public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
		super(renderManagerIn);
		this.mainModel = modelBaseIn;
		this.shadowSize = shadowSizeIn;
	}

	public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(Object layer) {
		return this.layerRenderers.add((LayerRenderer<T>) layer);
	}

	protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
		return this.layerRenderers.remove(layer);
	}

	public ModelBase getMainModel() {
		return this.mainModel;
	}

	protected float interpolateRotation(float par1, float par2, float par3) {
		float f;
		for (f = par2 - par1; f < -180.0f; f += 360.0f) {
		}
		while (f >= 180.0f) {
			f -= 360.0f;
		}
		return par1 + par3 * f;
	}

	public void transformHeldFull3DItemLayer() {
	}

	@Override
	public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
		this.mainModel.isRiding = ((Entity) entity).isRiding();
		this.mainModel.isChild = ((EntityLivingBase) entity).isChild();
		try {
			float f = this.interpolateRotation(((EntityLivingBase) entity).prevRenderYawOffset,
					((EntityLivingBase) entity).renderYawOffset, partialTicks);
			float f1 = this.interpolateRotation(((EntityLivingBase) entity).prevRotationYawHead,
					((EntityLivingBase) entity).rotationYawHead, partialTicks);
			float f2 = f1 - f;
			if (((Entity) entity).isRiding() && ((EntityLivingBase) entity).ridingEntity instanceof EntityLivingBase) {
				EntityLivingBase entitylivingbase = (EntityLivingBase) ((EntityLivingBase) entity).ridingEntity;
				f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset,
						partialTicks);
				f2 = f1 - f;
				float f3 = MathHelper.wrapAngleTo180_float(f2);
				if (f3 < -85.0f) {
					f3 = -85.0f;
				}
				if (f3 >= 85.0f) {
					f3 = 85.0f;
				}
				f = f1 - f3;
				if (f3 * f3 > 2500.0f) {
					f += f3 * 0.2f;
				}
			}
			this.renderLivingAt(entity, x, y, z);
			float f8 = this.handleRotationFloat(entity, partialTicks);
			float f7 = ((EntityLivingBase) entity).prevRotationPitch
					+ (((EntityLivingBase) entity).rotationPitch - ((EntityLivingBase) entity).prevRotationPitch)
							* partialTicks;
			this.rotateCorpse(entity, f8, f, partialTicks);
			GlStateManager.enableRescaleNormal();
			GlStateManager.scale(-1.0f, -1.0f, 1.0f);
			this.preRenderCallback(entity, partialTicks);
			float f4 = 0.0625f;
			GlStateManager.translate(0.0f, -1.5078125f, 0.0f);
			float f5 = ((EntityLivingBase) entity).prevLimbSwingAmount
					+ (((EntityLivingBase) entity).limbSwingAmount - ((EntityLivingBase) entity).prevLimbSwingAmount)
							* partialTicks;
			float f6 = ((EntityLivingBase) entity).limbSwing
					- ((EntityLivingBase) entity).limbSwingAmount * (1.0f - partialTicks);
			if (((EntityLivingBase) entity).isChild()) {
				f6 *= 3.0f;
			}
			if (f5 > 1.0f) {
				f5 = 1.0f;
			}
			GlStateManager.enableAlpha();
			this.mainModel.setLivingAnimations((EntityLivingBase) entity, f6, f5, partialTicks);
			this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, (Entity) entity);
			if (this.renderOutlines) {
				boolean flag1 = this.setScoreTeamColor(entity);
				this.renderModel(entity, f6, f5, f8, f2, f7, f4);
				if (flag1) {
					this.unsetScoreTeamColor();
				}
			} else {
				boolean flag = this.setDoRenderBrightness(entity, partialTicks);
				this.renderModel(entity, f6, f5, f8, f2, f7, f4);
				GlStateManager.depthMask(true);
				if (!(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator()) {
					this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
				}
				if (flag) {
					this.unsetBrightness();
				}
			}
			GlStateManager.disableRescaleNormal();
		} catch (Exception exception) {
			logger.error("Couldn't render entity", (Throwable) exception);
		}
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		if (!this.renderOutlines) {
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	protected boolean setScoreTeamColor(T entityLivingBaseIn) {
		String s;
		ScorePlayerTeam scoreplayerteam;
		int i = 0xFFFFFF;
		if (entityLivingBaseIn instanceof EntityPlayer
				&& (scoreplayerteam = (ScorePlayerTeam) ((EntityLivingBase) entityLivingBaseIn).getTeam()) != null
				&& (s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix())).length() >= 2) {
			i = this.getFontRendererFromRenderManager().getColorCode(s.charAt(1));
		}
		float f1 = (float) (i >> 16 & 0xFF) / 255.0f;
		float f2 = (float) (i >> 8 & 0xFF) / 255.0f;
		float f = (float) (i & 0xFF) / 255.0f;
		GlStateManager.disableLighting();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.color(f1, f2, f, 1.0f);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		return true;
	}

	protected void unsetScoreTeamColor() {
		GlStateManager.enableLighting();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	protected void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_,
			float p_77036_5_, float p_77036_6_, float p_77036_7_) {
		boolean flag3;
		boolean flag = !((Entity) entitylivingbaseIn).isInvisible();
		boolean flag1 = !flag && !((Entity) entitylivingbaseIn).isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
		ChamsMod chams = Autumn.MANAGER_REGISTRY.moduleManager.getModuleOrNull(ChamsMod.class);
		boolean flag2 = chams.isEnabled() && entitylivingbaseIn instanceof EntityPlayer;
		boolean bl = flag3 = Autumn.MANAGER_REGISTRY.moduleManager.getModuleOrNull(GlowMod.class).isEnabled()
				&& entitylivingbaseIn instanceof EntityOtherPlayerMP;
		if (flag || flag1) {
			Color chamsColor;
			boolean textured;
			if (!this.bindEntityTexture(entitylivingbaseIn)) {
				return;
			}
			int blend = 3042;
			int depth = 2929;
			int srcAlpha = 770;
			int srcAlphaPlus1 = 771;
			int polygonOffsetLine = 10754;
			int texture2D = 3553;
			int lighting = 2896;
			boolean bl2 = textured = chams.mode.getValue() == ChamsMod.Mode.TEXTURED;
			if (flag1) {
				GL11.glPushMatrix();
				GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 0.15f);
				GL11.glDepthMask((boolean) false);
				GL11.glEnable((int) 3042);
				GlStateManager.blendFunc(770, 771);
			}
			if (flag3) {
				Color color = (Color) GlowMod.enemyColor.getValue();
				FrameBufferUtils.checkSetupFBO();
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				StencilUtils.renderOne();
				GL11.glLineWidth((float) ((Double) GlowMod.lineWidth.getValue()).floatValue());
				GL11.glBlendFunc((int) 770, (int) 771);
				GL11.glEnable((int) 3042);
				GL11.glEnable((int) 2848);
				GL11.glHint((int) 3154, (int) 4354);
				GlStateManager.enableAlpha();
				GL11.glEnable((int) 3024);
				GL11.glColor4f((float) ((float) color.getRed() / 255.0f), (float) ((float) color.getGreen() / 255.0f),
						(float) ((float) color.getBlue() / 255.0f), (float) ((float) color.getAlpha() / 255.0f));
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				StencilUtils.renderTwo();
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				StencilUtils.renderThree();
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				StencilUtils.renderFour();
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				StencilUtils.renderFive();
				GL11.glDisable((int) 3024);
			}
			if (flag2) {
				chamsColor = Palette.fade(((Palette) ((Object) chams.playerBehindWalls.getValue())).getColor());
				GL11.glPushMatrix();
				GL11.glEnable((int) 10754);
				GL11.glPolygonOffset((float) 1.0f, (float) 1000000.0f);
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
				if (!textured) {
					GL11.glEnable((int) 3042);
					GL11.glDisable((int) 3553);
					GL11.glDisable((int) 2896);
					GL11.glBlendFunc((int) 770, (int) 771);
					GL11.glColor4f((float) ((float) chamsColor.getRed() / 255.0f),
							(float) ((float) chamsColor.getGreen() / 255.0f),
							(float) ((float) chamsColor.getBlue() / 255.0f),
							(float) ((float) chamsColor.getAlpha() / 255.0f));
				}
				GL11.glDisable((int) 2929);
				GL11.glDepthMask((boolean) false);
			}
			this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
					p_77036_6_, p_77036_7_);
			if (flag2) {
				GL11.glEnable((int) 2929);
				GL11.glDepthMask((boolean) true);
				if (!textured) {
					chamsColor = ((Palette) ((Object) chams.player.getValue())).getColor();
					GL11.glColor4f((float) ((float) chamsColor.getRed() / 255.0f),
							(float) ((float) chamsColor.getGreen() / 255.0f),
							(float) ((float) chamsColor.getBlue() / 255.0f),
							(float) ((float) chamsColor.getAlpha() / 255.0f));
				}
				this.mainModel.render((Entity) entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_,
						p_77036_6_, p_77036_7_);
				if (!textured) {
					GL11.glEnable((int) 3553);
					GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, (float) 1.0f);
					GL11.glDisable((int) 3042);
					GL11.glEnable((int) 2896);
				}
				GL11.glPolygonOffset((float) 1.0f, (float) -1000000.0f);
				GL11.glDisable((int) 10754);
				GL11.glPopMatrix();
			}
			if (flag1) {
				GL11.glDisable((int) 3042);
				GL11.glDepthMask((boolean) false);
				GL11.glPopMatrix();
			}
		}
	}

	protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
		return this.setBrightness(entityLivingBaseIn, partialTicks, true);
	}

	protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
		float f = ((Entity) entitylivingbaseIn).getBrightness(partialTicks);
		int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
		boolean flag = (i >> 24 & 0xFF) > 0;
		Color colorRef = (Color) DamageColorMod.color.getValue();
		boolean flag1 = ((EntityLivingBase) entitylivingbaseIn).hurtTime > 0
				|| ((EntityLivingBase) entitylivingbaseIn).deathTime > 0;
		int textureEnv = 8960;
		if (!flag && !flag1) {
			return false;
		}
		if (!flag && !combineTextures) {
			return false;
		}
		this.GLFunc0(7681);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_ALPHA, (int) 770);
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.enableTexture2D();
		GL11.glTexEnvi((int) 8960, (int) 8704, (int) OpenGlHelper.GL_COMBINE);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_RGB, (int) OpenGlHelper.GL_INTERPOLATE);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_RGB, (int) OpenGlHelper.GL_CONSTANT);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE1_RGB, (int) OpenGlHelper.GL_PREVIOUS);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE2_RGB, (int) OpenGlHelper.GL_CONSTANT);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND1_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND2_RGB, (int) 770);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_ALPHA, (int) 7681);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_ALPHA, (int) OpenGlHelper.GL_PREVIOUS);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_ALPHA, (int) 770);
		this.brightnessBuffer.position(0);
		if (flag1) {
			this.brightnessBuffer.put((float) colorRef.getRed() / 255.0f);
			this.brightnessBuffer.put((float) colorRef.getGreen() / 255.0f);
			this.brightnessBuffer.put((float) colorRef.getBlue() / 255.0f);
			this.brightnessBuffer.put((float) colorRef.getAlpha() / 255.0f);
		} else {
			float f1 = (float) (i >> 24 & 0xFF) / 255.0f;
			float f2 = (float) (i >> 16 & 0xFF) / 255.0f;
			float f3 = (float) (i >> 8 & 0xFF) / 255.0f;
			float f4 = (float) (i & 0xFF) / 255.0f;
			this.brightnessBuffer.put(f2);
			this.brightnessBuffer.put(f3);
			this.brightnessBuffer.put(f4);
			this.brightnessBuffer.put(1.0f - f1);
		}
		this.brightnessBuffer.flip();
		GL11.glTexEnv((int) 8960, (int) 8705, (FloatBuffer) this.brightnessBuffer);
		GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
		GlStateManager.enableTexture2D();
		GlStateManager.bindTexture(field_177096_e.getGlTextureId());
		GL11.glTexEnvi((int) 8960, (int) 8704, (int) OpenGlHelper.GL_COMBINE);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_RGB, (int) 8448);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_RGB, (int) OpenGlHelper.GL_PREVIOUS);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE1_RGB, (int) OpenGlHelper.lightmapTexUnit);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND1_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_ALPHA, (int) 7681);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_ALPHA, (int) OpenGlHelper.GL_PREVIOUS);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_ALPHA, (int) 770);
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		return true;
	}

	protected void unsetBrightness() {
		int textureEnv = 8960;
		this.GLFunc0(8448);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE1_ALPHA, (int) OpenGlHelper.GL_PRIMARY_COLOR);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_ALPHA, (int) 770);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND1_ALPHA, (int) 770);
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		this.GLFunc1();
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
		GlStateManager.disableTexture2D();
		GlStateManager.bindTexture(0);
		this.GLFunc1();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private void GLFunc1() {
		GL11.glTexEnvi((int) 8960, (int) 8704, (int) OpenGlHelper.GL_COMBINE);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_RGB, (int) 8448);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND1_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_RGB, (int) 5890);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE1_RGB, (int) OpenGlHelper.GL_PREVIOUS);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_ALPHA, (int) 8448);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_ALPHA, (int) 770);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_ALPHA, (int) 5890);
	}

	private void GLFunc0(int glModulate) {
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
		GL11.glTexEnvi((int) 8960, (int) 8704, (int) OpenGlHelper.GL_COMBINE);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_RGB, (int) 8448);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_RGB, (int) OpenGlHelper.defaultTexUnit);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE1_RGB, (int) OpenGlHelper.GL_PRIMARY_COLOR);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND0_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_OPERAND1_RGB, (int) 768);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_COMBINE_ALPHA, (int) glModulate);
		GL11.glTexEnvi((int) 8960, (int) OpenGlHelper.GL_SOURCE0_ALPHA, (int) OpenGlHelper.defaultTexUnit);
	}

	protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
		GlStateManager.translate((float) x, (float) y, (float) z);
	}

	protected void rotateCorpse(T bat, float p_77043_2_, float p_77043_3_, float partialTicks) {
		GlStateManager.rotate(180.0f - p_77043_3_, 0.0f, 1.0f, 0.0f);
		if (((EntityLivingBase) bat).deathTime > 0) {
			float f = ((float) ((EntityLivingBase) bat).deathTime + partialTicks - 1.0f) / 20.0f * 1.6f;
			if ((f = MathHelper.sqrt_float(f)) > 1.0f) {
				f = 1.0f;
			}
			GlStateManager.rotate(f * this.getDeathMaxRotation(bat), 0.0f, 0.0f, 1.0f);
		}
	}

	protected float getSwingProgress(T livingBase, float partialTickTime) {
		return ((EntityLivingBase) livingBase).getSwingProgress(partialTickTime);
	}

	protected float handleRotationFloat(T livingBase, float partialTicks) {
		return (float) ((EntityLivingBase) livingBase).ticksExisted + partialTicks;
	}

	protected void renderLayers(T entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks,
			float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_) {
		for (LayerRenderer<T> layerrenderer : this.layerRenderers) {
			boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
			layerrenderer.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_,
					p_177093_6_, p_177093_7_, p_177093_8_);
			if (!flag)
				continue;
			this.unsetBrightness();
		}
	}

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 90.0f;
	}

	protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
		return 0;
	}

	protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
	}

	@Override
	public void renderName(T entity, double x, double y, double z) {
		if (this.canRenderName(entity)) {
			float f;
			double d0 = ((Entity) entity).getDistanceSqToEntity(this.renderManager.livingPlayer);
			float f2 = f = ((Entity) entity).isSneaking() ? 32.0f : 64.0f;
			if (d0 < (double) (f * f)) {
				RenderNametagEvent event = new RenderNametagEvent((EntityLivingBase) entity,
						((Entity) entity).getDisplayName().getFormattedText());
				Autumn.EVENT_BUS_REGISTRY.eventBus.post(event);
				if (event.isCancelled()) {
					return;
				}
				String s = event.getRenderedName();
				float f1 = 0.02666667f;
				GlStateManager.alphaFunc(516, 0.1f);
				if (((Entity) entity).isSneaking()) {
					FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
					GlStateManager.pushMatrix();
					GlStateManager.translate((float) x,
							(float) y + ((EntityLivingBase) entity).height + 0.5f
									- (((EntityLivingBase) entity).isChild() ? ((EntityLivingBase) entity).height / 2.0f
											: 0.0f),
							(float) z);
					GL11.glNormal3f((float) 0.0f, (float) 1.0f, (float) 0.0f);
					GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
					GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
					GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f);
					GlStateManager.translate(0.0f, 9.374999f, 0.0f);
					GlStateManager.disableLighting();
					GlStateManager.depthMask(false);
					GlStateManager.enableBlend();
					GlStateManager.disableTexture2D();
					GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
					int i = fontrenderer.getStringWidth(s) / 2;
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
					worldrenderer.pos(-i - 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
					worldrenderer.pos(-i - 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
					worldrenderer.pos(i + 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
					worldrenderer.pos(i + 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
					tessellator.draw();
					GlStateManager.enableTexture2D();
					GlStateManager.depthMask(true);
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 0x20FFFFFF);
					GlStateManager.enableLighting();
					GlStateManager.disableBlend();
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					GlStateManager.popMatrix();
				} else {
					this.renderOffsetLivingLabel(entity, x,
							y - (((EntityLivingBase) entity).isChild()
									? (double) (((EntityLivingBase) entity).height / 2.0f)
									: 0.0),
							z, s, 0.02666667f, d0);
				}
			}
		}
	}

	@Override
	protected boolean canRenderName(T entity) {
		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;
		if (entity instanceof EntityPlayer && entity != entityplayersp) {
			Team team = ((EntityLivingBase) entity).getTeam();
			Team team1 = entityplayersp.getTeam();
			if (team != null) {
				Team.EnumVisible team$enumvisible = team.getNameTagVisibility();
				switch (team$enumvisible) {
				case ALWAYS: {
					return true;
				}
				case NEVER: {
					return false;
				}
				case HIDE_FOR_OTHER_TEAMS: {
					return team1 == null || team.isSameTeam(team1);
				}
				case HIDE_FOR_OWN_TEAM: {
					return team1 == null || !team.isSameTeam(team1);
				}
				}
				return true;
			}
		}
		return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer
				&& !((Entity) entity).isInvisibleToPlayer(entityplayersp)
				&& ((EntityLivingBase) entity).riddenByEntity == null;
	}

	public void setRenderOutlines(boolean renderOutlinesIn) {
		this.renderOutlines = renderOutlinesIn;
	}

	static {
		int[] aint = field_177096_e.getTextureData();
		for (int i = 0; i < 256; ++i) {
			aint[i] = -1;
		}
		field_177096_e.updateDynamicTexture();
	}
}
