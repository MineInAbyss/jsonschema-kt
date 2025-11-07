package com.mineinabyss.jsonschema.dsl

enum class SchemaType(val typeName: String) {
    ARRAY("array"),
    BOOLEAN("boolean"),
    INTEGER("integer"),
    NULL("null"),
    NUMBER("number"),
    OBJECT("object"),
    STRING("string"),
}