import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class MultiLaneCarGame extends JPanel {

    private int carX = 200; // Starting position of the car
    private final int carY = 400; // Vertical position of the car
    private final int carWidth = 60;
    private final int carHeight = 100;

    private boolean gameOver = false;
    private int score = 0;
    private int speed = 1; // Game speed

    private final ArrayList<Obstacle> obstacles = new ArrayList<>();
    private final Random random = new Random();
    private final int[] laneX = {100, 200, 300, 400}; // Lane positions
    private int currentLane = 1; // Start in the second lane
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;

    public MultiLaneCarGame() {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Multi-Lane Digital Car Game");
        MultiLaneCarGame gamePanel = new MultiLaneCarGame();

        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(gamePanel);
        frame.setVisible(true);

        gamePanel.runGame();
    }

    private void runGame() {
        Timer gameTimer = new Timer(30, e -> {
            if (!gameOver) {
                generateObstacles();
                checkCollisions();
                repaint();
            }
        });
        gameTimer.start();

        Timer speedIncreaseTimer = new Timer(500, e -> {
            if (speed < 10) {
                speed++;
            }
        });
        speedIncreaseTimer.start();

        addKeyListener();
    }

    private void addKeyListener() {
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    if (!isMovingLeft && currentLane > 0) {
                        isMovingLeft = true;
                        currentLane--;
                        carX = laneX[currentLane];
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    if (!isMovingRight && currentLane < laneX.length - 1) {
                        isMovingRight = true;
                        currentLane++;
                        carX = laneX[currentLane];
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    isMovingLeft = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    isMovingRight = false;
                }
            }
        });
    }

    private void generateObstacles() {
        if (random.nextInt(100) < 2) {
            int laneIndex = random.nextInt(4);
            int width = random.nextInt(30) + 30;
            int height = random.nextInt(30) + 30;
            obstacles.add(new Obstacle(laneX[laneIndex], 0, width, height));
        }

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obs = obstacles.get(i);
            obs.y += speed;
            if (obs.y > getHeight()) {
                obstacles.remove(i);
                i--;
                score++;
            }
        }
    }

    private void checkCollisions() {
        for (Obstacle obs : obstacles) {
            if (obs.getBounds().intersects(carX, carY, carWidth, carHeight)) {
                gameOver = true;
                Timer crashTimer = new Timer(100, e -> repaint());
                crashTimer.start();
                return;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        for (int x : laneX) {
            g.fillRect(x + 5, 0, 10, getHeight());
        }

        drawCar(g, carX, carY);

        for (Obstacle obs : obstacles) {
            g.setColor(Color.RED);
            g.fillRect(obs.x, obs.y, obs.width, obs.height);
        }

        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    private void drawCar(Graphics g, int x, int y) {
        // Car body (rectangle)
        g.setColor(Color.BLUE);
        g.fillRect(x, y, carWidth, carHeight);

        // Roof (semi-circle)
        g.setColor(Color.BLUE);
        g.fillArc(x, y - 20, carWidth, 40, 0, 180);

        // Wheels
        g.setColor(Color.BLACK);
        g.fillOval(x + 10, y + carHeight - 10, 20, 20); // Front wheel
        g.fillOval(x + carWidth - 30, y + carHeight - 10, 20, 20); // Rear wheel
    }

    class Obstacle {
        int x, y, width, height;

        Obstacle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }
}
