package nonstar.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScopeBlock {
	
	public HashMap<String, SymbolRecord> symbols = new HashMap<String, SymbolRecord>();
	
	public SymbolRecord addRecord(String symbolName){
		
		SymbolRecord sr = new SymbolRecord(symbolName);
		symbols.put(symbolName, sr);
		sr.scope = this;
		return sr;
		
	}
	
	public SymbolRecord accessSymbolInThisScope(String symbolName){
		return symbols.get(symbolName);
	}
	
	public boolean alreadyHave(String id){
		return symbols.containsKey(id);
	}
	
	
}