# Scalavro

An elegant* reflection-based Avro library in pure Scala.

_* but as yet incomplete_

## Motivation
1. **We would rather write Scala classes than Avro IDL.**
2. **We would like to avoid adding a compilation phase to generate IO bindings.**

## Goals
1. To provide an in-memory representation of avro schemas and protocols.
2. To generate avro schemas and protocols automatically from Scala types.
3. To produce flat (.avsc) Avro schema files.
4. To generate Scala bindings for reading and writing Avro-mapped Scala types.
5. Generally, to minimize fuss required to create an Avro-capable Scala application.

## Usage

    package com.gensler.scalavro.tests

    import com.gensler.scalavro.types._
    import scala.util.{Try, Success, Failure}

    case class Person(name: String, age: Int)
    val Success(personAvroType) = AvroType.fromType[Person]
    personAvroType.schema

Which yields:

    {
      "name": "Person",
      "type": "record",
      "fields": [
        {"name": "name","type": "string"},
        {"name": "age","type": "int"}
      ],
      "namespace": "com.gensler.scalavro.tests"
    }

And perhaps more interestingly:

    case class SantaList(nice: List[Person], naughty: List[Person])
    val Success(santaListAvroType) = AvroType.fromType[SantaList]
    santaListAvroType.schema

Which yields:

    {
      "name": "SantaList",
      "type": "record",
      "fields": [
        {
          "name": "nice",
          "type": {"type": "array", "items": "Person"}
        },
        {
          "name": "naughty",
          "type": {"type": "array","items": "Person"}
        }
      ],
      "namespace": "com.gensler.scalavro.tests"
    }

## Current Capabilities
- Schema generation from basic types.
- Schema generation from sequences (`List`, `Seq`, `Array`, etc.)
- Schema generation from String-keyed `Map` types.
- Schema output for Avro protocols.
- Robustness in the face of cyclic type dependencies (such records are never valid Avro).
- Conversion to "Canonical Parsing Form"

## Current Limitations
- IO is not yet implemented (schema generation only).
- Reading JSON schemas is not yet supported.
- Only binary disjunctive union types are currently supported (via `scala.Either[A, B]`).
- Enums are not yet supported.

## Reference

1. [Current Apache Avro Specification](http://avro.apache.org/docs/current/spec.html)
1. [Scala 2.10 Reflection Overview](http://docs.scala-lang.org/overviews/reflection/overview.html)

## Legal

Apache Avro is a trademark of The Apache Software Foundation.

Scalavro is distributed under the BSD 2-Clause License, the text of which follows:

Copyright (c) 2013, Gensler  
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
