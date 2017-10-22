package methods.methodsOfSearchBoards;

import objects.Iris;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

public class HoughMethod {

    private Settings insideSettings,
            outsideSettings;

    private int areaSize = 8;

    public HoughMethod(Settings insideSettings, Settings outsideSettings) {
        this.insideSettings = insideSettings;
        this.outsideSettings = outsideSettings;
    }

    public HoughMethod(Settings insideSettings, Settings outsideSettings, int areaSize) {

        this.insideSettings = insideSettings;
        this.outsideSettings = outsideSettings;
        this.areaSize = areaSize;
    }


    public Iris SearchIris(Mat in){
        Mat origin = in.clone();

        Mat temp = in.clone();
        int acc[][][];
        int max, resX, resY, resR;


        //Inside Border
        Imgproc.threshold(temp, temp, insideSettings.getTresh(), 255, THRESH_BINARY);
        Imgproc.Sobel(temp, temp, 0, 1, 1);

        acc = new int[temp.rows()][temp.cols()][insideSettings.getMaxR()];

        for(int i = 0; i < temp.rows(); i++){
            for(int j = 0; j < temp.cols(); j++){
                for(int k = 0; k < insideSettings.getMaxR(); k++){
                    acc[i][j][k] = 0;
                }
            }
        }

        max = 0;
        resX = 0;
        resY = 0;
        resR = 0;

        for(int x = insideSettings.getDistance(); x < temp.rows() - insideSettings.getDistance(); x++) {
            for (int y = insideSettings.getDistance(); y < temp.cols() - insideSettings.getDistance(); y++) {
                if (temp.get(x, y)[0] > 0) {
                    int x0, y0, Width0, Height0;

                    if (x > insideSettings.getMaxR())
                        x0 = x - insideSettings.getMaxR();
                    else
                        x0 = 0;

                    if (x > temp.rows() - insideSettings.getMaxR())
                        Width0 = temp.rows();
                    else
                        Width0 = x + insideSettings.getMaxR();

                    for (; x0 < Width0; x0++) {
                        if (y > insideSettings.getMaxR())
                            y0 = y - insideSettings.getMaxR();
                        else
                            y0 = 0;

                        if (y > temp.cols() - insideSettings.getMaxR())
                            Height0 = temp.cols();
                        else
                            Height0 = y + insideSettings.getMaxR();

                        for (; y0 < Height0; y0++) {
                            double r = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
                            if (insideSettings.getMinR() < Math.round(r) && Math.round(r) < insideSettings.getMaxR()) {
                                acc[x0][y0][(int) Math.round(r)]++;
                                if (acc[x0][y0][(int) Math.round(r)] > max) {
                                    resR = (int) Math.round(r);
                                    resX = x0;
                                    resY = y0;
                                    max = acc[x0][y0][(int) Math.round(r)];
                                }
                            }
                        }
                    }
                }
            }
        }

        Iris.Border inside = new Iris.Border(resX, resY, resR);


        outsideSettings.setMinR(resR + 25);

        //Outside Border
        temp = in.clone();
        Imgproc.threshold(temp, temp, outsideSettings.getTresh(),255,THRESH_BINARY);
        Imgproc.Sobel(temp, temp, 0, 1, 1);

        acc = new int[temp.rows()][temp.cols()][outsideSettings.getMaxR()];

        for(int i = 0; i < temp.rows(); i++){
            for(int j = 0; j < temp.cols(); j++){
                for(int k = 0; k < outsideSettings.getMaxR(); k++){
                    acc[i][j][k] = 0;
                }
            }
        }

        max = 0;
        resX = 0;
        resY = 0;
        resR = 0;

        for(int x = inside.getX() - outsideSettings.getMaxR(); x < inside.getX() + outsideSettings.getMaxR(); x++) {
            for (int y = inside.getY() - outsideSettings.getMaxR(); y < inside.getY() + outsideSettings.getMaxR(); y++) {
                if (temp.get(x, y)[0] > 0) {
                    int x0 = 0, y0 = 0;


                    for (x0 = inside.getX() - areaSize; x0 < inside.getX() + areaSize; x0++) {

                        for (y0 = inside.getY() - areaSize; y0 < inside.getY() + areaSize; y0++) {
                            double r = Math.sqrt((x - x0) * (x - x0) + (y - y0) * (y - y0));
                            if (outsideSettings.getMinR() < Math.round(r) && Math.round(r) < outsideSettings.getMaxR()) {
                                acc[x0][y0][(int) Math.round(r)]++;
                                if (acc[x0][y0][(int) Math.round(r)] > max) {
                                    resR = (int) Math.round(r);
                                    resX = x0;
                                    resY = y0;
                                    max = acc[x0][y0][(int) Math.round(r)];
                                }
                            }
                        }
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
