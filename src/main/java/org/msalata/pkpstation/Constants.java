package org.msalata.pkpstation;

import javafx.scene.image.Image;

public class Constants {
    public static final int BLOCK_SIZE = 40;
    public static final int APP_W = 40 * BLOCK_SIZE;
    public static final int APP_H = 35 * BLOCK_SIZE;
    public static final double PANE_WIDTH = 1600;
    public static final double PANE_HEIGHT = 900;
    public static final double OBJECT_MOVEMENT_DELTA = 10.0;
    public static final int CLIENT_BLOCK_SIZE = 10;
    public static final double MOVING_STARTING_POINT_X = 0.0;
    public static final double MOVING_STARTING_POINT_Y = PANE_HEIGHT / 2.0 - 30.0;
    public static final int OUT_OF_QUEUE_POSITION = 13;
    public static final double ESCAPE_X = Constants.PANE_WIDTH - 40.0;
    public static final double ESCAPE_Y = Constants.PANE_HEIGHT / 2.0 - 25.0;
    public static final int MAX_CLIENT_THREADS = 30;
    public static final Image CITY_OFFICES_IMAGE = new Image("city_ticket_office_min.png");
    public static final Image INTERCITY_OFFICES_IMAGE = new Image("intercity_ticket_office_min.png");
    public static final Image INFO_OFFICES_IMAGE = new Image("info_office_min.png");
    public static final Image CITY_OFFICES_CLOSED_IMAGE = new Image("city_ticket_office_closed_min.png");
    public static final Image INTERCITY_OFFICES_CLOSED_IMAGE = new Image("intercity_ticket_office_closed_min.png");
    public static final Image INFO_OFFICES_CLOSED_IMAGE = new Image("info_office_closed_min.png");
    public static final Image TECHNICIAN_IMAGE = new Image("technician_min.png");
    private Constants() {}
}

