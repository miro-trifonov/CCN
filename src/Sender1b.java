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

            byte[] buffer = new byte[1024];
            File file = new File("test.jpg");
            FileInputStream fis = new FileInputStream(file);
            short counter = 0;
            boolean retransmit = false;
            byte[] sendBuffer = new byte[1027];
            // 1024 bytes
            while (true) {
                if (!retransmit) {
                    buffer = new byte[1024];
                    int bytesRead = fis.read(buffer);
                    sendBuffer = new byte[bytesRead + 3];
                    counter++;
                    byte[] counterByte = ByteBuffer.allocate(2).putShort(counter).array();
                    boolean end = bytesRead < 1024;
                    if (bytesRead <= 0) {
                        break;
                    } else {
                        for (int i = 0; i <= bytesRead; i++) {
                            if (i == bytesRead) {
                                String helpMe = "I'm trapped in a program";
                                sendBuffer[i] = counterByte[0];
                                sendBuffer[i + 1] = counterByte[1];
                                if (end) {
                                    sendBuffer[i + 2] = 0;

                                } else {
                                    sendBuffer[i + 2] = 1;
                                }
                            }else {
                                sendBuffer[i] = buffer[i];
                            }
                        }
                    }
                }
                DatagramPacket packet = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), port);
                sock.send(packet);
//                    System.out.println("Sent");
                try {
                    byte[] replyBuffer = new byte[2];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    sock.receive(reply);
                    sock.setSoTimeout(10);
                    byte[] data = reply.getData();
                    int packetReceived = ByteBuffer.wrap(data).getShort();
                    if (packetReceived == counter) {
                        retransmit = false;
                    }
                } catch (SocketTimeoutException x) {
                    System.out.println("timeout");
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