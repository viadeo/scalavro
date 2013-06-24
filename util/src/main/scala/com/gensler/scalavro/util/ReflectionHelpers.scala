package com.gensler.scalavro.util

import scala.collection.immutable.ListMap
import scala.reflect.api.{ Universe, Mirror, TypeCreator }
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Companion object for [[ReflectionHelpers]]
  */
object ReflectionHelpers extends ReflectionHelpers

trait ReflectionHelpers {

  protected[scalavro] val classLoaderMirror = runtimeMirror(getClass.getClassLoader)

  /**
    * Returns a sequence of Strings, each of which names a value of the
    * supplied enumeration type.
    */
  protected[scalavro] def symbolsOf[E <: Enumeration: TypeTag]: Seq[String] = {
    val valueType = typeOf[E#Value]

    val isValueType = (sym: Symbol) => {
      !sym.isMethod && !sym.isType &&
        sym.typeSignature.baseType(valueType.typeSymbol) =:= valueType
    }

    typeOf[E].members.collect {
      case sym: Symbol if isValueType(sym) => sym.name.toString.trim
    }.toSeq.reverse
  }

  /**
    * Returns a type tag for the parent `scala.Enumeration` of the supplied
    * enumeration value type.
    */
  protected[scalavro] def enumForValue[V <: Enumeration#Value: TypeTag]: TypeTag[_ <: Enumeration] = {
    val TypeRef(enclosing, _, _) = typeOf[V]
    tagForType(enclosing).asInstanceOf[TypeTag[_ <: Enumeration]]
  }

  private lazy val reflections = {
    import org.reflections.Reflections
    import org.reflections.util.{ ConfigurationBuilder, FilterBuilder }
    import org.reflections.scanners.SubTypesScanner

    val classFilter = new FilterBuilder
    classFilter.excludePackage("java")
    classFilter.excludePackage("scala")
    classFilter.excludePackage("ch.qos.logback")
    classFilter.excludePackage("org.slf4j")
    classFilter.excludePackage("org.reflections")
    classFilter.excludePackage("spray.json")
    classFilter.excludePackage("com.google.common")
    classFilter.excludePackage("org.parboiled")
    classFilter.excludePackage("org.scalatest")

    val urls = getClass.getClassLoader.asInstanceOf[java.net.URLClassLoader].getURLs

    val configBuilder = new ConfigurationBuilder
    configBuilder setUrls (urls: _*)
    configBuilder.useParallelExecutor() // scan using # available processors
    configBuilder filterInputsBy classFilter
    configBuilder setScanners new SubTypesScanner(false)

    configBuilder.build
  }

  /**
    * Returns all currently loaded case class subtypes of the supplied type.
    */
  protected[scalavro] def caseClassSubTypesOf[S: TypeTag]: Seq[Type] = {
    import scala.collection.JavaConversions.asScalaSet
    import java.lang.reflect.Modifier

    // filter out abstract classes
    val subClassSymbols = asScalaSet(
      reflections.getSubTypesOf(classLoaderMirror.runtimeClass(typeOf[S]))
    ).collect {
        case clazz: Class[_] if !Modifier.isAbstract(clazz.getModifiers) =>
          classLoaderMirror classSymbol clazz
      }

    // filter class symbols to include only case classes with no type parameters
    subClassSymbols.collect {
      case sym: ClassSymbol if (sym.isCaseClass && sym.typeParams.isEmpty) => sym.selfType
    }.toSeq
  }

  /**
    * Returns a map from formal parameter names to type tags, containing one
    * mapping for each constructor argument.  The resulting map (a ListMap)
    * preserves the order of the primary constructor's parameter list.
    */
  protected[scalavro] def caseClassParamsOf[T: TypeTag]: ListMap[String, TypeTag[_]] = {
    val tpe = typeOf[T]
    val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR)
    val defaultConstructor =
      if (constructorSymbol.isMethod) constructorSymbol.asMethod
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
      }

    ListMap[String, TypeTag[_]]() ++ defaultConstructor.paramss.reduceLeft(_ ++ _).map {
      sym => sym.name.toString -> tagForType(tpe.member(sym.name).asMethod.returnType)
    }
  }

  /**
    * Returns Some(MethodMirror) for the public construcor of the supplied
    * class type that takes the supplied argument type as its only parameter.
    *
    * Returns None if no suitable public single-argument constructor can
    * be found for the supplied type.
    *
    * @tparam T the type of the class to inspect for a suitable single-argument
    *           constructor
    * @tparam A the type of the constructor's formal parameter
    */
  protected[scalavro] def singleArgumentConstructor[T: TypeTag, A: TypeTag]: Option[MethodMirror] = {

    val classType = typeOf[T]
    val targetArgType = typeOf[A]
    val constructorSymbol = classType.declaration(nme.CONSTRUCTOR)

    def isPublicAndMatchesArgument(methodSymbol: MethodSymbol): Boolean =
      methodSymbol.isPublic && {
        methodSymbol.asMethod.paramss match {
          case List(List(argSym)) => argSym.typeSignatureIn(classType) =:= targetArgType
          case _                  => false
        }
      }

    val singleArgumentConstructor: Option[MethodSymbol] =
      if (constructorSymbol.isMethod && isPublicAndMatchesArgument(constructorSymbol.asMethod)) {
        Some(constructorSymbol.asMethod)
      }
      else {
        val ctors = constructorSymbol.asTerm.alternatives
        ctors.map{ _.asMethod }.find { isPublicAndMatchesArgument }
      }

    singleArgumentConstructor.collect {
      case constructorSymbol: MethodSymbol if classType.typeSymbol.isClass => {
        val classMirror = classLoaderMirror reflectClass classType.typeSymbol.asClass
        classMirror reflectConstructor constructorSymbol
      }
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
    product: P,
    memberName: String): Option[T] = {

    implicit val productClassTag = ClassTag[P](product.getClass)

    val getterSymbol = typeOf[P].member(memberName: TermName)

    if (getterSymbol.isMethod &&
      getterSymbol.asMethod.isGetter &&
      getterSymbol.asMethod.returnType <:< typeOf[T]) {
      scala.util.Try {
        val instanceMirror = classLoaderMirror reflect product
        val getterMethod = instanceMirror reflectMethod getterSymbol.asMethod
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

      if (!(tpe <:< typeOf[Product] && classSymbol.isCaseClass))
        throw new IllegalArgumentException(
          "instantiateCaseClassWith may only be applied to case classes!"
        )

      val classMirror = classLoaderMirror reflectClass classSymbol

      val constructorSymbol = tpe.declaration(nme.CONSTRUCTOR)

      val defaultConstructor =
        if (constructorSymbol.isMethod) constructorSymbol.asMethod
        else {
          val ctors = constructorSymbol.asTerm.alternatives
          ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
        }

      val constructorMethod = classMirror reflectConstructor defaultConstructor

      constructorMethod(args: _*).asInstanceOf[T]
    }

}
