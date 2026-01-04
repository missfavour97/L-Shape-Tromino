import java.util.Scanner;

public class Main {

    static int tileId = 1;
    static int[][] board;

    // Phase 2 toggles
    static boolean LOG_STEPS = false;     // set true to log each placement step
    static boolean SHOW_FINAL_BOARD = true;

    public static void main(String[] args) {
        System.out.println("L-Shape Tromino Tiling Project ");

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("New Test Run");
            System.out.println("-----------------------------------------");

            // --- Read + validate n ---
            Integer n = readInt(sc, "Enter n (board size will be 2^n). Enter -1 to quit: ");
            if (n == null) continue;
            if (n == -1) {
                System.out.println("Exiting!");
                break;
            }
            if (n < 1) {
                System.out.println("Invalid n. Must be >= 1.");
                continue;
            }

            // Prevent huge memory usage accidentally
            if (n > 10) { // 2^10 = 1024 -> already big
                System.out.println("Warning: n is large (2^" + n + "). This may be slow / memory heavy.");
                System.out.println("Try n <= 10 for safe testing.");
                continue;
            }

            int size = 1 << n; // 2^n

            // --- Read + validate defect coordinates ---
            Integer defectRow = readInt(sc, "Enter defect row (0 to " + (size - 1) + "): ");
            if (defectRow == null) continue;

            Integer defectCol = readInt(sc, "Enter defect col (0 to " + (size - 1) + "): ");
            if (defectCol == null) continue;

            if (!inRange(defectRow, 0, size - 1) || !inRange(defectCol, 0, size - 1)) {
                System.out.println("Invalid defect coordinates. Must be within 0.." + (size - 1));
                continue;
            }

            // --- Optional: log steps? ---
            LOG_STEPS = readYesNo(sc, "Enable step logging? (y/n): ");
            SHOW_FINAL_BOARD = readYesNo(sc, "Print final tiled board? (y/n): ");

            // --- Initialize board for this run ---
            tileId = 1;
            board = new int[size][size];
            board[defectRow][defectCol] = -1;

            System.out.println("\nBoard size: " + size + " x " + size);
            System.out.println("Defect cell: (" + defectRow + ", " + defectCol + ")");

            // --- Performance measurement: time + memory ---
            Runtime rt = Runtime.getRuntime();
            rt.gc();
            long memBefore = usedMemoryBytes(rt);

            long start = System.nanoTime();
            tileBoard(0, 0, defectRow, defectCol, size);
            long end = System.nanoTime();

            long memAfter = usedMemoryBytes(rt);

            // --- Output ---
            if (SHOW_FINAL_BOARD) {
                System.out.println("\nFinal tiled board (-1 = defect):");
                printBoard();
            }

            // --- Report ---
            double ms = (end - start) / 1_000_000.0;
            long memDelta = memAfter - memBefore;

            System.out.println("\n===== Performance Report =====");
            System.out.printf("n = %d, N = 2^n = %d%n", n, size);
            System.out.printf("Execution time: %.3f ms%n", ms);
            System.out.printf("Approx. memory change: %,.2f KB%n", memDelta / 1024.0);

            System.out.println("\n===== Complexity (Theory) =====");
            System.out.println("Time: O(4^n)  (equivalently O(N^2) where N = 2^n)");
            System.out.println("Space: O(N^2) for the board + O(log N) recursion depth");
            System.out.println("Note: recursion depth = n, since size halves each level.");

            // --- Continue? ---
            boolean again = readYesNo(sc, "\nRun another test? (y/n): ");
            if (!again) {
                System.out.println("Done. Bye!");
                break;
            }
        }

        sc.close();
    }

    public static void tileBoard(int topRow, int leftColumn,
                                 int defectRow, int defectColumn,
                                 int size) {

        if (size == 2) {
            int currentId = tileId++;

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    int r = topRow + i;
                    int c = leftColumn + j;

                    if (r == defectRow && c == defectColumn) {
                        continue;
                    }
                    board[r][c] = currentId;
                }
            }

            if (LOG_STEPS) {
                System.out.println("Placed tile " + currentId + " in 2x2 block at (" +
                        topRow + "," + leftColumn + "), defect=(" + defectRow + "," + defectColumn + ")");
            }
            return;
        }

        int half = size / 2;
        int centerRow = topRow + half - 1;
        int centerColumn = leftColumn + half - 1;

        int defectQuadrant;
        if (defectRow <= centerRow && defectColumn <= centerColumn) {
            defectQuadrant = 0; // top-left
        } else if (defectRow <= centerRow && defectColumn > centerColumn) {
            defectQuadrant = 1; // top-right
        } else if (defectRow > centerRow && defectColumn <= centerColumn) {
            defectQuadrant = 2; // bottom-left
        } else {
            defectQuadrant = 3; // bottom-right
        }

        int currentId = tileId++;

        // Place central tromino (3 squares)
        if (defectQuadrant != 0) board[centerRow][centerColumn] = currentId;
        if (defectQuadrant != 1) board[centerRow][centerColumn + 1] = currentId;
        if (defectQuadrant != 2) board[centerRow + 1][centerColumn] = currentId;
        if (defectQuadrant != 3) board[centerRow + 1][centerColumn + 1] = currentId;

        if (LOG_STEPS) {
            System.out.println("Placed center tile " + currentId +
                    " for size " + size + " at center around (" + centerRow + "," + centerColumn + "), defectQuadrant=" + defectQuadrant);
        }

        // Recurse into 4 quadrants
        int tlDefectRow = (defectQuadrant == 0) ? defectRow : centerRow;
        int tlDefectCol = (defectQuadrant == 0) ? defectColumn : centerColumn;
        tileBoard(topRow, leftColumn, tlDefectRow, tlDefectCol, half);

        int trDefectRow = (defectQuadrant == 1) ? defectRow : centerRow;
        int trDefectCol = (defectQuadrant == 1) ? defectColumn : centerColumn + 1;
        tileBoard(topRow, leftColumn + half, trDefectRow, trDefectCol, half);

        int blDefectRow = (defectQuadrant == 2) ? defectRow : centerRow + 1;
        int blDefectCol = (defectQuadrant == 2) ? defectColumn : centerColumn;
        tileBoard(topRow + half, leftColumn, blDefectRow, blDefectCol, half);

        int brDefectRow = (defectQuadrant == 3) ? defectRow : centerRow + 1;
        int brDefectCol = (defectQuadrant == 3) ? defectColumn : centerColumn + 1;
        tileBoard(topRow + half, leftColumn + half, brDefectRow, brDefectCol, half);
    }

    // ------------------------------------------------------------
    // Output helpers
    // ------------------------------------------------------------
    public static void printBoard() {
        int n = board.length;

        // Header row
        System.out.print("    ");
        for (int j = 0; j < n; j++) {
            System.out.printf("%4d", j);
        }
        System.out.println();

        // Separator
        System.out.print("    ");
        for (int j = 0; j < n; j++) {
            System.out.print("----");
        }
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.printf("%2d |", i);
            for (int j = 0; j < n; j++) {
                System.out.printf("%4d", board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    // ------------------------------------------------------------
    // Validation + input helpers
    // ------------------------------------------------------------
    private static boolean inRange(int x, int lo, int hi) {
        return x >= lo && x <= hi;
    }

    private static Integer readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter an integer.");
            return null;
        }
    }

    private static boolean readYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            System.out.println("Please answer y/n.");
        }
    }

    private static long usedMemoryBytes(Runtime rt) {
        return rt.totalMemory() - rt.freeMemory();
    }
}
