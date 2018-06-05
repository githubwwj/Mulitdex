package com.wwj.mulitdex;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File appDir=getDir("test",MODE_PRIVATE);
        Log.e("tag", "appDir=" + appDir.getAbsolutePath());

        File cacheDir=getCacheDir();
        Log.e("tag", "cacheDir=" + cacheDir.getAbsolutePath());

        File fileDir=getFilesDir();
        Log.e("tag", "fileDir=" + fileDir.getAbsolutePath());

        ClassLoader classLoader = getClassLoader();

//        while (classLoader != null) {
//            Log.e("tag", classLoader.toString());
//            classLoader = classLoader.getParent();
//        }

//        Class clazz=BaseDexClassLoader.class;
        try {
            //1 DexPathList 对象
            Object object = getFieldObject(classLoader, "dalvik.system.BaseDexClassLoader", "pathList");

            //2 获取上下文类加载器
            ClassLoader contextClassLoader = (ClassLoader) getFieldObject(object, "dalvik.system.DexPathList", "definingContext");

            Object nativeLibraryPathElements = getFieldObject(object, "dalvik.system.DexPathList", "nativeLibraryPathElements");
            List<File> nativeLibraryDirectories = (List<File>) getFieldObject(object, "dalvik.system.DexPathList", "nativeLibraryDirectories");
            List<File> systemNativeLibraryDirectories = (List<File>) getFieldObject(object, "dalvik.system.DexPathList", "systemNativeLibraryDirectories");

            Log.e("tag", "pathList=" + object);
            Log.e("tag", "contextClassLoader=" + contextClassLoader);
            Log.e("tag", "nativeLibraryPathElements=" + nativeLibraryPathElements);

            for (File file : nativeLibraryDirectories) {
                Log.e("tag", "nativeLibraryDirectories=" + file.getAbsolutePath());
            }

            for (File file : systemNativeLibraryDirectories) {
                Log.e("tag", "systemNativeLibraryDirectories=" + file.getAbsolutePath());
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private Object getFieldObject(Object object, String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        return field.get(object);
    }


}
