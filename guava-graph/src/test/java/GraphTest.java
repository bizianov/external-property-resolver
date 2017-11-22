import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import guava.model.Edge;
import guava.model.GraphModel;
import guava.model.Node;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static guava.util.GraphConverter.graphToModel;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by viacheslav on 6/23/17.
 */
public class GraphTest {
    public static final String JSON_PATH = "/home/viacheslav/dev/java/agt/graph/src/test/resources/graph.json";
    public static final String ID_FIELD = "id";
    public static final String VALUE_FIELD = "value";
    public static final Integer EDGE_NODES_NUMBER = 2;

    private MutableValueGraph<Node<Integer>, Double> weightedGraph;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void buildGraph() {
        weightedGraph = ValueGraphBuilder.directed().build();

        Node<Integer> node1 = new Node<>(1, 1);
        Node<Integer> node2 = new Node<>(2, 2);
        Node<Integer> node3 = new Node<>(3, 3);
        Node<Integer> node4 = new Node<>(4, 4);

        weightedGraph.addNode(node1);
        weightedGraph.addNode(node2);
        weightedGraph.addNode(node3);
        weightedGraph.addNode(node4);

        weightedGraph.putEdgeValue(node1, node2, 1.2);
        weightedGraph.putEdgeValue(node1, node3, 1.3);
        weightedGraph.putEdgeValue(node2, node4, 2.4);
        weightedGraph.putEdgeValue(node3, node4, 3.4);
    }

    @Test
    public void toJSON() throws IOException {
        GraphModel<Integer, Double> graphModel = graphToModel(weightedGraph);
        mapper.writeValue(Files.newBufferedWriter(Paths.get(JSON_PATH)), graphModel);

        JsonNode rootNode = mapper.readTree(Paths.get(JSON_PATH).toFile());
        JsonNode nodes = rootNode.findValue("nodes");
        JsonNode edges = rootNode.findValue("edges");

        assertTrue(nodes.isArray());
        List<String> ids = nodes.findValuesAsText(ID_FIELD);
        List<String> expected = Arrays.asList("1", "2", "3", "4");
        assertThat(ids, equalTo(expected));

        assertTrue(edges.isArray());
        List<String> values = edges.findValuesAsText(VALUE_FIELD);
        List<String> nodeValues = Arrays.asList("1", "2", "3", "4");
        List<String> edgeValues = Arrays.asList("1.2", "2.4", "3.4", "1.3");
        assertTrue(values.containsAll(nodeValues));
        assertTrue(values.containsAll(edgeValues));
        assertTrue(values.size() == nodeValues.size() * EDGE_NODES_NUMBER + edgeValues.size());
    }

    @Test
    public void toGraph() throws IOException {
        GraphModel<Integer, Double> graphModel = mapper.readValue(Paths.get(JSON_PATH).toFile(), GraphModel.class);
        Set<Edge<Integer, Double>> edges = graphModel.getEdges();
        MutableValueGraph<Node<Integer>, Double> weightedGraphFromJson = ValueGraphBuilder.directed().build();
        edges.forEach(edge ->
                weightedGraphFromJson.putEdgeValue(
                        edge.getSource(),
                        edge.getTarget(),
                        edge.getValue()));

        Set<EndpointPair<Node<Integer>>> edgesFromJson = weightedGraphFromJson.edges();
        Set<EndpointPair<Node<Integer>>> edgesFromGraph = weightedGraph.edges();
        assertThat(edgesFromJson, equalTo(edgesFromGraph));

        Set<Node<Integer>> nodesFromJson = weightedGraphFromJson.nodes();
        Set<Node<Integer>> nodesFromGraph = weightedGraph.nodes();
        assertThat(nodesFromJson, equalTo(nodesFromGraph));
    }
}
