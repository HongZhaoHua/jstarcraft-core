grammar Type;

@header {
package com.jstarcraft.core.common.reflection;
}
    
array
    : clazz ((ARRAY+ GENERIC*) | GENERIC+)
    | generic ((ARRAY+ GENERIC*) | GENERIC+)
    ;

clazz
    : ID
    ;

generic
    : clazz '<' type (',' type)* '>'
    ;

type
    : wildcard
    | array
    | clazz
    | generic
    | variable
    ;
    
variable
    : ID (BOUND (type) ('&' (type))*)?
    ;

wildcard
    : '?' (BOUND type)?
    ;

ARRAY
    : '[]'
    ;

BOUND
    : ('extends' | 'super')
    ;

GENERIC
    : '<>'
    ;

ID
    : [a-zA-Z][a-zA-Z0-9._]*
    ;

SPACE
    : [ \t\r\n]+ -> skip
    ;
