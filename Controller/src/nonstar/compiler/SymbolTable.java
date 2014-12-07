package nonstar.compiler;

import java.util.*;

public class SymbolTable {
	
	public static int current; // 1 for nonstar_config, 2 for afterwards
	
	public static ScopeBlock playerBlock = new ScopeBlock();
	
	public static ScopeBlock reservedBlock = new ScopeBlock();
	
	public static ScopeBlock circuitBlock = new ScopeBlock();
	public static ScopeBlock switchBlock = new ScopeBlock();
	
	public static FunctionObj curFunction = null;
	public static LinkedList<ScopeBlock> function_locals = new LinkedList<ScopeBlock>();
	
	public static HashSet<String> current_skill_IDs = null;
	public static HashSet<String> current_all_IDs = null;
	public static HashSet<String> all_IDs = new HashSet<String>();
	
	public static HashSet<String> card_names = new HashSet<String>();
	public static HashSet<String> character_names = new HashSet<String>();
	
	
	public static void newLocalBlock() {
		function_locals.addFirst(new ScopeBlock());
	}

	public static void popLocalBlock() {
		function_locals.removeFirst();
	}
	
	public static SymbolRecord accessID(String ID){
//		for(ScopeBlock sb : function_locals){
//			if(sb.alreadyHave(ID))
//				return sb.accessSymbolInThisScope(ID);
//		}
//		if((current == 2||current == 3) && currentCardCharacterBlock.alreadyHave(ID)){
//			return currentCardCharacterBlock.accessSymbolInThisScope(ID);
//		}
//		if(gameBlock.alreadyHave(ID)){
//			return gameBlock.accessSymbolInThisScope(ID);
//		}
		return null;
	}
//	
//	public static boolean putInGame(String id, boolean isAttribute, Object attrOrFunc){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id) || all_IDs.contains(id)){
//			return false;
//		}
//		else{
//			SymbolRecord sr = gameBlock.addRecord(id);
//			sr.setValue(isAttribute, attrOrFunc);
//			all_IDs.add(id);
//			return true;
//		}
//	}
	
//	public static boolean putNewCardName(String id){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id) || all_IDs.contains(id)){
//			return false;
//		}
//		else{
//			card_names.add(id);
//			all_IDs.add(id);
//			return true;
//		}
//	}
	
//	public static boolean putNewCharacterName(String id){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id) || all_IDs.contains(id)){
//			return false;
//		}
//		else{
//			character_names.add(id);
//			all_IDs.add(id);
//			return true;
//		}
//	}
//	
//	public static boolean putInCard(String id, boolean isAttribute, Object attrOrFunc){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id) || currentCardCharacterBlock.alreadyHave(id) || current_all_IDs.contains(id) ){
//			return false;
//		}
//		else{
//			SymbolRecord sr = currentCardCharacterBlock.addRecord(id);
//			sr.setValue(isAttribute, attrOrFunc);
//			
//			if(firstCard){
//				sr = baseCardBlock.addRecord(id);
//				sr.setValue(isAttribute, attrOrFunc);
//			}
//			
//			all_IDs.add(id);
//			current_all_IDs.add(id);
//			return true;
//		}
//	}
//	
//	public static boolean putInCharacter(String id, boolean isAttribute, Object attrOrFunc){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id) || currentCardCharacterBlock.alreadyHave(id) || current_all_IDs.contains(id) ){
//			return false;
//		}
//		else{
//			SymbolRecord sr = currentCardCharacterBlock.addRecord(id);
//			sr.setValue(isAttribute, attrOrFunc);
//			
//			if(firstCharacter){
//				sr = baseCharacterBlock.addRecord(id);
//				sr.setValue(isAttribute, attrOrFunc);
//			}
//			
//			all_IDs.add(id);
//			current_all_IDs.add(id);
//			return true;
//		}
//	}
//	
//	public static boolean putInSkill(String id){
//		if(current_skill_IDs.contains(id))
//			return false;
//		current_skill_IDs.add(id);
//		return true;
//	}
//	
//	public static boolean putInLocal(String id, AttributeObj attr){
//		if(reservedBlock.alreadyHave(id)){
//			return false;
//		}
//		
//		if(gameBlock.alreadyHave(id))
//			return false;
//		for(ScopeBlock sb : function_locals){
//			if(sb.alreadyHave(id))
//				return false;
//		}
//		if(( current == 2 || current == 3) && currentCardCharacterBlock.alreadyHave(id) )
//			return false;
//		if( current == 2 && baseCardBlock.alreadyHave(id) )
//			return false;
//		if( current == 3 && baseCharacterBlock.alreadyHave(id))
//			return false;
//		
//		ScopeBlock sb = function_locals.getFirst();
//		SymbolRecord sr = sb.addRecord(id);
//		sr.setValue(true, attr);
//		
//		return true;
//	}
//	
//	public static void addAttributeRecordToScope(String id, ScopeBlock scope, Type type){
//		SymbolRecord sr = scope.addRecord(id);
//		sr.setValue(true, new AttributeObj(id, type));
//	}
//	
//	public static void addFunctionRecordToScope(String id, ScopeBlock scope, 
//			Type return_type, ArrayList<AttributeObj> parameters){
//		SymbolRecord sr = scope.addRecord(id);
//		FunctionObj func = new FunctionObj();
//		func.id = id;
//		func.return_type = return_type;
//		func.parameters = parameters;
//		sr.setValue(false, func);
//	}
	
	public static void initSymbolTable(){
//				
//		// Can not be used in GameWizard because they are types in Java 
//		String[] java_keywords = {"IOException", "ArrayList", "Collections", 
//				"HashMap", "Map", "LinkedList", "List", "Array", "Integer"};
//		// Can not be used in GameWizard because they are GameWizard keywords
////		String[] game_wizard_keywords = {"define", "game", "cards", "characters", 
////				"method", "Player", "skill", "void", "int", "String", "boolean", 
////				"init", "round_begin", "turn", "round_end", "true", "false", "in"};
//		// Can not be used in GameWizard because they are used in templates of target code
//		String[] game_wizard_reserved = {"CharacterBase", "CardBase", "GameServer", "port", 
//				"map", "currentPlayerIndex", "roundCount", "nextOnlinePlayer"};
//		
//		
//		for(String id : java_keywords){
//			reservedBlock.addRecord(id);
//			all_IDs.add(id);
//		}
//		for(String id : game_wizard_reserved){
//			reservedBlock.addRecord(id);
//			all_IDs.add(id);
//		}
//		
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
