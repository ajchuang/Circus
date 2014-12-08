package nonstar.compiler;

public enum PrimaryType {
	
	INTEGER, DOUBLE, STRING, BOOLEAN, FLOW, SWITCH, VOID, LIST, DICT,
	NULL;

	public static boolean isPrimary(PrimaryType type) {
		if(type == PrimaryType.INTEGER || type == PrimaryType.DOUBLE || 
				type == PrimaryType.BOOLEAN || type == PrimaryType.STRING ) {
			return true;
		}
		return false;
	}
	
	public static boolean isVoid(PrimaryType type) {
		return (type == PrimaryType.VOID);
	}
	
	public static boolean isNull(PrimaryType type) {
		return (type == PrimaryType.NULL);
	}
	
	public static boolean nullable(PrimaryType type) {
		return !isPrimary(type) && !isVoid(type) && !isNull(type);
	}
	
	public static boolean iterable(PrimaryType type) {
		return type == PrimaryType.LIST || type == PrimaryType.DICT;
	}
}
