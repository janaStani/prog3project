package com.example.prog3proj1;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Scale; // to change zoom level
import javafx.scene.transform.Translate; // to change position of the view

// holds the logic for handling the view transformations
public class ViewController {

    // fields to store the scale and translation transformations
    public Scale scaleTransform;
    public Translate translateTransform;

    // store initial coordinates of mouse pointer and view translation for drag operation
    private double initialX, initialY;
    private double startX, startY;

    // constructor
    public ViewController(Scale scaleTransform, Translate translateTransform) {
        this.scaleTransform = scaleTransform;
        this.translateTransform = translateTransform;
    }

    // method adjusts the scale of the drawing pane based on a zoom factor
    public void zoom(double factor) {
        // retrieve the current horizontal and vertical scale factor and multiply it by the zoom factor
        scaleTransform.setX(scaleTransform.getX() * factor);
        scaleTransform.setY(scaleTransform.getY() * factor);

        // if factor is greater than 1, the zoom is in
        // if factor is less than 1, the zoom is out

    }

    // method resets the scale and position of the drawing pane to the default values
    public void resetView(double sceneWidth, double sceneHeight) {
        // set horizontal and vertical scale to 1
        scaleTransform.setX(1);
        scaleTransform.setY(1);
        // calculate the distances needed to center the drawing pane
        translateTransform.setX((sceneWidth - HelloApplication.DEFAULT_SIZE) / 2 - HelloApplication.DEFAULT_X); // center the drawing pane horizontally, minus the already existing offset
        translateTransform.setY((sceneHeight - HelloApplication.DEFAULT_SIZE) / 2 - HelloApplication.DEFAULT_Y);
    }



    // method initializes the drag operation
    public void startDrag(MouseEvent event) {
        // get the x and y coordinates of the mouse pointer in the scene when the drag starts this will be the initial position
        initialX = event.getSceneX();
        initialY = event.getSceneY();

        // store the x and y coordinates of the current translation of the view, this will be the starting point of the drag operation
        startX = translateTransform.getX();
        startY = translateTransform.getY();

        ((Scene) event.getSource()).setCursor(Cursor.CLOSED_HAND); // Set cursor to hand when dragging starts
    }


    // method updates the view's translation based on the mouse movement during the drag operation
    public void drag(MouseEvent event) {
        // calculate the x and y offset of the mouse pointer from the initial position
        double offsetX = event.getSceneX() - initialX;
        double offsetY = event.getSceneY() - initialY;

        // update the translation of the view based on the calculated offset
        translateTransform.setX(startX + offsetX);
        translateTransform.setY(startY + offsetY);
    }


}

