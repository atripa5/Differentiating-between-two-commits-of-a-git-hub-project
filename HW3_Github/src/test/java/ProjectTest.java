import com.scitools.understand.Database;
import com.scitools.understand.Understand;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;


public class ProjectTest {

    String database_file = ".\\TestDatabase.udb";
    String root = ".\\";
    File filename = new File(root);

    Database db;
    SimpleDirectedGraph<String, DefaultEdge> g = new SimpleDirectedGraph<>(DefaultEdge.class);


    /***********************************Unit Tests****************************************************/

    @Test
    public void openDatabase() throws Exception
    {   //System.out.println("----->" + filename.getAbsolutePath());
        System.out.println("\nTesting openDatabase()...");
        db = DataFile.openDatabase(database_file);

        assertEquals(filename.getAbsolutePath().concat(database_file.substring(3)) ,db.name().toString());
    }

    @Test
    public void getFileName() throws Exception
    {
        System.out.println("\nTesting getFileName()...");
        assertEquals("TestDatabase.udb",DataFile.getFileName(database_file));
    }


    @Test
    public void createDependencyGraph() throws Exception
    {
        db = DataFile.openDatabase(database_file);
        System.out.println("\nTesting createDependencyGraph()...");
        g = Graph.createDependencyGraph(db);

        assertNotEquals(true,g.vertexSet().isEmpty());

    }

    @Test //involves multiple functions
    public void getEdgeDifferences() throws Exception
    {
        db = DataFile.openDatabase(database_file);
        System.out.println("\nTesting getEdgeDifferences()...");
        g = Graph.createDependencyGraph(db);

        int num = Graph.getEdgeDifferences(g,g,true);

        assertEquals(0,num);

    }
}