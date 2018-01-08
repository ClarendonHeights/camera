package opencv;

import op.Option;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import ui.ImageGUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static op.Status.*;

/**
 * @author Paranoid
 * @description
 * @date 2017/10/17
 * @company 美衫
 **/
public class Camera {

    public final static String dir = System.getProperty("user.dir");  //bin下

    private String fileName = "";

    private int i=1;

    public void run() {

        String path = "";
        //windows下
        if ("\\".equals(File.separator)) {
            path = dir + File.separator + "opencv_java330.dll";
        }
        //linux下
        if ("/".equals(File.separator)) {
            path = dir + File.separator + "opencv_java330.so";
        }
        System.load(path);
        // 打开摄像头或者视频文件
        VideoCapture capture = new VideoCapture();

        Mat dst = new Mat();
        capture.open(0);
        if (!capture.isOpened()) {
            System.out.println("摄像头打开失败！");
            return;
        }

        int frame_width = (int) capture.get(3);
        int frame_height = (int) capture.get(4);
        ImageGUI gui = null;

        VideoWriter writer = null;
        Size frameSize = new Size(frame_width, frame_height);
        if (Option.status.equals(cameraRec)) {
            writer = new VideoWriter();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-HH-mm-ss");
            Date date = new Date();
            fileName = dir + File.separator + formatter.format(date) + ".avi";
            writer.open(fileName, VideoWriter.fourcc('M', 'J', 'P', 'G'), 10, frameSize, true);
        } else if (Option.status.equals(cameraStart) || Option.status.equals(camearTest)) {
            gui = new ImageGUI();
            gui.createWin("ceeety  摄像头调试", new Dimension(frame_width, frame_height));
        }


        Mat frame = new Mat();
        int frameNum = 0;
        while (true) {
            frameNum++;
            boolean have = capture.read(frame);
            // Core.flip(frame, frame, 1);// 摄像头画面翻转
            if (!have) break;
            if (!frame.empty()) {
                if (Option.status.equals(cameraStart)) {
                    BufferedImage image = conver2Image(frame);
                    gui.imShow(image);
                    gui.repaint();
                } else if (Option.status.equals(cameraRec)) {
                    writer.write(frame);
                } else if (Option.status.equals(camearTest)) {
                    //对图片进行处理，亮度等调节
                    //感兴趣的区域===>通道  从配置文件中拿到多边形的各个顶点
                    Rect roi = new Rect(0, (frame_height / 2), frame_width, (frame_height / 2));
                    Mat backF = frame.clone();
                    Imgproc.rectangle(backF, new Point(0, frame_height / 2), new Point(frame_width, frame_height / 2), new Scalar(255, 255, 0));
                    dst = features(frame);
                    frame.copyTo(dst);
                    BufferedImage image = conver2Image(backF);
                    gui.imShow(image);
                    gui.repaint();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Option.status.equals(cameraRec)) {
                File file = new File(fileName);
                if (file.exists() && file.isFile()) {
                    if (file.length() > 52428800) {
                        System.out.println("第"+ (i++) +"个视频文件");
                        writer.release();
                        writer = new VideoWriter();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-HH-mm-ss");
                        Date date = new Date();
                        fileName = dir + File.separator + formatter.format(date) + ".avi";
                        writer.open(fileName, VideoWriter.fourcc('M', 'J', 'P', 'G'), 24, frameSize, true);
                    }
                }
            }


        }

    }

    public BufferedImage conver2Image(Mat mat) {
        int width = mat.cols();
        int height = mat.rows();
        int dims = mat.channels();
        int[] pixels = new int[width * height];
        byte[] rgbdata = new byte[width * height * dims];
        mat.get(0, 0, rgbdata);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int index = 0;
        int r = 0, g = 0, b = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (dims == 3) {
                    index = row * width * dims + col * dims;
                    b = rgbdata[index] & 0xff;
                    g = rgbdata[index + 1] & 0xff;
                    r = rgbdata[index + 2] & 0xff;
                    pixels[row * width + col] = ((255 & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | b & 0xff;
                }
                if (dims == 1) {
                    index = row * width + col;
                    b = rgbdata[index] & 0xff;
                    pixels[row * width + col] = ((255 & 0xff) << 24) | ((b & 0xff) << 16) | ((b & 0xff) << 8) | b & 0xff;
                }
            }
        }
        setRGB(image, 0, 0, width, height, pixels);
        return image;
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public void setRGB(BufferedImage image, int x, int y, int width, int height, int[] pixels) {
        int type = image.getType();
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)
            image.getRaster().setDataElements(x, y, width, height, pixels);
        else
            image.setRGB(x, y, width, height, pixels, 0, width);
    }


    /**
     * 特征识别，使用头部分类器
     *
     * @param mat
     * @return
     */
    public static Mat features(Mat mat) {
        HOGDescriptor hogDescriptor = new HOGDescriptor();
        MatOfFloat matOfFloat = new MatOfFloat();

        hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());

        MatOfRect found = new MatOfRect();
        MatOfDouble matOfDouble = new MatOfDouble();
        hogDescriptor.detectMultiScale(mat, found, matOfDouble);
        //   List<Double> doubles=matOfDouble.toList();
        List<Rect> rects = found.toList();
        int size = 0;
        for (Rect r : rects) {
            Imgproc.rectangle(mat, new Point(r.x, r.y), new Point(r.x + r.width, r.y + r.height), new Scalar(255, 123, 156));
            //   Imgproc.putText(mat,doubles.get(0)*100+"%" , new Point(r.x, r.y), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255), 1);
            //    size++;
        }
//
//
//
//
//        System.out.println("************************");
//        CascadeClassifier faceDetector = new CascadeClassifier();
//
//        //头部分类器  检测头部尺寸范围为10*10-28*28
//        // faceDetector.load(dir + File.separator + "cascades.xml");
//        faceDetector.load("C:\\Users\\Paranoid\\Desktop\\cart\\cascade.xml");
//
//        // Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);  //RGB图检测浪费时间，转化为灰度图
//        //需要识别的图片
//        MatOfRect faceDetections = new MatOfRect();
//
//        faceDetector.detectMultiScale(mat, faceDetections);
//        Mat backMat = mat.clone();
//        Random random = new Random();
//        int b = random.nextInt(10);
//        roi_num++;
//        for (Rect rect : faceDetections.toArray()) {
//
//            if (rect.width>10 && rect.width < 100) {
//                //扩大选区，通过深度学习，判断识别的准确率，当达到90%以上，执行逻辑
//
//                if(roi_num%10==1){
//                    Mat bm = new Mat(backMat, rect);
//                    Imgproc.resize(bm, bm, new Size(200, 200));
//                    Imgcodecs.imwrite("F:\\sample\\error\\c" + (roi_num) + ".png", bm);
//                }
//                Imgproc.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 123, 156));
//                Imgproc.putText(mat, rect.width + "", new Point(rect.x, rect.y), Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255, 255, 255), 1);
//            }
//
//        }
//        System.out.println();
        return mat;

    }

    /**
     * 图片亮度对比度调整
     *
     * @param image
     */
    public Mat op(Mat image) {
        // 亮度提升
        Mat dst = new Mat();
        Mat black = Mat.zeros(image.size(), image.type());
        Core.addWeighted(image, 1.5, black, 0.5, 10, dst);
        return dst;
    }

}
