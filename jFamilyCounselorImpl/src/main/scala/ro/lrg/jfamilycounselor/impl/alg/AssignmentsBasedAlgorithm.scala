package ro.lrg.jfamilycounselor.impl.alg

import ro.lrg.jfamilycounselor.alg.UsedConcreteTypePairsAlgorithm
import ro.lrg.jfamilycounselor.impl.model.pair.{SConcreteTypePair, SRefPair}
import ro.lrg.jfamilycounselor.impl.model.ref.{SParam, SRef}

object AssignmentsBasedAlgorithm extends UsedConcreteTypePairsAlgorithm {
  override private[jfamilycounselor] def compute[R <: SRef](refPair: SRefPair[R]): List[SConcreteTypePair] = {
    val (sr1, sr2) = (refPair._1, refPair._2)

    println("--- First methods calls: ")
    sr1.asInstanceOf[SParam].declaringMethod.invocations.foreach(println)
    println()

    println("--- Second methods calls: ")
    sr2.asInstanceOf[SParam].declaringMethod.invocations.foreach(println)
    println()

    List()
  }
}
