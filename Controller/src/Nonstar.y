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

%token <sval> STATLIST

%type <sval> statement_list
%type <sval> statement
%type <obj> type
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
%type <obj> QuarlifiedName
%type <obj> ComplexPrimaryNoParenthesis
%type <obj> MethodCall
%type <obj> ArgumentList

%left '*' '/'
%right '^'         /* exponentiation        */
      
%%

source_code: { Util.init(); } nonstar_config procedures_list
  {
    ArrayList<AttributeObj> nonstar_config = (ArrayList<AttributeObj>)$2;
    ArrayList<FunctionObj> procedures = (ArrayList<FunctionObj>)$3;
    Util.genNonstar(nonstar_config, procedures);
    yydebug("source_code finished");
  }
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
      yyerror($1 + " is not an available name."); 
    }
    $$ = jsonitem;
  }
  ;
function_definition
: function_signature block
  {
  }
  ;
function_signature
: built_in_function_signature { $$ = $1; }
| type ID '(' parameter_config_list ')'
  {
  }
| type ID '(' ')'
  {
  }
  ;
built_in_function_signature
: INIT
  {
  }
| ON_REQ
  {
  }
  ; 
parameter_config_list
: parameter_config 
  {
  }
| parameter_config_list ',' parameter_config
  {
  }
  ;
parameter_config: type ID
  {
  }
  ;
procedures_list
: function_definition 
  {	
  }
| procedures_list function_definition
  {
  }
  ;
type
: non_void_type { $$ = $1; }
| VOID { $$ = Type.VOID; }
  ;
non_void_type
: FLOW { $$ = Type.FLOW; }
| SWITCH { $$ = Type.SWITCH; }
| LIST '<' type '>' 
  {
    Type second_type = (Type)$3;
    $$ = new Type(PrimaryType.LIST, second_type, null);
  }
| DICT '<' type ',' type '>' 
  {
    Type second_type = (Type)$3;
    Type third_type = (Type)$5;
    $$ = new Type(PrimaryType.DICT, second_type, third_type);
  }
| primitive_type { $$ = $1; }
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
  ;
/* ----------------------------------------------------------- */
block
: '{' statement_list '}' {  }
| '{' '}' { $$ = ""; }
  ;
statement_list
: statement_list statement { }
| statement { }
	;
statement
: selection_statement { }
| field_declaration { }
| while_loop_statement { }
| Expression ';'
  {
  }
| QuarlifiedName '=' Expression ';'
  {
  }
| return_statement { }
| foreach_statement {}
  ;
return_statement
: RETURN Expression ';'
  {
  }
| RETURN ';' 
  {
  }
  ;
selection_statement
: if_condition_block { }
| if_condition_block ELSE block
  {
  }
  ;
if_condition_block:
IF '(' Expression ')' block
  {
  }
  ;
field_declaration
: type ID ';'
  {
  }
| type ID '=' Expression ';' 
  {
  }
  ;
while_loop_statement: WHILE '(' Expression ')' block
  {
  }
  ;
foreach_statement: FOREACH '(' type ID IN Expression ')' block
  {
  }
  ;
MethodCall
: QuarlifiedName '(' ArgumentList ')'
  {
  }
| QuarlifiedName '(' ')'   
  {
  }
  ;
ArgumentList
: Expression     
  {
  }
| ArgumentList ',' Expression
  {
  }
  ;
QuarlifiedName
: QuarlifiedName '.' ID
  {
  }
| ID
  {
  }
  ;
PrimaryExpression
: '(' Expression ')'
  {
  }
| ComplexPrimaryNoParenthesis{$$=$1;}
  ;
ComplexPrimaryNoParenthesis
: value
  {
  }
| QuarlifiedName
  {
  }
| MethodCall
  {
  }
  ;
UnaryExpression
: LogicalUnaryExpression    {}
  ;
LogicalUnaryExpression
: PrimaryExpression {}
| LogicalUnaryOperator UnaryExpression
  {
  }
  ;
MultiplicativeExpression
: UnaryExpression {}
| MultiplicativeExpression '*' UnaryExpression
  {
  }
| MultiplicativeExpression '/' UnaryExpression
  {
  }
| MultiplicativeExpression '%' UnaryExpression 
  {
  }
  ;
AdditiveExpression
: MultiplicativeExpression {}
| AdditiveExpression '+' MultiplicativeExpression
  {
  }
| AdditiveExpression '-' MultiplicativeExpression
  {
  }
  ;
RelationalExpression
: AdditiveExpression    {}
| AdditiveExpression '<' AdditiveExpression {}
| AdditiveExpression '>' AdditiveExpression {}
| AdditiveExpression RelationalBinaryOperator AdditiveExpression
  {
  }
  ;
ConditionalAndExpression
: RelationalExpression    {}
| ConditionalAndExpression OP_LAND RelationalExpression
  {
  }
  ;
ConditionalOrExpression
: ConditionalAndExpression  { }
| ConditionalOrExpression OP_LOR ConditionalAndExpression
  {
  }
  ;
Expression
: ConditionalOrExpression {}
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
