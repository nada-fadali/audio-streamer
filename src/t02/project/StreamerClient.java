package t02.project;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javazoom.jl.player.*;

/**
 * Created by nada on 04/12/14.
 */
public class StreamerClient {
    private final static String ADDRESS = "localhost";
    private final static int PORT = 9876;
    private final static String DECODER = System.getProperty("user.dir") + "/src/t02/batch/decode.bat";

    private DatagramSocket socket;

    private BatchEvent decodeBatch;

    private FileEvent receivedFile;

//    private String originalAudioFormat;
    private String originalAudioBitrate;

    public StreamerClient(){
        this.decodeBatch = new BatchEvent();
        this.receivedFile = new FileEvent();
    }

    public void connect() throws IOException, ClassNotFoundException, InterruptedException, UnsupportedAudioFileException {
        // establish connection
        this.establishConnection();

//        // receive original audio format from server
//        this.receiveOriginalAudioFormat();
//
        // receive original audio bitrate from server
        this.receiveOriginalBitrate();

        // receive audio data from server
        this.receiveAudioData();

        // save received audio to hard disk
        this.saveReceivedFile();

        // decode audio
        this.setDecodeBatch();

        // close connection
        this.socket.close();

        // play sound
        System.out.println("Do you wish to play file?(ok/no): ");
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        String response = rdr.readLine();
        rdr.close();
        if(response.equalsIgnoreCase("ok"))
            this.playAudioFile();

        System.out.println("Bye!");
    }

    private void establishConnection() throws IOException {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Do you wish to connect to server and receive audio?(ok/no)");
        String response = rdr.readLine();
        if (response.equalsIgnoreCase("ok")){
            System.out.println("Connecting to Server...");
            this.socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(ADDRESS);
            byte[] sendData = response.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
            this.socket.send(sendPacket);
            System.out.println("Connected!");
        } else {
            System.out.println("Okay. Bye!");
            System.exit(0);
        }
    }

//    private void receiveOriginalAudioFormat() throws IOException {
//        System.out.println("Receiving original audio format from Server...");
//        byte[] receiveData = new byte[10];
//        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//        this.socket.receive(receivePacket);
//        this.originalAudioFormat = new String(receivePacket.getData());
//        System.out.println("audio format: " + this.originalAudioFormat);
//    }

    private void receiveOriginalBitrate() throws IOException {
        System.out.println("Receiving original audio bitrate...");
        byte[] receiveData = new byte[10];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        this.originalAudioBitrate = new String(receivePacket.getData());
        System.out.println("original audio bitrate: " + this.originalAudioBitrate);
    }

    private void receiveAudioData() throws IOException, ClassNotFoundException {
        System.out.println("Receiving encoded audio data...");

        byte[] incomingData = new byte[1024 * 1000 * 50];
        while (true) {
            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            this.socket.receive(incomingPacket);
            byte[] data = incomingPacket.getData();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            ObjectInputStream is = new ObjectInputStream(in);
            this.receivedFile = (FileEvent) is.readObject();
            if (this.receivedFile.getStatus().equalsIgnoreCase("Error")) {
                System.out.println("Some issue happened while packing the data @ client side");
                System.exit(0);
            }
            break;
        }
    }

    private void saveReceivedFile() throws IOException {
        System.out.println("Saving to drive...");
        String output = System.getProperty("user.dir")+ this.receivedFile.getDestinationPath() + this.receivedFile.getFilename();
        if (!new File(System.getProperty("user.dir")+this.receivedFile.getDestinationPath()).exists()) {
            new File(System.getProperty("user.dir")+this.receivedFile.getDestinationPath()).mkdirs();
        }
        File dstFile = new File(output);
        FileOutputStream fileOutputStream = null;
        fileOutputStream = new FileOutputStream(dstFile);
        fileOutputStream.write(this.receivedFile.getFileData());
        fileOutputStream.flush();
        fileOutputStream.close();
        System.out.println("Received file : " + output + " is successfully saved!");
    }


    private void setDecodeBatch() throws IOException, InterruptedException {
        this.decodeBatch.setParam(
                new String[]{
                        DECODER,
                        this.originalAudioBitrate.replaceAll("[^0-9]+", "") + "k"
                }
        );
        this.decodeBatch.setDirectory(System.getProperty("user.dir") + "/src/t02/audio");
        System.out.println("Executing batch file...");
        this.decodeBatch.execute();

        System.out.println("Done decoding!");
    }

    private void playAudioFile(){
        try{
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/t02/audio/decoded.mp3");
            Player playMP3 = new Player(fis);

            playMP3.play();

        }catch(Exception e){System.out.println(e);}
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
