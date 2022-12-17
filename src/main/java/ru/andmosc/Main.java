package ru.andmosc;

public class Main {

    public static final int PORT = 8080;
    public static final int SIZE_POOL = 64;
    public static void main(String[] args) {
        public static void main(String[] args){
            final var server = new Server();
            // код инициализации сервера (из вашего предыдущего ДЗ)

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

            server.listen(9999);
        }

    }
}


