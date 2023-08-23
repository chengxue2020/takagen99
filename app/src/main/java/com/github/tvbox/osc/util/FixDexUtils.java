package com.github.tvbox.osc.util;

import android.content.Context;
import android.util.Log;

import com.github.tvbox.osc.base.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class FixDexUtils {

    public static void copy(Context context, InputStream is) {
        // 拷贝的目的文件目录
        // /data/user/0/kim.hsl.hotfix/app_odex/update.dex
        File targetDir = context.getDir("odex", Context.MODE_PRIVATE);
        // 拷贝的目的文件名称
        String targetName = "update.dex";

        // 准备目的文件, 将 Dex 文件从 SDK 卡拷贝到此文件中
        String filePath = new File(targetDir, targetName).getAbsolutePath();
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        // 准备输出流, 输出到目的文件
        FileOutputStream os = null;

        try {
            // 输出到目标文件
            os = new FileOutputStream(filePath);

            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭 IO 流
            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 加载 Dex 文件
     * @param context
     */
    public static void loadDex(Context context){
        // 修复包可能有多个, 如先后进行了多次修复 , 存在多个修复包 Dex 文件
        // 这些 Dex 文件按照时间顺序进行放置
        // 之前已经将 SD 卡中的 /storage/emulated/0/update.dex 文件拷贝到了
        // 原应用内置存储空间 /data/user/0/kim.hsl.hotfix/app_odex/update.dex

        // /data/user/0/kim.hsl.hotfix/app_odex/ 目录文件
        File filesDir = context.getDir("odex", Context.MODE_PRIVATE);

        // 获取 /data/user/0/kim.hsl.hotfix/app_odex/ 目录下的所有文件
        File[] listFiles = filesDir.listFiles();

        // 缓存 odex 文件的目录 , 将 dex 优化为 odex 文件
        String optimizedDir = filesDir.getAbsolutePath() + File.separator + "cache_odex";
        File fileOptimizedDir = new File(optimizedDir);
        if (!fileOptimizedDir.exists())
            fileOptimizedDir.mkdirs();

        // 过滤文件, 系统打包都是 classes.dex , classes1.dex , classes2.dex 等文件
        // 上传的更新包 update.dex 以 .dex 为结尾
        // 以上面两个条件作为过滤的依据
        for (File file : listFiles){
            if (file.getAbsolutePath().startsWith("classes") ||
                    file.getAbsolutePath().endsWith(".dex")){

                // 将 dex 文件加载到内存中
                // 该 DexClassLoader 是 BaseDexClassLoader 的子类
                // BaseDexClassLoader 中有 DexPathList pathList 成员
                // 构造该类时 , 会自动将 dex 文件进行优化为 odex , 然后加载到上述 DexPathList pathList 中
                //
                // 参数一 : Dex 文件路径
                // 参数二 : 缓存路径, 指的是缓存 Odex 文件的目录
                // 参数三 : Dex 中的 lib 库路径, 可以设置 null
                // 参数四 : 上下文的 ClassLoader
                DexClassLoader dexClassLoader = new DexClassLoader(
                        file.getAbsolutePath(),
                        optimizedDir,
                        null,
                        context.getClassLoader());

                // 该 PathClassLoader 是用于加载查找 Android 应用所有 dex 文件的类加载器
                // 将上面获取的 dexClassLoader 中的 DexPathList pathList
                // 插入到 PathClassLoader 中的 DexPathList pathList 成员中
                PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();

                // BaseDexClassLoader 中的 DexPathList pathList 是 private 私有的
                // 无法直接获取
                // 需要使用反射机制获取该 Dex 数组
                // 拿到 PathClassLoader (继承 BaseDexClassLoader 类) 对象后
                // 先使用反射机制获取 private final DexPathList pathList 成员
                // 然后再次通过反射 , 获取 DexPathList 中的 private final Element[] dexElements 成员


                try {
                    // 加载系统的 Element[] dexElements ---------------------------------------------
                    // 反射获取 BaseDexClassLoader 类对象
                    Class systemBaseDexClassLoaderClass =
                            Class.forName("dalvik.system.BaseDexClassLoader");
                    // 反射获取 BaseDexClassLoader 中的 private final DexPathList pathList 字段
                    Field systemPathListField =
                            systemBaseDexClassLoaderClass.getDeclaredField("pathList");
                    // 由于是私有成员字段 , 需要设置可访问性
                    systemPathListField.setAccessible(true);

                    // 获取系统的 PathClassLoader pathClassLoader 对象的
                    // private final DexPathList pathList 成员
                    Object systemPathListObject = systemPathListField.get(pathClassLoader);

                    // 获取 DexPathList 类
                    Class systemPathListClass = systemPathListObject.getClass();
                    // 获取 DexPathList 类中的 private final Element[] dexElements 成员字段
                    Field systemDexElementsField =
                            systemPathListClass.getDeclaredField("dexElements");
                    // 由于是私有成员字段 , 需要设置可访问性
                    systemDexElementsField.setAccessible(true);
                    // 获取 DexPathList pathList 对象的 Element[] dexElements 成员
                    Object systemDexElementsObject =
                            systemDexElementsField.get(systemPathListObject);
                    // 系统的 Element[] dexElements 加载完毕-----------------------------------------

                    // 上述反射的是系统的 PathClassLoader 的对象
                    // 下面开始反射在本次循环方法中加载的 DexClassLoader dexClassLoader

                    // 加载自己的 Element[] dexElements ---------------------------------------------
                    // 反射获取 BaseDexClassLoader 类对象
                    Class myBaseDexClassLoaderClass =
                            Class.forName("dalvik.system.BaseDexClassLoader");
                    // 反射获取 BaseDexClassLoader 中的 private final DexPathList pathList 字段
                    Field myPathListField =
                            myBaseDexClassLoaderClass.getDeclaredField("pathList");
                    // 由于是私有成员字段 , 需要设置可访问性
                    myPathListField.setAccessible(true);

                    // 获取系统的 PathClassLoader pathClassLoader 对象的
                    // private final DexPathList pathList 成员
                    Object myPathListObject = myPathListField.get(dexClassLoader);

                    // 获取 DexPathList 类
                    Class myPathListClass = myPathListObject.getClass();
                    // 获取 DexPathList 类中的 private final Element[] dexElements 成员字段
                    Field myDexElementsField =
                            myPathListClass.getDeclaredField("dexElements");
                    // 由于是私有成员字段 , 需要设置可访问性
                    myDexElementsField.setAccessible(true);
                    // 获取 DexPathList pathList 对象的 Element[] dexElements 成员
                    Object myDexElementsObject = myDexElementsField.get(myPathListObject);
                    // 自己的 Element[] dexElements 加载完毕-----------------------------------------

                    // 将系统 PathClassLoader pathClassLoader 的
                    // DexPathList pathList 对象的 Element[] dexElements 成员
                    // systemDexElementsObject
                    // 与
                    // 自己在程序中的 DexClassLoader dexClassLoader 的
                    // DexPathList pathList 对象的 Element[] dexElements 成员
                    // myDexElementsObject
                    // 进行融合
                    // 将 myDexElementsObject 插入到 systemDexElementsObject

                    // 获取 Dex 数组 , Element 类型无法引用 , 不是公开的
                    // 首先获取 Element 类型
                    // systemDexElementsObject
                    Class<?> elementClass = systemDexElementsObject.getClass().getComponentType();

                    // 获取两个 Element[] dexElements 数组的成员个数
                    // 系统中的 PathClassLoader 中的 Element[] dexElements 数组大小
                    int systemDexCount = Array.getLength(systemDexElementsObject);
                    // 本应用中的 DexClassLoader 中的 Element[] dexElements 数组大小
                    int myDexCount = Array.getLength(myDexElementsObject);

                    Log.i("TAG", "systemDexCount = " + systemDexCount + " , myDexCount = " + myDexCount);

                    // 重新创建一个数组
                    // 类型 : Class<?> elementClass
                    // 长度 : systemDexCount + myDexCount
                    Object elementArray =
                            Array.newInstance(elementClass, systemDexCount + myDexCount);

                    // 填充数组内容, 这里特别注意 , 数组中的元素的顺序很重要 ,
                    // 同样类型的类 , 在多个 Dex 都存在 , 如果在前面的 Dex 中查找到了 , 就不再向后查找了
                    // 修复包的 Dex 要放在最前面 , 这样才能起到修复作用

                    // 先放置修复包 Dex
                    for(int i = 0; i < myDexCount; i ++){
                        // 获取 myDexElementsObject 数组中的第 i 个元素
                        // 放置到 elementArray 数组中的第 i 个元素位置
                        Array.set(elementArray, i,
                                Array.get(myDexElementsObject, i));
                    }

                    // 再放置系统 Dex
                    for(int i = 0; i < systemDexCount; i ++){
                        // 获取 systemDexElementsObject 数组中的第 i 个元素
                        // 放置到 elementArray 数组中的第 i + myDexCount 个元素位置
                        Array.set(elementArray,
                                i + myDexCount,
                                Array.get(systemDexElementsObject, i));
                    }

                    // 通过反射方法
                    // 将合并后的 elementArray 数组放置到
                    // PathClassLoader 中的 Element[] dexElements 中
                    systemDexElementsField.set(systemPathListObject, elementArray);



                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}