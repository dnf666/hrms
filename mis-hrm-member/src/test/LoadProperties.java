import java.io.*;
import java.util.Properties;

public class LoadProperties {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream is = new FileInputStream("ValidationMessages.properties");
        byte[] bytes = new byte[10240];
        is.read(bytes);
        String s = new String(bytes, "ISO-8859-1");
        System.out.println(s);
        /*properties.load(is);
        System.out.println("email: " + properties.getProperty("email"));
        String str = properties.getProperty("email");
        System.out.println(str);
        byte[] bytes = str.getBytes();
        for (byte aByte : bytes) {
            System.out.print(aByte + " ");
        }
        System.out.println();
        String str2 = new String(str.getBytes(),"UTF-8");
        byte[] bytes1 = str2.getBytes();
        for (byte b : bytes1) {
            System.out.print(b + " ");
        }
        System.out.println(str2);*/
    }
}
