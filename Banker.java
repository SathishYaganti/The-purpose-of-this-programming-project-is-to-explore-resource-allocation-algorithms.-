//********************************************************************
// Manideep Mukkala C Programmer
// Operating Systems
// Project #4: Implementation of Banker's Algorithm
// July 06,2025
// Instructor: Dr. Siming Liu
//********************************************************************

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Banker {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java banker <input_file>");
            return;
        }
   try {
            Scanner scanner =new Scanner(new File(args[0]));
          int t=scanner.nextInt();  
            int k=scanner.nextInt();  

            int[][] allocation =new int[t][k];
            int[][] max =new int[t][k];
            int[] available =new int[k];
            int[] request =new int[k];
            int requestProcess;

            // Reading Allocation matrix
            for (int z=0; z<t; z++) {
                for (int j=0; j<k;j++) {
                    allocation[z][j] =scanner.nextInt();
                }
            }

            // Reading Max matrix
            for (int z= 0; z< t; z++) {
                for (int j=0; j<k; j++) {
                    max[z][j] = scanner.nextInt();
                }
            }

            // Reading Available vector
            for (int z=0; z<k; z++) {
                available[z]=scanner.nextInt();
            }

            // Reading request vector
            requestProcess = scanner.nextInt();
            for (int z=0; z<k; z++) {
                request[z] = scanner.nextInt();
            }

            scanner.close();

            int[][] need = new int[t][k];
            for (int z=0; z<t;z++) {
                for (int j = 0; j <k; j++) {
                    need[z][j] = max[z][j] - allocation[z][j];
                }
            }

            System.out.println("There are " +t+ " processes in the system.");
            System.out.println("There are " +k+ " resource types.");
            printMatrix("The Allocation Matrix is...", allocation,t,k);
            printMatrix("The Max Matrix is...", max, t,k);
            printMatrix("The Need Matrix is...", need, t,k);
            printVector("The Available Vector is...", available,k);

            boolean safe = isSafeState(t,k, allocation, need, available);
            System.out.println("THE SYSTEM IS " + (safe ? "IN A SAFE STATE!" : "NOT IN A SAFE STATE!"));

            System.out.print("The Request Vector is...\n  A B C D\n" + requestProcess + ": ");
            for (int z=0; z<k; z++) {
                System.out.print(request[z] + " ");
            }
            System.out.println();

            boolean canGrant = canRequestBeGranted(k, request, need[requestProcess], available);
            System.out.println("THE REQUEST CAN " + (canGrant ? "BE GRANTED!" : "NOT BE GRANTED!"));

            if (canGrant) {
                for (int z=0; z< k; z++) {
                    available[z] -= request[z];
                    allocation[requestProcess][z]=allocation[requestProcess][z]+ request[z];
                    need[requestProcess][z]=need[requestProcess][z]-request[z];
                }
                printVector("The Available Vector is...", available,k);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error opening file: " + e.getMessage());
        }
    }
//********************************************************************
// Function Name: printMatrix
//
// This function mainly  prints a labeled matrix 
// This also process numbers as row labels and resource types as column labels.
//
// Value Parameters
// title      String         Title of the matrix
// matrix     int[][]        The matrix to print
// n          int            Number of processes (rows)
// m          int            Number of resource types (columns)
//
// Local Variables
// i          int            Row iterator
// j          int            Column iterator
//
//********************************************************************

    private static void printMatrix(String title, int[][] matrix, int n, int m) {
        System.out.println(title);
        System.out.print("   ");
        for (int i=0; i<m; i++) {
            System.out.print((char) ('A' +i) + " ");
        }
        System.out.println();
        for (int i= 0; i< n; i++) {
            System.out.print(i+ ": ");
            for (int j = 0; j < m; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
//********************************************************************
//
// Function Name: printVector
//
// This function mainly prints a labeled vector with
// resource types as label column.
//
// Value Parameters
// ----------------
// title      String         Title of the vector
// vector     int[]          The vector to print
// m          int            Number of resource types (columns)
//
// Local Variables
// ---------------
// i          int            This will iterate the column
//
//********************************************************************

    private static void printVector(String title, int[] vector, int m) {
        System.out.println(title);
        for (int i = 0; i < m; i++) {
            System.out.print((char) ('A' + i) + " "); }
        System.out.println();
        for (int i = 0; i < m; i++) {
            System.out.print(vector[i] + " ");
        }
        System.out.println("\n");
    }
//********************************************************************
// Function Name: isSafeState
//
// This function mainly checks wether the system is in a safe state when using the
// Banker's algorithm. It mainly tries to find a safe sequence in which all processes can complete without causing any deadlock.
//
// Return Value
// ------------
// boolean                   True when the system is in a safe state,
//                           False otherwise.
//
// Value Parameters
// ----------------
// n          int            Number of processes
// m          int              Number of resource types
// allocation int[][]        Current allocation matrix
// need       int[][]         Need matrix
// available  int[]          Available vector
//
// Local Variables
// ---------------
// finish     boolean[]      this will Tracks completion status of processes
// work       int[]          this will Copy of available resources for simulation
// found      boolean       this will Indicates if a process can proceed in the current iteration
//********************************************************************

    private static boolean isSafeState(int n, int m, int[][] allocation, int[][] need, int[] available) {
        boolean[] finish = new boolean[n];
        int[] work = available.clone();

        while (true) {
            boolean found= false;
            for (int i= 0; i < n; i++) {
                if (!finish[i]) {
                    boolean canFinish =true;
                    for (int j=0; j<m; j++) {
                        if (need[i][j]>work[j]) {
                            canFinish=false;
                            break; }}
                    if (canFinish) {
                        for (int j = 0; j < m; j++) {
                            work[j] += allocation[i][j];
                        }
                        finish[i] = true;
                        found = true;}
                }
            }
            if (!found) break;}

        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;}
//********************************************************************
//
// Function Name: canRequestBeGranted
//
// This function mainly checks if a resource request by a process can be
// safely granted based on the current available vectors.
//
// Return Value
// ------------
// boolean                   True if the request can be granted safely
//                            otherwise False.
//
// Value Parameters
// ----------------
// m          int           this is  Number of resource types
// request    int[]          The request vector
// need       int[]          Need vector for the requesting process
// available  int[]          Available vector
//
// Local Variables
// ---------------
// k          int            Resource type iterator
//
//********************************************************************

    private static boolean canRequestBeGranted(int m, int[] request, int[] need, int[] available) {
        for (int k= 0; k< m; k++) {
            if (request[k] > need[k] || request[k] > available[k]) {
                return false;
            }
        }
        return true;}
}
