# list-weka-classes
Helper library for outputting superclasses or all available classes for a
specified superclass on the current classpath, with or without packages.

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

* [0.0.1](https://github.com/fracpete/list-weka-classes/releases/download/list-weka-classes-0.0.1/list-weka-classes-0.0.1-spring-boot.jar)
