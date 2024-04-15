package org.mossmc.mosscg.MossFrpBackend.Web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.mossmc.mosscg.MossFrpBackend.Web.Handler.HandlerNode;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class WebNode {
    public static HttpServerProvider provider;
    public static HttpServer server;

    public static void load() {
        try {
            provider = HttpServerProvider.provider();
            server = provider.createHttpServer(new InetSocketAddress(Integer.parseInt(getConfig("webNodePort"))),0);
            server.createContext("/API", new HandlerNode());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            sendInfo("节点WebAPI已启动");
        } catch (Exception e) {
            sendException(e);
            sendError("无法加载节点WebAPI");
        }
    }

    public static void stop() {
        try {
            server.stop(0);
            sendInfo("节点WebAPI已关闭");
        } catch (Exception e) {
            sendException(e);
            sendError("无法关闭节点WebAPI");
        }
    }
}
