import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class ClientMessageThread extends Thread{
	
	private Client client;
	private MessageManager messageManager;
	private FileManager fileManager;
	
	private Socket messageSocket;
	private Socket fileSocket;
	
	private boolean running;
	private String hostAdress;

	public ClientMessageThread(String hostAdress, String clientName) {
		super();
		this.running = false;
		this.hostAdress = hostAdress;
		this.messageSocket = null;
		this.fileSocket = null;
		this.client = new Client(clientName);
		this.messageManager = null;
		this.fileManager = null;
	}
	
	public void run() {
		Message msg = new Message(getClient(), Message.TYPE_UPDATECLIENT);
		getMessageManager().sendMessage(msg);
		
		while(isRunning()) {
			if(!getMessageSocket().isClosed()) {
				Message message = readMessage();
				if(message == null){
					break;
				}
			}	
			else {
				break;
			}
		}
	}
	
	public void connect() {
		try {
			setMessageSocket(new Socket(hostAdress, ServerConsts.MESSAGE_PORT));
			ObjectInputStream ois = new ObjectInputStream(getMessageSocket().getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(getMessageSocket().getOutputStream());
			setMessageManager(new MessageManager(ois, oos));
			
			setRunning(true);
			
			MainClient.jtaChat.setText("");
			MainClient.jtfIp.setEditable(false);
			MainClient.jtfName.setEditable(false);
			MainClient.jbConnect.setText("Disconnect");
			
			this.start();
		} catch(ConnectException e) {
			setRunning(false);
			
			MainClient.jtaChat.setText(e.getMessage());
			MainClient.jtfIp.setEditable(true);
			MainClient.jtfName.setEditable(true);
			MainClient.clientThread = null;
			MainClient.jbConnect.setText("Connect");
		} catch (IOException e) {
			//TODO Tratar catch
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		setRunning(false);
		MainClient.removeUserRows();
		
		try {
			getMessageManager().close();
			getMessageSocket().close();
			
			MainClient.jtaChat.setText("");
			MainClient.jtaChat.append("SERVER [" + new Date() +"] => Disconnected\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message readMessage() {
		Message message = null;
	
		message = getMessageManager().readMessage();
		
		if(message == null) {
			//Do nothing
		}
		else if(message.getType() == Message.TYPE_UPDATECLIENT) {
			setClient(message.getUpdate());
		}
		else if(message.getType() == Message.TYPE_UPDATEUSERS) {
			MainClient.updateUserTable(message.getUsers());
		}
		else if(message.getType() == Message.TYPE_PLAINTEXT){
			if(message.hasReceiver()) {
				MainClient.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") TO " + message.getReceiver().getName() + "(ID: " + message.getReceiver().getId() + ") >> " + message.getMessage() + "\n");
			}
			else if(message.hasSender()) {
				MainClient.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") >> " + message.getMessage() + "\n");
			}
			else {
				MainClient.jtaChat.append("SERVER ["+ message.getFormattedServerDate() +"] => " + message.getMessage() + "\n");
			}
			
		}
		else if(message.getType() == Message.TYPE_FILE){
			//TODO Quando cliente recebe arquivo
			//Conecta no socket de files
			//Baixa o arquivo X
		}
		
		return message;
	}
	
	public boolean isSocketClosed() {
		if(getMessageSocket() == null || getMessageSocket().isClosed()) {
			return true;
		}
		else if(getFileSocket() == null || getFileSocket().isClosed()) {
			return true;
		}
		else {
			return false;
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Socket getMessageSocket() {
		return messageSocket;
	}

	public void setMessageSocket(Socket messageSocket) {
		this.messageSocket = messageSocket;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public Socket getFileSocket() {
		return fileSocket;
	}

	public void setFileSocket(Socket fileSocket) {
		this.fileSocket = fileSocket;
	}
	
	public void sendFile(File myFile) {
		//TODO: Quando cliente manda arquivo
		//Conecta no server de files
		//Envia arquivo X
		
		try {
			setFileSocket(new Socket(hostAdress, ServerConsts.FILE_PORT));
			setFileManager(new FileManager(getFileSocket().getInputStream(), getFileSocket().getOutputStream()));
			getFileManager().sendFile(getFileSocket(), myFile);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	


	
	
	

}