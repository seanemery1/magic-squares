import java.util.Date;

public class tester {


    public static void testCasesDebug() {
        byte n = 3;
        Node.newParam(n);
        byte [] testMagicSquare1D =
            {8, 1, 6,
            3, 5, 7,
            4, 9, 2};
        byte [] testMagicSquare1D2 =
            {9, 1, 5,
            2, 6, 7,
            4, 8, 3};
        System.out.println("3*3: " + Magic.checkIfMagicFast(testMagicSquare1D));
        System.out.println("3*3: " + Magic.checkIfMagicFast(testMagicSquare1D2));
        Magic.displayMagic2WaysArrayFaster(testMagicSquare1D);

        byte[] testNotMagicSquare1 =
            {3, 5, 7,
            8, 1, 6,
            4, 9, 2};
        System.out.println("3*3: " + Magic.checkIfMagicFast(testNotMagicSquare1));

        n = 4;
        Node.newParam(n);
        byte[] testMagicSquare2 =
            {12, 6, 15, 1,
            13, 3, 10, 8,
            2, 16, 5, 11,
            7, 9, 4, 14};
        System.out.println("4*4: " + Magic.checkIfMagicFast(testMagicSquare2));
        byte[] testNotMagicSquare2 =
            {13, 3, 10, 8,
            12, 6, 15, 1,
            2, 16, 5, 11,
            7, 9, 4, 14};
        System.out.println("4*4: " + Magic.checkIfMagicFast(testNotMagicSquare2));
        byte[] testNotMagicSquare3 =
            {8, 7, 9, 10,
            1, 2, 15, 16,
            3, 4, 13, 14,
            5, 6, 11, 22};
        System.out.println("4*4: " + Magic.checkIfMagicFast(testNotMagicSquare3));
    }

    public static void main(String[] args) {
        //testCasesDebug();
        Date start = new Date();

        byte n = 4;
        Node.newParam(n);
        Node root = new Node(n);

        Date start2 = new Date();
        System.out.println("All summands found. Time: " + (start2.getTime() - start.getTime()) + " msec");
        new Magic(root);
        Date end = new Date();
        System.out.println("Total time: " + (end.getTime() - start2.getTime()) + " msec");


    }
}



