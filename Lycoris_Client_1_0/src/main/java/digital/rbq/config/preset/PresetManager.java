package digital.rbq.config.preset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import digital.rbq.Lycoris;

import java.io.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PresetManager {
    public static CopyOnWriteArrayList<String> presets = new CopyOnWriteArrayList<>();

    public static void addPreset(String text, boolean saveBinds) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("Lycoris/presets/" + text + ".prs"));
            writer.write(JSONObject.toJSONString(Lycoris.INSTANCE.getConfig().saveModules(saveBinds), true));
            writer.flush();
            writer.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean deletePreset(String preset) {
        File target = new File("Lycoris/presets/" + preset + ".prs");
        return target.exists() && target.delete();
    }

    public static boolean loadPreset(String preset) {
        File target = new File("Lycoris/presets/" + preset + ".prs");
        if (!target.exists() && !target.isDirectory())
            return false;

        try {
            StringBuilder presetString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(target));
            String line;
            while ((line = reader.readLine()) != null) {
                presetString.append(line);
            }
            JSONObject presetObj = JSON.parseObject(presetString.toString());
            Lycoris.INSTANCE.getConfig().loadModules(presetObj);
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    public static JSONObject getPreset(String preset) {
        File target = new File("Lycoris/presets/" + preset + ".prs");
        if (!target.exists() && !target.isDirectory())
            return null;

        try {
            StringBuilder presetString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(target));
            String line;
            while ((line = reader.readLine()) != null) {
                presetString.append(line);
            }
            return JSON.parseObject(presetString.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
