import java.util.*;

public class MagicSquares {
    public static void main(String[] args){
        chains(4);
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

        all = filterUniqueRows(all, n); // We only want the unique row groupings (ie: no grouping has more than n - 2 similar rows).
        
        // now we must filter all the columns in those row groupings
        System.out.println(toStringRowsCols(findColumnsGivenRows(all, n))); // At this point there are some rows that are 90 degree transforms in here (we need to filter those out)

        
        List<List<List<Set<Integer>>>> test = findColumnsGivenRows(filterUniqueRows(all, n), n); 

        
        //System.out.println(toStringRowsCols(possibleMagicSquares(findDiagonals(test, filteredComboList))));
        //System.out.println("Total results: " + possibleMagicSquares(findDiagonals(test, filteredComboList)).size());
        
        
        List<List<List<Set<Integer>>>> almostDone = possibleMagicSquares(findDiagonals(test, filteredComboList));

        //The code gives all possible arrangements of rows cols and diagonals (103 of them).
        //System.out.println(toStringRowsCols(almostDone) + "\n" + almostDone.size());

        List<List<List<Set<Integer>>>> cleanedAlmostDone = new ArrayList<>();
        cleanedAlmostDone = cleaningDiagonals(almostDone);
        //The code below gives 880 results
        //System.out.println(toStringRowsCols(cleanedAlmostDone) + "\n" + cleanedAlmostDone.size()); 
        //The code below gives 514 results
        //filterImpossibleSquares(cleanedAlmostDone);
    
        //filteredDiagonalMagicSquares(almostDone, n);

        
        //System.out.println(findDiagonals(findColumnsGivenRows(filterUniqueRows(all, n), n), filteredComboList).toString());

        //System.out.println(toStringAll(all) + "\n\n" + "Total Row Combinations: " +  all.size() + "\n" + filterUniqueRows(all, n) + "\n" + filterUniqueRows(all, n).size());
        //System.out.println("Total number of rows: " + size + "\n\n" + toStringRowsCols(filterUniqueRows(all, n)) + "\n\n\nTotal number of unique row combinations: " + filterUniqueRows(all, n).size());
        //System.out.println(findColumnsGivenRows(all, n));
        //return filteredComboList;
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
        int index = 0;

        // We have the groupings for all unique magic squares of size n and now must combine these to see which rows match with which columns
        for(List<Set<Integer>> groups: uniqueGroupings){ // For each row grouping in the uniqueGrouping set
            index++;
            for(int i = index; i < uniqueGroupings.size(); i++){ // For each grouping in the uniqueGrouping set
                for(Set<Integer> groupsSet: groups){ // For each row in the row grouping 
                    for(int j = 0; j < uniqueGroupings.get(i).size(); j++){  
                        count = 0;
                        for(int k: uniqueGroupings.get(i).get(j)){
                            if(groupsSet.contains(k)){
                                count++;
                            }
                        }
                        if(count != 1){
                            break; // keeps the value of count | Need to get the next iteration of i. 
                        }
                    }
                    if(count != 1){
                        break; // need to get the next iteration of i since not a column match. 
                    }
                }
                if(count == 1){ // here if count = 1 for the last comparision -> count = 1 for all the comparisons since it didn't break which means a column match has been found. 
                    group.add(groups);
                    group.add(uniqueGroupings.get(i));
                    groupings.add(group);
                    group = new ArrayList<>();
                }
            }
        }

        return groupings;
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

    // Interestingly some of these diagonal pairs dont work and others have more than two
    // Lets combine the relation idea and return a list of groupings with diagonals that work (or just the rows/columns in their 4 pair matches)

    public static void filteredDiagonalMagicSquares(List<List<List<Set<Integer>>>> groups, int n){
        List<HashMap<List<List<Set<Integer>>>, HashMap<Integer, Integer>>> totalList = new ArrayList<>();
        HashMap<List<List<Set<Integer>>>, HashMap<Integer, Integer>> frequencyGroups = new HashMap<>();
        HashMap<Integer, Set<Integer>> keepTrackofRows = new HashMap<>();
        
        List<List<List<Set<Integer>>>> finalList = new ArrayList<>();
        List<List<Set<Integer>>> cleaned = new ArrayList<>();
        List<Set<Integer>> diagonals = new ArrayList<>();
        List<Set<Integer>> fourSets = new ArrayList<>();
        HashMap<Integer, Integer> rowRelations = new HashMap<>();

        ArrayList<Integer> discoveredSet = new ArrayList<>();

        boolean relation = true;
        boolean contains = false;
        int count = 0;
        
        for(List<List<Set<Integer>>> group: groups){
            for(int i = 0; i < group.get(2).size(); i++){ // outer diagonal group
                for(int j = i + 1; j < group.get(2).size(); j++){ // inner diagonal group

                    /* special instance when there is two numbers that are the same in the evens case
                    if(n%2 == 0){
                        contains = false;
                        for(int num: group.get(2).get(j)){
                            if(group.get(2).get(i).contains(num)){
                                contains = true;
                                break;
                            }
                        }
                        if(contains){ // move onto the next diagonal 
                            break;
                        }
                    }
                    */
                    
                    
                    diagonals.add(group.get(2).get(i));
                    diagonals.add(group.get(2).get(j));
                    // find the row with the outer group number
                    for(int l: group.get(2).get(i)){ // the numbers in the outer loop
                        count = 0; // reset the count
                        for(int k = 0; k < group.get(0).size(); k++){ // loop through to find the row with the otter number
                            // reset k each time this loop resets.
                            if(group.get(0).get(k).contains(l)){ // find the row with the outer group number
                                // with the outer group number row found, search this row for the opposing diagonal number
                                for(int m: group.get(2).get(j)){
                                    if(group.get(0).get(k).contains(m)){ // if you found a row matching keep track of it
                                        rowRelations.put(l, m); // with this done move onto the next outer diagonal number
                                        keepTrackofRows.put(m, group.get(0).get(k));

                                        count ++;
                                        break;
                                    }
                                }
                            }
                            if(count == 1){ // the mapping was found no need to search the rest of the rows. 
                                break;
                            }
                        }
                    }// Repeat this for loop for the other diagonal 

                    for(int l: group.get(2).get(j)){ // the numbers in the outer loop
                        count = 0; // reset the count
                        for(int k = 0; k < group.get(1).size(); k++){ // loop through to find the col with the other number
                            // reset k each time this loop resets.
                            if(group.get(1).get(k).contains(l)){ // find the row with the outer group number
                                // with the outer group number row found, search this row for the opposing diagonal number
                                for(int m: group.get(2).get(i)){
                                    if(group.get(1).get(k).contains(m)){
                                        rowRelations.put(l, m); // with this done move onto the next outer diagonal number
                                        keepTrackofRows.put(m, group.get(1).get(k));
                                        count ++;
                                        break;
                                    }
                                }
                            }
                            if(count == 1){ // the mapping was found no need to search the rest of the rows. 
                                break;
                            }
                        }
                    }
                    
                    /*
                    for(int start: diagonals.get(0)){
                        if(!discoveredSet.contains(start)){
                            int one = rowRelations.get(start);
                            int two = rowRelations.get(one);
                            int three = rowRelations.get(two);
                            int four = rowRelations.get(three);
                            if(start != four){ 
                                relation = false;
                                break;
                            }

                            fourSets = new ArrayList<>();
                            discoveredSet.add(one);
                            discoveredSet.add(two);
                            discoveredSet.add(three);
                            discoveredSet.add(four);
                            fourSets.add(keepTrackofRows.get(one));
                            fourSets.add(keepTrackofRows.get(two));
                            fourSets.add(keepTrackofRows.get(three));
                            fourSets.add(keepTrackofRows.get(four));
                            cleaned.add(fourSets);
                            
                        }
                    }
                    */

                    if(relation){ // if it's a relation all we need to do is to return the value of the rows/cols/diags
                        cleaned.add(group.get(0));
                        cleaned.add(group.get(1)); // delete the diagonals
                        cleaned.add(diagonals); // insert the new diagonals
                        finalList.add(cleaned);
                        
                        frequencyGroups.put(cleaned, rowRelations);
                        totalList.add(frequencyGroups); 
                    }

                    discoveredSet = new ArrayList<>();
                    diagonals = new ArrayList<>();
                    rowRelations = new HashMap<>();
                    cleaned = new ArrayList<>();
                    frequencyGroups = new HashMap<>();
                    relation = true;
                    // This works but instead of adding it to a larger set can we check for relation condition on the spot and only add those that pass those conditions? 
                }
            }
        }
        //System.out.println(totalList.toString() + "\n\n" + totalList.size());
        System.out.println(toStringRowsCols(finalList) + "\n" + finalList.size());
    }

    // The last method was a mess so let's make a method that takes the list of row/col groupings with their possible diagonals and return a complete list of all the combinations 
    public static List<List<List<Set<Integer>>>> cleaningDiagonals(List<List<List<Set<Integer>>>> list){
        List<List<List<Set<Integer>>>> completedList = new ArrayList<>();
        List<List<Set<Integer>>> tempList = new ArrayList<>();
        List<Set<Integer>> diagonals = new ArrayList<>();
        for(List<List<Set<Integer>>> group: list){ // for each row/col/diag grouping
            for(int i = 0; i < group.get(2).size(); i++){
                for(int j = 1; j < group.get(2).size(); j++){
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

    public static void filterImpossibleSquares(List<List<List<Set<Integer>>>> cleanedList){
        List<List<List<Set<Integer>>>> completedList = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();
        
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

                /*

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

                    /* I am not getting the expected output so there is some condition missing and I think it has to do with keeping track of the relations 

                } // with this if there is no relation we set the condition to false
                
                */

                if(condition){ // if the condition is met we keep the group otherwise we don't 
                    completedList.add(group);
                }
            }
        }

        System.out.println(toStringRowsCols(completedList) + "\n" + completedList.size());

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
                    str +="\nColumns: " + all.get(i).get(j);
                } else{
                    str +="\nDiagonals: " + all.get(i).get(j) + "\n\n";
                }
            }
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
