import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.activation.MimetypesFileTypeMap;

public class JavaWebServer {

	private static final int NUMBER_OF_THREADS = 100;
	private static final Executor THREAD_POOL = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

	public static void main(String[] args) throws IOException {
		ServerSocket socket = new ServerSocket(8080);

		// Waits for a connection request
		while (true) {
			final Socket connection = socket.accept();
			Runnable task = new Runnable() {
				@Override
				public void run() {
					HandleRequest(connection);
				}
			};
			THREAD_POOL.execute(task);

		}

	}

    private static void HandleRequest(Socket s)
    {
        BufferedReader in;
        PrintWriter out;
        String request, accumulate = "";
        Scanner fileLoader;
        File file;
        String contentType = "text/html";
        
        try
        {
            String webServerAddress = s.getInetAddress().toString();
            System.out.println("New Connection:" + webServerAddress);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            boolean isFirst = true;
            String firstLine = "";
            while(in.ready()) {
            		request = in.readLine();	
            		if(isFirst) {
            			firstLine = request.substring(5, request.length()-9);
            			isFirst = false;
            			if(request.contains("application/javascript")) {
            				contentType = "application/javascript";
            			}
            		}
            		System.out.println("--- Client request: " + request);
            		System.out.println(contentType);
            }
            file = new File("/Users/league/Desktop/create-a-server-TrollOfAllTrolls/" + firstLine);
            fileLoader = new Scanner(file);
            out = new PrintWriter(s.getOutputStream(), true);
            out.println("HTTP/1.0 200");
            out.println("Content-type: " + contentType);
            out.println("Server-name: myserver");
            out.println("");
            while(fileLoader.hasNextLine()) {
            		accumulate += fileLoader.nextLine();
            }
            System.out.println(accumulate);
            out.println("Content-Length: " + accumulate.length());
            out.println(accumulate);
            out.flush();
            out.close();
            s.close();
        }
        catch (IOException e)
        {
            System.out.println("Failed respond to client request: " + e.getMessage());
        }
        finally
        {
            if (s != null)
            {
                try
                {
                    s.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return;
    }
 
}