import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileManager	{
	public final static int FILE_SIZE = 6022386; // file size temporary hard coded should bigger than the file to be downloaded
	public final static int TYPE_RECEIVE = 1;
	public final static int TYPE_SEND = 2;
	
	private int type;
	private String sendFilePath;
	private String receiveFilePath;
	
	private InputStream is;
	private OutputStream os;
	
	public FileManager(InputStream is, OutputStream os){
		this.type = 0;
		this.receiveFilePath = "";
		this.sendFilePath = "";
		
		this.is = is;
		this.os = os;
		
	}
	
	/**
	 * 
	 * @param sendFilePath Caminho do arquivo que est� sendo enviado
	 */
	public void startSendFile(String sendFilePath) {
		this.type = TYPE_SEND;
		this.sendFilePath = sendFilePath;
	}
	
	/**
	 * 
	 * @param receiveFilePath Caminho onde o arquivo recebido ser� salvo junto com o nome do arquivo
	 */
	public void startReceiveFile(String receiveFilePath) {
		this.type = TYPE_RECEIVE;
		this.receiveFilePath = receiveFilePath;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}