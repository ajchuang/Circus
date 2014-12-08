/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (C) 2000 Gerwin Klein <lsf@jflex.de>                          *
 * All rights reserved.                                                    *
 *                                                                         *
 * Thanks to Larry Bell and Bob Jamison for suggestions and comments.      *
 *                                                                         *
 * License: BSD                                                            *
 *                                                                         *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

%%

%byaccj

%{
  private Parser yyparser;

  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

/* keywords for language structure */
NONSTAR_DF = "Nonstar"

/* built-in function names */
INIT = "on_start#"
ON_REQ = "on_req#"

FLOW = "Flow"
SWITCH = "Switch"


VOID = "void"
INTEGER = [0-9]+
STRING = \"[^\"]*\"

/*identifier*/
ID = [a-zA-Z][_a-zA-Z0-9]*
delim = [ \t\n]
ws = {delim}+

/*used as debug*/

%%

/* operators */
"+" | 
"-" | 
"*" | 
"/" | 
"%" |
"^" | 
"(" |
")" |
"{" |
"}" |
";" |
"[" |
"]" |
"=" |
">" |
"<" |
"~" |
"!" |
"." |
"," |
":" { return (int) yycharat(0); }

"=="		{return Parser.OP_EQ;}
"<="		{return Parser.OP_LE;}
">="		{return Parser.OP_GE;}
"!="		{return Parser.OP_NE;}
"||"		{return Parser.OP_LOR;}
"&&"		{return Parser.OP_LAND;}

{ws}  {/*do nothing*/}

/* float */
/*{NUM}  { yyparser.yylval = new ParserVal(Double.parseDouble(yytext()));*/
/*         return Parser.NUM; }*/

{INTEGER}  { yyparser.yylval = new ParserVal(Integer.parseInt(yytext()));
		return Parser.INTEGER;}
{STRING}   { yyparser.yylval = new ParserVal(yytext()); return Parser.STRING;}

{NONSTAR_DF}  {return Parser.NONSTAR_DF;}
{INIT} {return Parser.INIT;}
{ON_REQ} {return Parser.ON_REQ;}

"return" {return Parser.RETURN;}

"false" {return Parser.FALSE;}
"true" {return Parser.TRUE;}
"if" {return Parser.IF;}
"else" {return Parser.ELSE;}
"while"		{return Parser.WHILE;}
"foreach"	{return Parser.FOREACH;}
"in"	{return Parser.IN;}
"is"    {return Parser.IS;}

"int"   {return Parser.DECLR_INT;}
"String"    {return Parser.DECLR_STR;}
"bool"  {return Parser.DECLR_BOOL;}
{FLOW} {return Parser.FLOW;}
{SWITCH} {return Parser.SWITCH;}
"Dict"  {return Parser.DICT;}
"List"  {return Parser.LIST;}
"null"  {return Parser.NULL;}
{VOID} {return Parser.VOID;}

{ID} { yyparser.yylval = new ParserVal(yytext()); return Parser.ID;}

/*debug*/

/* whitespace */
[ \t]+ { }

\b     { System.err.println("Sorry, backspace doesn't work"); }

/* error fallback */
[^]    { System.err.println("Error: unexpected character '"+yytext()+"'"); return -1; }
