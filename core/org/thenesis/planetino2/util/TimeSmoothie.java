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
    Smoothes out the jumps in time do to poor timer accuracy.
    This is a simple algorithm that is slightly inaccurate (the
    smoothed time may be slightly ahead of real time) but gives
    better-looking results.
*/
public class TimeSmoothie {

    /**
        How often to recalc the frame rate
    */
    protected static final long FRAME_RATE_RECALC_PERIOD = 500;

    /**
        Don't allow the elapsed time between frames to be more than 100 ms
    */
    protected static final long MAX_ELAPSED_TIME = 100;

    /**
        Take the average of the last few samples during the last 100ms
    */
    protected static final long AVERAGE_PERIOD = 100;

    protected static final int NUM_SAMPLES_BITS = 6; // 64 samples
    protected static final int NUM_SAMPLES = 1 << NUM_SAMPLES_BITS;
    protected static final int NUM_SAMPLES_MASK = NUM_SAMPLES - 1;

    protected long[] samples;
    protected int numSamples = 0;
    protected int firstIndex = 0;

    // for calculating frame rate
    protected int numFrames = 0;
    protected long startTime;
    protected float frameRate;

    public TimeSmoothie() {
        samples = new long[NUM_SAMPLES];
    }


    /**
        Adds the specified time sample and returns the average
        of all the recorded time samples.
    */
    public long getTime(long elapsedTime) {
        addSample(elapsedTime);
        return getAverage();
    }


    /**
        Adds a time sample.
    */
    public void addSample(long elapsedTime) {
        numFrames++;

        // cap the time
        elapsedTime = Math.min(elapsedTime, MAX_ELAPSED_TIME);

        // add the sample to the list
        samples[(firstIndex + numSamples) & NUM_SAMPLES_MASK] =
            elapsedTime;
        if (numSamples == samples.length) {
            firstIndex = (firstIndex + 1) & NUM_SAMPLES_MASK;
        }
        else {
            numSamples++;
        }
    }


    /**
        Gets the average of the recorded time samples.
    */
    public long getAverage() {
        long sum = 0;
        for (int i=numSamples-1; i>=0; i--) {
            sum+=samples[(firstIndex + i) & NUM_SAMPLES_MASK];

            // FIXME ???
            // if the average period is already reached, go ahead and return
            // the average.
            if (sum >= AVERAGE_PERIOD) {
                Math.floor(((double)sum / (numSamples-i)) + 0.5d);
            }
        }
        return (long) Math.floor(((double)sum / numSamples) + 0.5d);
    }


    /**
        Gets the frame rate (number of calls to getTime() or
        addSample() in real time). The frame rate is recalculated
        every 500ms.
    */
    public float getFrameRate() {
        long currTime = System.currentTimeMillis();

        // calculate the frame rate every 500 milliseconds
        if (currTime > startTime + FRAME_RATE_RECALC_PERIOD) {
            frameRate = (float)numFrames * 1000 /
                (currTime - startTime);
            startTime = currTime;
            numFrames = 0;
        }

        return frameRate;
    }
}
