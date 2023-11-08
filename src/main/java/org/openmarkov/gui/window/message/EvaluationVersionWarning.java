package org.openmarkov.gui.window.message;
/**
 * Warning for OpenMarkov with DES evaluation software
 * @author cmyago
 * @version 1.0; 14/01/2023
 */

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.DAYS;

public class EvaluationVersionWarning {
    private static final LocalDate END_OF_LIFE = LocalDate.of(2024, 06, 1);

    public static boolean show() {

        String evaluationMessage;
        long daysLeft = DAYS.between(LocalDate.now(), END_OF_LIFE);
        if (daysLeft < 0) daysLeft = 0;
        boolean expired = daysLeft == 0;

        String messageBody = expired ? loadResourceAsText("message-expired.html")
                : loadResourceAsText("message-not-expired.html");

        try {
            evaluationMessage = String.format(
                    loadResourceAsText("evaluation-message.html"),
                    expired ? "red" : "black",
                    END_OF_LIFE.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)),
                    daysLeft,
                    messageBody
            );
        } catch (Exception e) {
            evaluationMessage = e.toString();
        }

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setEnabled(true);
        textPane.setText(evaluationMessage);
        textPane.setPreferredSize(new Dimension(900, 550));
        textPane.addHyperlinkListener(hle -> {
                    if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                        try {
                            Desktop.getDesktop().browse(hle.getURL().toURI());
                        } catch (Exception ignored) {
                        }
                    }
                }
        );

        JOptionPane.showMessageDialog(
                null,
                textPane,
                "OpenMarkov evaluation version",
                JOptionPane.PLAIN_MESSAGE
        );

        return expired;
    }

    private static String loadResourceAsText(String resourceFileName) {
        try (InputStream is = EvaluationVersionWarning.class.getClassLoader().getResourceAsStream(resourceFileName)) {
            return new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A").next();
        } catch (Exception e) {
            return e.toString();
        }
    }
}
