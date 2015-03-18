import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.*;

public class Tests {
	static MyGraph graph;

	public Tests() {
		graph = new MyGraph();
	}

	public static void main(String []args) {
		Tests tests = new Tests();
		tests.init(System.in);

		ArrayList inputArray = new ArrayList(3);
		
		inputArray.add('B');
		inputArray.add('E');
		inputArray.add('A');
		
		if (graph.hasRoute(inputArray)) {
			System.out.println(graph.routeTotalWgt(inputArray));
		} else {
			System.out.println("NO SUCH ROUTE");
		}
	}

	private void init(InputStream in) {

		Scanner scanner = new Scanner(in);
		Pattern routes = Pattern.compile("(\\w\\w\\d+).*");
		Pattern weight = Pattern.compile("(\\d+)");
		String cur;
		Matcher m;
		char node1, node2;
		int wgt;

		while (scanner.hasNext(routes)) {
			//Get a route specifier:
			cur = scanner.next(routes);
			//Store the node identifying characters:
			node1 = cur.charAt(0);
			node2 = cur.charAt(1);
			//Use regex to extract the weight:
			m = weight.matcher(cur);
			m.find();
			wgt = Integer.parseInt(m.group());
			//Make new edge:
			graph.newEdge(node1, node2, wgt);
		}
	}
}

