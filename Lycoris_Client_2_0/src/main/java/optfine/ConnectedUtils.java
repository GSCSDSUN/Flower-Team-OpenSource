/*
 * Decompiled with CFR 0.150.
 */
package optfine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import optfine.ResourceUtils;

public class ConnectedUtils {
    public static String[] collectFiles(IResourcePack p_collectFiles_0_, String p_collectFiles_1_, String p_collectFiles_2_, String[] p_collectFiles_3_) {
        if (p_collectFiles_0_ instanceof DefaultResourcePack) {
            return ConnectedUtils.collectFilesFixed(p_collectFiles_0_, p_collectFiles_3_);
        }
        if (!(p_collectFiles_0_ instanceof AbstractResourcePack)) {
            return new String[0];
        }
        AbstractResourcePack abstractresourcepack = (AbstractResourcePack)p_collectFiles_0_;
        File file1 = ResourceUtils.getResourcePackFile(abstractresourcepack);
        return file1 == null ? new String[0] : (file1.isDirectory() ? ConnectedUtils.collectFilesFolder(file1, "", p_collectFiles_1_, p_collectFiles_2_) : (file1.isFile() ? ConnectedUtils.collectFilesZIP(file1, p_collectFiles_1_, p_collectFiles_2_) : new String[0]));
    }

    private static String[] collectFilesFixed(IResourcePack p_collectFilesFixed_0_, String[] p_collectFilesFixed_1_) {
        if (p_collectFilesFixed_1_ == null) {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < p_collectFilesFixed_1_.length; ++i) {
            String s = p_collectFilesFixed_1_[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            if (!p_collectFilesFixed_0_.resourceExists(resourcelocation)) continue;
            list.add(s);
        }
        String[] astring = list.toArray(new String[list.size()]);
        return astring;
    }

    private static String[] collectFilesFolder(File p_collectFilesFolder_0_, String p_collectFilesFolder_1_, String p_collectFilesFolder_2_, String p_collectFilesFolder_3_) {
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
                if (!s3.startsWith(s) || !(s3 = s3.substring(s.length())).startsWith(p_collectFilesFolder_2_) || !s3.endsWith(p_collectFilesFolder_3_)) continue;
                list.add(s3);
                continue;
            }
            if (!file1.isDirectory()) continue;
            String s1 = p_collectFilesFolder_1_ + file1.getName() + "/";
            String[] astring = ConnectedUtils.collectFilesFolder(file1, s1, p_collectFilesFolder_2_, p_collectFilesFolder_3_);
            for (int j = 0; j < astring.length; ++j) {
                String s2 = astring[j];
                list.add(s2);
            }
        }
        String[] astring1 = list.toArray(new String[list.size()]);
        return astring1;
    }

    private static String[] collectFilesZIP(File p_collectFilesZIP_0_, String p_collectFilesZIP_1_, String p_collectFilesZIP_2_) {
        ArrayList<String> list = new ArrayList<String>();
        String s = "assets/minecraft/";
        try {
            ZipFile zipfile = new ZipFile(p_collectFilesZIP_0_);
            Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry zipentry = enumeration.nextElement();
                String s1 = zipentry.getName();
                if (!s1.startsWith(s) || !(s1 = s1.substring(s.length())).startsWith(p_collectFilesZIP_1_) || !s1.endsWith(p_collectFilesZIP_2_)) continue;
                list.add(s1);
            }
            zipfile.close();
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return new String[0];
        }
    }

    public static int getAverage(int[] p_getAverage_0_) {
        if (p_getAverage_0_.length <= 0) {
            return 0;
        }
        int i = 0;
        for (int j = 0; j < p_getAverage_0_.length; ++j) {
            int k = p_getAverage_0_[j];
            i += k;
        }
        int l = i / p_getAverage_0_.length;
        return l;
    }
}

