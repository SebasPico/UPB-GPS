# Sistema de Detección de Ruta Más Corta Entre Edificios UPB
### Especificación técnica para asistir en el desarrollo del proyecto (Java + JavaFX)

> **Propósito de este documento:** servir de guía completa para que una IA (o cualquier desarrollador) entienda el proyecto de principio a fin y pueda generar código, documentación o artefactos consistentes con lo ya definido en las Fases 1 y 2 del proyecto de aula de la UPB. No inventes requisitos nuevos ni cambies el alcance sin que el usuario lo indique explícitamente; usa este documento como fuente de verdad.

---

## 0. Resumen ejecutivo

Construir un **sistema de escritorio en Java, con interfaz gráfica en JavaFX**, que permita a estudiantes, profesores y visitantes de la Universidad Pontificia Bolivariana (UPB) calcular la **ruta más corta y accesible** entre dos puntos del campus (edificios o lugares específicos dentro de ellos), modelando el campus como un **grafo ponderado no dirigido** y aplicando el **algoritmo de Dijkstra**.

Autores del proyecto:
- Sebastian Pico Afanador — sebastian.pico.2022@upb.edu.co
- Carlos Felipe Sarmiento Avellaneda — carlosf.sarmiento@upb.edu.co
- David Mattias Barbosa Vargas — david.barbosa.2023@upb.edu.co

Facultad de Ingeniería de Sistemas e Informática. Metodología: **SCRUM**, en iteraciones semanales desde la semana 5 hasta la semana 14.

---

## 1. Planteamiento del problema

La UPB tiene un campus amplio con múltiples edificios, auditorios, biblioteca y zonas de descanso, conectados por caminos internos. Cada edificio contiene a su vez varias dependencias (salas, oficinas, auditorios, laboratorios), por lo que es común que una persona sepa a qué **lugar** debe ir sin saber en qué **edificio** está ubicado.

Hoy la orientación depende de mapas estáticos, señalización física o preguntar a otras personas, lo que genera:

1. **Falta de rutas optimizadas** — no siempre es claro cuál es el camino más corto.
2. **Desconocimiento del campus** — estudiantes nuevos o visitantes pierden tiempo y se estresan.
3. **Bloqueos temporales** — mantenimiento o eventos cierran caminos sin aviso claro.
4. **Accesibilidad limitada** — no todas las rutas sirven para personas con movilidad reducida.

**Pregunta de investigación:** ¿Cómo facilitar la movilidad dentro del campus UPB mediante un sistema que calcule automáticamente la ruta más corta y accesible entre dos puntos, permitiendo además la búsqueda directa por lugar de destino?

---

## 2. Objetivos

### 2.1 Objetivo general
Desarrollar un sistema interactivo de cálculo de rutas óptimas para el campus UPB que permita identificar el camino más corto y accesible entre dos puntos, modelando los caminos como un grafo ponderado y aplicando el algoritmo de Dijkstra.

### 2.2 Objetivos específicos
- Modelar el campus como un grafo ponderado (edificios, caminos, conexiones internas).
- Implementar Dijkstra para calcular la ruta más corta entre origen y destino.
- Diseñar una interfaz gráfica intuitiva (mapa interactivo o lista desplegable) para seleccionar origen/destino.
- Incorporar un mapeo interno de lugares por edificio, para buscar directamente un lugar (ej. "biblioteca") sin conocer el edificio.
- Implementar una opción de accesibilidad que excluya rutas con escaleras, priorizando rampas/ascensores.
- Desarrollar un módulo de actualización dinámica del grafo (agregar/eliminar conexiones) para reflejar bloqueos o cambios de infraestructura.
- Documentar técnicamente el sistema: algoritmo, arquitectura y manual de uso.

---

## 3. Alcance

**Incluye:**
- Modelado del campus (edificios, caminos, lugares) como grafo ponderado.
- Cálculo de ruta más corta mediante Dijkstra.
- Búsqueda de rutas por edificio y por lugar específico.
- Opción de accesibilidad (excluir escaleras).
- Interfaz gráfica para seleccionar origen/destino y visualizar la ruta.
- Módulo de actualización manual del grafo ante bloqueos o cambios.
- Documentación técnica (arquitectura, algoritmo, manual de uso).

**No incluye:**
- Geolocalización satelital (GPS) en tiempo real.
- Aplicación móvil nativa (el sistema es de escritorio/web).
- Actualización automática de bloqueos vía sensores/IoT (es manual).
- Integración con mapas externos (Google Maps, OpenStreetMap, etc.).
- Navegación turn-by-turn en tiempo real.
- Extensión a otras sedes distintas a la UPB.

---

## 4. Requerimientos funcionales (RF)

| ID | Requisito | Descripción |
|---|---|---|
| RF-01 | Cálculo de la ruta más corta | Calcular la ruta más corta entre origen y destino aplicando Dijkstra sobre un grafo ponderado. |
| RF-02 | Selección de origen y destino | Permitir seleccionar origen/destino mediante mapa interactivo o lista desplegable. |
| RF-03 | Búsqueda por lugar específico | Buscar directamente un lugar (ej. "biblioteca", "auditorio menor") sin conocer el edificio. |
| RF-04 | Mapeo interno de lugares por edificio | Mantener un registro que asocie cada lugar con su edificio, para resolver ubicaciones automáticamente. |
| RF-05 | Opción de accesibilidad | Excluir rutas con escaleras, priorizando rampas o ascensores. |
| RF-06 | Visualización de la ruta calculada | Mostrar gráficamente en pantalla origen, destino y puntos intermedios del recorrido. |
| RF-07 | Actualización dinámica del grafo | Agregar o eliminar conexiones (aristas) para reflejar bloqueos o cambios de infraestructura. |
| RF-08 | Persistencia de la información | Guardar/cargar el grafo (edificios, caminos, lugares, conexiones) desde archivo entre ejecuciones. |
| RF-09 | Gestión de errores de ruta | Informar al usuario cuando no exista ruta disponible (bloqueos, nodos desconectados). |

## 5. Requerimientos no funcionales (RNF)

| ID | Requisito | Descripción |
|---|---|---|
| RNF-01 | Usabilidad | Interfaz intuitiva, comprensible sin conocimientos técnicos, con retroalimentación visual clara. |
| RNF-02 | Rendimiento | Cálculo de ruta en menos de 2 segundos incluso con grafos de varios cientos de nodos/aristas. |
| RNF-03 | Portabilidad | Ejecutable en cualquier SO compatible con JVM, sin recompilación. |
| RNF-04 | Mantenibilidad | Código en POO, clases/paquetes bien separados y documentados. |
| RNF-05 | Escalabilidad | Permitir agregar edificios/caminos/lugares sin cambios estructurales grandes. |
| RNF-06 | Confiabilidad | Manejar rutas inexistentes, nodos desconectados o datos inválidos sin caídas inesperadas. |
| RNF-07 | Compatibilidad | La interfaz JavaFX debe funcionar en equipos Windows de la comunidad UPB. |
| RNF-08 | Documentación | Documentación técnica clara (arquitectura, algoritmo, manual de uso) para otros desarrolladores. |

## 6. Criterios de aceptación (de PROYECTO_DE_AULA)

1. **Calcular la mejor ruta:** ruta óptima en distancia/tiempo desde origen a destino, considerando todos los caminos disponibles.
2. **Interfaz gráfica intuitiva:** fácil de usar, accesible y comprensible para cualquier usuario.
3. **Opción para evitar escaleras:** el algoritmo excluye caminos con escaleras y propone rutas alternativas con rampas/ascensores.
4. **Búsqueda por lugar y no solo por edificio:** el sistema identifica automáticamente el edificio correspondiente al lugar buscado.
5. **Actualización dinámica de caminos:** agregar/eliminar conexiones manualmente para reflejar la situación actual del campus.
6. **Documentación completa:** clara, estructurada y comprensible para otros desarrolladores o administradores.
7. **Visualización de la ruta:** el usuario ve el camino marcado con instrucciones claras en pantalla.

---

## 7. Marco teórico (resumen que la IA debe respetar al explicar/documentar)

- **Grafo:** conjunto de vértices (edificios, entradas, lugares) y aristas (caminos que los conectan).
- **Grafo ponderado:** cada arista tiene un peso (distancia, tiempo o costo).
- **Grafo no dirigido:** se usará por defecto, ya que los caminos se recorren en ambos sentidos, salvo restricciones puntuales.
- **Representación en memoria:** **lista de adyacencia** (elegida sobre matriz de adyacencia por ser más eficiente en grafos dispersos como una red de caminos de campus).
- **Algoritmo de Dijkstra (1959):** calcula la ruta más corta desde un nodo origen a todos los demás en grafos con pesos no negativos. Mantiene distancias provisionales, selecciona en cada iteración el nodo no visitado de menor distancia acumulada y relaja las distancias de sus vecinos. Se eligió porque garantiza la ruta óptima con pesos no negativos (siempre el caso aquí) y, con cola de prioridad (min-heap), logra complejidad **O((V + E) log V)**.
- **Estructuras de apoyo:** `PriorityQueue` para Dijkstra; `HashMap`/`HashSet` para mapear lugares↔edificios↔nodos y controlar visitados.
- **Accesibilidad / diseño inclusivo:** permitir excluir del cálculo los caminos con escaleras.
- **IHC (Interacción Humano-Computador):** aplicar simplicidad, retroalimentación visual inmediata y consistencia en la interfaz.

---

## 8. Marco tecnológico (obligatorio — no sustituir sin autorización del usuario)

| Componente | Tecnología |
|---|---|
| Lenguaje | **Java** (POO, multiplataforma, JVM) |
| Interfaz gráfica | **JavaFX** (mapa interactivo o lista desplegable, visualización de ruta) |
| Grafo | `Map<Nodo, List<Arista>>` (lista de adyacencia, `java.util`) |
| Cálculo de ruta | `PriorityQueue` de Java para Dijkstra |
| Colecciones auxiliares | `HashMap`, `HashSet` (mapeo de lugares/edificios, nodos visitados) |
| Persistencia | Archivos **JSON o CSV** (posible evolución a SQLite en fases posteriores) |
| IDE | IntelliJ IDEA |
| Control de versiones | Git + GitHub |
| Diagramación | draw.io / Lucidchart |
| Gestión de proyecto | SCRUM + tablero (Trello o GitHub Projects) |
| Documentación | Word / PDF |

**Justificación:** Java y JavaFX son robustos, orientados a objetos y multiplataforma, adecuados tanto para las estructuras de datos (grafos, colas de prioridad) como para una interfaz de escritorio interactiva, cumpliendo el requisito de portabilidad (RNF-03).

---

## 9. Lineamientos de arquitectura sugeridos para la IA al generar código

Cuando se genere código para este proyecto, seguir esta organización modular (ajustable pero manteniendo la separación de responsabilidades):

```
src/
 └─ main/java/upb/rutas/
     ├─ modelo/          → Nodo, Arista, Grafo, Lugar, Edificio
     ├─ algoritmo/        → Dijkstra (con soporte para exclusión de aristas "con escaleras")
     ├─ persistencia/     → Carga/guardado de grafo en JSON o CSV
     ├─ servicio/         → Lógica de búsqueda por lugar, resolución lugar→edificio
     ├─ ui/               → Vistas JavaFX (selección origen/destino, mapa, resultado de ruta)
     └─ Main.java         → Punto de entrada (Application de JavaFX)
```

Reglas de diseño:
- Cada arista debe tener: peso (distancia/tiempo), y un atributo booleano o enum que indique si **tiene escaleras** (para el filtro de accesibilidad, RF-05).
- Dijkstra debe aceptar un parámetro de "modo accesible" que excluya aristas marcadas con escaleras antes o durante el cálculo.
- El grafo debe soportar `agregarArista`, `eliminarArista` (o marcarla como bloqueada) en tiempo de ejecución (RF-07), sin reiniciar la aplicación.
- Los "lugares" (ej. biblioteca, auditorio menor) deben mapearse a un edificio/nodo mediante una estructura tipo `Map<String Lugar, Nodo>` para soportar búsqueda directa (RF-03/RF-04).
- Si no hay ruta disponible, la interfaz debe mostrar un mensaje claro al usuario (RF-09), nunca fallar silenciosamente ni lanzar una excepción no controlada (RNF-06).
- El cálculo debe responder en menos de 2 segundos incluso con varios cientos de nodos/aristas (RNF-02): usar `PriorityQueue` para Dijkstra, no búsqueda por fuerza bruta.

---

## 10. Cronograma de entregables (fases del proyecto)

| Fase | Entregable | Semana | Descripción |
|---|---|---|---|
| 1 | Definición del Proyecto | 5 | Planteamiento del problema, objetivos, alcance |
| 2 | Requerimientos, Análisis e Investigación | 5 | RF, RNF, marco teórico y tecnológico |
| 3 | Diagrama de Clases | 6 | Atributos, relaciones y métodos |
| 4 | Diseño de la Arquitectura del Sistema | 7 | Casos de uso, modelo de datos, estructura del grafo |
| 5 | Metodología de Desarrollo | 7 | Documento SCRUM, iteraciones, pruebas |
| 6 | Mapa de Lugares por Edificio | 8 | Relación lugar específico ↔ edificio |
| 7 | Diseño de Interfaz Gráfica (UI/UX) | 9 | Bocetos/mockups de la interfaz |
| 8 | Primer Avance del Proyecto | 9 | Grafo funcional y cálculo de rutas en consola |
| 9 | Segundo Avance del Proyecto | 10 | Integración de interfaz gráfica con el cálculo de rutas |
| 10 | Gestión de Caminos Bloqueados | 11 | Módulo de actualización de red ante bloqueos |
| 11 | Pruebas y Optimización | 12 | Validación y mejoras de eficiencia del algoritmo |
| 12 | Entrega Final del Proyecto | 14 | Código fuente, documentación técnica completa, presentación |

> Estamos actualmente al inicio del proyecto (Fase 1–2, semana 5). El desarrollo funcional en Java/JavaFX comienza formalmente en la **Fase 8 (Semana 9)** con el grafo y Dijkstra en consola, y se integra con la interfaz en la **Fase 9 (Semana 10)**.

---

## 11. Instrucciones para la IA que use este documento

1. **No cambiar la tecnología base**: el proyecto es en Java con interfaz JavaFX. No proponer frameworks web, Python, u otros lenguajes salvo que el usuario lo pida explícitamente.
2. **Respetar el algoritmo definido**: usar Dijkstra con lista de adyacencia y cola de prioridad, no otros algoritmos de ruta mínima salvo solicitud explícita.
3. **Mantener el grafo no dirigido** por defecto, salvo que el usuario indique restricciones de sentido único en algún camino.
4. **Incluir siempre** el criterio de accesibilidad (exclusión de escaleras) en cualquier lógica de cálculo de rutas que se genere.
5. **Alinear cualquier código, diagrama o documento nuevo** con los RF/RNF y criterios de aceptación de este documento; si una solicitud del usuario contradice el alcance definido (sección 3), señalarlo antes de proceder.
6. **Al generar entregables** (diagramas de clases, arquitectura, mockups, código, documentación), ubicarlos dentro de la fase correspondiente del cronograma (sección 10) y usar terminología consistente con el marco teórico (sección 7).
7. **Idioma:** toda la documentación y el código deben mantenerse en español, salvo nombres de clases/métodos en Java que pueden seguir convenciones en inglés si el usuario lo prefiere (indicar la convención elegida).
