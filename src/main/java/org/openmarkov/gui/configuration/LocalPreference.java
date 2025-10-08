package org.openmarkov.gui.configuration;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openmarkov.core.annotation.ToCheck;
import org.openmarkov.gui.dialog.ExceptionDialog;

import java.io.*;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * A simplified mechanism to access the contents of a {@link Preferences} node's value, alleviating you from manually
 * serializing and deserializing values to that node, verifying the node, or checking whether it is already written.
 * <p>
 * <strong>Example of use</strong>
 * <p>
 * Consider you have a node such as org/openmarkov/favourite_color to represent an accent {@link java.awt.Color} the
 * user can change, for this, you need to provide:
 * <ul>
 *     <li>The node and the key, in this case: {@code org/openmarkov} and {@code favourite_color}.</li>
 *     <li>The class of the value (Optional), in this case: {@link java.awt.Color}.class, this is used to verify
 *     the contents of the node are correct, in this case, if it was a {@link java.util.List} of Integers instead of a
 *     {@link java.awt.Color}, it would be incorrect.</li>
 *     <li>A default value when the user hasn't specified it's favourite color, in this case, this default color is
 *     Green.</li>
 * </ul>
 *
 * <pre>
 * {@code
 * private static final Preferences OPENMARKOV_PREFERENCES = Preferences.userRoot().node("org").node("openmarkov");
 * private static final LocalPreference<Color> FAVOURITE_COLOR = new LocalPreference<>
 *     (OPENMARKOV_PREFERENCES, "favourite_color", Color.class, () -> new Color(0, 255, 0));
 * }
 * </pre>
 * <br><br>
 * With this {@code FAVOURITE_COLOR} you can use the following functions to help you using the node's value:
 *
 * <ul>
 *     <li>{@link LocalPreference#get()}: This will give you the {@link java.awt.Color} calculating it from the node's
 *     value, or giving you the default value if not present.</li>
 *     <li>{@link LocalPreference#set(T)}: You set the new {@link java.awt.Color} of the node's value, which will also
 *     be persistently overwritten in the node.</li>
 *     <li>{@link LocalPreference#save()}: Persistently saves the current {@link java.awt.Color} to the node.</li>
 *     <li>{@link LocalPreference#clear()}: Removes the current {@link java.awt.Color}, and persistently removes the
 *     Preference node.</li>
 * </ul>
 *
 * @param <T> The type of the Value.
 * @author jrico
 */
public final class LocalPreference<T extends Serializable> {
    
    private final @NotNull Preferences node;
    private final @NotNull String key;
    private final @Nullable Class<? extends T> valueClass;
    private final @NotNull Supplier<? extends @NotNull T> defaultValue;
    private @Nullable T value;
    
    /**
     * When possible, using the constructor receiving a {@link Class}{@code <T>} over this constructor is preferred
     * ({@link LocalPreference#LocalPreference(Preferences, String, Class, Supplier)}), as that {@link Class} is used to
     * verify the node's value.
     *
     * @param node Node where the properties are written.
     * @param key The exact key in the node where the value is written
     * @param defaultValue A function returning a default value for when the preference's node isn't set.<br>
     *                     It is intended to be a function, so the default value is only loaded when needed.
     */
    public LocalPreference(@NotNull Preferences node, @NotNull String key, @NotNull Supplier<? extends @NotNull T> defaultValue) {
        this(node, key, null, defaultValue);
    }
    
    /**
     * @param node Node where the properties are written.
     * @param key The exact key in the node where the value is written.
     * @param valueClass The class of the value, this is used to verify the contents of the node match this class.
     * @param defaultValue A function returning a default value for when the preference's node isn't set.<br>
     *                     It is intended to be a function, so the default value is only loaded when needed.
     */
    public LocalPreference(@NotNull Preferences node, @NotNull String key, @Nullable Class<? extends T> valueClass, @NotNull Supplier<? extends @NotNull T> defaultValue) {
        this.node = Objects.requireNonNull(node);
        this.key = Objects.requireNonNull(key);
        this.valueClass = valueClass;
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }
    
    /**
     * Gets the value corresponding to the node.
     * <p>
     * If the node is empty (because it was cleared or wasn't ever written) or its value is not of type {@code T}, it
     * will return the calculated result of {@link LocalPreference#defaultValue}.
     * <p>
     * This operation is cached, meaning if the value was calculated in a previous call to
     * {@code get()}, it won't re-calculate it again, so the value is only loaded when needed.
     *
     * @return the value corresponding to the node.
     */
    public @NotNull T get() {
        if (this.value == null) {
            String nodeValue = this.node.get(this.key, null);
            if (nodeValue == null) {
                this.value = Objects.requireNonNull(this.defaultValue.get());
                return this.value;
            }
            byte[] data = Base64.getDecoder().decode(nodeValue);
            try (ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(data))) {
                T value = this.valueClass != null ? this.valueClass.cast(in.readObject()) : (T) in.readObject();
                this.value = Objects.requireNonNull(value);
            } catch (IOException | ClassNotFoundException | ClassCastException e) {
                this.value = Objects.requireNonNull(this.defaultValue.get());
            }
        }
        return this.value;
    }
    
    /**
     * Manually sets the {@link LocalPreference#value} of this node, which also implies calling to
     * {@link LocalPreference#save()} in order to save your changes persistently.
     *
     * @param newValue the new value, it must not be null.
     */
    public void set(@NotNull T newValue) {
        this.value = Objects.requireNonNull(newValue);
        this.save();
    }
    
    /**
     * Persistently saves the value into the node.
     * <p>
     * If the value hasn't been loaded by a previous call to {@link LocalPreference#get()}, the changes won't be saved.
     */
    @ToCheck(reasonKind = ToCheck.ReasonKind.USER_EXPERIENCE,
            reasonDescription = "Many of the exceptions of this class are ignored, as otherwise, the user would be" +
                    "bombarded with exceptions happening if their OS doesn't allow to use Backing Stores. But, do we " +
                    "want them to be logged nevertheless")
    public void save() {
        if (this.value == null) {
            return;
        }
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (ObjectOutput out = new ObjectOutputStream(byteOut)) {
            out.writeObject(this.value);
            String newNodeValue = Base64.getEncoder().encodeToString(byteOut.toByteArray());
            this.node.put(this.key, newNodeValue);
        } catch (IOException e) {
        }
    }
    
    /**
     * Removes the value and also removes it persistently from the {@link Preferences}' node.
     * <p>
     * This implies you won't find this node in your {@link Preferences}, and the next time you call to
     * {@link LocalPreference#get()}, you will get the {@link LocalPreference#defaultValue}.
     */
    public void clear() {
        this.node.remove(this.key);
        this.value = null;
    }
    
    /**
     * This is a shortcut method intended to be used for simple operations where you need to get the value, modify it,
     * and save it, all of it consecutively.
     *
     * @param onValue action to trigger over the value.
     */
    public void use(@NotNull Consumer<? super @NotNull T> onValue) {
        onValue.accept(this.get());
        this.save();
    }
}
