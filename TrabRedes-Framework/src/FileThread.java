import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileThread extends Thread{
	public final static int FILE_SIZE = 6022386; // file size temporary hard coded should bigger than the file to be downloaded
	public final static int TYPE_RECEIVE = 1;
	public final static int TYPE_SEND = 2;
	
	private int type;
	private String sendFilePath;
	private String receiveFilePath;
	
	private InputStream is;
	private OutputStream os;
	private Socket socket;
	
	public FileThread(Socket socket){
		this.type = 0;
		this.receiveFilePath = "";
		this.sendFilePath = "";
		this.socket = socket;
		
		try {
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		try {
			if(this.type == TYPE_SEND) {
				sendFile();
			}
			else if(this.type == TYPE_RECEIVE) {
				receiveFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param sendFilePath Caminho do arquivo que est� sendo enviado
	 */
	public void startSendFile(String sendFilePath) {
		this.type = TYPE_SEND;
		this.sendFilePath = sendFilePath;
		this.start();
	}
	
	/**
	 * 
	 * @param receiveFilePath Caminho onde o arquivo recebido ser� salvo junto com o nome do arquivo
	 */
	public void startReceiveFile(String receiveFilePath) {
		this.type = TYPE_RECEIVE;
		this.receiveFilePath = receiveFilePath;
		this.start();
	}
	
	private void sendFile() throws IOException{
		File myFile = new File(sendFilePath);
        byte [] mybytearray  = new byte [(int) myFile.length()];
        
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
		
		bis.read(mybytearray,0,mybytearray.length);
        System.out.println("Sending " + sendFilePath + "(" + mybytearray.length + " bytes)");
        
        this.os.write(mybytearray,0,mybytearray.length);
        this.os.flush();
        
        if (fis != null) fis.close();
		if (bis != null) bis.close();
        
	}
	
	private void receiveFile() throws IOException{
		byte [] mybytearray  = new byte [FILE_SIZE];
		
		FileOutputStream fos = new FileOutputStream(receiveFilePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
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
		
		if (fos != null) fos.close();
		if (bos != null) bos.close();
		
		System.out.println("File " + receiveFilePath + " downloaded (" + current + " bytes read)");
	}
}
