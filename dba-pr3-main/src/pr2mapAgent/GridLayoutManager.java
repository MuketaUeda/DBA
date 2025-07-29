package pr2mapAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.LineBorder;
import java.util.Arrays;

public class GridLayoutManager extends JFrame implements PositionListener {

    private JPanel gridPanel;
    private CellPanel[][] squares;
    private int height;
    private int width;
    private Environment env;
    private Map map;

    private BufferedImage grassImage;
    private BufferedImage raccoonImage;
    private BufferedImage obstacleImage; // Image of obstacles (walls)
    private BufferedImage endImage; // Image of the target

    private boolean endSet = false;
    private boolean startSet = false;
    private boolean positionsSet = false;
    private int[] startPos = new int[2];
    private int[] endPos = new int[2];  // Target position

    private JTextArea chatArea; // Chat area for messages

    public GridLayoutManager(Map map) {
        super("GUI GridLayout Manager");

        this.env = env;
        this.map = map;

        // Load images for cells
        try {
            grassImage = ImageIO.read(new File("snow.png"));
            raccoonImage = ImageIO.read(new File("agent.png"));
            obstacleImage = ImageIO.read(new File("grass.png"));
            endImage = ImageIO.read(new File("reindeer.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        width = map.getWidth();
        height = map.getHeight();

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(height, width));
        squares = new CellPanel[height][width];
        createCells();

        // Chat configs
        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(new JLabel("Messages:"), BorderLayout.NORTH);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        // Buttons configs
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            resetPositions();
            addChatMessage("Positions have been reset.");
        });
        buttonPanel.add(resetButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            startRunning();
            addChatMessage("Search Started.");
        });
        buttonPanel.add(startButton);

        chatPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Dinamic chat/map configs
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gridPanel, chatPanel);
        splitPane.setDividerLocation(900); //
        splitPane.setResizeWeight(0.80); // Proportion between map and chat
        splitPane.setOneTouchExpandable(true); // button to resize

        add(splitPane, BorderLayout.CENTER);

        // window configs
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setVisible(true);
    }

    private void startRunning() {
        positionsSet = true;
    }

    private void addChatMessage(String message) {
        chatArea.append(message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll to bottom
    }

    public boolean positionsSet() {
        return positionsSet;
    }

    public int[] getStartPos() {
        return startPos;
    }

    public int[] getEndPos() {
        return endPos;
    }

    private void createCells() {
        int cellSize = Math.max(600 / Math.max(height, width), 30); // Dinamic cells

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squares[i][j] = new CellPanel(i, j);
                squares[i][j].setPreferredSize(new Dimension(cellSize, cellSize)); // cells size
                squares[i][j].setBorder(new LineBorder(Color.WHITE, 0));
                setCellStyle(i, j); // initial style
                gridPanel.add(squares[i][j]);
            }
        }
    }



    private void setCellStyle(int i, int j) {
        if (map.getMatrix()[i][j] == -1) {
            squares[i][j].setBackground(Color.RED); // Obstacle
        } else {
            squares[i][j].setBackground(Color.WHITE); // Free cell
        }
    }

    public void onPositionUpdated(int[] oldPos, int[] currentPos, boolean targetReached, int energy) {
        if (!startSet || !endSet) {
            return;
        }

        if (targetReached) {
            squares[currentPos[1]][currentPos[0]].setTarget(false);
            squares[oldPos[1]][oldPos[0]].setRaccoon(false);
            squares[currentPos[1]][currentPos[0]].setRaccoon(true);
            gridPanel.repaint();
            addChatMessage("Agent Saved All Reindeers");
        } else if (!Arrays.equals(oldPos, currentPos)) {
            addChatMessage("Agent moved from [" + oldPos[0] + ", " + oldPos[1] + "] to [" + currentPos[0] + ", " + currentPos[1] + "].");
            squares[oldPos[1]][oldPos[0]].setRaccoon(false);
            squares[currentPos[1]][currentPos[0]].setRaccoon(true);
            gridPanel.repaint();
        }
    }

    private void resetPositions() {
        startSet = false;
        endSet = false;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                squares[i][j].setRaccoon(false);
                squares[i][j].setTarget(false);
            }
        }
        gridPanel.repaint();
    }

    private class CellPanel extends JPanel {
        private int row, col;
        private boolean hasRaccoon = false;
        private boolean hasTarget = false;

        public CellPanel(int row, int col) {
            this.row = row;
            this.col = col;

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (map.getMatrix()[row][col] == -1) {
                        JOptionPane.showMessageDialog(null, "Obstacle cell. Cannot place the Agent or target here.");
                        return;
                    }
                    if (!startSet) {
                        startPos[0] = col;
                        startPos[1] = row;
                        hasRaccoon = true;
                        startSet = true;
                        addChatMessage("Start position set at [" + row + ", " + col + "].");
                    } else if (!endSet) {
                        if (row == startPos[1] && col == startPos[0]) {
                            JOptionPane.showMessageDialog(null, "The target position cannot be the same as the start position.");
                        } else {
                            endPos[0] = col;
                            endPos[1] = row;
                            hasTarget = true;
                            endSet = true;
                            addChatMessage("Target position set at [" + row + ", " + col + "].");
                        }
                    }
                    gridPanel.repaint();
                }
            });
        }

        public void setRaccoon(boolean hasRaccoon) {
            this.hasRaccoon = hasRaccoon;
        }

        public void setTarget(boolean hasTarget) {
            this.hasTarget = hasTarget;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (map.getMatrix()[row][col] == -1) {
                g.drawImage(obstacleImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.drawImage(grassImage, 0, 0, getWidth(), getHeight(), null);
            }
            if (hasRaccoon) {
                g.drawImage(raccoonImage, 0, 0, getWidth(), getHeight(), null);
            }
            if (hasTarget) {
                g.drawImage(endImage, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }
}
