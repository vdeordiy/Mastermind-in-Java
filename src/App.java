import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;

public class App extends JFrame {
    private final int WIDTH = 400;
    private int HEIGHT;

    public App() {
        setTitle("Mastermind");

        // Golden ratio for visually-pleasant window proportions
        HEIGHT = (int) (WIDTH * ((1.0 + Math.sqrt(5.0)) / 2.0));
        setSize(new Dimension(WIDTH, HEIGHT));

        setResizable(false);

        // Place window on the center
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load
        Gameplay gameplay = new Gameplay(this);
        addKeyListener(gameplay);
        setContentPane(gameplay);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Better than Candy Crush
                App app = new App();
                app.setVisible(true);
            }
        });
    }
}
