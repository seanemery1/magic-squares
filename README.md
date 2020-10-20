## magic-squares
A magic square is a square array of numbers consisting of the distinct positive integers 1, 2, ..., n^2 arranged such that the sum of the n numbers in any horizontal, vertical, or main diagonal line is always the same number. For instance, for a 3x3 Magic Square, every row, column, and diagonal sums to 15:

3x3 Magic Square Example
----------
![alt tag](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Magicsquareexample.svg/180px-Magicsquareexample.svg.png)
 
Row summations: 2 + 7 + 6 = 15, 9 + 5 + 1 = 15, 4 + 3 + 8 = 15.
Column summations: 2 + 9 + 4 = 15, 7 + 5 + 3 = 15, 6 + 1 + 8 = 15.
Diagonal summations: 2 + 5 + 7 = 15, 6 + 5 + 4 = 16.

## Algorithm
The naive/brute-force approach finding all NxN magic squares would be to simply test every permutation of 1, 2, ..., N^2. However, this is extremely problematic because a NxN magic square would have N^2! permutations to validate. Although brute-forcing through all 362880 (9!) permutations of a 3x3 magic square is manageable, anything beyond this would take too long to run (a 4x4 magic square would have ~21 trillion permuations).

The approach that this algorithm uses to significantly cut down on the number of permutations we would normally have to validate is by abusing two known properties of magic squares:

1. Each total row/column/diagonal sum of a NxN magic square is N(N^2-1)/2
2. One base magic square has 8 variants if we apply the appropriate transformation.

Original              Flip Y-axis            Transpose             Flip Y-Axis
                      or 90° ⟳                                     or 90° ⟳ 
| 8 | 1 | 6 |         | 6 | 1 | 8 |          | 8 | 3 | 4 |         | 4 | 3 | 8 |
|---|---|---|         |---|---|---|          |---|---|---|         |---|---|---|
| 3 | 5 | 7 |         | 7 | 5 | 3 |          | 1 | 5 | 9 |         | 9 | 5 | 1 |
| 4 | 9 | 2 |         | 2 | 9 | 4 |          | 6 | 7 | 2 |         | 2 | 7 | 6 |

Flip X-axis           Flip XY-axis           Flip X-axis           Flip XY-Axis
or 90° ⟲             or 180° ⟳               or 90° ⟲             or 180° ⟳
| 4 | 9 | 2 |         | 2 | 9 | 4 |          | 6 | 7 | 2 |         | 2 | 7 | 6 |
|---|---|---|         |---|---|---|          |---|---|---|         |---|---|---|
| 3 | 5 | 7 |         | 7 | 5 | 3 |          | 1 | 5 | 9 |         | 9 | 5 | 1 |
| 8 | 1 | 6 |         | 6 | 1 | 8 |          | 8 | 3 | 4 |         | 4 | 3 | 8 |

Ideally, it might be possible to only search through a subset of permutations that will only find 1/8 of all the possible NxN magic squares and then apply the above transformations to obtain all 8. However, the algorithm in this code only manages to find 1/2 of all the possible magic squares before it applys a reflection on the Y-axis to obtain the rest of the valid magic squares.

The way this algorithm is implemented is as follows:
1. Using an index tree, we search through all combinations of N summands that add up N(N^2-1)/2 and store them in an ArrayList. For example, in a 3x3 magic square, the summands are as follow:

15=1+5+9              15=1+6+8              15=2+4+9              15=2+5+8
15=2+6+7              15=3+4+8              15=3+5+7              15=4+5+6

Consequently, the summands {1,5,9}, {1,6,8}, {2,4,9}, {2,5,8} {2,6,7}, {3,4,8}, {3,5,7} {4,5,6} are then stored in an ArrayList.

2. Next, a recursive method is called that isolates the first set of valid summands from the ArrayList, and then filters all other sets by removing any sets that shares a common element with the removed set:

{1,5,9} is isolated.
{1,6,8}, {2,4,9}, {2,5,8} {2,6,7} are removed because it contains 1, 5, or 9.
{3,4,8}, {4,5,6} are kept through the filtering process.

3. With the isolated set of summands, all permuations are found:

{1,5,9}, {1,9,5}, {5,1,9}, {5,9,1}, {9,1,5}, {9,5,1}

4. Construct the first row of multiple magic square candidates by making the above permutations the numbers in the first row. However, it should be noted that when constructing the first row, only the first half of the permutations of the isolated summands are kept (keep {1,5,9}, {1,9,5}, {5,1,9}) while the second half is discarded (discard {5,9,1}, {9,1,5}, {9,5,1}). By doing this only on the construction of the first row of a magic square, when we do eventually find one magic square, we can also find the other by reflecting the square on the Y-axis:

Kept Summands          Discarded summands
(first row)            (Y-axis reflection)
| 1 | 5 | 9 |          | 9 | 5 | 1 |
|---|---|---|          |---|---|---|
| x | x | x |          | x | x | x |
| x | x | x |          | x | x | x |

| 1 | 9 | 5 |          | 5 | 9 | 1 |
|---|---|---|          |---|---|---|
| x | x | x |          | x | x | x |
| x | x | x |          | x | x | x |

| 5 | 1 | 9 |          | 5 | 1 | 9 |
|---|---|---|          |---|---|---|
| x | x | x |          | x | x | x |
| x | x | x |          | x | x | x |

5. For each of the currently constructed magic squares with the first row filled in, repeat steps 2-4 on the remaining summands that survived the filter (in this case, {3,4,8}, {4,5,6}). For instance:

This magic square
| 1 | 5 | 9 |
|---|---|---|
| x | x | x |
| x | x | x |

will have its second row recursively filled with either {3,4,8}, {3,8,4}, {4,3,8}, {4,8,3}, {8,3,4}, or {8,4,3}. And because the remaining set of summands ({4,5,6}) shares no common elements with {3,4,8}, it will survive the filtering process and all of its permutations will be filled in on the 3rd and final row.

6. When a magic square is fully filled in (or there are no more filtered summands that can complete the square), all column and diagonal sums will be validated to see if they each add up to N(N^2-1)/2 (row sums have already been validated to add up N(N^2-1)/2). For example:

Invalid Magic
Square (Discarded)
| 1 | 5 | 9 |
|---|---|---|
| 3 | 4 | 8 |
| 4 | 5 | 6 |

Found Valid             Y-flipped 2nd      
Magic Square            Valid Square  
| 8 | 1 | 6 |     |     | 6 | 1 | 8 |
|---|---|---|     |     |---|---|---|
| 3 | 5 | 7 |     |     | 7 | 5 | 3 |
| 4 | 9 | 2 |     |     | 2 | 9 | 4 |

When a valid magic square is found, both the original and its flipped variant are logged and the count for the total number of square is incremented by 2.

## Usage
Clone or download the repository.

Under the public static void main(String[] args) method in tester.java, modify "byte n = 4;" to desired magic square dimension to find all nxn magic squares.

Benchmarks:
n = 3 will finish in a few seconds
n = 4 will finish in 4-10 minutes (depending on hardware)
n = 5 will produce magic squares throughout, but may take a few days to finish running
