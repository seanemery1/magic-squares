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
    private ArrayList<ArrayList<Byte>> remain;


    Magic() {

    }


    private Magic(byte[] magic, ArrayList<ArrayList<Byte>> remain, byte rowIndex) {
        int n = Node.lengthMax;
        this.remain = deepCopy(remain);
        for (ArrayList<Byte> bytes : this.remain) {
            List<List<Byte>> nextRows = listPermutations(bytes);
            ArrayList<ArrayList<Byte>> remainClone = deepCopy(remain);
            if (!nextRows.get(0).isEmpty()) {
                for (byte i = (byte) (n * rowIndex); i < n * (rowIndex + 1); i++) {
                    magic[i] = nextRows.get(0).get(i - n * rowIndex);
                }
                if (rowIndex < n - 1) {
                    filter(magic, remainClone);
                }
            }
            for (List<Byte> nextRow : nextRows) {
                if (!nextRow.isEmpty()) {
                    for (byte j = (byte) (n * rowIndex); j < n * (rowIndex + 1); j++) {
                        magic[j] = nextRow.get(j - n * rowIndex);
                    }
                    if (rowIndex < n - 1) {
                        new Magic(magic.clone(), remainClone, (byte) (rowIndex + 1));
                    } else if (checkIfMagicFast(magic)) {
                        displayMagic2WaysArrayFaster(magic);
                    }
                }
            }
        }
    }

    // Creates a deep copy of ArrayList<ArrayList<Integer>>
    private ArrayList<ArrayList<Byte>> deepCopyInt(ArrayList<ArrayList<Integer>> input) {
        ArrayList<ArrayList<Byte>> remain = new ArrayList<>();
        for (ArrayList<Integer> leaf : input) {
            ArrayList<Byte> clone = new ArrayList<>();
            for (int i : leaf) {
                clone.add((byte)i);
            }
            remain.add(clone);
        }
        return remain;
    }

    // Creates a deep copy of ArrayList<ArrayList<Byte>>
    private ArrayList<ArrayList<Byte>> deepCopy(ArrayList<ArrayList<Byte>> input) {
        ArrayList<ArrayList<Byte>> remain = new ArrayList<>();
        for (ArrayList<Byte> leaf : input) {
            ArrayList<Byte> clone = new ArrayList<>();
            for (int i : leaf) {
                clone.add((byte)i);
            }
            remain.add(clone);
        }
        return remain;
    }

    
    Magic(Node root) {
        count = 0;
        this.remain = deepCopyInt(root.leaves);

        for (ArrayList<Byte> bytes : remain) {
            List<List<Byte>> firstRows = listPermutations(bytes);
            firstRows.subList(firstRows.size() / 2, firstRows.size()).clear();
            ArrayList<ArrayList<Byte>> remainClone = deepCopyInt(root.leaves);
            byte[] magic = new byte[Node.n2];
            if (!firstRows.get(0).isEmpty()) {
                for (byte i = 0; i < Node.lengthMax; i++) {
                    magic[i] = firstRows.get(0).get(i);
                }
                filter(magic, remainClone);
            }
            for (List<Byte> firstRow : firstRows) {
                if (!firstRow.isEmpty()) {
                    for (int j = 0; j < Node.lengthMax; j++) {
                        magic[j] = firstRow.get(j);
                    }
                    new Magic(magic.clone(),
                        remainClone, (byte) 1);
                }
            }
        }
        System.out.println("total squares:" + count);
    }

    // Recursive method to find all permutations of a sequence of numbers in a List
    private static List<List<Byte>> listPermutations(List<Byte> list) {
        // Base case/list is empty, return empty
        if (list.size() == 0) {
            List<List<Byte>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        } else {
            List<List<Byte>> returnMe = new ArrayList<>();
            Byte firstElement = list.remove(0);
            List<List<Byte>> recursiveReturn = listPermutations(list);
            for (List<Byte> li : recursiveReturn) {
                for (int index = 0; index <= li.size(); index++) {
                    List<Byte> temp = new ArrayList<>(li);
                    temp.add(index, firstElement);
                    returnMe.add(temp);
                }
            }
            return returnMe;
        }
    }


    static boolean checkIfMagicFast(byte[] magic) {
        // Initializing SumChecks + increments
        byte n = Node.lengthMax;
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
        // Verifying that all columns/diagonal sum up to (n * (n * n + 1) / 2)
        if (sumCheckDiag1 == 0 && sumCheckDiag2 == 0) {
            for (byte i = 0; i < n; i++) {
                if (sumCheckCols[i] != 0) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    // Removes all row combinations that shares any common numbers in the magic square
    private static void filter(byte[] magic, ArrayList<ArrayList<Byte>> remain) {
        // Initializing and populating list of elements that have already been used
        // in the magic square (to remove from the list of possible row combinations in remain)
        List<Byte> list = new ArrayList<>();
        for(byte b : magic) {
            list.add(b);
        }
        // In reversed ArrayList order, remove all elements that have already been used
        // in the magic square
        for (int i = remain.size() - 1; i >= 0; i--) {
            remain.get(i).removeAll(list);
            // If any elements were deleted, remove entire row combination
            if (remain.get(i).size() < Node.lengthMax) {
                remain.remove(i);
            }
        }
    }

    // Debugging tool/magic square uniqueness verification
    // Can be turned off
    static void displayMagic2WaysArrayFaster(byte[] magic) {
        // Initializing display
        StringBuilder original = new StringBuilder();
        StringBuilder reverse = new StringBuilder();
        byte offset = 0;

        for (byte row = 0; row < Node.n2; row++) {
            // For every n numbers in the 1D array, we add a line break to represent rows
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


