package es.udc.redes.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class WebServer {
    
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Format: es.udc.redes.webserver.WebServer <port>");
            System.exit(-1);
        }
        int port = Integer.parseInt(args[0]);
        Socket socket = null;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(300000);
            while (true) {
                socket = serverSocket.accept();
                es.udc.redes.webserver.ServerThread serverThread = new ServerThread(socket);
                serverThread.start();
            }

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally{
            //Close the socket
            socket.close();
        }
    }
    
}
