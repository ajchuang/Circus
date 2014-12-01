package nonstar.basic;

import java.util.Iterator;
import java.util.LinkedList;

public class Flow {
	LinkedList<Link> link;
	LinkedList<Switch> sw;
	
	public Flow() {
		super();
		link = new LinkedList<Link>();
		sw = new LinkedList<Switch>();
	}
	
	public void addLinkFirst(Link l) {
		link.addFirst(l);
	}
	
	public boolean delLink(Link l) {
		return link.remove(l);
	}
	
	public void addSwFirst(Switch s) {
		sw.addFirst(s);
	}
	
	public boolean delSw(Switch s) {
		return sw.remove(s);
	}
	
	public Iterator<Link> getLinkIter() {
		return link.iterator();
	}
	
	public Iterator<Switch> getSwIter() {
		return sw.iterator();
	}
}
