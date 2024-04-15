package org.mossmc.mosscg.MossFrpBackend.File;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendInfo;

public class FileDependency {
    public static void initDependency() {
        checkDependencyDirExist("./MossFrp");
        checkDependencyDirExist("./MossFrp/Dependency");
        loadDependencyDir();
    }

    public static void loadDependencyDir() {
        try {
            URL url = FileCheck.class.getClassLoader().getResource("dependency/");
            assert url != null;
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            JarFile jarFile = connection.getJarFile();
            Enumeration<JarEntry> jarEntry = jarFile.entries();
            while (jarEntry.hasMoreElements()) {
                JarEntry entry = jarEntry.nextElement();
                if (entry.getName().startsWith("dependency/") && !entry.isDirectory()) {
                    String name = entry.getName().replace("dependency/", "");
                    loadDependency("./MossFrp/Dependency/" + name, "dependency/" + name);
                }
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    public static void loadDependency(String path, String packPath) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                InputStream input = FileCheck.class.getClassLoader().getResourceAsStream(packPath);
                assert input != null;
                Files.copy(input, file.toPath());
                sendInfo("已创建缺失的依赖："+path);
            }
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
            addURL.invoke(ClassLoader.getSystemClassLoader(), file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkDependencyDirExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }
}
