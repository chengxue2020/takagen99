package com.github.tvbox.osc.util;

import android.text.TextUtils;

import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.urlhttp.OkHttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.github.tvbox.quickjs.JSUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static File open(String str) {
        return new File(App.getInstance().getExternalCacheDir().getAbsolutePath() + "/qjscache_" + str + ".js");
    }

    public static boolean writeSimple(byte[] data, File dst) {
        try {
            if (dst.exists())
                dst.delete();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dst));
            bos.write(data);
            bos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] readSimple(File src) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
            int len = bis.available();
            byte[] data = new byte[len];
            bis.read(data);
            bis.close();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyFile(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    public static void recursiveDelete(File file) {
        try {
            if (!file.exists())
                return;
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    recursiveDelete(f);
                }
            }
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String loadModule(String name) {
        try {
            if(name.contains("cheerio.min.js")){
                name = "cheerio.min.js";
            } else if(name.contains("crypto-js.js")){
                name = "crypto-js.js";
            } else if(name.contains("dayjs.min.js")){
                name = "dayjs.min.js";
            } else if(name.contains("uri.min.js")){
                name = "uri.min.js";
            } else if(name.contains("underscore-esm-min.js")){
                name = "underscore-esm-min.js";
            }
            if (name.startsWith("http://") || name.startsWith("https://")) {
                String cache = getCache(MD5.encode(name));
                if (JSUtils.isEmpty(cache)) {
                    String netStr = OkHttpUtil.get(name);
                    if (!TextUtils.isEmpty(netStr)) {
                        setCache(604800, MD5.encode(name), netStr);
                    }
                    return netStr;
                }
                return cache;
            } else if (name.startsWith("assets://")) {
                return getAsOpen(name.substring(9));
            } else if (isAsFile(name, "js/lib")) {
                return getAsOpen("js/lib/" + name);
            } else if (name.startsWith("file://")) {
                return OkHttpUtil.get(ControlManager.get().getAddress(true) + "file/" + name.replace("file:///", "").replace("file://", ""));
            } else if (name.startsWith("clan://localhost/")) {
                return OkHttpUtil.get(ControlManager.get().getAddress(true) + "file/" + name.replace("clan://localhost/", ""));
            } else if (name.startsWith("clan://")) {
                String substring = name.substring(7);
                int indexOf = substring.indexOf(47);
                return OkHttpUtil.get("http://" + substring.substring(0, indexOf) + "/file/" + substring.substring(indexOf + 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return name;
        }
        return name;
    }

    public static boolean isAsFile(String name, String path) {
        try {
            for (String fname : App.getInstance().getAssets().list(path)) {
                if (fname.equals(name.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAsOpen(String name) {
        try {
            InputStream is = App.getInstance().getAssets().open(name);
            byte[] data = new byte[is.available()];
            is.read(data);
            return new String(data, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCache(String name) {
        try {
            String code = "";
            File file = open(name);
            if (file.exists()) {
                code = new String(readSimple(file));
            }
            if (TextUtils.isEmpty(code)) {
                return "";
            }
            JsonObject asJsonObject = (new Gson().fromJson(code, JsonObject.class)).getAsJsonObject();
            if (((long) asJsonObject.get("expires").getAsInt()) > System.currentTimeMillis() / 1000) {
                return asJsonObject.get("data").getAsString();
            }
            recursiveDelete(open(name));
            return "";
        } catch (Exception e4) {
            return "";
        }
    }

    public static void setCache(int time, String name, String data) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("expires", (int) (time + (System.currentTimeMillis() / 1000)));
            jSONObject.put("data", data);
            writeSimple(jSONObject.toString().getBytes(), open(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getCacheDir() {
        return App.getInstance().getCacheDir();
    }
    public static File getExternalCacheDir() {
        return App.getInstance().getExternalCacheDir();
    }
    public static String getExternalCachePath() {
        return getExternalCacheDir().getAbsolutePath();
    }

    public static String getCachePath() {
        return getCacheDir().getAbsolutePath();
    }
    
    public static void cleanPlayerCache() {
        String thunderCachePath = getCachePath() + "/thunder/";
        File thunderCacheDir = new File(thunderCachePath);
        try {
            if (thunderCacheDir.exists()) recursiveDelete(thunderCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String ijkCachePath = getExternalCachePath() + "/ijkcaches/";
        File ijkCacheDir = new File(ijkCachePath);
        try {
            if (ijkCacheDir.exists()) recursiveDelete(ijkCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jpaliCachePath = getExternalCachePath() + "/jpali/Downloads/";
        File jpaliCacheDir = new File(jpaliCachePath);
        try {
            if (jpaliCacheDir.exists()) recursiveDelete(jpaliCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getFileNameWithoutExt(String filePath){
        if(TextUtils.isEmpty(filePath)) return "";
        String fileName = filePath;
        int p = fileName.lastIndexOf(File.separatorChar);
        if(p != -1){
            fileName = fileName.substring(p + 1);
        }
        p = fileName.indexOf('.');
        if(p != -1){
            fileName = fileName.substring(0, p);
        }
        return fileName;
    }

}