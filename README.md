# jsonschema-kt

Kotlin Multiplatform JSON Schema DSL and generator.

We provide a DSL for describing a JSON Schema, with helper functions for generating parts of the schema from kotlinx.serialization descriptors. We also provide helpers annotations and serial descriptor wrappers for kotlinx.serialization that can manually define what their schema looks like using the DSL.

This approach lets users define complex schemas, ex. a map where keys define what polymorphic class is used, or serializers that can decode either a list or string value, while still generating the majority of it automatically.

We're currently experimenting with the DSL to fit our needs, if you're an outside organization interested in using this, please reach out, we hope to make this useful to others!