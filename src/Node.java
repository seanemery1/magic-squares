import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Node {
    private static Logger logger
        = Logger.getLogger(
        Magic.class.getName());
    private boolean root = false;
    private boolean leaf = false;
    private boolean fortify = false;
    private ArrayList<Node> childNodes = new ArrayList<>();

    private byte value;
    private byte length;
    private short currentSum;

    ArrayList<ArrayList<Integer>> leaves;
    private Node parent;

    // Easy class reference for n*n magic square
    static byte lengthMax;
    static byte n2;




    // Initializes defining parameters of a n*n magic square using known properties
    static void newParam(byte n) {
        lengthMax = n;
        n2 = (byte) ((int) (lengthMax) * (int) (lengthMax));


    }

    public Node() {

    }

    private Node(Node parent, byte value, boolean leaf) {
        // Base case, cease if node is a leaf
        if (leaf) {
            // Declaring leaf properties
            this.length = (byte) (parent.length + 1);
            this.leaf = true;
            this.value = value;
            this.parent = parent;
            this.currentSum = (byte) (parent.currentSum + value);
            this.fortify = true;

            StringBuilder str = new StringBuilder(value + "]");
            ArrayList<Integer> row = new ArrayList<>();
            row.add((int) value);

            Node node = parent;
            while (!node.root) {
                str.insert(0, node.value + " ");
                node.fortify = true;
                row.add((int) node.value);
                node = node.parent;
            }
            node.leaves.add(row);
            str.insert(0, "[");
            logger.log(Level.INFO, "logging: {0}", str);
            node.currentSum++;

        } else {
            this.value = value;
            this.parent = parent;
            this.length = (byte) (parent.length + 1);
            if (parent.root) {
                this.currentSum = value;
            } else {
                this.currentSum = (short) (parent.currentSum + value);
            }
            int totalSum = ((int) lengthMax) * (((int) lengthMax) * ((int) lengthMax) + 1) / 2;
            if (this.length < lengthMax && this.value < n2
                && this.currentSum < totalSum - value) {
                if (length == (short) (lengthMax - 2) && (n2 + n2 - 1) < totalSum - currentSum) {

                } else if (lengthMax >= 7 && length == (short) (lengthMax - 3)
                    && 144 < totalSum - currentSum) {
                } else {
                    for (byte i = (byte) (value + 1); i <= n2; i++) {
                        if (this.currentSum == totalSum - i && this.length == lengthMax - 1) {
                            this.childNodes.add(new Node(this, i, true));
                            break;
                        } else {
                            this.childNodes.add(new Node(this, i, false));
                        }
                    }
                }
            }
        }
    }

    public Node(byte n) {
        leaves = new ArrayList<ArrayList<Integer>>();
        value = 0;

        root = true;

        length = 0;
        currentSum = 0;
        parent = null;

        for (byte i = (byte) (value + 1); i < n2; i++) {
            childNodes.add(new Node(this, i, false));
        }

        deleteChildren(this);


    }

    public void deleteChildren(Node node) {
        for (int i = node.childNodes.size() - 1; i >= 0; i--) {
            if (!node.childNodes.get(i).fortify) {
                deleteChildren(node.childNodes.get(i));
                node.childNodes.remove(i);
            } else {
                deleteChildren(node.childNodes.get(i));
            }
        }
    }

    // Debug
    private int leafCount(Node node, int count) {
        // base case
        if (node.leaf) {
            return 1;
        } else {
            int newcount = 0;
            for (int i = 0; i < node.childNodes.size(); i++) {
                newcount += leafCount(node.childNodes.get(i), count);
            }
            return newcount;
        }

    }
}




