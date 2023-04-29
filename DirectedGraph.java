package GraphPackage;

import java.util.Iterator;
import ADTPackage.*; // Classes that implement various ADTs

/**
 * A class that implements the ADT directed graph.
 * 
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 * @version 5.1
 */
public class DirectedGraph<T> implements GraphInterface<T> {
	private DictionaryInterface<T, VertexInterface<T>> vertices;
	private int edgeCount;
	private int rowNumber;
	private int colNumber;

	public DirectedGraph() {
		vertices = new UnsortedLinkedDictionary<>();
		edgeCount = 0;
	} // end default constructor

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public int getColNumber() {
		return colNumber;
	}

	public void setColNumber(int colNumber) {
		this.colNumber = colNumber;
	}

	public boolean addVertex(T vertexLabel) {
		VertexInterface<T> addOutcome = vertices.add(vertexLabel, new Vertex<>(vertexLabel));
		return addOutcome == null; // Was addition to dictionary successful?
	} // end addVertex

	public boolean addEdge(T begin, T end, double edgeWeight) {
		boolean result = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ((beginVertex != null) && (endVertex != null))
			result = beginVertex.connect(endVertex, edgeWeight);
		if (result)
			edgeCount++;
		return result;
	} // end addEdge

	public boolean addEdge(T begin, T end) {
		return addEdge(begin, end, 0);
	} // end addEdge

	public boolean hasEdge(T begin, T end) {
		boolean found = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ((beginVertex != null) && (endVertex != null)) {
			Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
			while (!found && neighbors.hasNext()) {
				VertexInterface<T> nextNeighbor = neighbors.next();
				if (endVertex.equals(nextNeighbor))
					found = true;
			} // end while
		} // end if

		return found;
	} // end hasEdge

	public boolean isEmpty() {
		return vertices.isEmpty();
	} // end isEmpty

	public void clear() {
		vertices.clear();
		edgeCount = 0;
	} // end clear

	public int getNumberOfVertices() {
		return vertices.getSize();
	} // end getNumberOfVertices

	public int getNumberOfEdges() {
		return edgeCount;
	} // end getNumberOfEdges

	protected void resetVertices() {
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext()) {
			VertexInterface<T> nextVertex = vertexIterator.next();
			nextVertex.unvisit();
			nextVertex.setCost(0);
			nextVertex.setPredecessor(null);
		} // end while
	} // end resetVertices

	public StackInterface<T> getTopologicalOrder() {
		resetVertices();

		StackInterface<T> vertexStack = new LinkedStack<>();
		int numberOfVertices = getNumberOfVertices();
		for (int counter = 1; counter <= numberOfVertices; counter++) {
			VertexInterface<T> nextVertex = findTerminal();
			nextVertex.visit();
			vertexStack.push(nextVertex.getLabel());
		} // end for

		return vertexStack;
	} // end getTopologicalOrder

	@SuppressWarnings("unchecked")
	@Override
	public QueueInterface<T> getBreadthFirstTraversal(T origin, T end) {
		resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<T>();
		QueueInterface<T> vertexQueue = new LinkedQueue<T>();
		vertices.getValue(origin).visit();
		traversalOrder.enqueue(origin);
		vertexQueue.enqueue(origin);
		while (!vertexQueue.isEmpty()) {
			VertexInterface<T> frontVertex = vertices.getValue(vertexQueue.dequeue());
			Iterator<T> itr = (Iterator<T>) frontVertex.getNeighborIterator();
			while (itr.hasNext()) {
				VertexInterface<T> nextNeighbor = (VertexInterface<T>) itr.next();
				if (!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					traversalOrder.enqueue(nextNeighbor.getLabel());
					vertexQueue.enqueue(nextNeighbor.getLabel());
					if (nextNeighbor.getLabel().equals(end)) {
						return traversalOrder;
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public QueueInterface<T> getDepthFirstTraversal(T origin, T end) {
		resetVertices();
		QueueInterface<T> traversalOrder = new LinkedQueue<T>();
		StackInterface<T> vertexStack = new LinkedStack<T>();
		vertices.getValue(origin).visit();
		traversalOrder.enqueue(origin);
		vertexStack.push(origin);
		while (!vertexStack.isEmpty()) {
			VertexInterface<T> topVertex = vertices.getValue(vertexStack.peek());
			Iterator<T> itr = (Iterator<T>) topVertex.getNeighborIterator();
			if (topVertex.hasNeighbor()) {
				VertexInterface<T> nextNeighbor = topVertex.getUnvisitedNeighbor();
				if (nextNeighbor != null) {
					nextNeighbor.visit();
					traversalOrder.enqueue(nextNeighbor.getLabel());
					vertexStack.push(nextNeighbor.getLabel());
					if (nextNeighbor.getLabel().equals(end)) {
						return traversalOrder;
					}
				} else
					vertexStack.pop();
			} else
				vertexStack.pop();

		}

		return null;
	}

	public QueueInterface<T> getDepthFirstTraversal2(T origin, T end) {
		resetVertices();
		System.out.println("yeaasassa");
		boolean isFound = false;
		QueueInterface<T> traversalOrder = new LinkedQueue<T>();
		StackInterface<T> vertexStack = new LinkedStack<T>();
		vertices.getValue(origin).visit();
		traversalOrder.enqueue(origin);
		vertexStack.push(origin);
		while (!vertexStack.isEmpty() && !isFound) {
			VertexInterface<T> topVertex = vertices.getValue(vertexStack.pop());
			Iterator<T> itr = (Iterator<T>) topVertex.getNeighborIterator();
			while (itr.hasNext()) {
				VertexInterface<T> nextNeighbor = (VertexInterface<T>) itr.next();
				if (!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					traversalOrder.enqueue(nextNeighbor.getLabel());
					vertexStack.push(nextNeighbor.getLabel());
					if (nextNeighbor.getLabel().equals(end)) {
						return traversalOrder;
					}
				}
			}
		}

		return null;
	}

	@Override
	public int getShortestPath(T begin, T end, StackInterface<T> path) {
		resetVertices();
		boolean done = false;
		QueueInterface<T> vertexQueue = new LinkedQueue<T>();
		vertices.getValue(begin).visit();
		vertexQueue.enqueue(begin);

		while (!done && !vertexQueue.isEmpty()) {
			VertexInterface<T> frontVertex = vertices.getValue(vertexQueue.dequeue());
			Iterator<T> itr = (Iterator<T>) frontVertex.getNeighborIterator();
			while (!done && itr.hasNext()) {
				VertexInterface<T> nextNeighbor = (VertexInterface<T>) itr.next();
				if (!nextNeighbor.isVisited()) {
					nextNeighbor.visit();
					nextNeighbor.setCost(1 + frontVertex.getCost());
					nextNeighbor.setPredecessor(frontVertex);
					vertexQueue.enqueue(nextNeighbor.getLabel());
				}
				if (nextNeighbor.getLabel().equals(end)) {
					done = true;
				}
			}

		}
		int pathLength = 0;
		if (vertices.getValue(end).isVisited()) {
			pathLength = (int) vertices.getValue(end).getCost();
			path.push(end);

			VertexInterface<T> vertex = vertices.getValue(end);
			while (vertex.hasPredecessor()) {
				vertex = vertex.getPredecessor();
				path.push(vertex.getLabel());
			}
			System.out.println();
		} else
			pathLength = 0;

		return pathLength;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getCheapestPath(T begin, T end, StackInterface<T> path) {
		resetVertices();
		int pathCost = 0;
		boolean done = false;
		PriorityQueueInterface<? super T> priorityQueue = new HeapPriorityQueue();
		priorityQueue.add((T) new EntryPQ(vertices.getValue(begin), 0, null));

		while (!done && !priorityQueue.isEmpty()) {
			EntryPQ frontEntry = (DirectedGraph<T>.EntryPQ) priorityQueue.remove();
			VertexInterface<T> frontVertex = frontEntry.getVertex();
			if (!frontVertex.isVisited()) {
				frontVertex.visit();
				frontVertex.setCost(frontEntry.getCost());
				frontVertex.setPredecessor(frontEntry.getPredecessor());
				if (frontVertex.getLabel().equals(end)) {
					done = true;
				} else {
					Iterator<T> itr = (Iterator<T>) frontVertex.getNeighborIterator();
					Iterator<Double> weight_itr = frontVertex.getWeightIterator();

					while (itr.hasNext()) {

						double weightt = weight_itr.next();
						VertexInterface<T> nextNeighbor = (VertexInterface<T>) itr.next();
						if (!nextNeighbor.isVisited()) {
							int nextCost = pathCost + (int) weightt;
							pathCost = nextCost;
							nextNeighbor.setPredecessor(frontVertex);
							priorityQueue.add((T) new EntryPQ(nextNeighbor, nextCost, frontVertex));
						}
					}
				}
			}
		}

		VertexInterface<T> vertex = vertices.getValue(end);
		path.push(end);
		while (vertex.hasPredecessor()) {
			vertex = vertex.getPredecessor();
			path.push(vertex.getLabel());
		}

		System.out.println();
		return pathCost;
	}

	public void AdjacencyMatrix() {
		Iterator<T> itr = vertices.getKeyIterator();
		System.out.print("         ");
		while (itr.hasNext()) {//prints all the vertices at the top of the page
			String str = String.format("|%-9s", itr.next());
			System.out.print(str);
		}
		System.out.println();
		Iterator<T> itr2 = vertices.getKeyIterator();
		while (itr2.hasNext()) {//gets the first vertex to check if there is an edge existing
			T label = itr2.next();
			String str = String.format("%-9s", label);
			System.out.print(str);
			Iterator<T> itr3 = vertices.getKeyIterator();
			while (itr3.hasNext()) {//gets the second vertex to check
				if (hasEdge(label, itr3.next())) {//if has edge prints 1
					String str1 = String.format("|%-9d", 1);
					System.out.print(str1);
				} else {//if not prints 0
					String str1 = String.format("|%-9d", 0);
					System.out.print(str1);
				}
			}
			System.out.println();//goes to next line for next vertex
		}
	}

	protected VertexInterface<T> findTerminal() {
		boolean found = false;
		VertexInterface<T> result = null;

		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();

		while (!found && vertexIterator.hasNext()) {
			VertexInterface<T> nextVertex = vertexIterator.next();

			// If nextVertex is unvisited AND has only visited neighbors)
			if (!nextVertex.isVisited()) {
				if (nextVertex.getUnvisitedNeighbor() == null) {
					found = true;
					result = nextVertex;
				} // end if
			} // end if
		} // end while

		return result;
	} // end findTerminal

	// Used for testing
	public void displayEdges() {
		System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
		System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext()) {
			((Vertex<T>) (vertexIterator.next())).display();
		} // end while
	} // end displayEdges

	public void display(QueueInterface<T> order) {
		while (!order.isEmpty())
			System.out.println(vertices.getValue(order.dequeue()).getLabel());
	}

	private class EntryPQ implements Comparable<EntryPQ> {
		private VertexInterface<T> vertex;
		private VertexInterface<T> previousVertex;
		private double cost; // cost to nextVertex

		private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex) {
			this.vertex = vertex;
			this.previousVertex = previousVertex;
			this.cost = cost;
		} // end constructor

		public VertexInterface<T> getVertex() {
			return vertex;
		} // end getVertex

		public VertexInterface<T> getPredecessor() {
			return previousVertex;
		} // end getPredecessor

		public double getCost() {
			return cost;
		} // end getCost

		public int compareTo(EntryPQ otherEntry) {
			// Using opposite of reality since our priority queue uses a maxHeap;
			// could revise using a minheap
			return (int) Math.signum(otherEntry.cost - cost);
		} // end compareTo

		public String toString() {
			return vertex.toString() + " " + cost;
		} // end toString
	} // end EntryPQ

} // end DirectedGraph
