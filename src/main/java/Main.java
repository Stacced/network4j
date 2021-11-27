import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("#### Bienvenue sur Network4J ####");
        Scanner in = new Scanner(System.in);

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

        System.out.println("#### Merci d'avoir utilisé Network4J ####");
        // Close database session and connection
        try {
            Neo4jDatabase.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
