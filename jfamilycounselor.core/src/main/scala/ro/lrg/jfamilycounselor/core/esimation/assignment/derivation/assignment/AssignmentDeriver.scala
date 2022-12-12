package ro.lrg.jfamilycounselor.core.esimation.assignment.derivation.assignment

private object AssignmentDeriver {
  def derive(assignment: Assignment): List[Assignment] = derive(assignment, List())

  private def derive(assignment: Assignment, derived: List[Assignment]): List[Assignment] = {
    if (!assignment.canBeDerived)
      List(assignment)
    else
      ExpressionDeriver
        .derive(assignment.expression)
        .map(expression => Assignment(assignment.referenceVariable, expression, assignment.expression.`type`.get))
        .filterNot(derived.contains)
        .flatMap(newAssignment => derive(newAssignment, newAssignment :: derived))
  }
}
