package it.unibo.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

public final class Constants {
    /** The screen dimension as detected from the system. */
    public static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    /** The screen height. */
    public static final int SH = (int) SCREEN.getHeight();
    /** The screen width. */
    public static final int SW = (int) SCREEN.getWidth();

    /** The scale factor for the game frame. */
    public static final double SCALE = 0.8;

    /** Height of the game frame in pixels. */
    public static final int FRAME_HEIGHT = (int) (SH * SCALE);
     /** Height of the game frame in pixels. */
    public static final int FRAME_WIDTH = (int) (FRAME_HEIGHT * SCALE);


    // COLORS
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color BORDER_COLOR = new Color(200, 200, 200);
    public static final Color PRIMARY_COLOR = new Color(0, 150, 136);
    public static final Color SYSTEM_MESSAGE_TEXT_COLOR = new Color(150, 150, 150);
    
    // FONTS
    public static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);
    public static final Font SYSTEM_MESSAGE_FONT = new Font("Arial", Font.ITALIC, 11);
    public static final Font TIMESTAMP_FONT = new Font("Arial", Font.PLAIN, 10);
    
    // BUTTON DIMENSIONS
    public static final int INPUT_BUTTON_HEIGHT = 40;
    public static final int BUTTON_WIDTH = 100;

    // MESSAGE DIMENSIONS
    public static final int MESSAGE_WRAPPER_MAX_HEIGHT = 70;
    public static final int SYSTEM_MESSAGE_MAX_HEIGHT = 50;
    
}
