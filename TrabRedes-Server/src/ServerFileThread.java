import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ServerFileThread extends Thread{
	
	private Client client;
	private Socket fileSocket;
	private FileManager fileManager;
	ArrayList<File> filesToDownload;
	
	public static final String SERVER_STORAGE = "C:/Users/Mineradora03/Desktop/serverStorage/";
	
	public ServerFileThread(Socket fileSocket) {
		super();
		this.fileSocket = fileSocket;
		this.client = new Client();
		this.fileManager = null;
		this.filesToDownload = new ArrayList<File>();
		
		this.start();
	}
	
	public void run() {
		try {
			setFileManager(new FileManager(getFileSocket().getInputStream(), getFileSocket().getOutputStream()));
			Message m = getFileManager().readConfig();
			m.setServerDate(new Date());
			
			if(Message.TYPE_FILE_SEND == m.getType_file()) {
				setClient(m.getSender());
				
				if(getClient() == null) {
					System.out.println("DEU PAU");
					//TODO: Para tudo, deu pau
				}
				else {
					System.out.println("Recebeu cliente: " + getClient().getName() + " ID = " + getClient().getId());
				}
				
				getFileManager().receiveFile(SERVER_STORAGE + getClient().getId() + "_" + m.getFile().getName());
				
				
				m.setType_file(Message.TYPE_FILE_RECEIVE);
				m.setFile(new File(SERVER_STORAGE + getClient().getId() + "_" + m.getFile().getName()));
				
				if(m.hasReceiver()) {
					MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + m.getSender().getName() + "(ID: " + m.getSender().getId() + ") TO " + m.getReceiver().getName() + "(ID: " + m.getReceiver().getId() + ") >> Send a file: '" +m.getFile().getName() + "'\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						if(tc.getClient().getId() == m.getReceiver().getId() || tc.getClient().getId() == m.getSender().getId()) {
							tc.getMessageManager().sendMessage(m);
						}
					}
				}
				else {
					MainServer.jtaChat.append("["+ m.getFormattedServerDate() + "] " + m.getSender().getName() + "(ID: " + m.getSender().getId() + ") >> Send a file: '" + m.getFile().getName() + "'\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(m);
					}
				}
				
				
			}
			else if(Message.TYPE_FILE_RECEIVE == m.getType_file()) {
				setClient(m.getSender());
				
				if(getClient() == null) {
					System.out.println("DEU PAU");
					//TODO: Para tudo, deu pau
				}
				else {
					System.out.println("Recebeu cliente: " + getClient().getName() + " ID = " + getClient().getId());
				}
				
				System.out.println("FILE RECEIVE: " + m.getFile());
				
				//getFileManager().sendConfig(m);
	    		getFileManager().sendFile(m.getFile());
				
			}
			
			getFileManager().close();
			getFileSocket().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Socket getFileSocket() {
		return fileSocket;
	}

	public void setFileSocket(Socket fileSocket) {
		this.fileSocket = fileSocket;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public void addFileToDownload(File file) {
		this.filesToDownload.add(file);
	}
	
	
	
}