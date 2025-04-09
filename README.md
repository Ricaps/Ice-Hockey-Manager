# Ice Hockey Manager

Several human players can manage their hockey teams out of a list of real ice hockey teams of several
championships across Europe. Human players can pick their team and add / remove ice hockey players from a list of
available free agents. There is a schedule of games and results will be generated taking into account the players
characteristics (no need to have some advanced algorithm simulating games: just a simple randomization will do it!).
Admin can put new hockey players in the main list of free agents and change their attributes before they are selected by
other human players. If you want, you can implement a budget system for each team, so that players can be bought and
sold based on the financial availability of teams.

## How to run

Since the services have **externalized** configuration, you must include the path to the configuration in run command.

1. Run `mvn clean install` in the root directory.
2. Then for each service, go to its sub-folder (e.g. `cd game-service`) and run:
   1. `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.location=file:../etc/world-list-service.yaml"`
   2. `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.location=file:../etc/team-service.yaml"`
   3. `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.location=file:../etc/game-service.yaml"`
   4. `mvn spring-boot:run -Dspring-boot.run.arguments="--spring.config.location=file:../etc/user-service.yaml"`

## Microservices

### 1. Word List Service

> Responsible person: Matej Keznikl

World List service takes care about the list of the real ice hockey teams. Use can see player characteristics,
information about championship where the player and its team belongs. There are multiple types of the
championships (International, Continental, National, ...). Users can choose players listed in this service into his
team.

### 2. Team Service

> Responsible person: Jan Martinek

Team Service cares about the managing user's fictive teams. It means, when the user chooses (buys) player from the
world list, then the player becomes managed by the Team service. It cares about grouping players into the teams, which
can then register and participate in the competitions. The service also takes care about the budget of the team,
so then users can buy or sell the players. Teams earn money from the won competitions.

### 3. Game Service

> Responsible person: Martin Procházka

Game Service takes care about the competitions and matches. Admin can create competitions, where the team can
participate.
Team registers into the competitions. For each competition, the matches can be generated within the defined timeframe of
the competition. When the matches are created, they are automatically started at the defined time. The result of the
match
is done by randomization and takes teams characteristics into account (in the next Milestone). Other services are
informed
about the finished match and the result. User can also create friendly match between two teams.

### 4. User service

> Responsible person: Jakub Dvořák

User Service takes care about users (players). User can create and manage his account. He can also buy Budget offer
packages, which gives user extended initial budget for buying players and composing his teams. User can have assigned
roles (Player, Admin, etc...). The service will also serve as authentication server.

## Use case diagram

![use-case-diagram.png](docs/diagrams/use-case/use-case.png)

## Class diagram for the DTOs

### World List Service

![world-list-service-dtos.png](docs/diagrams/class/world-list-service-dtos.png)

### Team Service

![team-service-dtos.png](docs/diagrams/class/team-service-dtos.png)

### Game Service

![game-service-dtos.png](docs/diagrams/class/game-service-dtos.png)

### User Service

![user-service-dtos.png](docs/diagrams/class/user-service-dtos.png)