package com.example.prog3proj1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.concurrent.RecursiveTask;

// subclass of RecursiveTask which is part of the Fork/Join framework
class ParallelGasketTask extends RecursiveTask<Void> {

    private final int level;  // recursion depth
    private final List<Rectangle> rectangles; // list to store the white rectangles
    private final int x, y, size;  // position and size of the current square


    // constructor to initialize the task
    public ParallelGasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
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
        //System.out.println("Thread: " + Thread.currentThread().getName() + " Level: " + level); // Log current thread name and level

        // base case: if level is 0, stop the recursion
        if (level > 0) {

            int sub = size / 3;  // calculate the size of each smaller sub-square by dividing the current square size by 3

            // create a white rectangle at the center of the current square
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1);
            // position at one-third of the current square width and height, the size is smaller than the sub-square
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box);  // add the white rectangle to the list
            }

            int newLevel = level - 1; // decrement the level of recursion


            // recursively compute the white rectangles for the 8 sub-squares, invoking each task in parallel
            invokeAll(
                    new ParallelGasketTask(newLevel, rectangles, x, y, sub), // top-left square
                    new ParallelGasketTask(newLevel, rectangles, x + sub, y, sub), // top-center square
                    new ParallelGasketTask(newLevel, rectangles, x + 2 * sub, y, sub), // top-right square
                    new ParallelGasketTask(newLevel, rectangles, x, y + sub, sub), // middle-left square

                    // skip the middle-middle sub-square (since it is already filled with a white rectangle).

                    new ParallelGasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub), // middle-right square
                    new ParallelGasketTask(newLevel, rectangles, x, y + 2 * sub, sub), // bottom-left square
                    new ParallelGasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub), // bottom-center square
                    new ParallelGasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub) // bottom-right square
            );
        }
        return null;
        // RecursiveTask is of type void doesn't return any value
        // the task's purpose is to modify the rectangles list
    }

}