package de.wnill.master.core.wdp.utils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TreeNode {

  private static int idCounter = 0;

  private int id;

  private int bidId;

  private TreeNode parent;

  private List<Integer> deliveryIds;

  private long valuation;

  private int truckId;

  private List<TreeNode> children;

  private boolean valid = true;

  private List<LocalTime> expected;

  private List<LocalTime> proposed;

  public TreeNode(int bidId, List<Integer> deliveryIds, long valuation, int truckId) {
    this.bidId = bidId;
    this.deliveryIds = deliveryIds;
    this.valuation = valuation;
    this.truckId = truckId;
    id = idCounter++;
  }

  public TreeNode(int bidId, List<Integer> deliveryIds, List<LocalTime> expected,
      List<LocalTime> proposed, int truckId) {
    this.bidId = bidId;
    this.deliveryIds = deliveryIds;
    this.expected = expected;
    this.proposed = proposed;
    this.truckId = truckId;
    id = idCounter++;
  }

  public void addChild(TreeNode child) {
    if (children == null) {
      children = new ArrayList<>();
    }
    children.add(child);
    child.setParent(this);
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @return the truckId
   */
  public int getTruckId() {
    return truckId;
  }

  /**
   * @return the bidId
   */
  public int getBidId() {
    return bidId;
  }

  /**
   * @return the parent
   */
  public TreeNode getParent() {
    return parent;
  }

  /**
   * @return the expected
   */
  public List<LocalTime> getExpected() {
    return expected;
  }

  /**
   * @return the proposed
   */
  public List<LocalTime> getProposed() {
    return proposed;
  }

  /**
   * @param parent the parent to set
   */
  public void setParent(TreeNode parent) {
    this.parent = parent;
  }

  /**
   * @return the valuation
   */
  public long getValuation() {
    return valuation;
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

  /**
   * @param valid the valid to set
   */
  public void setValid(boolean valid) {
    this.valid = valid;
  }

  /**
   * @return the valid
   */
  public boolean isValid() {
    return valid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "TreeNode [id=" + id + ", bidId=" + bidId + ", parent=" + parent + ", deliveryIds="
        + deliveryIds + ", valuation=" + valuation + ", truckId=" + truckId + ", valid=" + valid
        + ", expected=" + expected + ", proposed=" + proposed + "]";
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + bidId;
    result = prime * result + ((deliveryIds == null) ? 0 : deliveryIds.hashCode());
    result = prime * result + ((expected == null) ? 0 : expected.hashCode());
    result = prime * result + id;
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
    result = prime * result + ((proposed == null) ? 0 : proposed.hashCode());
    result = prime * result + truckId;
    result = prime * result + (valid ? 1231 : 1237);
    result = prime * result + (int) (valuation ^ (valuation >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TreeNode other = (TreeNode) obj;
    if (bidId != other.bidId)
      return false;
    if (children == null) {
      if (other.children != null)
        return false;
    } else if (!children.equals(other.children))
      return false;
    if (deliveryIds == null) {
      if (other.deliveryIds != null)
        return false;
    } else if (!deliveryIds.equals(other.deliveryIds))
      return false;
    if (expected == null) {
      if (other.expected != null)
        return false;
    } else if (!expected.equals(other.expected))
      return false;
    if (id != other.id)
      return false;
    if (parent == null) {
      if (other.parent != null)
        return false;
    } else if (!parent.equals(other.parent))
      return false;
    if (proposed == null) {
      if (other.proposed != null)
        return false;
    } else if (!proposed.equals(other.proposed))
      return false;
    if (truckId != other.truckId)
      return false;
    if (valid != other.valid)
      return false;
    if (valuation != other.valuation)
      return false;
    return true;
  }
}
