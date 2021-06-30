# Dice Distribution Simulator
This project provides endpoints for performing random dice rolling simulations.

## Building and starting the application
The project has been developed using JDK15 and this particular version is recommended for building and running the code.

In order to build the executable jar run:

```$ gradlew build --no-daemon --console verbose```

The resulting jar will be located in ```build/libs/``` catalog.

Before running the program, we need to launch the database. We use a simple, yet powerful PostgreSQL database for this purpose and Docker (with Testcontainers for testing). To launch the database, use `docker-compose` in the main project directory as follows: \
```$ docker-compose up -d```

This will bring up a running instance of PostgreSQL container in the background.

To shut down the container use: \
```$ docker-compose down```

To run the program execute the following command in the console:

```$ java -jar build/libs/dice-distribution-simulation-1.0-SNAPSHOT.jar```

From IDE, simply run `tech.blackfall.dicedist.DiceDistributionApplication` main class.

## API definitions
To visit full API definitions go to: `http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`

## Endpoints
* `GET /v1/simulation` - returns a random distribution for default setup (3 dice, 6-sided, 100 rolls)
* `GET /v1/simulation?dice={numberOfDice}&sides={numberOfSides}&rolls={numberOfRolls}&mode={simulationMode}` - return a random distribution for `numberOfDice` dice, each having `numberOfSides` sides and executing `numberOfRolls` rolls. Additional parameter `mode` can have one of two values: `iter` - iterative algorithm or `conc` - concurrent using fork join pool. By default, the service uses `iter` version.
* `POST /v1/simulation` - does exactly the same as `GET` but the payload contains parameters:
```
  {
   "dice": "{numberOfDice}",
   "rolls": "{numberOfRolls}",
   "sides": "{numberOfSides}",
   "mode": "{ITER|CONC}"
  }
```
All parameters for above methods are optional. If any of the parameters is missing then the method uses a relevant defaults (3 dice, 6-sided, 100 rolls).

* `GET /v1/simulation/stats` - returns global statistics grouped by dice and sides. The numbers of dice and sides are glued together using a hyphen separator to reduce payload.
* `GET /v1/simulation/distribution?dice={numberOfDice}&sides={numberOfSides}` - returns relative statistics for given numbers of dice and sides compared to the total rolls for all the simulations. The results are similar to what `GET /v1/simulation` returns - however, instead of occurrences there is `percentages` field which contains a floating-point value representing percentage of such result in the whole set. In general, all percentages should sum up to 100% but due to floating-point approximation to two decimal points there may be some discrepancies (i.e., 0.02 difference).

## Architecture overview
The project follows hexagonal architecture principles (ports and adapters pattern) as described by Tom Hombergs to some point.

Technically, the `GET /v1/simulation` method is not omnipotent since it changes the server state and does not return the same values (since they are randomly generated). However, we can assume that the persistence is a side effect, used the same way as metrics or service telemetry.


Since `GET` operations can be cached by web servers or intermediate software, the service adds headers to control caching: `Cache-Control: no-cache, must-revalidate`. This ensures the distribution is generated between different calls.  

Most of JavaScript plotting libraries use arrays to initialize the graphs. Hence, the service returns two arrays:
* `totals` - containing sorted list of total sum of dice in the distribution (`NOTE:` if there was no occurrence of a value then it is not present to reduce the payload)
* `occurrences` or `percentages` - containing the number of occurrences/percentages of a corresponding total (positions match the `totals` array) 

Current version contains two implementations of `SimulationDistributionProvider` for generating distribution:
* iterative `IterativeSimulationDistributionProvider` uses brute-force nested looped solution. Its complexity is `O(m*n)` where `m` is the number of dice and `n` number of rolls.
* concurrent `ConcurrentSimulationDistributionProvider` uses two types of recursive actions. First one splits the number of rolls into chunks of at most 1024 elements, while the second splits the number of dice into chunks of at most 128 elements. The algorithm uses a fork join pool which is tuned to use all `availableProcessors` for partial processing.

Code has been written using [Google Code Style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml)
