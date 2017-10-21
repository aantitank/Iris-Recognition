package methods.methodsOfSearchBoards;

import javafx.util.Pair;
import objects.Iris;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.util.concurrent.Callable;

public class DaugmanMethod {
    private Settings insideSettings,
            outsideSettings;
    private int areaSize = 8;

    public Iris SearchIris(Mat in) {
        Mat origin = in.clone();

        Mat temp;
        int x = 0, y = 0, resX, resY, resR;
        double max;

        //Inside Border

        temp = in.clone();

        Imgproc.threshold(temp, temp, insideSettings.getTresh(), 255, 1);

        PlaneForInsideX planeX1 = new PlaneForInsideX(insideSettings.getDistance(), in.rows()/2, temp);
        PlaneForInsideX planeX2 = new PlaneForInsideX(in.rows()/2 + 1, in.rows() - insideSettings.getDistance(), temp);

        PlaneForInsideY planeY1 = new PlaneForInsideY(insideSettings.getDistance(), in.cols()/2, temp);
        PlaneForInsideY planeY2 = new PlaneForInsideY(in.cols()/2 + 1, in.cols() - insideSettings.getDistance(), temp);

        Pair<Integer, Integer> pairX1 = planeX1.call();
        Pair<Integer, Integer> pairX2 = planeX2.call();

        Pair<Integer, Integer> pairY1 = planeY1.call();
        Pair<Integer, Integer> pairY2 = planeY2.call();

        if(pairX1.getValue() > pairX2.getValue())
            x = pairX1.getKey();
        else
            x = pairX2.getKey();

        if(pairY1.getValue() > pairY2.getValue())
            y = pairY1.getKey();
        else
            y = pairY2.getKey();

        max = 0;
        resX = 0;
        resY = 0;
        resR = 0;

        for(int i = x - areaSize; i < x + areaSize; i++){
            for(int j = y - areaSize; j < y + areaSize; j++){
                for(int r = insideSettings.getMinR(); r < insideSettings.getMaxR(); r++){
                    double sum = 0;

                    for(int k = r - 3 * Gauss_sigma; k < r + 3 * Gauss_sigma; k++){
                        double def = funcGauss(r - k + 1) - funcGauss(r - k);
                        sum = sum + def * sumCircle(i, j, k, in);
                    }

                    if(sum > max){
                        max = sum;
                        resX = i;
                        resY = j;
                        resR = r;
                    }
                }
            }
        }

        Iris.Border inside = new Iris.Border(resX, resY, resR);

        outsideSettings.setMinR(resR + 25);

        //Outside Border

        temp = in.clone();

        Imgproc.threshold(temp, temp, outsideSettings.getTresh(), 255, 1);

        PlaneForOutsideX planeX3 = new PlaneForOutsideX(insideSettings.getDistance() , temp.rows()/2, resX, areaSize, temp);
        PlaneForOutsideX planeX4 = new PlaneForOutsideX(temp.rows()/2 + 1, temp.rows() - insideSettings.getDistance(),resX, areaSize, temp);

        PlaneForOutsideY planeY3 = new PlaneForOutsideY(insideSettings.getDistance() , temp.cols()/2, resY, areaSize, temp);
        PlaneForOutsideY planeY4 = new PlaneForOutsideY(temp.cols()/2 + 1, temp.cols() - insideSettings.getDistance(), resY, areaSize, temp);

        Pair<Integer, Integer> pairX3 = planeX3.call();
        Pair<Integer, Integer> pairX4 = planeX4.call();

        Pair<Integer, Integer> pairY3 = planeY3.call();
        Pair<Integer, Integer> pairY4 = planeY4.call();

        if(pairX3.getValue() > pairX4.getValue())
            x = pairX3.getKey();
        else
            x = pairX4.getKey();

        if(pairY3.getValue() > pairY3.getValue())
            y = pairY3.getKey();
        else
            y = pairY4.getKey();

        max = 0;
        resX = 0;
        resY = 0;
        resR = 0;

        for(int i = x - areaSize; i < x + areaSize; i++){
            for(int j = y - areaSize; j < y + areaSize; j++){
                for(int r = outsideSettings.getMinR(); r < outsideSettings.getMaxR(); r++){
                    double sum = 0;

                    for(int k = r - 3 * Gauss_sigma; k < r + 3 * Gauss_sigma; k++){
                        double def = funcGauss(r - k + 1) - funcGauss(r - k);
                        sum = sum + def * sumCircle(i, j, k, in);
                    }

                    if(sum > max){
                        max = sum;
                        resX = i;
                        resY = j;
                        resR = r;
                    }
                }
            }
        }

        Iris.Border outside = new Iris.Border(resX, resY, resR);
        Iris.Builder irisBuilder = new Iris.Builder();

        irisBuilder.withInsideBorder(inside).
                withOutsideBorder(outside).
                withMat(origin);

        return irisBuilder.build();

    };

    //settings and methods for Gauss

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

    private class PlaneForInsideX implements Callable<Pair<Integer, Integer>>{
        private int begin,
                end;
        private Mat in;

        public PlaneForInsideX(int begin, int end, Mat in) {
            this.begin = begin;
            this.end = end;
            this.in = in;
        }

        @Override
        public Pair<Integer, Integer> call() {
            Pair<Integer, Integer> res;

            int max = 0,
                    x = 0;

            for(int i = begin; i < end; i++){
                int sum = 0;
                for(int j = 0; j < in.cols(); j++){
                    sum = sum + (int)in.get(i, j)[0];
                }
                if(sum > max){
                    max = sum;
                    x = i;
                }
            }

            res = new Pair<Integer, Integer>(x , max);

            return res;
        }
    }

    private class PlaneForInsideY implements Callable<Pair<Integer, Integer>>{
        private int begin,
                end;
        private Mat in;

        public PlaneForInsideY(int begin, int end, Mat in) {
            this.begin = begin;
            this.end = end;
            this.in = in;
        }

        @Override
        public Pair<Integer, Integer> call() {
            Pair<Integer, Integer> res;

            int max = 0,
                    x = 0;

            for(int i = begin; i < end; i++){
                int sum = 0;
                for(int j = 0; j < in.rows(); j++){
                    sum = sum + (int)in.get(j, i)[0];
                }
                if(sum > max){
                    max = sum;
                    x = i;
                }
            }

            res = new Pair<Integer, Integer>(x , max);

            return res;
        }
    }

    private class PlaneForOutsideX implements Callable<Pair<Integer, Integer>>{
        private int begin,
                end,
                areaX,
                areaSize;
        private Mat in;

        public PlaneForOutsideX(int begin, int end, int areaX, int areaSize, Mat in) {
            this.begin = begin;
            this.end = end;
            this.areaX = areaX;
            this.areaSize = areaSize;
            this.in = in;
        }

        @Override
        public Pair<Integer, Integer> call() {
            Pair<Integer, Integer> res;

            int max = 0,
                    x = 0;

            for(int i = begin; i < end; i++){
                int sum = 0;
                for(int j = 0; j < in.cols(); j++){
                    sum = sum + (int)in.get(i, j)[0];
                }
                if(sum > max && areaX - areaSize <= i && i <= areaX + areaSize){
                    max = sum;
                    x = i;
                }
            }

            res = new Pair<Integer, Integer>(x , max);

            return res;
        }
    }

    private class PlaneForOutsideY implements Callable<Pair<Integer, Integer>>{
        private int begin,
                end,
                areaY,
                areaSize;
        private Mat in;

        public PlaneForOutsideY(int begin, int end, int areaY, int areaSize, Mat in) {
            this.begin = begin;
            this.end = end;
            this.areaY = areaY;
            this.areaSize = areaSize;
            this.in = in;
        }

        @Override
        public Pair<Integer, Integer> call() {
            Pair<Integer, Integer> res;

            int max = 0,
                    x = 0;

            for(int i = begin; i < end; i++){
                int sum = 0;
                for(int j = 0; j < in.rows(); j++){
                    sum = sum + (int)in.get(j, i)[0];
                }
                if(sum > max && areaY - areaSize <= i && i <= areaY + areaSize){
                    max = sum;
                    x = i;
                }
            }

            res = new Pair<Integer, Integer>(x , max);

            return res;
        }
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

    public DaugmanMethod(Settings insideSettings, Settings outsideSettings) {
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
