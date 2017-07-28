import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerFileThread extends Thread{
	private Client client;
	private InputStream is;
	private OutputStream os;
	private Socket socket;
	
	public ServerFileThread(Socket socket, Client client) {
		super();
		this.socket = socket;
		this.client = client;
		
		try {
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		
	}
	
	
}
