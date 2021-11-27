import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Neo4jDatabase {
    private static Driver conn;
    private static Session session;

    public static void connect(String uri, String user, String password) {
        conn = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        session = conn.session();
    }

    public static void query(String cypher) {
        return;
    }

    public static void close() {
        session.close();
        conn.close();
    }
}
