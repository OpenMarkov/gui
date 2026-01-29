package org.openmarkov.gui.configuration;

import java.io.*;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

public final class JavaSerializationUtils {
    
    public static <T> T javaDeserialize(String string) {
        byte[] bytes = Base64.getDecoder().decode(string);
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return (T) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> String javaSerialize(T value) {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos)
        ) {
            out.writeObject(value);
            byte[] objectBytes = bos.toByteArray();
            return Base64.getEncoder().encodeToString(objectBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static <T> boolean verifyCollection(Object collection, Class<T> clazz) {
        if (!(collection instanceof Collection<?> castedCollection)) {
            return false;
        }
        for (var element : castedCollection) {
            var isValid = element == null || clazz.isInstance(element);
            if (!isValid) return false;
        }
        return true;
    }
    
    public static <K, V> boolean verifyMap(Object map, Class<K> kClazz, Class<V> vClazz) {
        if (!(map instanceof Map<?, ?> castedMap)) {
            return false;
        }
        for (var entry : castedMap.entrySet()) {
            var isValidKey = entry.getKey() == null || kClazz.isInstance(entry.getKey());
            if (!isValidKey) return false;
            var isValidValue = entry.getValue() == null || vClazz.isInstance(entry.getValue());
            if (!isValidValue) return false;
        }
        return true;
    }
    
}
