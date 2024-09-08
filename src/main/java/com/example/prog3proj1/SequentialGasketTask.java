package com.example.prog3proj1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class SequentialGasketTask {
    private final int level;     // recursion depth
    private final List<Rectangle> rectangles;  // list to store the white rectangles
    private final int x, y, size;  // position and size of the current square

    // constructor
    public SequentialGasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
        this.level = level;
        this.rectangles = rectangles;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    // method to compute the positions and sizes of white rectangles
    public void compute() {
        //System.out.println("Sequential computation running on thread: " + Thread.currentThread().getName());

        if (level > 0) {

            int sub = size / 3;   // size of sub-grid

            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1); // create white rectangle at center of current square
            box.setFill(Color.WHITE); // set color to white

            synchronized (rectangles) {
                rectangles.add(box);  // add white rectangle to list
            }

            int newLevel = level - 1;   // decrease level

            // recursively compute white rectangles for 8 sub-grids
            new SequentialGasketTask(newLevel, rectangles, x, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + sub, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x, y + sub, sub).compute();

            // skip middle-middle sub-grid, white rectangle already added

            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x, y + 2 * sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub).compute();
        }
    }
}
