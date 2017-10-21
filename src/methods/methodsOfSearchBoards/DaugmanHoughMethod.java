package methods.methodsOfSearchBoards;

import org.opencv.core.Mat;

public class DaugmanHoughMethod {
    private Settings insideSettings,
            outsideSettings;
    private int areaSize = 8;


    private int Gauss_sigma = 3;
    private double Gauss_1div_sigma_sqrt_2_pi = 1 / (3 * Math.sqrt(6.28));
    private double Gauss_2_sqr_sigma = 18.0;

    private double funcGauss(int x){
        double res = Gauss_1div_sigma_sqrt_2_pi * Math.exp( - 1.0 * Math.pow(x, 2) / Gauss_2_sqr_sigma) ;
        return res;
    }

    private int sumCircle(int X, int Y, int R, Mat in){

        int sum = 0;

        int x1 = X + R;
        int y1 = Y;

        while(true){
            if(0 <= x1 && x1 < in.rows() && 0 <= y1 && y1 < in.cols())
                sum = sum + (int)(in.get(x1, y1)[0]);
            if(0 <= X - (x1 - X) && X - (x1 - X) < in.rows() && 0 <= y1 && y1 < in.cols())
                sum = sum + (int)in.get(X - (x1 - X), y1)[0];
            if(0 <= X - (x1 - X) && X - (x1 - X) < in.rows() && 0 <= Y - (y1 - Y) && Y - (y1 - Y) < in.cols())
                sum = sum + (int)in.get(X - (x1 - X),Y - (y1 - Y))[0];
            if(0 <= x1 && x1 < in.rows() && 0 <= Y - (y1 - Y) && Y - (y1 - Y) < in.cols())
                sum = sum + (int)in.get(x1, Y - (y1 - Y))[0];

            ++y1;
            double x1_1 = Math.sqrt(R * R - (y1 - Y) * (y1 - Y));
            x1 = (int)Math.round(X + x1_1);

            if(x1 == X )
                break;
        }

        return sum;
    }

    //class for search Settigs
    public static class Settings{
        private int minR;
        private int maxR;
        private int tresh;
        private int distance;

        public Settings() {
        }

        public Settings(int minR, int maxR, int tresh, int distance) {
            this.minR = minR;
            this.maxR = maxR;
            this.tresh = tresh;
            this.distance = distance;
        }

        public int getMinR() {
            return minR;
        }

        public void setMinR(int minR) {
            this.minR = minR;
        }

        public int getMaxR() {
            return maxR;
        }

        public void setMaxR(int maxR) {
            this.maxR = maxR;
        }

        public int getTresh() {
            return tresh;
        }

        public void setTresh(int tresh) {
            this.tresh = tresh;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public String toString() {
            return "settings{" +
                    "minR=" + minR +
                    ", maxR=" + maxR +
                    ", tresh=" + tresh +
                    ", maxDistance=" + distance +
                    '}';
        }
    }

    public DaugmanHoughMethod(Settings insideSettings, Settings outsideSettings) {
        this.insideSettings = insideSettings;
        this.outsideSettings = outsideSettings;
    }

    @Override
    public String toString() {
        return "HoughMethod{" +
                "insideSettings=" + insideSettings + "\n" +
                ", outsideSettings=" + outsideSettings + "\n" +
                ", areaSize=" + areaSize +  "\n" +
                '}';
    }

    //setter for AreaSize
    public void setAreaSize(int areaSize) {
        this.areaSize = areaSize;
    }

    //setter for InsideSettings
    public void setInsideSettings(Settings insideSettings) {
        this.insideSettings = insideSettings;
    }

    //setter for OutsideSettings
    public void setOutsideSettings(Settings outsideSettings) {
        this.outsideSettings = outsideSettings;
    }

    //getter for AreaSize
    public int getAreaSize() {
        return areaSize;
    }

    //getter for OutsideSettings
    public Settings getOutsideSettings() {
        return outsideSettings;
    }

    //getter for InsideSettings
    public Settings getInsideSettings() {
        return insideSettings;
    }
}