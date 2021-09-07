package base.service.frameworks.utils;

import base.service.frameworks.processor.BaseTaskPool;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by Administrator on 2017/6/8.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ImageUtil {
    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(ImageUtil.class);
    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 二进制转成图片
     */
    public static boolean byte_to_image(byte[] data, String path) {
        if (data == null || data.length < 3 || StringUtil.isEmpty(path)) return false;
        try {
            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            LOG.info("Make Picture success,Please find image in " + path);
            return true;
        } catch (Exception ex) {
            LOG.error("UserProcessor byte2image", ex);
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean base64_to_image(String pImageString, String pImagePath) {
        try {
            byte[] decodedBytes = EncryptUtil.base64_decode(pImageString);
            Files.write(Paths.get(pImagePath), decodedBytes);
            return true;
        } catch (IOException e) {
            LOG.error("base64 to image failed.", e);
        }
        return false;
    }

    public static String image_to_base64(String pImagePath) {
        String result = "";

        try {
            byte[] content = Files.readAllBytes(Paths.get(pImagePath));
            result = EncryptUtil.base64_encode(content);
        } catch (IOException e) {
            LOG.error("image to base64 failed.", e);
        }

        return result;
    }

    /**
     * 产生缩略图
     * @param pImage 图片二进制内容
     * @param pWidth 缩略图宽度，高度会等比计算
     * @param pEnableAlpha 是否支持alpha，支持时输出格式为png，不支持输出格式为jpg
     * @return 缩略图二进制内容
     */
    public static byte[] thumbnail(byte[] pImage, int pWidth, boolean pEnableAlpha) {
        if (pWidth > 0) {
            try {
                InputStream   input  = new ByteArrayInputStream(pImage);
                BufferedImage origin = ImageIO.read(input);

                int originWidth  = origin.getWidth();
                int originHeight = origin.getHeight();
                int height       = MathUtils.divide(originHeight * pWidth, originWidth, 6).intValue();
                //LOG.dd("origin image %d x %d, resize to %d x %d", originWidth, originHeight, pWidth, height);

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Thumbnails.of(origin)
                        .size(pWidth, height)
                        .imageType(pEnableAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB)
                        .outputFormat(pEnableAlpha ? "png" : "jpg")
                        .toOutputStream(output);
                return output.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                LOG.error("generate image thumbnail failed.", e);
            }
        }
        return new byte[0];
    }

    public static void main(String[] args) {
        try {
//            byte[] content   = Files.readAllBytes(Paths.get("D:\\Images\\steven-stahlberg-dejahthorisdetail.jpg"));
//            byte[] content   = Files.readAllBytes(Paths.get("D:\\Images\\avatar512x521.png"));

            BaseTaskPool mTasks = new BaseTaskPool("Image-Tasks", 6, 12, 2000);
            mTasks.init();

            long start = System.nanoTime();
            for(int i=0; i<1; i++) {
                mTasks.queue(() -> {
                    try {
                        byte[] content   = Files.readAllBytes(Paths.get("D:\\Images\\微信图片_20210525115307.jpg"));
                        byte[] thumbnail = thumbnail(content, 200, false);
                        if (thumbnail.length > 0) {
                            FileUtil.write(Paths.get("D:\\Images\\test\\", StringUtil.generateUUID() + ".jpg"),
                                    thumbnail, StandardOpenOption.CREATE_NEW);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            mTasks.release(false);
            long end = System.nanoTime();
            LOG.debug("cost " + ((end - start) / 1000000D) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
