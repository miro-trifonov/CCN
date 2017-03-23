import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class Receiver2b {
    //TODO buffer packages; support out of order storage and then writing;

    public static void main(String args[]) {
        int port = 2000;

        try {
            DatagramSocket sock = new DatagramSocket(port, InetAddress.getLocalHost());
            //buffer to receive incoming data
            byte[] buffer = new byte[1027];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
//            ByteOutputStream image = new ByteOutputStream();
            short ack = 0;

            while (true) {
                sock.receive(incoming);
                int length = incoming.getLength();
                buffer = incoming.getData();
                byte[] reply = {buffer[1025], buffer[1026]};
                short packetReceived = ByteBuffer.wrap(reply).getShort();
                if (packetReceived == ack){
                    DatagramPacket dp = new DatagramPacket(reply, reply.length, incoming.getAddress(), incoming.getPort());
                    sock.send(dp);
                    ack ++;
                    FileOutputStream output = new FileOutputStream("newImage", true);
                    try {
                        output.write(buffer,0, length -3);
                    } finally {
                        output.close();
                    }
//                System.out.println(buffer[length -1]);
                    if (buffer[length -1] == 0){
                        break;
                    }
                    continue;
                } else {
                    DatagramPacket dp = new DatagramPacket(reply, reply.length, incoming.getAddress(), incoming.getPort());
                    sock.send(dp);
                }

//                for (int i = 0; i < length - 3 ; i++) {
//                    image.write(buffer[i]);
//                }
//                System.out.println(buffer[length -1]);


            }

        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }
}

//                s = "OK : " + s;
