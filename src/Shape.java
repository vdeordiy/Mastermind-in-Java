import java.awt.Color;
import java.awt.Graphics2D;

public class Shape {
    public Vector2 position;
    private int width;
    private int height;
    private Color color;

    public Shape(Vector2 position, int width, int height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    public Shape(int width, int height) {
        this(new Vector2(0, 0), width, height);
    }

    public void draw(Gameplay gameplay, Graphics2D g2d, int x, int y) {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}