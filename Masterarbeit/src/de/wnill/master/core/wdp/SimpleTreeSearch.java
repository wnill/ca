package de.wnill.master.core.wdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.wnill.master.core.wdp.utils.TreeNode;
import de.wnill.master.core.wdp.viz.TreeVisualisation;
import de.wnill.master.simulator.types.Bid;
import de.wnill.master.simulator.types.Delivery;

/**
 * A Search Tree-based approach to determine winning bids as described by Sandholm, T. (1999) in:
 * Algorithm for optimal winner determination in combinatorial auctions.
 *
 */
public class SimpleTreeSearch implements WinnerDeterminationAlgorithm {

  private TreeNode searchTree;

  private ArrayList<Integer> deliveryIds = new ArrayList<>();

  private long bestValuation = Long.MAX_VALUE;

  private Set<Integer> bestBidset = new HashSet<>();

  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {

    for (Delivery delivery : deliveries) {
      deliveryIds.add(delivery.getId());
    }
    Collections.sort(deliveryIds);

    searchTree = constructSearchTree(bids, deliveries);
    System.out.println(searchTree);

    purgeInvalidNodes(searchTree);

    findOptimalBidSet(searchTree, 0);

    Set<Bid> winners = new HashSet<>();

    for (Bid bid : bids) {
      if (bestBidset.contains(bid.getId())) {
        winners.add(bid);
      }
    }

    TreeVisualisation viz = new TreeVisualisation(searchTree, bestBidset);

    return winners;
  }

  /**
   * Depth-first search of the tree for paths with best valuation.
   * 
   * @param startNode
   * @param bestValue
   * @return
   */
  private void findOptimalBidSet(TreeNode startNode, long valuationSum) {

    long valuation = valuationSum + startNode.getValuation();

    // If this is a leaf node, compute total valuation and see if this is the best path so far
    if (startNode.getChildren() == null || startNode.getChildren().isEmpty()) {
      // best solution so far, so save it
      if (valuation < bestValuation) {
        bestValuation = valuation;
        bestBidset.clear();
        bestBidset.add(startNode.getBidId());
        TreeNode parent = startNode.getParent();
        while (parent != null) {
          bestBidset.add(parent.getBidId());
          parent = parent.getParent();
        }
      }
      // Node has children, so continue the path
    } else {
      for (TreeNode child : startNode.getChildren()) {
        findOptimalBidSet(child, valuation);
      }
    }
  }

  private TreeNode constructSearchTree(Collection<Bid> bids, Collection<Delivery> deliveries) {

    int listIndex = 0;

    // Create root node
    TreeNode tree = new TreeNode(-1, Collections.EMPTY_LIST, 0, -1);
    for (Bid bid : bids) {
      if (bid.getDeliveryIds().contains(deliveryIds.get(listIndex))) {
        tree.addChild(new TreeNode(bid.getId(), bid.getDeliveryIds(), bid.getSumLateness()
            .toMinutes(), bid.getTruck().getId()));
      }
    }

    listIndex++;
    for (TreeNode firstNode : tree.getChildren()) {
      constructChildren(bids, listIndex, firstNode);
    }

    return tree;
  }

  /**
   * Populates the tree root with child nodes. For all children the following conditions hold true:
   * the child covers the next delivery id from the sorted list of deliveryIds to be covered and it
   * contains only those deliveries that have not been covered before on the given path.
   * 
   * @param bids
   * @param listIndex
   * @param oneNode
   */
  private void constructChildren(Collection<Bid> bids, int listIndex, TreeNode oneNode) {
    TreeNode parent = oneNode.getParent();
    HashSet<Integer> covered = new HashSet<>();
    covered.addAll(oneNode.getDeliveryIds());
    Set<Integer> truckIds = new HashSet<>();
    truckIds.add(oneNode.getTruckId());
    while (parent != null) {
      covered.addAll(parent.getDeliveryIds());
      truckIds.add(parent.getTruckId());
      parent = parent.getParent();
    }

    // Add children
    for (Bid bid : bids) {

      // Allow only one bid per truck in the winner set. This is only due to the bid set being
      // exhaustive!
      if (truckIds.contains(bid.getTruck().getId()))
        continue;


      while (listIndex < deliveryIds.size() && covered.contains(deliveryIds.get(listIndex))) {
        listIndex++;
      }

      if (listIndex < deliveryIds.size()
          && bid.getDeliveryIds().contains(deliveryIds.get(listIndex))) {
        boolean valid = true;

        // Child must not contain a delivery already covered
        for (Integer id : bid.getDeliveryIds()) {
          if (covered.contains(id)) {
            valid = false;
            continue;
          }
        }


        if (valid) {
          oneNode.addChild(new TreeNode(bid.getId(), bid.getDeliveryIds(), bid.getSumLateness()
              .toMinutes(), bid.getTruck().getId()));
        }
      }
    }

    int newListIndex = listIndex + 1;

    if (oneNode.getChildren() != null && !oneNode.getChildren().isEmpty()) {
      for (TreeNode child : oneNode.getChildren()) {
        constructChildren(bids, newListIndex, child);
      }
    } else {
      // This is a leaf node. Check if the path up to here is valid. If not, purge this particular
      // path.
      if (!covered.containsAll(deliveryIds)) {
        oneNode.setValid(false);
        parent = oneNode.getParent();

        while (parent != null) {
          int validCounter = 0;

          for (TreeNode child : parent.getChildren()) {
            if (child.isValid()) {
              validCounter++;
            }
          }

          if (validCounter == 0) {
            parent.setValid(false);
          }

          parent = parent.getParent();
        }
      }
    }

  }

  /**
   * Remove all nodes from a tree which are flagged as invalid.
   * 
   * @param startNode
   */
  private void purgeInvalidNodes(TreeNode startNode) {

    if (startNode == null || startNode.getChildren() == null || startNode.getChildren().isEmpty())
      return;

    Iterator<TreeNode> it = startNode.getChildren().iterator();
    while (it.hasNext()) {
      TreeNode node = it.next();
      if (!node.isValid()) {
        it.remove();
      } else {
        purgeInvalidNodes(node);
      }
    }
  }
}
