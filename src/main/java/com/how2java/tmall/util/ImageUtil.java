package com.how2java.tmall.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个是从百度抄的，原理不太懂
 * 在WIN7和WIN10环境下测试没有问题
 * LINUX环境下未测试
 */
public class ImageUtil {
    /**
     * 这段转换代码，可以确保转换后jpg的图片显示正常，但有一定几率出现暗红色
     * @param f
     * @return
     */
    public static BufferedImage change2jpg(File f) {
        try {
            //获取文件绝对路径
            Image i = Toolkit.getDefaultToolkit().createImage(f.getAbsolutePath());
            //创建一个 PixelGrabber 对象，以从指定的图像中抓取像素矩形部分
            PixelGrabber pg = new PixelGrabber(i, 0, 0, -1, -1, true);
            //请求 Image对象或ImageProducer对象开始传递像素，并等待传递完相关矩形中的所有像素。
            pg.grabPixels();
            //获取像素矩阵的宽高，注意这俩方法都有同步锁
            int width = pg.getWidth();
            int height = pg.getHeight();
            //[255,0,0] 纯红 #FF0000
            final int[] RGB_MASKS = { 0xFF0000, 0xFF00, 0xFF }; //[255,0,0] 纯红 #FF0000
            //ColorModel是DirectColorModel的顶层父类，根据RGB颜色对应的int值在构造DirectColorModel时显示什么颜色
            final ColorModel RGB_OPAQUE = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);
            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            BufferedImage img = new BufferedImage(RGB_OPAQUE, raster, false, null);
            return img;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public static void resizeImage(File srcFile, int width,int height, File destFile) {
        try {
            if(!destFile.getParentFile().exists())
                destFile.getParentFile().mkdirs();
            Image i = ImageIO.read(srcFile);
            i = resizeImage(i, width, height);
            ImageIO.write((RenderedImage) i, "jpg", destFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static Image resizeImage(Image srcImage, int width, int height) {
        try {

            BufferedImage buffImg = null;
            buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            buffImg.getGraphics().drawImage(srcImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);

            return buffImg;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
