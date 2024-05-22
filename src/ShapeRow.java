import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

public class ShapeRow {
    public ArrayList<Shape> shapeList;
    public Vector2 position;
    private HashMap<Color, Integer> feedback;
    public Easing easing = null;

    public ShapeRow(Vector2 position, ArrayList<Shape> shapeList) {
        this.position = position;
        this.shapeList = shapeList;
        feedback = new HashMap<>();
    }

    public ShapeRow(ArrayList<Shape> shapes) {
        this(new Vector2(0, 0), shapes);
    }

    public ShapeRow(Vector2 position) {
        this(position, new ArrayList<>());
    }

    public ShapeRow() {
        this(new Vector2(0, 0), new ArrayList<>());
    }

    public ShapeRow clone() {
        return new ShapeRow(position.clone(), new ArrayList<Shape>(shapeList));
    }

    public void drawFeedback(Graphics2D g2d) {
        final int margin = 4;

        int totalIndex = 0;

        for (int i = 0; i < feedback.getOrDefault(Constants.correctSelection, 0); i++) {
            Shape shape = shapeList.get(totalIndex);

            g2d.setPaint(Constants.correctSelection);
            g2d.fillRect(shape.position.getX() + position.getX() + margin,
                    shape.position.getY() + position.getY() + margin,
                    shape.getWidth() - margin * 2, shape.getHeight() - margin * 2);

            totalIndex += 1;
        }

        for (int i = 0; i < feedback.getOrDefault(Constants.almostCorrectSelection, 0); i++) {
            Shape shape = shapeList.get(totalIndex);

            g2d.setPaint(Constants.almostCorrectSelection);
            g2d.fillRect(shape.position.getX() + position.getX() + margin,
                    shape.position.getY() + position.getY() + margin,
                    shape.getWidth() - margin * 2, shape.getHeight() - margin * 2);

            totalIndex += 1;
        }
    }

    public void bulkDraw(Gameplay gameplay, Graphics2D g2d) {
        drawFeedback(g2d);

        for (Shape shape : shapeList) {
            shape.draw(gameplay, g2d, position.getX() + shape.position.getX(), position.getY() + shape.position.getY());
        }
    }

    public ArrayList<Color> getColors() {
        ArrayList<Color> colors = new ArrayList<>();

        for (Shape shape : shapeList) {
            colors.add(shape.getColor());
        }

        return colors;
    }

    public HashMap<Color, Integer> getFeedback() {
        return feedback;
    }

    public void setFeedback(HashMap<Color, Integer> feedback) {
        this.feedback = feedback;
    }
}
