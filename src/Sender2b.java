import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.*;

public class Sender2b {

    private  final Runnable sender;
    private  final Runnable selectiveRepeat;

    private int port = 2000;
    private int windowsize = 5;
    private int retryTimeout = 100;
    private DatagramSocket sock = null;
    private TreeMap<Integer, byte[]> packetsInTransit = new TreeMap<>();
    private TreeMap<Integer, Long> timeMap = new TreeMap<Integer, Long>();

    private Sender2b() {
        sender = new Runnable() {
            public void run() {
                senderThread();
            }
        };
        selectiveRepeat = new Runnable() {
            public void run() {
                selectiveRepeat();
            }
        };
    }

    public static void main(String args[]) {

        Sender2b mainProgram = new Sender2b();
        new Thread(mainProgram.sender).start(); // start A
        new Thread(mainProgram.selectiveRepeat).start(); // start B
    }

    private  void senderThread() {
        try {

            sock = new DatagramSocket(2001);
            byte[] buffer;
            File file = new File("test.jpg");
            FileInputStream fis = new FileInputStream(file);
            short packetNumber = 0; // or 1 ?
            byte[] sendBuffer = new byte[1027];
            int windowMaxPackageNumber = windowsize;

            // 1024 bytes
            while (true) {
                if (packetNumber == windowMaxPackageNumber) {
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
                    long time = sendPackage(sendBuffer, port, sock) + retryTimeout;
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
                    if (packetsInTransit.lastKey().equals(packetsInTransit.firstKey())){
                        windowMaxPackageNumber =packetsInTransit.firstKey() + windowsize + 1;
                    }
                    packetsInTransit.remove(packetReceived);
                    timeMap.remove(packetReceived);
                    windowMaxPackageNumber =packetsInTransit.firstKey() + windowsize;
                } catch (IOException e) {
                    System.err.println("IOException" + e);
                }

            }
            // That would be be the second thread
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

    private  void selectiveRepeat() {
        while (true) {
            for (Integer key: timeMap.keySet()){
                if (System.currentTimeMillis() > timeMap.get(key)) {
                    long newTime = sendPackage(packetsInTransit.get(key), port, sock);
                    timeMap.put(key, newTime);
                }

            }
        }
    }

    private static long sendPackage(byte[] byteArray, int port, DatagramSocket sock) {
        try {
            DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length, InetAddress.getLocalHost(), port);
            sock.send(packet);
        } catch (IOException e) {
            System.err.println("IOException" + e);
        }
        return System.currentTimeMillis();
    }
}