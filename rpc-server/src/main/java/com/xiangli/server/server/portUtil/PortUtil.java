package com.xiangli.server.server.portUtil;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/08 10:22
 */

@Slf4j
public class PortUtil {
    public static int findAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            log.info("Server: Find an available port: " + serverSocket.getLocalPort());
            return serverSocket.getLocalPort();  // Automatically chooses an available port
        } catch (IOException e) {
            throw new RuntimeException("Failed to find an available port", e);
        }
    }
}