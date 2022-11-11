/**
 * Put your custom implementation here. To this end, implement the following interfaces:
 * <ul>
 *     <li>Define a {@link de.uni_passau.fim.se2.metaheuristics.configurations.Configuration
 *     solution encoding} for test case orderings of regression test suites. Next, define an
 *     {@link de.uni_passau.fim.se2.metaheuristics.configurations.ElementaryTransformation
 *     elementary transformation}. Also, create a {@link
 *     de.uni_passau.fim.se2.metaheuristics.configurations.ConfigurationGenerator generator} for
 *     random encodings.
 *     </li>
 *     <li>Implement a {@link de.uni_passau.fim.se2.metaheuristics.fitness_functions.FitnessFunction
 *     fitness function} that computes the APLC metric of a test case ordering.
 *     </li>
 *     <li>Create a new {@link de.uni_passau.fim.se2.metaheuristics.stopping_conditions.StoppingCondition
 *     stopping condition} that enforces a maximum number of fitness evaluations.
 *     </li>
 *     <li>Provide two implementations of the interface
 *     {@link de.uni_passau.fim.se2.metaheuristics.algorithms.SearchAlgorithm SearchAlgorithm}:
 *     one for Random Search, and one for Simulated Annealing.
 *     </li>
 * </ul>
 * Inside this package, you may create as many classes, interfaces, etc. as you like. However, do
 * not alter the classes and interfaces that are already given to you.
 */
package de.uni_passau.fim.se2.test_prioritization;