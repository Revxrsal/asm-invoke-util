[![](https://jitpack.io/v/Revxrsal/asm-invoke-util.svg)](https://jitpack.io/#Revxrsal/asm-invoke-util)
# asm-invoke-util

A small utility that provides a faster alternative to reflections for invoking methods.

# Background

Undeniably, reflections have proved to be one of the most powerful APIs introduced to Java since. However, they have
also been infamous for being expensive on memory and performance.

Although they have been massively improved through updates, as well as low-level APIs like MethodHandles, it is always
possible (and even necessary sometimes) to squeeze out less execution time and memory.

# Implementation

<details>
  <summary>Click to expand</summary>
This utility's implementation is simple: It constructs bytecode using <a href="https://asm.ow2.io">ASM</a> to generate a class
that directly invokes the method

```java
public class Dummy {

    public void doMagic(String spell) {
        System.out.println(spell + "! Woooosh");
    }
}
```

```java
/* Implementation for the caller */
public final class DummyMethodCaller0 implements MethodCaller {

    @Override public Object call(@Nullable Object instance, Object... arguments) {
        return ((Dummy) instance).doMagic((String) arguments[0]);
    }
}
```

This way, we can simply acquire an instance of `DummyMethodCaller0` to call the `doMagic` method. No reflection is
needed, performs just like direct method execution.
</details>

# Usage

### Gradle

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Revxrsal:asm-invoke-util:1.0'
}
```

### Maven

```xml

<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.Revxrsal</groupId>
    <artifactId>asm-invoke-util</artifactId>
    <version>1.0</version>
</dependency>
```

### Example

```java
public static void main(String[] args) {
    Method method = Dummy.class.getDeclaredMethod("doMagic", String.class);
    MethodCaller caller = MethodCaller.wrap(method);
    Dummy dummy = new Dummy();
    caller.call(dummy, "Magic stuff");

    /* Or */

    BoundMethodCaller caller = MethodCaller.wrap(method).bindTo(new Dummy());
    caller.call("Magic stuff");
}
```

# Final notes

Because this library effectively generates code that calls the method directly, it is impossible to use ASM to invoke
private methods. Similarly, due to some limitations in newer Java version, it is impossible to invoke package-private
ones. In such cases, the utility will automatically fallback to the MethodHandles API when ASM is not a choice.
