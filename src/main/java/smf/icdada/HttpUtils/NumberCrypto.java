package smf.icdada.HttpUtils;

public class NumberCrypto {
    public static int encrypt(int DecryptNumber) {
        return ((DecryptNumber ^ 13) << 13) | (DecryptNumber >>> 19);
    }

    public static int decrypt(int EncryptNumber) {
        return ((EncryptNumber >>> 13) ^ 13) | (EncryptNumber << 19);
    }
}
