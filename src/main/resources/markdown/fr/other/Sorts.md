## 🔥 Magie

### 📖 Lexique

- ⚔️ - Attaque
- ❤️ - Gain de Vie
- 😵 - Chance Étourdissement
- 🛡 - Défense
- 🧿 - Défense de l'ennemi
- `rand(x, y)` - Valeur aléatoire entre x et y

### ❗ Particularités

- Les sorts venant du livre **La Bible de l'Archimage** et **Le Codex des Abysses** ne peuvent être utilisés qu'une
  seule fois par combat.
- Les sorts venant du livre **La Bible de l'Archimage** ont une chance de 20% d'échouer.

| Nom                           | Description                                                                                                                      | Livre                                      | Type de Magie | Coût | Conséquences                                           |
|-------------------------------|----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------|---------------|------|--------------------------------------------------------|  
| Les Chaînes Noires            | Bloquer votre adversaire avec des chaînes pendant 1 tour                                                                         | Livre des Sombres Secrets (Grade I)        | Sombre        | 15   | ⚔️rand(0, 10) 😵20%                                    |
| Guérison                      | Le sort originel de soin, essentiel pour tout mage qui se respecte                                                               | Livre des Sombres Secrets (Grade I)        | Sombre        | 35   | ❤️rand(10, 40)                                         |
| Main de Force                 | Augmenter la force dans votre bras quand vous assénez un puissant coup                                                           | Grimoire de l'Initié (Grade II)            | Céleste       | 23   | ⚔️rand(10, 30)                                         |
| Dissimulation                 | Être invisible aux yeux de votre adversaire                                                                                      | Grimoire de l'Initié (Grade II)            | Céleste       | 10   | 😵20%                                                  |
| Boule de Feu                  | Lancer une boule de feu à votre adversaire                                                                                       | Codex des Arcanes (Grade III)              | Élémentaire   | 40   | ⚔️rand(25, 40) 😵20%                                   |
| Barrière Runique              | Créer une barrière de protection pour bloquer l'attaque de votre adversaire                                                      | Codex des Arcanes (Grade III)              | Élémentaire   | 20   | 🛡+30%                                                 |
| Le Draînage d'Énergie         | Draîner l'énergie de votre adversaire pour le rendre vulnérable à vos coups                                                      | Codex des Arcanes (Grade III)              | Arcanes       | 65   | ⚔️rand(40, 50) 😵30%                                   |
| La Fureur du Givre            | Projeter du givre pouvant geler un corps humain                                                                                  | Tome de l'Élémentaliste (Grade IV)         | Élémentaire   | 50   | ⚔️rand(40, 50) 🛡+20%                                  |
| Eau de Vie                    | Faire jaillir une eau pure pour soigner vos blessures                                                                            | Tome de l'Élémentaliste (Grade IV)         | Élémentaire   | 30   | ❤️rand(50, 80)                                         |
| Les Crocs de la Terre         | Faire sortir des pics acérés de la terre                                                                                         | Tome de l'Élémentaliste (Grade IV)         | Élémentaire   | 80   | ⚔️rand(45, 80)                                         |
| Armure Animée                 | Créer une armure animée allant attaquer votre adversaire à votre place                                                           | Grand Grimoire des Enchanteurs (Grade V)   | Enchantement  | 130  | ⚔️rand(75, 115) 🧿20% -rand(10, 20)%                   |
| Illusion Brillante            | Créer une lumière aveuglante votre ennemi pour l'attaquer                                                                        | Grand Grimoire des Enchanteurs (Grade V)   | Enchantement  | 130  | ⚔️rand(70, 120) 😵20%                                  |
| Charme Protecteur             | Appâter votre ennemi avec votre incroyable charisme pour ne pas se faire attaquer                                                | Grand Grimoire des Enchanteurs (Grade V)   | Enchantement  | 50   | ⚔️rand(20, 30) 🛡+40%                                  |
| La Nuit Éternelle             | Plonger votre ennemi dans l'obscurité, le rendant vulnérable à vos attaques                                                      | Livre de la Lune et des Étoiles (Grade VI) | Céleste       | 140  | ⚔️rand(70, 110) 😵25% 🧿40% -rand(10, 30)%             |
| Au Clair de Lune              | Attaquer votre ennemi avec un rayon de clair de Lune                                                                             | Livre de la Lune et des Étoiles (Grade VI) | Céleste       | 120  | ⚔️rand(90, 120)                                        |
| Lumière Lunaire               | Une lumière provenant de la lune excellente contre les blessures                                                                 | Livre de la Lune et des Étoiles (Grade VI) | Céleste       | 150  | ❤️rand(80, 130) 🛡+rand(30, 40)%                       |
| Le Miroir de Duplication      | Créer des copies illusoires du lanceur pour le confondre avec les ennemis.                                                       | Tome des Mille Formes (Grade VII)          | Illusion      | 175  | ⚔️rand(120, 160)                                       |
| Polymorphios                  | Transforme un ennemi en une créature inoffensive pour une courte durée                                                           | Tome des Mille Formes (Grade VII)          | Illusion      | 145  | ⚔️rand(100, 120) 😵20%                                 |
| Le Pont des Âmes              | Ouvre un portail vers le royaume des morts, permettant l'appel de puissants esprits.                                             | Grimoire des Âmes Damnées (Grade VIII)     | Nécromancie   | 200  | ⚔️rand(150, 210) 😵30%                                 |
| Draînage de Vie               | Draîne la vie de votre adversaire.                                                                                               | Grimoire des Âmes Damnées (Grade VIII)     | Nécromancie   | 230  | ⚔️rand(200, 240)                                       |
| Insufflation d'Âme            | Insuffle une âme dans votre corps afin de récupérer son énergie vitale et se soigner plus rapidement                             | Grimoire des Âmes Damnées (Grade VIII)     | Nécromancie   | 190  | ❤️rand(130, 230)                                       |
| Les Tentacules de l'Abîme     | Fait surgir des tentacules obscurs de l'abîme pour enserrer et étouffer l'ennemi.                                                | Codex des Abysses (Grade IX)               | Ténèbres      | 290  | ⚔️rand(230, 275) 😵rand(20, 40)%                       |
| Le Banni                      | Fait apparaître d'un portail temporel Le Banni, une créature démoniaque                                                          | Codex des Abysses (Grade IX)               | Ténèbres      | 310  | ⚔️rand(240, 350) 😵rand(20, 40)%                       |
| Le Calice des Abysses         | Un Calice réputé comme maudit, offrant pourtant une regénération execptionnelle. Peut parfois diminuer la défense ou l'augmenter | Codex des Abysses (Grade IX)               | Ténèbres      | 350  | ❤️rand(225, 430) 🛡30%-rand(10, 30)% OU +rand(20, 30)% |
| Déchaînement des Éléments     | Déchaîne les éléments sur son ennemi (tsunami, éruption volcanique...)                                                           | La Bible de l'Archimage (Grade X)          | Élémentaire   | 430  | ⚔️rand(330, 410) 😵rand(20, 40)%                       |
| La Création du Monde Primitif | Créer un monde hors du temps où l'ennemi se retrouve emprisonné et assailli de coups                                             | La Bible de l'Archimage (Grade X)          | Céleste       | 475  | ⚔️rand(430, 450) 😵rand(20, 40)%                       |
| La Pluie d'Étoiles            | Dirige toute étoile et comète sur son ennemi, provoquant des dégâts majeurs                                                      | La Bible de l'Archimage (Grade X)          | Céleste       | 500  | ⚔️rand(450, 480) 😵rand(20, 40)%                       |
| Le Jugement Divin             | Appelle la puissance divine suprême des Dieux pour punir son ennemi                                                              | La Bible de l'Archimage (Grade X)          | Céleste       | 610  | ⚔️rand(500, 600) 😵50% 🛡+50%                          |