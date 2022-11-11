![Coverage](.github/badges/jacoco.svg) ![Branches](.github/badges/branches.svg)
Improve your tests using [Gamekins](http://jenkins.se2.fim.uni-passau.de:8080/).
[Learn how](#Gamekins).

# Test Case Prioritization

A framework to solve the regression test case prioritization problem as part of the Search-Based
Software Engineering course at the University of Passau.

The project uses the [Gradle](https://gradle.org) build system for building and dependency 
management. It can be simply imported to IntelliJ IDEA or Eclipse by using the import helpers
provided by these IDEs. Of course, it is possible to use other IDEs as well, but this might require 
you to do things manually.

For example, to build your application and create an executable JAR artefact, you can run the 
provided Gradle wrapper script and specify the `jar` task:
```bash
./gradlew jar
```

To run the generated JAR artefact from the command line:
```
usage: java -jar build/libs/test-prioritization-1.0-SNAPSHOT.jar
 -a,--algorithms <arg>      which algorithms to use (any combination of
                            "RS:RW:SA"; default: "RS:SA")
 -c,--class <arg>           the name of the class under test
 -f,--fitness-evals <arg>   maximum number of fitness evaluations per
                            repetition
 -m,--matrix <arg>          load coverage matrix with the given name
 -o,--ordering <arg>        specify an ordering manually (e.g., "1:2:0")
 -p,--package <arg>         the package containing the class under test
                            (default: "de.uni_passau.fim.se2.examples")
 -q,--quiet                 redirect some console output to files
                            (default: "false")
 -r,--repetitions <arg>     how often to repeat the search (default: "30")
 -s,--seed <arg>            use a fixed RNG seed
 -t,--time <arg>            maximum search time per repetition, in seconds
                            or "HH:MM:SS"
```

We refer you to the assignment sheet and the exercise class for more information and questions.

## Implementation

Unless otherwise stated, always put your custom implementations into the package
[test_prioritization](src/main/java/de/uni_passau/fim/se2/test_prioritization).

1. Define the solution encoding ("configuration") by implementing the interfaces/abstract 
   classes in the [configurations](src/main/java/de/uni_passau/fim/se2/metaheuristics/configurations)
   package:
   - Create a subclass for `Configuration` to represent test case orderings.
   - Implement an elementary transformation for your subclass.
   - Define a generator to create random configurations from scratch.
2. Create a fitness function for the APLC metric ("average percentage of lines covered") by 
   implementing one of the interfaces in the
   [fitness_functions](src/main/java/de/uni_passau/fim/se2/metaheuristics/fitness_functions) 
   package.
3. Implement a
   [stopping condition](src/main/java/de/uni_passau/fim/se2/metaheuristics/stopping_conditions)
   similar to `MaxTime` but use a maximum number of fitness evaluations instead of time as
   search budget.
4. Implement the
   [SearchAlgorithm](src/main/java/de/uni_passau/fim/se2/metaheuristics/algorithms/SearchAlgorithm.java)
   interface: create one class for Random Search, and another class for Simulated Annealing.

The [Bridge](src/main/java/de/uni_passau/fim/se2/Bridge.java) class acts as a bridge between already
existing scaffolding code and the custom code you implement. It sketches out utility and factory
methods for creating new search algorithms, stopping conditions, etc. These will be invoked by
the scaffolding code when running the application. As such, most of these methods still lack an
implementation (as indicated by a comment), and simply throw an exception:
```java
// TODO: please implement
throw new UnsupportedOperationException("please implement");
```
Give a working implementation for these methods.

## Tests

You have to provide unit tests for your custom code in the
[test_prioritization](src/main/java/de/uni_passau/fim/se2/test_prioritization) package. Use an
appropriate [package structure](src/test/java/de/uni_passau/fim/se2/test_prioritization) for your
tests.

You need *not* implement own tests for the code that was already given to you (i.e., everything 
outside the `test_prioritization` package). The 
[Bridge](src/main/java/de/uni_passau/fim/se2/Bridge.java) class already comes with a test suite
[BridgeTest](src/test/java/de/uni_passau/fim/se2/BridgeTest.java). It checks the
correct computation of the APLC metric. You are required to pass these checks!

## Jupyter Notebook

Put your Jupyter Notebook in the [csv](csv) folder.

## Gamekins

In order to get Gamekins to work for you, you have to follow a few small steps:

- Add your ZIM username to your `WHOAMI.md` file
- You will get an email (https://email.uni-passau.de/) with your account details for the used Jenkins instance, usually on the next working day
- Go to the university or install and activate OpenVPN (https://www.zim.uni-passau.de/en/services/network-and-server/network-access/openvpn/)
- Login to the Jenkins instance with your credentials (http://jenkins.se2.fim.uni-passau.de:8080/)
- Navigate to your project in the folder Search-Based Software Engineering -> Test Case Prioritization. The project is named after your ZIM username
- Navigate to the leaderboard on the left side to see your challenges/tasks
- Write a test that solves a challenge, commit and push it
- Trigger a build of your project manually or wait for an automatically triggered build (once a hour) to see if the challenge is really solved
- Also have a look at the leaderboard at each task and the whole course

Note: the first assignment uses a system test `BridgeTest.java`. By default,
this test fails because method `computeAPLC` inside class `Bridge.java`
throws an exception. This prevents Gamekins from generating new challenges.
To resolve this, you have to implement `computeAPLC`. Returning a dummy value
such as `0` is sufficient to make Gamekins work. If you do this, please do not
forget to revisit `computeAPLC` and put the correct implementation there before
the end of the deadline.

