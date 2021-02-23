
package application;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements EventHandler<KeyEvent> {
    final private static double FRAMES_PER_SECOND = 5.0;

    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label gameOverLabel;
    @FXML private PacManView pacManView;
    private PacManModel pacManModel;
    private static final String[] levelFiles = {"src/levels/level1.txt", "src/levels/level2.txt", "src/levels/level3.txt"};

    private Timer timer;
    private static int ghostEatingModeCounter;
    private boolean paused;

    public Controller() {
        this.paused = false;
    }

    public void initialize() {
        String file = this.getLevelFile(0);
        this.pacManModel = new PacManModel();
        this.update(PacManModel.Direction.NONE);
        ghostEatingModeCounter = 25;
        this.startTimer();
    }

    private void startTimer() {
        this.timer = new java.util.Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        update(pacManModel.getCurrentDirection());
                    }
                });
            }
        };

        long frameTimeInMilliseconds = (long)(1000.0 / FRAMES_PER_SECOND);
        this.timer.schedule(timerTask, 0, frameTimeInMilliseconds);
    }
    private void update(PacManModel.Direction direction) {
        this.pacManModel.step(direction);
        this.pacManView.update(pacManModel);
        this.scoreLabel.setText(String.format("Score: %d", this.pacManModel.getScore()));
        this.levelLabel.setText(String.format("Level: %d", this.pacManModel.getLevel()));
        if (pacManModel.isGameOver()) {
            this.gameOverLabel.setText(String.format("GAME OVER"));
            pause();
        }
        if (pacManModel.isYouWon()) {
            this.gameOverLabel.setText(String.format("YOU WON!"));
        }
        if (pacManModel.isGhostEatingMode()) {
            ghostEatingModeCounter--;
        }
        if (ghostEatingModeCounter == 0 && pacManModel.isGhostEatingMode()) {
            pacManModel.setGhostEatingMode(false);
        }
    }
    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        KeyCode code = keyEvent.getCode();
        PacManModel.Direction direction = PacManModel.Direction.NONE;
        if (code == KeyCode.LEFT) {
            direction = PacManModel.Direction.LEFT;
        } else if (code == KeyCode.RIGHT) {
            direction = PacManModel.Direction.RIGHT;
        } else if (code == KeyCode.UP) {
            direction = PacManModel.Direction.UP;
        } else if (code == KeyCode.DOWN) {
            direction = PacManModel.Direction.DOWN;
        } else if (code == KeyCode.G) {
            pause();
            this.pacManModel.startNewGame();
            this.gameOverLabel.setText(String.format(""));
            paused = false;
            this.startTimer();
        } else {
            keyRecognized = false;
        }
        if (keyRecognized) {
            keyEvent.consume();
            pacManModel.setCurrentDirection(direction);
        }
    }
    public void pause() {
            this.timer.cancel();
            this.paused = true;
    }

    public double getBoardWidth() {
        return PacManView.CELL_WIDTH * this.pacManView.getColumnCount();
    }

    public double getBoardHeight() {
        return PacManView.CELL_WIDTH * this.pacManView.getRowCount();
    }

    public static void setGhostEatingModeCounter() {
        ghostEatingModeCounter = 25;
    }

    public static int getGhostEatingModeCounter() {
        return ghostEatingModeCounter;
    }

    public static String getLevelFile(int x)
    {
        return levelFiles[x];
    }

    public boolean getPaused() {
        return paused;
    }
}
