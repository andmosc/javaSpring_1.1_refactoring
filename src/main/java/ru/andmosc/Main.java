package ru.andmosc;

import java.io.BufferedOutputStream;

public class Main {

    public static final int PORT = 8080;
    public static final int SIZE_POOL = 64;

    public static void main(String[] args) {

        final Server server = new Server(PORT, SIZE_POOL);
/*
        // добавление handler'ов (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
            }
        });
*/
        server.listen();
    }

}



