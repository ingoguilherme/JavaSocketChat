import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

public class ServerMessageThread extends Thread{
	Client client;
	private MessageManager messageManager;
	
	private boolean running;
	private Socket messageSocket;
	
	public ServerMessageThread(Socket messageSocket) throws IOException {
		super();
		this.messageSocket = messageSocket;
		this.running = true;
		this.client = new Client();
		
		this.messageManager = null;
		
		this.start();
	}
	
	public void run() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(getMessageSocket().getOutputStream());
			ObjectInputStream ois  = new ObjectInputStream(getMessageSocket().getInputStream());
			setMessageManager(new MessageManager(ois, oos));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(isRunning()) {
			Message message = getMessageManager().readMessage();
			
			if(message == null) {
				disconnect();
				break;
			}
			else if(message.getType() == Message.TYPE_UPDATECLIENT) {
				String tempName = this.client.getName();
				
				setClient(message.getUpdate());
				
				if(tempName.equals("")) {
					getClient().setId(MainServer.getNewID());
					Message msgUpdate = new Message(getClient(), Message.TYPE_UPDATECLIENT);
					getMessageManager().sendMessage(msgUpdate);
					
					for(Message msg : MainServer.messageHistoric) {
						if(msg.hasReceiver()) {
							if(this.getClient().getId() == msg.getReceiver().getId()) {
								getMessageManager().sendMessage(message);
								break;
							}
						}
						else {
							getMessageManager().sendMessage(msg);
						}
					}
					
					Message msgConnect = new Message("Client connected (" + getClient().getName() + "). ID: " + getClient().getId() + "!", Message.TYPE_PLAINTEXT, new Date());
					MainServer.jtaChat.append("[" + msgConnect.getFormattedServerDate() + "] " + msgConnect.getMessage() + "!\n");
					MainServer.messageHistoric.add(msgConnect);
					
					ArrayList<Client> clients = new ArrayList<Client>();
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						clients.add(tc.getClient());
					}
					
					Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(msgConnect);
						tc.getMessageManager().sendMessage(updateUsers);
					}
				}
				else if(!tempName.equals(this.client.getName())) {
					Message msg = new Message(tempName + " has changed his name to " + getClient().getName() + "!", Message.TYPE_PLAINTEXT, new Date());
					MainServer.jtaChat.append("[" + msg.getFormattedServerDate() + "] " + msg.getMessage() + "\n");
					MainServer.messageHistoric.add(msg);
					
					ArrayList<Client> clients = new ArrayList<Client>();
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						clients.add(tc.getClient());
					}
					
					Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(msg);
						tc.getMessageManager().sendMessage(updateUsers);
					}
					
				}
			}
			else if(message.getType() == Message.TYPE_PLAINTEXT) {
				message.setServerDate(new Date());
				
				MainServer.messageHistoric.add(message);
				
				if(message.hasReceiver()) {
					MainServer.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") TO " + message.getReceiver().getName() + "(ID: " + message.getReceiver().getId() + ") >> " + message.getMessage() + "\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						if(tc.getClient().getId() == message.getReceiver().getId() || tc.getClient().getId() == message.getSender().getId()) {
							tc.getMessageManager().sendMessage(message);
						}
					}
				}
				else {
					MainServer.jtaChat.append("["+ message.getFormattedServerDate() + "] " + message.getSender().getName() + "(ID: " + message.getSender().getId() + ") >> " + message.getMessage() + "\n");
					
					for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
						tc.getMessageManager().sendMessage(message);
					}
				}
				
				
			}
			else if(message.getType() == Message.TYPE_FILE) {
				//TODO Pegar a thread de arquivo do cliente sender e fazer ela reenviar para as pessoas certas
				//TODO O download qd o sender mando � automatico, reconhecer arquivo enviado pela ID e filename
				
				
				
				
			}
		}
	}
	
	public void disconnect() {
		setRunning(false);
		
		try {
			getMessageManager().close();
			getMessageSocket().close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		MainServer.getConnectedMessageThreads().remove(this);
		Message msg = new Message("Client " + getClient().getName() + " [ID:" + getClient().getId() + "] disconnected!", Message.TYPE_PLAINTEXT, new Date());
		MainServer.jtaChat.append("[" + msg.getFormattedServerDate() + "] " + msg.getMessage() + "!\n");
		MainServer.messageHistoric.add(msg);
		
		ArrayList<Client> clients = new ArrayList<Client>();
		for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
			clients.add(tc.getClient());
		}
		
		Message updateUsers = new Message(clients, Message.TYPE_UPDATEUSERS);
		
		for(ServerMessageThread tc: MainServer.getConnectedMessageThreads()) {
			tc.getMessageManager().sendMessage(msg);
			tc.getMessageManager().sendMessage(updateUsers);
		}
	}
	
	public MessageManager getMessageManager() {
		return messageManager;
	}

	public void setMessageManager(MessageManager messageManager) {
		this.messageManager = messageManager;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Socket getMessageSocket() {
		return messageSocket;
	}

	public void setMessageSocket(Socket messageSocket) {
		this.messageSocket = messageSocket;
	}
	
	
	
	
	
}
