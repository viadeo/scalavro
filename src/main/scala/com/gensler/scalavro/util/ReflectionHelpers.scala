package com.gensler.scalavro.util

import scala.reflect.api.{ Universe, Mirror, TypeCreator }
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

trait ReflectionHelpers {

  protected val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  protected[scalavro] def productParamsOf[T: TypeTag]: Map[String, TypeTag[_]] = {
    val tpe = typeOf[T]
    val classSymbol = tpe.typeSymbol.asClass
    val classMirror = classLoaderMirror reflectClass classSymbol

    val accessorPredicate = (sym: Symbol) => sym.isMethod && sym.asMethod.isCaseAccessor

    tpe.members.filter(accessorPredicate).map { sym =>
      sym.name.toString -> tagForType(sym.asMethod.returnType)
    }.toMap
  }

  protected[scalavro] def tagForType(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

  protected[scalavro] def productElement[P: TypeTag, T: TypeTag](product: P, memberName: String): Option[T] = {

    implicit val productClassTag = ClassTag[P](product.getClass)

    val getter = typeOf[P].member(memberName: TermName).asMethod

    if (getter.isGetter && getter.returnType =:= typeOf[T]) {
      scala.util.Try {
        val instanceMirror = classLoaderMirror reflect product
        (instanceMirror reflectMethod getter).apply().asInstanceOf[T]
      }.toOption
    }

    else None
  }

}

object ReflectionHelpers extends ReflectionHelpers