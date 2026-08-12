# Console Doom

A top-down, terminal-based arena shooter written in Java. Fight off waves of
monsters on an ASCII battlefield rendered in a [Lanterna](https://github.com/mabe02/lanterna)
terminal window, with user accounts, an admin panel, and a persistent
leaderboard backed by PostgreSQL.

## Features

- **Wave-based combat** — survive escalating waves of monsters in a bounded arena.
- **Multiple monster types** — `Basic`, `Fast`, and `Tank` monsters, created via a
  Monster Factory and driven by an A\*-pathfinding AI.
- **Accounts & auth** — register/log in before playing; sessions time out after
  inactivity.
- **Role-based access control** — `USER` and `ADMIN` roles with a permission system
  gating features such as the admin panel.
- **Admin panel** — manage users and view game data from inside the game.
- **Persistent leaderboard** — scores, kills, waves, and survival time saved to
  PostgreSQL and ranked across players.

## Requirements

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL** database (connection is configured in
  `src/main/resources/application.properties`)
- A desktop environment — the game opens a Swing-based terminal emulator window,
  so it needs a display (not a headless server).

## Build & Run

```bash
# Build a runnable jar
mvn clean package

# Run
java -jar target/console-doom-1.0.0.jar
```

Or run directly with Maven during development:

```bash
mvn compile exec:java -Dexec.mainClass=com.consoledoom.Main
```

## Controls

| Key            | Action                          |
| -------------- | ------------------------------- |
| `W` `A` `S` `D` | Move up / left / down / right   |
| Arrow keys     | Aim and shoot in that direction |
| `Q`            | Quit the current run            |
| `Esc`          | Quit the game (from login)      |

The player is shown as `@`, walls as `#`, and monsters and bullets as their own
symbols on the arena grid.

## Configuration

Game and infrastructure settings live in
`src/main/resources/application.properties`. Highlights:

```properties
# Database
db.url=jdbc:postgresql://<host>:5432/postgres?sslmode=require
db.user=<user>
db.password=<password>

# Game
game.arena.width=60
game.arena.height=15
game.target.fps=10
game.max.health=5

# Security
security.password.min.length=4
security.session.timeout.minutes=30

# Default admin account
admin.default.username=admin
admin.default.password=admin123
```

> **⚠️ Security note:** The committed `application.properties` currently contains a
> live database URL, credentials, and a default admin password. Before using this
> project for anything real, move these out of version control (e.g. into
> environment variables or an untracked local config) and rotate any exposed
> credentials.

## Project Structure

```
src/main/java/com/consoledoom/
├── Main.java            # Entry point
├── core/                # Game loop and state machine
├── ui/                  # Auth, menu, game, leaderboard, admin, game-over screens
├── arena/               # Arena / map model
├── entities/            # Player, Bullet, Wall, and monster types
│   └── monsters/        # Basic / Fast / Tank monsters + factory
├── systems/             # Movement, collision, combat, weapons, monster AI, input
├── render/              # Terminal renderer
├── factory/             # Monster factory
├── service/             # Auth and admin services
├── security/            # Roles, permissions, security context
├── validation/          # User and game-data validators
├── db/                  # Database connection and DAOs
├── models/              # User, LeaderboardEntry
├── config/              # GameConfig
└── utils/               # Vec2, A* pathfinder

src/main/resources/
├── application.properties
└── maps/                # ASCII map layouts (map1.txt, map2.txt)
```

## Tech Stack

- Java 17
- Maven
- [Lanterna](https://github.com/mabe02/lanterna) — terminal UI
- PostgreSQL (via the JDBC driver)
- [Lombok](https://projectlombok.org/) — boilerplate reduction

## Design Patterns

The codebase demonstrates several classic patterns: a **Factory** for creating
monsters, a **Singleton** for the database connection and game configuration, a
**state machine** driving the game loop, and a system/entity split for game logic.

## License

See [LICENSE](LICENSE).
