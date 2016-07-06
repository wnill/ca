package de.wnill.master.core.wdp.utils;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

  private static int idCounter = 0;

  private int id;

  private TreeNode parent;

  private List<Integer> deliveryIds;

  private long valuation;

  private List<TreeNode> children;

  public TreeNode(List<Integer> deliveryIds, long valuation) {
    this.deliveryIds = deliveryIds;
    this.valuation = valuation;
    id = idCounter++;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  public void addChild(TreeNode child) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(child);
    child.setParent(this);
  }

  /**
   * @return the parent
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(TreeNode parent) {
    this.parent = parent;
  }

  /**
   * @return the children
   */
  public List<TreeNode> getChildren() {
    return children;
  }

  /**
   * @return the deliveryIds
   */
  public List<Integer> getDeliveryIds() {
    return deliveryIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("TreeNode [parent=");
    if (parent == null)
      sb.append("none");
    else
      sb.append(parent.getId());
    sb.append(", deliveryIds=").append(deliveryIds).append(", valuation=").append(valuation)
        .append(", children=");
    if (children != null && !children.isEmpty()) {
      for (TreeNode child : children) {
        sb.append(child);
      }
    } else {
      sb.append("none");
    }
    sb.append("]");
    return sb.toString();
  }
}
