import com.sun.source.tree.LiteralTree;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.HashMap;
import java.util.List;

public class Neo4jDatabase {
    private static Driver conn;

    public static void connect(String uri, String user, String password) {
        conn = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


    public static void addEgressRouter(String outgoingIp, String egressRouterName) {
        if (outgoingIp.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            String cypherQuery = String.format("MATCH (if:Interface {ip:'%s'})\n" +
                    "CREATE (if)<-[:ROUTES]-(egr:Router:Egress {name:'%s'})<-[:CONTAINS]-(dc:DataCenter{name:'DC1'});", outgoingIp, egressRouterName);

            // Dans le cas où l'interface de sortie n'est pas trouvée, Neo4j va 'gracefully fail', c'est-à-dire qu'aucune exception
            // ne sera relevée. On peut donc parfaitement ignorer le résultat final.
            query(cypherQuery);

            System.out.println("Routeur de sortie créé !");
        } else {
            System.out.println("L'IP entrée n'est pas une IPv4 valide, veuillez réessayer.");
        }
    }

    public static boolean calcShortestPath(String rack, String zone) {

        String cypherQuery = "MATCH path = allShortestPaths( (rack:Rack{zone:";

        try {
            Integer.parseInt(rack);
            Integer.parseInt(zone);

            cypherQuery +=  zone + ",rack: " + rack + "})-[:HOLDS|ROUTES|CONNECTS*]-(router:Router:Egress) )\n" +
                    "MATCH (router)-[:ROUTES]->(i:Interface)\n" +
                    "RETURN length(path) as hops, count(*) as count, rack.name as rack, router.name as rtr, i.ip as ip_rtr;";

        } catch (NumberFormatException e) {
            return false;
        }

        List<Record> result = query(cypherQuery);

        System.out.println();


        for (Record record : result) {
            System.out.println("Chemin le plus cours pour le rack " + record.get("rack").asString() + "\n" +
                    "Routeur destination : " + record.get("rtr").asString() + " (IP: " + record.get("ip_rtr").asString() + ")\n" +
                    "Longueur du chemin : " + record.get("hops").asInt());
        }

        System.out.println();
        return true;
    }


    public static void getAllRacks() {
        String cypherQuery = "MATCH (dc:DataCenter {name:\"DC1\"})-[:CONTAINS]->(rack:Rack)-[:HOLDS]->(s:Switch)-[:ROUTES]->(si:Interface)<-[:ROUTES]-(nr:Network:Zone)\n" +
                "RETURN rack.rack as no_rack, rack.name as rack, s.ip as ip_sw, si.ip as ip_con_int, nr.ip as network;";

        List<Record> result = query(cypherQuery);

        System.out.println();


        for (Record record : result) {
            System.out.println("Rack: " + record.get("rack").asString() + " :\n" +
                    "   Sous-réseaux du switch: " + record.get("ip_sw").asString() + ".0\n" +
                    "   Interface connectée au switch: " + record.get("ip_con_int").asString() + " (Zone: " + record.get("network").asString() + ".0.0" + ")");
            System.out.println();
        }

        System.out.println();
    }


    public static void getRoutersOut() {


            String cypherQuery = "MATCH (dc:DataCenter {name: \"DC1\"})-[:CONTAINS]->(r:Router:Egress)-[:ROUTES]->(i:Interface)-[:CONNECTS]->(nw:Network)<-[:CONNECTS]-(i2:Interface)<-[:ROUTES]-(rin:Router)" +
                    "return i.ip as ip_rtr_out, nw.ip as network, r.name as rtr_out, rin.name as rtr_inside, i2.ip as ip_rtr_inside;";

            List<Record> result = query(cypherQuery);

            System.out.println();

            for (Record record : result) {
                System.out.println("Nom du routeur : " + record.get("rtr_inside").asString() +
                        " (IP: " + record.get("ip_rtr_inside") .asString() +
                        " Zone: " + record.get("network").asString() + ".0.0" + ")" +
                        " via le routeur " + record.get("rtr_out").asString() +
                        " (IP: " + record.get("ip_rtr_out").asString() + ")");
            }

            System.out.println();
    }


    private static List<Record> query(String cypher) {
        try (Session session = conn.session()) {

            Result r = session.run(cypher);
            return r.list();

        } catch (Neo4jException ex) {
            System.out.println("[ERR] " + ex);
        }
        return null;
    }

    public static void close() {
        conn.close();
    }
}
