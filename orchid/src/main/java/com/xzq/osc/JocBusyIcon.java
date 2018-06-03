/*
 * An icon to instruct busy state.
 * Can use as normal icon object, in JLabel, JButton, etc.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import com.xzq.osc.about.OrchidAboutIntf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.Timer;

/**
 *
 * @author zqxu
 */
public class JocBusyIcon implements Icon, OrchidAboutIntf {

  public static enum Direction {

    /**
     * cycle proceeds forward
     */
    CLOCK_WISE,
    /**
     * cycle proceeds backward
     */
    ANTI_CLOCK_WISE,
  };
  private Component painter;
  private int px, py;
  private int delay;
  private boolean busy;
  private Timer runner;
  private Timer monitor;
  private boolean painting;
  private int frame = -1;
  private int points = 9;
  private Color baseColor;
  private Color highlightColor;
  private int trailLength = 8;
  private Shape pointShape;
  private Shape trajectory;
  private Direction direction = Direction.CLOCK_WISE;
  protected PropertyChangeSupport changeSupport;

  /**
   * Constructor of JocBusyIcon with default shape height <b>19</b> and default
   * state <b>idle</b>.
   */
  public JocBusyIcon() {
    this(19, false);
  }

  /**
   * Constructor of JocBusyIcon with specified shape height and default state
   * <b>idle</b>.
   *
   * @param height shape height
   */
  public JocBusyIcon(int height) {
    this(height, false);
  }

  /**
   * Constructor of JocBusyIcon with specified state and default shape height
   * <b>19</b>.
   *
   * @param busy state for new JocBusyIcon object, true for busy and false for
   * idle.
   */
  public JocBusyIcon(boolean busy) {
    this(19, busy);
  }

  /**
   * Constructor of JocBusyIcon with specified shape height and state.
   *
   * @param height shape height
   * @param busy state for new JocBusyIcon object, true for busy and false for
   * idle.
   */
  public JocBusyIcon(int height, boolean busy) {
    this(getDefaultPoint(height), getDefaultTrajectory(height), busy);
  }

  /**
   * Constructor of JocBusyIcon with specified shape and state, auto caculate
   * size for befit contains shape.
   *
   * @param point shape for point
   * @param trajectory shape for trajectory
   * @param busy state for new JocBusyIcon object, true for busy and false for
   * idle.
   */
  public JocBusyIcon(Shape point, Shape trajectory, boolean busy) {
    changeSupport = new PropertyChangeSupport(this);
    initTimer();
    setDelay(100);
    setBusy(busy);
    init(point, trajectory, Color.LIGHT_GRAY, Color.BLUE.brighter());
  }

  /**
   * Returns default point shape according to given shape height.
   *
   * @param height shape height
   * @return point shape
   */
  protected static Shape getDefaultPoint(int height) {
    return new Ellipse2D.Float(0, 0, height / 3, 3);
  }

  /**
   * Returns default trajectory shape according to give shape height.
   *
   * @param height shape height
   * @return trajectory shape
   */
  protected static Shape getDefaultTrajectory(int height) {
    return new Ellipse2D.Float(height / 6, height / 6,
            height - height / 3, height - height / 3);
  }

  /**
   * Initialize icon use specified shape and color.
   *
   * @param point point shape
   * @param trajectory trajectory shape
   * @param baseColor base color
   * @param highlightColor hightlight color
   */
  protected void init(Shape point, Shape trajectory, Color baseColor,
          Color highlightColor) {
    this.baseColor = baseColor;
    this.highlightColor = highlightColor;
    this.pointShape = point;
    this.trajectory = trajectory;
  }

  /**
   * initialize timer
   */
  private void initTimer() {
    runner = new Timer(delay, new ActionListener() {
      private int rFrame = getPoints();

      @Override
      public void actionPerformed(ActionEvent e) {
        rFrame = (rFrame + 1) % getPoints();
        setFrame(direction == Direction.ANTI_CLOCK_WISE ? getPoints() - rFrame : rFrame);
      }
    });
    monitor = new Timer(delay * 2, new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!painting) {
          stopAnimation();
          painter = null;
        }
        painting = false;
      }
    });
  }

  /**
   * Returns delay time in millisecond between two frame, affect to flush rate
   * in busy state.
   *
   * @return delay time in millisecond
   */
  public int getDelay() {
    return delay;
  }

  /**
   * Sets delay time in millisecond between two frame.
   *
   * @param delay delay time in millisecond
   */
  public void setDelay(int delay) {
    this.delay = delay;
    runner.setDelay(delay);
    monitor.setDelay(delay * 2);
  }

  /**
   * Returns current state, true for busy and false for idle.
   *
   * @return current state.
   */
  public boolean isBusy() {
    return busy;
  }

  /**
   * Sets new state, true for busy and false for idle.
   *
   * @param busy new state.
   */
  public void setBusy(boolean busy) {
    boolean old = this.busy;
    if (old != busy) {
      this.busy = busy;
      if (busy) {
        startAnimation();
      } else {
        stopAnimation();
      }
      changeSupport.firePropertyChange("busy", old, busy);
    }
  }

  //start icon animation for busy state
  private void startAnimation() {
    if (painter != null) {
      runner.start();
      monitor.start();
    }
  }

  //stop icon animation for idle state
  private void stopAnimation() {
    runner.stop();
    monitor.stop();
    setFrame(-1);
  }

  /**
   * Returns icon width.
   *
   * @return icon width.
   */
  @Override
  public int getIconWidth() {
    return getPrefSize().width;
  }

  /**
   * Returns icon height.
   *
   * @return icon height.
   */
  @Override
  public int getIconHeight() {
    return getPrefSize().height;
  }

  //Caculate preferred size for icon
  private Dimension getPrefSize() {
    Rectangle rt = getTrajectory().getBounds();
    Rectangle rp = getPointShape().getBounds();
    int max = Math.max(rp.width, rp.height);
    return new Dimension(rt.width + max, rt.height + max);
  }

  /**
   * Only for show height property in property editor, always return 0.
   *
   * @return always 0.
   */
  public int getHeight() {
    return 0;
  }

  /**
   * For quickly set shape, auto create point and trajectory shape according to
   * given shape height.
   *
   * @param height shape height
   */
  public void setHeight(int height) {
    setPointShape(getDefaultPoint(height));
    setTrajectory(getDefaultTrajectory(height));
  }

  /**
   * Returns current frame number.
   *
   * @return current frame.
   */
  public int getFrame() {
    return frame;
  }

  /**
   * Sets current frame.
   *
   * @param frame current frame.
   */
  public void setFrame(int frame) {
    int old = getFrame();
    if (old != frame) {
      this.frame = frame;
      repaintBusyIcon();
      changeSupport.firePropertyChange("frame", old, getFrame());
    }
  }

  /**
   * Return base color.
   *
   * @return base color.
   */
  public Color getBaseColor() {
    return baseColor;
  }

  /**
   * Sets base color.
   *
   * @param baseColor base color.
   */
  public void setBaseColor(Color baseColor) {
    Color old = getBaseColor();
    this.baseColor = baseColor;
    repaintBusyIcon();
    changeSupport.firePropertyChange("baseColor", old, getBaseColor());
  }

  /**
   * Returns highlight color.
   *
   * @return highlight color.
   */
  public Color getHighlightColor() {
    return highlightColor;
  }

  /**
   * Sets highlight color.
   *
   * @param highlightColor highlight color.
   */
  public void setHighlightColor(Color highlightColor) {
    Color old = getHighlightColor();
    this.highlightColor = highlightColor;
    repaintBusyIcon();
    changeSupport.firePropertyChange("highlightColor", old, getHighlightColor());
  }

  /**
   * Returns points count at trajectory, defaults is <b>9</b>.
   *
   * @return points count
   */
  public int getPoints() {
    return points;
  }

  /**
   * Sets points count at trajectory
   *
   * @param points points count
   */
  public void setPoints(int points) {
    int old = getPoints();
    this.points = points;
    repaintBusyIcon();
    changeSupport.firePropertyChange("points", old, getPoints());
  }

  /**
   * Returns trailer points count at trajectory, defaults is <b>8</b>.
   *
   * @return trailer points count.
   */
  public int getTrailLength() {
    return trailLength;
  }

  /**
   * Sets trailer points count at trajectory.
   *
   * @param trailLength trailer points count.
   */
  public void setTrailLength(int trailLength) {
    int old = getTrailLength();
    this.trailLength = trailLength;
    repaintBusyIcon();
    changeSupport.firePropertyChange("trailLength", old, getTrailLength());
  }

  /**
   * Returns current point shape.
   *
   * @return point shape.
   */
  public Shape getPointShape() {
    return pointShape;
  }

  /**
   * Sets new point shape.
   *
   * @param pointShape point shape.
   */
  public void setPointShape(Shape pointShape) {
    Shape old = getPointShape();
    this.pointShape = pointShape;
    if (getPointShape() != old && getPointShape() != null
            && !getPointShape().equals(old)) {
      if (painter != null) {
        painter.invalidate();
        painter.repaint();
      }
      changeSupport.firePropertyChange("pointShape", old, getPointShape());
    }
  }

  /**
   * Returns current trajectory shape.
   *
   * @return Current trajectory shape.
   */
  protected Shape getTrajectory() {
    return trajectory;
  }

  /**
   * Sets new trajectory shape.
   *
   * @param trajectory trajectory shape.
   */
  protected void setTrajectory(Shape trajectory) {
    Shape old = getTrajectory();
    this.trajectory = trajectory;
    if (getTrajectory() != old && getTrajectory() != null
            && !getTrajectory().equals(old)) {
      if (painter != null) {
        painter.invalidate();
        painter.repaint();
      }
      changeSupport.firePropertyChange("trajectory", old, getTrajectory());
    }
  }

  /**
   * Returns current animation direction, defaults is
   * <b>Direction.CLOCK_WISE</b>.
   *
   * @return animation direction.
   */
  public Direction getDirection() {
    return this.direction;
  }

  /**
   * Sets new animation direction.
   *
   * @param dir animation direction.
   */
  public void setDirection(Direction dir) {
    Direction old = getDirection();
    this.direction = dir;
    if (getDirection() != old && getDirection() != null
            && !getDirection().equals(old)) {
      repaintBusyIcon();
      changeSupport.firePropertyChange("direction", old, getDirection());
    }
  }

  @Override
  public void paintIcon(Component c, Graphics g, int x, int y) {
    updateComponent(c, x, y);
    Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
              RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
              RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g2.translate(x, y);
      paintPoints(g2);
      g2.translate(-x, -y);
    } finally {
      g2.dispose();
    }
  }

  // update container
  private void updateComponent(Component component, int px, int py) {
    this.px = px;
    this.py = py;
    painting = true;
    painter = component;
    if (isBusy()) {
      startAnimation();
    }
  }

  private void paintPoints(Graphics2D g2) {
    double halfWidth = getIconWidth() / 2.0;
    double halfHeight = getIconHeight() / 2.0;
    int pointCount = getPoints();
    double stepRadian = Math.PI * 2 / pointCount;
    for (int i = 0; i < pointCount; i++) {
      g2.translate(halfWidth, halfHeight);
      g2.setColor(getPointColor(i));
      paintOnePoint(g2, i * stepRadian);
      g2.translate(-halfWidth, -halfHeight);
    }
  }

  private void paintOnePoint(Graphics2D g2, double radians) {
    Shape point = getPointShape();
    double offsety = -point.getBounds().height / 2.0;
    double offsetx = getIconWidth() / 2.0 - point.getBounds().width;
    g2.rotate(radians);
    g2.translate(offsetx, offsety);
    g2.fill(getPointShape());
    g2.translate(-offsetx, -offsety);
    g2.rotate(-radians);
  }

  private void repaintBusyIcon() {
    if (painter != null) {
      painter.repaint(px, py, getIconWidth(), getIconHeight());
    }
  }
  // caculate color for point

  private Color getPointColor(int i) {
    if (frame == -1) {
      return getBaseColor();
    }

    for (int t = 0; t < getTrailLength(); t++) {
      if (direction == JocBusyIcon.Direction.CLOCK_WISE
              && i == (frame - t + getPoints()) % getPoints()) {
        float terp = 1 - ((float) (getTrailLength() - t))
                / (float) getTrailLength();
        return calcGradientColor(
                getHighlightColor(), getBaseColor(), terp);
      } else if (direction == JocBusyIcon.Direction.ANTI_CLOCK_WISE
              && i == (frame + t) % getPoints()) {
        float terp = ((float) (t)) / (float) getTrailLength();
        return calcGradientColor(
                getHighlightColor(), getBaseColor(), terp);
      }
    }
    return getBaseColor();
  }

  private Color calcGradientColor(Color high, Color dark, float rate) {
    float[] highRGB = high.getRGBComponents(null);
    float[] darkRGB = dark.getRGBComponents(null);
    float[] retnRGB = new float[4];
    for (int i = 0; i < 4; i++) {
      retnRGB[i] = highRGB[i] + (darkRGB[i] - highRGB[i]) * rate;
    }
    return new Color(retnRGB[0], retnRGB[1], retnRGB[2], retnRGB[3]);
  }

  public void addPropertyChangeListener(PropertyChangeListener l) {
    changeSupport.addPropertyChangeListener(l);
  }

  public void removePropertyChangeListener(PropertyChangeListener l) {
    changeSupport.removePropertyChangeListener(l);
  }

  /**
   * Returns about box dialog
   *
   * @return An about box dialog
   */
  @Override
  public JDialog getAboutBox() {
    return DefaultOrchidAbout.getDefaultAboutBox(getClass());
  }

  /**
   * internal use.
   *
   * @param aboutBox about dialog
   */
  public void setAboutBox(JDialog aboutBox) {
    // no contents need.
  }
}
