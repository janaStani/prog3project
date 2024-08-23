package com.example.prog3proj1;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class HelloApplication extends Application {

    // Fragment default values
    private static final int DEFAULT_SIZE = 500;
    private static final int DEFAULT_X = 150;
    private static final int DEFAULT_Y = 45;
    private static final int MAX_LEVEL = 5;

    // Manage the drawing pane transformations scaling and translating
    private Scale scaleTransform;
    private Translate translateTransform;
    // Handling drag events to move the view
    private double initialX, initialY;
    private double startX, startY;

    // To track the scene's width and height
    private double sceneWidth;
    private double sceneHeight;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sierpinski Carpet");  // Window title

        int level = getValidInput();   // Get the number of levels from the user

        Pane drawingPane = new Pane();     // Create a pane to draw on
        scaleTransform = new Scale(1, 1, 0, 0);   // Initialize scale with default values
        translateTransform = new Translate(0, 0); // Initialize translate with default values
        drawingPane.getTransforms().addAll(scaleTransform, translateTransform); // Add the scale and translate to the pane

        BorderPane borderPane = new BorderPane();    // Layout manager

        // Toolbar with zoom and reset buttons
        ToolBar toolBar = new ToolBar();

        // Create buttons
        Button zoomInButton = new Button("Zoom In");
        Button zoomOutButton = new Button("Zoom Out");
        Button resetButton = new Button("Reset View");

        // Set actions
        zoomInButton.setOnAction(e -> zoom(1.2)); // Zoom in by 1.2
        zoomOutButton.setOnAction(e -> zoom(0.8)); // Zoom out by 0.8
        resetButton.setOnAction(e -> resetView(sceneWidth, sceneHeight)); // Reset to default view

        toolBar.getItems().addAll(zoomInButton, zoomOutButton, resetButton); // Add buttons to the toolbar

        borderPane.setTop(toolBar); // place the toolbar at the top of the borderPane
        borderPane.setCenter(drawingPane); // place the drawingPane in the center of the borderPane

        // Draw black rectangle
        Rectangle box = new Rectangle(DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE, DEFAULT_SIZE); // Create a black rectangle at the coordinates
        box.setFill(Color.BLACK);
        drawingPane.getChildren().addAll(box); // Add the black rectangle to the drawingPane, the background of the carpet

        // List to store rectangles
        List<Rectangle> rectangles = new ArrayList<>();  // holds all the white rectangles that will be added

        GasketTask task = new GasketTask(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE);  // Create the root task


        // Compute white rectangles
        // Use parallel or sequential computation based on user choice
        if (shouldUseParallel()) {
            computeGasketParallel(level, rectangles, DEFAULT_X, DEFAULT_Y, DEFAULT_SIZE); // parallel version
        } else {
            task.compute(); // compute position and sizes of white rectangles
        }
        // takes the level of recursion, the list of rectangles to store the results and the initial position and size of the largest rectangle the base

        // Draw white rectangles
        drawingPane.getChildren().addAll(rectangles); // Add all the rectangles stored in the rectangles list to the drawingPane

        // Create a StackPane to layer the BorderPane on top of the drawingPane
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(drawingPane, borderPane); // layout that stacks its children on top of each other
        //  The drawingPane is added first, so it is at the bottom, and the borderPane is added on top, so it appears above the drawing.


        Scene scene = new Scene(stackPane, 800, 600); // Create a scene with the stackPane as the root node

        //  keep track of the current width and height of the scene, which can change if the user resizes the window.

        // Track scene width and height
        scene.widthProperty().addListener((obs, oldVal, newVal) -> sceneWidth = newVal.doubleValue());
        // Adds a listener to the scene's width property. Whenever the width changes, sceneWidth is updated to the new width.
        scene.heightProperty().addListener((obs, oldVal, newVal) -> sceneHeight = newVal.doubleValue());
        // Adds a listener to the scene's height property. Whenever the height changes, sceneHeight is updated to the new height.

        // Initialize scene dimensions to current width and height
        sceneWidth = scene.getWidth();
        sceneHeight = scene.getHeight();

        // Enable mouse dragging for panning
        scene.setOnMousePressed(this::startDrag); // event handler for mouse press
        scene.setOnMouseReleased(e -> scene.setCursor(Cursor.DEFAULT)); // event handler for mouse release
        scene.setOnMouseDragged(this::drag); // event handler for mouse drag
        //  updates the view's translation based on the mouse movement, allowing the user to pan or move the view around.

        primaryStage.setScene(scene); // Set the scene to the stage, assigns the previously created Scene to primaryStage.
        // Primary stage will display the content defined in the Scene (StackPane that contains the drawingPane and borderPane)
        primaryStage.show(); // Display the stage


        // The Stage class represents the main window of a JavaFX application. It is the top-level container that holds everything you see on the screen.

        // The Scene class represents the content and layout of a Stage. It holds all the graphical elements and controls that you want to display.
        // We create and configure a Scene to define the layout and appearance of your application. This includes setting the root node (e.g., StackPane, BorderPane) and adding child nodes to it.

        // Stage Contains a Scene: The Stage serves as the main window that holds a single Scene. Each Stage can have only one Scene at a time, but a Scene can be switched out if needed (e.g., to show different content).


    }

    // Determine whether to use parallel computation
    private boolean shouldUseParallel() {
        // Ask the user in what mode should the application run
        System.out.println("Would you like to run the computation in parallel or sequentially?");
        System.out.println("Enter 'p' for parallel or 's' for sequential:");

        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();

        // Check the response
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


    // compute the positions and sizes of white rectangles
    // it breaks down a larger square into smaller squares and adds a white rectangle in the middle of each division


    // Define the GasketTask class
    class GasketTask extends RecursiveTask<Void> {
        private final int level;
        private final List<Rectangle> rectangles;
        private final int x, y, size;

        // Constructor to initialize the task
        public GasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
            this.level = level;
            this.rectangles = rectangles;
            this.x = x;
            this.y = y;
            this.size = size;
        }

        // The compute method contains the logic for the Sierpinski Carpet
        @Override
        protected Void compute() {
            // Base case: if level is 0, stop the recursion
            if (level > 0) {

                int sub = size / 3; // calculate the size of each smaller sub-square by dividing the current square size by 3

                // create a white rectangle at the center of the current square
                Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1); // position at one-third of the current square width and height, the size is smaller than the sub-square
                box.setFill(Color.WHITE);
                synchronized (rectangles) {
                    rectangles.add(box); // add the white rectangle to the list
                }

                int newLevel = level - 1; // decrement the level of recursion

                // Recursively compute the white rectangles for the 8 sub-squares
                GasketTask topLeft = new GasketTask(newLevel, rectangles, x, y, sub); // top-left square
                GasketTask topCenter = new GasketTask(newLevel, rectangles, x + sub, y, sub); // top-center square
                GasketTask topRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y, sub); // top-right square
                GasketTask middleLeft = new GasketTask(newLevel, rectangles, x, y + sub, sub); // middle-left square

                // Skip the middle-middle sub-square (since it is already filled with a white rectangle).

                GasketTask middleRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub); // middle-right square
                GasketTask bottomLeft = new GasketTask(newLevel, rectangles, x, y + 2 * sub, sub); // bottom-left square
                GasketTask bottomCenter = new GasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub); // bottom-center square
                GasketTask bottomRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub); // bottom-right square

                // Invoke all tasks in parallel
                invokeAll(topLeft, topCenter, topRight, middleLeft, middleRight, bottomLeft, bottomCenter, bottomRight);
            }
            return null;
        }
    }

    private void computeGasketParallel(int level, List<Rectangle> rectangles, int x, int y, int size) {
        ForkJoinPool pool = new ForkJoinPool();  // Create a ForkJoinPool
        GasketTask task = new GasketTask(level, rectangles, x, y, size);  // Create the root task
        pool.invoke(task);  // Start the parallel computation
    }


    // method adjusts the scale of the drawing pane based on a zoom factor
    private void zoom(double factor) {
        // retrieve the current horizontal and vertical scale factor and multiply it by the zoom factor
        scaleTransform.setX(scaleTransform.getX() * factor);
        scaleTransform.setY(scaleTransform.getY() * factor);

        // if factor is greater than 1, the zoom is in
        // if factor is less than 1, the zoom is out

    }

    // method resets the scale and position of the drawing pane to the default values
    private void resetView(double sceneWidth, double sceneHeight) {
        //set horizontal and vertical scale to 1
        scaleTransform.setX(1);
        scaleTransform.setY(1);
        // calculate the distances needed to center the drawing pane
        translateTransform.setX((sceneWidth - DEFAULT_SIZE) / 2 - DEFAULT_X); // center the drawing pane horizontally, minus the already existing offset
        translateTransform.setY((sceneHeight - DEFAULT_SIZE) / 2 - DEFAULT_Y);
    }


    // method initializes the drag operation
    private void startDrag(MouseEvent event) {
        // capture the x and y coordinates of the mouse pointer in the scene when the drag starts this will be the initial position
        initialX = event.getSceneX();
        initialY = event.getSceneY();

        //store the x and y coordinates of the current translation of the view, this will be the starting point of the drag operation
        startX = translateTransform.getX();
        startY = translateTransform.getY();

        ((Scene) event.getSource()).setCursor(Cursor.CLOSED_HAND); // Set cursor to hand when dragging starts
    }


    // method updates the view's translation based on the mouse movement during the drag operation
    private void drag(MouseEvent event) {
        // calculate the x and y offset of the mouse pointer from the initial position
        double offsetX = event.getSceneX() - initialX;
        double offsetY = event.getSceneY() - initialY;

        // update the translation of the view based on the calculated offset
        translateTransform.setX(startX + offsetX);
        translateTransform.setY(startY + offsetY);
    }

    public static void main(String[] args) {
        launch(); // Launch the JavaFX application
    }
}
