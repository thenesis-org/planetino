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
package org.thenesis.planetino2.graphics;

import java.util.Vector;

/**
 The Animation class manages a series of images (frames) and
 the amount of time to display each frame.
 */
public class Animation {

	private Vector frames;
	private int currFrameIndex;
	private long animTime;
	private long totalDuration;

	/**
	 Creates a new, empty Animation.
	 */
	public Animation() {
		this(new Vector(), 0);
	}

	private Animation(Vector frames, long totalDuration) {
		this.frames = frames;
		this.totalDuration = totalDuration;
		start();
	}

	/**
	 Creates a duplicate of this animation. The list of frames
	 are shared between the two Animations, but each Animation
	 can be animated independently.
	 */
	public Object clone() {
		return new Animation(frames, totalDuration);
	}

	/**
	 Adds an image to the animation with the specified
	 duration (time to display the image).
	 */
	public synchronized void addFrame(Image image, long duration) {
		totalDuration += duration;
		frames.addElement(new AnimFrame(image, totalDuration));
	}

	/**
	 Starts this animation over from the beginning.
	 */
	public synchronized void start() {
		animTime = 0;
		currFrameIndex = 0;
	}

	/**
	 Updates this animation's current image (frame), if
	 neccesary.
	 */
	public synchronized void update(long elapsedTime) {
		if (frames.size() > 1) {
			animTime += elapsedTime;

			if (animTime >= totalDuration) {
				animTime = animTime % totalDuration;
				currFrameIndex = 0;
			}

			while (animTime > getFrame(currFrameIndex).endTime) {
				currFrameIndex++;
			}
		}
	}

	/**
	 Gets this Animation's current image. Returns null if this
	 animation has no images.
	 */
	public synchronized Image getImage() {
		if (frames.size() == 0) {
			return null;
		} else {
			return getFrame(currFrameIndex).image;
		}
	}

	private AnimFrame getFrame(int i) {
		return (AnimFrame) frames.elementAt(i);
	}

	private class AnimFrame {

		Image image;
		long endTime;

		public AnimFrame(Image image, long endTime) {
			this.image = image;
			this.endTime = endTime;
		}
	}
}
