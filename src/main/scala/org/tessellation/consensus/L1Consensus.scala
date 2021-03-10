package org.tessellation.consensus

import cats.effect.IO
import cats.syntax.all._
import higherkindness.droste.{AlgebraM, CoalgebraM, scheme}
import org.tessellation.consensus.L1ConsensusStep.{L1ConsensusError, L1ConsensusMetadata}
import org.tessellation.schema.Ω

object L1Consensus {

  val coalgebra: CoalgebraM[IO, StackF, (L1ConsensusMetadata, Ω)] = CoalgebraM {
    case (metadata, cmd) =>
      cmd match {
        case block @ L1Block(_) =>
          IO {
            Done(block.asRight[L1ConsensusError])
          }
        case end @ ConsensusEnd(_) =>
          IO {
            Done(end.asRight[L1ConsensusError])
          }
        case response @ ProposalResponse(_) =>
          IO {
            Done(response.asRight[L1ConsensusError])
          }
        case _ @L1Error(reason) =>
          IO {
            Done(L1ConsensusError(reason).asLeft[Ω])
          }
        case _ =>
          scheme.hyloM(L1ConsensusStep.algebra, L1ConsensusStep.coalgebra).apply(cmd).run(metadata).map {
            case (m, cmd) => {
              if (cmd.isLeft) {
                Done(L1ConsensusError(cmd.left.get.reason).asLeft[Ω])
              } else {
                More((m, cmd.right.get))
              }
            }
          }
      }
  }

  val algebra: AlgebraM[IO, StackF, Either[L1ConsensusError, Ω]] = AlgebraM {
    case More(a) =>
      IO {
        a
      }
    case Done(result) =>
      IO {
        result
      }
  }
}
