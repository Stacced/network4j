import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

public class Neo4jDatabase {
    private static Driver conn;

    public static void connect(String uri, String user, String password) {
        conn = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    public static Result query(String cypher) {
        try (Session session = conn.session()) {
            // TODO: use transactions ?
            return session.run(cypher);
        } catch (Neo4jException ex) {
            System.out.println("[ERR] " + ex);
        }
        return null;
    }

    public static void close() {
        conn.close();
    }
}
