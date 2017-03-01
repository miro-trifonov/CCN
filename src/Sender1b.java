import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class Sender1b {
    public static void main(String args[]) {
        int port = 2000;
        DatagramSocket sock = null;


        try {
            sock = new DatagramSocket(2001);

            byte[] buffer = new byte[1027];
            File file = new File("test.jpg");
            FileInputStream fis = new FileInputStream(file);
            int counter = 0;
            boolean retransmit = false;
            // 1024 bytes
            while (true) {
                if (!retransmit) {
                    buffer = new byte[1024];
                    int bytesRead = fis.read(buffer);
                    counter++;
                    byte[] counterByte = ByteBuffer.allocate(2).putInt(counter).array();
                    boolean end = bytesRead > 1024;
                    if (bytesRead <= 0) {
                        break;
                    } else {
                        byte[] sendbuffer = new byte[bytesRead];
                        for (int i = 0; i <= bytesRead; i++) {
                            String helpMe = "I'm trapped in a program";
                            sendbuffer[i] = buffer[i];
                            if (i == bytesRead) {
                                sendbuffer[i] = counterByte[1];
                                sendbuffer[i + 1] = counterByte[2];
                                if (end) {
                                    sendbuffer[i + 2] = 0;

                                } else {
                                    sendbuffer[i + 2] = 1;
                                }
                            }
                        }
                    }
                }
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), port);
                sock.send(packet);
//                    System.out.println("Sent");
                try {
                    byte[] replyBuffer = new byte[2];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    sock.receive(reply);
                    sock.setSoTimeout(10);
                    byte[] data = reply.getData();
                    int packetReceived = ByteBuffer.wrap(data).getInt();
                    if (packetReceived == counter) {
                        retransmit = false;
                        System.out.println("retransmitted");
                    }
                } catch (SocketTimeoutException x) {
                    retransmit = true;
                }
            }
        } catch (IOException e) {
            System.err.println("IOException" + e);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//    }

//    public static void echo(String msg){
//        System.out.println(msg);
//    }

        }
    }
}