package com.example.prog3proj1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class SequentialGasketTask {
    private final int level;
    private final List<Rectangle> rectangles;
    private final int x, y, size;

    public SequentialGasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
        this.level = level;
        this.rectangles = rectangles;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void compute() {
        System.out.println("Sequential computation running on thread: " + Thread.currentThread().getName());

        if (level > 0) {
            int sub = size / 3;
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1);
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box);
            }

            int newLevel = level - 1;

            new SequentialGasketTask(newLevel, rectangles, x, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + sub, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x, y + sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y + sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x, y + 2 * sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + sub, y + 2 * sub, sub).compute();
            new SequentialGasketTask(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub).compute();
        }
    }
}
