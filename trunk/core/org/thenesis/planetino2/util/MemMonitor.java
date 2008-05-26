/*
 * Planetino - Copyright (C) 2007-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */

/* Copyright (c) 2003, David Brackeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *   - Redistributions of source code must retain the above copyright notice, 
 *     this list of conditions and the following disclaimer.
 *   - Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *   - Neither the name of David Brackeen nor the names of its contributors may be
 *     used to endorse or promote products derived from this software without 
 *     specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.planetino2.util;

/**
    Monitors heap size, allocation size, change in allocation,
    change in heap size, and detects garbage collection (when the
    allocation size decreases). Call takeSample() to take a
    "sample" of the currently memory state.
*/
public class MemMonitor {

    /**
        The Data class contains info on a series of float values.
        The min, max, sum and count of the data can be retrieved.
    */
    public static class Data {

        float lastValue = 0;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float sum = 0;
        int count = 0;

        public void addValue(float value) {
            lastValue = value;
            sum+=value;
            count++;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }


        public String toString() {
            return "Min: " + toByteFormat(min) + "  " +
                   "Max: " + toByteFormat(max) + "  " +
                   "Avg: " + toByteFormat(sum / count);
        }
    }

    private Data heapSize = new Data();
    private Data allocSize = new Data();
    private Data allocIncPerUpdate = new Data();
    private int numHeapIncs = 0;
    private long startTime = System.currentTimeMillis();


    /**
        Takes a sample of the current memory state.
    */
    public void takeSample() {
        Runtime runtime = Runtime.getRuntime();
        long currHeapSize = runtime.totalMemory();
        long currAllocSize = currHeapSize - runtime.freeMemory();

        if (currHeapSize > heapSize.lastValue) {
            numHeapIncs++;
        }
        if (currAllocSize >= allocSize.lastValue) {
            allocIncPerUpdate.addValue(
                (currAllocSize - allocSize.lastValue));
        }

        heapSize.addValue(currHeapSize);
        allocSize.addValue(currAllocSize);
    }


    /**
        Convert number of bytes to string representing bytes,
        kilobytes, megabytes, etc.
    */
    public static String toByteFormat(float numBytes) {
        String[] labels = {" bytes", " KB", " MB", " GB"};
        int labelIndex = 0;

        // decide most appropriate label
        while (labelIndex < labels.length - 1 && numBytes > 1024) {
            numBytes/=1024;
            labelIndex++;
        }
        return (Math.floor(numBytes*10 + 0.5d)/10f) + labels[labelIndex];
    }


    public String toString() {
        long time = System.currentTimeMillis() - startTime;
        float timeSecs = (float)time / 1000;
        return "Total Time: " + timeSecs + "s\n" +
            "Heap: " + heapSize + "\n" +
            "Allocation: " + allocSize + "\n" +
            "Allocation inc/update: " + allocIncPerUpdate + "\n" +
            "Num Heap Incs: " + numHeapIncs;
    }
}
