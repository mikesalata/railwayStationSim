package org.msalata.railwayStationSim;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.msalata.railwayStationSim.models.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.msalata.railwayStationSim.Constants.*;
public class Main extends Application {

    private boolean running = false;
    private final Timeline timeline = new Timeline();

    @Override
    public void start(Stage stage) {
        Map<String, StationClientNode> clientNodeMap = new ConcurrentHashMap<>();
        Map<String, StaticStationObjectNode> staticObjectsMap = new ConcurrentHashMap<>();
        Map<String, DynamicStationObject> dynamicObjectsMap = new ConcurrentHashMap<>();
        final StationObjects stationObjects = new StationObjects(dynamicObjectsMap, staticObjectsMap);
        Pane root = new Pane();
        Thread cleaner = new ObjectsCleaner(clientNodeMap);
        List<Thread> eternalThreads = BackgroundObjectsCreator.setupBackgroundObjects(stationObjects, clientNodeMap, root);
        eternalThreads.add(cleaner);
        Scene scene = new Scene(createContent(clientNodeMap, root));
        stage.setTitle("pkpStation");
        stage.setHeight(PANE_HEIGHT);
        stage.setWidth(PANE_WIDTH);
        stage.setScene(scene);
        stage.show();
        eternalThreads.forEach(Thread::start);
        startSimulation();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Parent createContent(Map<String, StationClientNode> clientNodeMap, Pane root) {
        Group clientObjects = new Group();
        root.getChildren().add(clientObjects);
        root.setBackground(Background.fill(Color.BLACK));
        root.setPrefSize(APP_W, APP_H);
        KeyFrame frame = new KeyFrame(Duration.seconds(0.04), event -> {
            if (!running) {
                return;
            }
            clientNodeMap.values().stream()
                    .filter(StationClientNode::isDead)
                    .map(StationClientNode::getNode)
                    .forEach(node -> clientObjects.getChildren().remove(node));
            clientNodeMap.values().forEach((clientNode -> {
                if (!clientNode.appeared()) {
                    clientNode.appear();
                    clientObjects.getChildren().add(clientNode.getNode());
                }
                clientNode.performClientMovement();
            }));
        });

        timeline.getKeyFrames().add(frame);
        timeline.setCycleCount(Animation.INDEFINITE);
        return root;
    }
    private void startSimulation() {
        timeline.play();
        running = true;
    }
}
