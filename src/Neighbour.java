/*
 * Neighbour.java
 * 
 * Version: $Id: Neighbour.java, v 1.1 2015/10/09 11:21:12
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

import java.io.Serializable;
import java.net.InetAddress;




public class Neighbour implements Serializable{

	int id;
	int port;
	int cost;
	InetAddress iP;
	
	public Neighbour(int id,int cost, int port, InetAddress iP) {
		this.id=id;
		this.port = port;
		this.cost = cost;
		this.iP = iP;
		
	}
	public Neighbour() {
		
	}
	
	@Override
	public String toString() {
		return "ID: "+id+"@["+iP+":"+port+"]"+" Cost: "+cost;
	}

}