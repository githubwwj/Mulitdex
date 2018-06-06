package com.wwj.mulitdex;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

/**
 * Created by Administrator on 2018/6/6 0006.
 */

public class FixBugUtil {

    public static final String dexFileName = "out.dex";
    public static final String optimization = "optimization";

    public static void fixBug(Context context) {
        File sdCardFile = new File(Environment.getExternalStorageDirectory(), dexFileName);
        if (!sdCardFile.exists()) {
            return;
        }
        File cacheDir = context.getCacheDir();
        File dexFile = new File(cacheDir, dexFileName);  //内置卡的dexFile存放绝对路径
        File optimizationDir = new File(cacheDir, optimization);  //优化目录路径
        if (!optimizationDir.exists()) {
            optimizationDir.mkdirs();
        }
        if (!dexFile.exists()) {
            return;
        }
        ClassLoader classLoader = context.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexFile.getAbsolutePath(), optimizationDir.getAbsolutePath(), null, classLoader);

        try {
            Object patchPathList = getFieldObject(dexClassLoader, "dalvik.system.BaseDexClassLoader", "pathList");
            Object patchDexElements = getFieldObject(patchPathList, "dalvik.system.DexPathList", "dexElements");


            Object currentPathList = getFieldObject(classLoader, "dalvik.system.BaseDexClassLoader", "pathList");
            Object currentDexElements = getFieldObject(currentPathList, "dalvik.system.DexPathList", "dexElements");

            int patchLen = Array.getLength(patchDexElements);
            Log.e("tag", "patchLen=" + patchLen);

            int currentLen = Array.getLength(currentDexElements);
            Log.e("tag", "currentLen=" + currentLen);

            int len = patchLen + currentLen;
            Log.e("tag", "len=" + len);

            Object newElement = Array.newInstance(patchDexElements.getClass().getComponentType(), len);
            System.arraycopy(patchDexElements, 0, newElement, 0, patchLen);
            System.arraycopy(currentDexElements, 0, newElement, patchLen, currentLen);

            Log.e("tag", "newElement len=" + Array.getLength(newElement));

            Class pathListClass = currentPathList.getClass();
            Field field = pathListClass.getDeclaredField("dexElements");
            field.setAccessible(true);  //告诉虚拟机可以访问私有属性
            field.set(currentPathList, newElement);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 移动文件到内置卡
     */
    public static void moveFileToFlashDisk(Context context) {

        File sdCardFile = new File(Environment.getExternalStorageDirectory(), dexFileName);

        if (!sdCardFile.exists()) {
            return;
        }

        File cacheDir = context.getCacheDir();
        File dexFile = new File(cacheDir, dexFileName);  //内置卡的dexFile存放绝对路径
        if (dexFile.exists()) {
            dexFile.delete();
        }
        File optimizationDir = new File(cacheDir, optimization);  //优化目录路径
        if (!optimizationDir.exists()) {
            optimizationDir.mkdirs();
        }
//        /out.dex     /optionDir
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(sdCardFile));
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(dexFile));
            byte[] buff = new byte[1024 * 2];
            int len;
            while ((len = bufferedInputStream.read(buff)) != -1) {
                bufferedOutputStream.write(buff, 0, len);
                bufferedOutputStream.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedOutputStream.close();
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static Object getFieldObject(Object object, String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

}
