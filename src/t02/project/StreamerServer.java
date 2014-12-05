package t02.project;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by nada on 04/12/14.
 */

public class StreamerServer {
    private final static int PORT = 9876;
    private final static String ENCODER = System.getProperty("user.dir") + "/src/t02/batch/encode.bat";

    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    private BatchEvent encodeBatch;

    private FileEvent sendFile;

    private String originalFilePath;
    private float originalBitrate;
    private String orignalAudioFormat;


    public StreamerServer() throws SocketException {
        this.encodeBatch = new BatchEvent();
        this.sendFile = new FileEvent();
    }


    public void connect() throws IOException, UnsupportedAudioFileException, InterruptedException {
        // establish connection
        this.establishConnection();

        // get original file ready
        this.setOriginalFile();

        // get batch file ready and execute
        this.setEncodeBatch();

        // get sendFile ready
        this.setSendFile();

        // send file
        this.sendFileOverConnection();

        // close connection
        this.socket.close();
    }

    private void setOriginalFile() throws IOException, UnsupportedAudioFileException {
        // input original file path
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter audio file path(or press enter to skip): ");
        this.originalFilePath = rdr.readLine();
        rdr.close();
        if(this.originalFilePath.equals("")) {
            this.originalFilePath = System.getProperty("user.dir") + "/src/t02/audio/audio.wav";
        }

        // input file audio format (=wav if default)
        System.out.println("Setting original audio format...");
        this.orignalAudioFormat = this.originalFilePath.substring(this.originalFilePath.lastIndexOf(".") + 1);

        // input audio bitrate
        System.out.println("Calculating original audio bitrate...");
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(this.originalFilePath));
        AudioFormat format = stream.getFormat();
        stream.close();
        this.originalBitrate = format.getSampleSizeInBits() * format.getSampleRate() * format.getChannels();
    }

    private void setEncodeBatch() throws IOException, InterruptedException {
        this.encodeBatch.setParam(
                new String[]{
                        ENCODER,
                        this.originalFilePath,
                        this.orignalAudioFormat,
                        (int) (this.originalBitrate / 4000) + "k"
                }
        );
        this.encodeBatch.setDirectory(System.getProperty("user.dir")+"/src/t02/audio");
        System.out.println("Executing batch file...");
        this.encodeBatch.execute();
    }

    private void setSendFile() throws IOException, UnsupportedAudioFileException {
        System.out.println("Preparing the encoded audio to be sent...");
        // set destination path of send file
        this.sendFile.setDestinationPath("/src/t02/audio/");
        this.sendFile.setFilename("received.mp3");
        this.sendFile.setSourcePath(System.getProperty("user.dir") + "/src/t02/audio/encoded.mp3");
        File file = new File(this.sendFile.getSourcePath());
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read,
                        fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                this.sendFile.setFileSize(len);
                this.sendFile.setFileData(fileBytes);
                this.sendFile.setStatus("Success");
                diStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                this.sendFile.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            this.sendFile.setStatus("Error");
        }
    }

    private void establishConnection() throws IOException {
        System.out.println("Establishing connection with client...");
        this.socket = new DatagramSocket(PORT);
        while(true){
            DatagramPacket receivePacket = new DatagramPacket(new byte[2], 2);
            this.socket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            if (sentence.equalsIgnoreCase("ok")) {
                // Get client info
                this.clientAddress = receivePacket.getAddress();
                this.clientPort = receivePacket.getPort();
                System.out.println("Client " + this.clientAddress + " connected...");
                break;
            }
        }
    }

    private void sendFileOverConnection() throws IOException {
        System.out.println("Sending audio file data to client...");
//        // send original format
//        byte[] data = this.orignalAudioFormat.getBytes();
//        DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.clientAddress, this.clientPort);
//        this.socket.send(sendPacket);
//
        // send original bitrate
        byte[] data = new String(""+(int)this.originalBitrate/1000).getBytes();
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.clientAddress, this.clientPort);
        this.socket.send(sendPacket);

        System.out.println("Sending audio file to client...");
        // convert file data to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(outputStream);
        os.writeObject(this.sendFile);
        data = outputStream.toByteArray();
        os.close();

        // send data
        sendPacket = new DatagramPacket(data, data.length, this.clientAddress, this.clientPort);
        this.socket.send(sendPacket);

        System.out.println("File sent!");
    }

    public static void main(String[] args) {
        StreamerServer server = null;
        try {
            server = new StreamerServer();
            server.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
