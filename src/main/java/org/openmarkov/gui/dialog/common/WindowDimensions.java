package org.openmarkov.gui.dialog.common;

import java.awt.*;
import java.io.Serializable;

public record WindowDimensions(Point location, Dimension size, int extendedState) implements Serializable {
    
}
