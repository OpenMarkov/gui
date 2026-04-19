package org.openmarkov.java.swing;

import javax.swing.JMenu;
import java.awt.Component;
import java.awt.Container;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentUtilities {
    
    public static ArrayList<Component> parents(Component component) {
        var parents = new ArrayList<Component>();
        while (component.getParent() != null) {
            parents.add(component.getParent());
            component = component.getParent();
        }
        return parents;
    }
    
    public static ArrayDeque<Component> flatComponents(Component component) {
        ArrayDeque<Component> toVisit = new ArrayDeque<Component>();
        ArrayDeque<Component> resultStack = new ArrayDeque<Component>();
        toVisit.addFirst(component);
        while (!toVisit.isEmpty()) {
            var visitingComponent = toVisit.removeFirst();
            resultStack.add(visitingComponent);
            var subcomponents = ComponentUtilities.extractComponents(visitingComponent)
                                                  .collect(Collectors.toCollection(ArrayList::new));
            Collections.reverse(subcomponents);
            subcomponents.forEach(toVisit::addFirst);
        }
        return resultStack;
    }
    
    
    public static Stream<Component> extractComponents(Component component) {
        return switch (component) {
            case JMenu menu -> Stream.concat(
                    Arrays.stream(menu.getComponents()),
                    Arrays.stream(menu.getMenuComponents()));
            case Container container -> Arrays.stream(container.getComponents());
            case null, default -> Stream.empty();
        };
    }
    
}
