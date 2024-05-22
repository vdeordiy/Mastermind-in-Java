import javax.swing.JPanel;

public class Engine extends JPanel implements Runnable {
    private final int FPS = 30;
    private boolean running = false;
    private Thread mainThread = null;

    @Override
    public void addNotify() {
        super.addNotify();

        if (mainThread == null) {
            running = true;

            mainThread = new Thread(this);
            mainThread.start();
        }
    }

    @Override
    public void run() {
        long startTimer;
        long timeDifference;
        long waitInMilliseconds;

        final long OPTIMAL_TIME = (long) Math.pow(10, 9) / FPS;

        while (running) {
            startTimer = System.nanoTime();

            // This calls paintComponent(Graphics g)
            repaint();

            timeDifference = System.nanoTime() - startTimer;
            waitInMilliseconds = (OPTIMAL_TIME - timeDifference) / (long) Math.pow(10, 6);

            try {
                Thread.sleep(waitInMilliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}