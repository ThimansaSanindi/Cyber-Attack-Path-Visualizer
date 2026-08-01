# core.graph — Core Graph & Data Model

## Purpose
Provides the network graph data structure for the Cyber Attack Path Visualizer.
Represents devices as nodes and connections as edges, using an adjacency-list
structure for efficient neighbor lookups during BFS attack simulation.

## Classes

- **NetworkGraph**
- **Node** — represents a device (id, label, x/y position for rendering).
- **Edge** — represents a connection between two nodes (sourceId, targetId).
- **GraphImpl** — adjacency-list implementation of NetworkGraph, backed by a
  `HashMap<String, List<Edge>>`.

## Features
- Add/remove nodes and edges
- Duplicate node ID validation
- Safe handling of isolated nodes and self-loop edges
- Neighbor lookup and full adjacency list retrieval
- Save/load a network configuration to/from JSON (via Gson)

## Example usage

```java
GraphImpl graph = new GraphImpl();
graph.addNode(new Node("A", "Server A", 0, 0));
graph.addNode(new Node("B", "Server B", 100, 0));
graph.addEdge(new Edge("A", "B"));

List<String> neighbors = graph.getNeighbors("A"); // ["B"]

graph.saveToFile("network.json");
```

## Testing
Unit tests are in `src/test/java/core/graph/GraphImplTest.java`, covering node/edge
CRUD, duplicate IDs, isolated nodes, self-loops, and JSON save/load round-tripping.
Run with:
```
mvn test
```