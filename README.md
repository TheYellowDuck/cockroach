# Cockroach

A real-time 2D **Java** desktop game where cockroaches swarm in from the edges of the screen. Squash them before they eat your crumbs. The cockroaches run a lightweight greedy-steering AI to seek food and flee from fast cursor movement, all rendered with procedural `Graphics2D` animation.

https://github.com/user-attachments/assets/1be13148-a2f2-4951-9818-146e4dd8ade0

## Gameplay

Cockroaches spawn continuously from all four edges and wander across the screen. Drop bread crumbs to lure them — cockroaches use greedy steering to seek the nearest crumb and will eat it down to nothing. Move your cursor too quickly near a cockroach and it will scatter. Left-click to squash; a splat mark fades on the floor where it died.

| Input | Action |
|---|---|
| Left-click | Squash cockroach |
| Left-click + drag | Pick up and throw a cockroach |
| Right-click (empty space) | Place a bread crumb (max 5) |
| Right-click (on crumb) | Remove that crumb |

## How It Works

- **Swing game loop** — `javax.swing.Timer` drives all updates on the EDT, giving thread-safe state mutation without locks while keeping repaint latency low.
- **Greedy steering AI** — cockroaches find the nearest crumb and pick the 8-directional heading with the highest dot product against the target bearing, replacing an earlier pixel-level BFS that allocated a fresh 600 × 600 visited array per entity per tick.
- **Flee behaviour** — peak cursor speed is sampled between direction-update cycles; any cockroach within 100 px when the speed threshold is exceeded steers directly away for ~0.8 s.
- **Custom 2D renderer** — entities are drawn with `Graphics2D` transforms (translate → rotate → scale): body oval, head, eyes, antennae, and three leg pairs, all rotated to face the direction of travel. Death plays a squish-and-fade animation by scaling the transform over 100 frames.
- **Persistent splat marks** — squash events stamp a dark oval into a separate list that fades over ~5 seconds independently of the entity lifecycle.
- **Crumb consumption** — each crumb has 10 health points; stopped cockroaches bite stochastically (~3 % chance per frame), and the crumb shrinks visually in proportion to remaining health.

## Skills Demonstrated

- Object-oriented design — `Cockroach`, `Entity`, `Mouse`, and `Layout` classes
- Event-driven programming — Java Swing mouse handling and timer-driven updates
- Game loop architecture — `javax.swing.Timer` on the EDT for lock-free, thread-safe state
- Game AI & steering behaviours — greedy seek toward nearest crumb, flee on fast cursor
- Vector math — 8-directional heading chosen by maximum dot product against the target bearing
- Performance optimisation — replaced a per-tick 600×600 BFS scan with O(1) greedy steering
- Collision & hit detection — click-to-squash hit testing and crumb proximity
- Custom 2D rendering — `Graphics2D` affine transforms (translate/rotate/scale), procedural sprites
- Animation — squish-and-fade death and time-decaying splat marks
- Object lifecycle management — independent entity, crumb, and splat lists
- Stochastic behaviour — probabilistic crumb biting and randomised spawning
- Input handling — left-click squash, drag-and-throw, right-click crumb placement
- Java Platform Module System — modular build with `module-info.java`
- JAR packaging — runnable modular Java application

## Tech Stack

- Java 17+
- Java Swing / AWT (`JPanel`, `Graphics2D`, `javax.swing.Timer`, `MouseListener`)
- Java Platform Module System (`module-info.java`)
- Packaged as a runnable JAR (`cockroach.jar`)
- Eclipse IDE

## Getting Started

Requires **Java 11+**.

```bash
java -jar cockroach.jar
```

Or double-click `cockroach.jar` if your system has a `.jar` file association.
