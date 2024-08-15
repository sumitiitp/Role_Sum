import java.io.IOException;
import java.util.Random;
import java.util.Arrays;
import java.util.LinkedList;

public class Role_Sum {
    public static void main(String[] args) throws IOException {
        int n = 10; // Number of points
        int d = 5; // Dimension
        long seed = 44; // Seed value
        Point[] population = new Point[n];
        for(int i = 0; i < n; i++) {
            population[i] = new Point(d);
        }
        population = geneartedata(n, d, seed);
        
        Helper helper = new Helper();
        helper.sortENS(population);
        System.out.println("Number of dominance comparisons by ENS = " + Global.noDC_ENS);
        helper.sortPalakonda(population);
        System.out.println("Number of dominance comparisons by Palakonda = " + Global.noDC_Palakonda);
        helper.sortSumENS(population);
        System.out.println("Number of dominance comparisons by sum-based ENS = " + Global.noDC_sumENS);
        
    }
    
    public static Point[] geneartedata(int n, int d, long seed) {
        Random random = new Random(seed);
        Point[] population = new Point[n];
        for (int i = 0; i < n; ++i) {
            population[i] = new Point(d);
            double[] point = new double[d];
            for (int j = 0; j < d; ++j) {
                point[j] = random.nextDouble();
            }
            population[i].setObjectives(point);
            population[i].setId(i);
        }
        return population;
    }
}


class Point {
    private int id;
    private double[] objectives;
    private double sumObjectives;
              
    public Point () {
        
    }
    
    public Point(int noObjectives) {
        this.objectives = new double[noObjectives];
    }
    
    public Point(Point p) {
        this.id = p.id;
        this.objectives = new double[p.objectives.length];
        for(int i = 0; i < p.objectives.length; i++) {
            this.objectives[i] = p.objectives[i];
        }
        this.sumObjectives = p.sumObjectives;
    }
        
    public Point(int id, double[] objectives, double sumObjectives) {
        this.id = id;
        this.objectives = new double[objectives.length];
        for(int i = 0; i < objectives.length; i++) {
            this.objectives[i] = objectives[i];
        }
        this.sumObjectives = sumObjectives;
    }

    public int getId() {
        return this.id;
    }

    public double[] getObjectives() {
        return this.objectives;
    }

    public int getNoObjectives() {
        return this.objectives.length;
    }
    
    public double getObjective(int index) {
        return this.objectives[index];
    }

    public double getSumObjectives() {
        return this.sumObjectives;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setObjectives(double[] objectives) {
        this.objectives = new double[objectives.length];
        for(int i = 0; i < objectives.length; i++) {
            this.objectives[i] = objectives[i];
        }
    }
    
    public void setObjective(double objective, int index) {
        this.objectives[index] = objective;
    }

    public int noObjectives(){
        return this.objectives.length;
    }

    public void setSumObjectives(double sumObjectives) {
        this.sumObjectives = sumObjectives;
    }


    @Override
    public String toString() {
        return "Point{" + "id=" + this.id + ", objectives=" + Arrays.toString(this.objectives) + ", sumObjectives=" + this.sumObjectives + '}';
    }

    /* Used for pre-sorting as in ENS
     * 1: First point is having small value for a objctive 
      -1: Second point is having small value for a objctive 
       0: Same */
    public int isSmall(Point p) {
        int noObjectives = p.getObjectives().length;
        for(int i = 0; i < noObjectives; i++) {
            if(this.objectives[i] < p.objectives[i]) {
                return 1;
            } else if (this.objectives[i] > p.objectives[i]) {
                return -1;
            }
        }
        return 0;
    }
    
    public boolean dominanceRelationshipENS(Point p) {
        boolean flag = true; // objectives and p are identical
        int noObjectives = p.objectives.length;
        for(int i = 0; i < noObjectives; i++) {
            if(p.objectives[i] < this.objectives[i]) {
                flag = false; // objectives and p are not identical
                return false; // objectives does not dominate p
            } else if(p.objectives[i] > this.objectives[i]) {
                flag = false; // objectives and p are not identical
            } else {
                
            }
        }
        if(flag == true) { // objectives and p are identical, so objectives does not dominate p
            return false;
        } else {
            return true; // objectives dominates p
        } 
    }
}


class Helper {
    /* ENS Approach: Main function */
    public void sortENS(Point population[]) {
        System.out.println("Sorting using ENS...");
        int populationSize = population.length;
        LinkedList<LinkedList<Integer>> setF = new LinkedList<>(); 

        Global.noDC_ENS = 0;
        int[] Q0 = new int[populationSize];
        HeapSort hs = new HeapSort();
        Q0 = hs.sortENS(population);
        
        for(int i = 0; i < populationSize; i++) {
            ENSSS(setF, Q0[i], population);
        }
    }
    
    /* ENS Approach: Helping function */
    public void ENSSS(LinkedList<LinkedList<Integer>> setF, int p, Point population[]) {
        boolean isInsertion = false;
        if(setF.isEmpty()) {
            LinkedList<Integer> newF = new LinkedList<>(); 
            newF.add(p);
            setF.add(newF);
        } else {
            for (LinkedList<Integer> F : setF) {
                boolean isDominated = false;
                for (Integer point : F) {
                    Global.noDC_ENS++;
                    if(population[point].dominanceRelationshipENS(population[p])) {
                        isDominated = true;
                    }
                }
                if(isDominated == false) {
                    F.add(p);
                    isInsertion = true;
                    break;
                }
            }
            if(isInsertion == false) {
                LinkedList<Integer> newF = new LinkedList<>(); 
                newF.add(p);
                setF.add(newF);
            }
        }    
    }
    
    /* Palakonda Sum based Approach: Main function */
    public void sortPalakonda(Point population[]) {
        System.out.println("Sorting using Palakonda method...");
        int populationSize = population.length;
        LinkedList<LinkedList<Integer>> setF = new LinkedList<>(); 

        Global.noDC_Palakonda = 0;
        int[] Q0 = new int[populationSize];
        HeapSort hs = new HeapSort();
        Q0 = hs.sortSumofObj(population);
        
        for(int i = 0; i < populationSize; i++) {
            PalakondaSS(setF, Q0[i], population);
        }
    }
    
    /* Palakonda Sum based Approach: Helping function */
    public void PalakondaSS(LinkedList<LinkedList<Integer>> setF, int p, Point population[]) {
        boolean isInsertion = false;
        if(setF.isEmpty()) {
            LinkedList<Integer> newF = new LinkedList<>(); 
            newF.add(p);
            setF.add(newF);
        } else {
            for (LinkedList<Integer> F : setF) {
                boolean isDominated = false;
                for (Integer point : F) {
                    Global.noDC_Palakonda++;
                    if(population[point].dominanceRelationshipENS(population[p])) {
                        isDominated = true;
                    }
                }
                if(isDominated == false) {
                    F.add(p);
                    isInsertion = true;
                    break;
                }
            }
            if(isInsertion == false) {
                LinkedList<Integer> newF = new LinkedList<>(); 
                newF.add(p);
                setF.add(newF);
            }
        }    
    }
    
    /* Sum based ENS Approach: Main function */
    public void sortSumENS(Point population[]) {
        System.out.println("Sorting using sum based ENS...");
        int populationSize = population.length;
        int noObjectives = population[0].noObjectives();
        LinkedList<LinkedList<Integer>> setF = new LinkedList<>(); 

        Global.noDC_sumENS = 0;
        for(int i = 0; i < populationSize; i++) {
            double sum = 0;
            for(int j = 0; j < noObjectives; j++) {
                sum = sum + population[i].getObjective(j);
            }
            population[i].setSumObjectives(sum);
        }

        int[] Q0 = new int[populationSize];
        HeapSort hs = new HeapSort();
        Q0 = hs.sortENS(population);
        
        for(int i = 0; i < populationSize; i++) {
            sumSSENS(setF, Q0[i], population);
        }
    }
    
    /* Sum based ENS Approach: Helping function */
    public void sumSSENS(LinkedList<LinkedList<Integer>> setF, int p, Point population[]) {
        boolean isInsertion = false;
        if(setF.isEmpty()) {
            LinkedList<Integer> newF = new LinkedList<>(); 
            newF.add(p);
            setF.add(newF);
        } else {
            for (LinkedList<Integer> F : setF) {
                boolean isDominated = false;
                for (Integer point : F) {
                    if(population[p].getSumObjectives() >= population[point].getSumObjectives()) {
                        Global.noDC_sumENS++;
                        if(population[point].dominanceRelationshipENS(population[p])) {
                            isDominated = true;
                        }
                    }
                }
                if(isDominated == false) {
                    F.add(p);
                    isInsertion = true;
                    break;
                }
            }
            if(isInsertion == false) {
                LinkedList<Integer> newF = new LinkedList<>(); 
                newF.add(p);
                setF.add(newF);
            }
        }    
    }
}


class HeapSort {
    void heapifyFirstObjective(int arr[], int heapSize, int i, Point population[]) {
        int largest = i;  // Initialize largest as root
        int l = 2*i + 1;  // left = 2*i + 1
        int r = 2*i + 2;  // right = 2*i + 2
        
        if (l < heapSize && population[arr[l]].isSmall(population[arr[largest]]) == -1) {
            largest = l;
        }
 
        if (r < heapSize && population[arr[r]].isSmall(population[arr[largest]]) == -1) {
            largest = r;
        }

        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            heapifyFirstObjective(arr, heapSize, largest, population);
        }
    }
    
    public int[] sortENS(Point population[]) {
        int n = population.length;
        int[] Q0 = new int[n];
        for(int i = 0; i < n; i++) {
            Q0[i] = population[i].getId();
        }

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyFirstObjective(Q0, n, i, population);
        }
 
        for (int i=n-1; i>=0; i--) {
            int temp = Q0[0];
            Q0[0] = Q0[i];
            Q0[i] = temp;
            heapifyFirstObjective(Q0, i, 0, population);
        }
        return Q0;
    }

    void heapifyFirstObjectiveSumofObj(int arr[], int heapSize, int i, Point population[]) {
        int largest = i;  // Initialize largest as root
        int l = 2*i + 1;  // left = 2*i + 1
        int r = 2*i + 2;  // right = 2*i + 2
        
        if (l < heapSize && population[arr[l]].getSumObjectives() > population[arr[largest]].getSumObjectives()) {
            largest = l;
        }
 
        if (r < heapSize && population[arr[r]].getSumObjectives() > population[arr[largest]].getSumObjectives()) {
            largest = r;
        }
        
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            heapifyFirstObjectiveSumofObj(arr, heapSize, largest, population);
        }
    }
    
    public int[] sortSumofObj(Point population[]) {
        int n = population.length;
        int[] Q0 = new int[n];
        for(int i = 0; i < n; i++) {
            Q0[i] = population[i].getId();
        }
        
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyFirstObjectiveSumofObj(Q0, n, i, population);
        }
 
        for (int i=n-1; i>=0; i--) {
            int temp = Q0[0];
            Q0[0] = Q0[i];
            Q0[i] = temp;
            heapifyFirstObjectiveSumofObj(Q0, i, 0, population);
        }
        return Q0;
    }
}


class Global {
    public static int noDC_ENS;         // Number of dominance comparisons by ENS
    public static int noDC_Palakonda;   // Number of dominance comparisons by Palakonda
    public static int noDC_sumENS;      // Number of dominance comparisons by sum-based ENS
}
