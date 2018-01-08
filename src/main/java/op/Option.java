package op;

import opencv.Camera;

import static op.Status.*;

/**
 * @author Paranoid
 * @description
 * @date 2017/10/23
 * @company 美衫
 **/
public class Option {

    public static String status = "";

    /**
     * 操作1. 启动摄像头
     */
    public void start() {

        Camera camera = new Camera();
        status = cameraStart;
        camera.run();
    }

    /**
     * 操作2. 关闭程序
     */
    public void stop() {
        status = cameraStop;
        System.exit(0);
    }

    /**
     * 视频录制
     */
    public void rec() {
        Camera camera = new Camera();
        status = cameraRec;
        camera.run();
    }

    /**
     * 人物检测
     */
    public void test() {
        Camera camera = new Camera();
        status = camearTest;
        camera.run();
    }


    public static void main(String[] args) {
        Option o = new Option();
        if (args.length>0){
            String type = args[0];
            if ("START".equalsIgnoreCase(type)) {
                o.start();
            } else if ("STOP".equalsIgnoreCase(type)) {
                o.stop();
            } else  if("REC".equalsIgnoreCase(type)){
                o.rec();
            }else if("TEST".equalsIgnoreCase(type)){
                o.test();
            }else{
                System.out.println("未知命令");
            }
        }else {
            System.out.println("无动作");
        }
    }
}
