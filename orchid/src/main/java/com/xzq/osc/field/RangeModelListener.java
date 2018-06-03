/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.field;

import java.util.EventListener;

public interface RangeModelListener extends EventListener {

  public void wholeRangesChange(RangeModelEvent evt);
}