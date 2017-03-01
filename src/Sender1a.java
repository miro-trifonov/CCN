import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class Sender1a {
    public static void main(String args[]) {
        int port = 2000;
        DatagramSocket sock = null;

        try {
            sock = new DatagramSocket(2001);

            byte[] buffer = new byte[1024];
            File file = new File("test.jpg");
            FileInputStream fis = new FileInputStream(file);
            int counter = 0;
            // 1024 bytes
            while (true) {
                counter++;
                int bytesRead = fis.read(buffer);
                byte[] counterByte = ByteBuffer.allocate(2).putInt(counter).array();
                boolean end = bytesRead > 1024;
                if (bytesRead > 0) {
                    byte[] sendbuffer = new byte[bytesRead];
                    for (int i = 0; i <= bytesRead; i++) {
                        sendbuffer[i] = buffer[i];
                        if (i == bytesRead) ;
                        sendbuffer[i] = counterByte[1];
                        sendbuffer[i + 1] = counterByte[2];
                        if (end){
                            sendbuffer[i + 2] = 0;

                        }else {
                            sendbuffer[i + 2] = 1;
                        }

                    }
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
                    sock.send(packet);
                    buffer = new byte[1024];
//                    System.out.println("Sent");
                    Thread.sleep(10);
                } else {
                    break;
                }


            }
        } catch (IOException e) {
            System.err.println("IOException" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//    public static void echo(String msg){
//        System.out.println(msg);
//    }

}
