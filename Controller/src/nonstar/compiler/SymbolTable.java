package nonstar.compiler;

import java.util.*;

public class SymbolTable {
	
	public static int current; // 1 for nonstar_config, 2 for afterwards
	
	public static ScopeBlock reservedBlock = new ScopeBlock();
	
	public static ScopeBlock nonstarBlock = new ScopeBlock();
	
	public static ScopeBlock flowBlock = new ScopeBlock();
	public static ScopeBlock switchBlock = new ScopeBlock();
	
	public static FunctionObj curFunction = null;
	public static boolean curFuncHasReturn = false; // whether current function has returned (might be of wrong type)
	public static boolean curFuncCorrectReturn = true; // whether current function has correct typed return
	public static LinkedList<ScopeBlock> function_locals = new LinkedList<ScopeBlock>();
	
	//public static HashSet<String> current_all_IDs = null;
	public static HashSet<String> all_IDs = new HashSet<String>();
	
	public static String errMsgNewID(String id) {
		if(current == 1)
			return id + " is not an available name in nonstar config";
		return id + " is not an available name in current scope";
	}
	
	public static void newLocalBlock() {
		function_locals.addFirst(new ScopeBlock());
	}

	public static void popLocalBlock() {
		function_locals.removeFirst();
	}
	
	public static SymbolRecord accessID(String ID){
		for(ScopeBlock sb : function_locals){
			if(sb.alreadyHave(ID))
				return sb.accessSymbolInThisScope(ID);
		}

		if(nonstarBlock.alreadyHave(ID)){
			return nonstarBlock.accessSymbolInThisScope(ID);
		}
		return null;
	}
	
	public static boolean putNonstar(String id, boolean isAttribute, Object attrOrFunc){
		if(reservedBlock.alreadyHave(id)){
			return false;
		}
		
		if(nonstarBlock.alreadyHave(id) || all_IDs.contains(id)){
			return false;
		}
		else{
			SymbolRecord sr = nonstarBlock.addRecord(id);
			sr.setValue(isAttribute, attrOrFunc);
			all_IDs.add(id);
			return true;
		}
	}

	
	public static boolean putInLocal(String id, AttributeObj attr){
		if(reservedBlock.alreadyHave(id)){
			return false;
		}
		
		if(nonstarBlock.alreadyHave(id))
			return false;
		for(ScopeBlock sb : function_locals){
			if(sb.alreadyHave(id))
				return false;
		}
		
		ScopeBlock sb = function_locals.getFirst();
		SymbolRecord sr = sb.addRecord(id);
		sr.setValue(true, attr);
		all_IDs.add(id);
		return true;
	}
	
	public static void addAttributeRecordToScope(String id, ScopeBlock scope, Type type){
		SymbolRecord sr = scope.addRecord(id);
		sr.setValue(true, AttributeObj.newAttributeObjByTypeID(type, id));
	}
	
	public static void addFunctionRecordToScope(String id, ScopeBlock scope, 
			Type return_type, ArrayList<AttributeObj> parameters){
		SymbolRecord sr = scope.addRecord(id);
		FunctionObj func = new FunctionObj();
		func.id = id;
		func.return_type = return_type;
		func.parameters = new ArrayList<AttributeObj>(parameters);
		sr.setValue(false, func);
	}
	
	public static void initSymbolTable(){
//				
		// Can not be used in Nonstar because they are types/keywords in Java 
		String[] java_reserved = {"Exception", "boolean", "Integer", "Boolean", 
				"try", "final", "finally", "System", "catch", "public", "protected", 
				"private", "static", "HashMap", "ArrayList", "java"};
		// Can not be used in Nonstar because they are Nonstar keywords
		String[] nonstar_keywords = {"Nonstar", "void", "int", "String", "boolean", 
				"true", "false", "in"};
		// Can not be used in Nonstar because they are used in templates of target code
		String[] nonstar_reserved = {"NenstarBase", "NonstarTemplate", "NetworkTopo", 
				"Controller", "onstart", "onreq", "nextOnlinePlayer", "nonstar", "controller",
				"Flow", "Switch"};	
		
		for(String id : java_reserved){
			reservedBlock.addRecord(id);
			all_IDs.add(id);
		}
		for(String id : nonstar_keywords){
			reservedBlock.addRecord(id);
			all_IDs.add(id);
		}
		for(String id : nonstar_reserved){
			reservedBlock.addRecord(id);
			all_IDs.add(id);
		}
		
		ArrayList<AttributeObj> parameters;
		parameters = new ArrayList<AttributeObj>();
		parameters.add(AttributeObj.newAttributeObjByTypeID(Type.SWITCH, "src"));
		parameters.add(AttributeObj.newAttributeObjByTypeID(Type.SWITCH, "dst"));
		addFunctionRecordToScope("getCurrCircuit", nonstarBlock, Type.FLOW, parameters);
		addFunctionRecordToScope("setupCircuit", nonstarBlock, Type.FLOW, parameters);
		
//		ArrayList<AttributeObj> parameters;
//		addAttributeRecordToScope("id", playerBlock, Type.INTEGER);
//		addAttributeRecordToScope("handCards", playerBlock, new Type(PrimaryType.LIST,Type.CARD,null));
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("character", Type.CHARACTER));
//		addFunctionRecordToScope("setCharacter", playerBlock, Type.VOID, parameters);
//		
//		addAttributeRecordToScope("playerList", gameBlock, new Type(PrimaryType.LIST, Type.PLAYER, null));
//		addAttributeRecordToScope("cardStack", gameBlock, new Type(PrimaryType.LIST, Type.CARD, null));
//		addAttributeRecordToScope("droppedCardStack", gameBlock, new Type(PrimaryType.LIST, Type.CARD, null));
//		addAttributeRecordToScope("gameover", gameBlock, Type.BOOLEAN);
//		addAttributeRecordToScope("roundSummary", gameBlock, new Type(PrimaryType.DICT, Type.INTEGER, Type.CARD));
//		
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("msg", Type.STRING));
//		addFunctionRecordToScope("broadcast", gameBlock, Type.VOID, parameters);
//		
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		parameters.add(new AttributeObj("msg", Type.STRING));
//		addFunctionRecordToScope("sendToOnePlayer", gameBlock, Type.VOID, parameters);
//		
//		parameters = new ArrayList<AttributeObj>();
//		addFunctionRecordToScope("GameGeneralInfo", gameBlock, Type.STRING, parameters);
//		parameters = new ArrayList<AttributeObj>();
//		addFunctionRecordToScope("PlayersInfo", gameBlock, Type.STRING, parameters);
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		addFunctionRecordToScope("HandCardInfo", gameBlock, Type.STRING, parameters);
//		
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		parameters.add(new AttributeObj("promt", Type.STRING));
//		parameters.add(new AttributeObj("range", Type.INTEGER));
//		addFunctionRecordToScope("waitForChoice", gameBlock, Type.INTEGER, parameters);
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		addFunctionRecordToScope("waitForSkill", gameBlock, Type.BOOLEAN, parameters);
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		addFunctionRecordToScope("waitForTarget", gameBlock, Type.PLAYER, parameters);
//		
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		addFunctionRecordToScope("putCard", gameBlock, Type.CARD, parameters);
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("player", Type.PLAYER));
//		parameters.add(new AttributeObj("num", Type.INTEGER));
//		addFunctionRecordToScope("drawCard", gameBlock, Type.VOID, parameters);
//		
//		parameters = new ArrayList<AttributeObj>();
//		parameters.add(new AttributeObj("list", new Type(PrimaryType.LIST, null, null)));
//		addFunctionRecordToScope("shuffle", gameBlock, Type.VOID, parameters);
//		
	}

	
}
