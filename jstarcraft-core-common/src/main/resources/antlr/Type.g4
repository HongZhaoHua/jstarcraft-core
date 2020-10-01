grammar Type;

@header {
package com.jstarcraft.core.common.reflection;
}
    
array
    : clazz ARRAY+
    | generic ARRAY+
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
    : ID (BOUND generic ('&' generic)*)?
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

ID
    : [a-zA-Z][a-zA-Z0-9._]*
    ;

SPACE
    : [ \t\r\n]+ -> skip
    ;
