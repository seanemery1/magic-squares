import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class Magic {
    private static Logger logger
            = Logger.getLogger(
            Magic.class.getName());
    private static int count;
    private ArrayList<ArrayList<Byte>> summands;


    Magic() {

    }

    // Initializes a combination tree of all possible row combinations of n elements that when
    // combined might produce a valid magic square
    Magic(Node root) {
        count = 0; // count of number of unique magic squares
        this.summands = deepCopyInt(root.leaves); // deep copy of all summands that sum to n[(n^2+1)/2]

        // Looping through all summands. For example, a 3x3 magic square has the following summands:
        //  1 + 5 + 9 = 15
        //  1 + 6 + 8 = 15
        //  2 + 4 + 9 = 15
        // etc...
        for (ArrayList<Byte> bytes : summands) {
            // Find all permutations of summands to populate the first row of a magic square:
            List<List<Byte>> firstRowsPerm = listPermutations(bytes);

            // Cuts the list of permutations in half. For example:
            // [[9, 5, 1], [5, 9, 1], [5, 1, 9], [9, 1, 5], [1, 9, 5], [1, 5, 9]]
            // gets reduced to:
            // [[9, 5, 1], [5, 9, 1], [5, 1, 9]]
            firstRowsPerm.subList(firstRowsPerm.size() / 2, firstRowsPerm.size()).clear();

            // Creating new deep copies of magic square array and list of summands
            ArrayList<ArrayList<Byte>> summandsClone = deepCopyInt(root.leaves);
            byte[] magic = new byte[Node.n2];
            if (!firstRowsPerm.get(0).isEmpty()) { // Empty list error check
                for (byte i = 0; i < Node.lengthMax; i++) {
                    magic[i] = firstRowsPerm.get(0).get(i); // Populating first row of magic square
                }
                // Removes all summands that share common elements with magic square
                filter(magic, summandsClone);
            }
            // Looping through each permutation and populating the magic square
            for (List<Byte> permutationRow : firstRowsPerm) {
                if (!permutationRow.isEmpty()) {
                    for (int j = 0; j < Node.lengthMax; j++) {
                        // Repopulating first row of magic square with new permutation of summands
                        magic[j] = permutationRow.get(j);
                    }
                    // Creating a new magic square branch with deep copies
                    new Magic(magic.clone(),
                        summandsClone, (byte) 1);
                }
            }
        }
        System.out.println("total squares:" + count); // Shows final count of unique magic squares
    }

    // Populates the remaining potential magic square beyond the first row
    // Extremely similar to previous Magic(Node root) constructor above
    private Magic(byte[] magic, ArrayList<ArrayList<Byte>> summands, byte rowIndex) {
        int n = Node.lengthMax; // Easy reference to Node.lengthMax
        this.summands = deepCopy(summands); // Deep copy of all summands that sum to n[(n^2+1)/2]
        for (ArrayList<Byte> bytes : this.summands) {
            List<List<Byte>> nextRowsPerm = listPermutations(bytes); // Find all permutations of summands
            ArrayList<ArrayList<Byte>> summandsClone = deepCopy(summands);
            if (!nextRowsPerm.get(0).isEmpty()) {
                // Looping through the index range of a 1D array as if it was a 2D array
                // For example: 2nd row of 3x3 array -> indexes 3, 4, 5
                //      2D array:   0, 1, 2,        1D array:
                //                  3*, 4*, 5*,    ->      0, 1, 2, 3*, 4*, 5*, 6, 7, 8
                //                  6, 7, 8
                for (byte i = (byte) (n * rowIndex); i < n * (rowIndex + 1); i++) {
                    magic[i] = nextRowsPerm.get(0).get(i - n * rowIndex);
                }
                if (rowIndex < n - 1) {
                    // Removes all summands that share common elements with magic square
                    filter(magic, summandsClone);
                }
            }
            for (List<Byte> permutationRow : nextRowsPerm) {
                if (!permutationRow.isEmpty()) {
                    // Looping through the index range of a 1D array as if it was a 2D array
                    for (byte j = (byte) (n * rowIndex); j < n * (rowIndex + 1); j++) {
                        magic[j] = permutationRow.get(j - n * rowIndex);
                    }
                    if (rowIndex < n - 1) { // If not the last row/square candidate is incomplete
                        new Magic(magic.clone(), summandsClone, (byte) (rowIndex + 1));
                    } else if (checkIfMagicFast(magic)) { // If square candidate = valid magic square
                        displayMagic2WaysArrayFaster(magic); // Display magic square and its mirror
                    }
                }
            }
        }
    }

    // Creates a deep copy of ArrayList<ArrayList<Integer>>
    private ArrayList<ArrayList<Byte>> deepCopyInt(ArrayList<ArrayList<Integer>> input) {
        ArrayList<ArrayList<Byte>> summands = new ArrayList<>();
        for (ArrayList<Integer> leaf : input) {
            ArrayList<Byte> clone = new ArrayList<>();
            for (int i : leaf) {
                clone.add((byte)i);
            }
            summands.add(clone);
        }
        return summands;
    }

    // Creates a deep copy of ArrayList<ArrayList<Byte>>
    private ArrayList<ArrayList<Byte>> deepCopy(ArrayList<ArrayList<Byte>> input) {
        ArrayList<ArrayList<Byte>> summands = new ArrayList<>();
        for (ArrayList<Byte> leaf : input) {
            ArrayList<Byte> clone = new ArrayList<>();
            for (int i : leaf) {
                clone.add((byte)i);
            }
            summands.add(clone);
        }
        return summands;
    }

    // Recursive method to find all permutations of a sequence of numbers in a List
    // ie. {1, 5, 9} - > {1, 5, 9}, {1, 9, 5}, {5, 1, 9}, {5, 9, 1}, {1, 5, 9}, {1, 5, 9}
    private static List<List<Byte>> listPermutations(List<Byte> list) {
        if (list.isEmpty()) { // Base case: if list is empty, return empty
            List<List<Byte>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        } else { // Inductive: successively remove elements, then re-add them at different indexes
            Byte firstElement = list.remove(0);
            List<List<Byte>> returnMe = new ArrayList<>();
            List<List<Byte>> permutations = listPermutations(list);
            for (List<Byte> subsetPerm : permutations) {
                for (int index = 0; index <= subsetPerm.size(); index++) {
                    List<Byte> temp = new ArrayList<>(subsetPerm);
                    temp.add(index, firstElement);
                    returnMe.add(temp);
                }
            }
            return returnMe;
        }
    }

    // Checks if magic square candidate is a valid magic square
    static boolean checkIfMagicFast(byte[] magic) {
        // Initializing SumChecks + increments
        byte n = Node.lengthMax; // Easy reference to Node.lengthMax
        byte summand = (byte) (n * (n*n + 1) / 2); // total sum of each row/column/diagonal
        byte[] sumCheckCols = new byte[n];
        Arrays.fill(sumCheckCols, summand);
        byte sumCheckDiag1 = summand;
        byte sumCheckDiag2 = summand;

        // Initializing offSet to interpret a 1D array as 2D array
        byte offSet = 0;

        // Looping through Magic square to verify sums of columns/diagonals are valid
        for (byte row = 0; row < n*n; row++) {
            if (row % n == 0) {
                // Offset formulas used to check sum of diagonals in a single pass-through
                sumCheckDiag1 -= magic[n + row - offSet - 1];
                sumCheckDiag2 -= magic[n*n  -row - offSet - 1];
                offSet++;
            }

            // Checking the sum of each column in a single pass-through
            sumCheckCols[row % n] -= magic[row];

            // If sum of column/diagonal exceeds (n * (n * n + 1) / 2), magic square is invalid
            if (sumCheckCols[row % n] < 0 || sumCheckDiag1 < 0 || sumCheckDiag2 < 0) {
                return false;
            }
        }
        // Verifying that all columns/diagonal sum up to n[(n^2+1)/2]
        if (sumCheckDiag1 == 0 && sumCheckDiag2 == 0) {
            for (byte i = 0; i < n; i++) {
                if (sumCheckCols[i] != 0) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true; // If the candidate square passes all tests, then it is a valid magic square
    }

    // Removes all row combinations that shares any common numbers in the magic square
    private static void filter(byte[] magic, ArrayList<ArrayList<Byte>> summands) {
        // Initializing and populating list of elements that have already been used
        // in the magic square (to remove from the list of possible row combinations in summands)
        List<Byte> list = new ArrayList<>();
        for(byte b : magic) {
            list.add(b);
        }

        // In reversed ArrayList order, remove all list of elements that have been used
        // in the magic square
        for (int i = summands.size() - 1; i >= 0; i--) {
            summands.get(i).removeAll(list);
            // If any elements were deleted, remove entire row combination
            if (summands.get(i).size() < Node.lengthMax) {
                summands.remove(i);
            }
        }
    }

    // Debugging tool/magic square uniqueness verification
    // Can be turned off
    static void displayMagic2WaysArrayFaster(byte[] magic) {
        // Initializing display output
        StringBuilder original = new StringBuilder();
        StringBuilder reverse = new StringBuilder();
        byte offset = 0;

        for (byte row = 0; row < Node.n2; row++) {
            // For every n numbers in the 1D array, we add a line break to represent a new row
            if (row % Node.lengthMax == 0) {
                original.append("\n");
                reverse.append("\n");
                offset = 0;
            }

            // Original magic square gets appended in sequential order
            original.append(magic[row]);
            original.append(" ");

            // Reversed magic square gets appended in reversed row order
            reverse.append(magic[row + Node.lengthMax - 1 - offset]);
            reverse.append(" ");

            offset += 2; // row (+1 each iteration) - offset (+2) = net -1 each iteration
        }
        // Displaying log statements for both magic squares and running total count
        logger.log(Level.INFO, "Original: {0} ", original);
        logger.log(Level.INFO, "Reversed: {0} ", reverse);
        count += 2;
        logger.log(Level.INFO, "Total Magic Square count: {0} ", String.valueOf(count));
    }
}


