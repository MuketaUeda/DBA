package pr2mapAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Map {
    int height;
    int width;
    File file;
    int[][] matrix;

    Map (String filename) {
        file = new File(filename);
        try {
            setMap();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMap() throws FileNotFoundException {
        Scanner scan = new Scanner(file);

        height = scan.nextInt();
        width = scan.nextInt();

        matrix = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = scan.nextInt();
            }
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void printMap() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (matrix[i][j] == 0) {
                    System.out.print(" ");
                }
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}
