package org.mossmc.mosscg.MossFrpBackend.File;

import org.mossmc.mosscg.MossFrpBackend.Enums;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getSystemType;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.sendException;

public class FileFrp {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void copyCore(String ID) {
        File filePath = new File("./MossFrp/Frps/frp_"+ ID);
        File sourcePath = new File("./MossFrp/Frps/files/frps.exe");
        File targetPath = new File("./MossFrp/Frps/frp_"+ ID +"/frps_"+ ID +".exe");
        if (getSystemType == Enums.systemType.LINUX) {
            sourcePath = new File("./MossFrp/Frps/files/frps");
            targetPath = new File("./MossFrp/Frps/frp_"+ ID +"/frps_"+ ID);
        }
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        if (!targetPath.exists()) {
            try {
                Files.copy(sourcePath.toPath(), targetPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
                targetPath.setWritable(true);
                targetPath.setExecutable(true);
                targetPath.setReadable(true);
            } catch (IOException e) {
                sendException(e);
            }
        }
    }

    public static void writeConfig(String ID, String port, String code) {
        FileWriter fileWriter;
        int portStart = Integer.parseInt(port)+1;
        int portEnd = Integer.parseInt(port)+9;
        try {
            fileWriter = new FileWriter("./MossFrp/Frps/frp_"+ ID +"/frps.ini",false);
            fileWriter.write("[common]"+"\r\n");
            fileWriter.write("bind_port = "+port+"\r\n");
            fileWriter.write("kcp_bind_port = "+port+"\r\n");
            fileWriter.write("log_file = ./MossFrp/Frps/frp_"+ ID +"/frps.log"+"\r\n");
            fileWriter.write("log_level = info"+"\r\n");
            fileWriter.write("log_max_days = 7"+"\r\n");
            fileWriter.write("token = "+code+"\r\n");
            fileWriter.write("allow_ports = "+portStart+"-"+portEnd+"\r\n");
            fileWriter.write(" "+"\r\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            sendException(e);
        }
    }
}
