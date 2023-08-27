package smf.icdada.Deprecated;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

@java.lang.Deprecated
public class Deprecated {
    protected static void init(String Deprecated) {
        try {
            String stringUrl = System.getProperty("user.dir") + File.separator + "key.json";
            Path keyPath = Paths.get(stringUrl);
            if (Files.exists(keyPath)) {
                byte[] keyData = Files.readAllBytes(keyPath);
                String key = new String(keyData);
                /*Gson gson = new Gson();
                Type mapType = new TypeToken<Map<String, String>>() {
                }.getType();
                keyMap = gson.fromJson(key, mapType);*/
            } else {
                System.out.println("\033[31m" + "Key null, please check out the " + System.getProperty("user.dir") + File.separator + "key.json exist" + "\033[0m");
                Scanner scanner = new Scanner(System.in);
                scanner.nextLine();
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
