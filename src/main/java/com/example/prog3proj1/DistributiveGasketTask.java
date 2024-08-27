package com.example.prog3proj1;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mpi.MPI;

import java.util.List;

public class DistributiveGasketTask {
    private final List<Rectangle> rectangles;
    private final int x;
    private final int y;

    public DistributiveGasketTask(int level, List<Rectangle> rectangles, int x, int y, int size) {
        this.rectangles = rectangles;
        this.x = x;
        this.y = y;
    }

    public void compute() {
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Determine the sub-task for each process
        int subTaskSize = 1;
        int startLevel = rank * subTaskSize;
        int endLevel = (rank + 1) * subTaskSize;

        // Perform the computation for each sub-task
        for (int l = startLevel; l < endLevel; l++) {
            computeGasket(l, rectangles, x, y, size);
        }

        // Gather results from all processes
        gatherResults();
    }

    private void computeGasket(int level, List<Rectangle> rectangles, int x, int y, int size) {
        // Implement the Sierpinski Carpet generation logic here
        if (level > 0) {
            int sub = size / 3;
            Rectangle box = new Rectangle(x + sub, y + sub, sub - 1, sub - 1);
            box.setFill(Color.WHITE);
            synchronized (rectangles) {
                rectangles.add(box);
            }

            int newLevel = level - 1;

            computeGasket(newLevel, rectangles, x, y, sub);
            computeGasket(newLevel, rectangles, x + sub, y, sub);
            computeGasket(newLevel, rectangles, x + 2 * sub, y, sub);
            computeGasket(newLevel, rectangles, x, y + sub, sub);
            computeGasket(newLevel, rectangles, x + 2 * sub, y + sub, sub);
            computeGasket(newLevel, rectangles, x, y + 2 * sub, sub);
            computeGasket(newLevel, rectangles, x + sub, y + 2 * sub, sub);
            computeGasket(newLevel, rectangles, x + 2 * sub, y + 2 * sub, sub);
        }
    }

    private void gatherResults() {
        // Gather the rectangles from all processes
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Here, we could use MPI functions to gather results from all processes
        // For example: MPI.Gather(rectangles, ...);
    }
}
