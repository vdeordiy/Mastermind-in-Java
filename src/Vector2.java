public class Vector2 {
    private int x;
    private int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 clone() {
        return new Vector2(x, y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(getX() + other.getX(), getY() + other.getY());
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(getX() - other.getX(), getY() - other.getY());
    }

    public Vector2 multiply(double scalar) {
        return new Vector2((int) (getX() * scalar), (int) (getY() * scalar));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
