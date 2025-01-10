# Guía de Uso del Plugin UHCPoints

## Comandos

- `/uhcpoints start`: Inicia un juego UHC y otorga puntos iniciales a todos los jugadores.
- `/uhcpoints end`: Finaliza el juego UHC y otorga puntos de bonificación a los mejores jugadores.
- `/uhcpoints dump`: Guarda todas las puntuaciones de los jugadores en un archivo YAML.

## Ganar Puntos

Los jugadores pueden ganar puntos a través de varias acciones:

1. Primera muerte del juego: 50 puntos
2. Muertes subsiguientes: 30 puntos
3. Consumir una manzana de Notch: 5 puntos
4. Minar mineral de diamante (cada 5 diamantes): 1 punto
5. Encantamiento de alto nivel (nivel 30+): 5 puntos

## Placeholders

Usa estos placeholders con PlaceholderAPI:

- `%uhcpoints_points%`: Muestra los puntos actuales del jugador
- `%uhcpoints_ownplace%`: Muestra la clasificación actual del jugador
- `%uhcpoints_place_<número>_points%`: Muestra los puntos del jugador en la clasificación especificada
- `%uhcpoints_place_<número>_name%`: Muestra el nombre del jugador en la clasificación especificada

## Permisos

- `uhcpoints.use`: Permite el uso de los comandos de UHC Points
- `uhcpoints.bypass`: Excluye al jugador de la clasificación de UHC Points