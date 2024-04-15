package org.mossmc.mosscg.MossFrpBackend.File;

import org.mossmc.mosscg.MossFrpBackend.BasicInfo;
import org.mossmc.mosscg.MossFrpBackend.Enums;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class FileConfig {
    public static void checkConfig() {
        File file = new File("./MossFrp/config.yml");
        if (!file.exists()) {
            String template = getTemplate();
            assert template != null;
            if (BasicInfo.getRunType.equals(Enums.runType.NODE)) {
                sendInfo("请输入节点编号：");
                String nodeName = readInput();
                sendInfo("请输入节点校验码：");
                String nodeAuth = readInput();
                template = template
                        .replace("[name]",nodeName)
                        .replace("[auth]",nodeAuth);
                if (BasicInfo.getSystemType.equals(Enums.systemType.LINUX)) {
                    sendInfo("请输入网卡名称（如eth0/ens33等）：");
                    String ethName = readInput();
                    template = template.replace("[eth]",ethName);
                }
            }
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath()),StandardCharsets.UTF_8));
                writer.write(template);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                sendException(e);
                sendError("无法写入Config配置文件！");
            }
        }
    }

    public static String getTemplate() {
        try {
            InputStream inputStream;
            if (BasicInfo.getRunType.equals(Enums.runType.NODE)) {
                inputStream = FileCheck.class.getClassLoader().getResourceAsStream("config-node.yml");
            } else {
                inputStream = FileCheck.class.getClassLoader().getResourceAsStream("config-center.yml");
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int length;
            byte[] buffer = new byte[1024];
            assert inputStream != null;
            while((length = inputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer,0,length);
            }
            byteArrayOutputStream.close();
            inputStream.close();
            byte[] result = byteArrayOutputStream.toByteArray();
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            sendException(e);
            sendWarn("无法读取配置文件模板！");
            return null;
        }
    }

    public static String readInput() {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            sendException(e);
            return "null";
        }
    }

    public static void loadConfigYaml() {
        Yaml yaml = new Yaml();
        FileInputStream input = null;
        try {
            input = new FileInputStream("./MossFrp/config.yml");
        } catch (FileNotFoundException e) {
            sendException(e);
        }
        BasicInfo.getConfigYaml = yaml.loadAs(input, Map.class);
    }
}
