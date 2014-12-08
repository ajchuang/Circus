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
		
		if(type.equals(Type.SWITCH)){
			return SymbolTable.switchBlock.accessSymbolInThisScope(id);
		}
		else if(type.equals(Type.FLOW)){
			return SymbolTable.flowBlock.accessSymbolInThisScope(id);
		}
//		else if(type.primary_type == PrimaryType.LIST){
//			if(id.equals("add")){
//				FunctionObj func = new FunctionObj();
//				func.id = "add";
//				func.return_type = Type.VOID;
//				func.parameters = new ArrayList<AttributeObj>();
//				func.parameters.add(new AttributeObj("obj", type.second_type));
//				SymbolRecord sr = new SymbolRecord("add");
//				sr.setValue(false, func);
//				return sr;
//			}
//			else if(id.equals("get")){
//				FunctionObj func = new FunctionObj();
//				func.id = "get";
//				func.return_type = type.second_type;
//				func.parameters = new ArrayList<AttributeObj>();
//				func.parameters.add(new AttributeObj("index", type.INTEGER));
//				SymbolRecord sr = new SymbolRecord("get");
//				sr.setValue(false, func);
//				return sr;
//			}
//			else if(id.equals("clear")){
//				FunctionObj func = new FunctionObj();
//				func.id = "clear";
//				func.return_type = Type.VOID;
//				func.parameters = new ArrayList<AttributeObj>();
//				SymbolRecord sr = new SymbolRecord("clear");
//				sr.setValue(false, func);
//				return sr;
//			}
//			return null;
//		}
//		else if(type.primary_type == PrimaryType.DICT){
//			if(id.equals("put")){
//				FunctionObj func = new FunctionObj();
//				func.id = "put";
//				func.return_type = Type.VOID;
//				func.parameters = new ArrayList<AttributeObj>();
//				func.parameters.add(new AttributeObj("key", type.second_type));
//				func.parameters.add(new AttributeObj("value", type.third_type));
//				SymbolRecord sr = new SymbolRecord("put");
//				sr.setValue(false, func);
//				return sr;
//			}
//			else if(id.equals("get")){
//				FunctionObj func = new FunctionObj();
//				func.id = "get";
//				func.return_type = type.third_type;
//				func.parameters = new ArrayList<AttributeObj>();
//				func.parameters.add(new AttributeObj("key", type.second_type));
//				SymbolRecord sr = new SymbolRecord("get");
//				sr.setValue(false, func);
//				return sr;
//			}
//			
//			return null;
//		}
//			
//		
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
