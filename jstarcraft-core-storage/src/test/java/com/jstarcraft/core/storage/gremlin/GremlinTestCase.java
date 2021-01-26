package com.jstarcraft.core.storage.gremlin;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.idmanagement.IDManager;
import org.junit.Assert;
import org.junit.Test;

public class GremlinTestCase {

    @Test
    public void testJanusGraph() {
        JanusGraphFactory.Builder builder = JanusGraphFactory.build();
        builder.set("graph.set-vertex-id", true);
        builder.set("storage.backend", "inmemory");
        StandardJanusGraph graph = (StandardJanusGraph) builder.open();
        IDManager manager = graph.getIDManager();
        GraphTraversalSource traversal = graph.traversal();
        for (int index = 1; index <= 1000; index++) {
            long id = manager.toVertexId(index);
            manager.fromVertexId(id);
            traversal.addV("jstarcraft").property(T.id, id).next();
        }
        Assert.assertTrue(traversal.V().count().next() == 1000L);
        graph.close();
    }

    /**
     * <pre>
     * ./gremlin-server.bat conf/gremlin-server-modern.yaml
     * ./gremlin-server.sh conf/gremlin-server-modern.yaml
     * </pre>
     */
    @Test
    public void testTinkerGraph() {
        TinkerGraph graph = TinkerFactory.createModern();
        GraphTraversalSource traversal = graph.traversal();
        Assert.assertEquals(6, traversal.V().count().next().intValue());
        graph.close();

        Cluster cluster = Cluster.build("localhost").port(8182).create();
        Client client = cluster.connect();
        Assert.assertEquals(6, client.submit("g.V().count()").one().getInt());
        cluster.close();

    }

}
