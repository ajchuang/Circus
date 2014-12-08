package nonstar.compiler;

public class RecursiveState {
	public boolean semantically_fine;
	public boolean quiet;
	
	public RecursiveState() {
		this.semantically_fine = true;
		this.quiet = false;
	}
	
	public boolean isSemanticallyFine() {
		return semantically_fine;
	}
	
	public boolean isQuiet() {
		return quiet;
	}
}
