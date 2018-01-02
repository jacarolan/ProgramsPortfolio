import java.util.*;

/**
 * This is just a random messing around class, too lazy to make a new project. No correlation to tangencies.
 */
public class RecursionTesting {
    public static void main() {
        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        System.out.println(allPermutations(arr, 9));
    }
    
    private static ArrayList<ArrayList<Integer>> allPermutations(int[] arr, int n) {
        ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();
        ret.add(new ArrayList<Integer>());
        int[] data = new int[n];
        addNumber(data, 0, arr, n, ret);
        /*int[][] realRet = new int[ret.size()-1][];
        for(int i = 0; i < ret.size()-1; i++) {
            int[] addRet = new int[ret.get(i).size()];
            for(int ii = 0; ii < ret.get(i).size(); ii++) {
                addRet[ii] = ret.get(i).get(ii);
            }
            realRet[i] = addRet;
        }
        return realRet;*/
        return ret;
    }
    
    private static void addNumber(int[] data, int cval, int[] arr, int n, ArrayList<ArrayList<Integer>> masterData) {
        if (cval == n) {
            for (int j=0; j<n; j++) {
                masterData.get(masterData.size()-1).add(data[j]);
            }
            masterData.add(new ArrayList<Integer>());
            return;
        }
        /*for (int i=start; i<=end && end-i+1 >= r-index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i+1, end, index+1, r, masterData);
        }*/
        for(int i = 0; i < arr.length; i++) {
            boolean contains = false;
            for(int l:arr) {
                if(l == i) {
                    contains = true;
                }
            }
            if(!contains) {
                data[cval] = arr[i];
                addNumber(data, cval+1, arr,  n, masterData);
            }
        }
    }
}
