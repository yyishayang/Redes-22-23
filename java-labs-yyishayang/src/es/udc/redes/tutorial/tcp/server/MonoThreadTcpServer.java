package es.udc.redes.tutorial.tcp.server;

import java.net.*;
import java.io.*;

/**
 * MonoThread TCP echo server.
 */
public class MonoThreadTcpServer {

    public static void main(String argv[]) throws IOException {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.tcp.server.MonoThreadTcpServer <port>");
            System.exit(-1);
        }
        int port = Integer.parseInt(argv[0]);
        ServerSocket serverSocket = null;
        try {
            // Create a server socket
            serverSocket = new ServerSocket(port);
            // Set a timeout of 300 secs
            serverSocket.setSoTimeout(300000);

            while (true) {
                // Wait for connections
                Socket socket = serverSocket.accept();
                // Set the input channel
                BufferedReader input = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                // Set the output channel
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                // Receive the client message
                String message = input.readLine();
                System.out.println("SERVER: Received " + message
                        + " from " + socket.getInetAddress().toString()
                        + ":" + socket.getPort());
                // Send response to the client
                output.println(message);
                System.out.println("SERVER: Sending " + message +
                        " to " + socket.getInetAddress().toString() +
                        ":" + socket.getPort());
                // Close the streams
                output.close();
                input.close();
                socket.close();
            }
        // Uncomment next catch clause after implementing the logic            
        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
	        //Close the socket
            serverSocket.close();
        }
    }
}
