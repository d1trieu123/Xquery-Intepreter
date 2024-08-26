grammar XQuery;

xq
    : var                                               #varXQ
    | strconst                                          #strXQ        
    | ap                                                #apXQ
    | '(' xq ')'                                        #parenXQ 
    | xq ',' xq                                         #commaXQ
    | xq '/' rp                                         #slashXQ
    | xq '//' rp                                        #doubleslashXQ 
    | '<' tagname '>' (xq | '{' xq '}')? '</' tagname '>'       #tagXQ
    | forClause letClause? whereClause? returnClause    #flwrXQ
    | letClause xq                                      #letXQ     
    | joinClause                                        #joinXQ
    ;

ap
    : doc '/' rp                                        #childAP
    | doc '//' rp                                       #descendAP
    ;

doc
    : 'doc(' filename ')'                               #docName
    | 'document(' filename ')'                          #docName
    ;

filename
    : QTEXT ;

rp
    : tagname                                           #tagRP
    | '*'                                               #childrenRP
    | '.'                                               #selfRP
    | '..'                                              #parentRP
    | 'text()'                                          #textRP
    | '@' WORD                                          #attrRP
    | '(' rp')'                                         #parenRP
    | rp'/' rp                                          #childRP
    | rp'//' rp                                         #descendRP
    | rp'[' filter ']'                                  #filterPath
    | rp',' rp                                          #commaRP
    ;

filter
    : rp                                                #rpPF
    | filter AND filter                                 #apPF
    | filter OR filter                                  #orPF
    | 'not' filter                                      #notPF
    | rp'=' rp                                          #equalsPF
    | rp EQ rp                                          #equalsPF
    | rp'==' rp                                         #samePF
    | rp' is ' rp                                       #samePF
    | rp'=' strconst                                    #stringPF
    | '(' filter ')'                                    #parenPF
    ;

forClause
    : 'for' var 'in' xq (',' var 'in' xq)* ;

letClause
    : 'let' var ':=' xq (',' var ':=' xq)* ;

whereClause
    : 'where' condition ;

condition
    : '(' condition ')'                                 #parenCond
    | condition AND condition                           #andCond
    | condition OR condition                            #orCond 
    | xq  '='  xq                                       #valueEqualityCond
    | xq EQ xq                                          #valueEqualityCond
    | xq  '=='  xq                                      #idEqualityCond
    | xq ' is ' xq                                      #idEqualityCond
    | EMPTY xq ')'                                      #emptyCond
    | someClause                                        #someCond
    | 'not' condition                                   #notCond
    ;

someClause
    : 'some' var 'in' xq (',' var 'in' xq)* 'satisfies' condition;


returnClause
    : 'return' xq ;

joinClause
    : 'join' '(' xq ',' xq ',' '[' joinVars ']' ',' '[' joinVars ']' ')' ;

joinVars
    : WORD (',' WORD)* ;

var
    : '$' WORD ;


strconst
    : QTEXT ;

EMPTY 
    : 'empty('
    | 'empty (' ;

AND
    : 'and';

OR
    : 'or';

tagname
    : WORD ;
    
EQ : 'eq';

QTEXT
    : '"'~["]*'"' ;

WORD
    : [a-zA-Z][a-zA-Z_0-9-]* ;

WS
    : [ \t\r\n] -> skip;
