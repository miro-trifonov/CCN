import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Receiver1a {

    public static void main(String args[]) {
        int port = 2000;
        try {
            DatagramSocket sock = new DatagramSocket(port, InetAddress.getLocalHost());

            //buffer to receive incoming data
            byte[] buffer = new byte[1027]; // How to deal with buffer overflow
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            ByteOutputStream image = new ByteOutputStream();

            while (true) {
                sock.receive(incoming);
                int length = incoming.getLength();
                buffer = incoming.getData();
                for (int i = 0; i < length; i++) {
                    image.write(buffer[i]);
                }
                FileOutputStream output = new FileOutputStream("newImage", true);
                try {
                    output.write(buffer);
                } finally {
                    output.close();
                }
                if (buffer[length + 2] == 0){
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }
}
