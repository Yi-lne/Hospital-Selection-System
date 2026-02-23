package com.chen.HospitalSelection.util;

import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;

/**
 * 滑块验证码生成工具类
 * 生成滑块拼图验证码，用户需要拖动滑块到正确位置
 *
 * @author chen
 * @since 2025-02-16
 */
public class CaptchaUtil {

    private static final int WIDTH = 350;
    private static final int HEIGHT = 200;
    private static final int SLIDER_WIDTH = 60;
    private static final int SLIDER_HEIGHT = 60;
    private static final int TOLERANCE = 5; // 滑块容差范围（像素）

    private static final Random random = new Random();

    /**
     * 验证码信息
     */
    @Data
    public static class CaptchaInfo {
        private String captchaId;
        private String backgroundImage;  // 背景图Base64
        private String sliderImage;      // 滑块图Base64
        private int targetX;             // 目标X坐标
        private int targetY;             // 目标Y坐标
    }

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    public static CaptchaInfo generateCaptcha() {
        CaptchaInfo captchaInfo = new CaptchaInfo();
        captchaInfo.setCaptchaId(generateCaptchaId());

        // 随机生成背景色方案
        Color[] colorScheme = generateColorScheme();

        // 创建背景图
        BufferedImage background = createBackgroundImage(colorScheme);

        // 生成滑块位置
        int sliderX = SLIDER_WIDTH + random.nextInt(WIDTH - SLIDER_WIDTH * 3);
        int sliderY = 50 + random.nextInt(HEIGHT - SLIDER_HEIGHT - 100);

        captchaInfo.setTargetX(sliderX);
        captchaInfo.setTargetY(sliderY);

        // 创建带缺口的背景图
        BufferedImage backgroundWithHole = createBackgroundWithHole(background, sliderX, sliderY, colorScheme);

        // 创建滑块图
        BufferedImage slider = createSliderImage(background, sliderX, sliderY);

        // 转换为Base64
        captchaInfo.setBackgroundImage(imageToBase64(backgroundWithHole));
        captchaInfo.setSliderImage(imageToBase64(slider));

        return captchaInfo;
    }

    /**
     * 验证滑块位置
     *
     * @param targetX 目标X坐标
     * @param userX   用户拖动的X坐标
     * @return 是否验证成功
     */
    public static boolean verifySlider(int targetX, int userX) {
        return Math.abs(targetX - userX) <= TOLERANCE;
    }

    /**
     * 生成验证码ID
     */
    private static String generateCaptchaId() {
        return String.valueOf(System.currentTimeMillis()) + "-" + random.nextInt(10000);
    }

    /**
     * 生成随机颜色方案
     */
    private static Color[] generateColorScheme() {
        // 预定义几种渐变色方案
        Color[][] schemes = {
                // 蓝色系
                {new Color(72, 126, 176), new Color(100, 181, 246)},
                // 绿色系
                {new Color(56, 142, 60), new Color(129, 199, 132)},
                // 紫色系
                {new Color(123, 31, 162), new Color(186, 104, 200)},
                // 橙色系
                {new Color(230, 81, 0), new Color(255, 138, 101)},
                // 青色系
                {new Color(0, 151, 167), new Color(77, 208, 225)},
                // 粉色系
                {new Color(194, 24, 91), new Color(236, 64, 122)},
                // 深蓝系
                {new Color(13, 71, 161), new Color(25, 118, 210)},
                // 棕色系
                {new Color(121, 85, 72), new Color(188, 143, 143)}
        };

        return schemes[random.nextInt(schemes.length)];
    }

    /**
     * 创建背景图
     */
    private static BufferedImage createBackgroundImage(Color[] colorScheme) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建渐变背景
        GradientPaint gradient = new GradientPaint(
                0, 0, colorScheme[0],
                random.nextInt(WIDTH), random.nextInt(HEIGHT), colorScheme[1]
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 添加随机图案
        drawRandomPatterns(g2d, colorScheme);

        g2d.dispose();
        return image;
    }

    /**
     * 创建带缺口的背景图
     */
    private static BufferedImage createBackgroundWithHole(BufferedImage background, int x, int y, Color[] colorScheme) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.drawImage(background, 0, 0, null);

        // 绘制缺口形状
        drawPuzzleShape(g2d, x, y, true, colorScheme);

        g2d.dispose();
        return image;
    }

    /**
     * 创建滑块图
     */
    private static BufferedImage createSliderImage(BufferedImage background, int x, int y) {
        BufferedImage image = new BufferedImage(SLIDER_WIDTH, SLIDER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 从背景图中截取滑块图案
        BufferedImage cropped = background.getSubimage(x, y, SLIDER_WIDTH, SLIDER_HEIGHT);

        // 绘制拼图形状（带凸起）
        Shape puzzleShape = createPuzzleShape(0, 0);
        g2d.setClip(puzzleShape);
        g2d.drawImage(cropped, 0, 0, null);

        // 绘制边框和阴影
        g2d.setClip(null);
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(puzzleShape);

        // 添加阴影效果
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(1));
        Shape shadowShape = createPuzzleShape(2, 2);
        g2d.draw(shadowShape);

        g2d.dispose();
        return image;
    }

    /**
     * 绘制拼图形状
     */
    private static void drawPuzzleShape(Graphics2D g2d, int x, int y, boolean isHole, Color[] colorScheme) {
        Shape shape = createPuzzleShape(x, y);

        if (isHole) {
            // 缺口：绘制半透明深色
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fill(shape);
        }

        // 绘制边框
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(shape);
    }

    /**
     * 创建拼图形状路径
     */
    private static Shape createPuzzleShape(int x, int y) {
        int w = SLIDER_WIDTH;
        int h = SLIDER_HEIGHT;

        // 创建拼图形状路径
        java.awt.geom.Path2D path = new java.awt.geom.Path2D.Double();

        // 顶部：平边
        path.moveTo(x, y);
        path.lineTo(x + w * 0.4, y);

        // 右侧凸起1
        int bumpSize = h * 3 / 10;
        path.lineTo(x + w * 0.4, y + h * 0.35 - bumpSize);
        path.quadTo(x + w * 0.4, y + h * 0.35, x + w * 0.5, y + h * 0.35);
        path.quadTo(x + w * 0.6, y + h * 0.35, x + w * 0.6, y + h * 0.35 + bumpSize);

        path.lineTo(x + w * 0.6, y + h * 0.65 - bumpSize);
        path.quadTo(x + w * 0.6, y + h * 0.65, x + w * 0.5, y + h * 0.65);
        path.quadTo(x + w * 0.4, y + h * 0.65, x + w * 0.4, y + h * 0.65 + bumpSize);

        // 右侧：平边
        path.lineTo(x + w, y + h * 0.4);
        path.lineTo(x + w, y + h * 0.6);

        // 底部：平边
        path.lineTo(x + w * 0.6, y + h);
        path.lineTo(x, y + h);

        // 左侧：带凹槽
        path.lineTo(x, y + h * 0.6);
        path.quadTo(x + w * 0.1, y + h * 0.5, x, y + h * 0.4);

        path.closePath();

        return path;
    }

    /**
     * 绘制随机图案
     */
    private static void drawRandomPatterns(Graphics2D g2d, Color[] colorScheme) {
        // 随机绘制圆圈数量和大小
        int circleCount = 5 + random.nextInt(10);
        for (int i = 0; i < circleCount; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int radius = 3 + random.nextInt(25);
            float alpha = 0.05f + random.nextFloat() * 0.2f;

            // 随机使用白色或主题色
            if (random.nextBoolean()) {
                g2d.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
            } else {
                Color baseColor = colorScheme[random.nextInt(colorScheme.length)];
                g2d.setColor(new Color(
                        baseColor.getRed(),
                        baseColor.getGreen(),
                        baseColor.getBlue(),
                        (int) (alpha * 255)
                ));
            }
            g2d.fillOval(x, y, radius, radius);
        }

        // 随机绘制线条数量
        int lineCount = 3 + random.nextInt(8);
        for (int i = 0; i < lineCount; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            float alpha = 0.05f + random.nextFloat() * 0.15f;

            g2d.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
            g2d.setStroke(new BasicStroke(1 + random.nextInt(2)));
            g2d.drawLine(x1, y1, x2, y2);
        }

        // 添加随机文字或数字（可选）
        if (random.nextBoolean()) {
            int textCount = 1 + random.nextInt(3);
            for (int i = 0; i < textCount; i++) {
                int x = 20 + random.nextInt(WIDTH - 40);
                int y = 20 + random.nextInt(HEIGHT - 40);
                float alpha = 0.03f + random.nextFloat() * 0.08f;

                g2d.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
                g2d.setFont(new Font("Arial", Font.BOLD, 20 + random.nextInt(40)));
                String text = String.valueOf(random.nextInt(10));
                g2d.drawString(text, x, y);
            }
        }
    }

    /**
     * 将BufferedImage转换为Base64字符串
     */
    private static String imageToBase64(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("图片转换Base64失败", e);
        }
    }
}
