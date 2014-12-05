package t02.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by nada on 04/12/14.
 */
public class StreamerClient {
    final static String LOCALHOST = "localhost";

    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(LOCALHOST);

        //TODO Establish connection with server
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        BufferedReader inputCommand = new BufferedReader(new InputStreamReader(System.in));
        String sentence = inputCommand.readLine();
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);
        clientSocket.close();

        //TODO Receive packets from server

        //TODO decode received audio


    }
}
