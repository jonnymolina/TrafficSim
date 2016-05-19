package tmcsim.cadsimulator.paramicscontrol;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 *
 * @author Jonathan Molina
 */
public class ParamicsModeler
{
    public static void main(String[] args) throws IOException
    {
        String host = "127.0.0.1";
        int port = 4450;
        File file = new File("paramics_status.xml");
        Path path = file.toPath();
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        OutputStream out = socket.getOutputStream();
        Files.copy(path, out);
        out.flush();
    }
}
