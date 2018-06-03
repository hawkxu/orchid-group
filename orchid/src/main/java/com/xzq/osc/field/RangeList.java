/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author zqxu
 */
public class RangeList<V> extends ArrayList<Range<V>>
        implements Serializable {

  public static <T> RangeList<T> asList(Range<T>... ranges) {
    return new RangeList<T>(Arrays.asList(ranges));
  }

  public RangeList() {
    super();
  }

  public RangeList(int initialCapacity) {
    super(initialCapacity);
  }

  public RangeList(Collection<? extends Range<V>> c) {
    super(c);
  }
}