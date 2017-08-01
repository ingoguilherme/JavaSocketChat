import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileManager {
	public final static int FILE_SIZE = 6022386; // file size temporary hard coded should bigger than the file to be downloaded
	public final static int TYPE_RECEIVE = 1;
	public final static int TYPE_SEND = 2;
	
	private int type;
	
	private InputStream input;
	private OutputStream output;
	
	public FileManager(InputStream is, OutputStream os){
		this.type = 0;
		
		this.input = is;
		this.output = os;
	}
	
	public void sendMessage(Message message) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(getOutput());
			oos.flush();
			oos.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message readMessage() {
		Message message = null;
	
		try {
			ObjectInputStream ois = new ObjectInputStream(getInput());
			message = (Message) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	public void sendFile(Socket socket, File myFile) throws IOException{
		
		FileInputStream fis = null;
	    BufferedInputStream bis = null;
	    OutputStream os = null;
		
		System.out.println("Waiting...");
        try {
          System.out.println("Accepted connection : " + socket);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = socket.getOutputStream();
          System.out.println("Sending " + myFile.getAbsolutePath() + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
        }
        finally {
          if (bis != null) bis.close();
          if (os != null) os.close();
          if (socket!=null) socket.close();
        }
		
		/*
		
		FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
		
		try {
			byte [] mybytearray  = new byte [(int) myFile.length()];
	        
			bis.read(mybytearray,0,mybytearray.length);
	        System.out.println("Sending " + myFile.getAbsolutePath() + "(" + mybytearray.length + " bytes)");
	        
	        this.getOutput().write(mybytearray,0,mybytearray.length);
	        this.getOutput().flush();
		} 
		finally {
			if (fis != null) fis.close();
			if (bis != null) bis.close();
		}
        */
	}
	
	public void receiveFile(Socket socket, String receiveFilepath) throws IOException{
		int bytesRead;
	    int current = 0;
	    FileOutputStream fos = null;
	    BufferedOutputStream bos = null;
		
		try {
			System.out.println("Connecting...");
		
			// receive file
			byte [] mybytearray  = new byte [FILE_SIZE];
			InputStream is = socket.getInputStream();
			fos = new FileOutputStream(receiveFilepath);
			bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			current = bytesRead;
		
			do {
				bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
				if(bytesRead >= 0)
					current += bytesRead;
			} while(bytesRead > -1);
		
			bos.write(mybytearray, 0 , current);
			bos.flush();
			System.out.println("File " + receiveFilepath + " downloaded (" + current + " bytes read)");
		}
		finally {
			if (fos != null) fos.close();
			if (bos != null) bos.close();
			if (socket != null) socket.close();
		}
		
		/*
		byte [] mybytearray  = new byte [FILE_SIZE];
		
		FileOutputStream fos = new FileOutputStream(receiveFilepath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		int bytesRead = getInput().read(mybytearray,0,mybytearray.length);
		
		int current = bytesRead;
		
		try{
			do {
				bytesRead = getInput().read(mybytearray, current, (mybytearray.length-current));
				
				if(bytesRead >= 0) {
					current += bytesRead;
				}
			} while(bytesRead > -1);
			
			bos.write(mybytearray, 0 , current);
			bos.flush();
		}
		finally {
			if (fos != null) fos.close();
			if (bos != null) bos.close();
			
			System.out.println("File " + receiveFilepath + " downloaded (" + current + " bytes read)");
		}
		*/
		
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}
	
	
	
}
