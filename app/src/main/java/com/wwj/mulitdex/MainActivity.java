package com.wwj.mulitdex;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    public static final String dexFileName = "out.dex";
    public static final String optimization = "optimization";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getApkDir();
//        print();


        findViewById(R.id.btnError).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Test.divide(30, 10);
            }
        });

        findViewById(R.id.btnBugFix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fixBug();
                moveFileToFlashDisk();
            }
        });

        //明确的代码实现思路,接下来我们自己尝试实现这个功能
        //1  修复有BUG的类
        //2  把有BUG的字节码文件转化为dex
        //3  把dex文件存放到内置卡中
        //4  把dex文件加载到内存中,获取它的dexElements
        //5  获取去当前工程的dexElements,把4步的dexElements插入到当前工程dexElements数组的最前面


    }

    /**
     * 移动文件到内置卡
     */
    private void moveFileToFlashDisk() {

        File sdCardFile = new File(Environment.getExternalStorageDirectory(), dexFileName);

        File cacheDir = getCacheDir();
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


    private Object getFieldObject(Object object, String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class clazz = Class.forName(className);
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private void print() {
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
            Object dexElements = getFieldObject(object, "dalvik.system.DexPathList", "dexElements");

            List<File> nativeLibraryDirectories = (List<File>) getFieldObject(object, "dalvik.system.DexPathList", "nativeLibraryDirectories");
            List<File> systemNativeLibraryDirectories = (List<File>) getFieldObject(object, "dalvik.system.DexPathList", "systemNativeLibraryDirectories");

            Log.e("tag", "dexElements=" + dexElements);
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

    private void getApkDir() {
        File appDir = getDir("test", MODE_PRIVATE);
        Log.e("tag", "appDir=" + appDir.getAbsolutePath());

        File cacheDir = getCacheDir();
        Log.e("tag", "cacheDir=" + cacheDir.getAbsolutePath());

        File fileDir = getFilesDir();
        Log.e("tag", "fileDir=" + fileDir.getAbsolutePath());
    }


}
