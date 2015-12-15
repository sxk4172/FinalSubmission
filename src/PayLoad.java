/*
 * PayLoad.java
 * 
 * Version: $Id: PayLoad.java, v 1.1 2015/10/09 11:21:12
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


public class PayLoad implements Serializable{

	
	int from;
	int to;
	Object payload;
	
	public PayLoad(int to, int from, Object payload) {
		this.from = from;
		this.to = to;
		this.payload = payload;
	}
	
	
	@Override
	public String toString() {
		
		return "From: "+from+"\nTo: "+to+"\nData: "+payload.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
