/*
 * Decompiled with CFR 0.150.
 */
package optfine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import optfine.Blender;
import optfine.Config;
import optfine.CustomSkyLayer;
import optfine.TextureUtils;

public class CustomSky {
    private static CustomSkyLayer[][] worldSkyLayers = null;

    public static void reset() {
        worldSkyLayers = null;
    }

    public static void update() {
        CustomSky.reset();
        if (Config.isCustomSky()) {
            worldSkyLayers = CustomSky.readCustomSkies();
        }
    }

    private static CustomSkyLayer[][] readCustomSkies() {
        CustomSkyLayer[][] acustomskylayer = new CustomSkyLayer[10][0];
        String s = "mcpatcher/sky/world";
        int i = -1;
        for (int j = 0; j < acustomskylayer.length; ++j) {
            String s1 = s + j + "/sky";
            ArrayList<CustomSkyLayer> list = new ArrayList<CustomSkyLayer>();
            for (int k = 1; k < 1000; ++k) {
                String s2 = s1 + k + ".properties";
                try {
                    ResourceLocation resourcelocation = new ResourceLocation(s2);
                    InputStream inputstream = Config.getResourceStream(resourcelocation);
                    if (inputstream == null) break;
                    Properties properties = new Properties();
                    properties.load(inputstream);
                    Config.dbg("CustomSky properties: " + s2);
                    String s3 = s1 + k + ".png";
                    CustomSkyLayer customskylayer = new CustomSkyLayer(properties, s3);
                    if (!customskylayer.isValid(s2)) continue;
                    ResourceLocation resourcelocation1 = new ResourceLocation(customskylayer.source);
                    ITextureObject itextureobject = TextureUtils.getTexture(resourcelocation1);
                    if (itextureobject == null) {
                        Config.log("CustomSky: Texture not found: " + resourcelocation1);
                        continue;
                    }
                    customskylayer.textureId = itextureobject.getGlTextureId();
                    list.add(customskylayer);
                    inputstream.close();
                    continue;
                }
                catch (FileNotFoundException var15) {
                    break;
                }
                catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }
            if (list.size() <= 0) continue;
            CustomSkyLayer[] acustomskylayer2 = list.toArray(new CustomSkyLayer[list.size()]);
            acustomskylayer[j] = acustomskylayer2;
            i = j;
        }
        if (i < 0) {
            return null;
        }
        int l = i + 1;
        CustomSkyLayer[][] acustomskylayer1 = new CustomSkyLayer[l][0];
        for (int i1 = 0; i1 < acustomskylayer1.length; ++i1) {
            acustomskylayer1[i1] = acustomskylayer[i1];
        }
        return acustomskylayer1;
    }

    public static void renderSky(World p_renderSky_0_, TextureManager p_renderSky_1_, float p_renderSky_2_, float p_renderSky_3_) {
        CustomSkyLayer[] acustomskylayer;
        int i;
        if (worldSkyLayers != null && Config.getGameSettings().renderDistanceChunks >= 8 && (i = p_renderSky_0_.provider.getDimensionId()) >= 0 && i < worldSkyLayers.length && (acustomskylayer = worldSkyLayers[i]) != null) {
            long j = p_renderSky_0_.getWorldTime();
            int k = (int)(j % 24000L);
            for (int l = 0; l < acustomskylayer.length; ++l) {
                CustomSkyLayer customskylayer = acustomskylayer[l];
                if (!customskylayer.isActive(k)) continue;
                customskylayer.render(k, p_renderSky_2_, p_renderSky_3_);
            }
            Blender.clearBlend(p_renderSky_3_);
        }
    }

    public static boolean hasSkyLayers(World p_hasSkyLayers_0_) {
        if (worldSkyLayers == null) {
            return false;
        }
        if (Config.getGameSettings().renderDistanceChunks < 8) {
            return false;
        }
        int i = p_hasSkyLayers_0_.provider.getDimensionId();
        if (i >= 0 && i < worldSkyLayers.length) {
            CustomSkyLayer[] acustomskylayer = worldSkyLayers[i];
            return acustomskylayer == null ? false : acustomskylayer.length > 0;
        }
        return false;
    }
}

