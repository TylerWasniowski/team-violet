/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.framework;

import com.horstmann.violet.commands.ChangePropertyCommand;
import com.horstmann.violet.commands.MoveNodeCommand;
import com.horstmann.violet.commands.ChangeItemSelectionsCommand;
import com.horstmann.violet.graphs.TeamDiagram;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.beans.*;
import java.util.*;
import java.util.List;

/**
 * A panel to draw a graph
 */
public class GraphPanel extends JPanel
{
   private static final long serialVersionUID = -6784187653877104152L;

   /**
    * Constructs a graph.
    * @param aToolBar the tool bar with the node and edge tools
    */
   public GraphPanel(ToolBar aToolBar)
   {
      grid = new Grid();
      gridSize = GRID;
      grid.setGrid((int) gridSize, (int) gridSize);
      zoom = 1;
      toolBar = aToolBar;
      setBackground(Color.WHITE);

      selectedItems = new HashSet<>();
      addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent event)
         {
            requestFocus();
            final Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
                  event.getY() / zoom);
            boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 
            Node n = graph.findNode(mousePoint);
            Edge e = graph.findEdge(mousePoint);
            Object tool = toolBar.getSelectedTool();
            if (event.getClickCount() > 1
                  || (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
            // double/right-click
            {
               if (e != null)
               {
                  setSelectedItem(e);
                  editSelected();
               }
               else if (n != null)
               {
                  setSelectedItem(n);
                  editSelected();
               }
               else
               {
                  toolBar.showPopup(GraphPanel.this, mousePoint,
                        new ActionListener()
                        {
                           public void actionPerformed(ActionEvent event)
                           {
                              Object tool = toolBar.getSelectedTool();
                              if (tool instanceof Node)
                              {
                                 Node prototype = (Node) tool;

                                 Node newNode;
                                 try {
                                    newNode = (Node) prototype.clone();
                                 } catch (CloneNotSupportedException ex) {
                                    return;
                                 }

                                 boolean added = graph.add(newNode, mousePoint);
                                 if (added)
                                 {
                                    setModified(true);
                                    setSelectedItem(newNode);
                                 }
                              }
                           }
                        });
               }
            }
            else if (tool == null) // select
            {
               if (e != null)
               {
                  setSelectedItem(e);
               }
               else if (n != null)
               {
                  if (isCtrl)
                     addSelectedItem(n);
                  else if (!selectedItems.contains(n))
                     setSelectedItem(n);
                  dragMode = DRAG_MOVE;
               }
               else
               {
                  if (!isCtrl)
                     clearSelection();
                  dragMode = DRAG_LASSO;
               }
            }
            else if (tool instanceof Node)
            {
               Node prototype = (Node) tool;

               Node newNode;
               try {
                  newNode = (Node) prototype.clone();
               } catch (CloneNotSupportedException ex) {
                  return;
               }

               boolean added = graph.add(newNode, mousePoint);
               if (added)
               {
                  setModified(true);
                  setSelectedItem(newNode);
                  dragMode = DRAG_MOVE;
               }
               else if (n != null)
               {
                  if (isCtrl)
                     addSelectedItem(n);
                  else if (!selectedItems.contains(n))
                     setSelectedItem(n);
                  dragMode = DRAG_MOVE;
               }
            }
            else if (tool instanceof Edge)
            {
               if (n != null) dragMode = DRAG_RUBBERBAND;
            }

            lastMousePoint = mousePoint;
            mouseDownPoint = mousePoint;
            repaint();
         }

         public void mouseReleased(MouseEvent event)
         {
            Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
                  event.getY() / zoom);
            Object tool = toolBar.getSelectedTool();
            if (dragMode == DRAG_RUBBERBAND)
            {
               Edge prototype = (Edge) tool;

               Edge newEdge;
               try {
                  newEdge = (Edge) prototype.clone();
               } catch (CloneNotSupportedException ex) {
                  return;
               }

               if (mousePoint.distance(mouseDownPoint) > CONNECT_THRESHOLD
                     && graph.connect(newEdge, mouseDownPoint, mousePoint))
               {
                  setModified(true);
                  setSelectedItem(newEdge);
               }
            }
            else if (dragMode == DRAG_MOVE)
            {
               if (graph instanceof TeamDiagram) {
                  selectedItems.forEach((Object selectedItem) -> {
                     if (selectedItem instanceof Node) {
                        Node selectedNode = (Node) selectedItem;

                        double newX = selectedNode.getBounds().getX();
                        double newY = selectedNode.getBounds().getY();

                        ((TeamDiagram) graph).sendCommandToServer(
                                new MoveNodeCommand(((Node) selectedItem).getID(), new Point2D.Double(newX, newY)));
                     }
                  });
               }

               graph.layout();
               setModified(true);
            }
            dragMode = DRAG_NONE;

            revalidate();
            repaint();
         }
      });

      addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent event)
         {
            Point2D mousePoint = new Point2D.Double(event.getX() / zoom, 
                  event.getY() / zoom);
            boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0; 

            if (dragMode == DRAG_MOVE && lastSelected instanceof Node)
            {               
               Node lastNode = (Node) lastSelected;
               Rectangle2D bounds = lastNode.getBounds();
               double dx = mousePoint.getX() - lastMousePoint.getX();
               double dy = mousePoint.getY() - lastMousePoint.getY();
                            
               // we don't want to drag nodes into negative coordinates
               // particularly with multiple selection, we might never be 
               // able to get them back.
               Iterator iter = selectedItems.iterator();
               while (iter.hasNext())
               {
                  Object selected = iter.next();                 
                  if (selected instanceof Node)
                  {
                     Node n = (Node) selected;
                     bounds.add(n.getBounds());
                  }
               }
               dx = Math.max(dx, -bounds.getX());
               dy = Math.max(dy, -bounds.getY());
               
               iter = selectedItems.iterator();
               while (iter.hasNext())
               {
                  Object selected = iter.next();                 
                  if (selected instanceof Node)
                  {
                     Node n = (Node) selected;
                     n.translate(dx, dy);
                  }
               }
               // we don't want continuous layout any more because of multiple selection
               // graph.layout();
            }            
            else if (dragMode == DRAG_LASSO)
            {
               double x1 = mouseDownPoint.getX();
               double y1 = mouseDownPoint.getY();
               double x2 = mousePoint.getX();
               double y2 = mousePoint.getY();
               Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
                     Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
               Iterator iter = graph.getNodes().iterator();
               while (iter.hasNext())
               {
                  Node n = (Node) iter.next();
                  Rectangle2D bounds = n.getBounds();
                  if (!isCtrl && !lasso.contains(bounds))
                  {
                     removeSelectedItem(n);
                  }
                  else if (lasso.contains(bounds))
                  {
                     addSelectedItem(n);
                  }
               }
            }
            
            lastMousePoint = mousePoint;
            repaint();
         }
      });
   }

   /**
    * Edits the properties of the selected graph element.
    */
   public void editSelected()
   {
      UniquelyIdentifiable edited;
      if (lastSelected == null)
      {
         if (selectedItems.size() == 1)
            edited = selectedItems.iterator().next();
         else
            return;
      } else {
          edited = lastSelected;
      }

      PropertySheet sheet = new PropertySheet(edited, this);
      sheet.addChangeListener(new ChangeListener()
      {
         public void stateChanged(ChangeEvent event)
         {
            if (graph instanceof TeamDiagram
                    && event.getSource() instanceof PropertyEditor
                    && event instanceof PropertyChangeEvent) {
                ((TeamDiagram) graph).sendCommandToServer(
                        new ChangePropertyCommand(
                                edited.getID(),
                                ((PropertyChangeEvent) event).getPropertyName(),
                                ((PropertyEditor) event.getSource()).getValue()
                        )
                );
            }

            graph.layout();
            repaint();
         }
      });
      JOptionPane.showInternalMessageDialog(this, sheet, 
            ResourceBundle.getBundle("com.horstmann.violet.framework.EditorStrings").getString("dialog.properties"),            
            JOptionPane.QUESTION_MESSAGE);
      setModified(true);
   }

   /**
    * Removes the selected nodes or edges.
    */
   public void removeSelected()
   {
      Iterator iter = selectedItems.iterator();
      while (iter.hasNext())
      {
         Object selected = iter.next();                 
         if (selected instanceof Node)
         {
            graph.removeNode((Node) selected);
         }
         else if (selected instanceof Edge)
         {
            graph.removeEdge((Edge) selected);
         }
      }
      if (selectedItems.size() > 0) setModified(true);
      repaint();
   }

   /**
    * Set the graph in the panel
    * @param aGraph the graph to be displayed and edited
    */
   public void setGraph(Graph aGraph)
   {
      graph = aGraph;
      aGraph.setJPanel(this);
      setModified(false);
      revalidate();
      repaint();
   }
   /**
    * Paints the graph panel
    * @param g the graphics context
    */
   public synchronized void paintComponent(Graphics g)
   {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      g2.scale(zoom, zoom);
      Rectangle2D bounds = getBounds();
      Rectangle2D graphBounds = graph.getBounds(g2);
      if (!hideGrid) grid.draw(g2, new Rectangle2D.Double(0, 0, 
            Math.max(bounds.getMaxX() / zoom, graphBounds.getMaxX()), 
            Math.max(bounds.getMaxY() / zoom, graphBounds.getMaxY())));
      graph.draw(g2, grid);

      // Remove any selectedItems that don't exist in the graph
      synchronized (selectedItems) {
         Set<UniquelyIdentifiable> itemsToBeRemoved = new HashSet<>();
         selectedItems.stream()
                 .filter((UniquelyIdentifiable selectedItem) ->
                         (!graph.getNodes().contains(selectedItem) && !graph.getEdges().contains(selectedItem)))
                 .forEach(itemsToBeRemoved::add);

         itemsToBeRemoved.stream().forEach(this::removeSelectedItem);
      }

      drawItemSelections(g2, selectedItems);

      // Draw selections of all of the other clients
      if (graph instanceof TeamDiagram) {
         for (String graphID: ((TeamDiagram) graph).getItemSelectionsMap().keySet()) {

            // Draw selections and names for all of the other clients
            if (!graphID.equals(((TeamDiagram) graph).getClientID())) {

               Pair<Color, Set<UniquelyIdentifiable>> colorItemSelectionsPair =
                       ((TeamDiagram) graph).getItemSelectionsMap().get(graphID);

               // Remove any node from the selection map if it was already deleted
               Iterator<UniquelyIdentifiable> selectedItemsIterator = colorItemSelectionsPair.getValue().iterator();
               UniquelyIdentifiable selectedItem;
               while (selectedItemsIterator.hasNext()) {
                  selectedItem = selectedItemsIterator.next();
                  if (((TeamDiagram) graph).findItemFromID(selectedItem.getID()) == null)
                     selectedItemsIterator.remove();

               }

               drawItemSelections(g2, colorItemSelectionsPair.getValue(), colorItemSelectionsPair.getKey(), graphID);
            }

         }
      }
      
      if (dragMode == DRAG_RUBBERBAND)
      {
         Color oldColor = g2.getColor();
         g2.setColor(PURPLE);
         g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
         g2.setColor(oldColor);
      }      
      else if (dragMode == DRAG_LASSO)
      {
         Color oldColor = g2.getColor();
         g2.setColor(PURPLE);
         double x1 = mouseDownPoint.getX();
         double y1 = mouseDownPoint.getY();
         double x2 = lastMousePoint.getX();
         double y2 = lastMousePoint.getY();
         Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2), 
               Math.min(y1, y2), Math.abs(x1 - x2) , Math.abs(y1 - y2));
         g2.draw(lasso);
         g2.setColor(oldColor);
      }
   }

   private static void drawItemSelections(Graphics2D g2, Set<UniquelyIdentifiable> selectedItems) {
      drawItemSelections(g2, selectedItems, PURPLE, "You");
   }

   private static void drawItemSelections(Graphics2D g2, Set<UniquelyIdentifiable> selectedItems, Color color, String nameOfSelector) {
      FontMetrics fontMetrics = g2.getFontMetrics();

      Color oldColor = g2.getColor();
      g2.setColor(color);
      for (UniquelyIdentifiable selectedItem: selectedItems) {
         if (selectedItem instanceof Node) {
            Rectangle2D grabberBounds = ((Node) selectedItem).getBounds();
            drawFilledSquare(g2, grabberBounds.getMinX(), grabberBounds.getMinY(), color);
            drawFilledSquare(g2, grabberBounds.getMinX(), grabberBounds.getMaxY(), color);
            drawFilledSquare(g2, grabberBounds.getMaxX(), grabberBounds.getMinY(), color);
            drawFilledSquare(g2, grabberBounds.getMaxX(), grabberBounds.getMaxY(), color);

            int stringX = (int) (grabberBounds.getX() + (grabberBounds.getWidth() - fontMetrics.stringWidth(nameOfSelector))/2);
            int stringY = (int) grabberBounds.getMaxY() + fontMetrics.getHeight();
            g2.drawString(nameOfSelector, stringX, stringY);
         } else if (selectedItem instanceof Edge) {
            Line2D line = ((Edge) selectedItem).getConnectionPoints();
            drawFilledSquare(g2, line.getX1(), line.getY1(), color);
            drawFilledSquare(g2, line.getX2(), line.getY2(), color);

            int stringX = (int) (line.getX1() + ((line.getX2() - line.getX1()) - fontMetrics.stringWidth(nameOfSelector))/2);
            int stringY = (int) Math.max(line.getY1(), line.getY2()) + fontMetrics.getHeight();
            g2.drawString(nameOfSelector, stringX, stringY);
         }
      }
      g2.setColor(oldColor);
   }

   /**
    * Draws a single "grabber", a filled square in purple.
    * @param g2 the graphics context
    * @param x the x coordinate of the center of the grabber
    * @param y the y coordinate of the center of the grabber
    */
   public static void drawFilledSquare(Graphics2D g2, double x, double y)
   {
      drawFilledSquare(g2, x, y, PURPLE);
   }

   /**
    * Draws a single "grabber", a filled square with the given color.
    * @param g2 the graphics context
    * @param color the color fo the filled square
    * @param x the x coordinate of the center of the grabber
    * @param y the y coordinate of the center of the grabber
    */
   private static void drawFilledSquare(Graphics2D g2,double x, double y, Color color)
   {
      final int SIZE = 5;
      Color oldColor = g2.getColor();
      g2.setColor(color);
      g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
      g2.setColor(oldColor);
   }
   /**
    * Gets the correct size of the graph panel
    * @return the size of the graph panel as a Dimension
    */
   public Dimension getPreferredSize()
   {
      Rectangle2D bounds = graph.getBounds((Graphics2D) getGraphics());
      return new Dimension((int) (zoom * bounds.getMaxX()),
            (int) (zoom * bounds.getMaxY()));
   }

   /**
    * Changes the zoom of this panel. The zoom is 1 by default and is multiplied
    * by sqrt(2) for each positive stem or divided by sqrt(2) for each negative
    * step.
    * @param steps the number of steps by which to change the zoom. A positive
    * value zooms in, a negative value zooms out.
    */
   public void changeZoom(int steps)
   {
      final double FACTOR = Math.sqrt(2);
      for (int i = 1; i <= steps; i++)
         zoom *= FACTOR;
      for (int i = 1; i <= -steps; i++)
         zoom /= FACTOR;
      revalidate();
      repaint();
   }

   /**
    * Changes the grid size of this panel. The zoom is 10 by default and is
    * multiplied by sqrt(2) for each positive stem or divided by sqrt(2) for
    * each negative step.
    * @param steps the number of steps by which to change the zoom. A positive
    * value zooms in, a negative value zooms out.
    */
   public void changeGridSize(int steps)
   {
      final double FACTOR = Math.sqrt(2);
      for (int i = 1; i <= steps; i++)
         gridSize *= FACTOR;
      for (int i = 1; i <= -steps; i++)
         gridSize /= FACTOR;
      grid.setGrid((int) gridSize, (int) gridSize);
      graph.layout();
      repaint();
   }
   /**
    * Selects the next selectable item based on it's 
    * location on the graph.
    * @param n number of items to skip over in list of selectable items
    */
   public void selectNext(int n)
   {
      List<UniquelyIdentifiable> selectables = new ArrayList<>();
      selectables.addAll(graph.getNodes());
      selectables.addAll(graph.getEdges());
      if (selectables.size() == 0) return;
      java.util.Collections.sort(selectables, (UniquelyIdentifiable item, UniquelyIdentifiable otherItem) ->
      {
         double x1;
         double y1;
         if (item instanceof Node)
         {
            Rectangle2D bounds = ((Node) item).getBounds();
            x1 = bounds.getX();
            y1 = bounds.getY();
         }
         else
         {
            Point2D start = ((Edge) item).getConnectionPoints().getP1();
            x1 = start.getX();
            y1 = start.getY();
         }
         double x2;
         double y2;
         if (otherItem instanceof Node)
         {
            Rectangle2D bounds = ((Node) otherItem).getBounds();
            x2 = bounds.getX();
            y2 = bounds.getY();
         }
         else
         {
            Point2D start = ((Edge) otherItem).getConnectionPoints().getP1();
            x2 = start.getX();
            y2 = start.getY();
         }
         if (y1 < y2) return -1;
         if (y1 > y2) return 1;
         if (x1 < x2) return -1;
         if (x1 > x2) return 1;
         return 0;
      });
      int index;
      if (lastSelected == null) index = 0;
      else index = selectables.indexOf(lastSelected) + n;
      while (index < 0)
         index += selectables.size();
      index %= selectables.size();
      setSelectedItem(selectables.get(index));
      repaint();
   }

   /**
    * Checks whether this graph has been modified since it was last saved.
    * @return true if the graph has been modified
    */
   public boolean isModified()
   {
      return modified;
   }

   /**
    * Sets or resets the modified flag for this graph
    * @param newValue true to indicate that the graph has been modified
    */
   public void setModified(boolean newValue)
   {
      modified = newValue;

      if (frame == null)
      {
         Component parent = this;
         do
         {
            parent = parent.getParent();
         }
         while (parent != null && !(parent instanceof GraphFrame));
         if (parent != null) frame = (GraphFrame) parent;
      }
      if (frame != null)
      {
         String title = frame.getFileName();
         if (title != null)
         {
            if (modified)
            {
               if (!frame.getTitle().endsWith("*")) frame.setTitle(title + "*");
            }
            else frame.setTitle(title);
         }
      }
   }

   private void sendSelectionChangeToServer() {
      if (graph instanceof TeamDiagram) {
         ((TeamDiagram) graph).sendCommandToServer(
                 new ChangeItemSelectionsCommand(((TeamDiagram) graph).getClientID(), selectedItems));
      }
   }

   private void addSelectedItem(UniquelyIdentifiable item)
   {
      lastSelected = item;
      if (item != null) selectedItems.add(item);

      sendSelectionChangeToServer();
   }
   
   private void removeSelectedItem(UniquelyIdentifiable item)
   {
      if (item == lastSelected)
         lastSelected = null;
      selectedItems.remove(item);

      sendSelectionChangeToServer();
   }
   
   private void setSelectedItem(UniquelyIdentifiable item)
   {
      selectedItems.clear();
      addSelectedItem(item);
   }

   private void clearSelection()
   {
      setSelectedItem(null);
   }
   
   /**
    * Sets the value of the hideGrid property
    * @param newValue true if the grid is being hidden
    */
   public void setHideGrid(boolean newValue)
   {
      hideGrid = newValue;
      repaint();
   }

   /**
    * Gets the value of the hideGrid property
    * @return true if the grid is being hidden
    */
   public boolean getHideGrid()
   {
      return hideGrid;
   }

   private Graph graph;
   private Grid grid;
   private GraphFrame frame;
   private ToolBar toolBar;

   private double zoom;
   private double gridSize;
   private boolean hideGrid;
   private boolean modified;

   private UniquelyIdentifiable lastSelected;
   private Set<UniquelyIdentifiable> selectedItems;
   private Map<String, Set<UniquelyIdentifiable>> graphIDToSelectedItems;

   private Point2D lastMousePoint;
   private Point2D mouseDownPoint;
   private int dragMode;
      
   private static final int DRAG_NONE = 0;
   private static final int DRAG_MOVE = 1;
   private static final int DRAG_RUBBERBAND = 2;
   private static final int DRAG_LASSO = 3;
   
   private static final int GRID = 10;

   private static final int CONNECT_THRESHOLD = 8;

   private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);
}