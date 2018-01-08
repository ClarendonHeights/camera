package opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;

/**
 * @author Paranoid
 * @description 视频截图
 * @date 2018/1/8
 * @company 美衫
 **/
public class Screenshot {
    public final static String dir = System.getProperty("user.dir");

    public static void main(String[] args) {
        //加载动态链库
       String path = dir + File.separator + "opencv_java330.dll";
       System.load(path);

        VideoCapture capture=new VideoCapture("E:\\01-08-09-20-06.avi");

        if (!capture.isOpened()) {
            System.out.println("摄像头打开失败！");
            return;
        }

        Mat frame = new Mat();
        int frameNum=1100;
        while (true) {
            boolean have = capture.read(frame);
            if (!have) break;
            if (!frame.empty()) {
                Core.flip(frame,frame,1);
                int i=frameNum++;
                System.out.println(i);
                if(frameNum % 50==1) {
                    Imgcodecs.imwrite("E:\\photo\\" + i + ".png", frame);
                }
            }
        }
    }
}
