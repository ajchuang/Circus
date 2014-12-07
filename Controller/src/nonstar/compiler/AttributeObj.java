package nonstar.compiler;

public class AttributeObj {
	
	public Type type;
	public String value;
	public String id;
	
	public AttributeObj(Type type, String value, String id) {
		super();
		this.type = type;
		this.value = value;
		this.id = id;
	}
	
	public static AttributeObj newAttributeObjByTypeValue(Type type, String value) {
		return new AttributeObj(type, value, null);
	}
	
	public static AttributeObj newAttributeObjByTypeID(Type type, String id) {
		return new AttributeObj(type, null, id);
	}

	@Override
	public boolean equals(Object attr){
		if(attr == null)
			return false;
		if(! (attr instanceof AttributeObj))
			return false;
		AttributeObj aobj = (AttributeObj)attr;
		return (this.type.equals(aobj.type) && this.id.equals(aobj.id));	
	}
	
	public boolean equalsAsPara(AttributeObj para){
		if(para == null)
			return false;
		return this.type.equals(para.type);
	}

}
