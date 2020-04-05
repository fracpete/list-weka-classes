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

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.ArgumentParserException;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.simpleargparse4j.Option.Type;
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
    m_Offline       = false;
    m_LoadPackages  = false;
    m_SuperClass    = "";
    m_Logger        = null;
    m_HelpRequested = false;
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
   * Configures and returns the commandline parser.
   *
   * @return		the parser
   */
  protected ArgumentParser getParser() {
    ArgumentParser 		parser;

    parser = new ArgumentParser("Listing Weka class hierarchies.");
    parser.addOption("-o", "--offline")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("offline")
      .help("If enabled, the package manager is run in offline mode.");
    parser.addOption("-l", "--load_packages")
      .type(Type.BOOLEAN)
      .setDefault(false)
      .dest("load_packages")
      .help("If enabled, packages get loaded before determining the class hierarchies.");
    parser.addOption("-s", "--super_class")
      .metaVar("CLASSNAME")
      .type(Type.STRING)
      .setDefault("")
      .dest("super_class")
      .help("The super class to list the class names for; outputs all super classes if not supplied.");

    return parser;
  }

  /**
   * Sets the parsed options.
   *
   * @param ns		the parsed options
   * @return		if successfully set
   */
  protected boolean setOptions(Namespace ns) {
    offline(ns.getBoolean("offline"));
    loadPackages(ns.getBoolean("load_packages"));
    superClass(ns.getString("super_class"));
    return true;
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
    ArgumentParser 	parser;
    Namespace 		ns;

    m_HelpRequested = false;
    parser          = getParser();
    try {
      ns = parser.parseArgs(options);
    }
    catch (ArgumentParserException e) {
      parser.handleError(e);
      m_HelpRequested = parser.getHelpRequested();
      return m_HelpRequested;
    }

    return setOptions(ns);
  }

  /**
   * Performs the bootstrapping.
   *
   * @return		null if successful, otherwise error message
   */
  protected String doExecute() {
    List<String> 	list;

    // set up environment
    Environment.getSystemWide().addVariable(OFFLINE, "" + m_Offline);
    Environment.getSystemWide().addVariable(LOAD_PACKAGES, "" + m_LoadPackages);

    // determine classes
    GenericObjectEditor.determineClasses();

    list = new ArrayList<>();
    if (m_SuperClass.isEmpty()) {
      list.addAll(AccessiblePluginManager.getPlugins().keySet());
    }
    else {
      if (AccessiblePluginManager.getPlugins().containsKey(m_SuperClass)) {
        list.addAll(AccessiblePluginManager.getPlugins().get(m_SuperClass).keySet());
      }
      else {
        getLogger().severe("Unknown superclass: " + m_SuperClass);
      }
    }

    // print list
    Collections.sort(list);
    for (String item: list)
      System.out.println(item);

    return null;
  }

  /**
   * Performs the bootstrapping.
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
   * @throws Exception	if invalid options or failed to execute
   */
  public static void main(String[] args) throws Exception {
    Main main = new Main();

    if (!main.setOptions(args)) {
      System.err.println("Failed to parse options!");
      System.exit(1);
    }
    else if (main.getHelpRequested()) {
      System.exit(0);
    }

    String result = main.execute();
    if (result != null) {
      System.err.println("Failed to perform bootstrapping:\n" + result);
      System.exit(2);
    }
  }
}
