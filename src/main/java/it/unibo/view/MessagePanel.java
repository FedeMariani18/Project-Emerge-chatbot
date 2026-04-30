package it.unibo.view;

import javax.swing.*;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static it.unibo.common.Constants.*;

public class MessagePanel extends JPanel {
    private static final int MESSAGE_PADDING = 5;
    private static final int MESSAGE_HORIZONTAL_PADDING = 10;
    private static final int VERTICAL_STRUT_SIZE = 5;
    
    private Box messageBox;
    private JScrollPane scrollPane;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public MessagePanel() {
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout()); 
        
        messageBox = Box.createVerticalBox();
        messageBox.setBackground(BACKGROUND_COLOR);
        messageBox.setOpaque(true);
        messageBox.add(Box.createVerticalGlue());
        
        scrollPane = new JScrollPane(messageBox);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(String text, boolean isUser) {
        // The outer wrapper (the chat row)
        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING, MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING));

        // The actual "Bubble" (a JPanel instead of a JTextArea)
        JPanel bubble = new JPanel(new BorderLayout(5, 2)); // 5px horizontal gap, 2px vertical
        bubble.setBackground(isUser ? new Color(0, 150, 136) : new Color(230, 230, 230));
        bubble.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // Text Area
        JTextArea textArea = new JTextArea(text);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false); // Transparent to show the bubble color
        textArea.setFont(DEFAULT_FONT);
        textArea.setForeground(isUser ? Color.WHITE : Color.BLACK);

        // Time Label
        String time = timeFormat.format(new Date());
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 10)); // Smaller font
        timeLabel.setForeground(isUser ? new Color(200, 200, 200) : Color.GRAY);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Dynamic Size Calculation
        int maxWidth = (int)Math.round(((double)FRAME_WIDTH) * 0.75);
        textArea.setSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        Dimension textDim = textArea.getPreferredSize();
        
        // If the text is narrower than 300px, the bubble shrinks (optional)
        int finalWidth = Math.min(maxWidth, textDim.width + 20); 
        bubble.setPreferredSize(new Dimension(finalWidth, textDim.height + 35)); // +25 to make room for the time

        // Assemblaggio della Bolla
        bubble.add(textArea, BorderLayout.CENTER);
        bubble.add(timeLabel, BorderLayout.SOUTH);

        // Positioning in the row
        if (isUser) {
            messageWrapper.add(bubble, BorderLayout.EAST);
        } else {
            messageWrapper.add(bubble, BorderLayout.WEST);
        }

        // Height constraint for BoxLayout
        messageWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, bubble.getPreferredSize().height + 10));

        // UI Update (Standard logic)
        if (messageBox.getComponentCount() > 0) {
            messageBox.remove(messageBox.getComponentCount() - 1);
        }
        messageBox.add(messageWrapper);
        messageBox.add(Box.createVerticalStrut(VERTICAL_STRUT_SIZE));
        messageBox.add(Box.createVerticalGlue());

        messageBox.revalidate();
        messageBox.repaint();
        
        // Auto-scroll
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
