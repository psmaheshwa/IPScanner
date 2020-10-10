import java.io.IOException;

public class Shutdown {
    void down(String ip) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String command = "nc "+ip+" 4444 <<< down.bat";
        processBuilder.command("bash", "-c", command);
        try {
            Process process = processBuilder.start();
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}

