package com.gensler.scalavro.util

import scala.collection.immutable.ListMap
import scala.reflect.api.{Universe, Mirror, TypeCreator}
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Companion object for [[ReflectionHelpers]]
  */
object ReflectionHelpers extends ReflectionHelpers


trait ReflectionHelpers {

  protected val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  /**
    * Returns a map from formal parameter names to type tags, containing one
    * mapping for each constructor argument.  The resulting map (a ListMap)
    * preserves the order of the primary constructor's parameter list.
    */
  protected[scalavro] def caseClassParamsOf[T: TypeTag]: ListMap[String, TypeTag[_]] = {
    val tpe = typeOf[T]
    val classSymbol = tpe.typeSymbol.asClass
    val classMirror = classLoaderMirror reflectClass classSymbol
    val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR).asMethod

    val isAccessor = (sym: Symbol) => sym.isMethod && sym.asMethod.isCaseAccessor

    ListMap[String, TypeTag[_]]() ++ constructorSymbol.paramss.reduceLeft( _ ++ _ ).map {
      sym => sym.name.toString -> tagForType(tpe.member(sym.name).asMethod.returnType)
    }
  }

  /**
    * Returns a TypeTag in the current runtime universe for the supplied type.
    */
  protected[scalavro] def tagForType(tpe: Type): TypeTag[_] = TypeTag(
    classLoaderMirror,
    new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]) = tpe.asInstanceOf[U#Type]
    }
  )

  /**
    * Attempts to fetch the value of a named component of a product instance,
    * while verifying the value conforms to some expected type.
    *
    * @tparam P         the type of the product instance in question
    * @tparam T         the expected type of the value
    * @param product    an instance of some product type, P
    * @param membername the arguments to supply to the constructor method
    */
  protected[scalavro] def productElement[P: TypeTag, T: TypeTag](
    product: P, memberName: String
  ): Option[T] = {

    implicit val productClassTag = ClassTag[P](product.getClass)

    val getterSymbol = typeOf[P].member(memberName: TermName).asMethod

    if (getterSymbol.isGetter && getterSymbol.returnType <:< typeOf[T]) {
      scala.util.Try {
        val instanceMirror = classLoaderMirror reflect product
        val getterMethod = instanceMirror reflectMethod getterSymbol
        getterMethod().asInstanceOf[T]
      }.toOption
    }

    else None
  }

  /**
    * Attempts to create a new instance of the specified type by calling the
    * constructor method with the supplied arguments.
    *
    * @tparam T   the type of object to construct, which must be a case class
    * @param args the arguments to supply to the constructor method
    */
  protected[scalavro] def instantiateCaseClassWith[T: TypeTag](args: Seq[_]): scala.util.Try[T] =
    scala.util.Try {
      val tpe = typeOf[T]
      val classSymbol = tpe.typeSymbol.asClass
      
      if (! (tpe <:< typeOf[Product] && classSymbol.isCaseClass))
        throw new IllegalArgumentException(
          "instantiateCaseClassWith may only be applied to case classes!"
        )

      val classMirror = classLoaderMirror reflectClass classSymbol
      val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR).asMethod
      val constructorMethod = classMirror reflectConstructor constructorSymbol

      constructorMethod(args: _*).asInstanceOf[T]
    }

}
