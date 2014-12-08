package nonstar.compiler;

public class QuarlifiedName extends RecursiveState{
	
	public String code;
	public SymbolRecord sr;
	
	public QuarlifiedName() {
		super();
	}
	
	public static QuarlifiedName joinQuarlifiedName(QuarlifiedName pre_qn, String id) {
		QuarlifiedName qn = new QuarlifiedName();
		qn.code = pre_qn.code + "." + id;
		
		if(!pre_qn.isSemanticallyFine()){
			qn.semantically_fine = false;
			qn.quiet = true;
			return qn;
		}
		if(!pre_qn.sr.isAttribute){
			qn.semantically_fine = false;
			return qn;
		}
		qn.sr = Type.dotOP(pre_qn.sr.attr.type, id);
		if(qn.sr == null){
			qn.semantically_fine = false;
		}
		return qn;
	}

}
