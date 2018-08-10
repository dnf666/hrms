import java.io.UnsupportedEncodingException;

public class StringTest {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = new String("I an 君山");
        byte[] bytes = s.getBytes();
        for (byte aByte : bytes) {
            System.out.print(aByte + " ");
        }
        System.out.println();
        byte[] bytes2 = s.getBytes("UTF-8");
        for (byte b : bytes2) {
            System.out.print(b + " ");
        }
        System.out.println();
    }
}
