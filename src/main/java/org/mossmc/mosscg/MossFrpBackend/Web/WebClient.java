package org.mossmc.mosscg.MossFrpBackend.Web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.mossmc.mosscg.MossFrpBackend.Web.Handler.HandlerClient;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.concurrent.Executors;

import static org.mossmc.mosscg.MossFrpBackend.BasicInfo.getConfig;
import static org.mossmc.mosscg.MossFrpBackend.Logger.LoggerSender.*;

public class WebClient {

    public static boolean testHttps = true;
    public static HttpServer httpServer;

    public static HttpsServer httpsServer;

    public static void load() {
        try {
            loadHttpServer();
            if (testHttps) {
                loadHttpsServer();
            }
            sendInfo("客户端WebAPI已启动");
        } catch (Exception e) {
            sendException(e);
            sendError("无法加载客户端WebAPI");
        }
    }

    public static void loadHttpsServer() throws Exception {
        httpsServer = HttpsServer.create(new InetSocketAddress(Integer.parseInt(getConfig("webClientHttpsPort"))),0);
        httpsServer.createContext("/API", new HandlerClient());
        httpsServer.setExecutor(Executors.newCachedThreadPool());

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(Files.newInputStream(Paths.get("./MossFrp/Web/server.jks")), getConfig("webKeyPassword").toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, getConfig("webKeyPassword").toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    SSLContext SSL_Context = getSSLContext();
                    SSLEngine SSL_Engine = SSL_Context.createSSLEngine();

                    params.setNeedClientAuth(false);
                    params.setCipherSuites(SSL_Engine.getEnabledCipherSuites());
                    params.setProtocols(SSL_Engine.getEnabledProtocols());

                    SSLParameters SSL_Parameters = SSL_Context.getSupportedSSLParameters();
                    params.setSSLParameters(SSL_Parameters);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        httpsServer.start();
    }

    public static void loadHttpServer() throws Exception {
        httpServer = HttpServerProvider.provider().createHttpServer(new InetSocketAddress(Integer.parseInt(getConfig("webClientPort"))),0);
        httpServer.createContext("/API", new HandlerClient());
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();
    }

    public static void stop() {
        try {
            httpServer.stop(0);
            if (testHttps) {
                httpsServer.stop(0);
            }
            sendInfo("客户端WebAPI已关闭");
        } catch (Exception e) {
            sendException(e);
            sendError("无法关闭客户端WebAPI");
        }
    }
}
