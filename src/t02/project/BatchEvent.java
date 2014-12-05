package t02.project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nada on 05/12/14.
 */
public class BatchEvent {
    private String[] param;
    private String directory;

    public BatchEvent() {

    }

    public void setParam(String[] param) {
        this.param = param;
//        for (int i = 0; i < param.length; i++) {
//            System.out.println(param[i]);
//        }
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void execute() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(this.param);
        processBuilder.directory(new File(this.directory));
        Process process = processBuilder.start();
        process.waitFor();


//        InputStream in = process.getInputStream();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        int c;
//        while((c = in.read()) != -1)
//        {
//            baos.write(c);
//        }
//
//        String response = new String(baos.toByteArray());
//        System.out.println("Response From Exe : "+response);
    }
}
