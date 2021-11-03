import java.util.*;

public class MagicSquares {
    public static void main(String[] args){
        chains(4);
    }

    public static void/*List<Set<Integer>>*/ chains(int n){
        int value = value(n); // calculate the value
        List<Integer> nums = numbers(n); // create a list of all the numbers
        List<Set<Integer>> combos = getSubsets(nums, n); // find all unique sets of size n
        List<Set<Integer>> filteredComboList = filteredCombos(combos, value, n); // filter those sets to those that sum up to the calculated value.
        //HashMap<Integer, Integer> frequencies = frequenciesMap(filteredComboList); // create a hashmap of frequencies, then use that information to create one single magic square.
        //HashMap<Set, Integer> pieceFrequencies = piecesFrequencyMap(filteredComboList ,frequencies); // create a hashmap of the sum of the frequencies of the given pieces
        
        //System.out.println(toString(filteredComboList) + "\n");
        //System.out.println(frequencies + "\n");
        //System.out.println("Size: " + filteredComboList.size() + "\n");
        //System.out.println(rowFilterLayer1((filteredComboList)) + "\n\n\n");
        List<List<Set<Integer>>> all = new ArrayList<>(); //rowFilterLayer1(filteredComboList);
        for(int i = 0; i < n ; i++){ // This works but it puts all the rows in different orders (is that necessary?)
            all = rowFilterLayerPermutations(all, filteredComboList);
        }
        //int size = all.size();

        //System.out.println(toStringAll(all) + "\n" + all.size());

        //This is all the groupings of rows and columns
        System.out.println(toStringRowsCols(findColumnsGivenRows(filterUniqueRows(all, n), n)) + "Total number of groupings: " + findColumnsGivenRows(filterUniqueRows(all, n), n).size());

        
        
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
        //System.out.println(filteredCombo + "\n\n" + frequenciesMap(filteredCombo) + "\n");
        // At this point filteredCombo is a the List of all the sets that can make up the magic square

        // In order to progress forward, we take one of these sets and either assign it to the row or column set. 
        // From this random set, we collect all the pieces that mkae up the rows and the columns of the magic square.
        // To ensure that the diagonals are collected as well we sift through the row and column sets to see if there is more than one matching number
        // If there is more than one matching number its a permutation piece otherwise it may be used to construct the magic square. 
        // I guess that means we have to loop through both the columns and the rows.

        //System.out.println(filteredCombo.size());

  
        return filteredCombo;
    }

    public static List<List<Set<Integer>>> rowFilterLayer1(List<Set<Integer>> filtered){ // brute force all the row groupings
        List<List<Set<Integer>>> all = new ArrayList<>();
        List<Set<Integer>> rows = new ArrayList<>();
        boolean contains = false;
        for(Set<Integer> set: filtered){ // go though each one once
            for(Set<Integer> innerSet: filtered){ // comparison
                rows.add(set); // initalize this here so that we can reset each time. 
                for(int n: innerSet){
                    if(rows.get(0).contains(n)){
                        contains = true;
                        break;
                    }
                }
                if(!contains){ // doesn't contain any of the values
                    rows.add(innerSet);
                }
                if(rows.size() > 1){
                    all.add(rows);
                }
                rows = new ArrayList<>(); // delete and restart
                contains = false;

            }

        }

        return all;
    }

    // Do the previous step again but now with more subsets
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
            } else{
                rows.add(subset);
                all.add(rows);
                rows = new ArrayList<>();
            }
        }
            
        return all;
    }

    // Now that we have all the row groupings,
    // we know that the columns of all magic squares are in those groupings
    // can we loop through the set of all row groupings and find columns for those groups? 

    // SIDENOTE WE MUST FIRST FIND ALL UNIQUE ROW GROUPINGS BEFORE WE CAN MOVE FORWARD

    public static List<List<Set<Integer>>> filterUniqueRows (List<List<Set<Integer>>> all, int n){
        List<List<Set<Integer>>> uniqueRows = new ArrayList<>();
        int count = 0;
        int maxCount = 0;
        int index = 0;
        // loop through each of the groups
        // comparing them to the rest of the groups
        for(List<Set<Integer>> group: all){
                index++;
                maxCount = 0;
            for(int i = index; i < all.size(); i++){ // we have to count the max after this entire loop is done
                // compare all the subsets in the group set with the grouping from this loop,
                count = 0; // for each new group reset the count. 
                for(Set<Integer> groupSet: group){ 
                    if(all.get(i).contains(groupSet)){
                        count++;
                    }
                }
                
                if(count == n){ // if at any point another set with the same rows was found break this loop and move onto the next row
                    maxCount = count;
                    break;
                } else {
                    if(count > maxCount){
                        maxCount = count;
                    }
                }
            }
            if(maxCount < n){
                uniqueRows.add(group);
            }
        }

        return uniqueRows;
    }

    public static List<List<List<Set<Integer>>>> findColumnsGivenRows(List<List<Set<Integer>>> uniqueGroupings, int n){
        List<List<List<Set<Integer>>>> groupings = new ArrayList<>();
        List<List<Set<Integer>>> group = new ArrayList<>();
        int count = 0;
        int index = 0;

        // We have the groupings for all unique magic squares of size n and now must combine these to see which rows match with which columns
        for(List<Set<Integer>> groups: uniqueGroupings){
            index++;
            for(int i = index; i < uniqueGroupings.size(); i++){
                for(Set<Integer> groupsSet: groups){
                    for(int j = 0; j < uniqueGroupings.get(i).size(); j++){
                        count = 0;
                        for(int k: uniqueGroupings.get(i).get(j)){
                            if(groupsSet.contains(k)){
                                count++;
                            }
                        }
                        if(count != 1){
                            break; // keeps the value of count| Need to get the next iteration of i. 
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
