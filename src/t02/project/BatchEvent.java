package t02.project;

import java.io.File;
import java.io.IOException;

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
        for (int i = 0; i < param.length; i++) {
            System.out.println(param[i]);
        }
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public void execute() throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(this.param);
        processBuilder.directory(new File(this.directory));
        Process process = processBuilder.start();
        process.waitFor();
    }
}
