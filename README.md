# AgarioSae 🧩

Bienvenue dans **AgarioSae**, notre adaptation revisitée du célèbre jeu Agar.io ! Plongez dans une expérience en temps réel où vous pouvez jouer en mode local avec des bots ou en réseau pour discuter et affronter d’autres joueurs (même si certaines fonctionnalités restent à finaliser).

---

## Sommaire 📑

- [Description](#description)
- [Fonctionnalités du jeu](#fonctionnalités-du-jeu)
- [UML avec MLD](#uml-avec-mld)
- [Patron de conception utilisé](#patron-de-conception-utilisé)
- [Installation](#installation)
- [Technologies utilisées](#technologies-utilisées)
- [Structure du projet](#structure-du-projet)
- [Améliorations futures](#améliorations-futures)
- [Auteur](#auteur)

---

## Description 🎮

**AgarioSae** est une version revisitée du jeu Agar.io. Le joueur commence par renseigner un pseudo (obligatoire) et choisit ensuite entre deux modes de jeu :

- **Mode local** : Le jeu se lance en local avec des bots programmés selon différentes stratégies (déplacement aléatoire, collecte de pastilles uniquement, ou chasse aux joueurs).  
- **Mode réseau** : Après avoir renseigné l’adresse IP et le port du serveur, le jeu démarre avec la possibilité de discuter via un chat intégré.  
  > **Note** : Actuellement, en mode réseau, le joueur joue en mode local malgré la connexion réseau et ne voit pas les autres joueurs.

---

## Fonctionnalités du jeu 🚀

- **Saisie du pseudo obligatoire** pour personnaliser l’expérience.
- **Modes de jeu variés** :
  - Mode local avec IA (bots suivant diverses stratégies).
  - Mode réseau permettant la connexion via IP/port et l’utilisation d’un chat.
- **Types de pastilles** :
  - **Classiques** : Permettent au joueur de grossir.
  - **Spéciales** : Offrent des bonus/malus (invisibilité, accélération ou ralentissement).
- **Interface dynamique** :
  - Une **minimap** en bas à droite affiche la position des joueurs, bots et pastilles.
  - Un **classement** en haut de l’écran intègre à la fois les joueurs et les bots.
- **Retour à l’accueil** : En cas de défaite, le joueur est redirigé avec son pseudo prérempli.
- **Communication** : Un chat intégré en mode réseau permet d’échanger avec les autres joueurs.

---

## UML 📊

Le diagramme **de classes** est disponible dans le dossier **`uml`**.  
Ce dossier comprend :
- Un diagramme de classes détaillé décrivant l’architecture globale du projet.

---

## Patron de conception utilisé 🛠️

Pour garantir une architecture robuste et évolutive, nous utilisons plusieurs patrons de conception :
- **Patron Fabrique (Factory)** : Pour la création des pastilles et des ennemis.
- **Patron Composite** : Pour la gestion des MapNode et des joueurs (notamment lors de la division).
- **Patron Stratégie** : Pour implémenter les différentes intelligences artificielles (IA) des bots.

---

## Installation 💻

Pour installer et lancer **AgarioSae** :

1. **Cloner le dépôt :**
   ```bash
   git clone https://github.com/z0ralex/AgarioSae.git
   ```

2. **Compiler et lancer le projet :**  
   Assurez-vous d’avoir [JavaFX](https://openjfx.io/) installé et une configuration correcte pour les sockets.  
   Par exemple :
   ```bash
   ./gradlew run   # ou la commande adaptée à votre environnement
   ```

## Technologies utilisées 🔧

Le projet s’appuie sur des technologies modernes pour offrir une expérience fluide et interactive :
- **JavaFX** : Pour l’interface graphique et la gestion des nodes.
- **Sockets** : Pour la communication réseau en temps réel.
- **Java** : Le langage de programmation principal.

---

## Structure du projet 📂

Voici une vue d’ensemble détaillée de l’arborescence du projet, incluant les principaux packages :

```bash
AgarioSae/
├── AgarIOClient/
│   └── src/main/                          
│       ├── java/           
│       │   └── iut/gon/agarioclient/
│       │   │   ├── controller/           # Controle des vues
│       │   │   ├── model/                # Implémentation du modèle de jeu
│       │   │   │   ├── entity/           # Implémentation des entitées du jeu
│       │   │   │   │   ├── ia/           # Implémentation des bots
│       │   │   │   │   ├── moveable/     # Implémentation du Joueur et des Ennemis
│       │   │   │   │   └── pellet/       # Implémentation des pastilles 
│       │   │   │   └── map/              # Implémentation de la map du jeu
│       │   │   └── server/               # Implémentation du serveur
│       └── resources/                    # Ressources graphiques, fichiers fxml, etc.
├── uml/                                  # Diagramme de Classes
│   ├── DiagrammeClasses.png
│   └── MLD.pdf
└── README.md                             # Ce fichier 😊
```

## Améliorations futures ✨

Bien que le jeu soit fonctionnel, certaines améliorations sont à prévoir :

- **Affichage des autres joueurs en mode réseau** :  
  - En version locale, seul le joueur est visible.  
  - En version réseau, même si la connexion et le chat fonctionnent, l’affichage des autres joueurs n’est pas encore implémenté.
- **Classement** :  
  - Le classement inclut déjà les joueurs et les bots, mais son affichage pourrait être optimisé pour une meilleure lisibilité.
- **Fonctionnalités additionnelles** :  
  - Possibilité de diviser la cellule du joueur (fonctionnalité envisagée pour les futures versions).

Ces améliorations feront l’objet de futures mises à jour pour enrichir l’expérience de jeu.

---

## Auteur 👤

Ce projet a été développé dans le cadre de la **SAÉ 4 : Développement d'une application complexe** au BUT Informatique de l'Université Grand Ouest Normandie.🏫

**Auteurs :**
- Bates Lukes
- Berranger Léo
- Duroy Cyprien
- Ferault Noa
- Le Roy Alexandre
- Ragot Alexis

**Encadré par :**
- Dorbec Paul
- Jacquier Yohann
- JeanPierre Laurent

Un grand merci à tous les encadrants pour leur soutien et leurs conseils ! 🙏
