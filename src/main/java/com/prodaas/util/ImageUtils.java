package com.prodaas.util;

import com.prodaas.exception.DeltaXResolveFailException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ImageUtils {

    /**
     * 合成指定的多张图片到一张图片
     *
     * @param imgSrc           图片的地址
     * @param topLeftPointList 每张小图片的偏移量
     * @param countOfLine      每行的小图片个数
     * @param cutWidth         每张小图片截取的宽度（像素）
     * @param cutHeight        每张小图片截取的高度（像素）
     * @param savePath         合并后图片的保存路径
     * @param suffix           合并后图片的后缀
     * @return 是否合并成功
     */
    public static boolean combineImages(String imgSrc, List<String[]> topLeftPointList, int countOfLine, int cutWidth, int cutHeight, String savePath, String suffix) {
        if (imgSrc == null || savePath == null || savePath.trim().length() == 0) return false;
        BufferedImage lastImage = new BufferedImage(cutWidth * countOfLine, cutHeight * ((int) (Math.floor(topLeftPointList.size() / countOfLine))), BufferedImage.TYPE_INT_RGB);
        String prevSrc = "";
        BufferedImage prevImage = null;
        try {
            for (int i = 0; i < topLeftPointList.size(); i++) {
                String src = imgSrc;
                BufferedImage image;
                if (src.equals(prevSrc)) image = prevImage;
                else {
                    if (src.trim().toLowerCase().startsWith("http"))
                        image = ImageIO.read(new URL(src));
                    else
                        image = ImageIO.read(new File(src));
                    prevSrc = src;
                    prevImage = image;

                }
                if (image == null) continue;
                String[] topLeftPoint = topLeftPointList.get(i);
                int[] pixArray = image.getRGB(0 - Integer.parseInt(topLeftPoint[0].trim()), 0 - Integer.parseInt(topLeftPoint[1].trim()), cutWidth, cutHeight, null, 0, cutWidth);
                int startX = ((i) % countOfLine) * cutWidth;
                int startY = ((i) / countOfLine) * cutHeight;

                lastImage.setRGB(startX, startY, cutWidth, cutHeight, pixArray, 0, cutWidth);
            }
            File file = new File(savePath);
            return ImageIO.write(lastImage, suffix, file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static boolean isPixelNotEqual(BufferedImage image1, BufferedImage image2, int i, int j) {
        int pixel1 = image1.getRGB(i, j);
        int pixel2 = image2.getRGB(i, j);

        int[] rgb1 = new int[3];
        rgb1[0] = (pixel1 & 0xff0000) >> 16;
        rgb1[1] = (pixel1 & 0xff00) >> 8;
        rgb1[2] = (pixel1 & 0xff);

        int[] rgb2 = new int[3];
        rgb2[0] = (pixel2 & 0xff0000) >> 16;
        rgb2[1] = (pixel2 & 0xff00) >> 8;
        rgb2[2] = (pixel2 & 0xff);

        for (int k = 0; k < 3; k++)
            if (Math.abs(rgb1[k] - rgb2[k]) > 50)//因为背景图会有一些像素差异
                return true;

        return false;
    }


    public static int findXDiffRectangeOfTwoImage(String imgSrc1, String imgSrc2) throws DeltaXResolveFailException {
        try {
            BufferedImage image1 = ImageIO.read(new File(imgSrc1));
            BufferedImage image2 = ImageIO.read(new File(imgSrc2));
            int width1 = image1.getWidth();
            int height1 = image1.getHeight();
            int width2 = image2.getWidth();
            int height2 = image2.getHeight();

            if (width1 != width2) return -1;
            if (height1 != height2) return -1;

            for (int i = 30; i < width1 - 40; i++) {
                int gtThan200 = 0;
                for (int j = 0; j < height1; j++) {
                    int pixel1 = image1.getRGB(i, j);
                    int pixel2 = image2.getRGB(i, j);

                    int[] rgb1 = new int[3];
                    rgb1[0] = (pixel1 & 0xff0000) >> 16;
                    rgb1[1] = (pixel1 & 0xff00) >> 8;
                    rgb1[2] = (pixel1 & 0xff);

                    int[] rgb2 = new int[3];
                    rgb2[0] = (pixel2 & 0xff0000) >> 16;
                    rgb2[1] = (pixel2 & 0xff00) >> 8;
                    rgb2[2] = (pixel2 & 0xff);

                    int abs0 = Math.abs(rgb1[0] - rgb2[0]);
                    int abs1 = Math.abs(rgb1[1] - rgb2[1]);
                    int abs2 = Math.abs(rgb1[2] - rgb2[2]);
                    int sum = abs0 + abs1 + abs2;
                    if (sum > 150) {
                        gtThan200++;
                    }
                }
                if (gtThan200 > 20) {
                    return i;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // print bg
        BufferedImage image1 = null;
        BufferedImage image2 = null;
        try {
            image1 = ImageIO.read(new File(imgSrc1));
            image2 = ImageIO.read(new File(imgSrc2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width1 = image1.getWidth();
        int height1 = image1.getHeight();
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int pixel1 = image1.getRGB(j, i);
                int pixel2 = image2.getRGB(j, i);

                int[] rgb1 = new int[3];
                rgb1[0] = (pixel1 & 0xff0000) >> 16;
                rgb1[1] = (pixel1 & 0xff00) >> 8;
                rgb1[2] = (pixel1 & 0xff);

                int[] rgb2 = new int[3];
                rgb2[0] = (pixel2 & 0xff0000) >> 16;
                rgb2[1] = (pixel2 & 0xff00) >> 8;
                rgb2[2] = (pixel2 & 0xff);

                int abs0 = Math.abs(rgb1[0] - rgb2[0]);
                int abs1 = Math.abs(rgb1[1] - rgb2[1]);
                int abs2 = Math.abs(rgb1[2] - rgb2[2]);
                int sum = abs0 + abs1 + abs2;
                System.out.print(String.format("%3d ", sum));
            }
            System.out.println();
        }
        // print ed

        throw new DeltaXResolveFailException();
    }

    public static void main(String[] args) {
        BufferedImage image1 = null;
        BufferedImage image2 = null;
        try {
            image1 = ImageIO.read(new File("C:\\Users\\Administrator\\Downloads\\bg.jpg"));
            image2 = ImageIO.read(new File("C:\\Users\\Administrator\\Downloads\\fullbg.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int width1 = image1.getWidth();
        int height1 = image1.getHeight();
        for (int i = 0; i < height1; i++) {
            for (int j = 0; j < width1; j++) {
                int pixel1 = image1.getRGB(j, i);
                int pixel2 = image2.getRGB(j, i);

                int[] rgb1 = new int[3];
                rgb1[0] = (pixel1 & 0xff0000) >> 16;
                rgb1[1] = (pixel1 & 0xff00) >> 8;
                rgb1[2] = (pixel1 & 0xff);

                int[] rgb2 = new int[3];
                rgb2[0] = (pixel2 & 0xff0000) >> 16;
                rgb2[1] = (pixel2 & 0xff00) >> 8;
                rgb2[2] = (pixel2 & 0xff);

                int abs0 = Math.abs(rgb1[0] - rgb2[0]);
                int abs1 = Math.abs(rgb1[1] - rgb2[1]);
                int abs2 = Math.abs(rgb1[2] - rgb2[2]);
                int sum = abs0 + abs1 + abs2;
                System.out.print(String.format("%3d ", sum));
            }
            System.out.println();
        }
    }

}
