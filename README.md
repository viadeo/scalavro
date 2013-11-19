# Scalavro

A runtime reflection-based Avro library in Scala.

A full description of Avro is outside the scope of this documentation, but here is an introduction from [avro.apache.org](http://avro.apache.org/docs/current):

> __[Apache Avro &trade;](http://avro.apache.org)__ is a data serialization system.
>
> Avro provides:
> 
> - Rich data structures.
> - A compact, fast, binary data format.
> - A container file, to store persistent data.
> - Remote procedure call (RPC).
> - Simple integration with dynamic languages. Code generation is not required to read or write data files nor to use or implement RPC protocols. Code generation as an optional optimization, only worth implementing for statically typed languages.
>
> Avro provides functionality similar to systems such as __[Thrift](http://thrift.apache.org)__, __[Protocol Buffers](http://code.google.com/p/protobuf/)__, etc.

Scalavro takes a code-first, reflection based approach to schema generation and (de)serialization.  This yields a very low-overhead interface, and imposes some costs.  In general, Scalavro assumes you know what types you're reading and writing.  No built-in support is provided (as yet) for so-called schema resolution (taking the writer's schema into account when reading data).

## Goals

1. To provide an in-memory representation of avro schemas and protocols.
2. To synthesize avro schemas and protocols dynamically for a useful subset of Scala types.
4. To dynamically generate Scala bindings for reading and writing Avro-mapped Scala types to and from Avro binary.
5. Generally, to minimize fuss required to create an Avro-capable Scala application.

## Overview

Interacting with Scalavro is easy from a user's perspective.  Scalavro will use reflection to inspect some Scala type you supply and return an instance of `AvroType`.  With this object in hand, you are one or two method calls away from schema generation, type dependency graph inspection, or binary/JSON (de)serialization.

**"Crash Course" of Major Features, for the Impatient:**

```scala
import com.gensler.scalavro.types.AvroType
import scala.util.{ Try, Success, Failure }

// obtaining an instance of AvroType
val intSeqType = AvroType[Seq[Int]]

// obtaining an Avro schema for a given AvroType
intSeqType.schema

// obtaining an AvroTypeIO object for a given AvroType (via the `io` method)
val io: AvroTypeIO[Seq[Int]] = intSeqType.io

// binary I/O
io.write(Seq(1, 2, 3), outputStream)
val Sucess(readResult) = io read inputStream

// json I/O
val json = io writeJson Seq(1, 2, 3) // [1,2,3]
val Success(readResult) = io readJson json
```

## Obtaining Scalavro

The `Scalavro` artifacts are available from Maven Central. The current release is `0.6.0`, built against Scala 2.10.3.

Using SBT:

```scala
libraryDependencies += "com.gensler" %% "scalavro" % "0.6.0"
```

## API Documentation

- Generated [Scaladoc for version 0.6.0](http://genslerappspod.github.io/scalavro/api/0.6.0/index.html#com.gensler.scalavro.package)
- Generated [Scaladoc for version 0.5.1](http://genslerappspod.github.io/scalavro/api/0.5.1/index.html#com.gensler.scalavro.package)
- Generated [Scaladoc for version 0.5.0](http://genslerappspod.github.io/scalavro/api/0.5.0/index.html#com.gensler.scalavro.package)
- Generated [Scaladoc for version 0.3.0](http://genslerappspod.github.io/scalavro/api/0.4.0/index.html#com.gensler.scalavro.package)
- Generated [Scaladoc for version 0.3.1](http://genslerappspod.github.io/scalavro/api/0.3.1/index.html#com.gensler.scalavro.package)

## Index of Examples

- [Arrays](#arrays)
- [Maps](#maps)
- [Enums](#enums)
- [Unions](#unions)
- [Fixed-Length Data](#fixed)
- [Records](#records)
- [Binary IO](#binary-io)
- [JSON IO](#json-io)

## Type Mapping Strategy

### Primitive Types

<table>
  <thead>
  	<tr>
  	  <th>Scala Type</th>
  	  <th>Avro Type</th>
  	</tr>
  </thead>
  <tbody>
    <tr>
      <td><code>
        Unit
      </code></td>
      <td><code>
        null
      </code></td>
    </tr>
    <tr>
      <td><code>
        Boolean        
      </code></td>
      <td><code>
        boolean
      </code></td>
    </tr>
    <tr>
      <td><code>
        Byte
      </code></td>
      <td><code>
        int
      </code></td>
    </tr>
    <tr>
      <td><code>
        Char
      </code></td>
      <td><code>
        int
      </code></td>
    </tr>
    <tr>
      <td><code>
        Short
      </code></td>
      <td><code>
        int
      </code></td>
    </tr>
    <tr>
      <td><code>
        Int
      </code></td>
      <td><code>
        int
      </code></td>
    </tr>
    <tr>
      <td><code>
        Long
      </code></td>
      <td><code>
        long
      </code></td>
    </tr>
    <tr>
      <td><code>
        Float
      </code></td>
      <td><code>
        float
      </code></td>
    </tr>
    <tr>
      <td><code>
        Double
      </code></td>
      <td><code>
        double
      </code></td>
    </tr>
    <tr>
      <td><code>
        String
      </code></td>
      <td><code>
        string
      </code></td>
    </tr>
    <tr>
      <td><code>
        scala.collection.Seq[Byte]
      </code></td>
      <td><code>
        bytes
      </code></td>
    </tr>
  </tbody>
</table>

### Complex Types

<table>
  <thead>
  	<tr>
  	  <th>Scala Type</th>
  	  <th>Avro Type</th>
  	</tr>
  </thead>
  <tbody>
    <tr>
      <td><code>
        scala.collection.Seq[T]
      </code></td>
      <td><code>
        array
      </code></td>
    </tr>
    <tr>
      <td><code>
        scala.collection.Set[T]
      </code></td>
      <td><code>
        array
      </code></td>
    </tr>
    <tr>
      <td><code>
        scala.collection.Map[String, T]
      </code></td>
      <td><code>
        map
      </code></td>
    </tr>
    <tr>
       <td><code>
        scala.Enumeration#Value
      </code></td>
      <td><code>
        enum
      </code></td>
    </tr>
    <tr>
      <td>
        <code>enum</code> (Java)</td>
      <td><code>
        enum
      </code></td>
    </tr>
    <tr>
       <td><code>
        scala.util.Either[A, B]
      </code></td>
      <td><code>
        union
      </code></td>
    </tr>
    <tr>
       <td><code>
        scala.util.Option[T]
      </code></td>
      <td><code>
        union
      </code></td>
    </tr>
    <tr>
       <td><code>
        com.gensler.scalavro.util.Union[U]
      </code></td>
      <td><code>
        union
      </code></td>
    </tr>
    <tr>
      <td><code>
        com.gensler.scalavro.util.FixedData
      </code></td>
      <td><code>
        fixed
      </code></td>
    </tr>
    <tr>
      <td><em>
        Supertypes of case classes without type parameters
      </em></td>
      <td><code>
        union
      </code></td>
    </tr>
    <tr>
      <td><em>
        Case classes without type parameters
      </em></td>
      <td><code>
        record
      </code></td>
    </tr>
  </tbody>
</table>

## General Information
- Built against Scala 2.10.2 with SBT 0.12.4
- Depends upon [spray-json](https://github.com/spray/spray-json)
- Depends upon the Apache Java implementation of Avro (Version 1.7.5)

## Current Capabilities
- Dynamic Avro schema generation from vanilla Scala types
- Avro protocol definitions and schema generation
- Support for recursively defined record types
- Convenient, dynamic binary and JSON (de)serialization
- Avro RPC protocol representation and schema generation
- Schema conversion to "Parsing Canonical Form" (useful for Avro RPC protocol applications)

## Current Limitations
- Schema resolution (taking the writer's schema into account when reading) is not yet implemented
- Although recursively defined records (case classes) are supported, serializing all such instances is not.  In particular, reading and writing cyclic object graphs is not supported.
- Although records are supported (via case classes), only the case class's default constructor parameters are serialized.

## Scalavro by Example: Schema Generation

<a name="arrays"></a>
### Arrays

#### scala.Seq

```scala
import com.gensler.scalavro.types.AvroType
AvroType[Seq[String]].schema
```

Which yields:

```json
{
  "type" : "array",
  "items" : "string"
}
```

#### scala.Set

```scala
import com.gensler.scalavro.types.AvroType
AvroType[Set[String]].schema
```

Which yields:

```json
{
  "type" : "array",
  "items" : "string"
}
```

<a name="maps"></a>
### Maps

```scala
import com.gensler.scalavro.types.AvroType
AvroType[Map[String, Double]].schema
```

Which yields:

```json
{
  "type" : "map",
  "values" : "double"
}
```

<a name="enums"></a>
### Enums

#### scala.Enumeration

```scala
package com.gensler.scalavro.test
import com.gensler.scalavro.types.AvroType

object CardinalDirection extends Enumeration {
  type CardinalDirection = Value
  val N, NE, E, SE, S, SW, W, NW = Value
}

import CardinalDirection._
AvroType[CardinalDirection].schema
```

Which yields:

```json
{
  "name" : "CardinalDirection",
  "type" : "enum",
  "symbols" : ["N","NE","E","SE","S","SW","W","NW"],
  "namespace" : "com.gensler.scalavro.test.CardinalDirection"
}
```

#### Java `enum`

Definition (Java):

```java
package com.gensler.scalavro.test;
enum JCardinalDirection { N, NE, E, SE, S, SW, W, NW };
```

Use (Scala):

```scala
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.test.JCardinalDirection

AvroType[JCardinalDirection].schema
```

Which yields:

```json
{
  "name" : "JCardinalDirection",
  "type" : "enum",
  "symbols" : ["N","NE","E","SE","S","SW","W","NW"],
  "namespace" : "com.gensler.scalavro.test"
}
```

<a name="unions"></a>
### Unions

#### scala.Either

```scala
package com.gensler.scalavro.test
import com.gensler.scalavro.types.AvroType

AvroType[Either[Int, Boolean]].schema
```

Which yields:

```json
["int", "boolean"]
```

and

```scala
AvroType[Either[Seq[Double], Map[String, Seq[Int]]]].schema
```

Which yields:

```json
[{
  "type" : "array",
  "items" : "double"
},
{
  "type" : "map",
  "values" : {
    "type" : "array",
    "items" : "int"
  }
}]
```

#### scala.Option

```scala
package com.gensler.scalavro.test
import com.gensler.scalavro.types.AvroType

AvroType[Option[String]].schema
```

Which yields:

```json
["null", "string"]
```

#### com.gensler.scalavro.util.Union.union

```scala
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.util.Union._

AvroType[union [Int] #or [String] #or [Boolean]].schema
```

Which yields:

```json
["int", "string", "boolean"]
```

<a name="fixed"></a>
### Fixed-Length Data

```scala
package com.gensler.scalavro.test

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.util.FixedData
import scala.collection.immutable

@FixedData.Length(16)
case class MD5(override val bytes: immutable.Seq[Byte])
           extends FixedData(bytes)

AvroType[MD5].schema
```

Which yields:

```json
{
  "name": "MD5",
  "type": "fixed",
  "size": 16,
  "namespace": "com.gensler.scalavro.test"
}
```

<a name="records"></a>
### Records

#### From case classes

```scala
package com.gensler.scalavro.test
import com.gensler.scalavro.types.AvroType

case class Person(name: String, age: Int)

val personAvroType = AvroType[Person]
personAvroType.schema
```

Which yields:

```json
{
  "name": "com.gensler.scalavro.test.Person",
  "type": "record",
  "fields": [
    {
      "name": "name",
      "type": "string"
    },
    {
      "name": "age",
      "type": "int"
    }
  ]
}
```

And perhaps more interestingly:

```scala
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

val santaListAvroType = AvroType[SantaList]
santaListAvroType.schema
```

Which yields:

```json
{
  "name": "com.gensler.scalavro.test.SantaList",
  "type": "record",
  "fields": [
    {
      "name": "nice",
      "type": {
        "type": "array",
        "items": [
          {
            "name": "com.gensler.scalavro.test.Person",
            "type": "record",
            "fields": [
              {
                "name": "name",
                "type": "string"
              },
              {
                "name": "age",
                "type": "int"
              }
            ]
          },
          {
            "name": "com.gensler.scalavro.Reference",
            "type": "record",
            "fields": [
              {
                "name": "id",
                "type": "long"
              }
            ]
          }
        ]
      }
    },
    {
      "name": "naughty",
      "type": {
        "type": "array",
        "items": [
          "com.gensler.scalavro.test.Person",
          "com.gensler.scalavro.Reference"
        ]
      }
    }
  ]
}
```

**Whoa -- what happened there?!**

Scalavro as of version `0.5.1` supports _reference tracking_ for record instances.  Every time Scalavro writes a record to binary, it saves the source object reference and assigns a reference number.  If that same instance is required to be written again, it simply writes the reference number instead.  Scalavro reverses this process when reading from binary.  Therefore, references to shared data exist in the source object graph, then those references in the deserialized object graph will also be shared.  This imposes little performance penalty during serialization, and in general reduces serialized data size as well as deserialization time.

Scalavro implements this by replacing any nested record type within a schema with a binary union of the target type and a `Reference` schema.  References are encoded as an Avro `long` value.  Here is the schema for `Reference`:

```json
{
  "name": "com.gensler.scalavro.Reference",
  "type": "record",
  "fields": [
    {
      "name": "id",
      "type": "long"
    }
  ]
}
```

For comparison, in versions of Scalavro before `0.5.1`, the `SantaList` schema looked like this:

```json
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
      "type": {"type": "array", "items": "Person"}
    }
  ],
  "namespace": "com.gensler.scalavro.test"
}
```

Here is an example of a simple recursively defined type (a singly-linked list):

```scala
package com.gensler.scalavro.test
import com.gensler.scalavro.types.AvroType

case class Strings(data: String, next: Option[Strings])

AvroType[Strings].schema
```

Which yields:

```json
{
  "name": "com.gensler.scalavro.test.Strings",
  "type": "record",
  "fields": [
    {
      "name": "data",
      "type": "string"
    },
    {
      "name": "next",
      "type": [
        "null",
        [
          "com.gensler.scalavro.test.Strings",
          {
            "name": "com.gensler.scalavro.Reference",
            "type": "record",
            "fields": [
              {
                "name": "id",
                "type": "long"
              }
            ]
          }
        ]
      ]
    }
  ]
}
```

<a name="supertypes-of-case-classes"></a>
#### From supertypes of case classes

Given:

```scala
package com.gensler.scalavro.test

abstract class Alpha { def magic: Double }
class Beta extends Alpha { val magic = math.Pi }
case class Gamma(magic: Double) extends Alpha
case class Delta() extends Beta
case class Epsilon[T]() extends Beta
case class AlphaWrapper(inner: Alpha) extends Alpha { def magic = inner.magic }
```

Usage:

```scala
import com.gensler.scalavro.types.AvroType
AvroType[Alpha].schema
```

Which yields:

```json
[
  [
    {
      "name": "com.gensler.scalavro.test.Delta",
      "type": "record",
      "fields": []
    },
    {
      "name": "com.gensler.scalavro.Reference",
      "type": "record",
      "fields": [
        {
          "name": "id",
          "type": "long"
        }
      ]
    }
  ],
  [
    {
      "name": "com.gensler.scalavro.test.Gamma",
      "type": "record",
      "fields": [
        {
          "name": "magic",
          "type": "double"
        }
      ]
    },
    "com.gensler.scalavro.Reference"
  ],
  [
    {
      "name": "com.gensler.scalavro.test.AlphaWrapper",
      "type": "record",
      "fields": [
        {
          "name": "inner",
          "type": [
            [
              "com.gensler.scalavro.test.Delta",
              "com.gensler.scalavro.Reference"
            ],
            [
              "com.gensler.scalavro.test.Gamma",
              "com.gensler.scalavro.Reference"
            ],
            [
              "com.gensler.scalavro.test.AlphaWrapper",
              "com.gensler.scalavro.Reference"
            ]
          ]
        }
      ]
    },
    "com.gensler.scalavro.Reference"
  ]
]
```

Note that in the above example:

- `Alpha` is excluded from the union because it is not a case class
- `Beta` is excluded from the union because it is abstract
- `Epsilon` is excluded from the union because it takes type parameters

<a name="binary-io"></a>
## Scalavro by Example: Binary IO

```scala
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.io.AvroTypeIO
import scala.util.{Try, Success, Failure}

case class Person(name: String, age: Int)
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

val santaList = SantaList(
  nice = Seq(
    Person("John", 17),
    Person("Eve", 3)
  ),
  naughty = Seq(
    Person("Jane", 25),
    Person("Alice", 65)
  )
)

val santaListType = AvroType[SantaList]

val outStream: java.io.OutputStream = // some stream...

santaListType.io.write(santaList, outStream)

val inStream: java.io.InputStream = // some stream...

santaListType.io.read(inStream) match {
  case Success(readResult) => assert(readResult == santaList) // true
  case Failure(cause)      => // handle failure...
}
```

## Scalavro by Example: JSON IO

```scala
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.io.AvroTypeIO
import scala.util.{Try, Success, Failure}

case class Person(name: String, age: Int)
case class SantaList(nice: Seq[Person], naughty: Seq[Person])

val santaList = SantaList(
  nice = Seq(
    Person("John", 17),
    Person("Eve", 3)
  ),
  naughty = Seq(
    Person("Jane", 25),
    Person("Alice", 65)
  )
)

val santaListType = AvroType[SantaList]

val json = santaListType.io writeJson santaList

/*
  json.prettyPrint now yields:

  {
    "nice": [{
      "name": "John",
      "age": 17
    }, {
      "name": "Eve",
      "age": 3
    }],
    "naughty": [{
      "name": "Jane",
      "age": 25
    }, {
      "name": "Alice",
      "age": 65
    }]
  }

*/

santaListType.io.readJson(json) match {
  case Success(readResult) => assert(readResult == santaList) // true
  case Failure(cause)      => // handle failure...
}
```

### A Neat Fact about Scalavro's IO Capabilities

Scalavro tries to produce read results whose runtime types are as accurate as possible for collections (the supported collection types are `Seq`, `Set`, and `Map`).  It accomplishes this by looking for a public varargs `apply` factory method on the target type's companion object.  This is why `AvroType[ArrayBuffer[Int]].io.read(…)` is able to return a `Try[ArrayBuffer[Int]]`.

This works for custom subtypes of the supported collections types -- as long as you define a public varargs `apply` in the companion you're good to go.

## Reference
1. [Current Apache Avro Specification](http://avro.apache.org/docs/current/spec.html)
1. [Scala 2.10 Reflection Overview](http://docs.scala-lang.org/overviews/reflection/overview.html)
1. [Great article on schema evolution in various serialization systems](http://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html)
1. [Wickedly clever technique for representing unboxed union types, proposed by Miles Sabin](http://chuusai.com/2011/06/09/scala-union-types-curry-howard)

## Legal
Apache Avro is a trademark of The Apache Software Foundation.

Scalavro is distributed under the BSD 2-Clause License, the text of which follows:

Copyright (c) 2013, Gensler  
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

