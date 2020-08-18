grammar Calculator;

formula
    : Open formula Close
    | formula Multiply formula
    | formula Divide formula
    | formula Add formula
    | formula Subtract formula
    | number
    ; 

number
    : Number
    ;

Add
    : 'plus'
    | '+'
    ;
    
Subtract
    : 'minus'
    | '-'
    ;
    
Multiply
    : 'multiply'
    | '*'
    ;
    
Divide
    : 'divide'
    | '/'
    ;

Number
    : '-'? [0-9]+
    ;
Open
    : '('
    ;
    
Close
    : ')'
    ;

Space
    : [ \t\r\n]+ -> skip
    ;