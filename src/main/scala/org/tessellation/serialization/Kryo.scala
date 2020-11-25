package org.tessellation.serialization

import com.twitter.chill.{IKryoRegistrar, KryoInjection, KryoPool, ScalaKryoInstantiator}
import cats.syntax.all._

class Kryo(kryoPool: KryoPool) extends SerDe {
  def serialize[T <: Any](obj: T): Either[SerializationError, Array[Byte]] = {
    try {
      KryoInjection.instance(kryoPool)(obj).asRight[SerializationError]
    } catch {
      case err: Throwable => SerializationException(err).asLeft[Array[Byte]]
    }
  }

  def deserialize[T](b: Array[Byte]): Either[SerializationError, T] = {
    KryoInjection.instance(kryoPool).invert(b).toEither.bimap(DeserializationException, _.asInstanceOf[T])
  }
}

object Kryo {
  def apply(registrar: IKryoRegistrar): Kryo = {
    val instance = new ScalaKryoInstantiator()
      .setRegistrationRequired(true)
      .withRegistrar(registrar)

    val kryoPool = KryoPool.withByteArrayOutputStream(
      10,
      instance
    )

    new Kryo(kryoPool)
  }
}
