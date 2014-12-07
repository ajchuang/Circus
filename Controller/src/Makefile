# only works with the Java extension of yacc: 
# byacc/j from http://troi.lincom-asg.com/~rjamison/byacc/

JFLEX  = bin/jflex 
BYACCJ = ./yacc.macosx -v -J
JAVAC  = javac
JFLAGS = -g

.SUFFIXES: .java .class

CLASSES = \
    nonstar/interpreter/PrimaryType.java \
    nonstar/interpreter/Type.java 

all: Parser.class

classes: $(CLASSES:.java=.class)

.java.class:
	$(JAVAC) $(JFLAGS) $*.java

# targets:


run: Parser.class
	java -cp nonstar/interpreter/:. Parser

build: clean Parser.class

clean:
	rm -f *~ *.class Parser.java Yylex.java .*.swp
	rm -f nonstar/interpreter/*.class
	rm -f y.output

Parser.class: Yylex.java Parser.java classes
	$(JAVAC) Parser.java

Yylex.java: Nonstar.flex
	$(JFLEX) Nonstar.flex

Parser.java: Nonstar.y
	$(BYACCJ) Nonstar.y
