/*
 * Orchid components
 * and open the template in the editor.
 */
package com.xzq.osc;

import com.xzq.osc.resource.Resource;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Orchid logger class, all Orchid components use this class to record their
 * running info, developer can set Orchid components logger level or logging
 * handler by this class, etc. This class also provide some shortcut methods for
 * relaive method of Logger class.
 *
 * @author zqxu
 */
public class OrchidLogger {

  private static final Logger logger =
          Logger.getLogger("com.xzq.osc", Resource.BASE_NAME);

  public static Logger getLogger() {
    return logger;
  }

  /**
   * A shortcut for getLogger().getLevel().
   *
   * @return current logger level.
   */
  public static Level getLevel() {
    return getLogger().getLevel();
  }

  /**
   * A shortcut for getLogger().setLevel(level).
   *
   * @param level new level for logger.
   */
  public static void setLevel(Level level) {
    getLogger().setLevel(level);
  }

  /**
   * A shortcut for getLogger.isLoggable(level).<br> Check if a message of the
   * given level would actually be logged by current logger.
   *
   * @param level a message logging level
   * @return true if the given message level is currently being logged.
   */
  public static boolean isLoggable(Level level) {
    return getLogger().isLoggable(level);
  }

  /**
   * A shortcut for getLogger.addHandler(handler).<br> Add a log Handler to
   * receive logging messages.
   *
   * @param handler a logging Handler
   */
  public static void addHandler(Handler handler) {
    getLogger().addHandler(handler);
  }

  /**
   * A shortcut for getLogger().removeHandler(handler).<br> Remove a log
   * Handler.
   *
   * @param handler a logging Handler
   */
  public static void removeHandler(Handler handler) {
    getLogger().removeHandler(handler);
  }

  /**
   * A shortcut for getLogger().severe(msg).<br> Log a SEVERE message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void severe(String msg) {
    getLogger().severe(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.SEVERE, msg, thrown).<br> Log a
   * SEVERE message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void severe(String msg, Throwable thrown) {
    getLogger().log(Level.SEVERE, msg, thrown);
  }

  /**
   * A shortcut for getLogger().warning(msg).<br> Log a WARNING message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void warning(String msg) {
    getLogger().warning(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.WARNING, msg, thrown).<br> Log a
   * WARNING message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void warning(String msg, Throwable thrown) {
    getLogger().log(Level.WARNING, msg, thrown);
  }

  /**
   * A shortcut for getLogger().info(msg).<br> Log a INFO message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void info(String msg) {
    getLogger().info(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.INFO, msg, thrown).<br> Log a INFO
   * message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void info(String msg, Throwable thrown) {
    getLogger().log(Level.INFO, msg, thrown);
  }

  /**
   * A shortcut for getLogger().config(msg).<br> Log a CONFIG message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void config(String msg) {
    getLogger().config(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.CONFIG, msg, thrown).<br> Log a
   * CONFIG message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void config(String msg, Throwable thrown) {
    getLogger().log(Level.CONFIG, msg, thrown);
  }

  /**
   * A shortcut for getLogger().fine(msg).<br> Log a FINE message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void fine(String msg) {
    getLogger().fine(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.FINE, msg, thrown).<br> Log a FINE
   * message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void fine(String msg, Throwable thrown) {
    getLogger().log(Level.FINE, msg, thrown);
  }

  /**
   * A shortcut for getLogger().finer(msg).<br> Log a FINER message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void finer(String msg) {
    getLogger().finer(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.FINER, msg, thrown).<br> Log a FINER
   * message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void finer(String msg, Throwable thrown) {
    getLogger().log(Level.FINER, msg, thrown);
  }

  /**
   * A shortcut for getLogger().finest(msg).<br> Log a FINEST message.
   *
   * @param msg The string message (or a key in the message catalog)
   */
  public static void finest(String msg) {
    getLogger().finest(msg);
  }

  /**
   * A shortcut for getLogger().log(Level.FINEST, msg, thrown).<br> Log a
   * FINEST message, with associated Throwable information.
   *
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void finest(String msg, Throwable thrown) {
    getLogger().log(Level.FINEST, msg, thrown);
  }

  /**
   * A shortcut for getLogger().log(level, msg).<br> Log a message, with no
   * arguments.
   *
   * @param level One of the message level identifiers, e.g. SEVERE
   * @param msg The string message (or a key in the message catalog)
   */
  public static void log(Level level, String msg) {
    getLogger().log(level, msg);
  }

  /**
   * A shortcut for getLogger().log(level, msg, thrown).<br> Log a message,
   * with associated Throwable information.
   *
   * @param level One of the message level identifiers, e.g. SEVERE
   * @param msg The string message (or a key in the message catalog)
   * @param thrown Throwable associated with log message
   */
  public static void log(Level level, String msg, Throwable thrown) {
    getLogger().log(level, msg, thrown);
  }
}
