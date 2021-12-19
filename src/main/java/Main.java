import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("#### Bienvenue sur Network4J ####");
        Scanner in = new Scanner(System.in);
        String userCommand;

        System.out.print("> Veuillez entrer l'URI de connexion à votre base de données Neo4J: ");
        String neoUri = in.nextLine();
        System.out.print("> Veuillez entrer votre nom d'utilisateur: ");
        String neoUser = in.nextLine();
        System.out.print("> Veuillez entre votre mot de passe: ");
        String neoPassword = in.nextLine();

        try {
            Neo4jDatabase.connect(neoUri, neoUser, neoPassword);
            System.out.println("[INFO] Connecté à la base de données");
        } catch (Exception e) {
            System.out.printf("[ERR] Erreur lors de la connexion à la base de données: %s", e.getMessage());
            return;
        }

        do {
            System.out.println();
            System.out.println("> Veuillez entrer la commande à executer");
            System.out.println("  > RTR : Récupérer tous les routeurs sortant d'un datacenter (relations, interfaces et zones réseaux aussi)");
            System.out.println("  > AR : Récupérer tous les racks d'un datacenter dont les switches liés, avec les interfaces connectées et la zone IP associée");
            System.out.println("  > I-RTR : Insérer un routeur de sortie d'un datacenter");
            System.out.println("  > CR : Calculer la route la plus efficace pour aller d'un rack à un routeur de sortie");
            System.out.println("  > EXIT : Quitter et se déconnecter");

            userCommand = in.nextLine();

            switch (userCommand.toUpperCase(Locale.ROOT)) {
                case "RTR":
                    Neo4jDatabase.getRoutersOut();
                    break;
                case "AR":
                    Neo4jDatabase.getAllRacks();
                    break;
                case "I-RTR":
                    System.out.print("> Veuillez indiquer l'IP de sortie à laquelle associer le routeur de sortie: ");
                    String ip = in.nextLine();

                    System.out.print("> Veuillez indiquer le nom du routeur de sortie: ");
                    String routerName = in.nextLine();

                    Neo4jDatabase.addEgressRouter(ip, routerName);
                    break;
                case "CR":
                    System.out.print("> Veuillez indiquer le numéro du rack de départ: ");
                    String rack = in.nextLine();

                    System.out.print("> Veuillez indiquer le numéro de la zone du rack: ");
                    String zone = in.nextLine();

                    if(!Neo4jDatabase.calcShortestPath(rack, zone)) {
                        System.out.println("> /!\\ Veuillez saisir des valeurs numériques /!\\");
                    }
                    break;
            }

        } while(!userCommand.equals("EXIT"));


        System.out.println("#### Merci d'avoir utilisé Network4J ####");
        // Close database session and connection
        try {
            Neo4jDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
