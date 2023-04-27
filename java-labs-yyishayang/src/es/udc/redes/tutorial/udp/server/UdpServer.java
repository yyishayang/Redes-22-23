package es.udc.redes.tutorial.udp.server;

import java.net.*;

/**
 * Implements a UDP echo server.
 */
public class UdpServer {

    public static void main(String argv[]) {
        if (argv.length != 1) {
            System.err.println("Format: es.udc.redes.tutorial.udp.server.UdpServer <port_number>");
            System.exit(-1);
        }
        DatagramSocket server = null;
        try {
            // Create a server socket
            int puerto = Integer.parseInt(argv[0]);
            server = new DatagramSocket(puerto);
            // Set maximum timeout to 300 secs
            server.setSoTimeout(300000);
            while (true) {
                // Prepare datagram for reception
                byte array[] = new byte[1024];
                DatagramPacket datagrecep = new DatagramPacket(array, array.length);
                
                // Receive the message
                server.receive(datagrecep);
                System.out.println("SERVER: Received "+
                        new String(datagrecep.getData(), 0, datagrecep.getLength()) +" from "
                        +datagrecep.getAddress().toString()
                        +":" +datagrecep.getPort());

                // Prepare datagram to send response
                DatagramPacket datagsend = new DatagramPacket(array, datagrecep.getLength(), datagrecep.getAddress(), datagrecep.getPort());
                System.out.println("SERVER: Sending: "
                        + new String(datagsend.getData(), 0, datagsend.getLength()) + " to "
                        + datagsend.getAddress().toString() + ":"
                        + datagsend.getPort());
                // Send response
                server.send(datagsend);
            }
          
        // Uncomment next catch clause after implementing the logic
        } catch (SocketTimeoutException e) {
            System.err.println("No requests received in 300 secs ");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
        // Close the socket
            server.close();

        }
    }
}
