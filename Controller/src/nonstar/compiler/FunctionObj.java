package nonstar.compiler;


import java.util.ArrayList;

public class FunctionObj {
	
	public String id;
	public Type return_type;
	
	public ArrayList<AttributeObj> parameters;
	public String body;
	
	@Override
	public boolean equals(Object obj){
		if(obj == null)
			return false;
		if(!(obj instanceof FunctionObj))
			return false;
		FunctionObj func = (FunctionObj)obj;
		if(!this.id.equals(func.id) || !this.return_type.equals(func.return_type) 
				|| this.parameters.size()!=func.parameters.size())
			return false;
		int i;
		for(i=0;i<this.parameters.size();i++)
			if(!this.parameters.get(i).equalsAsPara(func.parameters.get(i)))
				return false;
		return true;
	}

}
