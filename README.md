# L-Shape Tromino Tiling Project

##  Overview
This project implements the **L-Shape Tromino Tiling Problem** using a **Divide and Conquer recursive algorithm**.  
Given a 2^n × 2^n board with exactly one missing (defective) cell, the algorithm tiles the remaining cells using L-shaped trominoes without overlap or gaps.

The program includes input validation, performance measurement, and clear console-based output.

---

##  Algorithm Description
- **Base Case:**  
  When the board size is **2×2**, a single L-shaped tromino is placed to cover the three non-defective cells.

- **Recursive Case:**  
  For larger boards:
  - The board is divided into **four equal quadrants**
  - A central L-tromino is placed to create artificial defects in three quadrants
  - Each quadrant is solved recursively

This approach guarantees correctness for all board sizes of the form 2^n.

---

## Features
- Recursive Divide & Conquer solution  
- Multiple test runs for different values of n  
- Input validation (invalid board sizes or defect coordinates are rejected)  
- Optional step-by-step logging of tromino placement  
- Neatly formatted board output  
- Execution time measurement  
- Approximate memory usage reporting  

---

## How to Run
1. Open the project in **IntelliJ IDEA** or any Java IDE  
2. Run `Main.java`  
3. Follow the console prompts:
   - Enter the value of n
   - Enter the defect row and column
   - Choose whether to enable step logging
   - Choose whether to print the final board

---

## Complexity Analysis

### Time Complexity
O(4^n) ≡ O(N^2), where N = 2^n  

Each cell in the board is processed exactly once.

### Space Complexity
- O(N^2) for storing the board  
- O(log N) for recursion depth  

---

##  Robustness
- Invalid input values are handled gracefully  
- Out-of-range defect coordinates are rejected  
- The program prevents crashes and infinite recursion  

---

##  Project Structure
```
LShapeTiling/
 └── src/
     └── Main.java
```

---

## Platform
- Developed and tested on macOS
- Console-based application (no GUI)

---

## Author
Favour Okwudili
