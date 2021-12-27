/*
 * This file is part of asm-invoke-util, licensed under the MIT License.
 *
 *  Copyright (c) Revxrsal <reflxction.github@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package revxrsal.asm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * Represents the entrypoint to method callers. This interface is
 * implemented by the generated classes to invoke targeted methods.
 * <p>
 * To generate, use {@link MethodCaller#wrap(Method)} or {@link MethodCaller#wrap(Method, ClassLoader)}.
 * <p>
 * Implementation note: Generating method callers using ASM to invoke private
 * methods is not possible. Therefore, if a private method is requested, the
 * method handles API will be used to generate method callers.
 */
@FunctionalInterface
public interface MethodCaller {

    /**
     * Invokes the method
     *
     * @param instance  Instance to invoke the method on. Can be null for
     *                  static methods.
     * @param arguments Arguments to invoke the method with
     * @return The returned value of the method. Will return null for void
     * methods
     */
    Object call(@Nullable Object instance, Object... arguments);

    /**
     * Binds this method caller to the given instance, and returns
     * a {@link BoundMethodCaller} that does not need an instance
     * to be provided for calls.
     * <p>
     * For static methods, instance can be {@code null}.
     *
     * @param instance Instance to bind to. Can be null for static
     *                 methods.
     * @return The bound method caller
     */
    default @NotNull BoundMethodCaller bindTo(@Nullable Object instance) {
        return (arguments) -> call(instance, arguments);
    }

    /**
     * Creates a {@link MethodCaller} that wraps the given method
     *
     * @param method Method to wrap
     * @return The method caller responsible for invoking
     * the method
     */
    static MethodCaller wrap(@NotNull Method method) {
        return MethodCallerFactory.createFor(method);
    }

    /**
     * Creates a {@link MethodCaller} that wraps the given method,
     * and defines the generated method caller class on the given classloader
     *
     * @param method Method to wrap
     * @param loader Classloader to define the generated method on.
     * @return The method caller responsible for invoking
     * the method
     */
    static MethodCaller wrap(@NotNull Method method, @NotNull ClassLoader loader) {
        return MethodCallerFactory.createFor(method, loader);
    }
}