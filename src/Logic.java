import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Logic {
    private static Logic instance = null;

    public final Color[] availableColors = { Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA, Color.YELLOW,
            new Color(9, 46, 45) };

    public final int sequenceLength = 4;

    public final int maximumGuessesAllowed = 10;
    public int guessesLeft;
    private Color[] secretColorSequence;

    public HashMap<Integer, Color> keyAndColorPairs = new HashMap<>();

    public Logic() {
        keyAndColorPairs.put(KeyEvent.VK_R, Color.RED);
        keyAndColorPairs.put(KeyEvent.VK_G, Color.GREEN);
        keyAndColorPairs.put(KeyEvent.VK_B, Color.BLUE);
        keyAndColorPairs.put(KeyEvent.VK_M, Color.MAGENTA);
        keyAndColorPairs.put(KeyEvent.VK_O, Color.YELLOW);
        keyAndColorPairs.put(KeyEvent.VK_D, new Color(9, 46, 45));

        // Alternative
        keyAndColorPairs.put(KeyEvent.VK_1, Color.RED);
        keyAndColorPairs.put(KeyEvent.VK_2, Color.GREEN);
        keyAndColorPairs.put(KeyEvent.VK_3, Color.BLUE);
        keyAndColorPairs.put(KeyEvent.VK_4, Color.MAGENTA);
        keyAndColorPairs.put(KeyEvent.VK_5, Color.YELLOW);
        keyAndColorPairs.put(KeyEvent.VK_6, new Color(9, 46, 45));
    }

    public static synchronized Logic getInstance() {
        // Singleton pattern
        if (instance == null) {
            instance = new Logic();
        }

        return instance;
    }

    public void generateSecretColorSequence() {
        secretColorSequence = new Color[sequenceLength];

        for (int i = 0; i < sequenceLength; i++) {
            // Avoids shared seed between Threads as opposed to Random, so safer
            int randomIndex = ThreadLocalRandom.current().nextInt(availableColors.length);
            secretColorSequence[i] = availableColors[randomIndex];
        }
    };

    public boolean isWinner(ArrayList<Color> colorSequence) {
        for (int i = 0; i < sequenceLength; i++) {
            if (colorSequence.get(i).getRGB() != secretColorSequence[i].getRGB()) {
                return false;
            }
        }

        return true;
    }

    public HashMap<Color, Integer> getFeedback(ArrayList<Color> colorSequence) {
        HashMap<Color, Integer> feedback = new HashMap<>();
        feedback.put(Constants.correctSelection, 0);
        feedback.put(Constants.almostCorrectSelection, 0);

        ArrayList<Integer> unmatchedColorSequence = new ArrayList<>();
        ArrayList<Integer> unmatchedSecretColorSequence = new ArrayList<>();

        // First check matched
        for (int i = 0; i < sequenceLength; i++) {
            if (colorSequence.get(i).getRGB() == secretColorSequence[i].getRGB()) {
                feedback.put(Constants.correctSelection, feedback.get(Constants.correctSelection) + 1);
            } else {
                unmatchedColorSequence.add(colorSequence.get(i).getRGB());
                unmatchedSecretColorSequence.add(secretColorSequence[i].getRGB());
            }
        }

        // Look for close ones
        for (int rgb : unmatchedColorSequence) {
            if (unmatchedSecretColorSequence.contains(rgb)) {
                feedback.put(Constants.almostCorrectSelection, feedback.get(Constants.almostCorrectSelection) + 1);
                unmatchedSecretColorSequence.remove(unmatchedSecretColorSequence.indexOf(rgb));
            }
        }

        return feedback;
    }

    public Color[] getSecretColorSequence() {
        return secretColorSequence;
    }
}