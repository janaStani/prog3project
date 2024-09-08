package com.example.prog3proj1;

import javafx.scene.shape.Rectangle;
import mpi.MPI;

import java.util.ArrayList;
import java.util.List;

public class DistributiveGasketRunner {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        System.out.println("Process " + rank + " of " + size + " starting computation.");

        // Create an instance of DistributiveGasketTask and perform computation
        List<Rectangle> rectangles = new ArrayList<>();
        DistributiveGasketTask task = new DistributiveGasketTask(rectangles, HelloApplication.DEFAULT_X, HelloApplication.DEFAULT_Y);
        task.compute();

        MPI.Finalize();
    }
}
