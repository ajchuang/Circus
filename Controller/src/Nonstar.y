/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2001 Gerwin Klein <lsf@jflex.de>                          *
 * All rights reserved.                                                    *
 *                                                                         *
 * This is a modified version of the example from                          *
 *   http://www.lincom-asg.com/~rjamison/byacc/                            *
 *                                                                         *
 * Thanks to Larry Bell and Bob Jamison for suggestions and comments.      *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

%{
	import java.io.*;
	import java.util.*;
	import nonstar.compiler.*;
%}
      
%token NONSTAR_DF

%token INIT
%token ON_REQ 

%token FLOW
%token SWITCH
%token DICT
%token LIST

%token RETURN

%token IF
%token ELSE
%token WHILE
%token TRUE
%token FALSE
%token FOREACH
%token IS
%token IN
%token OP_EQ
%token OP_LE
%token OP_GE
%token OP_NE
%token OP_LOR
%token OP_LAND

%token DECLR_INT
%token DECLR_STR
%token DECLR_BOOL
%token VOID
%token NULL

%token <ival> INTEGER
%token <sval> STRING
%token <sval> ID
%left '-' '+'

%type <obj> value
%type <obj> nonstar_config
%type <obj> json_item
%type <obj> json_content
%type <obj> json
%type <sval> block

%type <sval> statement_list
%type <sval> statement
%type <obj> type
%type <obj> dict_list_type
%type <obj> non_void_type
%type <obj> parameter_config
%type <obj> parameter_config_list
%type <obj> built_in_function_signature
%type <obj> function_signature
%type <obj> function_definition
%type <obj> procedures_list
%type <obj> primitive_type

%type <sval> selection_statement
%type <sval> if_condition_block
%type <sval> foreach_statement
%type <sval> while_loop_statement
%type <sval> field_declaration
%type <sval> return_statement

%type <obj> Expression
%type <obj> UnaryExpression
%type <obj> ConditionalOrExpression
%type <obj> ConditionalAndExpression
%type <obj> RelationalExpression
%type <obj> MultiplicativeExpression
%type <obj> AdditiveExpression
%type <obj> UnaryExpression
%type <obj> LogicalUnaryExpression
%type <obj> PrimaryExpression
%type <sval> LogicalUnaryOperator
%type <sval> RelationalBinaryOperator
%type <sval> MultiplicativeOperator
%type <sval> AddicativeOperator
%type <obj> QuarlifiedName
%type <obj> ComplexPrimaryNoParenthesis
%type <obj> MethodCall
%type <obj> ArgumentList

%left '*' '/'
%right '^'         /* exponentiation        */
      
%%

source_code: { Util.init(); Util.startCompilingNonstarConfig(); } nonstar_config { Util.startCompilingProcedures(); } procedures_list
  {
    ArrayList<AttributeObj> nonstar_config = (ArrayList<AttributeObj>)$2;
    ArrayList<FunctionObj> procedures = (ArrayList<FunctionObj>)$4;
    Util.genNonstar(nonstar_config, procedures);
    yydebug("source_code finished");
  }
| { Util.init(); Util.startCompilingProcedures(); } procedures_list
  {
    ArrayList<AttributeObj> nonstar_config = new ArrayList<AttributeObj>();
    ArrayList<FunctionObj> procedures = (ArrayList<FunctionObj>)$2;
    Util.genNonstar(nonstar_config, procedures);
    yydebug("source_code finished");
  }
| { Util.init(); Util.startCompilingProcedures(); } procedures_list
  ;
nonstar_config: NONSTAR_DF json
  {
    ArrayList<AttributeObj> nonstar_config = (ArrayList<AttributeObj>)$2;
    yydebug("nonstar_config finished, " + nonstar_config.size() + " json items" );
    $$ = nonstar_config;
  }
  ;
json: '{' json_content '}' { $$ = $2; }
  ;
json_content
: json_item 
  {
    AttributeObj jsonitem = (AttributeObj)$1;
    ArrayList<AttributeObj> json = new ArrayList<AttributeObj>();
    json.add(jsonitem);
    $$ = json;
  }
| json_content json_item
  {
    AttributeObj jsonitem = (AttributeObj)$2;
    ArrayList<AttributeObj> json = (ArrayList<AttributeObj>)$1;
    json.add(jsonitem);
    $$ = json;
  }
  ;
json_item
: ID ':' value ';'
  {
    AttributeObj jsonitem = (AttributeObj)$3;
    jsonitem.id = $1; 
    yydebug("Find json item " + $1 + " (" + jsonitem.type + ", " + jsonitem.value + ")");
    if(!SymbolTable.putNonstar($1, true, jsonitem)){
      yyerror(Util.newIDErr($1)); 
    }
    $$ = jsonitem;
  }
  ;
function_definition
: function_signature
  {
    FunctionObj func = (FunctionObj)$1;
    SymbolTable.newLocalBlock();
    SymbolTable.curFunction = func;
    SymbolTable.curFuncHasReturn = false;
    SymbolTable.curFuncCorrectReturn = true;
    for(AttributeObj para : func.parameters) {
      if(!SymbolTable.putInLocal(para.id, para)) {
        yyerror(Util.newIDErr(para.id));    
      }
    }
  } 
block
  {
    FunctionObj func = (FunctionObj)$1;
    func.body = $3;
    if(!SymbolTable.curFuncHasReturn && !func.return_type.equals(Type.VOID)){
      yyerror("Function " + func.id + " does not have proper return");
    }
    SymbolTable.popLocalBlock();
    yydebug("Fucntion " + func.id + " compiled");
    $$ = func;
  }
  ;
function_signature
: built_in_function_signature { $$ = $1; }
| type ID '(' parameter_config_list ')'
  {
    FunctionObj func = new FunctionObj();
    func.return_type = (Type)$1;
    func.id = $2;
    func.parameters =  (ArrayList<AttributeObj>)$4;
    if(!SymbolTable.putNonstar(func.id, false, func)){
      yyerror(Util.newIDErr(func.id)); 
    }
    else {
      $$ = func;
    }
  }
| type ID '(' ')'
  {
    FunctionObj func = new FunctionObj();
    func.return_type = (Type)$1;
    func.id = $2;
    func.parameters = new ArrayList<AttributeObj>();
    if(!SymbolTable.putNonstar(func.id, false, func)){
      yyerror(Util.newIDErr(func.id)); 
    }
    else {
      $$ = func;
    }
  }
  ;
built_in_function_signature
: INIT
  {
    FunctionObj func = FunctionObj.copyFunctionSignature(Util.onstart);
    if(!SymbolTable.putNonstar(func.id, false, func)){
      yyerror(Util.newIDErr(func.id)); 
    }
    else {
      $$ = func;
    }
  }
| ON_REQ
  {
    FunctionObj func = FunctionObj.copyFunctionSignature(Util.onreq);
    if(!SymbolTable.putNonstar(func.id, false, func)){
      yyerror(Util.newIDErr(func.id)); 
    }
    else {
      $$ = func;
    }
  }
  ; 
parameter_config_list
: parameter_config 
  {
    AttributeObj para = (AttributeObj)$1;
    ArrayList<AttributeObj> para_list = new ArrayList<AttributeObj>();
    para_list.add(para);
    $$ = para_list;
  }
| parameter_config_list ',' parameter_config
  {
    AttributeObj para = (AttributeObj)$3;
    ArrayList<AttributeObj> para_list = (ArrayList<AttributeObj>)$1;
    para_list.add(para);
    $$ = para_list;
  }
  ;
parameter_config: type ID
  {
    Type type = (Type)$1;
    AttributeObj para = AttributeObj.newAttributeObjByTypeID(type, $2);
    $$ = para;
  }
  ;
procedures_list
: function_definition 
  {	
    FunctionObj func = (FunctionObj)$1;
    ArrayList<FunctionObj> functions = new ArrayList<FunctionObj>();
    functions.add(func);
    $$ = functions;
  }
| procedures_list function_definition
  {
    FunctionObj func = (FunctionObj)$2;
    ArrayList<FunctionObj> functions = (ArrayList<FunctionObj>)$1;
    functions.add(func);
    $$ = functions;
  }
  ;
type
: non_void_type { $$ = $1; }
| VOID { $$ = Type.VOID; }
  ;
non_void_type
: FLOW { $$ = Type.FLOW; }
| SWITCH { $$ = Type.SWITCH; }
| dict_list_type 
  {
    yydebug("type -> dict_list_type");
    $$ = $1;
  }
| primitive_type { $$ = $1; }
  ;
dict_list_type
: LIST '<' type '>' 
  {
    Type second_type = (Type)$3;
    $$ = new Type(PrimaryType.LIST, second_type, null);
  }
| DICT '<' type ',' type '>' 
  {
    yydebug("dict type");
    Type second_type = (Type)$3;
    Type third_type = (Type)$5;
    $$ = new Type(PrimaryType.DICT, second_type, third_type);
  }
  ;
primitive_type
: DECLR_INT { $$ = Type.INTEGER; }
| DECLR_STR { $$ = Type.STRING; }
| DECLR_BOOL { $$ = Type.BOOLEAN; }
  ;
value
: INTEGER { $$=AttributeObj.newAttributeObjByTypeValue(Type.INTEGER, Integer.toString($1)); }
| STRING { $$=AttributeObj.newAttributeObjByTypeValue(Type.STRING, $1); }
| TRUE { $$=AttributeObj.newAttributeObjByTypeValue(Type.BOOLEAN, "true"); }
| FALSE { $$=AttributeObj.newAttributeObjByTypeValue(Type.BOOLEAN, "false"); }
| NULL { $$=AttributeObj.newAttributeObjByTypeValue(Type.NULL, "null");}
| dict_list_type '(' ')' 
  {
    yydebug("value -> dict_list_type");
    Type type = (Type)$1;
    $$=AttributeObj.newAttributeObjByTypeValue(type, type.defaultInitialization());
  }
  ;
/* ----------------------------------------------------------- */
block
: '{' statement_list '}' { $$ = $2; }
| '{' '}' { $$ = ""; }
  ;
statement_list
: statement_list statement 
  {
    yydebug("Compiled\n  " + $2);
    $$ = $1 + "\t\t" + $2 + "\n"; 
  }
| statement 
  {
    yydebug("Compiled\n  " + $1);
    $$ = $1 + "\n";
  }
  ;
statement
: selection_statement { $$ = $1; }
| field_declaration { $$ = $1; }
| while_loop_statement { $$ = $1; }
| Expression ';'
  {
    Expression exp = (Expression)$1;
    $$ = exp.code + ";";
  }
| QuarlifiedName '=' Expression ';'
  {
    QuarlifiedName qn = (QuarlifiedName)$1;
    Expression exp = (Expression)$3;
    if(exp.isSemanticallyFine() && qn.isSemanticallyFine() && !qn.sr.getType().equals(exp.return_type)){
      yyerror(Util.assignExpErr(qn.code, exp.code));
    }
    $$ = qn.code + " = " + exp.code + ";";
  }
| return_statement { $$ = $1; }
| foreach_statement { $$ = $1; }
  ;
return_statement
: RETURN Expression ';'
  {
    Expression exp = (Expression)$2;
    SymbolTable.curFuncHasReturn = true;
    if(exp.isSemanticallyFine() && !SymbolTable.curFunction.return_type.equals(exp.return_type)){
      SymbolTable.curFuncCorrectReturn = false;
      yyerror(exp.code + " does not match return type of " + SymbolTable.curFunction.id);
    }
    $$ = "return " + exp.code + ";";
  }
| RETURN ';' 
  {
    SymbolTable.curFuncHasReturn = true;
    if(!SymbolTable.curFunction.return_type.equals(Type.VOID)){
      SymbolTable.curFuncCorrectReturn = false;
      yyerror("Return type of " + SymbolTable.curFunction.id + " is not void");
    }
    $$ = "return;";
  }
  ;
selection_statement
: if_condition_block { $$ = $1; }
| if_condition_block ELSE { SymbolTable.newLocalBlock(); } block
  {
    SymbolTable.popLocalBlock();
    $$ = $1 + "\n\t\telse {\n\t\t\t" + $4 + "\n\t\t}\n";
  }
  ;
if_condition_block: IF '(' Expression ')' { SymbolTable.newLocalBlock(); } block
  {
    Expression cond = (Expression)$3;
    if(cond.isSemanticallyFine() && !cond.return_type.equals(Type.BOOLEAN)) {
      yyerror(Util.useExpAsTypeErr(cond.code, Type.BOOLEAN));
    }
    SymbolTable.popLocalBlock();
    $$ = "if(" + cond.code + ") {\n\t\t\t" + $6 + "\n\t\t}\n";
  }
  ;
field_declaration
: non_void_type ID ';'
  {
    Type type = (Type)$1;
    AttributeObj dcl = AttributeObj.newAttributeObjByTypeID(type, $2);
    if(!SymbolTable.putInLocal($2, dcl)){
      yyerror(Util.newIDErr($2)); 
    }
    String tail = type.defaultInitialization();
    if(tail == null){
      yyerror("type error");
    }
    $$ = type.toString() + " " + $2 + " = " + tail + ";";
  }
| non_void_type ID '=' Expression ';' 
  {
    Type type = (Type)$1;
    Expression exp = (Expression)$4;
    AttributeObj dcl = AttributeObj.newAttributeObjByTypeID(type, $2);
    if(!SymbolTable.putInLocal($2, dcl)){
      yyerror(Util.newIDErr($2)); 
    }
    if(exp.isSemanticallyFine() && !type.equals(exp.return_type)){
      yyerror(Util.assignExpErr($2, exp.code));
    }
    $$ = type.toString() + " " + $2 + " = " + exp.code + ";";
  }
  ;
while_loop_statement
: WHILE '(' Expression ')' { SymbolTable.newLocalBlock(); } block
  {
    Expression cond = (Expression)$3;
    if(!cond.isQuiet() && !cond.return_type.equals(Type.BOOLEAN)) {
      yyerror(Util.useExpAsTypeErr(cond.code, Type.BOOLEAN));
    }
    SymbolTable.popLocalBlock();
    $$ = "while(" + cond.code + ") {\n\t\t\t" + $6 + "\n\t\t}\n";
  }
  ;
foreach_statement: FOREACH '(' type ID IN Expression ')' 
  {
    Type type = (Type)$3;
    AttributeObj attr = AttributeObj.newAttributeObjByTypeID(type, $4);
    Expression exp = (Expression)$6;
    if(exp.isSemanticallyFine() && !Type.iterable(type, exp.return_type)){
      yyerror(Util.foreachErr($4, exp.code));
    }
    SymbolTable.newLocalBlock();
    if(!SymbolTable.putInLocal($4, attr)){
      yyerror(Util.newIDErr($4));
    }
  }
block
  {
    Type type = (Type)$3;
    Expression exp = (Expression)$6;
    if(exp.return_type.isDict()) {
      $$ = "for(" + type.toString() + " " + $4 + " : " + exp.code + ".keySet()) {\n\t\t\t" 
        + $9 + "\n\t\t}\n";
    }
    else if(exp.return_type.isList()) {
      $$ = "for(" + type.toString() + " " + $4 + " : " + exp.code + ") {\n\t\t\t" 
        + $9 + "\n\t\t}\n";
    }
    SymbolTable.popLocalBlock();

  }
  ;
MethodCall
: QuarlifiedName '(' ArgumentList ')'
  {
    QuarlifiedName qn = (QuarlifiedName)$1;
    ArrayList<Expression> args = (ArrayList<Expression>)$3;
    Expression exp = new Expression();
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for(Expression e : args) {
      if(first){
        sb.append("(");
        first = false;
      }
      else{
        sb.append(", ");
      }
      sb.append(e.code);
      if(exp.isSemanticallyFine() && !e.isSemanticallyFine()){
        exp.semantically_fine = false;
      }
    }
    sb.append(")");
    if(qn.isSemanticallyFine() && exp.isSemanticallyFine()){
      SymbolRecord sr = qn.sr;
      if(!sr.isAttribute) {
        if(sr.func.parameters.size()!=args.size()) {
          exp.quiet = true;
          exp.semantically_fine = false;
          yyerror(Util.functionArgsErr(sr.func.id));
        }
        int len = args.size();
        for(int i = 0; i<len; i++) {
          if(!sr.func.parameters.get(i).type.equals(args.get(i).return_type)) {
            exp.semantically_fine = false;
            yyerror(Util.useExpAsTypeErr(args.get(i).code, sr.func.parameters.get(i).type));
            break;
          }
        }
        exp.return_type = sr.func.return_type;
      }
      else {
        exp.semantically_fine = false;
        yyerror(Util.quarlifiedNameErr(qn.code, false));
      }
    }
    else{
      exp.semantically_fine = false;
    }
    exp.code = qn.code + sb.toString();
    yydebug(exp.code + " " + exp.isQuiet() + " " + exp.isSemanticallyFine());
    $$ = exp;
  }
| QuarlifiedName '(' ')'   
  {
    QuarlifiedName qn = (QuarlifiedName)$1;
    Expression exp = new Expression();
    if(qn.isSemanticallyFine()){
      SymbolRecord sr = qn.sr;
      if(!sr.isAttribute) {
        if(sr.func.parameters.size()!=0) {
          exp.semantically_fine = false;
          yyerror(Util.functionArgsErr(sr.func.id));
        }
        exp.return_type = sr.func.return_type;
      }
      else {
        exp.semantically_fine = false;
        yyerror(Util.quarlifiedNameErr(qn.code, false));
      }
    }
    else{
      exp.semantically_fine = false;
    }
    exp.code = qn.code + "()";
    $$ = exp;
  }
  ;
ArgumentList
: Expression     
  {
    Expression exp = (Expression)$1;
    ArrayList<Expression> args = new ArrayList<Expression>();
    args.add(exp);
    $$ = args;
  }
| ArgumentList ',' Expression
  {
    Expression exp = (Expression)$3;
    ArrayList<Expression> args = (ArrayList<Expression>)$1;
    args.add(exp);
    $$ = args;
  }
  ;
QuarlifiedName
: QuarlifiedName '.' ID
  {
    QuarlifiedName pre_qn = (QuarlifiedName)$1;
    QuarlifiedName qn = QuarlifiedName.joinQuarlifiedName(pre_qn, $3);
    if(!qn.isQuiet() && !qn.isSemanticallyFine()){
      yyerror(Util.findIDErr($3, pre_qn.code));
    }
    $$ = qn;
  }
| ID
  {
    SymbolRecord sr = SymbolTable.accessID($1);
    QuarlifiedName qn = new QuarlifiedName();
    if(sr == null) {
      qn.semantically_fine = false;
      yyerror(Util.findIDErr($1, null));
    }
    else {
      qn.sr = sr;
    }
    qn.code = $1;
    $$ = qn;
  }
  ;
PrimaryExpression
: '(' Expression ')'
  {
    Expression exp = (Expression)$2;
    exp.code = "(" + exp.code + ")";
    $$ = exp;
  }
| ComplexPrimaryNoParenthesis{$$=$1;}
  ;
ComplexPrimaryNoParenthesis
: value
  {
    Expression exp = new Expression();
    AttributeObj val = (AttributeObj)$1;
    exp.return_type = val.type;
    exp.code = val.value;
    $$ = exp;
  }
| QuarlifiedName
  {
    Expression exp = new Expression();
    QuarlifiedName qn = (QuarlifiedName)$1;
    if(qn.isSemanticallyFine()){
      SymbolRecord sr = qn.sr;
      if(sr.isAttribute) {
        AttributeObj id = sr.attr;
        exp.return_type = id.type;
      }
      else {
        exp.semantically_fine = false;
        yyerror(Util.quarlifiedNameErr(qn.code, true));
      }
    }
    else{
      exp.semantically_fine = false;
    }
    exp.code = qn.code;
    $$ = exp;
  }
| MethodCall
  {
    $$ = $1;
  }
  ;
UnaryExpression
: LogicalUnaryExpression { $$ = $1; }
  ;
LogicalUnaryExpression
: PrimaryExpression { $$ = $1; }
| LogicalUnaryOperator UnaryExpression
  {
    Expression exp1 = (Expression)$2;
    Expression exp = Expression.joinExpression($1, exp1, null);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr($1, exp1.code, null));
    }
    $$ = exp;
  }
  ;
MultiplicativeExpression
: UnaryExpression { $$ = $1; }
| MultiplicativeExpression MultiplicativeOperator  UnaryExpression
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression($2, exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr($2, exp1.code, exp2.code));
    }
    $$ = exp;
  }
  ;
AdditiveExpression
: MultiplicativeExpression { $$ = $1; }
| AdditiveExpression AddicativeOperator MultiplicativeExpression
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression($2, exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr($2, exp1.code, exp2.code));
    }
    $$ = exp;
  }
  ;
RelationalExpression
: AdditiveExpression { $$ = $1; }
| AdditiveExpression '<' AdditiveExpression 
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression("<", exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr("<", exp1.code, exp2.code));
    }
    $$ = exp;
  }
| AdditiveExpression '>' AdditiveExpression 
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression(">", exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr(">", exp1.code, exp2.code));
    }
    $$ = exp;
  }
| AdditiveExpression RelationalBinaryOperator AdditiveExpression
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression($2, exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr($2, exp1.code, exp2.code));
    }
    $$ = exp;
  }
  ;
ConditionalAndExpression
: RelationalExpression    { $$ = $1; }
| ConditionalAndExpression OP_LAND RelationalExpression
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression("$$", exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr("$$", exp1.code, exp2.code));
    }
    $$ = exp;
  }
  ;
ConditionalOrExpression
: ConditionalAndExpression  { $$ = $1; }
| ConditionalOrExpression OP_LOR ConditionalAndExpression
  {
    Expression exp1 = (Expression)$1;
    Expression exp2 = (Expression)$3;
    Expression exp = Expression.joinExpression("||", exp1, exp2);
    if(!exp.isSemanticallyFine() && !exp.isQuiet()) {
      yyerror(Util.operationErr("||", exp1.code, exp2.code));
    }
    $$ = exp;
  }
  ;
Expression
: ConditionalOrExpression { $$ = $1; }
  ;
AddicativeOperator
: '+' {$$="+";}
| '-' {$$="-";}
  ;
MultiplicativeOperator
: '*' {$$="*";}
| '/' {$$="/";}
| '%' {$$="%";}
  ;
LogicalUnaryOperator
: '~'   {$$="~";}
| '!'   {$$="!";}
	;
RelationalBinaryOperator
: OP_EQ {$$="==";}
| OP_LE {$$="<=";}
| OP_GE {$$=">=";}
| OP_NE {$$="!=";}
    ;
%%

  private Yylex lexer;
  static boolean success = true;
  static boolean debug = true;

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      success = false;
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }

  public void yydebug (String msg) {
    if(debug)
      System.out.println("Compiling: " + msg);
  }

  public void yyerror (String error) {
    success = false;
    System.err.println ("Error: " + error);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);
  }


  static boolean interactive;

  public static void main(String args[]) throws IOException {
    System.out.println("BYACC/Java with JFlex Calculator Demo");

    Parser yyparser;
    if ( args.length > 0 ) {
      // parse a file
      System.out.println("Starting to compile " + args[0]);
      yyparser = new Parser(new FileReader(args[0]));
    }
    else {
      // interactive mode
      System.out.println("[Quit with CTRL-D]");
      interactive = true;
      yyparser = new Parser(new InputStreamReader(System.in));
    }

    yyparser.yyparse();
    if(success) {
      System.out.println("Finished compiling. Congrats!");
    }

    if (interactive) {
      System.out.println();
      System.out.println("Have a nice day");
    }
  }
