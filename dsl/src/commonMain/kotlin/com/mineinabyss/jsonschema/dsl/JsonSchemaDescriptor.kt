package com.mineinabyss.jsonschema.dsl

interface JsonSchemaDescriptor {
    context(context: SchemaContext)
    fun SchemaProperty.defineSchema()
}
