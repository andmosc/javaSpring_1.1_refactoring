package ru.andmosc;

public class Main {

    public static final int PORT = 8080;
    public static final int SIZE_POOL = 64;
    public static void main(String[] args) {
        new Server(PORT,SIZE_POOL);
    }
}


