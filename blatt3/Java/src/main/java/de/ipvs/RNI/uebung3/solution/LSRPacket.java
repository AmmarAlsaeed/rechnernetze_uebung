package de.ipvs.RNI.uebung3.solution;

import java.util.ArrayList;

import de.ipvs.RNI.uebung3.simulator.Route;

public class LSRPacket {
	private int seqnum;
	private int ttl;
	private int source;
	private ArrayList<Route> content;

	public LSRPacket(int seqnum, int ttl, int source) {
		this.seqnum = seqnum;
		this.ttl = ttl;
		this.source = source;
		this.content = new ArrayList<Route>();
	}

	/**
	 * copy a LSR package into a new object.
	 * 
	 * @param toCopy
	 */
	public LSRPacket(LSRPacket toCopy) {
		this.seqnum = toCopy.getSeqNum();
		this.ttl = toCopy.getTTL();
		this.source = toCopy.getSrc();
		this.content = toCopy.getContent();
	}

	public int getSeqNum() {
		return this.seqnum;
	}

	public int getTTL() {
		return this.ttl;
	}

	public void decreaseTTL() {
		this.ttl--;
	}

	public int getSrc() {
		return this.source;
	}

	public ArrayList<Route> getContent() {
		return this.content;
	}

	public void addContent(Route r) {
		this.content.add(r);
	}

	public String toMessageString() {
		String output = seqnum + ";" + ttl + ";" + source + ",";

		for (Route r : content) {
			output += r.getDest() + ";" + r.getCost() + ";";
		}

		return output;
	}

	public static LSRPacket convertFromString(String input) {
		String header = input.split(",")[0];
		String[] headeritems = header.split(";");
		LSRPacket lsp = new LSRPacket(Integer.parseInt(headeritems[0]), Integer.parseInt(headeritems[1]),
				Integer.parseInt(headeritems[2]));
		String content = input.split(",")[1];
		String[] routes = content.split(";");
		for (int i = 0; i < routes.length; i += 2) {
			int dest = Integer.parseInt(routes[i]);
			int cost = Integer.parseInt(routes[i + 1]);

			lsp.addContent(new Route(dest, dest, cost));

		}

		return lsp;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + seqnum;
		result = prime * result + source;
		result = prime * result + ttl;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LSRPacket other = (LSRPacket) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (seqnum != other.seqnum)
			return false;
		if (source != other.source)
			return false;
		if (ttl != other.ttl)
			return false;
		return true;
	}

}
