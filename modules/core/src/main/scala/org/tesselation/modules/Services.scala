package org.tesselation.modules

import java.security.KeyPair

import cats.effect.kernel.Async
import cats.syntax.functor._

import org.tesselation.config.types.AppConfig
import org.tesselation.domain.cluster.services.{Cluster, Gossip, Session}
import org.tesselation.domain.healthcheck.HealthCheck
import org.tesselation.infrastructure.cluster.services.{Cluster, Gossip, Session}
import org.tesselation.infrastructure.healthcheck.HealthCheck
import org.tesselation.keytool.security.SecurityProvider
import org.tesselation.kryo.KryoSerializer
import org.tesselation.schema.peer.PeerId

object Services {

  def make[F[_]: Async: KryoSerializer: SecurityProvider](
    cfg: AppConfig,
    nodeId: PeerId,
    keyPair: KeyPair,
    storages: Storages[F],
    queues: Queues[F]
  ): F[Services[F]] =
    for {
      _ <- Async[F].unit
      healthcheck = HealthCheck.make[F]
      session = Session.make[F](storages.session, storages.cluster, storages.node)
      cluster = Cluster
        .make[F](cfg, nodeId, keyPair, storages.session)
      gossip = Gossip.make[F](queues.rumor, keyPair)
    } yield
      new Services[F](
        healthcheck = healthcheck,
        cluster = cluster,
        session = session,
        gossip = gossip
      ) {}
}

sealed abstract class Services[F[_]] private (
  val healthcheck: HealthCheck[F],
  val cluster: Cluster[F],
  val session: Session[F],
  val gossip: Gossip[F]
)
