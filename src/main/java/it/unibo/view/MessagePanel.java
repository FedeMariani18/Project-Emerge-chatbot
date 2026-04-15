package it.unibo.view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static it.unibo.common.Constants.*;

public class MessagePanel extends JPanel {
    private static final int MESSAGE_PADDING = 5;
    private static final int MESSAGE_HORIZONTAL_PADDING = 10;
    private static final int SYSTEM_MESSAGE_PADDING = 10;
    private static final int VERTICAL_STRUT_SIZE = 5;
    
    private Box messageBox;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public MessagePanel() {
        setBackground(BACKGROUND_COLOR);
        messageBox = Box.createVerticalBox();
        messageBox.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(messageBox);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(String text, boolean isUser) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, MESSAGE_WRAPPER_MAX_HEIGHT));

        BalloonMessage balloon = new BalloonMessage(text, isUser);
        
        if (isUser) {
            // User message on the right
            messageWrapper.add(Box.createHorizontalGlue(), BorderLayout.WEST);
            messageWrapper.add(balloon, BorderLayout.EAST);
        } else {
            // Agent message on the left
            messageWrapper.add(balloon, BorderLayout.WEST);
            messageWrapper.add(Box.createHorizontalGlue(), BorderLayout.EAST);
        }

        messageWrapper.setBorder(BorderFactory.createEmptyBorder(MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING, MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING));
        
        messageBox.remove(messageBox.getComponentCount() - 1); // Remove the glue
        messageBox.add(messageWrapper);
        messageBox.add(Box.createVerticalStrut(VERTICAL_STRUT_SIZE));
        messageBox.add(Box.createVerticalGlue()); // Add glue again
        messageBox.revalidate();
        messageBox.repaint();

        // Auto-scroll to the last message
        SwingUtilities.invokeLater(() -> {
            messageBox.scrollRectToVisible(messageBox.getBounds());
        });
    }

    public void addSystemMessage(String text) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, SYSTEM_MESSAGE_MAX_HEIGHT));

        JLabel systemLabel = new JLabel(text);
        systemLabel.setFont(SYSTEM_MESSAGE_FONT); 
        systemLabel.setForeground(SYSTEM_MESSAGE_TEXT_COLOR);
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);

        messageWrapper.add(systemLabel, BorderLayout.CENTER);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(SYSTEM_MESSAGE_PADDING, SYSTEM_MESSAGE_PADDING, SYSTEM_MESSAGE_PADDING, SYSTEM_MESSAGE_PADDING));

        messageBox.remove(messageBox.getComponentCount() - 1); // Remove the glue
        messageBox.add(messageWrapper);
        messageBox.add(Box.createVerticalStrut(VERTICAL_STRUT_SIZE));
        messageBox.add(Box.createVerticalGlue()); // Add glue again
        messageBox.revalidate();
        messageBox.repaint();
    }

    /**
     * Component that draws the balloon (rounded rectangle with message)
     */
    private static class BalloonMessage extends JPanel {
        private static final Color USER_COLOR = new Color(0, 150, 136);
        private static final Color AGENT_COLOR = new Color(230, 230, 230);

        private String message;
        private boolean isUser;
        private String timestamp;

        public BalloonMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = new SimpleDateFormat("HH:mm").format(new Date());
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Colors
            Color balloonColor = isUser ? USER_COLOR : AGENT_COLOR;
            Color textColor = isUser ? Color.WHITE : Color.BLACK;

            // Padding and dimensions
            int padding = 12;
            int arcSize = 15;
            int width = getWidth();
            int height = getHeight();

            // Draw the balloon (rounded rectangle)
            g2d.setColor(balloonColor);
            g2d.fillRoundRect(0, 0, width - 1, height - 15, arcSize, arcSize);

            // Draw the text
            g2d.setColor(textColor);
            g2d.setFont(DEFAULT_FONT);
            FontMetrics fm = g2d.getFontMetrics();

            String[] lines = wrapText(message, fm, 350 - 24); // 350 è maxWidth, 24 è padding*2
            int y = padding + fm.getAscent();

            for (String line : lines) {
                g2d.drawString(line, padding, y);
                y += fm.getHeight();
            }

            // Draw the timestamp
            g2d.setFont(TIMESTAMP_FONT);
            g2d.setColor(isUser ? USER_COLOR.darker() : AGENT_COLOR.darker());
            g2d.drawString(timestamp, 5, height - 3);
        }

        @Override
        public Dimension getPreferredSize() {
            Graphics2D g2d = (Graphics2D) getGraphics();
            if (g2d == null) return new Dimension(200, 50);

            FontMetrics fm = g2d.getFontMetrics(DEFAULT_FONT);
            int padding = 12;
            int maxWidth = 350;

            String[] lines = wrapText(message, fm, 350 - 24);
            int width = 0;
            for (String line : lines) {
                width = Math.max(width, fm.stringWidth(line));
            }

            width = Math.min(width + 2 * padding, maxWidth);
            int height = fm.getHeight() * lines.length + 2 * padding + 15;

            return new Dimension(width, height);
        }

        private String[] wrapText(String text, FontMetrics fm, int maxWidth) {
            java.util.List<String> lines = new java.util.ArrayList<>();
            String[] originalLines = text.split("\n");
            
            for (String line : originalLines) {
                if (fm.stringWidth(line) <= maxWidth) {
                    lines.add(line);
                } else {
                    String current = "";
                    for (String word : line.split(" ")) {
                        if (fm.stringWidth(current + word) <= maxWidth) {
                            current += (current.isEmpty() ? "" : " ") + word;
                        } else {
                            if (!current.isEmpty()) {
                                lines.add(current);
                            }
                            current = word;
                        }
                    }
                    if (!current.isEmpty()) {
                        lines.add(current);
                    }
                }
            }
            
            return lines.toArray(new String[0]);
        }
    }
}
