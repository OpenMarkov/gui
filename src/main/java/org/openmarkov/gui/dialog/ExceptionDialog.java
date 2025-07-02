package org.openmarkov.integrationTests.integrationTests.testOpenMarkovException2;

import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.exception.InvalidNetworkTypeException;
import org.openmarkov.core.exception.OpenMarkovException;
import org.openmarkov.core.exception.OpenMarkovException2;
import org.openmarkov.core.localize.StringDatabase;
import org.openmarkov.core.logging.OpenMarkovLogger;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.DistinctLinks;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.DecisionAnalysisNetworkType;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.MissingFormatArgumentException;
import java.util.stream.Collectors;

/**
 * Allows to display exception details in a dialog box with a method called {@link ExceptionDialog#show(Exception)}.
 *
 * @author jrico
 */
public class ExceptionDialog {
    
    /**
     * Gets the title and message of an exception and then shows them with a dialog.
     *
     * @param exception the exception to show.
     */
    public static void show(Exception exception) {
        ExceptionDialog.show(exception, null);
    }
    
    /**
     * Gets the title and message of an exception and then shows them with a dialog.
     *
     * @param exception the exception to show.
     * @param frame the parent component from which the dialog should pop.
     */
    public static void show(Exception exception, @Nullable java.awt.Component frame) {
        TitleAndMessage titleAndMessage = getTitleAndMessage(exception);
        JOptionPane.showMessageDialog(frame, titleAndMessage.message, titleAndMessage.title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Gets a title and a message for an {@link Exception}. This is different for different exception classes:
     * <ul>
     *   <li>If the class extends {@link OpenMarkovException2}, the title and message will come from
     *   {@code OpenMarkovException2#getExceptionTitle()} and {@code OpenMarkovException2#getExceptionMessage()}.</li>
     *   <li>If the class extends {@link OpenMarkovException}, the title and message will come from calling
     *   {@link StringDatabase#getString(String)} with {@link OpenMarkovException#getToken()} + {@code ".title"} and
     *   {@code ".message"}   </li>
     *   <li>If the exception is in a bundle file, it will take the title and message from the
     *   {@link StringDatabase#getUniqueInstance()}</li>
     *   <li>In any other case, the title is the localization of the key {@code "UnlocalizedJavaException.title"} and the
     *   message is the localization of the key {@code "UnlocalizedJavaException.value"} followed by the
     *   {@link Exception}'s stacktrace.
     *   </li>
     * </ul>
     *
     * @param exception the exception to extract a title and message to represent it.
     * @return a title and a message representing the exception.
     */
    private static TitleAndMessage getTitleAndMessage(Exception exception) {
        if (exception instanceof OpenMarkovException2 openMarkovException2) {
            try {
                var getExceptionTitle = OpenMarkovException2.class.getDeclaredMethod("getExceptionTitle");
                getExceptionTitle.setAccessible(true);
                String title = (String) getExceptionTitle.invoke(openMarkovException2);
                var getExceptionMessage = OpenMarkovException2.class.getDeclaredMethod("getExceptionMessage");
                getExceptionMessage.setAccessible(true);
                String message = (String) getExceptionMessage.invoke(openMarkovException2);
                return new TitleAndMessage(title, message);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (exception instanceof OpenMarkovException openMarkovException) {
            String token = openMarkovException.getToken();
            String title = StringDatabase.getUniqueInstance().getString(token + ".title");
            String message = StringDatabase.getUniqueInstance().getString(token + ".message");
            try {
                message = String.format(message, openMarkovException.getAttributes());
            } catch (MissingFormatArgumentException e1) {
                OpenMarkovLogger.LOGGER.warn("Invalid number of arguments in the formatter: " + message);
            }
            return new TitleAndMessage(title, message);
        }
        @Nullable Class<Exception> exceptionClass = (Class<Exception>) exception.getClass();
        while (exceptionClass != null) {
            String exceptionClassName = exceptionClass.getName();
            if (exceptionClassName.contains(".")) {
                exceptionClassName = exceptionClassName.substring(exceptionClassName.lastIndexOf('.') + 1);
            }
            String title = StringDatabase.getUniqueInstance()
                                         .getNullableString(exceptionClassName + ".title");
            String message = StringDatabase.getUniqueInstance()
                                           .getNullableString(exceptionClassName + ".message");
            if (title != null || message != null) {
                return new TitleAndMessage(title, message);
            }
            try {
                exceptionClass = (Class<Exception>) exceptionClass.getSuperclass();
                if (exceptionClass.isAssignableFrom(Exception.class) && exceptionClass != Exception.class) {
                    exceptionClass = null;
                }
            } catch (ClassCastException ignored) {
                exceptionClass = null;
            }
        }
        String title = StringDatabase.getUniqueInstance().getString("UnlocalizedJavaException.title");
        String stackTrace = Arrays.stream(exception.getStackTrace()).map(StackTraceElement::toString)
                                  .filter(s -> !s.isBlank())
                                  .map(s -> "\tat " + s)
                                  .collect(Collectors.joining("\n"));
        String message = StringDatabase.getUniqueInstance()
                                       .getString("UnlocalizedJavaException.message") + "\n" + exception + "\n" + stackTrace;
        return new TitleAndMessage(title, message);
    }
    
    /**
     * Represents a title and a message of an {@link OpenMarkovException2}.
     */
    private record TitleAndMessage(String title, String message) {
    }
    
}