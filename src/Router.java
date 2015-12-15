/*
 * Router.java
 * 
 * Version: $Id: Router.java, v 1.1 2015/10/09 11:21:12
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class Router {
	// initialize variables
	InetAddress self;
	int listeningPort;
	int id;
	static int SLEEP_THRESHOLD = 10000;

	ArrayList<Neighbour> allNeighbours;
	HashMap<Integer, Boolean> ifActive;
	HashMap<Integer, ArrayList<Neighbour>> routingTable;

	public Router(int id, int port, ArrayList<Neighbour> currentNeighbours) {

		try {
			self = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		allNeighbours = currentNeighbours;
		listeningPort = port;
		this.id = id;
		ifActive = new HashMap<Integer, Boolean>();
		routingTable = new HashMap<Integer, ArrayList<Neighbour>>();
	}

	// check if node active then add to hashtable
	public void addNieghbour(int id) {
		ifActive.put(id, true);
	}

	// Check if the neighbours active

	public void checkActiveNeighbours() {

		for (Neighbour current : allNeighbours) {

			try {
				Channel worker = new Channel(current.iP.getHostAddress(),
						current.port);
				worker.send(new PayLoad(current.id, id, "Milala ka ?"));
				ifActive.put(current.id, true);
				worker.end();
			} catch (Exception e) {

				ifActive.put(current.id, false);

			}

		}
	}

	// Read file and get all neighbours of router
	static ArrayList<Neighbour> getNeighboursFromFile(String fileName) {

		ArrayList<Neighbour> neighbours = new ArrayList<Neighbour>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String readLine = "";
			String[] readData;

			while ((readLine = br.readLine()) != null) {
				readData = readLine.split(" ");
				int id = Integer.parseInt(readData[0]);
				int cost = Integer.parseInt(readData[1]);
				int destinationPort = Integer.parseInt(readData[2]);
				InetAddress iP = InetAddress.getByName(readData[3]);
				neighbours.add(new Neighbour(id, cost, destinationPort, iP));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return neighbours;
	}

	// print received table
	public void printReceivedTable(int currentId,
			ArrayList<Neighbour> neighbours) {

		// id : destinaton : cost
		String result = "===========================\n";
		for (int i = 0; i < neighbours.size(); i++) {

			result += (i + 1) + ". " + currentId + " " + neighbours.get(i).id
					+ "  " + neighbours.get(i).cost + "\n";

		}
		result += "===========================";

		System.out.println(result);
	}

	// print received table after all routers connected
	public void printReceivedTable(
			HashMap<Integer, ArrayList<Neighbour>> routingTable) {

		for (Integer temp : routingTable.keySet()) {
			printReceivedTable(temp, routingTable.get(temp));
		}
	}

	// add neighbours to routing table
	public boolean addToRoutingTable(int id,
			ArrayList<Neighbour> currentNeighbours) {

		boolean result = false;
		if (routingTable != null) {

			routingTable.put(id, currentNeighbours);
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	// server handling
	private class requestHandler extends Thread {

		Channel puppetChannel;
		Router r;
		String tag = "[requestHandler] ";

		public requestHandler(Router r) {
			// TODO Auto-generated constructor stub
			this.r = r;
			tag = r + tag;
		}

		// adding channels to server
		public requestHandler addChannel(Channel puppetSocket) {
			// TODO Auto-generated constructor stub
			this.puppetChannel = puppetSocket;
			return this;
		}

		@Override
		public void run() {

			PayLoad recieved = (PayLoad) puppetChannel.receive();
			r.ifActive.put(recieved.from, true);
			puppetChannel.send(new PayLoad(recieved.from, r.id, true));
			System.out.println(tag + "Recieved a request:" + puppetChannel
					+ "\nPayload :" + recieved);

			System.out.println("\n\nROUTING TABLE :");
			r.printReceivedTable(r.id, r.allNeighbours);
			r.addToRoutingTable(recieved.from,
					(ArrayList<Neighbour>) recieved.payload);
			r.printReceivedTable(r.routingTable);
			System.out.println("\n\n");

		}

	}

	private class updateHandler extends Thread {

		Channel puppetChannel;
		Router r;
		String tag = " [Update Handler] ";

		public updateHandler(Router r) {
			// TODO Auto-generated constructor stub
			this.r = r;
			tag = r + tag;
		}

		@Override
		public void run() {
			while (true) {

				// loop
				boolean noActiveNeighbours = true;

				for (Neighbour current : r.allNeighbours) {

					if (r.ifActive.get(current.id)) {

						noActiveNeighbours = false;

						try {
							Channel connectNeighbour = new Channel(
									current.iP.getHostAddress(), current.port);
							connectNeighbour.send(new PayLoad(current.id, r.id,
									r.allNeighbours));
							PayLoad received = (PayLoad) connectNeighbour
									.receive();
							if ((boolean) received.payload) {
								System.out.println(tag
										+ "Update successful to " + current);
							} else {
								System.out.println(tag + "Update FAILED to "
										+ current);
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				try {
					Thread.sleep(Router.SLEEP_THRESHOLD);

					if (noActiveNeighbours) {
						System.out.println(tag + " No active neighbours found");
					}
					System.out.println();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Router : " + id + "]";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int id = Integer.parseInt(args[0]);
		int port = Integer.parseInt(args[1]);
		String file = args[2];

		Router r = new Router(id, port, getNeighboursFromFile(file));
		System.out.println("Router initiliazed :" + r);
		System.out.println("Checking for active neighbours if any");

		r.checkActiveNeighbours();

		System.out.println("done !");

		// start a updater thread
		r.new updateHandler(r).start();
		System.out.println(r + "update handler started..");

		try {
			ServerSocket listeningSocket = new ServerSocket(r.listeningPort);
			System.out.println(r + "Listening on :" + r.listeningPort);

			while (true) {

				Channel acceptedConnection = new Channel(
						listeningSocket.accept());
				System.out.println(r + "Accepted a connection :"
						+ acceptedConnection);
				r.new requestHandler(r).addChannel(acceptedConnection).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
