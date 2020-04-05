# list-weka-classes
Helper library for outputting Weka superclasses or all available Weka subclasses 
of a specified superclass on the current classpath, with or without packages loaded.

## Command-line

```
Listing Weka class hierarchies.


Usage: [--help] [-o] [-l] [-s CLASSNAME]

Options:
-o, --offline
	If enabled, the package manager is run in offline mode.

-l, --load_packages
	If enabled, packages get loaded before determining the class
	hierarchies.

-s, --super_class CLASSNAME
	The super class to list the class names for; outputs all super classes
	if not supplied.
```

## Java

```java
import com.github.fracpete.lwc.Main;

public class TestListing {
  public static void main(String[] args) throws Exception {
    // determine all superclasses
    Main main = new Main()
      .loadPackages(true)
      .offline(false);
    String msg = main.execute();
    if (msg != null)
      throw new Exception(msg);
    if (main.getList().isEmpty())
      throw new Exception("No superclasses found!");
    // list subclasses of 1st superclass
    main.superClass(main.getList().get(0));
    System.out.println(main.getList().get(0) + ":");
    msg = main.execute();
    if (msg != null)
      throw new Exception(msg);
    if (main.getList().isEmpty())
      throw new Exception("No subclasses found!");
    for (String cls: main.getList())
      System.out.println("- " + cls);
  }
}
```

## Maven

Add the following artifact to your dependencies of your `pom.xml`:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>list-weka-classes</artifactId>
      <version>0.0.1</version>
    </dependency>
```

## Releases

The following releases are available:

* [0.0.1](https://github.com/fracpete/list-weka-classes/releases/download/list-weka-classes-0.0.1/list-weka-classes-0.0.1.jar)
