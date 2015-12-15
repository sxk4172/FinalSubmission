/*
 * Channel.java
 * 
 * Version: $Id: Channel.java, v 1.1 2015/10/09 11:21:12
 * 
 * Revisions: 
 * 		
 * Initial Revision
 * 
 */

/**
 * TCP connection between routers to calculate RIP
 *
 * @author Sanika Kulkarni
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;



public class Channel implements Serializable {

	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	public Channel(SocketAddress to) {
		
		try {
			socket = new Socket (getInetAddress(to),getPort(to));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}
	
	
	
	public Channel(String ip, int port) throws IOException {
		
		
			socket = new Socket (ip,port);
	}
	
	public Channel(InetAddress ip, int port) {
		
		try {
			socket = new Socket (ip,port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Channel(Socket temp) {
		
		socket = temp;
	}
	
	
	
	public boolean end(){
		try {
			socket.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	
	public SocketAddress from(){
		return socket.getLocalSocketAddress();
	}
	
	public SocketAddress to(){
		return socket.getRemoteSocketAddress();
	}
	
	public Socket getSocket(){
		return this.socket;
	}


	public  void send(Object someObject){

		
		try {
			if(oos==null){
				oos = new ObjectOutputStream(socket.getOutputStream());
			}
			oos.writeObject(someObject);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			e.printStackTrace();
		}
	} 

	
	// Network related

	public  Object receive(){

		Object someObject = null;
		
		try {
			if(ois==null){
			ois = new ObjectInputStream(socket.getInputStream());
			}
			someObject = (Object) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			e.printStackTrace();
		}

		return someObject;
	} 


	public static String getInetAddress(SocketAddress temp){

		return temp.toString().substring(1).split(":")[0];
	}

	public static int getPort(SocketAddress temp){

		return Integer.parseInt(temp.toString().substring(1).split(":")[1]);
	}

	public String toString(){
		return "[CHANNEL] From: "+this.from()+" -- > To: "+this.to();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
