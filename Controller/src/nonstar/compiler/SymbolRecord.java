package nonstar.compiler;

public class SymbolRecord {
	
	public String symbolName;
	public boolean isAttribute; // false for function
	public AttributeObj attr;
	public FunctionObj func;
	public ScopeBlock scope;
	
	public SymbolRecord(String symbolName){
		this.symbolName = symbolName;
	}
	
	public void setValue(boolean isAttribute, Object attrOrfunc){
		this.isAttribute = isAttribute;
		if(isAttribute){
			this.attr = (AttributeObj)attrOrfunc;
		}
		else{
			this.func = (FunctionObj)attrOrfunc;
		}
	}

}
