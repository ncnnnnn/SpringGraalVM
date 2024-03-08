import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

public class MatrixMultiplicationBenchmark {
    private static final int SIZE = 512; // 矩阵的大小
    private static final int REPEAT = 10; // 性能测试的重复次数
    private static final int WARMUP = 10; // 预热的次数
    private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public static void main(String[] args) {
        addmain(null);
        float[][] a = new float[SIZE][SIZE];
        float[][] b = new float[SIZE][SIZE];
        float[][] c = new float[SIZE][SIZE];

        // 初始化矩阵 A 和 B
        initializeMatrix(a);
        initializeMatrix(b);

        // 预热
        for (int i = 0; i < WARMUP; i++) {
            matrixMultiplyVector(a, b, c);
            matrixMultiplySimple(a, b, c);
        }

        // 测试 SIMD 矩阵乘法
        long start = System.nanoTime();
        for (int i = 0; i < REPEAT; i++) {
            matrixMultiplyVector(a, b, c);
        }
        long end = System.nanoTime();
        System.out.println("Vector Time: " + (end - start) / REPEAT / 1e6 + " ms");

        // 测试普通矩阵乘法
        start = System.nanoTime();
        for (int i = 0; i < REPEAT; i++) {
            matrixMultiplySimple(a, b, c);
        }
        end = System.nanoTime();
        System.out.println("Simple Time: " + (end - start) / REPEAT / 1e6 + " ms");
    }

    private static void initializeMatrix(float[][] matrix) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = i * SIZE + j;
            }
        }
    }

    private static void matrixMultiplyVector(float[][] a, float[][] b, float[][] c) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                FloatVector vC = FloatVector.zero(SPECIES);
                for (int k = 0; k < SIZE; k += SPECIES.length()) {
                    FloatVector vA = FloatVector.fromArray(SPECIES, a[i], k);
                    FloatVector vB = FloatVector.fromArray(SPECIES, b[j], k);
                    vC = vA.fma(vB, vC); // Fused Multiply-Add
                }
                c[i][j] = vC.reduceLanes(VectorOperators.ADD);
            }
        }
    }

    private static void matrixMultiplySimple(float[][] a, float[][] b, float[][] c) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                float sum = 0.0f;
                for (int k = 0; k < SIZE; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
    }

    static float[][] a;
    // final static VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
    static int width, length;

    public static void addmain(String[] args) {
        width = 8192;
        length = 8192;
        System.out.printf("Single vector size is %.2f MB%n", width * length * 32.0 / 1024 / 1024);
        a = new float[length][width];
        // assign random value to a and b
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                a[i][j] = (float) Math.random();
            }
        }
        for (int i = 0; i < 10; i++) {
            // warm up
            addUsingVector();
        }
        long timeCostInVec = 0;
        for (int i = 0; i < 10; i++) {
            timeCostInVec += addUsingVector();
        }
        System.out.printf("Time cost in vector api is %.2f ns%n", timeCostInVec / 10.0);

        // using loop to add a and b
        for (int i = 0; i < 10; i++) {
            // warm up
            addUsingLoop();
        }
        long timeCostInLoop = 0;
        for (int i = 0; i < 10; i++) {
            timeCostInLoop += addUsingLoop();
        }
        System.out.printf("Time cost in loop is %.2f ns%n", timeCostInLoop / 10.0);
    }

    public static long addUsingVector() {
        long startTime = System.nanoTime();
        var v8sum = FloatVector.zero(SPECIES);
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j += SPECIES.length()) {
                FloatVector va = FloatVector.fromArray(SPECIES, a[i], j);
                v8sum = v8sum.add(va);
            }
        }
        float result = v8sum.reduceLanes(VectorOperators.ADD);
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static long addUsingLoop() {
        long startTime = System.nanoTime();
        float result = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                result += a[i][j];
            }
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}