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
	
	
	
	//public List<SymbolRecord> symbolList = new ArrayList<SymbolRecord>();

//	public void addRecord(String symbolName, SymbolType type) {
//
//		switch (type) {
//		case CARD:
//		case CHARACTER:
//			symbolList
//					.add(new SymbolRecord(symbolName, type, new ScopeBlock()));
//			break;
//		default:
//			symbolList.add(new SymbolRecord(symbolName, type, null));
//		}
//
//	}
//	
//	public List<String> genNamesOfSomeType(SymbolType type){
//		List<String> ret = new ArrayList<String>();
//		for (SymbolRecord record : symbolList) {
//			if (record.getSymbolType()==type) {
//				ret.add(record.getSymbolName());
//			}
//		}
//		return ret;
//	}
//
//	public SymbolType findRecordInThisBlock(String name) {
//		for (SymbolRecord record : symbolList) {
//			if (record.getSymbolName().equals(name)) {
//				return record.getSymbolType();
//			}
//		}
//		return SymbolType.UNDEFINED;
//	}
//
//	public ScopeBlock findMatchCardCharacter(String name) {
//		for (SymbolRecord record : symbolList) {
//			if (record.getSymbolName().equals(name)) {
//				SymbolType type = record.getSymbolType();
//				return record.getScopeBlock();
//			}
//		}
//		return null;
//	}
	
}