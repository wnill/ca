package de.wnill.master.core.wdp.viz;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JFrame;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import de.wnill.master.core.wdp.utils.TreeNode;

public class TreeVisualisation extends JFrame {

  private final mxGraph graph = new mxGraph();

  private Object parent = graph.getDefaultParent();

  public TreeVisualisation(TreeNode searchTree, Set<Integer> bestBidset) {
    visualizeTree(searchTree, bestBidset);
  }

  private void visualizeTree(TreeNode searchTree, Set<Integer> bestBidset) {

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(460, 400);
    this.setVisible(true);


    mxICell root =
        (mxICell) graph.insertVertex(parent, null, searchTree.getDeliveryIds(), 0, 0, 30, 30);

    for (TreeNode child : searchTree.getChildren()) {
      mxICell childCell =
          (mxICell) graph.insertVertex(parent, null, child.getDeliveryIds(), 0, 0, 30, 30);
      graph.insertEdge(parent, null, "", root, childCell);
      if (child.getChildren() != null && !child.getChildren().isEmpty()) {
        addChildCell(child, childCell);
      }
    }

    mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
    layout.execute(graph.getDefaultParent());

    mxGraphComponent graphComponent = new mxGraphComponent(graph);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(graphComponent, BorderLayout.CENTER);
  }

  private void addChildCell(TreeNode parentNode, mxICell parentCell) {
    for (TreeNode child : parentNode.getChildren()) {
      mxICell childCell =
          (mxICell) graph.insertVertex(parent, null, child.getDeliveryIds(), 0, 0, 30, 30);
      graph.insertEdge(parent, null, "", parentCell, childCell);
      if (child.getChildren() != null && !child.getChildren().isEmpty()) {
        addChildCell(child, childCell);
      }
    }

  }
}
