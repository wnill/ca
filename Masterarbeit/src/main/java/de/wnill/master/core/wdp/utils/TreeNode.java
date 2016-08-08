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
}
