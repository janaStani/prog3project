package com.example.prog3proj1;

/*
// subclass of RecursiveTask which is part of the Fork/Join framework
class GasketTask extends RecursiveTask<Void> {
    private final int level;
    private final List<Rectangle> rectangles;
    private final int x, y, size;

    // constructor to initialize the task
    public GasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
        this.level = level;
        this.rectangles = rectangles;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    // this method contains the logic for recursively creating the Sierpinski Carpet
    // compute the positions and sizes of white rectangles
    // it breaks down a larger square into smaller squares and adds a white rectangle in the middle of each division
    @Override
    protected Void compute() {
        // base case: if level is 0, stop the recursion
        if (level > 0) {

            int sub = size / 3; // calculate the size of each smaller sub-square by dividing the current square size by 3

            // create a white rectangle at the center of the current square
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1); // position at one-third of the current square width and height, the size is smaller than the sub-square
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box); // add the white rectangle to the list
            }

            int newLevel = level - 1; // decrement the level of recursion

            // recursively compute the white rectangles for the 8 sub-squares
            GasketTask topLeft = new GasketTask(newLevel, rectangles, x, y, sub); // top-left square
            GasketTask topCenter = new GasketTask(newLevel, rectangles, x + sub, y, sub); // top-center square
            GasketTask topRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y, sub); // top-right square
            GasketTask middleLeft = new GasketTask(newLevel, rectangles, x, y + sub, sub); // middle-left square

            // skip the middle-middle sub-square (since it is already filled with a white rectangle).

            GasketTask middleRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub); // middle-right square
            GasketTask bottomLeft = new GasketTask(newLevel, rectangles, x, y + 2 * sub, sub); // bottom-left square
            GasketTask bottomCenter = new GasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub); // bottom-center square
            GasketTask bottomRight = new GasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub); // bottom-right square

            // invoke all tasks in parallel
            invokeAll(topLeft, topCenter, topRight, middleLeft, middleRight, bottomLeft, bottomCenter, bottomRight);
        }
        return null;
        // RecursiveTask is of type void doesn't return any value
        // the task's purpose is to modify the rectangles list
    }
}*/

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.concurrent.RecursiveTask;

class GasketTask extends RecursiveTask<Void> {
    private static final int THRESHOLD = 3;
    private final int level;
    private final List<Rectangle> rectangles;
    private final int x, y, size;

    public GasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
        this.level = level;
        this.rectangles = rectangles;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    protected Void compute() {
        System.out.println("Task level: " + level + " at position (" + x + ", " + y + ") with size " + size);

        if (level <= THRESHOLD) {
            computeSequentially();
        } else {
            int sub = size / 3;
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1);
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box);
            }

            int newLevel = level - 1;

            invokeAll(
                    new GasketTask(newLevel, rectangles, x, y, sub),
                    new GasketTask(newLevel, rectangles, x + sub, y, sub),
                    new GasketTask(newLevel, rectangles, x + 2 * sub, y, sub),
                    new GasketTask(newLevel, rectangles, x, y + sub, sub),
                    new GasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub),
                    new GasketTask(newLevel, rectangles, x, y + 2 * sub, sub),
                    new GasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub),
                    new GasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub)
            );
        }
        return null;
    }

    private void computeSequentially() {
        if (level > 0) {
            int sub = size / 3;
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1);
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box);
            }

            int newLevel = level - 1;

            new GasketTask(newLevel, rectangles, x, y, sub).compute();
            new GasketTask(newLevel, rectangles, x + sub, y, sub).compute();
            new GasketTask(newLevel, rectangles, x + 2 * sub, y, sub).compute();
            new GasketTask(newLevel, rectangles, x, y + sub, sub).compute();
            new GasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub).compute();
            new GasketTask(newLevel, rectangles, x, y + 2 * sub, sub).compute();
            new GasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub).compute();
            new GasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub).compute();
        }
    }
}