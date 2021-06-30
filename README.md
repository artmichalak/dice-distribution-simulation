# Dice Distribution Simulator
This project provides endpoints for performing random dice rolling simulations.

## Building and starting the application
In order to build the executable jar run:

```gradlew build --no-daemon --console verbose```

The resulting jar will be located in ```build/libs/``` catalog.
To run the program execute the following command in the console:

```java -jar build/libs/dice-distribution-simulation-1.0-SNAPSHOT.jar```

From IDE, simply run tech.blackfall.dicedist.DiceDistributionApplication main class.

## API definitions
To visit full API definitions go to: `http://localhost:8080/swagger-ui/index.html`

## Endpoints
* `GET /v1/simulation` - returns a random distribution for default setup (3 dice, 6-sided, 100 rolls)
* `GET /v1/simulation?dice={numberOfDice}&sides={numberOfSides}&rolls={numberOfRolls}&mode={simulationMode}` - return a random distribution for `numberOfDice` dice, each having `numberOfSides` sides and executing `numberOfRolls` rolls. Additional parameter `mode` can have one of two values: `iter` - iterative algorithm or `conc` - concurrent using fork join pool. By default, the service uses `iter` version.

All parameters are optional. If any of the parameters is missing then the method uses a relevant defaults (3 dice, 6-sided, 100 rolls).

## Architecture overview
The project follows hexagonal architecture principles (ports and adapters pattern) as described by Tom Hombergs to some point.

The `GET /v1/simulation` method does not seem to be omnipotent at first. However this endpoint does not change the state of the server. It merely returns two arrays of random values.

Since `GET` operations can be cached by web servers or intermediate software, the service adds headers to control caching: `Cache-Control: no-cache, must-revalidate`. This ensures the distribution is generated between different calls.  

Most of JavaScript plotting libraries use arrays to initialize the graphs. Hence, the service returns two arrays:
* `totals` - containing sorted list of total sum of dice in the distribution (`NOTE:` if there was no occurrence of a value then it is not present to reduce the payload)
* `occurrences` - containing the number of occurrences of a corresponding total (positions match the `totals` array)

Current version contains two implementations of `SimulationDistributionProvider` for generating distribution:
* iterative `IterativeSimulationDistributionProvider` uses brute-force nested looped solution. Its complexity is `O(m*n)` where `m` is the number of dice and `n` number of rolls.
* concurrent `ConcurrentSimulationDistributionProvider` uses two types of recursive actions. First one splits the number of rolls into chunks of at most 1024 elements, while the second splits the number of dice into chunks of at most 128 elements. The algorithm uses a fork join pool which is tuned to use all `availableProcessors` for partial processing.
