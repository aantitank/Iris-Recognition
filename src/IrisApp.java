import methods.methodsOfSearchBoards.HoughMethod;
import objects.Iris;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import utils.WindowInterface;

public class IrisApp {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) throws Exception {

        HoughMethod.Settings settingsForInsideHoughMethod = new HoughMethod.Settings(25, 60, 55, 50);
        HoughMethod.Settings settingsForOutsideHoughMethod = new HoughMethod.Settings(60, 100, 145, 50);
        HoughMethod method = new HoughMethod(settingsForInsideHoughMethod, settingsForOutsideHoughMethod);

        //DaugmanMethod.Settings settingsForInsideDaugmanMethod = new DaugmanMethod.Settings(25, 60, 55, 50);
        //DaugmanMethod.Settings settingsForOutsideDaugmanMethod = new DaugmanMethod.Settings(60, 100, 145, 50);
        //DaugmanMethod method = new DaugmanMethod(settingsForInsideDaugmanMethod, settingsForOutsideDaugmanMethod);

        Mat im = Imgcodecs.imread("image.bmp");

        Iris iris = method.SearchIris(im);

        Mat matIris = iris.irisToMat();

        Imgcodecs.imwrite("iris.bmp", matIris);
        Iris.SettingsForGabor set = new Iris.SettingsForGabor();
        iris.irisMatToIrisCode(set);

    }
}
