namespace java com.jstarcraft.core.codec.thrift

enum MockEnumeration {
    PROTOSS = 0,
    TERRAN = 1,
    ZERG = 2,
}

struct MockComplexObject {
    1:i32 id,
    2:string firstName,
    3:string lastName,
    4:list<string> names,
    5:i32 money,
    6:list<i32> currencies,
    7:i64 instant,
    8:MockEnumeration race,
    9:list<i32> type,
    10:list<MockSimpleObject> mockList,
    11:map<i32, MockSimpleObject> mockMap,
}

struct MockSimpleObject {
    1:i32 id,
    2:string name,
}

struct MockMatrix {
    1:i32 rowSize,
    2:i32 columnSize,
    3:list<i32> rowPoints,
    4:list<i32> rowIndexes,
    5:list<i32> columnPoints,
    6:list<i32> columnIndexes,
    7:list<i32> termRows,
    8:list<i32> termColumns,
    9:list<double> termValues,
}
