package nonstar.compiler;

import java.util.ArrayList;

public class Type {
	
	public static Type INTEGER = new Type(PrimaryType.INTEGER, null, null);
	public static Type BOOLEAN = new Type(PrimaryType.BOOLEAN, null, null);
	public static Type VOID = new Type(PrimaryType.VOID, null, null);
	public static Type NULL = new Type(PrimaryType.NULL, null, null);
	public static Type STRING = new Type(PrimaryType.STRING, null, null);
	public static Type FLOW = new Type(PrimaryType.FLOW, null, null);
	public static Type SWITCH = new Type(PrimaryType.SWITCH, null, null);
	
	private PrimaryType primary_type;
	private Type second_type; // element of list OR key of dict
	private Type third_type; // value of dict
	
	public Type(PrimaryType primary_type, Type second_type, Type third_type) {
		super();
		this.primary_type = primary_type;
		this.second_type = second_type;
		this.third_type = third_type;
	}
	
	public static boolean iterable(Type item, Type list) {
		if(!PrimaryType.iterable(list.primary_type)){
			return false;
		}
		return item.equals(list.second_type);
	}
	
	public boolean isDict() {
		return this.primary_type == PrimaryType.DICT;
	}
	
	public boolean isList() {
		return this.primary_type == PrimaryType.LIST;
	}
	
	public String defaultInitialization() {
		String tail;
		if(this.isDict() || this.isList()) {
			tail = " = new " + toString() + "()";
		}
		else if(PrimaryType.nullable(this.primary_type)) {
			tail = " = null";
		}
		else if(this.primary_type == PrimaryType.INTEGER) {
			tail = " = 0";
		}
		else if(this.primary_type == PrimaryType.BOOLEAN) {
			tail = " = true";
		}
		else if(this.primary_type == PrimaryType.STRING) {
			tail = " = \"\"";
		}
		else{
			return "";
		}
		return tail;
	}
	
	public static Type unaryOP(String op, Type type){
		if("!".equals(op) || "~".equals(op)){
			if(type.primary_type==PrimaryType.BOOLEAN){
				return BOOLEAN;
			}
		}
		return null;
	}
	
	public static Type binaryOP(String op, Type type1, Type type2){
		if("&&".equals(op) || "||".equals(op)){
			if(type1.primary_type==PrimaryType.BOOLEAN && type2.primary_type==PrimaryType.BOOLEAN){
				return BOOLEAN;
			}
		}
		if(">".equals(op) || "<".equals(op) || "==".equals(op) || ">=".equals(op) || "<=".equals(op) || "!=".equals(op) ){
			if(type1.primary_type==PrimaryType.INTEGER && type2.primary_type==PrimaryType.INTEGER){
				return BOOLEAN;
			}
			if("==".equals(op) && (type1.primary_type == PrimaryType.NULL && PrimaryType.nullable(type2.primary_type) 
					|| type2.primary_type == PrimaryType.NULL && PrimaryType.nullable(type1.primary_type) )){
				return BOOLEAN;
			}
		}
		if("-".equals(op) || "*".equals(op) || "/".equals(op) || "%".equals(op) ){
			if(type1.primary_type==PrimaryType.INTEGER && type2.primary_type==PrimaryType.INTEGER){
				return INTEGER;
			}
		}
		if("+".equals(op)){
			if(type1.primary_type==PrimaryType.INTEGER && type2.primary_type==PrimaryType.INTEGER){
				return INTEGER;
			}
			if(type1.primary_type==PrimaryType.STRING && type2.primary_type!=PrimaryType.LIST 
					&& type2.primary_type!=PrimaryType.DICT){
				return STRING;
			}
				
		}
		return null;
	}
	
	public static SymbolRecord dotOP(Type type, String id){
		
		SymbolRecord sr = null;
		if(type.equals(Type.SWITCH)){
			return SymbolTable.switchBlock.accessSymbolInThisScope(id);
		}
		else if(type.equals(Type.FLOW)){
			return SymbolTable.flowBlock.accessSymbolInThisScope(id);
		}
		else if(type.primary_type == PrimaryType.LIST){
			FunctionObj func = new FunctionObj();
			func.id = id;
			func.parameters = new ArrayList<AttributeObj>();
			boolean fail = false;
			
			if(id.equals("add")){	
				func.return_type = Type.VOID;	
				func.parameters.add(AttributeObj.newAttributeObjByTypeID(type.second_type, "obj"));
			}
			else if(id.equals("get")){
				func.return_type = type.second_type;		
				func.parameters.add(AttributeObj.newAttributeObjByTypeID(Type.INTEGER, "index"));
			}
			else if(id.equals("size")){
				func.return_type = Type.INTEGER;
			}
			else if(id.equals("clear")){
				func.return_type = Type.VOID;
			}
			else{
				fail = true;
			}
			if(!fail) {
				sr = new SymbolRecord(id);
				sr.setValue(false, func);
			}
			return sr;
		}
		else if(type.primary_type == PrimaryType.DICT){
			FunctionObj func = new FunctionObj();
			func.id = id;
			func.parameters = new ArrayList<AttributeObj>();
			boolean fail = false;
			
			if(id.equals("put")){
				func.return_type = Type.VOID;
				func.parameters.add(AttributeObj.newAttributeObjByTypeID(type.second_type, "key"));
				func.parameters.add(AttributeObj.newAttributeObjByTypeID(type.third_type, "value"));
				
			}
			else if(id.equals("get")){
				func.return_type = type.third_type;
				func.parameters.add(AttributeObj.newAttributeObjByTypeID(type.second_type, "key"));
				
			}
			else if(id.equals("clear")){
				func.return_type = Type.VOID;
			}
			sr = new SymbolRecord(id);
			sr.setValue(false, func);
			return sr;
		}		
		
		return null;
	}
	
	@Override
	public boolean equals(Object type){
		if(type instanceof Type){
			Type t = (Type)type;
			if(this.primary_type!=t.primary_type){
				if((PrimaryType.nullable(this.primary_type) && PrimaryType.isNull(t.primary_type))
						|| (PrimaryType.nullable(t.primary_type) && PrimaryType.isNull(this.primary_type)))
					return true;
				return false;
			}
			if(this.primary_type!=PrimaryType.LIST && this.primary_type!=PrimaryType.DICT){
				return true;
			}
			if(!this.second_type.equals(t.second_type)){
				return false;
			}
			if(this.primary_type!=PrimaryType.DICT){
				return true;
			}
			return this.third_type.equals(t.third_type);
		}
		return false;
	}
	
	public boolean equalsAsPara(Type para){
		return this.equals(para) 
				|| (this.primary_type == PrimaryType.LIST 
				&& para.primary_type == PrimaryType.LIST && para.second_type == null);
	}
	
	public String toObjString(){
		switch(primary_type){
		case INTEGER:
			return "Integer";
		case  DOUBLE:
			return "Double";
		case  BOOLEAN:
			return "Boolean";
		default:
			return this.toString();
		}
	}
	
	@Override
	public String toString(){
		switch(primary_type){
		case INTEGER:
			return "int";
		case  DOUBLE:
			return "double";
		case  STRING:
			return "String";
		case  BOOLEAN:
			return "boolean";
		case  FLOW:
			return "Flow";
		case  SWITCH:
			return "Switch";
		case  VOID:
			return "void";
		case  LIST:
			return "ArrayList<"+second_type.toObjString()+">";
		case  DICT:
			return "HashMap<"+second_type.toObjString()+","+third_type.toObjString()+">";	
		default:
			System.out.println("An unknown type!");
			return null;
		}
	}

}
