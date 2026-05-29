# Core Continue Rules

Projet multi-parties : frontend Angular/TypeScript + backends Java/Spring Boot possibles.

Répondre en français.

# Tool call format critical rule

Never write tool calls as text.

Forbidden output examples:
* `<function=ls>`
* `<parameter=dirPath>`
* `</tool_call>`
*  JSON tool calls
* XML tool calls

If you need to use a tool, call the real Continue tool silently.
If you cannot call the tool silently, stop and say: "Je ne peux pas appeler l'outil correctement."

## Règles générales

* Ne rien inventer : fichiers, dossiers, dépendances, architecture ou outils.
* Explorer avant de conclure qu'un fichier est absent.
* Ne pas lire plus de 5 fichiers par tâche.
* Ne jamais relire deux fois le même fichier dans la même tâche.
* Si un fichier échoue, noter l'erreur et passer au suivant.
* Ne pas modifier sans validation explicite.
* Ne pas lancer de terminal sans validation explicite.
* Ne jamais afficher d'appel d'outil dans la réponse : ni JSON, ni XML, ni balise `<function=...>`.
* Utiliser les outils Continue réellement, sans les écrire dans le chat.

## Outils autorisés

Lecture / recherche :

* `read_file` avec `filepath`
* `read_currently_open_file`
* `ls` avec `dirPath`
* `file_glob_search` avec `pattern`
* `grep_search` avec `query`
* `view_diff`

Modification :

* `edit_existing_file` avec `filepath` et `changes`
* `single_find_and_replace` avec `filepath`, `old_string`, `new_string`
* `create_new_file` avec `filepath` et `contents`

Terminal :

* `run_terminal_command` uniquement si l'utilisateur l'autorise explicitement.

## Outils interdits

Ne jamais utiliser :

* `cat`
* `write_file`
* `execute_command`
* `bash`
* `shell`
* `terminal`

## Lecture de fichiers

* Avant de modifier un fichier existant, toujours le lire avec `read_file`.
* `read_file` doit toujours recevoir `filepath`.
* `filepath` doit être un chemin valide, non vide.
* Si `read_file` échoue, vérifier le chemin avec `file_glob_search`, puis retenter une seule fois.
* Si la deuxième lecture échoue, arrêter et demander à l'utilisateur d'ouvrir le fichier manuellement.

## Modification de fichiers existants

* Pour modifier un fichier existant, utiliser `edit_existing_file`.
* `edit_existing_file` doit toujours recevoir :

  * `filepath`
  * `changes`
* Ne jamais appeler `edit_existing_file` avec `filepath` vide.
* Ne jamais appeler `edit_existing_file` avec `changes` vide.
* `changes` doit contenir uniquement les changements de code nécessaires.
* Ne pas mettre d'explications dans `changes`.
* Ne pas entourer `changes` avec un bloc Markdown.
* Pour un petit remplacement exact, préférer `single_find_and_replace`.
* Après modification, utiliser `view_diff` si possible.

## Création de fichiers

* Pour créer un fichier, utiliser `create_new_file`.
* `create_new_file` doit toujours recevoir :

  * `filepath`
  * `contents`
* Ne jamais appeler `create_new_file` avec `contents` vide.
* Ne jamais créer de composant, service, route, DTO, controller ou test sans validation explicite.
* Si le contenu du fichier n'est pas prêt, arrêter et demander confirmation.

## Limites de tâche

* Travailler sur une seule zone à la fois.
* Pour une nouvelle fonctionnalité, proposer d'abord un plan court.
* Après validation, modifier uniquement les fichiers validés.
* Ne pas repartir en exploration complète après validation.
* Si plus de 5 fichiers sont nécessaires, arrêter et demander confirmation.
