# Cyber Attack Path Visualizer

PDSA group coursework. A Java/Swing tool that lets you build a small network
diagram, pick a start node, and simulate how a compromise at that node would
spread across the network using BFS — with a step-by-step playback and a
risk-scoring report of which devices matter most.

## What it does

1. **Build a network** — place devices on a canvas, wire them together, pick
   which device the attack starts from.
2. **Run the simulation** — a BFS traversal spreads the "infection" outward
   from the start node, tracking the order and step each device is reached.
3. **Watch it play out** — step-through animation showing infected vs. safe
   devices, plus a stats panel with risk scores and the most-affected device.


## Running it

Requires Java 17+ and Maven.

```
mvn compile
mvn "-Dexec.mainClass=Main" exec:java
```

### Using the app

- **Add Device**: fill in the ID/label/type fields, click *Add Device*, then
  click anywhere on the canvas to place it.
- **Connect an edge**: click one node, then click a different node — that
  draws an edge between them. (Not drag-to-draw — two separate clicks.)
- **Drag a node**: click and drag it to reposition (only works when you're
  not mid-way through connecting an edge).
- **Undo Last Device**: removes the most recently placed device.
- **Save Network**: saves the current graph to JSON.
- **Start node dropdown**: pick which device the attack simulation starts
  from.
- **Run Simulation**: runs the BFS attack simulation on the network you
  built and switches to the *Playback & Stats* tab with the real result.

## How the simulation works

`AttackSimulatorImpl` (`core.simulation`) does two things from a given start
node:

1. **BFS traversal** using a `Queue`, tracking infection order and the BFS
   step each node is reached at (`infectionOrder`, `infectionStep`).
2. **Risk scoring** for every node in the graph — not just infected ones —
   combining:
   - **connectivity**: node's out-degree, normalised against the
     highest-degree node in the graph
   - **impact**: how many nodes are reachable from it (a second BFS run from
     that node), normalised against the graph's max reach

   `score = 0.4 * normalizedDegree + 0.6 * normalizedReach`, weighted so
   blast-radius matters more than raw connection count. The node with the
   highest score becomes `mostAffectedNodeId`.

Edge cases handled: isolated nodes (no edges, never infected, still scored),
single-node graphs, cyclic graphs (visited-set prevents infinite loops), and
an invalid start node (throws an exception rather than returning a broken
result).


## Project structure

```
src/main/java/
  Main.java                     entry point — wires all 4 modules together
  core/graph/                   NetworkGraph, Node, Edge, GraphImpl (Perera)
  core/simulation/               AttackSimulator, AttackSimulatorImpl,
                                  SimulationResult, InfectionRecord (Mohanraj)
  ui/builder/                    NetworkBuilderPanel, DeviceForm (Sanindi)
  ui/playback/                   AttackPlaybackPanel, StatsPanel (Ashroff)
src/test/java/core/graph/        GraphImplTest.java
```



## Testing

```
mvn test
```
runs the graph unit tests. The simulation engine has a standalone manual
test runner (see above) rather than a JUnit suite. Integration was tested
manually end-to-end: build a network → connect edges → pick a start node →
run simulation → verify playback and stats reflect the real graph.
