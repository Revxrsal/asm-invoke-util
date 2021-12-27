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
package revxrsal.asm.define;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Uses an additional class loader. This does not support package-private
 * methods.
 */
final class GeneratedDefiner implements ClassDefiner {

    private static final Map<ClassLoader, GClassLoader> loaders = new HashMap<>();

    @Override
    public @NotNull Class<?> defineClass(@NotNull ClassLoader classLoader, @NotNull String name, byte[] data) {
        GClassLoader loader = loaders.computeIfAbsent(classLoader, GClassLoader::new);
        synchronized (loader.getClassLoadingLock(name)) {
            if (loader.hasClass(name)) {
                throw new IllegalArgumentException("Class " + name + " is already defined in this class loader!");
            }
            Class<?> c = loader.define(name, data);
            assert c.getName().equals(name);
            return c;
        }
    }

    private static class GClassLoader extends ClassLoader {

        private GClassLoader(ClassLoader parent) {
            super(parent);
        }

        static {
            ClassLoader.registerAsParallelCapable();
        }

        private Class<?> define(@NotNull String name, byte[] data) {
            synchronized (getClassLoadingLock(name)) {
                assert !hasClass(name);
                Class<?> c = defineClass(name, data, 0, data.length);
                resolveClass(c);
                return c;
            }
        }

        @Override
        public @NotNull Object getClassLoadingLock(@NotNull String name) {
            return super.getClassLoadingLock(name);
        }

        public boolean hasClass(@NotNull String name) {
            synchronized (getClassLoadingLock(name)) {
                try {
                    Class.forName(name);
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }
        }
    }
}
