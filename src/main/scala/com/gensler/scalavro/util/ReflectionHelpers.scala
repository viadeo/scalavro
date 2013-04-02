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
    val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR).asMethod

    val isAccessor = (sym: Symbol) => sym.isMethod && sym.asMethod.isCaseAccessor

    constructorSymbol.paramss.reduceLeft( _ ++ _ ).map {
      sym => sym.name.toString -> tagForType(tpe.member(sym.name).asMethod.returnType)
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

    val getterSymbol = typeOf[P].member(memberName: TermName).asMethod

    if (getterSymbol.isGetter && getterSymbol.returnType =:= typeOf[T]) {
      scala.util.Try {
        val instanceMirror = classLoaderMirror reflect product
        val getterMethod = instanceMirror reflectMethod getterSymbol
        getterMethod().asInstanceOf[T]
      }.toOption
    }

    else None
  }

  protected[scalavro] def instantiateCaseClassWith[T: TypeTag](args: Seq[_]): scala.util.Try[T] =
    scala.util.Try {
      val tpe = typeOf[T]
      val classSymbol = tpe.typeSymbol.asClass
      
      if (! (tpe <:< typeOf[Product] && classSymbol.isCaseClass)) throw new IllegalArgumentException(
        "instantiateCaseClassWith may only be applied to case classes!"
      )

      val classMirror = classLoaderMirror reflectClass classSymbol
      val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR).asMethod
      val constructorMethod = classMirror reflectConstructor constructorSymbol

      constructorMethod(args: _*).asInstanceOf[T]
    }

}

object ReflectionHelpers extends ReflectionHelpers