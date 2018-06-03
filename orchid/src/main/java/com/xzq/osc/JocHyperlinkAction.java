/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.about.DefaultOrchidAbout;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import javax.swing.JDialog;

/**
 * Action for open URI with Desktop for Desktop.Action.MAIL and
 * Desktop.Action.BROWSE
 *
 * @author zqxu
 */
public class JocHyperlinkAction extends JocAction {

  /**
   * Action property key for link target.
   */
  public static final String LINK_TARGET = "linkTarget";
  public static final String LINK_VISITED = "visited";

  /**
   * Constructor for JocHyperlinkAction, without target and name.
   */
  public JocHyperlinkAction() {
    super();
  }

  /**
   * Constructor for JocHyperlinkAction with link target uri.
   *
   * @param linkTarget link target uri.
   */
  public JocHyperlinkAction(URI linkTarget) {
    super();
    setLinkTarget(linkTarget);
  }

  /**
   * Constructor for JocHyperlinkAction with link target uri and name.
   *
   * @param linkTarget link target uri.
   * @param name name.
   */
  public JocHyperlinkAction(URI linkTarget, String name) {
    this(linkTarget);
    putValue(NAME, name);
  }

  /**
   * Returns value of property JocHyperlinkAction.LINK_TARGET.
   *
   * @return link target uri.
   */
  public URI getLinkTarget() {
    Object uri = getValue(LINK_TARGET);
    return uri instanceof URI ? (URI) uri : null;
  }

  /**
   * Set value of property JocHyperlinkAction.LINK_TARGET.
   *
   * @param linkTarget link target uri.
   */
  public void setLinkTarget(URI linkTarget) {
    putValue(LINK_TARGET, linkTarget);
  }

  /**
   * Returns value of property JocHyperlinkAction.LINK_VISITED.
   *
   * @return link visit state.
   */
  public boolean isVisited() {
    return Boolean.TRUE.equals(getValue(LINK_VISITED));
  }

  /**
   * Set value of property JocHyperlinkAction.LINK_VISITED.
   *
   * @param visited link visit state.
   */
  public void setVisited(boolean visited) {
    putValue(LINK_VISITED, visited);
  }

  private boolean isMailURI(URI target) {
    return target != null && "mailto".equalsIgnoreCase(target.getScheme());
  }

  /**
   * Returns true if target accesible, that is target must not be null and
   * platform support Desktop actions.
   *
   * @return true or false
   */
  public boolean isTargetAccessible() {
    URI target = getLinkTarget();
    Action dskAct = isMailURI(target) ? Action.MAIL : Action.BROWSE;
    return target != null && Desktop.isDesktopSupported()
            && Desktop.getDesktop().isSupported(dskAct);
  }

  /**
   * open target uri when action perform.
   *
   * @param e ignored.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    URI target = getLinkTarget();
    try {
      if (isMailURI(target)) {
        Desktop.getDesktop().mail(target);
      } else {
        Desktop.getDesktop().browse(target);
      }
      setVisited(true);
    } catch (IOException ex) {
      OrchidLogger.info("Can not visit target " + target, ex);
    }
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
}