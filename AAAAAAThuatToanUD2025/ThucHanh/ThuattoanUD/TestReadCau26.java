import java.nio.file.*;
import java.nio.charset.StandardCharsets;

public class TestReadCau26 {
    public static void main(String[] args) throws Exception {
        String text = new String(Files.readAllBytes(Paths.get("outtxt1.txt")), StandardCharsets.UTF_8);
        System.out.println(text);
    }
}
