package nonstar.compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Util {
	
	public static String templatePath = "nonstar/compiler/NonstarTemplate.txt";
	public static String targetPath = "nonstar/controller/NonstarTest.java";
	
	public static FunctionObj onstart = new FunctionObj();
	public static FunctionObj onreq = new FunctionObj();
	
	public static void init() {
		
		onstart.id = "on_start";
		onstart.return_type = Type.VOID;
		onstart.parameters = new ArrayList<AttributeObj>();
		onstart.body = "";
		
		onreq.id = "on_req";
		onreq.return_type = Type.FLOW;
		onreq.parameters = new ArrayList<AttributeObj>();
		onreq.parameters.add(AttributeObj.newAttributeObjByTypeID(Type.SWITCH, "src"));
		onreq.parameters.add(AttributeObj.newAttributeObjByTypeID(Type.SWITCH, "dst"));
		onreq.body = "\t\treturn null;";
	}
	
	public static void genNonstar(ArrayList<AttributeObj> nonstar_config, ArrayList<FunctionObj> procedures) {
		
		BufferedReader reader; 
		BufferedWriter writer;
		String line = null;
		StringBuilder sb = null;
		
		try {
			reader = new BufferedReader(new FileReader(templatePath));
			writer  = new BufferedWriter(new FileWriter(targetPath));
			int count = 0;
			boolean saw_onstart = false;
			boolean saw_onreq = false;
			String data = reader.readLine();  
			while( data!=null){  
				
				if(data.indexOf("###")!=-1){
					count++;
					//String[] parts = data.split("###");
					switch (count){
					case 1:
						sb = new StringBuilder();
						sb.append("\t");
						for(AttributeObj attr : nonstar_config){
							strBuildAttribute(sb, attr);
						}
						
						for(FunctionObj func : procedures){
							if(func.equals(onstart))
								saw_onstart = true;
							if(func.equals(onreq))
								saw_onreq = true;
							
							strBuildFunction(sb, func);
						}
						
						if(!saw_onstart){
							strBuildFunction(sb, onstart);
						}
						
						if(!saw_onreq){
							strBuildFunction(sb, onreq);
						}
						
						line = data.replaceAll("###", sb.toString());
						break;
					default:
						System.out.println("What happened???");
						break;
					}
					writer.write(line + "\n");
				}
				else{
					writer.write(data + "\n");
				}
				
				
				data = reader.readLine();   
			} 
			writer.flush();  
		    reader.close();  
		    writer.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
	}
	
	public static void strBuildAttribute(StringBuilder sb, AttributeObj attr) {
		sb.append(attr.type);
		sb.append(" ");
		sb.append(attr.id);
		sb.append(" = ");
		sb.append(attr.value);
		sb.append(";\n\t");
	}
	
	public static void strBuildFunction(StringBuilder sb, FunctionObj func) {
		sb.append("public ");
		sb.append(func.return_type);
		sb.append(" ");
		sb.append(func.id);
		sb.append("(");
		boolean first = true;
		for(AttributeObj para : func.parameters) {
			if(first){
				first = false;
			}
			else {
				sb.append(", ");
			}
			sb.append(para.type);
			sb.append(" ");
			sb.append(para.id);
		}
		sb.append(") ");
		sb.append(" {\n\t");
		sb.append(func.body);
		sb.append("\n\t}\n\t");
	}
	
	public static void main(String[] args) {
		
		if(args.length >= 2){
			templatePath = args[0];
			targetPath = args[1];
		}
		
		init();
		
		ArrayList<AttributeObj> config = new ArrayList<AttributeObj>();
		ArrayList<FunctionObj> procedures = new ArrayList<FunctionObj>();
		AttributeObj flag = AttributeObj.newAttributeObjByTypeValue(Type.BOOLEAN, "true");
		flag.id = "flag";
		config.add(flag);
		procedures.add(onstart);
		procedures.add(onreq);
		
		genNonstar(config, procedures);
		
	}

}
