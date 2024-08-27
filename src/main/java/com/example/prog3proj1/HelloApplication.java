package com.example.prog3proj1;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import mpi.MPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;

// extends Application making it a JavaFX application
public class HelloApplication extends Application {

    // default setup for the carpet
    public static final int DEFAULT_SIZE = 500;
    public static final int DEFAULT_X = 150;
    public static final int DEFAULT_Y = 45;
    public static final int MAX_LEVEL = 15;


    // instance of the ViewController class to handle view transformations
    private ViewController viewController;


    // keep track of current dimensions
    private double sceneWidth;
    private double sceneHeight;


    // initialize the JavaFX application
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sierpinski Carpet");  // window title

        int level = getValidInput();   // get the number of levels from the user

        Pane drawingPane = new Pane();     // create a pane to draw on
        Scale scaleTransform = new Scale(1, 1, 0, 0);   // initialize scale with default values, no scaling
        Translate translateTransform = new Translate(0, 0); // initialize translate with default values, no moving
        drawingPane.getTransforms().addAll(scaleTransform, translateTransform); // add adjustments to the carpet

        // used to arrange elements
        BorderPane borderPane = new BorderPane();

        // Toolbar with zoom and reset buttons
        ToolBar toolBar = new ToolBar();

        // create buttons
        Button zoomInButton = new Button("Zoom In");
        Button zoomOutButton = new Button("Zoom Out");
        Button resetButton = new Button("Reset View");

        // initialize the ViewController with the scale and translate transformations
        viewController = new ViewController(scaleTransform, translateTransform);

        // set actions
        zoomInButton.setOnAction(e -> viewController.zoom(1.2)); // when clicked call zoom method with factor of 1.2
        zoomOutButton.setOnAction(e -> viewController.zoom(0.8)); // zoom out by 0.8
        resetButton.setOnAction(e -> viewController.resetView(sceneWidth, sceneHeight)); // reset to default view

        toolBar.getItems().addAll(zoomInButton, zoomOutButton, resetButton); // add buttons to the toolbar

        borderPane.setTop(toolBar); // place the toolbar at the top of the borderPane
        borderPane.setCenter(drawingPane); // place the drawingPane in the center of the borderPane

        // draw black rectangle
        Rectangle box = new Rectangle(DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE, DEFAULT_SIZE); // create a black rectangle at the coordinates
        box.setFill(Color.BLACK);
        drawingPane.getChildren().addAll(box); // add the black rectangle to the drawingPane, the background of the carpet





        // list to store rectangles
        List<Rectangle> rectangles = new ArrayList<>();  // holds all the white rectangles that will be added

        // takes the level of recursion, the list of rectangles to store the results and the initial position and size of the largest rectangle the base
        //GasketTask task = new GasketTask(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE);


        if (shouldUseParallel()) {
            computeGasketParallel(level, rectangles);
        } else {
            computeGasketSequentially(level, rectangles);
        }



        drawingPane.getChildren().addAll(rectangles); // add all the rectangles stored in the rectangles list to the drawingPane






        // create a StackPane to layer the BorderPane on top of the drawingPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(drawingPane, borderPane); // layout that stacks its children on top of each other
        // drawingPane then borderPane, so it appears above the drawing

        Scene scene = new Scene(stackPane, 800, 600); // create a scene with the stackPane as the root node



        // keep track of the current width and height of the scene, which can change if the user resizes the window
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            sceneWidth = newVal.doubleValue();
            viewController.updateDrawingPane(sceneWidth, sceneHeight); // Update drawing pane on width change
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            sceneHeight = newVal.doubleValue();
            viewController.updateDrawingPane(sceneWidth, sceneHeight); // Update drawing pane on height change
        });


        // Initialize scene dimensions to current width and height
        sceneWidth = scene.getWidth();
        sceneHeight = scene.getHeight();



        // Enable mouse dragging for panning
        scene.setOnMousePressed(viewController::startDrag); // event handler for mouse press
        scene.setOnMouseReleased(e -> scene.setCursor(Cursor.DEFAULT)); // event handler for mouse release
        scene.setOnMouseDragged(viewController::drag); // event handler for mouse drag
        //  updates the view's translation based on the mouse movement, allowing the user to pan or move the view around.

        primaryStage.setScene(scene); // set the scene to the stage, assigns the previously created Scene to primaryStage
        // primary stage will display the content defined in the scene (StackPane that contains the drawingPane and borderPane)
        primaryStage.show(); // display the stage


        // The Stage class represents the main window of a JavaFX application. It is the top-level container that holds everything you see on the screen.

        // The Scene class represents the content and layout of a Stage. It holds all the graphical elements and controls that you want to display.
        // We create and configure a Scene to define the layout and appearance of the application. This includes setting the root node (e.g., StackPane, BorderPane) and adding child nodes to it.

        // Stage Contains a Scene: The Stage serves as the main window that holds a single Scene. Each Stage can have only one Scene at a time, but a Scene can be switched out if needed (e.g., to show different content).


    }

    // determine whether to use parallel computation
    private boolean shouldUseParallel() {
        // ask the user in what mode should the application run
        System.out.println("Would you like to run the computation in parallel or sequentially?");
        System.out.println("Enter 'p' for parallel or 's' for sequential:");

        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();

        // check the response
        return response.equalsIgnoreCase("p");
    }

    private int getValidInput() {
        Scanner keyboard = new Scanner(System.in); // read input from the console
        int level; // store the user input

        do {
            System.out.println("How many levels of carpet do you want? (Enter a number between 1 and " + MAX_LEVEL + ")");
            // user is prompted to enter a number between 1 and MAX_LEVEL
            while (!keyboard.hasNextInt()) { // check if the input is an integer
                System.out.println("Invalid input. Please enter a number between 1 and " + MAX_LEVEL +".");
                keyboard.next(); // Consume the invalid input
            }
            level = keyboard.nextInt(); // read the input and assign it to level
        } while (level <= 0 || level > MAX_LEVEL); // repeat the loop if the input is less than or equal to 0 or greater than MAX_LEVEL

        return level; // return the valid input
    }


    private void computeGasketParallel(int level, List<Rectangle> rectangles) {
        int parallelism = Runtime.getRuntime().availableProcessors();
        long startTime = System.currentTimeMillis();
        try (ForkJoinPool pool = new ForkJoinPool(parallelism)) {
            ParallelGasketTask task = new ParallelGasketTask(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE);
            pool.invoke(task);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Parallel computation took: " + (endTime - startTime) + " ms");
    }

    private void computeGasketSequentially(int level, List<Rectangle> rectangles) {
        long startTime = System.currentTimeMillis();
        SequentialGasketTask task = new SequentialGasketTask(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE);
        task.compute();
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential computation took: " + (endTime - startTime) + " ms");
    }

    private void computeGasketDistributively(int level, List<Rectangle> rectangles) {
        MPI.Init(null);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        System.out.println("Process " + rank + " of " + size + " starting computation.");

        DistributiveGasketTask task = new DistributiveGasketTask(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE);
        task.compute();

        MPI.Finalize();
    }


    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);

        // Initialize MPI
        MPI.Init(args);

        // Start MPI computation
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            System.out.println("Hello world from " + rank + " of " + size);
        }

        MPI.Finalize();

    }
}