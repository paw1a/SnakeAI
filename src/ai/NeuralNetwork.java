package ai;

public class NeuralNetwork {

    public double[][] weights1 = new double[32][20];
    public double[][] weights2 = new double[20][12];
    public double[][] weights3 = new double[12][4];

    public double[] biases1 = new double[20];
    public double[] biases2 = new double[12];
    public double[] biases3 = new double[4];

    private final double[] weightedSum1 = new double[20];
    private final double[] weightedSum2 = new double[12];
    private final double[] weightedSum3 = new double[4];

    private final double[] activated1 = new double[20];
    private final double[] activated2 = new double[12];

    public NeuralNetwork() {
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 20; j++) {
                if(i == 0) biases1[j] = Math.random() * 2.0 - 1.0;
                weights1[i][j] = Math.random() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 12; j++) {
                if(i == 0) biases2[j] = Math.random() * 2.0 - 1.0;
                weights2[i][j] = Math.random() * 2.0 - 1.0;
            }
        }
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 4; j++) {
                if(i == 0) biases3[j] = Math.random() * 2.0 - 1.0;
                weights3[i][j] = Math.random() * 2.0 - 1.0;
            }
        }
    }

    public double[] predict(double[] inputs) {
        double[] y = new double[4];

        //Прогоняем через 1 слой
        for (int l = 0; l < 20; l++) {
            for (int m = 0; m < 32; m++) {
                weightedSum1[l] += weights1[m][l] * inputs[m];
            }
            weightedSum1[l] += biases1[l];
        }
        //Активируем второй слой
        for (int l = 0; l < 20; l++) {
            activated1[l] = reluActivation(weightedSum1[l]);
        }
        //Прогоняем через 2 слой
        for (int l = 0; l < 12; l++) {
            for (int m = 0; m < 20; m++) {
                weightedSum2[l] += weights2[m][l] * activated1[m];
            }
            weightedSum2[l] += biases2[l];
        }
        //Активируем третий слой
        for (int l = 0; l < 12; l++) {
            activated2[l] = reluActivation(weightedSum2[l]);
        }
        for (int l = 0; l < 4; l++) {
            for (int m = 0; m < 12; m++) {
                weightedSum3[l] += weights3[m][l] * activated2[m];
            }
            weightedSum3[l] += biases3[l];
        }

        //Активируем выходной слой
        double sum = 0;
        for (int l = 0; l < 4; l++) {
            sum += Math.exp(weightedSum3[l]);
        }
        for (int l = 0; l < 4; l++) {
            y[l] = Math.exp(weightedSum3[l]) / sum;
        }

        for (int k = 0; k < 20; k++) {
            weightedSum1[k] = 0;
        }
        for (int k = 0; k < 12; k++) {
            weightedSum2[k] = 0;
        }
        for (int k = 0; k < 4; k++) {
            weightedSum3[k] = 0;
        }
        return y;
    }

    public static double reluActivation(double x) { return x <= 0 ? 0 : x; }
}
