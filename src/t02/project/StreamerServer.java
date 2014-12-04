package t02.project;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.util.Scanner;

/**
 * Created by nada on 04/12/14.
 */

public class StreamerServer {
    final static int SOCKET = 9876;
    final static String ENCODER = System.getProperty("user.dir") + "/src/t02/batch/encode.bat";

    public static void main(String[] args) throws IOException, UnsupportedAudioFileException, InterruptedException {
        // Get sound file
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter audio file path(or press enter to skip): ");
        // input file
        String audioFile = sc.nextLine();
        if (audioFile.equals("")) audioFile = System.getProperty("user.dir") + "/src/t02/audio/audio.wav";

        // input file audio format (=wav if default)
        String audioFileFormat = audioFile.substring(audioFile.lastIndexOf(".") + 1);

        // input audio bitrate
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(audioFile));
        AudioFormat format = stream.getFormat();
        float bitrate = (format.getSampleSizeInBits() * format.getSampleRate() * format.getChannels()) / 4;
        System.out.println(bitrate);
        // params of batch file
        String[] param = new String[]{ENCODER, audioFile, audioFileFormat, ""+bitrate};

        // execute batch file
        ProcessBuilder processBuilder = new ProcessBuilder(param);
        processBuilder.directory(new File(System.getProperty("user.dir") + "/src/t02/audio"));
        Process process = processBuilder.start();
        process.waitFor();

        // Open connection
        DatagramSocket serverSocket = new DatagramSocket(SOCKET);


    }
}
