/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

/**
 *
 * @author zqxu
 */
public class JocLabelBeanInfo extends OrchidBaseBeanInfo {

  public JocLabelBeanInfo() {
    super(JocLabel.class);
  }

  @Override
  protected void initialize() {
    super.initialize();
    setPreferred(true, "textPlacement", "iconZoomStyle", "rotateDegrees", "antialias");
    EnumerationValue[] placValues = new EnumerationValue[]{
      new EnumerationValue("LEFT", OrchidUtils.LEFT, "com.xzq.osc.OrchidUtils.LEFT"),
      new EnumerationValue("TOP", OrchidUtils.TOP, "com.xzq.osc.OrchidUtils.TOP"),
      new EnumerationValue("RIGHT", OrchidUtils.RIGHT, "com.xzq.osc.OrchidUtils.RIGHT"),
      new EnumerationValue("BOTTOM", OrchidUtils.BOTTOM, "com.xzq.osc.OrchidUtils.BOTTOM")
    };
    setEnumerationValues(placValues, "textPlacement");
    EnumerationValue[] hPosValues = new EnumerationValue[]{
      new EnumerationValue("LEFT", OrchidUtils.LEFT, "com.xzq.osc.OrchidUtils.LEFT"),
      new EnumerationValue("CENTER", OrchidUtils.CENTER, "com.xzq.osc.OrchidUtils.CENTER"),
      new EnumerationValue("RIGHT", OrchidUtils.RIGHT, "com.xzq.osc.OrchidUtils.RIGHT"),
      new EnumerationValue("LEADING", OrchidUtils.LEADING, "com.xzq.osc.OrchidUtils.LEADING"),
      new EnumerationValue("TRAILING", OrchidUtils.TRAILING, "com.xzq.osc.OrchidUtils.TRAILING")};
    setEnumerationValues(hPosValues, "horizontalIconPosition");
    EnumerationValue[] vPosValues = new EnumerationValue[]{
      new EnumerationValue("TOP", OrchidUtils.TOP, "com.xzq.osc.OrchidUtils.TOP"),
      new EnumerationValue("CENTER", OrchidUtils.CENTER, "com.xzq.osc.OrchidUtils.CENTER"),
      new EnumerationValue("BOTTOM", OrchidUtils.BOTTOM, "com.xzq.osc.OrchidUtils.BOTTOM"),};
    setEnumerationValues(vPosValues, "verticalIconPosition");
    EnumerationValue[] zoomValues = new EnumerationValue[]{
      new EnumerationValue("ZOOM_ORIGINAL", JocLabel.ZOOM_ORIGINAL, "com.xzq.osc.JocLabel.ZOOM_ORIGINAL"),
      new EnumerationValue("ZOOM_IN", JocLabel.ZOOM_IN, "com.xzq.osc.JocLabel.ZOOM_IN"),
      new EnumerationValue("ZOOM_OUT", JocLabel.ZOOM_OUT, "com.xzq.osc.JocLabel.ZOOM_OUT"),
      new EnumerationValue("ZOOM_FIT", JocLabel.ZOOM_FIT, "com.xzq.osc.JocLabel.ZOOM_FIT"),
      new EnumerationValue("ZOOM_STRETCH", JocLabel.ZOOM_STRETCH, "com.xzq.osc.JocLabel.ZOOM_STRETCH"),
      new EnumerationValue("ZOOM_RATE", JocLabel.ZOOM_RATE, "com.xzq.osc.JocLabel.ZOOM_RATE"),
      new EnumerationValue("ZOOM_SIZE", JocLabel.ZOOM_SIZE, "com.xzq.osc.JocLabel.ZOOM_SIZE"),};
    setEnumerationValues(zoomValues, "iconZoomStyle");
  }
}
