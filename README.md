# Network4J

Network4J est un programme permettant de gérer un schéma d'infrastructure réseau sous forme de graphe (noeuds & relations), en utilisant le DBMS [neo4j](https://neo4j.com/). Notre application utilise Neo4j Sandbox pour héberger de manière éphémère une instance de serveur avec le jeu de données approprié (environ 60k noeuds et 160k relations).

## Installation

### Prérequis

* IDE Java
* Maven
* Serveur neo4j

## Utilisation

* RTR
    * Récupérer tous les routeurs sortant d'un datacenter (relations, interfaces et zones réseaux aussi)
* AR
    * Récupérer tous les racks d'un datacenter dont les switches liés, avec les interfaces connectées et la zone IP associée
* I-RTR
    * Insérer un routeur de sortie d'un datacenter
* CR
    * Calculer la route la plus efficace pour aller d'un rack à un routeur de sortie

## License
[MIT](https://choosealicense.com/licenses/mit/)
