# Grafo de Distancias — Campus UPB
### Datos de entrada para el algoritmo de Dijkstra (peso = distancia en **pasos**)

> Este archivo convierte la tabla/matriz de distancias (`Distancias_GRAFO_UPB_-_Hoja_1.pdf`) en un formato limpio y estructurado para que una IA (o el propio código Java) pueda cargarlo directamente como grafo ponderado no dirigido. El grafo es **simétrico**: la distancia de X→Y es igual a Y→X.

---

## 1. Nodos del grafo (16 en total)

| Nodo | Tipo probable |
|---|---|
| A | Edificio |
| B | Edificio |
| C | Edificio |
| D | Edificio |
| E | Edificio |
| F | Edificio |
| G | Edificio |
| H | Edificio |
| I | Edificio |
| J | Edificio |
| K | Edificio |
| L | Edificio |
| Portería Entr | Punto de acceso (portería principal) |
| Portería K | Punto de acceso (portería cerca de edificio K) |
| CafeteriaAB | Punto de servicio (cafetería entre A y B) |
| Zona ventas PT | Punto de servicio (zona de ventas) |

> Nota: confirmar con el equipo si "A"…"L" corresponden a nombres reales de edificios de la UPB (ej. Biblioteca, Auditorio, etc.) para reemplazarlos por nombres descriptivos antes de usarlos en la interfaz final. Mientras tanto, se mantienen las letras tal como aparecen en la fuente original.

---

## 2. Lista de aristas (formato origen–destino–peso)

Peso expresado en **pasos**. Esta es la lista mínima de aristas (sin duplicar cada par); al cargar el grafo como **no dirigido**, cada arista debe agregarse en ambos sentidos (X→Y y Y→X) con el mismo peso.

| # | Origen | Destino | Peso (pasos) |
|---|---|---|---|
| 1 | A | B | 5 |
| 2 | B | C | 5 |
| 3 | B | D | 38 |
| 4 | B | Portería Entr | 36 |
| 5 | B | CafeteriaAB | 14 |
| 6 | C | D | 34 |
| 7 | C | J | 17 |
| 8 | C | Portería Entr | 30 |
| 9 | C | CafeteriaAB | 19 |
| 10 | D | E | 29 |
| 11 | D | F | 19.13 |
| 12 | D | G | 50 |
| 13 | D | J | 28 |
| 14 | D | K | 110 |
| 15 | D | L | 171 |
| 16 | D | Portería K | 106 |
| 17 | D | CafeteriaAB | 20 |
| 18 | E | F | 10 |
| 19 | E | G | 35 |
| 20 | E | H | 7 |
| 21 | E | I | 70 |
| 22 | E | K | 100 |
| 23 | E | Portería K | 110 |
| 24 | F | G | 18 |
| 25 | F | H | 13 |
| 26 | F | J | 77.09 |
| 27 | F | Zona ventas PT | 33 |
| 28 | G | H | 24 |
| 29 | G | J | 37 |
| 30 | G | Zona ventas PT | 10 |
| 31 | H | I | 80 |
| 32 | H | L | 77 |
| 33 | H | Zona ventas PT | 15 |
| 34 | I | K | 75 |
| 35 | I | L | 40.76 |
| 36 | J | K | 127 |
| 37 | J | Portería Entr | 30.84 |
| 38 | K | L | 120 |

**Total: 16 nodos, 38 aristas.**

---

## 3. Formato JSON listo para persistencia (RF-08)

Este bloque puede usarse directamente como archivo de datos que el sistema Java carga al iniciar (según el marco tecnológico: persistencia en JSON/CSV).

```json
{
  "nodos": [
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
    "Porteria_Entr", "Porteria_K", "CafeteriaAB", "Zona_ventas_PT"
  ],
  "aristas": [
    { "origen": "A", "destino": "B", "peso": 5, "escaleras": false },
    { "origen": "B", "destino": "C", "peso": 5, "escaleras": false },
    { "origen": "B", "destino": "D", "peso": 38, "escaleras": false },
    { "origen": "B", "destino": "Porteria_Entr", "peso": 36, "escaleras": false },
    { "origen": "B", "destino": "CafeteriaAB", "peso": 14, "escaleras": false },
    { "origen": "C", "destino": "D", "peso": 34, "escaleras": false },
    { "origen": "C", "destino": "J", "peso": 17, "escaleras": false },
    { "origen": "C", "destino": "Porteria_Entr", "peso": 30, "escaleras": false },
    { "origen": "C", "destino": "CafeteriaAB", "peso": 19, "escaleras": false },
    { "origen": "D", "destino": "E", "peso": 29, "escaleras": false },
    { "origen": "D", "destino": "F", "peso": 19.13, "escaleras": false },
    { "origen": "D", "destino": "G", "peso": 50, "escaleras": false },
    { "origen": "D", "destino": "J", "peso": 28, "escaleras": false },
    { "origen": "D", "destino": "K", "peso": 110, "escaleras": false },
    { "origen": "D", "destino": "L", "peso": 171, "escaleras": false },
    { "origen": "D", "destino": "Porteria_K", "peso": 106, "escaleras": false },
    { "origen": "D", "destino": "CafeteriaAB", "peso": 20, "escaleras": false },
    { "origen": "E", "destino": "F", "peso": 10, "escaleras": false },
    { "origen": "E", "destino": "G", "peso": 35, "escaleras": false },
    { "origen": "E", "destino": "H", "peso": 7, "escaleras": false },
    { "origen": "E", "destino": "I", "peso": 70, "escaleras": false },
    { "origen": "E", "destino": "K", "peso": 100, "escaleras": false },
    { "origen": "E", "destino": "Porteria_K", "peso": 110, "escaleras": false },
    { "origen": "F", "destino": "G", "peso": 18, "escaleras": false },
    { "origen": "F", "destino": "H", "peso": 13, "escaleras": false },
    { "origen": "F", "destino": "J", "peso": 77.09, "escaleras": false },
    { "origen": "F", "destino": "Zona_ventas_PT", "peso": 33, "escaleras": false },
    { "origen": "G", "destino": "H", "peso": 24, "escaleras": false },
    { "origen": "G", "destino": "J", "peso": 37, "escaleras": false },
    { "origen": "G", "destino": "Zona_ventas_PT", "peso": 10, "escaleras": false },
    { "origen": "H", "destino": "I", "peso": 80, "escaleras": false },
    { "origen": "H", "destino": "L", "peso": 77, "escaleras": false },
    { "origen": "H", "destino": "Zona_ventas_PT", "peso": 15, "escaleras": false },
    { "origen": "I", "destino": "K", "peso": 75, "escaleras": false },
    { "origen": "I", "destino": "L", "peso": 40.76, "escaleras": false },
    { "origen": "J", "destino": "K", "peso": 127, "escaleras": false },
    { "origen": "J", "destino": "Porteria_Entr", "peso": 30.84, "escaleras": false },
    { "origen": "K", "destino": "L", "peso": 120, "escaleras": false }
  ]
}
```

> **Nota sobre `"escaleras": false`:** el documento de distancias no especifica cuáles caminos tienen escaleras. Este campo se dejó en `false` por defecto para que el sistema funcione de inmediato; debe actualizarse manualmente (o mediante el módulo de RF-07) cuando se identifique qué tramos reales del campus tienen escaleras, para que el filtro de accesibilidad (RF-05) funcione correctamente.

---

## 4. Cómo debe usar esto la IA al generar código

1. Cargar los nodos y aristas de la sección 2 o 3 como el estado inicial del grafo (lista de adyacencia `Map<Nodo, List<Arista>>`), agregando cada arista en ambos sentidos por tratarse de un grafo **no dirigido**.
2. Usar estos pesos (en pasos) como la métrica de distancia para Dijkstra — no inventar ni normalizar otras unidades salvo que el usuario lo pida.
3. Mantener los nombres de nodo exactamente como aparecen aquí (`A`, `B`, ..., `Porteria_Entr`, `Porteria_K`, `CafeteriaAB`, `Zona_ventas_PT`) al escribir claves internas, evitando tildes/espacios en identificadores de código (ya normalizados en la sección 3), pero pueden mostrarse con tilde y espacio en la interfaz de usuario.
4. Si en el futuro se agregan "lugares" internos por edificio (ej. "biblioteca" dentro de J), estos deben mapearse a uno de estos 16 nodos existentes, según el objetivo específico de mapeo interno de lugares (RF-03/RF-04) del documento de especificación del proyecto.
