/*
 * MyGraph.java
 * Nate Matthews, Jan 15th 2015
 * 
 * MyGraph is an implementation of a directed graph data structure. Nodes are
 * referenced using characters. No two nodes have the same character, and every
 * node has an identifying character. Edges are one-way and each have a weight
 * greater than zero.
 */

import java.util.*;

public class MyGraph {
	Hashtable nodes;
	/* 
	 * Uses the identifying characters as the keys, and objects of the private
	 * class Node as the values. All Nodes are stored in this hashtable.
	 */

	public MyGraph() {
		nodes = new Hashtable();
	}

	/*
	 * Returns true if a node with identifying character 'id' exists in the
	 * graph, false if not.
	 */
	public boolean hasNode(char id) {
		return nodes.containsKey(id);
	}

	/*
	 * Creates a new node with identifying character 'id', unless a node with 
	 * that id already exists, in which case it prints an error message and does
	 * nothing.
	 */
	public void newNode(char id) {
		if (!hasNode(id)) {
			nodes.put(id, new Node(id));
		} else {
			System.out.println("Node '" + id + "' already exists.");
		}
	}

	/*
	 * Returns true if an edge exists from the node identified by 'node1' to the
	 * node identified by 'node2', false if not.
	 */
	public boolean hasEdge(char node1, char node2) {
		Node start;
		if(hasNode(node1)) { //Start node exists?
			start = (Node)nodes.get(node1);
			return start.hasEdge(node2); //Has an edge to node2?
		} else {
			return false;
		}
	}

	/*
	 * Creates a new edge in the table with weight 'wgt', starting from the node
	 * identified by 'node1' and ending at the node identified by 'node2'. If
	 * either of those nodes does not already exist, they are created. Prints an
	 * error message if wgt is less than 1.
	 */
	public void newEdge(char node1, char node2, int wgt) {

		if (wgt < 1) {
			System.out.println("Edge weight must be greater than zero.");
			return;
		}

		Node start;

		//Starting node does not yet exist case:
		if(!hasNode(node1)) {
			newNode(node1);
		}

		//Starting node gotten from hashtable using char ID as the key:
		start = (Node)nodes.get(node1);

		//Ending node does not yet exist case:
		if(!hasNode(node2)) {
			newNode(node2);
		}

		//The node "start" will now remember it has a new edge leading to node2:
		start.newEdge(node2, wgt);
	}

	/*
	 * Returns the weight of the edge from node1 to node2. If no such node
	 * exists, returns 0.
	 */
	public int edgeWeight(char node1, char node2) {
		Node start;
		if(hasNode(node1)) { //Start node exists?
			start = (Node)nodes.get(node1);
			return start.edgeWeight(node2);
		} else {
			return 0;
		}
	}

	/*
	 * Returns true if the route represented by the ArrayList exists, false
	 * otherwise (such as if a node or edge needed for the route does not exist,
	 * or does not exist in that order).
	 */
	public boolean hasRoute(ArrayList stops) {
		char cur, next;

		//i is index of start node's char, j is index of destination node's:
		for (int i = 0, j = 1; j < stops.size(); i++, j++) {
			cur  = (char)stops.get(i);
			next = (char)stops.get(j);

			/*
			 * If there isn't an edge between any of the consecutive nodes
			 * identified in the stops list, then the route does not exist:
			 */
			if (!hasEdge(cur, next)) {
				return false;
			}
		}
		return true;
	}

	/* 
	 * Returns zero if the route has only one stop, -1 if the route does not
	 * exist.
	 */
	public int routeTotalWgt(ArrayList stops) {
		int distance = 0;
		char cur, next;

		//i and j are indeces of the start and end nodes' chars respectively:
		for (int i = 0, j = 1; j < stops.size(); i++, j++) {
			cur  = (char)stops.get(i);
			next = (char)stops.get(j);

			if (hasEdge(cur, next)) {
				distance += edgeWeight(cur, next);
			} else {
				return -1;
			}
		}
		return distance;
	}

	/* 
	 * Returns the number of routes that start and end at the nodes specified by
	 * the passed chars with 'max' or fewer stops. If 'max' is less than 2 the
	 * function prints an error message and returns 0.
	 */
	public int routesMaxStops(char start, char end, int max) {

		if (max < 2) {
			System.out.println("Maximum stops must be at least 2.");
			return 0;
		}

		//This will hold all of the routes that match the criteria:
		ArrayList routes = new ArrayList();

		//Data needed for the search:
		Route next, cur = new Route();
		int len;
		Node here;

		//The search will use a stack:
		Stack<Route> stack = new Stack<Route>();
		
		/* 
		 * Priming the stack for the search, by pushing a new route starting
		 * at 'start':
		 */
		cur.addStop(start);
		stack.push(cur);

		while (!stack.empty()) {
			//Look at a route,
			cur = stack.pop();
			//Store its length for later,
			len = cur.len();
			//Make a copy of this route for later,
			next = cur.duplicate();
			if ((len > 1) && (len <= max + 1) && (cur.lastStop() == end)) {
				//Store the route if it's a valid length and ends at 'end'
				routes.add(cur);
			}
			//Fetch the Node of the last stop, so we can see what edges it has:
			here = (Node)nodes.get(cur.lastStop());
			//Add new valid routes to the stack:
			for (int i = 0; i < here.numEdges(); i++) {
				char edge = here.getEdge(i);
				next.addStop(edge);
				/* 
				 * Only routes under the max length need be added to the stack. 
				 * Without this conditional the search would continue
				 * indefinitely:
				 */
				if (next.len() <= max + 1) {
					stack.push(next);
				}
				//Reset 'next' to be ready to explore in a different direction:
				next = cur.duplicate();
			}
		}

		/*
		 * Return the size of the ArrayList, which is the number of routes that
		 * matched the criteria.
		 */
		return routes.size();
	}

	/* 
	 * Returns the number of routes that start and end at the nodes specified by
	 * the passed chars with 'exact' or fewer stops. If 'exact' is less than 2
	 * the function prints an error message and returns 0.
	 */
	public int routesExactStops(char start, char end, int exact) {

		if (exact < 2) {
			System.out.println("Exact number of stops must be at least 2.");
			return 0;
		}

		//This will hold all of the routes that match the criteria:
		ArrayList routes = new ArrayList();

		//Data needed for the search:
		Route next, cur = new Route();
		int len;
		Node here;

		//The search will use a stack:
		Stack<Route> stack = new Stack<Route>();
		
		/* 
		 * Priming the stack for the search, by pushing a new route starting
		 * at 'start':
		 */
		cur.addStop(start);
		stack.push(cur);

		while (!stack.empty()) {
			//Look at a route,
			cur  = stack.pop();
			//Make a copy of this route for later,
			next = cur.duplicate();
			if ((cur.len() == exact + 1) && (cur.lastStop() == end)) {
				//Store the route if it's the valid length and ends at 'end':
				routes.add(cur);
			}
			//Fetch the Node of the last stop, so we can see what edges it has:
			here = (Node)nodes.get(cur.lastStop());
			//Add new valid routes to the stack:
			for (int i = 0; i < here.numEdges(); i++) {
				char edge = here.getEdge(i);
				next.addStop(edge);
				/* 
				 * Only routes under the desired length need be pushed on the 
				 * stack. Without this conditional the search would continue
				 * indefinitely:
				 */
				if (next.len() <= exact + 1) {
					stack.push(next);
				}
				//Reset 'next' to be ready to explore in a different direction:
				next = cur.duplicate();
			}
		}

		/*
		 * Return the size of the ArrayList, which is the number of routes that
		 * matched the criteria.
		 */
		return routes.size();
	}

	/* 
	 * Returns the length of the shortest route between nodes 'start' and 'end',
	 * where the length is the sum of the weights of the edges connecting the
	 * nodes. If no such route exists, function will return 2147483647
	 * (MAX_VALUE).
	 */
	public int lenShortestRoute(char start, char end) {
		//Data needed for the search:
		Route next, cur = new Route();
		Node here;
		int wgt;
		/*
		 * This will hold the length of the shortest route found between 'start'
		 * and 'end'.
		 */
		int shortest = Integer.MAX_VALUE;

		//The search will use a FIFO queue in order to search breadth-first:
		LinkedList<Route> queue = new LinkedList<Route>();
		
		/* 
		 * Priming the queue for the search, by pushing a new route starting
		 * at 'start':
		 */
		cur.addStop(start);
		queue.add(cur);

		while (!queue.isEmpty()) {
			//Look at a route,
			cur  = queue.remove();
			//Store its total weight for later,
			wgt = cur.wgt();
			//Make a copy of this route for later,
			next = cur.duplicate();
			if ((cur.lastStop() == end) && (cur.len() > 1) 
													&& (wgt < shortest)) {	
				/*
				 * If new shortest route found that ends at 'end', update
				 * 'shortest'.
				 */
				shortest = wgt;
			}
			//Fetch the Node of the last stop, so we can see what edges it has:
			here = (Node)nodes.get(cur.lastStop());
			//Add new valid routes to the queue:
			for (int i = 0; i < here.numEdges(); i++) {
				char edge = here.getEdge(i);
				next.addStop(edge);
				/* 
				 * Only routes under the desired weight need be added to the 
				 * queue. Without this conditional the search would continue
				 * indefinitely:
				 */
				if (next.wgt() < shortest) {
					queue.add(next);
				}
				//Reset 'next' to be ready to explore in a different direction:
				next = cur.duplicate();
			}
		}
		return shortest;
	}

	/* 
	 * Returns the number of routes that start and end at the nodes specified by
	 * the passed chars with 'wgt' or less total weight.
	 */
	public int routesMaxWgt(char start, char end, int wgt) {

		if (wgt < 1) {
			System.out.println("Maximum weight must be at least 1.");
			return 0;
		}

		//This will hold all of the routes that match the criteria:
		ArrayList routes = new ArrayList();

		//Data needed for the search:
		Route next, cur = new Route();
		Node here;

		//The search will use a stack:
		Stack<Route> stack = new Stack<Route>();
		
		/* 
		 * Priming the stack for the search, by pushing a new route starting
		 * at 'start':
		 */
		cur.addStop(start);
		stack.push(cur);

		while (!stack.empty()) {
			//Look at a route,
			cur  = stack.pop();
			//Make a copy of this route for later,
			next = cur.duplicate();
			if ((cur.len() > 1) && (cur.lastStop() == end) 
				                              && (cur.wgt() < wgt)) {
				/*
				 * Store the route if it's a valid weight and ends at 'end':
				 */
				routes.add(cur);
			}
			//Fetch the Node of the last stop, so we can see what edges it has:
			here = (Node)nodes.get(cur.lastStop());
			//Add new valid routes to the stack:
			for (int i = 0; i < here.numEdges(); i++) {
				char edge = here.getEdge(i);
				next.addStop(edge);
				/* 
				 * Only routes under the maximum weight need be pushed on the 
				 * stack. Without this conditional the search would continue
				 * indefinitely:
				 */
				if (next.wgt() < wgt) {
					stack.push(next);
				}
				//Reset 'next' to be ready to explore in a different direction:
				next = cur.duplicate();
			}
		}

		/*
		 * Return the size of the ArrayList, which is the number of routes that
		 * matched the criteria.
		 */
		return routes.size();
	}

	private class Route {
		ArrayList route; //Chars specifying the route.
		int len; //Length the of the route.
		int wgt; //Total weight of the route.

		public Route() {
			route = new ArrayList();
			len = 0;
			wgt = 0;
		}

		//This private constructor is for the duplicate method below
		private Route(ArrayList old_route, int old_len, int old_wgt) {
			route = old_route;
			len = old_len;
			wgt = old_wgt;
		}

		public void addStop(char id) {
			//First node case:
			if (route.isEmpty()) {
				Node start = (Node)nodes.get(id);
				if (start == null) {
					System.out.println("Node does not exist");
					return;
				} else {
					route.add(id);
					len = 1;
				}
				return;
			}

			Node last = (Node)nodes.get(route.get(route.size() - 1));
			Node stop = (Node)nodes.get(id);
			if (stop == null) {
				/*
				 * If the hashtable of nodes returned null from that key, then
				 * the node does not exist:
				 */
				System.out.println("Node does not exist");
				return;
			}
			if (last.hasEdge(id)) {
				route.add(id);
				//Update private variables:
				len += 1;
				wgt += last.edgeWeight(id);
			} else {
				//Can't have a stop in a route if there's no edge to travel!
				System.out.println("No route from " + last.id()
																+ " to " + id);
				return;
			}
		}

		public int len() {
			return len;
		}

		public int wgt() {
			return wgt;
		}

		//Returns the ID of the last stop in the route.
		public char lastStop() {
			if (len == 0) {
				System.out.println("Empty route.");
				return '0';
			}
			return (char)route.get(len - 1);
		}

		//Returns a duplicate of the route.
		public Route duplicate() {
			//Our private constructor does the work:
			return new Route((ArrayList)route.clone(), len, wgt);
		}
	}

	private class Node {
		//Node ID:
		char id;
		/*
		 * In this hashtable, the destination IDs are the keys, and the weights
		 * of the edges are the values:
		 */
		Hashtable edges;

		public Node(char node_id) {
			id = node_id;
			edges = new Hashtable();
		}

		public char id() {
			return id;
		}

		public void newEdge(char end_id, int wgt) {
			edges.put(end_id, wgt);
		}

		public boolean hasEdge(char end_id) {
			return edges.containsKey(end_id);
		}

		public int edgeWeight(char end_id) {
			if (edges.containsKey(end_id)) {
				return (int)edges.get(end_id);
			} else {
				return 0;
			}
		}

		public int numEdges() {
			return edges.size();
		}

		public char getEdge(int i) {
			Set<Character> keySet = edges.keySet();
			Object [] keyArray = keySet.toArray();
			return (char)keyArray[i];
		}
	}
}

