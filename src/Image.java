import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image extends Shape {
    private BufferedImage image;

    public Image(Vector2 position, int width, int height) {
        super(position, width, height);
    }

    public Image(int width, int height) {
        super(width, height);
    }

    public void setImage(String filename) {
        try {
            image = ImageIO.read(getClass().getResource(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Image getBlendedImage(Color blendColor) {
        BufferedImage copyImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);

                // Extract colors
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                // Blend colors
                red = (red * blendColor.getRed()) / 255;
                green = (green * blendColor.getGreen()) / 255;
                blue = (blue * blendColor.getBlue()) / 255;

                int blendedRGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                copyImage.setRGB(x, y, blendedRGB);
            }
        }

        Image blendedImage = new Image(position.clone(), getWidth(), getHeight());
        blendedImage.setImage(copyImage);

        return blendedImage;
    }

    @Override
    public void draw(Gameplay gameplay, Graphics2D g2, int x, int y) {
        g2.drawImage(image, x, y, getWidth(), getHeight(), gameplay);
    }
}