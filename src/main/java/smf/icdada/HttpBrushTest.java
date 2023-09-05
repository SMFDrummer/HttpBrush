package smf.icdada;

import java.util.Scanner;

public class HttpBrushTest {
    private static final Print print = new Print();

    public static int encrypt(int key) {
        return ((key ^ 13) << 13) | (key >>> 19);
    }

    public static int decrypt(int key) {
        return ((key >>> 13) ^ 13) | (key << 19);
    }

    public static void main(String[] args) {


        int key, a;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Created By SMFDrummer");
        while (true) {
            try {
                System.out.println("\nInput Key:");
                key = scanner.nextInt();
                System.out.println("\n[1]decrypt");
                System.out.println("[2]encrypt");
                System.out.println("\nswitch:");
                a = scanner.nextInt();
                switch (a) {
                    case 1 -> System.out.println("\nans:" + decrypt(key));
                    case 2 -> System.out.println("\nans:" + encrypt(key));
                    default -> {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
