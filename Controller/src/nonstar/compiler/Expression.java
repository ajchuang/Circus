package nonstar.compiler;

public class Expression extends RecursiveState {
	
	public Type return_type;
	public String code;
	
	public Expression() {
		super();
	}

	public static String joinExpressionCode(String op, String exp1, String exp2) {
		if(exp2 == null) 
			return op + exp1;
		else
			return exp1 + " " + op + " " + exp2; 
	}
	
	public static Expression joinExpression(String op, Expression exp1, Expression exp2) {
		Expression exp = new Expression();
		if(exp2 != null)
			exp.code = Expression.joinExpressionCode(op, exp1.code, exp2.code); 
		else
			exp.code = Expression.joinExpressionCode(op, exp1.code, null); 
		
		if(!exp1.isSemanticallyFine() || (exp2 != null && !exp2.isSemanticallyFine())) {
			exp.semantically_fine = false;
			exp.quiet = true;
			
			return exp;
		}
		Type return_type = Type.binaryOP(op, exp1.return_type, exp2.return_type);
		exp.return_type = return_type;
	    if(return_type == null){ 
	    	exp.semantically_fine = false;
	    }
	    return exp;

	}
}
