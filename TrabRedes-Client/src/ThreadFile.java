import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadFile extends Thread{
	private FileOutputStream fos;
	private FileInputStream fis;
	private BufferedOutputStream bos;
	private BufferedInputStream bis;
	private InputStream is;
	private OutputStream os;
	
	private Socket socket;
	
	public final static int FILE_SIZE = 6022386; // file size temporary hard coded
    											 // should bigger than the file to be downloaded
	
	public ThreadFile(Socket socket){
		this.socket = socket;
		
		this.fos = null;
		this.fis = null;
		this.bos = null;
		this.bis = null;
		this.is = null;
		this.os = null;
	}
	
	public void run() {
		close();
	}
	
	public void sendFile(String sendFilePath){
		File myFile = new File(sendFilePath);
        byte [] mybytearray  = new byte [(int)myFile.length()];
        
		try {
			this.fis = new FileInputStream(myFile);
	        this.bis = new BufferedInputStream(this.fis);
	        this.bis.read(mybytearray,0,mybytearray.length);
	        this.os = this.socket.getOutputStream();
	        
	        System.out.println("Sending " + sendFilePath + "(" + mybytearray.length + " bytes)");
	        this.os.write(mybytearray,0,mybytearray.length);
	        this.os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public void receiveFile(String receiveFilePath) {
		byte [] mybytearray  = new byte [FILE_SIZE];
		
		try {
			this.is = this.socket.getInputStream();
			this.fos = new FileOutputStream(receiveFilePath);
			this.bos = new BufferedOutputStream(fos);
			int bytesRead = is.read(mybytearray,0,mybytearray.length);
			int current = bytesRead;
			
			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				
				if(bytesRead >= 0) {
					current += bytesRead;
				}
			} while(bytesRead > -1);
			
			bos.write(mybytearray, 0 , current);
			bos.flush();
			
			System.out.println("File " + receiveFilePath + " downloaded (" + current + " bytes read)");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			if (fos != null) fos.close();
			if (fis != null) fis.close();
			if (bos != null) bos.close();
			if (bis != null) bis.close();
			if (os != null) os.close();
			if (is != null) is.close();
			if (socket != null) socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}