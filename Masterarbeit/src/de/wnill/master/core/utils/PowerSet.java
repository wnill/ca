package de.wnill.master.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.wnill.master.simulator.types.Delivery;

public class PowerSet {

  /**
   * Returns a set of all possible subsets of a given set.
   * 
   * @param originalSet
   * @return
   */
  public static Set<Set<Delivery>> powerSet(Set<Delivery> originalSet) {
    Set<Set<Delivery>> sets = new HashSet<Set<Delivery>>();
    if (originalSet.isEmpty()) {
      sets.add(new HashSet<Delivery>());
      return sets;
    }
    List<Delivery> list = new ArrayList<>(originalSet);
    Delivery head = list.get(0);
    Set<Delivery> rest = new HashSet<>(list.subList(1, list.size()));
    for (Set<Delivery> set : powerSet(rest)) {
      Set<Delivery> newSet = new HashSet<>();
      newSet.add(head);
      newSet.addAll(set);
      sets.add(newSet);
      sets.add(set);
    }
    return sets;
  }
}
