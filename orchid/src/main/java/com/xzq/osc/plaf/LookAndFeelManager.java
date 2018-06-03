/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.plaf;

import com.xzq.osc.plaf.basic.BasicOrchidDefaults;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author zqxu
 */
public class LookAndFeelManager {

  private static final String ORCHID_UI_INSTALLED = "osc.Installed";
  private static final String METAL_LAF_CLASS =
          "javax.swing.plaf.metal.MetalLookAndFeel";
  private static final String SYNTH_LAF_CLASS =
          "javax.swing.plaf.synth.SynthLookAndFeel";
  private static final String MOTIF_LAF_CLASS =
          "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
  private static final String NIMBUS_LAF_CLASS =
          "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
  private static final String WINDOWS_LAF_CLASS =
          "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

  /**
   * install Orchid UI defaults to current LookAndFeel
   */
  public static void installOrchidUI() {
    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
    if ("installed".equals(defaults.get(ORCHID_UI_INSTALLED))) {
      return;
    }
    defaults.put(ORCHID_UI_INSTALLED, "installed");
    UIDefaultsInitializer initializer = getDefaultsInitializer();
    if (initializer != null) {
      initializer.initialize(defaults);
    }
    UIDefaultsInitializer lfInitializer = getLookAndFeelInitializer();
    if (lfInitializer != null) {
      lfInitializer.initialize(defaults);
    }
  }

  private static UIDefaultsInitializer getDefaultsInitializer() {
    return new BasicUIDefaultsInitializer();
  }

  private static UIDefaultsInitializer getLookAndFeelInitializer() {
    //String lookAndFeel = UIManager.getLookAndFeel().getClass().getName();
    return null;
  }

  /**
   *
   * @return
   */
  public static boolean isMetalLookAndFeel() {
    return isLookAndFeel(METAL_LAF_CLASS);
  }

  /**
   *
   * @return
   */
  public static boolean isSynthLookAndFeel() {
    return isLookAndFeel(SYNTH_LAF_CLASS);
  }

  /**
   *
   * @return
   */
  public static boolean isMotifLookAndFeel() {
    return isLookAndFeel(MOTIF_LAF_CLASS);
  }

  /**
   *
   * @return
   */
  public static boolean isNimbusLookAndFeel() {
    return isLookAndFeel(NIMBUS_LAF_CLASS);
  }

  /**
   *
   * @return
   */
  public static boolean isWindowsLookAndFeel() {
    return isLookAndFeel(WINDOWS_LAF_CLASS);
  }

  /**
   *
   * @param lookAndFeel
   * @return
   */
  public static boolean isLookAndFeel(String lookAndFeel) {
    return UIManager.getLookAndFeel().getClass().getName().equals(lookAndFeel);
  }

  private static interface UIDefaultsInitializer {

    void initialize(UIDefaults uiDefaults);
  }

  private static class BasicUIDefaultsInitializer
          implements UIDefaultsInitializer {

    private UIDefaults uiDefaults;

    private void installUIClass(String uiClassID) {
      String packageName = "com.xzq.osc.plaf.basic.Basic";
      uiDefaults.put(uiClassID, packageName + uiClassID);
    }

    @Override
    public void initialize(UIDefaults uiDefaults) {
      this.uiDefaults = uiDefaults;
      installUIClass("OrchidFolderChooserUI");
      installUIClass("OrchidGregCalendarPaneUI");
      installUIClass("OrchidGroupPaneUI");
      installUIClass("OrchidHyperLinkUI");
      installUIClass("OrchidLabelUI");
      installUIClass("OrchidLabelEditorUI");
      installUIClass("OrchidTabbedPaneUI");
      installUIClass("OrchidTableUI");
      installUIClass("OrchidCheckedTreeUI");
      installUIClass("OrchidRangeFieldUI");
      BasicOrchidDefaults.install(uiDefaults);
    }
  }
}
