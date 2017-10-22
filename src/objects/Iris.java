package objects;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_32F;
import static org.opencv.core.CvType.CV_64F;

public class Iris {
    private Border insideBorder;

    private Border outsideBorder;

    private Mat originalMat;
    private Mat irisMat;
    private byte irisCode[][];

    private int rowsIrisMat = 256;
    private int colsIrisMat = 128;

    //builder for iris
    public static class Builder {
        private Iris iris;

        public Builder() {
            this.iris = new Iris();
        }

        public Builder withMat(Mat originalMat){
            iris.setOriginalMat(originalMat);
            return this;
        }

        public Builder withInsideBorder(Border insideBorder){
            iris.setInsideBorder(insideBorder);
            return this;
        }

        public Builder withOutsideBorder(Border outsideBorder){
            iris.setOutsideBorder(outsideBorder);
            return this;
        }

        public Iris build() {
            return iris;
        }
    }

    //constructor
    public Iris() {
    }

    //getter for InsideBorder
    public Border getInsideBorder() {
        return insideBorder;
    }

    //getter for OutsideBorder
    public Border getOutsideBorder() {
        return outsideBorder;
    }

    //getter for OriginalMat
    public Mat getOriginalMat() {
        return originalMat;
    }

    //getter for IrisCode
    public Mat getIrisMat() {
        return irisMat;
    }

    //getter for RowsIrisMat
    public byte[][] getIrisCode() {
        return irisCode;
    }

    //getter for RowsIrisMat
    public int getRowsIrisMat() {
        return rowsIrisMat;
    }

    //getter for ColsIrisMat
    public int getColsIrisMat() {
        return colsIrisMat;
    }

    //setter for InsideBorder
    public void setInsideBorder(Border insideBorder) {
        this.insideBorder = insideBorder;
    }

    //setter for OutsideBorder
    public void setOutsideBorder(Border outsideBorder) {
        this.outsideBorder = outsideBorder;
    }

    //setter for OriginalMat
    public void setOriginalMat(Mat originalMat) {
        this.originalMat = originalMat;
    }

    //setter for OriginalMat
    public void setIrisMat(Mat irisMat) {
        this.irisMat = irisMat;
    }

    //setter for IrisCode
    public void setIrisCode(byte[][] irisCode) {
        this.irisCode = irisCode;
    }

    //setter for RowsIrisMat
    public void setRowsIrisMat(int rowsIrisMat) {
        this.rowsIrisMat = rowsIrisMat;
    }

    //setter for ColsIrisMat
    public void setColsIrisMat(int colsIrisMat) {
        this.colsIrisMat = colsIrisMat;
    }

    // toString
    @Override
    public String toString() {

        return "Iris{" +
                "insideBorder = " + insideBorder +
                ", outsideBorder = " + outsideBorder +
                '}';
    }

    //class Border
    public static class Border{
        private int x, y, r;

        public Border(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        public Border() {
            this.x = 0;
            this.y = 0;
            this.r = 0;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getR() {
            return r;
        }

        public void setR(int r) {
            this.r = r;
        }

        @Override
        public String toString() {
            return "Border{" +
                    "X = " + x +
                    ", Y = " + y +
                    ", R = " + r +
                    '}';
        }
    }

    // convert iris to mat
    public Mat irisToMat(){
        if(irisMat == null) {
            irisMat = new Mat((int) colsIrisMat, (int) rowsIrisMat, 6);

            double c1 = 1.0d * (1d / 128d);
            double c2 = 2.0d * (3.14d / 256d);

            for (int y = 0; y < colsIrisMat; ++y) {
                for (int x = 0; x < rowsIrisMat; ++x) {
                    double p = c1 * y;
                    double O = c2 * x;

                    int newX = (int) Math.round((1 - p) * (outsideBorder.getX() + outsideBorder.getR() * Math.cos(O)) + p * (insideBorder.getX() + insideBorder.getR() * Math.cos(O)));
                    int newY = (int) Math.round((1 - p) * (outsideBorder.getY() + outsideBorder.getR() * Math.sin(O)) + p * (insideBorder.getY() + insideBorder.getR() * Math.sin(O)));

                    irisMat.put(y, x, originalMat.get(newX, newY));
                }
            }
        }

        return irisMat;
    }

    // class for Gabor settings
    public static class SettingsForGabor{
        private double ksize = 3;
        private double sigma = 1.5;
        private double theta = 1.5;
        private double lambd = 0.5;
        private double gamma = 0.5;

        public SettingsForGabor() {
        }

        public double getKsize() {
            return ksize;
        }

        public void setKsize(double ksize) {
            this.ksize = ksize;
        }

        public double getSigma() {
            return sigma;
        }

        public void setSigma(double sigma) {
            this.sigma = sigma;
        }

        public double getTheta() {
            return theta;
        }

        public void setTheta(double theta) {
            this.theta = theta;
        }

        public double getLambd() {
            return lambd;
        }

        public void setLambd(double lambd) {
            this.lambd = lambd;
        }

        public double getGamma() {
            return gamma;
        }

        public void setGamma(double gamma) {
            this.gamma = gamma;
        }

        @Override
        public String toString() {
            return "SettingsForGabor{" +
                    "ksize=" + ksize +
                    ", sigma=" + sigma +
                    ", theta=" + theta +
                    ", lambd=" + lambd +
                    ", gamma=" + gamma +
                    '}';
        }
    }

    // convert irisMat to irisCode
    public byte[][] irisMatToIrisCode(SettingsForGabor settingsForGabor){
        irisCode = new byte[irisMat.cols() * 2][irisMat.rows()];

        double ksize = settingsForGabor.getKsize();
        double sigma = settingsForGabor.getSigma();
        double theta = settingsForGabor.getTheta();
        double lambd = settingsForGabor.getLambd();
        double gamma = settingsForGabor.getGamma();

        Mat realKernel = Imgproc.getGaborKernel(new Size(ksize, ksize), sigma, theta, lambd, gamma, 0, CV_64F);
        Mat imKernel = Imgproc.getGaborKernel(new Size(ksize, ksize), sigma, theta, lambd, gamma, 3.14/2, CV_64F);

        Mat realMat = irisMat.clone();
        Mat imMat = irisMat.clone();

        Imgproc.filter2D(realMat, realMat, 6, realKernel);
        Imgproc.filter2D(imMat, imMat, 6, imKernel);

        Imgcodecs.imwrite("tempSaveForMehod\\real.bmp", realMat);
        Imgcodecs.imwrite("tempSaveForMehod\\im.bmp", imMat);

        for(int i = 0; i < irisMat.rows(); ++i){
            int k = 0;

            for(int j = 0; j < irisMat.cols(); ++j){

                if(realMat.get(i, j)[0] < 0)
                    irisCode[k][i] = 0;
                else
                    irisCode[k][i] = 1;

                if(imMat.get(i, j)[0] < 0)
                    irisCode[k + 1][i] = 0;
                else
                    irisCode[k + 1][i] = 1;
                k = k + 2;
            }
        }

        return irisCode;
    }

}
