# Risk 
An implementation of the board game _Risk_ to fulfil the project requirements of SOEN6441 at Concordia University, Montréal.

### Details:
- Programming Language - Java
- Testing Framework - JUnit 4
- Documentation Tool - Javadoc
- Build Tool - Maven
- SCM - git

### Architectural Design

We follow a model similar to the Model View Controller (MVC) pattern. This is how the directory structure looks like:

```
- src/
-- models/
-- views/
-- controllers/
-- utils/
- test/
```

![Design Diagram](https://i.imgur.com/X5jPtNX.png)

The model classes hold the definition for the various entities in the game: `Continent`, `Country`, `GameMap`, `Player` etc. The views house the CLI `Runner` and will hold the GUI elements (future builds). The controller is the `GameRunner` which is the main driver for our game loop. The utils folder has all sorts of utility methods to help our processing like the `MapParser` and the `CLI` manager class.

This bifurcation allows us to have different logical components in their respective directories. The unit tests are in the test folder separated using the same directory structure as the main Java source.

Our main method resides in the `Runner` class which allows the players to type in commands on a CLI. Using the `EditMap` and `MapParser` utilities, we handle .map files parsing and editing. The `GameRunner` singleton takes over when the player is ready to play the game. Throughout the project, the `CLI` utility class maintains the context using an Enum to validate which commands are allowed in the current phase.

```java
// Handle the various phases for validation
public enum Context {
  MAIN_MENU,
  GAME_SETUP,
  GAME_REINFORCE,
  GAME_ATTACK,
  GAME_FORTIFY,
  EDITOR
}
```

The `GameMap` is the mutable object holding the current state of the game. A reference to this object is passed around and used while processing most gameplay commands. The validations and management of the game phases is all done within the `GameRunner`. 

### Coding Standards

We follow consistent coding standards in our team.

Most standard Java conventions are used. Variables are camel cased and class names are proper singular nouns. Constants are named using capitals and underscores. White space after each declaration and definition is present. A 2-space model is followed for indentation, as [defined by Google](https://plugins.jetbrains.com/plugin/8527-google-java-format). Comments are spell-checked and maintained for clarity of code. All code is documented using Javadoc, including private instance members.

We strive for high code quality. To ensure our code stays readable, brief but descriptive names are used for variables and methods. Utility methods are separated modules to ensure reusability and maintainability. We almost always work using Pair Programming to ensure all members are aware of all the sections of the code, as well as having a consensus on the methodology of the code we write.

### References:
- [SourceForge for src & rules](https://sourceforge.net/projects/domination/)
- [Dr.Joey Paquet's - Project Document](https://users.encs.concordia.ca/~paquet/wiki/images/2/25/Project.SOEN6441.2019.2.pdf)
