/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Main.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.lwc;

import weka.core.Environment;
import weka.core.PluginManager;
import weka.gui.GenericObjectEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * For outputting Weka class hierarchies.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Main {

  /** whether to run the package manager in offline mode. */
  public final static String OFFLINE = "weka.packageManager.offline";

  /** whether to load packages. */
  public final static String LOAD_PACKAGES = "weka.packageManager.loadPackages";

  /**
   * For getting access to protected members in the package manager.
   */
  public static class AccessiblePluginManager
    extends PluginManager {

    /**
     * Returns the plugins.
     *
     * @return		the plugins
     */
    public static Map<String, Map<String, String>> getPlugins() {
      return PLUGINS;
    }

    /**
     * Returns the resources.
     *
     * @return		the resources
     */
    public static Map<String, Map<String, String>> getResources() {
      return RESOURCES;
    }

    /**
     * Returns the disabled plugins.
     *
     * @return		the disabled plugins
     */
    public static Set<String> getDisabled() {
      return DISABLED;
    }
  }

  /** whether to run in quiet mode. */
  protected boolean m_Quiet;

  /** whether to run package manager in offline mode. */
  protected boolean m_Offline;

  /** whether to load packages. */
  protected boolean m_LoadPackages;

  /** the superclass to list the classes for. */
  protected String m_SuperClass;

  /** for logging. */
  protected Logger m_Logger;

  /** whether help got requested. */
  protected boolean m_HelpRequested;

  /** the list of classes (either superclasses or subclasses). */
  protected List<String> m_List;

  /**
   * Default constructor.
   */
  public Main() {
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Quiet         = true;
    m_Offline       = false;
    m_Quiet         = true;
    m_LoadPackages  = false;
    m_SuperClass    = "";
    m_Logger        = null;
    m_HelpRequested = false;
    m_List          = new ArrayList<>();
  }

  /**
   * Returns the logger instance to use.
   *
   * @return		the logger
   */
  protected Logger getLogger() {
    if (m_Logger == null)
      m_Logger = Logger.getLogger(getClass().getName());
    return m_Logger;
  }

  /**
   * Sets whether to run in quiet mode.
   *
   * @param value	true if quiet
   * @return		itself
   */
  protected Main quiet(boolean value) {
    m_Quiet = value;
    return this;
  }

  /**
   * Returns whether to run in quiet mode.
   *
   * @return		true if quiet
   */
  protected boolean getQuiet() {
    return m_Quiet;
  }

  /**
   * Sets whether to run the package manager in offline mode.
   *
   * @param value	true if offline
   * @return		itself
   */
  public Main offline(boolean value) {
    m_Offline = value;
    return this;
  }

  /**
   * Returns whether to run the package manager in offline mode.
   *
   * @return		true if offline
   */
  public boolean getOffline() {
    return m_Offline;
  }

  /**
   * Sets whether to load packages.
   *
   * @param value	true if to load
   * @return		itself
   */
  public Main loadPackages(boolean value) {
    m_LoadPackages = value;
    return this;
  }

  /**
   * Returns whether to load packages.
   *
   * @return		true if to load
   */
  public boolean getLoadPackages() {
    return m_LoadPackages;
  }

  /**
   * Sets the superclass to list classes for.
   *
   * @param value	the superclass classname, can be null or empty for listing them
   * @return		itself
   */
  public Main superClass(String value) {
    if (value == null)
      value = "";
    m_SuperClass = value;
    return this;
  }

  /**
   * Returns the superclass to list classes for.
   *
   * @return		the superclass, empty string for listing them
   */
  public String getSuperClass() {
    return m_SuperClass;
  }

  /**
   * Returns the list of classes.
   *
   * @return		the superclasses or subclasses
   */
  public List<String> getList() {
    return m_List;
  }

  /**
   * Returns whether help got requested when setting the options.
   *
   * @return		true if help got requested
   */
  public boolean getHelpRequested() {
    return m_HelpRequested;
  }

  /**
   * Parses the options and configures the object.
   *
   * @param options	the command-line options
   * @return		true if successfully set (or help requested)
   */
  public boolean setOptions(String[] options) {
    boolean	result;
    int		i;

    result          = true;
    m_HelpRequested = false;

    for (i = 0; i < options.length; i++) {
      if (options[i].equals("--help") || options[i].equals("-h")) {
        m_HelpRequested = true;
        break;
      }
      else if (options[i].equals("--offline") || options[i].equals("-o")) {
        offline(true);
      }
      else if (options[i].equals("--load_packages") || options[i].equals("-l")) {
        loadPackages(true);
      }
      else if (options[i].equals("--super_class") || options[i].equals("-s")) {
        if (i < options.length - 1) {
          i++;
          superClass(options[i]);
	}
      }
      else if (!options[i].isEmpty()){
        getLogger().warning("Unexpected argument: " + options[i]);
        result = false;
        break;
      }
    }

    if (m_HelpRequested || !result) {
      if (m_HelpRequested) {
	System.out.println("Help requested");
	System.out.println();
      }
      System.out.println("Listing Weka class hierarchies.");
      System.out.println();
      System.out.println("Usage: [--help] [-o] [-l] [-s CLASSNAME]");
      System.out.println();
      System.out.println("Options:");
      System.out.println("-o, --offline");
      System.out.println("	If enabled, the package manager is run in offline mode.");
      System.out.println();
      System.out.println("-l, --load_packages");
      System.out.println("	If enabled, packages get loaded before determining the class");
      System.out.println("	hierarchies.");
      System.out.println();
      System.out.println("-s, --super_class CLASSNAME");
      System.out.println("	The super class to list the class names for; outputs all super classes");
      System.out.println("	if not supplied.");
      System.out.println();
    }

    return result;
  }

  /**
   * Performs the bootstrapping.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    // set up environment
    Environment.getSystemWide().addVariable(OFFLINE, "" + m_Offline);
    Environment.getSystemWide().addVariable(LOAD_PACKAGES, "" + m_LoadPackages);

    // determine classes
    GenericObjectEditor.determineClasses();

    m_List.clear();
    if (m_SuperClass.isEmpty()) {
      m_List.addAll(AccessiblePluginManager.getPlugins().keySet());
    }
    else {
      if (AccessiblePluginManager.getPlugins().containsKey(m_SuperClass)) {
        m_List.addAll(AccessiblePluginManager.getPlugins().get(m_SuperClass).keySet());
      }
      else {
        getLogger().severe("Unknown superclass: " + m_SuperClass);
        return "Unknown superclass: " + m_SuperClass;
      }
    }

    // print list
    Collections.sort(m_List);
    if (!m_Quiet) {
      for (String item : m_List)
	System.out.println(item);
    }

    return null;
  }

  /**
   * Performs the listing.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String		result;

    result = doExecute();
    if (result != null)
      getLogger().severe(result);

    return result;
  }

  /**
   * Launches the class from the commandline.
   *
   * @param args	the options to parse
   */
  public static void main(String[] args) {
    Main main = new Main();
    main.quiet(false);

    if (!main.setOptions(args)) {
      System.err.println("Failed to parse options!");
      System.exit(1);
    }
    else if (main.getHelpRequested()) {
      System.exit(0);
    }

    String result = main.execute();
    if (result != null) {
      System.err.println("Failed to perform class listing:\n" + result);
      System.exit(2);
    }
  }
}
