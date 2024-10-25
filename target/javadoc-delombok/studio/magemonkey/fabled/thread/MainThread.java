/**
 * Fabled
 * studio.magemonkey.fabled.thread.MainThread
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2024 MageMonkeyStudio
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package studio.magemonkey.fabled.thread;

import org.bukkit.plugin.IllegalPluginAccessException;

import java.util.ConcurrentModificationException;
import java.util.Objects;

/**
 * The main async task for Fabled functions
 */
public class MainThread extends Thread {
    private static final TaskList tasks = new TaskList();

    private long time;

    private boolean enabled;

    private boolean print = true;

    /**
     * Sets up the main thread
     */
    public MainThread() {
        time = System.currentTimeMillis();
        enabled = true;
        start();
    }

    /**
     * Runs the thread until disabled or interrupted
     */
    @Override
    public void run() {
        while (enabled) {
            try {
                tasks.iterator();
                while (tasks.hasNext()) {
                    if (tasks.next().tick()) {
                        tasks.remove();
                    }
                }

                long current = System.currentTimeMillis();
                time += 50;
                sleep(Math.max(1, time - current));
            } catch (ConcurrentModificationException ex) {
                // Concurrent exceptions would happen infrequently
                // but shouldn't be a concern. We'll just continue
                // functionality next tick.
            } catch (Exception ex) {
                if (print) {
                    ex.printStackTrace();
                    print = false;
                }
            }
        }
    }

    /**
     * Disables the main thread, stopping future runs
     */
    public void disable() {
        for (IThreadTask task : tasks) {
            try {
                task.run();
            } catch (IllegalPluginAccessException ignored) {
            }
        }
        tasks.clear();
        enabled = false;
    }

    /**
     * Registers a new task to run
     *
     * @param task task to run
     */
    public static void register(IThreadTask task) {
        Objects.requireNonNull(task, "Cannot register a null task");
        tasks.add(task);
    }
}
