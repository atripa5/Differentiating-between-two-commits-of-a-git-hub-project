/**
 * Created by Mike on 10/8/2016.
 */

import com.scitools.understand.Database;
import com.scitools.understand.Entity;
import com.scitools.understand.Reference;
import org.jgrapht.GraphMapping;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.alg.isomorphism.VF2SubgraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;


public class Graph {


    //
    // method that takes a database and returns its dependency graph
    //
    public static SimpleDirectedGraph<String, DefaultEdge> createDependencyGraph(Database db) {

        SimpleDirectedGraph<String, DefaultEdge> depends = new SimpleDirectedGraph<>(DefaultEdge.class);

        // Get a list of all functions, methods, classes, and interfaces
        Entity[] funcs = db.ents("function ~unknown ~unresolved,method ~unknown ~unresolved,class,interface");

        for (Entity e : funcs) {
            //System.out.print(e.name()+"(");
            depends.addVertex(e.name());
            //System.out.println("Added '" + e.kind() + "' vertex with name: " + e.name());

            //Find all the parameters for the given method/function and build them into a string
            StringBuilder paramList = new StringBuilder();
            Reference[] parameterRefs = e.refs("define", "parameter", true);

            for (Reference pRef : parameterRefs) {
                Entity pEnt = pRef.ent();
                paramList.append(pEnt.type() + " " + pEnt.name());
                paramList.append(",");
                depends.addVertex(pRef.ent().name());
                //System.out.println("Added 'parameter' vertex with name: " + pRef.ent().name());
                depends.addEdge(e.name(), pRef.ent().name());
                //System.out.println("Added edge from: " + e.name() + " to " + pRef.ent().name());
            }

            //Remove trailing comma from parameter list
            if (paramList.length() > 0)
                paramList.setLength(paramList.length() - 1);

            //System.out.print(paramList+")\n");
        }

        return depends;

    } // end of createDependencyGraph()


    //
    // takes two graphs and finds the differences between them
    //
    public static void checkIsomorphism(SimpleDirectedGraph<String, DefaultEdge> g1,
                                      SimpleDirectedGraph<String, DefaultEdge> g2)
    {
        VF2SubgraphIsomorphismInspector<String,DefaultEdge> dependency_iso =
                new VF2SubgraphIsomorphismInspector<String, DefaultEdge>(g1,g2);


        try
        {
            assertEquals(true, dependency_iso.isomorphismExists());
            System.out.println("\nIsomorphism exists.\n\n");
            Iterator<GraphMapping<String,DefaultEdge>> iter = dependency_iso.getMappings();

        }
        catch(AssertionError e)
        {
            System.out.println("No isomorphism exists between these graphs.\n");

            // since no isomorphism, get differences
            int num = getEdgeDifferences(g1,g2,false);
        }



    } // end of getDifferences


    //
    // method that takes 2 graphs and finds the differences between them
    //
    public static int getEdgeDifferences(SimpleDirectedGraph<String, DefaultEdge> g1,
                                             SimpleDirectedGraph<String, DefaultEdge> g2, boolean isTest)
    {
        boolean isFound;
        int num_diffs1 = 0, num_diffs2 = 0;

        if(!isTest)
        {
            //System.out.println("\nDifferences between versions 1 and 2 : \n");
            System.out.println("What's new in current commit: \n");
        }

        // check for what's only in graph1 (version1)
        for (DefaultEdge e: g1.edgeSet())
        {
            isFound = false;
            for (DefaultEdge e2: g2.edgeSet())
            {
                if (e2.equals(e))
                {
                    //System.out.println("edges match...\n");
                    isFound = true;
                }
            }

            if(!isFound)
            {
                num_diffs1++;
                //System.out.println("edges don't match...\n");
                System.out.println(g1.getEdgeSource(e) + " -----> " + g1.getEdgeTarget(e));
            }

        }
        /*
        if(!isTest)
        {
            System.out.println("\n\nEdges found only in version 2: \n");
        }

        // check for what's only in graph2 (version2)
        for (DefaultEdge e: g2.edgeSet())
        {
            isFound = false;
            for (DefaultEdge e2 : g1.edgeSet())
            {
                if (e2.equals(e))
                {
                    //.out.println("edges match...\n");
                    isFound = true;
                    break;
                }
            }

            if (!isFound)
            {
                num_diffs2++;
                //System.out.println("edges don't match...\n");
                System.out.println(g2.getEdgeSource(e) + " -----> " + g2.getEdgeTarget(e));
            }
        }
          */
        return num_diffs1; //+ num_diffs2

    } // end of getVersionDifferences


}// end of Graph class
