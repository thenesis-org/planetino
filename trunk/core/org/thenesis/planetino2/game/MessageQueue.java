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
package org.thenesis.planetino2.game;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics3D.Overlay;
import org.thenesis.planetino2.math3D.ViewWindow;

public class MessageQueue implements Overlay {

	static class Message {
		String text;
		long remainingTime;
	}

	private static final long MESSAGE_TIME = 5000;
	private static final long MAX_SIZE = 10;

	private static MessageQueue instance;

	private Vector messages;
	private boolean debug;

	//private Font font;

	public static synchronized MessageQueue getInstance() {
		if (instance == null) {
			instance = new MessageQueue();
		}
		return instance;
	}

	private MessageQueue() {
		messages = new Vector();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void debug(String text) {
		if (debug) {
			add(text);
		}
	}

	public boolean isEnabled() {
		return true;
	}

	public void add(String text) {
		Message message = new Message();
		message.text = text;
		message.remainingTime = MESSAGE_TIME;
		messages.addElement(message);
		if (messages.size() > MAX_SIZE) {
			messages.removeElementAt(0);
		}
	}

	public void update(long elapsedTime) {
		Enumeration i = messages.elements();
		while (i.hasMoreElements()) {
			Message message = (Message) i.nextElement();
			message.remainingTime -= elapsedTime;
			if (message.remainingTime < 0) {
				messages.removeElement(message);
			}
		}
	}

	public void draw(Graphics g, ViewWindow window) {

		// Note: font color is set by the overlay manager

		// set the font (scaled for this view window)
		//int fontHeight = Math.max(9, window.getHeight() / 40);
		//        if (font == null || fontHeight != font.getSize()) {
		//            font = new Font("Dialog", Font.PLAIN, fontHeight);
		//        }
		//        g.setFont(font);

		//        int x = window.getLeftOffset() + window.getWidth() -
		//            fontHeight/4;

		int fontHeight = Font.getDefaultFont().getHeight();
		int x = window.getLeftOffset();
		int y = window.getTopOffset();

		g.setColor(Color.WHITE.getRGB());

		Enumeration i = messages.elements();
		while (i.hasMoreElements()) {
			String text = ((Message) i.nextElement()).text;
			//System.out.println(text);
			g.drawString(text, x, y, Graphics.TOP | Graphics.LEFT);
			y += fontHeight;
		}

		//        Iterator i = messages.iterator();
		//        while (i.hasNext()) {
		//            String text = ((Message)i.next()).text;
		//            Rectangle2D displayBounds = font.getStringBounds(text,
		//                g.getFontRenderContext());
		//            y+=(int)displayBounds.getHeight();
		//            g.drawString(text,
		//                x - (int)displayBounds.getWidth(), y);
		//        }
	}
}
