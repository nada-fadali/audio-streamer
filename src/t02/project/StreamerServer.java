package t02.project;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Created by nada on 04/12/14.
 */

public class StreamerServer {
    final static int SOCKET = 9876;
    final static String ENCODER = System.getProperty("user.dir") + "/src/t02/batch/encode.bat";

    private DatagramSocket socket;

    private BatchEvent encodeBatch;

    private FileEvent sendFile;

    private String originalFilePath;
    private float originalBitrate;
    private String orignalAudioFormat;


    public StreamerServer() throws SocketException {
        this.encodeBatch = new BatchEvent();
        this.sendFile = new FileEvent();
        this.socket = new DatagramSocket(SOCKET);
    }


    public void run() throws IOException, UnsupportedAudioFileException, InterruptedException {
        // get original file ready
        this.setOriginalFile();

        // get batch file ready and execute
        this.setEncodeBatch();

        // get sendFile ready
        this.setSendFile();







//        // Packtize encoded audio
//
//        // Establish connection with client
//        DatagramSocket serverSocket = new DatagramSocket(SOCKET);
//        DatagramPacket receivePacket = new DatagramPacket(new byte[4], 4);
//        while(true) {
//            serverSocket.receive(receivePacket);
//            String sentence = new String(receivePacket.getData());
//            if (sentence.equals("SEND")) break;
//        }
//
//        // Get client info
//        //InetAddress IPAddress = receivePacket.getAddress();
//        //int port = receivePacket.getPort();
//
//        //send to client
//        while(true) {
//            serverSocket.receive(receivePacket);
//            System.out.println(new String(receivePacket.getData()));
//        }
    }

    private void setOriginalFile() throws IOException, UnsupportedAudioFileException {
        // input original file path
        BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter audio file path(or press enter to skip): ");
        this.originalFilePath = rdr.readLine();
        if(this.originalFilePath.equals("")) {
            this.originalFilePath = System.getProperty("user.dir") + "/src/t02/audio/audio.wav";
        }

        // input file audio format (=wav if default)
        this.orignalAudioFormat = this.originalFilePath.substring(this.originalFilePath.lastIndexOf(".") + 1);

        // input audio bitrate
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
                        "" + (this.originalBitrate/4)
                }
        );
        this.encodeBatch.setDirectory(System.getProperty("user.dir")+"/src/t02/audio");
        this.encodeBatch.execute();
    }

    private void setSendFile() throws IOException, UnsupportedAudioFileException {
        // set destination path of send file
        this.sendFile.setDestinationPath("/src/t02/audio");


        this.sendFile.setFilename("output.mp3");
        this.sendFile.setSourcePath(System.getProperty("user.dir") + "/src/t02/audio/output.mp3");
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
            } catch (Exception e) {
                e.printStackTrace();
                this.sendFile.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            this.sendFile.setStatus("Error");
        }
    }

    public static void main(String[] args) {
        StreamerServer server = null;
        try {
            server = new StreamerServer();
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
