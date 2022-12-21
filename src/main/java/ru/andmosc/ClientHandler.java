package ru.andmosc;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final static List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png"
            , "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html"
            , "/classic.html", "/events.html", "/events.js");
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                socket;
                final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            Request request = createRequest(in, out);
            Handler handler = Server.getHandlers().get(request.getMethod()).get(request.getPath());
            if (handler == null) {
                out.write(errResponse().getBytes());
                out.flush();
            } else {
                handler.handle(request, out);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Request createRequest(BufferedReader in, BufferedOutputStream out) throws IOException {

        final String requestLine = in.readLine();
        final String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            socket.close();
        }
        final String method = parts[0];
        final String path = parts[1];

        if (!validPaths.contains(path)) {
            out.write(errResponse().getBytes());
            out.flush();
        }

        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            int indexOf = line.indexOf(":");
            String name = line.substring(0, indexOf);
            String value = line.substring(indexOf + 2);
            headers.put(name, value);
        }

        return new Request(method, path, headers, socket.getInputStream());
    }

    public static void responseServer(Request request, BufferedOutputStream out) throws IOException {
        final Path filePath = Path.of(".", "public", request.getPath());
        final String mimeType = Files.probeContentType(filePath);

        if (request.getPath().equals("/classic.html")) {
            classicResponse(out, filePath, mimeType);
            return;
        }

        final long length = Files.size(filePath);
        out.write(okResponse(mimeType, length).getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    private static void classicResponse(BufferedOutputStream out, Path filePath, String mimeType) throws IOException {
        final String template = Files.readString(filePath);
        final byte[] content = template.replace(
                "{time}",
                LocalDateTime.now().toString()
        ).getBytes();
        out.write(okResponse(mimeType, content.length).getBytes());
        out.write(content);
        out.flush();
    }

    private static String errResponse() {
        return "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
    }

    private static String okResponse(String mimeType, long length) {
        return "HTTP/1.1 200 OK\r\n" +
                "Content-Type: " + mimeType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
    }

    public static List<String> getValidPaths() {
        return validPaths;
    }
}
