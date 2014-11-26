package nonstar.basic;

import java.util.HashSet;
import java.util.Iterator;

public class Flow {
	HashSet<Link> link;
	
	public Flow() {
		super();
		link = new HashSet<Link>();
	}
	
	public boolean addLink(Link l) {
		return link.add(l);
	}
	
	public boolean delLink(Link l) {
		return link.remove(l);
	}
	
	public Iterator<Link> getLinkIter() {
		return link.iterator();
	}
}
