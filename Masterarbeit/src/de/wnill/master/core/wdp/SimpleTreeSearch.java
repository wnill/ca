package de.wnill.master.core.wdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.wnill.master.core.wdp.utils.TreeNode;
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


  @Override
  public Set<Bid> determineWinners(Collection<Bid> bids, Collection<Delivery> deliveries) {

    for (Delivery delivery : deliveries) {
      deliveryIds.add(delivery.getId());
    }
    Collections.sort(deliveryIds);

    searchTree = constructSearchTree(bids, deliveries);

    System.out.println(searchTree);

    return Collections.EMPTY_SET;
  }

  private TreeNode constructSearchTree(Collection<Bid> bids, Collection<Delivery> deliveries) {

    int listIndex = 0;

    // Create root node
    TreeNode tree = new TreeNode(Collections.EMPTY_LIST, 0);
    for (Bid bid : bids) {
      if (bid.getDeliveryIds().contains(deliveryIds.get(listIndex))) {
        tree.addChild(new TreeNode(bid.getDeliveryIds(), bid.getSumLateness().toMinutes()));
      }
    }

    listIndex++;

    for (TreeNode firstNode : tree.getChildren()) {
      constructChildren(bids, listIndex, firstNode);
    }


    return tree;
  }

  private void constructChildren(Collection<Bid> bids, int listIndex, TreeNode oneNode) {
    TreeNode parent = oneNode.getParent();
    HashSet<Integer> covered = new HashSet<>();
    covered.addAll(oneNode.getDeliveryIds());
    while (parent != null) {
      covered.addAll(parent.getDeliveryIds());
      parent = parent.getParent();
    }

    // Add children
    for (Bid bid : bids) {
      if (listIndex < deliveryIds.size()
          && bid.getDeliveryIds().contains(deliveryIds.get(listIndex))) {
        boolean valid = true;
        for (Integer id : bid.getDeliveryIds()) {
          if (covered.contains(id))
            valid = false;
        }

        if (valid) {
          oneNode.addChild(new TreeNode(bid.getDeliveryIds(), bid.getSumLateness().toMinutes()));
        }
      }
    }

    int newListIndex = listIndex + 1;

    if (oneNode.getChildren() != null && !oneNode.getChildren().isEmpty()) {
      for (TreeNode child : oneNode.getChildren()) {
        constructChildren(bids, newListIndex, child);
      }
    }

  }
}
