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
            short counter = 0;
            // 1024 bytes
            while (true) {
                counter++;
                int bytesRead = fis.read(buffer);
                byte[] counterByte = ByteBuffer.allocate(2).putShort(counter).array();
                boolean end = bytesRead < 1024;
                if (bytesRead > 0) {
                    byte[] sendBuffer = new byte[bytesRead + 3];
                    for (int i = 0; i < bytesRead; i++) {
                        if (i + 1 == bytesRead) {
                            sendBuffer[i + 1] = counterByte[0];
                            sendBuffer[i + 2] = counterByte[1];
                            if (end) {
                                sendBuffer[i + 3] = 0;

                            } else {
                                sendBuffer[i + 3] = 1;
                            }
                        }
                        sendBuffer[i] = buffer[i];

                    }
                    System.out.println(sendBuffer.length);
                    DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), port);
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
