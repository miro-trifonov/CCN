import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class Receiver1b {

    public static void main(String args[]) {
        int port = 2000;
        try {
            DatagramSocket sock = new DatagramSocket(port, InetAddress.getLocalHost());

            //buffer to receive incoming data
            byte[] buffer = new byte[1027];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
//            ByteOutputStream image = new ByteOutputStream();
            byte[] lastReceived = new byte[2];

            while (true) {
                sock.receive(incoming);
                int length = incoming.getLength();
                buffer = incoming.getData();
                byte[] reply = {buffer[1025], buffer[1026]};
                if (Arrays.equals(lastReceived, reply)){
                    DatagramPacket dp = new DatagramPacket(reply, reply.length, incoming.getAddress(), incoming.getPort());
                    sock.send(dp);
                    continue;
                } else {
                    lastReceived[0] = reply[0];
                    lastReceived[1] = reply[1];
                }

//                for (int i = 0; i < length - 3 ; i++) {
//                    image.write(buffer[i]);
//                }
                DatagramPacket dp = new DatagramPacket(reply, reply.length, incoming.getAddress(), incoming.getPort());
                sock.send(dp);
                FileOutputStream output = new FileOutputStream("newImage", true);
                System.out.println(buffer[length -1]);

                try {
                    output.write(buffer,0, length -3);
                } finally {
                    output.close();
                }
                System.out.println(buffer[length -1]);
                if (buffer[length -1] == 0){
                    break;
                }

            }

        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }
}

//                s = "OK : " + s;
