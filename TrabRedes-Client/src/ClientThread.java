import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;

public class ClientThread extends Thread{
	
	private Client client;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private InputStream is;
	private OutputStream os;
	
	private boolean stopFlag;
	private Socket socket;
	
	private String hostAdress;
	private int port;
	private String clientName;

	public ClientThread(String hostAdress, int port, String clientName) {
		super();
		this.stopFlag = false;
		this.hostAdress = hostAdress;
		this.port = port;
		this.clientName = clientName;
		this.socket = null;
	}
	
	public void connect() {
		try {
			this.socket = new Socket(hostAdress, port);
			this.client = new Client(this.clientName);
			
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
			
			this.output = new ObjectOutputStream(this.os);
			this.input = new ObjectInputStream(this.is);
			
			
			MainClient.jtaChat.setText("");
			
			this.start();
		} catch (IOException e) {
			//TODO Tratar catch
			System.out.println("Erro: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		setStopFlag(true);
		MainClient.removeUserRows();
		
		try {
			output.close();
			input.close();
			socket.close();
			
			MainClient.jtaChat.setText("");
			MainClient.jtaChat.append("SERVER [" + new Date() +"] => Disconnected\n");
		} catch (IOException e) {
			//TODO Tratar catch
			e.printStackTrace();
		}
	}
	
	public void sendFile(Message message) {
		
	}
	
	public void sendMessage(Message message) {
		try {
			output.flush();
			output.writeObject(message);
		} catch (IOException e) {
			//TODO Tratar catch
			e.printStackTrace();
		}
	}
	
	public Message readMessage() {
		Message message = null;
	
		try {
			message = (Message) input.readObject();
			
			if(message.getType() == Message.TYPE_UPDATECLIENT) {
				this.client = message.getUpdate();
			}
			else if(message.getType() == Message.TYPE_UPDATEUSERS) {
				MainClient.updateUserTable(message.getUsers());
			}
			else if(message.getType() == Message.TYPE_PLAINTEXT){
				//TODO: Mostrar mensagem no chat
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
				
			}
		} catch (SocketException e) {
			//Fechou
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return message;
	}

	public void run() {
		Message msg = new Message(this.client, Message.TYPE_UPDATECLIENT);
		sendMessage(msg);
		
		while(!isStopFlag()) {
			if(!socket.isClosed() && !isStopFlag()) {
				readMessage();
			}	
			else {
				break;
			}
		}
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public void setInput(ObjectInputStream input) {
		this.input = input;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public void setOutput(ObjectOutputStream output) {
		this.output = output;
	}

	public boolean isStopFlag() {
		return stopFlag;
	}

	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}

	public String getHostAdress() {
		return hostAdress;
	}

	public void setHostAdress(String hostAdress) {
		this.hostAdress = hostAdress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public InputStream getIs() {
		return is;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	public OutputStream getOs() {
		return os;
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}
	
	


	
	
	

}