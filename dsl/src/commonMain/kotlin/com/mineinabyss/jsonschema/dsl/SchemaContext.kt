package com.mineinabyss.jsonschema.dsl

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.serializer

@DslMarker
annotation class SchemaDSLMarker

@SchemaDSLMarker
interface Schema

class SchemaContext {
    val defsForSerialDescriptors = mutableMapOf<SerialDescriptor, String>()
    val defs = mutableMapOf<String, SchemaProperty>()
    val schema = "https://json-schema.org/draft/2020-12/schema"
    var rootProperty: SchemaProperty = SchemaProperty()

    val definitionRequested = mutableSetOf<SerialDescriptor>()
    private var definitionProvider: SchemaProperty.(descriptor: SerialDescriptor) -> Unit = {
        error("Definition not provided for ${it.serialName}")
    }

    fun rootProperty(init: SchemaProperty.() -> Unit) {
        rootProperty = SchemaProperty().apply(init)
    }


    fun provideDefinitions(provider: SchemaProperty.(descriptor: SerialDescriptor) -> Unit) {
        definitionProvider = provider
    }


    fun requestDefinition(descriptor: SerialDescriptor) {
        definitionRequested += descriptor
    }

    /**
     * Gets a definition or creates a default one using the definition provider
     */
    inline fun <reified T> definition(): String {
        val descriptor = serializer<T>().descriptor
        requestDefinition(descriptor)
        return $$"#/$defs/$${descriptor.serialName}"
    }

    inline fun <reified T> definition(crossinline init: SchemaProperty.(descriptor: SerialDescriptor) -> Unit): String {
        val desc = serializer<T>().descriptor
        return definition(desc.serialName, desc) {
            init(desc)
        }
    }

    fun definition(
        serialDescriptor: SerialDescriptor,
        override: Boolean = false,
        init: SchemaProperty.() -> Unit,
    ): String {
        return definition(serialDescriptor.serialName, serialDescriptor, override, init)
    }

    fun definition(
        name: String,
        serialDescriptor: SerialDescriptor?,
        override: Boolean = false,
        init: SchemaProperty.() -> Unit,
    ): String {
        val ref = $$"#/$defs/$$name"
        if (!override && name in defs) return ref
        defs[name] = SchemaProperty().apply(init)
        if (serialDescriptor != null) defsForSerialDescriptors[serialDescriptor] = name
        return ref
    }

    fun build(): JsonObject = buildJsonObject {
        rootProperty.build().entries.forEach { (key, value) ->
            put(key, value)
        }
        put($$"$schema", schema)

        // Create defaults for definitions that were requested but not provided.
        val unspecifiedDefs = definitionRequested - defsForSerialDescriptors.keys
        val defaultProvidedDefs = unspecifiedDefs.associate { descriptor ->
            descriptor.serialName to SchemaProperty().apply { definitionProvider(descriptor) }
        }

        // put defs
        putJsonObject($$"$defs") {
            (defs + defaultProvidedDefs).forEach { (name, def) ->
                put(name, def.build())
            }
        }
    }
}

fun jsonSchema(init: SchemaContext.() -> Unit): JsonObject {
    return SchemaContext().apply(init).build()
}

context(schema: SchemaContext)
fun propertyBuilder(init: SchemaProperty.() -> Unit): SchemaProperty.() -> Unit = init
