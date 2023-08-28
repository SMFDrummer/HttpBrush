package smf.icdada;

import com.alibaba.fastjson2.JSON;

import java.util.Scanner;

public class HttpBrushTest {
    private static final Print print = new Print();
    public static int encrypt(int key) {
        return ((key ^ 13) << 13) | (key >>> 19);
    }

    public static int decrypt(int key) {
        return ((key >>> 13) ^ 13) | (key << 19);
    }
    //{"i":"V202","r":0,"t":{"ci":"93","cv":"3.1.7","di":"","li":"","oi":"109208X37542538","pi":"","r":"703740081","s":"a3fa45b7cfc5c85201dc139bab0ee6fb","ui":""}}
    //{"i":"V303","r":0,"t":{"al":[{"id":10868,"abi":0,"type":1,"config_version":1}],"ci":"93","cs":"0","pack":"com.popcap.pvz2cthdbk","pi":"728764199","sk":"c172894a5a2d4856965300ce1d95a962","ui":"728764199","v":"3.1.7"}}
    //{"i":"V876","r":0,"t":{"code":"CCC","pi":"AAA","sk":"BBB","star":"50","ui":"AAA"}}

    public static void main(String[] args) {
        System.out.println(RequestType.OI_FAKE.getRequestBody(109208,37542538));
        System.out.println(RequestType.ANNI_IN.getRequestBody("AAA","BBB"));
        System.out.println(RequestType.ANNI_BRUSH.getRequestBody("CCC","AAA","BBB"));
        int key, a;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Created By SMFDrummer");
        while (true) {
            System.out.println("\nInput Key:");
            key = scanner.nextInt();
            System.out.println("\n[1]decrypt");
            System.out.println("[2]encrypt");
            System.out.println("\nswitch:");
            a = scanner.nextInt();
            switch (a) {
                case 1 -> System.out.println("\nans:" + decrypt(key));
                case 2 -> System.out.println("\nans:" + encrypt(key));
                default -> {}
            }
        }
    }

}
