package utils;


import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WindowInterface extends JFrame{

    public void imShow(Mat image){
        Window1 window1 = new Window1(image);
        window1.setVisible(true);
    }


    public static class Window1 extends JFrame{
        public Window1 (Mat image){
            Imgcodecs.imwrite("tempForWindows\\window.bmp", image);

            setTitle("window");

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setBounds(0, 0, image.cols(), image.rows());
            setLocationRelativeTo(null);
            setResizable(false);

            setContentPane(new BgPanel("tempForWindows\\window.bmp"));
        }
    }











    private static class BgPanel extends JPanel{
        Image im;

        BgPanel(String name){
            try {
                im = ImageIO.read(new File(name));
            } catch (IOException e) {}
        }
        public void paintComponent(Graphics g){
            g.drawImage(im, 0, 0, null);
        }
    }
}
