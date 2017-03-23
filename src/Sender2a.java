import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.*;

public class Sender2a {
    public static void main(String args[]) {
        int port = 2000;
        int windowsize = 5;
        int retryTimeout = 100;
        DatagramSocket sock = null;


        try {

            TreeMap<Integer, byte[]> packetsInTransit = new TreeMap<>();
            TreeMap<Integer, time> timeMap = new TreeMap<Integer, time>();
            sock = new DatagramSocket(2001);
            byte[] buffer;
            File file = new File("test.jpg");
            FileInputStream fis = new FileInputStream(file);
            short packetNumber = 0; // or 1 ?
            byte[] sendBuffer = new byte[1027];
            // 1024 bytes
            while (true) {
                if (packetsInTransit.size() == windowsize) {
                    // do nothing
                } else {
                    buffer = new byte[1024];
                    int bytesRead = fis.read(buffer);
                    sendBuffer = new byte[bytesRead + 3];
                    byte[] counterByte = ByteBuffer.allocate(2).putShort(packetNumber).array();
                    boolean end = bytesRead < 1024;
                    if (bytesRead <= 0) {
                        break;
                    } else {
                        for (int i = 0; i <= bytesRead; i++) {
                            if (i == bytesRead) {
                                sendBuffer[i] = counterByte[0];
                                sendBuffer[i + 1] = counterByte[1];
                                if (end) {
                                    sendBuffer[i + 2] = 0;

                                } else {
                                    sendBuffer[i + 2] = 1;
                                }
                            } else {
                                sendBuffer[i] = buffer[i];
                            }
                        }
                    }
                    time = sendPackage(sendBuffer, port, sock);
                    packetsInTransit.put((int) packetNumber, sendBuffer);
                    timeMap.put((int) packetNumber, time);
                    packetNumber++;

                }
                // This part for catching the replies
                try {
                    byte[] replyBuffer = new byte[2];
                    DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                    sock.receive(reply);
                    byte[] data = reply.getData();
                    int packetReceived = ByteBuffer.wrap(data).getShort();
                    for (int key = timeMap.firstKey(); key <= packetReceived; key++) {
                        packetsInTransit.remove(key);
                        timeMap.remove(key);
                    }
                } catch (IOException e) {
                    System.err.println("IOException" + e);
                }

            }
            // That would be be the second thread
            while (true) {
                if (currentTime > timeMap.get(timeMap.firstKey())) {
                    //maybe TODO pause other thread
                    for (int key = timeMap.firstKey(); key <= timeMap.lastKey(); key++) {
                        newTime = sendPackage(packetsInTransit.get(key), port, sock);
                        timeMap.put(key, newTime);
                    }
                    // The key with min time is always going to be the one which is send at the latest point
                    minTimeKeyOffset++;
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

    private static void sendPackage(byte[] byteArray, int port, DatagramSocket sock) {
        try {
            DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, InetAddress.getLocalHost(), port);
            sock.send(packet);
        } catch (IOException e) {
            System.err.println("IOException" + e);
        }
    }
}