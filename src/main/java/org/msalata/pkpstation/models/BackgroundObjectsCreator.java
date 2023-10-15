package org.msalata.pkpstation.models;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.msalata.pkpstation.Constants;
import org.msalata.pkpstation.logic.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.msalata.pkpstation.Constants.PANE_HEIGHT;
import static org.msalata.pkpstation.Constants.PANE_WIDTH;

public class BackgroundObjectsCreator {

    private BackgroundObjectsCreator() {}

    public static List<Thread> setupBackgroundObjects(StationObjects stationObjects, Map<String, StationClientNode> clientNodeMap, Pane root) {

        double cityOfficesY = PANE_HEIGHT / 8.0 - 100;
        double firstOfficesX = PANE_WIDTH / 8.0 * 3.0;
        double secondOfficesX = PANE_WIDTH / 8.0 * 4.0;
        double intercityOfficesY = PANE_HEIGHT / 8.0 * 7.0 - 50.0;
        double infoOfficeX = PANE_WIDTH / 8.0 * 6.0;
        double infoOfficeY = PANE_HEIGHT / 8.0 * 2.0;

        List<Thread> eternalThreads = new ArrayList<>();
        UUID cityQueueId = UUID.randomUUID();

        // CITY objects
        StationQueue<Client> cityTrainsQueue = new StationQueue<>(10, cityQueueId);
        String cityTicketOffice1Name = "City ticket office #1";
        ImageView cityTicketOffice1Object = new ImageView(Constants.CITY_OFFICES_IMAGE);
        cityTicketOffice1Object.setTranslateX(firstOfficesX);
        cityTicketOffice1Object.setTranslateY(cityOfficesY);
        root.getChildren().add(cityTicketOffice1Object);
        buildTicketOffice(cityTicketOffice1Name, cityTicketOffice1Object, cityTrainsQueue, eternalThreads, stationObjects, false, StaticStationObjectNode.STATIC_OBJECT_TYPE.CITY);

        String cityTicketOffice2Name = "City ticket office #2";
        ImageView cityTicketOffice2Object = new ImageView(Constants.CITY_OFFICES_IMAGE);
        cityTicketOffice2Object.setTranslateX(secondOfficesX);
        cityTicketOffice2Object.setTranslateY(cityOfficesY);
        root.getChildren().add(cityTicketOffice2Object);
        buildTicketOffice(cityTicketOffice2Name, cityTicketOffice2Object, cityTrainsQueue, eternalThreads, stationObjects, false, StaticStationObjectNode.STATIC_OBJECT_TYPE.CITY);
        stationObjects.addDynamicStationObject(cityTrainsQueue.getIdentifier(), new DynamicStationObject((firstOfficesX + secondOfficesX) / 2.0 + Constants.CITY_OFFICES_IMAGE.getWidth() / 2.0,
                cityOfficesY + Constants.CITY_OFFICES_IMAGE.getHeight() * 1.5, false, cityQueueId, 0.0, 0.0));

        // INTERCITY objects
        UUID interCityQueueId = UUID.randomUUID();
        StationQueue<Client> interCityTrainsQueue = new StationQueue<>(10, interCityQueueId);
        String interCityTicketOffice1Name = "Inter city ticket office #1";
        ImageView interCityTicketOffice1Object = new ImageView(Constants.INTERCITY_OFFICES_IMAGE);
        interCityTicketOffice1Object.setTranslateX(firstOfficesX);
        interCityTicketOffice1Object.setTranslateY(intercityOfficesY);
        interCityTicketOffice1Object.setRotate(180.0);
        root.getChildren().add(interCityTicketOffice1Object);
        buildTicketOffice(interCityTicketOffice1Name, interCityTicketOffice1Object, interCityTrainsQueue, eternalThreads, stationObjects, true, StaticStationObjectNode.STATIC_OBJECT_TYPE.INTERCITY);

        String interCityTicketOffice2Name = "Inter city ticket office #2";
        ImageView interCityTicketOffice2Object = new ImageView(Constants.INTERCITY_OFFICES_IMAGE);
        interCityTicketOffice2Object.setTranslateX(secondOfficesX);
        interCityTicketOffice2Object.setTranslateY(intercityOfficesY);
        interCityTicketOffice2Object.setRotate(180.0);
        root.getChildren().add(interCityTicketOffice2Object);
        buildTicketOffice(interCityTicketOffice2Name, interCityTicketOffice2Object, interCityTrainsQueue, eternalThreads, stationObjects, true, StaticStationObjectNode.STATIC_OBJECT_TYPE.INTERCITY);
        stationObjects.addDynamicStationObject(interCityTrainsQueue.getIdentifier(), new DynamicStationObject((firstOfficesX + secondOfficesX) / 2.0 + Constants.INTERCITY_OFFICES_IMAGE.getWidth() / 2.0,
                intercityOfficesY - Constants.INTERCITY_OFFICES_IMAGE.getHeight() * 0.5, true, interCityQueueId, 0.0, 0.0));

        // INFO objects
        UUID infoQueueId = UUID.randomUUID();
        StationQueue<Client> infoQueue = new StationQueue<>(10, infoQueueId);
        OfficeWorkstation officeWorkstation = new OfficeWorkstation();
        eternalThreads.add(officeWorkstation);
        Technician technician = new Technician(officeWorkstation);
        ImageView technicianObject = new ImageView(Constants.TECHNICIAN_IMAGE);
        technicianObject.setTranslateX(PANE_WIDTH / 8.0 * 7.0);
        technicianObject.setTranslateY(PANE_HEIGHT / 8.0 * 2.0);
        root.getChildren().add(technicianObject);
        eternalThreads.add(technician);

        String infoOfficeName = "Info office";
        ImageView infoOfficeObject = new ImageView(Constants.INFO_OFFICES_IMAGE);
        infoOfficeObject.setTranslateX(infoOfficeX);
        infoOfficeObject.setTranslateY(infoOfficeY);
        root.getChildren().add(infoOfficeObject);

        stationObjects.addDynamicStationObject(infoQueue.getIdentifier(), new DynamicStationObject(infoOfficeX + Constants.INFO_OFFICES_IMAGE.getWidth() / 2.0,
                infoOfficeY + Constants.INFO_OFFICES_IMAGE.getHeight() * 1.5, false, infoQueueId, 0.0, 0.0));
        buildInfoOffice(infoOfficeName, infoOfficeObject, infoQueue, officeWorkstation, eternalThreads, stationObjects);


        //Client (customers)
        ClientGenerator clientGenerator = new ClientGenerator(cityTrainsQueue, interCityTrainsQueue,
                infoQueue, clientNodeMap, stationObjects);
        eternalThreads.add(clientGenerator);

        buildInfrastructureObjects(root, cityTicketOffice1Object, cityTicketOffice2Object);

        return eternalThreads;
    }

    private static void buildTicketOffice(String ticketOfficeName, ImageView ticketOfficeObject, StationQueue<Client> stationQueue, List<Thread> eternalThreads,
                                          StationObjects stationObjects, boolean reversed, StaticStationObjectNode.STATIC_OBJECT_TYPE staticObjectType) {
        UUID id = UUID.randomUUID();
        StaticStationObjectNode ticketOfficeObjectNode = new StaticStationObjectNode(ticketOfficeObject, id, reversed,
                ticketOfficeObject.getImage().getHeight(), ticketOfficeObject.getImage().getWidth(), staticObjectType);
        TicketOffice ticketOffice = new TicketOffice(stationQueue, ticketOfficeName, id, ticketOfficeObjectNode);
        eternalThreads.add(ticketOffice);
        stationObjects.addStaticStationObject(ticketOfficeObjectNode.getId(), ticketOfficeObjectNode);
    }

    private static void buildInfoOffice(String infoOfficeName, ImageView infoOfficeObject, StationQueue<Client> stationQueue, OfficeWorkstation officeWorkstation,
                                        List<Thread> eternalThreads, StationObjects stationObjects) {
        UUID id = UUID.randomUUID();
        StaticStationObjectNode infoOfficeObjectNode = new StaticStationObjectNode(infoOfficeObject, id, false,
                infoOfficeObject.getImage().getHeight(), infoOfficeObject.getImage().getWidth(), StaticStationObjectNode.STATIC_OBJECT_TYPE.INFO);
        InfoOffice ticketOffice = new InfoOffice(stationQueue, infoOfficeName, id, officeWorkstation, infoOfficeObjectNode);
        eternalThreads.add(ticketOffice);
        stationObjects.addStaticStationObject(infoOfficeObjectNode.getId(), infoOfficeObjectNode);
    }

    private static void buildInfrastructureObjects(Pane root, ImageView cityTicketOffice1Object, ImageView cityTicketOffice2Object) {
        Rectangle topBorder = new Rectangle(PANE_WIDTH - 15.0, 10);
        topBorder.setTranslateX(0.0);
        topBorder.setTranslateY(0.0);
        topBorder.setFill(Color.GRAY);
        Rectangle bottomBorder = new Rectangle(PANE_WIDTH - 15.0, 10);
        bottomBorder.setTranslateX(0.0);
        bottomBorder.setTranslateY(PANE_HEIGHT - 47.0);
        bottomBorder.setFill(Color.GRAY);
        Rectangle leftBorderTop = new Rectangle(10, PANE_HEIGHT / 2.0 - 50.0);
        leftBorderTop.setTranslateX(0.0);
        leftBorderTop.setTranslateY(0.0);
        leftBorderTop.setFill(Color.GRAY);
        Rectangle leftBorderBottom = new Rectangle(10, PANE_HEIGHT / 2.0 - 42.0);
        leftBorderBottom.setTranslateX(0.0);
        leftBorderBottom.setTranslateY(PANE_HEIGHT / 2.0 + 5.0);
        leftBorderBottom.setFill(Color.GRAY);
        Rectangle rightBorderTop = new Rectangle(10.0, PANE_HEIGHT / 2.0 - 50.0);
        rightBorderTop.setTranslateX(PANE_WIDTH - 25.0);
        rightBorderTop.setTranslateY(0.0);
        rightBorderTop.setFill(Color.GRAY);
        Rectangle rightBorderBottom = new Rectangle(10.0, PANE_HEIGHT / 2.0 - 50.0);
        rightBorderBottom.setTranslateX(PANE_WIDTH - 25.0);
        rightBorderBottom.setTranslateY(PANE_HEIGHT / 2.0 + 5.0);
        rightBorderBottom.setFill(Color.GRAY);
        Rectangle cityQueueWallLeft = new Rectangle(10.0, 100.0);
        cityQueueWallLeft.setTranslateX(
                (cityTicketOffice1Object.getTranslateX() + cityTicketOffice2Object.getTranslateX()) / 2.0 - 150.0);
        cityQueueWallLeft.setTranslateY(cityTicketOffice1Object.getTranslateY() + 250.0);
        cityQueueWallLeft.setFill(Color.GRAY);

        Rectangle interCityQueueWallLeft = new Rectangle(10.0, 100.0);
        interCityQueueWallLeft.setTranslateX(cityQueueWallLeft.getTranslateX());
        interCityQueueWallLeft.setTranslateY(cityTicketOffice1Object.getTranslateY() + 250.0 * 2);
        interCityQueueWallLeft.setFill(Color.GRAY);

        Rectangle entranceWallTop = new Rectangle(PANE_WIDTH / 2.0 - 250.0, 10);
        entranceWallTop.setTranslateX(10.0);
        entranceWallTop.setTranslateY(cityQueueWallLeft.getTranslateY() + 100.0);
        entranceWallTop.setFill(Color.GRAY);

        Rectangle entranceWallBottom = new Rectangle(PANE_WIDTH / 2.0 - 250.0, 10);
        entranceWallBottom.setTranslateX(10.0);
        entranceWallBottom.setTranslateY(interCityQueueWallLeft.getTranslateY());
        entranceWallBottom.setFill(Color.GRAY);

        root.getChildren().addAll(List.of(topBorder, bottomBorder, leftBorderTop, leftBorderBottom,
                rightBorderTop, rightBorderBottom, cityQueueWallLeft, interCityQueueWallLeft, entranceWallTop, entranceWallBottom));
    }
}
