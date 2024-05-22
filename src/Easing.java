public class Easing {
    private Vector2 targetVector2;
    private Vector2 fromVector2, toVector2;

    private double currentStep = 0;
    private double numberOfSteps = 24 * 2;

    public boolean complete;

    public Easing(Vector2 targetVector2, Vector2 toVector2) {
        this.targetVector2 = targetVector2;
        this.fromVector2 = targetVector2.clone();
        this.toVector2 = toVector2;

        this.complete = false;
    }

    private double easeOutCirc(double x) {
        return Math.sqrt(1 - Math.pow(x - 1, 2));
    }

    public void progress() {
        if (currentStep >= numberOfSteps) {
            complete = true;
            return;
        }

        double easing = easeOutCirc(currentStep / numberOfSteps);

        Vector2 difference = toVector2.subtract(fromVector2);

        Vector2 addend = fromVector2.add(difference.multiply(easing));

        targetVector2.setX(addend.getX());
        targetVector2.setY(addend.getY());

        currentStep += 1;
    }

    public Vector2 getToVector2() {
        return toVector2;
    }
}