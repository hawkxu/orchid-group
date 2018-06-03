/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xzq.osc.print;

import com.xzq.osc.OrchidLocale;

/**
 *
 * @author zqxu
 */
public class PaperSizeException extends RuntimeException {

  /**
   * Indicate multiple paper size in print task list.
   */
  public static final int MULTI_PAPER_SIZE = 1;
  /**
   * Indicate current paper size of printer is invalid.
   */
  public static final int PRINTER_PAPER_ERROR = 2;
  private static final String[] Err_Text = new String[]{
    OrchidLocale.getString("MULTI_PAPER_SIZE"),
    OrchidLocale.getString("INVALID_PRINTER_PAPER")
  };
  private int code;

  /**
   * Constructor for PaperSizeException, use default exception message.
   *
   * @param code exception code, MULTI_PAPER_SIZE or PRINTER_PAPER_ERROR
   */
  public PaperSizeException(int code) {
    this(code, Err_Text[code - 1]);
  }

  /**
   * Contructor for PaperSizeException
   *
   * @param code exception code, MULTI_PAPER_SIZE or PRINTER_PAPER_ERROR
   * @param message exception message
   */
  public PaperSizeException(int code, String message) {
    super(message);
    this.code = code;
  }

  /**
   * Returns exception code, MULTI_PAPER_SIZE or PRINTER_PAPER_ERROR
   *
   * @return exception code
   */
  public int getCode() {
    return code;
  }
}
