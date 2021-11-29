import java.util.*;

public class MagicSquares {
    public static void main(String[] args){
        final long startTime = System.currentTimeMillis();
        chains(4);
        final long elapsedTimeMillis = System.currentTimeMillis() - startTime;
        System.out.println("Total time to finish in milliseconds: " + elapsedTimeMillis);
    }

    public static void chains(int n){
        int value = value(n); // calculate the magic value
        List<Integer> nums = numbers(n); // create a list of all the number permutations 
        List<Set<Integer>> combos = getSubsets(nums, n); // find all unique sets of size n
        List<Set<Integer>> filteredComboList = filteredCombos(combos, value, n); // filter those sets to those that sum up to the calculated value.
        // At this point all we have are the subsets of size n that sum to the magic value
        List<List<Set<Integer>>> all = new ArrayList<>(); // This will be the placeholder that will eventually have all the row groupings

        for(int i = 0; i < n ; i++){ // We need to do this method n times because we are filling up each row grouping one row at a time
            all = rowFilterLayerPermutations(all, filteredComboList); // This method takes the given built rows and adds as many possible solutions to them.
        }
        // At this point we know that all the rows have been discovered, but this also implies that all the columns are in "all".
        // We must find a way to filter these out.

        /* For the 4 by 4 case, I found that there were 9408 total row groupings from the 86 subsets. Now after filtering all the unique row
           groupings, I found that there were a total of 392 row groupings. If my assumptions are correct, once filter the filtered uniqueRows 
           by finding columns within those rows, then there should be a total of 220 unique row groupings?
        */

        //System.out.println(all);
        all = filterUniqueRows(all, n); // We only want the unique row groupings (ie: no grouping has more than n - 2 similar rows).
        
        //System.out.println(all);
        List<List<List<Set<Integer>>>> rowColGroupings = findColumnsGivenRows(all, n); // for the n = 4 case there are 954
        //System.out.println(rowColGroupings);
        rowColGroupings = filterRowColGroupings(rowColGroupings); // for the n = 4 case, there are 477 so its working!!
        //System.out.println(rowColGroupings);
        // Now that we have all unique row/col groupings, find the diagonals that match with those row/col groupings
        rowColGroupings = findDiagonals(rowColGroupings, filteredComboList); // There are still 477 of them at this point now with their respective diagonals
        //System.out.println(rowColGroupings);

        rowColGroupings = possibleMagicSquares(rowColGroupings); // Now there are only 103 with diagonals.size() >= 2.
        //System.out.println(rowColGroupings);
        rowColGroupings = cleaningDiagonals(rowColGroupings); // There are 880 combinations of row/col/diag with diag size = 2.
        //System.out.println(rowColGroupings);
        rowColGroupings = filterDuplicateDiagonals(rowColGroupings); // I DID IT LET'S GOOOO!!!!!
        // filter the diagonals
        List<int[][]> magicSquares = new ArrayList<>();
        magicSquares = filterImpossibleSquares(rowColGroupings, n); // This completes the info needed to build a magic square now we must build it. 
    
        /*for(int j = 0; j < magicSquares.size(); j++){
            System.out.println(Arrays.deepToString(magicSquares.get(j)) + "\n");
        }
        System.out.println(magicSquares.size());
        */

        magicSquares = completedMagicSquaresList(magicSquares, n);

        /*
        for(int j = 0; j < magicSquares.size(); j++){
            System.out.println(Arrays.deepToString(magicSquares.get(j)) + "\n");
        }
        */

        System.out.println(toStringMagicSquares(magicSquares, n));
        System.out.println(magicSquares.size());


    }

    // calculate how much the sum of each row/col/diag will equal to.
    public static int value(int n){
        return (n * (n * n + 1))/2;
    }

    // create an arrayList of all the ints needed for magic squares.
    public static List<Integer> numbers(int n){
        List<Integer> nums = new ArrayList<>();
        for(int i = 1; i <= (n * n); i++){
            nums.add(i);
        }
        return nums;
    }

    // filter out the subsets into the ones that sum into the value
    public static List<Set<Integer>> filteredCombos(List<Set<Integer>> combo, int value, int n){
        List<Set<Integer>> filteredCombo = new ArrayList<>();
        int sum = 0;
        for(int i = 0; i < combo.size(); i++){
            sum = 0;
            for(int num: combo.get(i)){
                sum += num;
            }
            if(sum == value){
                filteredCombo.add(combo.get(i));
            }
        }
        return filteredCombo;
    }

    // Populate the rows layer by layer by brute forcing each possible combination
    public static List<List<Set<Integer>>> rowFilterLayerPermutations(List<List<Set<Integer>>> list, List<Set<Integer>> filtered){
        List<List<Set<Integer>>> all = new ArrayList<>();
        List<Set<Integer>> rows = new ArrayList<>();
        boolean contains = false;

        for(Set<Integer> subset: filtered){ // for each subset piece
            if(list.size() > 0){
                for(List<Set<Integer>> group: list){ // for each group in the list, 
                    contains = false; // for every new group we reset contains
                    for(Set<Integer> groupSubset: group){ // for each subset within the grouping
                        for(int n: groupSubset){ // for each number within each subset within each grouping
                            if(subset.contains(n)){ // if a number from the subset with the grouping has a number from the piece, then move on to the next grouping
                                contains = true;
                                break;
                            }
                        }
                        if(contains){
                            break;
                        } else{
                            // keep track of the groups
                            rows.add(groupSubset); // for every groupSubset that passes the test, add it to the rows.
                        }
                    }
                    if(rows.size() == group.size()){ // if all the subsets are unique add the subset piece.
                        rows.add(subset);
                        all.add(rows);
                    } 
                    rows = new ArrayList<>(); // reset the rows.
                }
            } else{ // This is just for when the inital list is empty
                rows.add(subset);
                all.add(rows);
                rows = new ArrayList<>();
            }
        }    
        return all;
    }

    // Since there are possibilities for the same row grouping (turned 180 for example) we want to get only one such instance so to filter 
    // those similarities out we do the next method 
    public static List<List<Set<Integer>>> filterUniqueRows (List<List<Set<Integer>>> all, int n){
        List<List<Set<Integer>>> uniqueRows = new ArrayList<>();
        int count = 0;
        int maxCount = 0;
        // loop through each of the groups
        // comparing them to the rest of the groups
        for(int i = 0; i < all.size(); i++){ // loop through the entire list of row groupings
            count = 0; // reset the count for the next iteration
            maxCount = 0; //reset the maxCount as well
            for(int j = i + 1; j < all.size(); j++){ // loop through the entire list of row groupings again 
                count = 0; // reset the count for the next iteration
                maxCount = 0; //reset the maxCount as well
                // compare all the subsets in the group set with the grouping from this loop
                for(int k = 0; k < all.get(i).size(); k++){
                    if(all.get(j).contains(all.get(i).get(k))){
                        count++; // keep track of how many similar rows there are (Logically speaking there cannot be more than n - 1 many non-unique rows because the fourth row will always be the same)
                    }
                }
                if(count >= n - 1){ // if at any point we find another row grouping with the same rows stop comparing and move on. 
                    maxCount = count;
                    break;
                }

                if(count > maxCount){
                    maxCount = count;
                }
            }
            if(maxCount < n - 1){ // The count must be less than n - 1 because having n - 1 same rows implies that the last row will be the same
                    // If this condition is met then the outside row grouping is unique to the rest of the set of row groups
                    uniqueRows.add(all.get(i));
            }
        }
        return uniqueRows;
    }

    // Now we make a new list where we find all possible columns for the unique rows
    // In the method following that we check and filter any rows with columns that exist with columns with the same row. 
    public static List<List<List<Set<Integer>>>> findColumnsGivenRows(List<List<Set<Integer>>> uniqueGroupings, int n){
        List<List<List<Set<Integer>>>> groupings = new ArrayList<>();
        List<List<Set<Integer>>> group = new ArrayList<>();
        int count = 0;
        boolean condition = false;

        // We have the groupings for all unique magic squares of size n and now must combine these to see which rows match with which columns
        for(int i = 0; i < uniqueGroupings.size(); i++){ // outer loop for row groups
            for(int j = 0; j < uniqueGroupings.size(); j++){ // inner loop for row groups
                condition = true; 
                for(int k = 0; k < uniqueGroupings.get(i).size(); k++){ // loop through all of the rows in the outer loop row group
                    for(int l = 0; l < uniqueGroupings.get(j).size(); l++){ // loop through the rows in the inner loop row group
                        count = 0; // reset the count for each new inner loop row 
                        for(int num: uniqueGroupings.get(i).get(k)){
                            if(uniqueGroupings.get(j).get(l).contains(num)){
                                count++;
                            }
                        }

                        if (count != 1){ // if at any point we find an inner loop with 0 or more than 1 similarity in two rows, then we know that those row/column groupings are impossible so skip them. 
                            condition = false;
                            break;
                        }
                    }
                    if(!condition){ // if the condition is failed we move onto the next inner loop (no need to search the remaining rows)
                        break;
                    }
                }
                if(condition){ // if we get to this point and the condition is satisfied, then add those row/column gropings.
                    group.add(uniqueGroupings.get(i));
                    group.add(uniqueGroupings.get(j));
                    groupings.add(group);
                }
                group = new ArrayList<>();
            }
        }
        return groupings;
    }

    // Now we need to see if there are groups with rows = other columns and those colums rows = the inital rows columns
    public static List<List<List<Set<Integer>>>> filterRowColGroupings(List<List<List<Set<Integer>>>> rowColGroupings){
        List<List<List<Set<Integer>>>> uniqueRowColGroupings = new ArrayList<>();
        boolean condition = true;
        for(int i = 0; i < rowColGroupings.size(); i++){
            condition = true;
            for(int j = i + 1; j < rowColGroupings.size(); j++){
                if(rowColGroupings.get(i).get(0).equals(rowColGroupings.get(j).get(1)) && rowColGroupings.get(i).get(1).equals(rowColGroupings.get(j).get(0))){ // if the row grouping is equal to another column grouping
                    condition = false;
                    break; // There is no need to keep searching just move on to the next row grouping 
                }
            }
            if(condition){ // if we fail to find a row/col col/row symmetry then the initial row is unique
                uniqueRowColGroupings.add(rowColGroupings.get(i));
            }
        }
        return uniqueRowColGroupings;
    }

    // Now that all the rows and columns have been found brute force search for diagonals. 

    public static List<List<List<Set<Integer>>>> findDiagonals(List<List<List<Set<Integer>>>> rowCols, List<Set<Integer>> rows){
        List<List<List<Set<Integer>>>> completedSet = new ArrayList<>();
        List<List<Set<Integer>>> rowColDiag = new ArrayList<>();
        List<Set<Integer>> diagonalPiece = new ArrayList<>();
        int count = 0;

        // for each grouping in rowCols, do the count thing from the last step with all the row subsets. 

        for(List<List<Set<Integer>>> rowCol: rowCols){ //grabs both row and column
            // reset the diagonal piece every iteration
            diagonalPiece = new ArrayList<>();
            for(Set<Integer> row: rows){ // grabs each individual diagonal piece
                for(List<Set<Integer>> col: rowCol){ // for each row / col grouping
                    for(Set<Integer> set: col){  // for each set in the row / col grouping
                        count = 0; // reset the count after each row change. 
                        for(int n: row){ // for each of the number in the diagonal piece
                            if(set.contains(n)){
                                count++;
                            }
                        }
                        if(count != 1){ // move onto the next diagonal piece comparison
                            break;
                        }
                    }
                    if(count != 1){// move into the next diagonal piece 
                        break;
                    } 
                }
                if(count == 1){ // if the last comparison is one then all the comparisons are one
                    diagonalPiece.add(row);
                }   
            } 
            // at this point all of the diagonal pieces have been added for the individual row col so combine them together 
            //if(count == 1){ // not necessary but just in case
                rowColDiag.add(rowCol.get(0));
                rowColDiag.add(rowCol.get(1));
                rowColDiag.add(diagonalPiece);
                completedSet.add(rowColDiag);
                rowColDiag = new ArrayList<>();
            //}
        }
        return completedSet;
    }

    // Now that everything has been discovered we only care aboue the row/column groups that have two or more diagonals so return those

    public static List<List<List<Set<Integer>>>> possibleMagicSquares(List<List<List<Set<Integer>>>> groups){
        List<List<List<Set<Integer>>>> filtered = new ArrayList<>();
        for(int i = 0; i < groups.size(); i++){
            if(groups.get(i).get(2).size() >= 2){
                filtered.add(groups.get(i));
            }
        }

        return filtered;
    }

    // The last method was a mess so let's make a method that takes the list of row/col groupings with their possible diagonals and return a complete list of all the combinations 
    public static List<List<List<Set<Integer>>>> cleaningDiagonals(List<List<List<Set<Integer>>>> list){
        List<List<List<Set<Integer>>>> completedList = new ArrayList<>();
        List<List<Set<Integer>>> tempList = new ArrayList<>();
        List<Set<Integer>> diagonals = new ArrayList<>();
        for(List<List<Set<Integer>>> group: list){ // for each row/col/diag grouping
            for(int i = 0; i < group.get(2).size(); i++){
                for(int j = i + 1; j < group.get(2).size(); j++){
                    diagonals.add(group.get(2).get(i));
                    diagonals.add(group.get(2).get(j));
                    tempList.add(group.get(0));
                    tempList.add(group.get(1));
                    tempList.add(diagonals);
                    completedList.add(tempList);
                    tempList = new ArrayList<>();
                    diagonals = new ArrayList<>();
                }
            }
        }
        return completedList;
    }

    // Now that we have a clean set of diagonals
    // Check their possibilities, if possible save the grouping if not erase.

    public static List<int[][]> filterImpossibleSquares(List<List<List<Set<Integer>>>> cleanedList, int n){
        List<List<List<Set<Integer>>>> completedList = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();
        List<HashMap<Integer, Integer>> filteredMap = new ArrayList<>();
        
        // Make a case for odd and even
        boolean condition = true;
        for(List<List<Set<Integer>>> group: cleanedList){
            if(group.get(0).size() % 2 == 0){ // even
                condition = true;
                // Since each grouping has only two diagonals we search through one and compare with the other
                for(int num: group.get(2).get(1)){
                    if(group.get(2).get(0).contains(num)){
                        condition = false;
                        break;
                    }
                }
            } else{ // odd
                condition = false; 
                for(int num: group.get(2).get(1)){
                    if(group.get(2).get(0).contains(num)){
                        condition = true;
                        break;
                    }
                }
            }

            if(condition){ // if the condition has been met then we check for relation otherwise we skip

            

                //check all 4 diagonals for simplicity 
                for(int firstDiagNum: group.get(2).get(0)){ // grab the first diagonal and loop through it's numbers
                    for(Set<Integer> row: group.get(0)){ // find the row with that number
                        if(row.contains(firstDiagNum)){ // enter that row and search for the oposing diagonal comparison
                            for(int colInnerNum: group.get(2).get(1)){
                                if(row.contains(colInnerNum)){ //WHAT HAPPENS WHEN THERE ISN'T A COMPARISON? 
                                    map.put(firstDiagNum, colInnerNum); // add that row comparison to the hashmap. 
                                    break;
                                }
                            }
                        }
                    }
                } // This completes the row mappings in the relation/now do the same for the columns. 

                for(int secondDiagNum: group.get(2).get(1)){
                    for(Set<Integer> col: group.get(1)){
                        if(col.contains(secondDiagNum)){
                            for(int rowInnerNum: group.get(2).get(0)){
                                if(col.contains(rowInnerNum)){
                                    map.put(secondDiagNum, rowInnerNum);
                                    break;
                                }
                            }
                        }
                    }
                } // this completes the relation mapping 

                // now we must check if the relation is met (if so save the group)
                
                for(int testRow: group.get(2).get(0)){
                    int one = map.get(testRow);
                    int two = map.get(one);
                    int three = map.get(two);
                    int four = map.get(three);
                    if(four != testRow){
                        condition = false;
                        break;
                    } 
                    /* I am not getting the expected output so there is some condition missing and I think it has to do with keeping track of the relations */

                } // with this if there is no relation we set the condition to false
                if(condition){ // if the condition is met we keep the group otherwise we don't 
                    completedList.add(group);
                    filteredMap.add(map);
                }
                map = new HashMap<>();
            }
        }
        constructAMagicSquare(completedList, filteredMap, n);

        // Since this function is too long and I don't want to return both the groupings and the hashmaps that is the key to making magic squares,
        // I'm going to make another function that takes hashmaps and groupings and uses them to make magic squares. 

        return constructAMagicSquare(completedList, filteredMap, n);
    }

    public static List<int[][]> constructAMagicSquare(List<List<List<Set<Integer>>>> combinations, List<HashMap<Integer, Integer>> filteredMap, int n){
        // First thing we need is to memoize the search through the hashmap so as to avoid duplicates
        ArrayList<Integer> searchedThrough = new ArrayList<>();

        // Make the matrix 
        int[][] magicSquare = new int[n][n]; // first [] is rows second [] is columns ( we only need n - 1 bc we include 0)

        // Make the list that will carry all the matricies

        List<int[][]> listOfMagicSquares = new ArrayList<>();

        // start populating the main daigonals (SEPERATE THESE LOOPS VIA ODD/EVEN)
        for(int l = 0; l < combinations.size(); l++){
            int i = 0;
            for(int diagonal: combinations.get(l).get(2).get(0)){
                if(n % 2 == 0){ // even case
                    // iteration to insert via main daigonal
                    if(!searchedThrough.contains(diagonal)){
                        magicSquare[i][i] = diagonal;
                        magicSquare[i][(n - (i + 1))] = filteredMap.get(l).get(diagonal);
                        magicSquare[(n - (i + 1))][(n - (i + 1))] = filteredMap.get(l).get(magicSquare[i][(n - (i + 1))]);
                        magicSquare[(n - (i + 1))][i] = filteredMap.get(l).get(magicSquare[(n - (i + 1))][(n - (i + 1))]);
                        searchedThrough.add(magicSquare[i][i]);
                        searchedThrough.add(magicSquare[i][(n - (i + 1))]);
                        searchedThrough.add(magicSquare[(n - (i + 1))][(n - (i + 1))]);
                        searchedThrough.add(magicSquare[(n - (i + 1))][i]);
                        i++;
                    }
                    
                } else { // odd case
                    // its the same as the even case except we need to take into account the center
                    if(filteredMap.get(l).get(diagonal) == diagonal){ // this is the center
                        magicSquare[n/2][n/2] = diagonal;
                        searchedThrough.add(diagonal);
                    } else {
                        if(!searchedThrough.contains(diagonal)){
                            magicSquare[i][i] = diagonal;
                            magicSquare[i][(n - (i + 1))] = filteredMap.get(l).get(diagonal);
                            magicSquare[(n - (i + 1))][(n - (i + 1))] = filteredMap.get(l).get(magicSquare[i][(n - (i + 1))]);
                            magicSquare[(n - (i + 1))][i] = filteredMap.get(l).get(magicSquare[(n - (i + 1))][(n - (i + 1))]);
                            searchedThrough.add(magicSquare[i][i]);
                            searchedThrough.add(magicSquare[i][(n - (i + 1))]);
                            searchedThrough.add(magicSquare[(n - (i + 1))][(n - (i + 1))]);
                            searchedThrough.add(magicSquare[(n - (i + 1))][i]);
                            i++;
                        }
                    }

                }
            } 
            listOfMagicSquares.add(magicSquare);
            searchedThrough = new ArrayList<>();
            magicSquare = new int[n][n];
        }
        // up to this point all the diagonals have been set. 
        for(int j = 0; j < listOfMagicSquares.size(); j++){
            for(int k = 0; k < n; k++){
                for(int m = 0; m < n; m++){
                    if(listOfMagicSquares.get(j)[k][m] == 0){ // we need to update if its zero
                        // we need to search through row k and column m from the original set
                        // magic squares[k][k] refers to the row diag
                        // magic squares [m][m] refers to the column diag
                        for(Set<Integer> rows: combinations.get(j).get(0)){
                            for(Set<Integer> columns: combinations.get(j).get(1)){
                                if(rows.contains(listOfMagicSquares.get(j)[k][k]) && columns.contains(listOfMagicSquares.get(j)[m][m])){ // identify the row/col
                                    for(int num: rows){
                                        if(columns.contains(num)){
                                            listOfMagicSquares.get(j)[k][m] = num;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return listOfMagicSquares;
    }

    // There is one more filter left to do 
    //SIDENOTE MAKE SURE THAT THE LAST UNIQUE ONE IS GETTING COUNTED IF THE STEP BEFORE IS NOT UNIQUE

    public static List<List<List<Set<Integer>>>> filterDuplicateDiagonals(List<List<List<Set<Integer>>>> rowColDiagGroupings){
        List<List<List<Set<Integer>>>> filtered = new ArrayList<>();
        boolean condition = true;

        for(int i = 0; i < rowColDiagGroupings.size(); i++){
            condition = true;
            for(int j = i + 1; j < rowColDiagGroupings.size(); j++){
                if(rowColDiagGroupings.get(i).get(0) == rowColDiagGroupings.get(j).get(0) && rowColDiagGroupings.get(i).get(1) == rowColDiagGroupings.get(j).get(1)){ // if the row and the columns are the same
                    //check if the diagonals are the same
                    if(rowColDiagGroupings.get(i).get(2).contains(rowColDiagGroupings.get(j).get(2).get(0)) && rowColDiagGroupings.get(i).get(2).contains(rowColDiagGroupings.get(j).get(2).get(1))){// if the diagonals are the same then fail the condition
                        condition = false;
                    }
                }
                if(!condition){ // if we fail then move on
                    break;
                }
            }
            if(condition){
                filtered.add(rowColDiagGroupings.get(i));
            }
        }
        return filtered;
    }

    public static int[][] matrixMultiply(int[][] matrixOne, int[][] matrixTwo, int n){ // always multiply matrixone into matrix two
        int[][] matrixThree = new int[n][n];

        for(int i = 0; i < n; i++){ // num of times done
            for(int j = 0; j < n; j++){ // rows 
                for(int k = 0; k < n; k++){ // columns
                    matrixThree[i][j] += matrixOne[i][k] * matrixTwo[k][j];
                }
            }
        }
        return matrixThree;
    }

    public static int[][] reflection(int n){
        int[][] reflection = new int[n][n];

        for(int i = 0; i < n; i ++){
            reflection[i][n - (i + 1)] = 1;
        }

        return reflection;
    }

    public static int[][] transpose(int[][] initalMatrix, int n){
        int[][] transpose = new int[n][n];

        for(int i = 0; i < n; i++){
            for (int j = 0; j < n; j++){
                transpose[i][j] = initalMatrix [j][i];
            }
        }
        return transpose;
    }

    // Given a list of completed matrices, compute all the matrix multiplications 

    public static List<int[][]> completedMagicSquaresList(List<int[][]> initialList, int n){
        List<int[][]> firstOutput = new ArrayList<>();
        List<int[][]> finalOutput = new ArrayList<>();

        int[][] reflection = reflection(n);
        int[][] kleinOne = {{0,0,1,0}, {1,0,0,0}, {0,0,0,1}, {0,1,0,0}};
        int[][] kleinTwo = {{1,0,0,0}, {0,0,1,0}, {0,1,0,0}, {0,0,0,1}};
        int[][] kleinThree = {{0,1,0,0}, {1,0,0,0}, {0,0,0,1}, {0,0,1,0}};
        if(n == 4){
            for(int i = 0; i < initialList.size(); i++){
                firstOutput.add(initialList.get(i));
                firstOutput.add(matrixMultiply(matrixMultiply(kleinOne, initialList.get(i), n), kleinOne, n));
                firstOutput.add(matrixMultiply(matrixMultiply(kleinTwo, initialList.get(i), n), kleinTwo, n));
                firstOutput.add(matrixMultiply(matrixMultiply(kleinThree, initialList.get(i), n), kleinThree, n));
            }
        } else { // for now we do nothing for other sizes. 
            for(int i = 0; i < initialList.size(); i++){
                firstOutput.add(initialList.get(i));
            }
        }

        for(int i = 0; i < firstOutput.size(); i++){
            int[][] r1 = firstOutput.get(i); // identity
            int[][] r2 = matrixMultiply(transpose(r1, n), reflection, n); // rotation by 90 degrees
            int[][] r3 = matrixMultiply(transpose(r2, n), reflection, n); // rotation by 180 degrees
            int[][] r4 = matrixMultiply(transpose(r3,n), reflection, n); // rotation by 270 degrees
            int[][] sr1 = matrixMultiply(r1, reflection, n); // vertical reflection
            int[][] sr2 = matrixMultiply(transpose(sr1, n), reflection, n); // vertical reflection + 90 degrees
            int[][] sr3 = matrixMultiply(transpose(sr2, n), reflection, n); // vertical reflection + 180 degrees
            int[][] sr4 = matrixMultiply(transpose(sr3, n), reflection, n); // vertical reflection + 270 degrees
            finalOutput.add(r1);
            finalOutput.add(r2);
            finalOutput.add(r3);
            finalOutput.add(r4);
            finalOutput.add(sr1);
            finalOutput.add(sr2);
            finalOutput.add(sr3);
            finalOutput.add(sr4);
        }
        return finalOutput;
    }

    // create a hashMap of frequencies
    public static HashMap<Integer, Integer> frequenciesMap(List<Set<Integer>> combos){
        HashMap<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < combos.size(); i++){
            for(int num: combos.get(i)){
                if(map.containsKey(num)){
                    map.put(num, map.get(num) + 1);
                } else {
                    map.put(num, 1);
                }
            }
        }
        return map;
    }

    // create a hashMap of summed frequencies
   public static HashMap<Set<Integer>, Integer> piecesFrequencyMap(List<Set<Integer>> combos, HashMap<Integer, Integer> frequencies){
        HashMap<Set<Integer>, Integer> map = new HashMap<>();
        int sum = 0;
        for(int i = 0; i < combos.size(); i++){
            sum = 0;
            for(int num: combos.get(i)){
                sum += frequencies.get(num);
            }
            map.put(combos.get(i), sum);
        }
        return map;
    }      

    public static List<Set<Integer>> groupRows(List<Set<Integer>> list){
        List<Set<Integer>> current = new ArrayList<>();
        int count = 0; 
        for(Set<Integer> s: list){
            for(Set<Integer> t: list){
                count = 0;
                for(int n: t){
                    if(s.contains(n)){
                        count++;
                    }
                }

                if(count < 2){
                    current.add(t);
                }
            }
        }
        System.out.println(current);
        return current;
    }
    
    public static String toString(List<Set<Integer>> list){
        String str = "";
        for(Set<Integer> set: list){
            str += set.toString() + "\n"; 
        }
        return str;
    }

    public static String toStringAll(List<List<Set<Integer>>> list){
        String str = "";
        for(List<Set<Integer>> group: list){
            str += group.toString() + "\n";
        }
        return str;
    }

    public static String toStringRowsCols(List<List<List<Set<Integer>>>> all){
        String str = "";
        for(int i = 0; i < all.size(); i++){
            for(int j = 0; j < all.get(i).size(); j++){
                if(j == 0){
                    str += "Rows: " + all.get(i).get(j);
                } else if(j == 1){
                    str +=" Columns: " + all.get(i).get(j) + "\n";
                } else{
                    str +="Diagonals: " + all.get(i).get(j) + "\n\n";
                }
            }
        }
        return str;
    }

    public static String toStringMagicSquares(List<int[][]> squares, int n){
        String str = "Start \n";
        for(int i = 0; i < 200; i++){
            for(int j = 0; j < n; j++){
                str += Arrays.toString(squares.get(i)[j])+"\n";
            }
            str += "\n";
        }
        return str;
    }

    // this is from stack overflow 
    // https://stackoverflow.com/questions/12548312/find-all-subsets-of-length-k-in-an-array
    private static void getSubsets(List<Integer> superSet, int k, int idx, Set<Integer> current,List<Set<Integer>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unseccessful stop clause
        if (idx == superSet.size()) return;
        Integer x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx+1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx+1, current, solution);
    }

    public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
        List<Set<Integer>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
        return res;
    }
}
