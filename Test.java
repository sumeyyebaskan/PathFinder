package GraphPackage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import ADTPackage.DictionaryInterface;
import ADTPackage.LinkedQueue;
import ADTPackage.LinkedStack;
import ADTPackage.QueueInterface;
import ADTPackage.StackInterface;
import ADTPackage.UnsortedLinkedDictionary;

public class Test {
	private UndirectedGraph<String> mazzee = new UndirectedGraph();
	private DictionaryInterface<String, Character> maze = new UnsortedLinkedDictionary<>();

	public int getRowNumber(String fileName) throws FileNotFoundException {
		int rowNumber = 0;
		File mazeFile = new File(fileName); // to open file
		try (Scanner scan = new Scanner(mazeFile)) {
			while (scan.hasNext()) {
				scan.nextLine();
				rowNumber++;
			}
		}
		mazzee.setRowNumber(rowNumber);
		return rowNumber;
	}

	public int getColNumber(String fileName) throws FileNotFoundException {
		int colNumber = 0;
		File mazeFile = new File(fileName); // to open file
		try (Scanner scan = new Scanner(mazeFile)) {
			colNumber = scan.nextLine().length();
		}
		mazzee.setColNumber(colNumber);
		return colNumber;
	}

	public void getMaze(String fileName) throws FileNotFoundException {
		File mazeFile = new File(fileName); // to open file
		try (Scanner scan = new Scanner(mazeFile)) {
			getRowNumber(fileName);
			getColNumber(fileName);

			for (int i = 0; scan.hasNext(); i++) {
				String line = scan.nextLine();
				for (int j = 0; j < line.length(); j++) {
					String label = String.valueOf(i) + "-" + String.valueOf(j);
					maze.add(label, line.charAt(j));//stores the maze in a dictionary
					if (line.charAt(j) == ' ') {//adds if it is a vertex
						mazzee.addVertex(label);

					}
				}
			}

			for (int i = 0; i < mazzee.getRowNumber(); i++) {
				for (int j = 0; j < mazzee.getColNumber(); j++) {

					String label = String.valueOf(i) + "-" + String.valueOf(j);
					String up = String.valueOf(i - 1) + "-" + String.valueOf(j);
					String down = String.valueOf(i + 1) + "-" + String.valueOf(j);
					String left = String.valueOf(i) + "-" + String.valueOf(j - 1);
					String right = String.valueOf(i) + "-" + String.valueOf(j + 1);
//					checks if it has an edge to left
					if (maze.getValue(label) == ' ' && (maze.getValue(left) != null && maze.getValue(left) == ' ')) {
						String beginVertex = String.valueOf(i) + "-" + String.valueOf(j);
						String endVertex = String.valueOf(i) + "-" + String.valueOf(j - 1);
						int weight = (int) ((Math.random() * 4) + 1);
						mazzee.addEdge(beginVertex, endVertex, weight);
					}
//					checks if it has an edge to right
					if (maze.getValue(label) == ' ' && (maze.getValue(right) != null && maze.getValue(right) == ' ')) {
						String beginVertex = String.valueOf(i) + "-" + String.valueOf(j);
						String endVertex = String.valueOf(i) + "-" + String.valueOf(j + 1);
						int weight = (int) ((Math.random() * 4) + 1);
						mazzee.addEdge(beginVertex, endVertex, weight);
					}
//					checks if it has an edge to up
					if (maze.getValue(label) == ' ' && (maze.getValue(up) != null && maze.getValue(up) == ' ')) {
						String beginVertex = String.valueOf(i) + "-" + String.valueOf(j);
						String endVertex = String.valueOf(i - 1) + "-" + String.valueOf(j);
						int weight = (int) ((Math.random() * 4) + 1);
						mazzee.addEdge(beginVertex, endVertex, weight);
					}
//					checks if it has an edge to down
					if (maze.getValue(label) == ' ' && (maze.getValue(down) != null && maze.getValue(down) == ' ')) {
						String beginVertex = String.valueOf(i) + "-" + String.valueOf(j);
						String endVertex = String.valueOf(i + 1) + "-" + String.valueOf(j);
						int weight = (int) ((Math.random() * 4) + 1);
						mazzee.addEdge(beginVertex, endVertex, weight);
					}
				}
			}

		}

	}

	public void DisplayMaze(String type, QueueInterface<String> queueMaze) {
		int visited_vertex = 0;
		while (!queueMaze.isEmpty()) {//counts the visited vertex number and dots the path
			String label = queueMaze.dequeue();
			maze.add(label, '.');
			visited_vertex++;
		}
		System.out.println("Number of visited vertices for " + type + " is: " + visited_vertex);
		//displays the maze
		for (int i = 0; i < mazzee.getRowNumber(); i++) {
			for (int j = 0; j < mazzee.getColNumber(); j++) {
				String label = String.valueOf(i) + "-" + String.valueOf(j);

				if (maze.getValue(label) == '.') {
					System.out.print(maze.getValue(label));
					maze.add(label, ' ');
				} else
					System.out.print(maze.getValue(label));

			}
			System.out.println();
		}
	}

	public void DisplayMaze(String type, StackInterface<String> path) {
		int visited_vertex = 0;
		while (!path.isEmpty()) {
			String label = path.pop();
			maze.add(label, '.');
			visited_vertex++;
		}
		System.out.println("Number of visited vertices for " + type + " is: " + visited_vertex);

		for (int i = 0; i < mazzee.getRowNumber(); i++) {
			for (int j = 0; j < mazzee.getColNumber(); j++) {
				String label = String.valueOf(i) + "-" + String.valueOf(j);

				if (maze.getValue(label) == '.') {
					System.out.print(maze.getValue(label));
					maze.add(label, ' ');
				} else
					System.out.print(maze.getValue(label));

			}
			System.out.println();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void test() throws FileNotFoundException {
		getMaze("maze2.txt");
		mazzee.displayEdges();
		mazzee.AdjacencyMatrix();
		System.out.println("The number of edges is: " + mazzee.getNumberOfEdges());
		QueueInterface<String> queuemaze = mazzee.getBreadthFirstTraversal("0-1", "19-20");
		System.out.println();
		System.out.println("Breadth First Traversal");
		DisplayMaze("BFS", queuemaze);
		System.out.println();
		queuemaze = new LinkedQueue();
		queuemaze = mazzee.getDepthFirstTraversal("0-1", "19-20");
		System.out.println("Depth First Traversal");
		DisplayMaze("DFS", queuemaze);
		System.out.println();
		StackInterface<String> path = new LinkedStack();
		mazzee.getShortestPath("0-1", "19-20", path);
		System.out.println("Shortest Path");
		DisplayMaze("Shortest Path", path);
		System.out.println();
		path = new LinkedStack();
		double cost= mazzee.getCheapestPath("0-1", "19-20", path);
		System.out.println("Cheapest Path");
		DisplayMaze("Cheapest Path", path);
		System.out.println("Path cost is: " + cost);
		System.out.println();
	}
}
