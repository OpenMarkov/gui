package org.openmarkov.gui.window.edition.networkEditorPanel;

import org.openmarkov.core.exception.DoEditException;
import org.openmarkov.core.exception.UnreachableException;
import org.openmarkov.core.model.network.Point2D;
import org.openmarkov.gui.action.MoveNodeEdit;
import org.openmarkov.gui.graphic.VisualNode;
import org.openmarkov.java.swing.GraphicsUtilities;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class NodesAlignment {
    
    
    private static final int HORIZONTAL_SPACING_IN_VERTICAL_ALIGN = 50;
    private static final int VERTICAL_SPACING_IN_HORIZONTAL_ALIGN = 30;
    
    public static void verticalAlign(NetworkEditorPanel networkEditorPanel) {
        var selectedNodes = new ArrayList<>(networkEditorPanel.getVisualNetwork().getSelectedNodes());
        var posX = networkEditorPanel.getVisualNetwork().getLastSelectedNode().getPosition().getX();
        var maxWidth = selectedNodes.stream().mapToDouble(node->node.getShape((Graphics2D) networkEditorPanel.getGraphics()).getBounds2D().getWidth()).max().getAsDouble();
        
        selectedNodes.forEach(selectedNode -> {
            var nodeWidth = selectedNode.getShape((Graphics2D) networkEditorPanel.getGraphics()).getBounds2D().getWidth();
            
            selectedNode.setTemporalPosition(new Point2D.Double(posX+ ((maxWidth-nodeWidth)/2), selectedNode.getPosition()
                                                                                                            .getY()));
        });
        Collections.reverse(selectedNodes);
        
        Function<VisualNode, Shape> doubleDoubleFunction = visualNode -> GraphicsUtilities.inflateShape(visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics()), (double) VERTICAL_SPACING_IN_HORIZONTAL_ALIGN / 10);
        BiConsumer<VisualNode, Double> pushForward = VisualNode::setTemporalCoordinateY;
        Function<VisualNode, Double> getForward = visualNode -> visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics())
                                                                          .getBounds2D().getY();
        Function<VisualNode, Double> getLineSize = visualNode -> visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics())
                                                                           .getBounds2D().getHeight();
        ShapeSorter.resolveOverlaps(
                selectedNodes
                        .stream()
                        .map(visualNode -> new ShapeSorter.ElementSorter<>(visualNode,
                                                                           doubleDoubleFunction, pushForward,
                                                                           getForward, getLineSize))
                        .toList(),
                VERTICAL_SPACING_IN_HORIZONTAL_ALIGN);
        
        try {
            new MoveNodeEdit(selectedNodes).executeEdit();
        } catch (DoEditException e) {
            throw new UnreachableException(e);
        }
    }
    
    public static boolean canVerticalAlign(NetworkEditorPanel networkEditorPanel) {
        var selectedNodes = networkEditorPanel.getVisualNetwork().getSelectedNodes();
        if(atLeastOneNodeOverlapsWithAnother(networkEditorPanel, selectedNodes)){
            return true;
        }
        /*
        var posStatistics = selectedNodes.stream()
                                         .mapToDouble(selectedNode -> selectedNode.getPosition().getX())
                                         .summaryStatistics();
        return posStatistics.getMin() != posStatistics.getMax();
        */
        return true;
    }
    
    public static void horizontalAlign(NetworkEditorPanel networkEditorPanel) {
        
        
        var selectedNodes = new ArrayList<>(networkEditorPanel.getVisualNetwork().getSelectedNodes());
        var posY = networkEditorPanel.getVisualNetwork().getLastSelectedNode().getPosition().getY();
        
        var maxHeight = selectedNodes.stream().mapToDouble(node->node.getShape((Graphics2D) networkEditorPanel.getGraphics()).getBounds2D().getHeight()).max().getAsDouble();
        
        selectedNodes.forEach(selectedNode -> {
            var nodeHeight = selectedNode.getShape((Graphics2D) networkEditorPanel.getGraphics()).getBounds2D().getHeight();
            
            selectedNode.setTemporalPosition(new Point2D.Double(selectedNode.getPosition().getX(), posY+ ((maxHeight-nodeHeight)/2)));
        });
        
        Collections.reverse(selectedNodes);
        
        Function<VisualNode, Shape> doubleDoubleFunction = visualNode -> GraphicsUtilities.inflateShape(visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics()), (double) HORIZONTAL_SPACING_IN_VERTICAL_ALIGN / 10);
        BiConsumer<VisualNode, Double> pushForward = VisualNode::setTemporalCoordinateX;
        Function<VisualNode, Double> getForward = visualNode -> visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics())
                                                                          .getBounds2D().getX();
        Function<VisualNode, Double> getLineSize = visualNode -> visualNode.getShape((Graphics2D) networkEditorPanel.getGraphics())
                                                                           .getBounds2D().getWidth();
        ShapeSorter.resolveOverlaps(
                selectedNodes
                        .stream()
                        .map(visualNode -> new ShapeSorter.ElementSorter<>(visualNode,
                                                                           doubleDoubleFunction, pushForward,
                                                                           getForward, getLineSize))
                        .toList(),
                HORIZONTAL_SPACING_IN_VERTICAL_ALIGN);
        
        
        try {
            new MoveNodeEdit(selectedNodes).executeEdit();
        } catch (DoEditException e) {
            throw new UnreachableException(e);
        }
    }
    
    public static boolean canHorizontalAlign(NetworkEditorPanel networkEditorPanel) {
        var selectedNodes = networkEditorPanel.getVisualNetwork().getSelectedNodes();
        if(atLeastOneNodeOverlapsWithAnother(networkEditorPanel, selectedNodes)){
            return true;
        }
        /*
        var posStatistics = selectedNodes.stream()
                                         .mapToDouble(selectedNode -> selectedNode.getPosition().getY())
                                         .summaryStatistics();
        return posStatistics.getMin() != posStatistics.getMax();
        */
        return true;
    }
    
    private static boolean atLeastOneNodeOverlapsWithAnother(NetworkEditorPanel networkEditorPanel, List<VisualNode> selectedNodes) {
        return selectedNodes.stream()
                            .anyMatch(node ->
                                              selectedNodes.stream().filter(otherNode -> otherNode != node)
                                                           .anyMatch(otherNode -> GraphicsUtilities.shapesIntersect(
                                                                   node.getShape((Graphics2D) networkEditorPanel.getGraphics()),
                                                                   otherNode.getShape((Graphics2D) networkEditorPanel.getGraphics())
                                                           ))
                            );
    }
    
    public static class ShapeSorter {
        
        
        static class ElementSorter<T, S extends Shape> {
            
            private final T element;
            private final Function<? super T, ? extends S> shapeGetter;
            private final BiConsumer<? super T, Double> setForward;
            private final Function<? super T, Double> getForward;
            private final Function<? super T, Double> getLineSize;
            
            ElementSorter(T element, Function<? super T, ? extends S> shapeGetter, BiConsumer<? super T, Double> setForward, Function<? super T, Double> getForward, Function<? super T, Double> getLineSize) {
                this.element = element;
                this.shapeGetter = shapeGetter;
                this.setForward = setForward;
                this.getForward = getForward;
                this.getLineSize = getLineSize;
            }
            
            S shape() {
                return this.shapeGetter.apply(this.element);
            }
            
            void setForward(double x) {
                this.setForward.accept(this.element, x);
            }
            
            double getForward() {
                return this.getForward.apply(this.element);
            }
            
            double getLineSize() {
                return this.getLineSize.apply(this.element);
            }
            
        }
        
        private static <T, S extends Shape> void resolveOverlaps(List<? extends ElementSorter<T, S>> elements, double spacing) {
            for (int indexOfShapeToCheck = 0; indexOfShapeToCheck < elements.size(); indexOfShapeToCheck++) {
                ElementSorter<?, ?> elementToCheck = elements.get(indexOfShapeToCheck);
                for (int possibleOverlapIndex = 0; possibleOverlapIndex < indexOfShapeToCheck; possibleOverlapIndex++) {
                    ElementSorter<?, ?> overlappedElement = elements.get(possibleOverlapIndex);
                    if (!GraphicsUtilities.shapesIntersect(elementToCheck.shape(), overlappedElement.shape())) {
                        continue;
                    }
                    double pushPosition = overlappedElement.getForward() + overlappedElement.getLineSize() + spacing;
                    if (pushPosition > elementToCheck.getForward()) {
                        elementToCheck.setForward(pushPosition);
                    }
                }
            }
        }
        
    }
    
}
