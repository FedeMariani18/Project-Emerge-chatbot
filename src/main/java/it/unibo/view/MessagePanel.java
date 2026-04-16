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
        setLayout(new BorderLayout()); 
        
        messageBox = Box.createVerticalBox();
        messageBox.setBackground(BACKGROUND_COLOR);
        messageBox.setOpaque(true);
        messageBox.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(messageBox);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(String text, boolean isUser) {
        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(BACKGROUND_COLOR);
        messageWrapper.setBorder(BorderFactory.createEmptyBorder(MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING, MESSAGE_PADDING, MESSAGE_HORIZONTAL_PADDING));

        JTextArea balloon = new JTextArea(text);
        balloon.setLineWrap(true);
        balloon.setWrapStyleWord(true);
        balloon.setEditable(false);
        balloon.setFocusable(false);
        balloon.setFont(DEFAULT_FONT);
        
        balloon.setOpaque(true);
        balloon.setBackground(isUser ? new Color(0, 150, 136) : new Color(230, 230, 230));
        balloon.setForeground(isUser ? Color.WHITE : Color.BLACK);
        balloon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        int maxWidth = 300; 
        balloon.setSize(new Dimension(maxWidth, Integer.MAX_VALUE)); 
        
        Dimension d = balloon.getPreferredSize();
        d.width = maxWidth;
        balloon.setPreferredSize(d); 

        if (isUser) {
            messageWrapper.add(balloon, BorderLayout.EAST);
        } else {
            messageWrapper.add(balloon, BorderLayout.WEST);
        }

        messageWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, balloon.getPreferredSize().height + (MESSAGE_PADDING * 2)));

        if (messageBox.getComponentCount() > 0) {
            messageBox.remove(messageBox.getComponentCount() - 1);
        }
        messageBox.add(messageWrapper);
        messageBox.add(Box.createVerticalStrut(VERTICAL_STRUT_SIZE));
        messageBox.add(Box.createVerticalGlue());

        messageBox.revalidate();
        messageBox.repaint();

        SwingUtilities.invokeLater(() -> {
            messageBox.scrollRectToVisible(messageWrapper.getBounds());
        });
    }
}
