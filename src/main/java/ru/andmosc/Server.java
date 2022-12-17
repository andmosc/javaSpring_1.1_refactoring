package ru.andmosc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final int PORT;
    private final ExecutorService threadPool;

    public Server(int PORT, int poolSize) {
        this.PORT = PORT;
        threadPool = Executors.newFixedThreadPool(poolSize);
        startServer();
    }

    private void startServer() {
        try (final ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                final Socket socket = serverSocket.accept();
                ThreadServer threadServer = new ThreadServer(socket);
                threadPool.execute(threadServer);
            }
        } catch (IOException e) {
            try {
                if (!threadPool.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException exception) {
                threadPool.shutdownNow();
            }
        }
    }
}
