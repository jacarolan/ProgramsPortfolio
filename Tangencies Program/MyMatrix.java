import Jama.*;
import java.util.*;

public class MyMatrix { //This does not extend because of Matrix's private access to it's value array. Instead, an inner matrix is kept which can be manipulated however we want.
    private Matrix innerMatrix;
    public MyMatrix(double[][] array) {
        innerMatrix = new Matrix(array);
    }

    public MyMatrix getBest(MyMatrix m) {
        double[][] endArray =  innerMatrix.solve(new Matrix(m.getArrayCopy())).getArrayCopy();
        double[][] valueArray = innerMatrix.getArrayCopy();
        double[][] answerArray = m.getArrayCopy();
        for(int i = 0; i < valueArray.length; i++) {
            double total = 0;
            for(int ii = 0; ii < valueArray[i].length; ii++) {
                total += valueArray[i][ii]*endArray[ii][0];
            }
        }
        return new MyMatrix(endArray);
    }

    public MyMatrix solve(MyMatrix m) {
        double[][] endArray =  innerMatrix.solve(new Matrix(m.getArrayCopy())).getArrayCopy();
        double[][] valueArray = innerMatrix.getArrayCopy();
        double[][] answerArray = m.getArrayCopy();
        for(int i = 0; i < valueArray.length; i++) {
            double total = 0;
            for(int ii = 0; ii < valueArray[i].length; ii++) {
                total += valueArray[i][ii]*endArray[ii][0];
            }
            if(Math.abs(total-answerArray[i][0]) > 0.5) {
                return null;
            }
        }
        for(double[] i:endArray) {
            if(Math.abs(i[0]) < 1.0 || Math.abs(i[0]) > 200.0) {
                return null;
            }
        }
        return new MyMatrix(endArray);
    }

    public double[][] getArrayCopy() {
        return innerMatrix.getArrayCopy();
    }

    public double[][] getArray() {
        return innerMatrix.getArray();
    }

    public void eraseRow(int rowNum) {
        double[][] cmatrix = innerMatrix.getArrayCopy();
        double[][] nvals = new double[cmatrix.length-1][cmatrix[0].length];
        int offset = 0;
        for(int i = 0; i < cmatrix.length; i++) {
            if(i != rowNum) {
                nvals[i-offset] = cmatrix[i];
            } else {
                offset++;
            }
        }
        innerMatrix = new Matrix(nvals);
    }

    public MyMatrix copyEraseRows(int[] rowNums) {
        MyMatrix retMatrix = this.getCopy();
        for(int i = 0; i < rowNums.length; i++) {
                int offset = 0;
                for(int ii = 0; ii < i; ii++) {
                    if(rowNums[ii] < rowNums[i]) {
                        offset++;
                    }
                }
                retMatrix.eraseRow(rowNums[i]-offset);
        }
        return retMatrix;
    }

    public MyMatrix copyEraseRow(int rowNum) {
        double[][] cmatrix = innerMatrix.getArrayCopy();
        double[][] nvals = new double[cmatrix.length-1][cmatrix[0].length];
        int offset = 0;
        for(int i = 0; i < cmatrix.length; i++) {
            if(i != rowNum) {
                nvals[i-offset] = cmatrix[i];
            } else {
                offset++;
            }
        }
        return new MyMatrix(nvals);
    }

    /*public MyMatrix[] allBestSolutions(MyMatrix m) {
    MyMatrix valueMatrix = this;
    MyMatrix answerMatrix = new MyMatrix(m.getArrayCopy());
    MyMatrix endMatrix;
    ArrayList<MyMatrix> ret = new ArrayList<MyMatrix>(0);
    int counts = 0;
    for(int del = 0; del < valueMatrix.getArrayCopy().length; del++) {

    for(int i = 0; i < valueMatrix.getArray().length; i++) {

    MyMatrix testValueMatrix = valueMatrix.getCopy();
    MyMatrix testAnswerMatrix = answerMatrix.getCopy();
    for(int ii = 0; ii < del; ii++) {
    testValueMatrix.eraseRow(i);
    testAnswerMatrix.eraseRow(i);
    }
    MyMatrix testEndMatrix = testValueMatrix.solve(testAnswerMatrix);
    if (testEndMatrix != null) {
    ret.add(testEndMatrix.getCopy());
    }
    }
    if(ret.size() > 0) {
    break;
    }
    }
    MyMatrix[] endRet = new MyMatrix[ret.size()];
    for(int i = 0; i < ret.size(); i++) {
    endRet[i] = ret.get(i);
    }
    return endRet;
    }*/

    public MyMatrix getCopy() {
        return new MyMatrix(innerMatrix.getArrayCopy());
    }

    public String toString() {
        double[][] vals = innerMatrix.getArrayCopy();
        String ret = "";
        for(double[] i:vals) {
            ret += "[";
            for(double ii:i) {
                ret += ii+", ";
            }
            ret += "]\n";
        }
        return ret;
    }
}
