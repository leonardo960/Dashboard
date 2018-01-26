package model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientWrapper {
	private Socket socket;
	private ObjectOutputStream out;
	
	public ClientWrapper(Socket s){
		socket = s;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket getClientSocket(){
		return socket;
	}
	
	public ObjectOutputStream getClientOut(){
		return out;
	}
}
