package org.tessellation.sdk.infrastructure.healthcheck.declaration

import cats.effect.Async

import scala.reflect.runtime.universe.TypeTag

import org.tessellation.kryo.KryoSerializer
import org.tessellation.sdk.domain.healthcheck.consensus.HealthCheckConsensus
import org.tessellation.sdk.infrastructure.gossip.{IgnoreSelfOrigin, RumorHandler}

object PeerDeclarationProposalHandler {

  def make[F[_]: Async: KryoSerializer, K: TypeTag](
    healthCheck: HealthCheckConsensus[F, Key[K], Health, Status[K], Decision]
  ): RumorHandler[F] =
    RumorHandler.fromPeerRumorConsumer[F, Status[K]](IgnoreSelfOrigin) { rumor =>
      healthCheck.handleProposal(rumor.content)
    }

}
