package t02.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 * Created by nada on 04/12/14.
 */
public class StreamerClient {
    final static String SERVERADDRESS = "localhost";
    private final static int PORT = 9876;
    private final static String DECODER = System.getProperty("user.dir") + "/src/t02/batch/decode.bat";

    private DatagramSocket socket;
    private InetAddress serverAddress;

    private BatchEvent decodeBatch;

    private FileEvent receivedFile;

    public StreamerClient(){
        this.decodeBatch = new BatchEvent();
        this.receivedFile = new FileEvent();
    }

    public void connect() throws IOException {
        // establish connection
        this.establishConnection();


    }

    public void establishConnection() throws IOException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Do you wish to connect to server and receive audio?(ok/no)");
        String response = rdr.readLine();
        rdr.close();
        if (response.equalsIgnoreCase("ok")){
            this.socket = new DatagramSocket();
            this.serverAddress = InetAddress.getByName("localhost");
        } else {
            System.out.println("Okay. Bye!");
            System.exit(0);
        }
    }


    public static void main(String[] args) {
        StreamerClient client = null;
        try {
            client = new StreamerClient();
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
