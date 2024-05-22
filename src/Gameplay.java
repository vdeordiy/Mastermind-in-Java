import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class Gameplay extends Engine implements KeyListener {
    private Logic logic = Logic.getInstance();
    private SoundEffect soundEffect = SoundEffect.getInstance();
    private int maximumShapeWidth;

    private ArrayList<ShapeRow> shapeRowList = new ArrayList<>();
    private ShapeRow inputShapeRow;
    private Image defaultImage;

    private boolean inputDebounce;

    private String topMessage;
    private String centralMessage;

    private boolean showSecretColorSequence = false;
    private ShapeRow secretShapeRow;

    public Gameplay(App parent) {
        setSize(parent.getSize());
        setBackground(Constants.backgroundColor);

        maximumShapeWidth = getWidth() / logic.sequenceLength;
        // Make images smaller
        maximumShapeWidth -= 20;

        defaultImage = new Image(maximumShapeWidth, maximumShapeWidth);
        defaultImage.setImage(Constants.imagePath);

        newGame();
    }

    public void newGame() {
        shapeRowList.clear();

        // Add spacing for top message and center the row
        inputShapeRow = new ShapeRow(new Vector2((getWidth() - maximumShapeWidth * logic.sequenceLength) / 2 - 7, 30));
        shapeRowList.add(inputShapeRow);

        logic.generateSecretColorSequence();

        // Messages
        topMessage = "(R)ed, (G)reen, (B)lue, (M)agenta, (O)range, (D)ark";
        centralMessage = "";

        // Guesses
        logic.guessesLeft = logic.maximumGuessesAllowed;

        inputDebounce = false;

        // Secret
        Color[] secretColorSequence = logic.getSecretColorSequence();

        secretShapeRow = new ShapeRow(new Vector2((getWidth() - maximumShapeWidth * logic.sequenceLength) / 2 - 7, 30));

        for (Color color : secretColorSequence) {
            Image secretImage = defaultImage.getBlendedImage(color);

            // Determine position
            secretImage.position.setX(secretShapeRow.shapeList.size() * maximumShapeWidth);

            secretShapeRow.shapeList.add(secretImage);
        }
    }

    public void drawTextWithBackground(Graphics2D g2d, FontMetrics metrics, String text, int x, int y) {
        if (text.length() == 0) {
            return;
        }

        final int padding = 5;

        g2d.setPaint(Constants.textBackgroundColor);
        Rectangle2D rect = metrics.getStringBounds(text, g2d);

        // metrics.getAscent() probably means height of the text
        g2d.fillRect(x - padding, y - metrics.getAscent() - padding, (int) rect.getWidth() + padding * 2,
                (int) rect.getHeight() + padding * 2);

        g2d.setPaint(Constants.textColor);
        g2d.drawString(text, x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        // Alternative: paint(Graphics g)
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        for (ShapeRow shapeRow : shapeRowList) {
            // Draw
            shapeRow.bulkDraw(this, g2d);

            if (shapeRow.easing != null) {
                shapeRow.easing.progress();

                if (shapeRow.easing.complete) {
                    shapeRow.easing = null;
                }
            }
        }

        // Makes things less pixelated
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Display top message
        g2d.setFont(new Font("Courier New", Font.BOLD, 10));
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(topMessage)) / 2;
        int y = metrics.getAscent() + 8;
        drawTextWithBackground(g2d, metrics, topMessage, x, y);

        // Display central message
        g2d.setFont(new Font("Courier New", Font.BOLD, 27));
        metrics = g2d.getFontMetrics();
        x = (getWidth() - metrics.stringWidth(centralMessage)) / 2;
        y = getHeight() / 2;
        drawTextWithBackground(g2d, metrics, centralMessage, x, y);

        // Secret
        if (showSecretColorSequence) {
            secretShapeRow.bulkDraw(this, g2d);
        }
    }

    public void restart() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = Constants.intermissionWait; i >= 0; i--) {
                        topMessage = String.format("Intermission: %d", i);
                        Thread.sleep(1000);
                    }

                    newGame();
                } catch (InterruptedException e) {
                    newGame();
                }
            }
        }).start();
    }

    public void handleWinningAndLosingConditions() {
        if (logic.isWinner(inputShapeRow.getColors())) {
            centralMessage = Constants.winningMessage;

            inputDebounce = true;

            soundEffect.playSound(Constants.winSoundPath);

            restart();
        } else if (logic.guessesLeft == 0) {
            centralMessage = Constants.losingMessage;

            inputDebounce = true;

            soundEffect.playSound(Constants.loseSoundPath);

            restart();
        } else {
            ShapeRow clonedShapeRow = inputShapeRow.clone();
            shapeRowList.add(clonedShapeRow);

            HashMap<Color, Integer> feedback = logic.getFeedback(clonedShapeRow.getColors());
            clonedShapeRow.setFeedback(feedback);

            inputShapeRow.shapeList.clear();

            soundEffect.playSound(Constants.slideDownSoundPath);

            slideDownRows();
        }
    }

    public void slideDownRows() {
        for (ShapeRow shapeRow : shapeRowList) {
            if (shapeRow == inputShapeRow) {
                continue;
            }

            // Patch bug where animation is left unfinished when spamming answers
            if (shapeRow.easing != null) {
                shapeRow.position = shapeRow.easing.getToVector2();
            }

            shapeRow.easing = new Easing(shapeRow.position, shapeRow.position.add(new Vector2(0, maximumShapeWidth)));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (inputDebounce) {
            return;
        }

        if (logic.keyAndColorPairs.containsKey(keyCode) && inputShapeRow.shapeList.size() < logic.sequenceLength) {
            Color selectedColor = logic.keyAndColorPairs.get(keyCode);
            Image selectedImage = defaultImage.getBlendedImage(selectedColor);
            selectedImage.setColor(selectedColor);

            // Determine position
            selectedImage.position.setX(inputShapeRow.shapeList.size() * maximumShapeWidth);

            inputShapeRow.shapeList.add(selectedImage);
        } else if (keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE) {
            if (inputShapeRow.shapeList.size() > 0) {
                inputShapeRow.shapeList.removeLast();
            }
        } else if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_SPACE) {
            if (inputShapeRow.shapeList.size() == logic.sequenceLength) {
                logic.guessesLeft -= 1;
                topMessage = String.format("Guesses Left: %d", logic.guessesLeft);

                handleWinningAndLosingConditions();
            }
        } else if (keyCode == KeyEvent.VK_0) {
            showSecretColorSequence = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_0) {
            showSecretColorSequence = false;
        }
    }
}