package t02.project;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by nada on 04/12/14.
 */
public class StreamerClient {
    final static String LOCALHOST = "localhost";

    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(LOCALHOST);

        //TODO Establish connection with server

        //TODO Receive packets from server

        //TODO decode received audio


    }
}
