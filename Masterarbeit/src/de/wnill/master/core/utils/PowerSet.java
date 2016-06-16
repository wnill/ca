package de.wnill.master.core.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PowerSet {

  /**
   * Returns a set of all possible subsets of a given set.
   * 
   * @param originalSet
   * @return
   */
  public static Set<Set<Long>> powerSet(Set<Long> originalSet) {
    Set<Set<Long>> sets = new HashSet<Set<Long>>();
    if (originalSet.isEmpty()) {
      sets.add(new HashSet<Long>());
      return sets;
    }
    List<Long> list = new ArrayList<Long>(originalSet);
    Long head = list.get(0);
    Set<Long> rest = new HashSet<Long>(list.subList(1, list.size()));
    for (Set<Long> set : powerSet(rest)) {
      Set<Long> newSet = new HashSet<Long>();
      newSet.add(head);
      newSet.addAll(set);
      sets.add(newSet);
      sets.add(set);
    }
    return sets;
  }

}
