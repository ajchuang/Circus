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
	import nonstar.interpreter.*;
%}
      
%token NONSTAR_DF

%token INIT
%token ON_REQ 

%token CIRCUIT
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
%type <obj> json_item
%type <obj> json_content
%type <obj> json
%type <sval> block

%token <sval> STATLIST

%type <sval> statement_list
%type <sval> statement
%type <obj> type
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

source_code: nonstar_config procedures_list
  {
    System.out.println("source_code finished");
  }
  ;
nonstar_config: NONSTAR_DF json
  {
    System.out.println("nonstar_config finished");
  }
  ;
json: '{' json_content '}' { }
  ;
json_content
: json_item 
  {
  }
| json_content json_item
  {
  }
  ;
json_item
: ID ':' value ';'
  {
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
: CIRCUIT { $$ = Type.CIRCUIT; }
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
| VOID { $$ = Type.VOID; }
| primitive_type { $$ = $1; }
	;
primitive_type
: DECLR_INT { $$ = Type.INTEGER; }
| DECLR_STR { $$ = Type.STRING; }
| DECLR_BOOL { $$ = Type.BOOLEAN; }
	;
value
: INTEGER { $$=new Value(Type.INTEGER, new Integer($1)); }
| STRING { $$=new Value(Type.STRING, $1); }
| TRUE { $$=new Value(Type.BOOLEAN, new Boolean(true)); }
| FALSE { $$=new Value(Type.BOOLEAN, new Boolean(false)); }
| NULL { $$=new Value(Type.NULL, NULL);}
    ;
/* ----------------------------------------------------------- */
block
: '{' statement_list '}' {  }
| '{' '}' { }
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

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }


  public void yyerror (String error) {
    System.err.println ("Error: " + error);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);
  }


  static boolean interactive;

   class foo{
       public foo()
       {
           System.out.println("HAHAHA");
       }
   }

  public static void main(String args[]) throws IOException {
    System.out.println("BYACC/Java with JFlex Calculator Demo");

    Parser yyparser;
    if ( args.length > 0 ) {
      // parse a file
      yyparser = new Parser(new FileReader(args[0]));
    }
    else {
      // interactive mode
      System.out.println("[Quit with CTRL-D]");
      System.out.print("Expression: ");
      interactive = true;
	    yyparser = new Parser(new InputStreamReader(System.in));
    }

    yyparser.yyparse();
    
    if (interactive) {
      System.out.println();
      System.out.println("Have a nice day");
    }
  }
