Author : Maciej Sałata

### About
PKP station (railway station) lobby simulation with visualisation in Java 21 + Java FX.
A client enter the station,
buys a train ticket (in city/intercity ticket office),
then optionally go for information (info office),
then finally moves away from lobby.
Additionally:
* clients waits in queues to get to offices
* ticket offices may take a break sometime
* info office may experience a workstation malfunction - in that case it needs to be repaired by technician to continue its work.

### Genesis:
A project to have some fun and verify my multithreading skills (generally thread sync) after completing course
(https://www.udemy.com/course/java-multithreading-concurrency-performance-optimization/)

### To open the project using Intellij
1. File -> New -> "Project from Existing Sources" or "Import Project".
2. Select the project directory.
3. Select "Create Project from Existing Sources" and click "Next" repeatedly until the last window.
4. Click "Finish"

### To run the JavaFX application
1. Open the terminal by going to View -> Tool Windows -> Terminal.
2. In the terminal run the following command: ```mvn clean compile exec:java```

