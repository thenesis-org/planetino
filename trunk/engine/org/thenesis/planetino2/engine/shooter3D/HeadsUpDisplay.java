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
package org.thenesis.planetino2.engine.shooter3D;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics3D.Overlay;
import org.thenesis.planetino2.math3D.ViewWindow;

public class HeadsUpDisplay implements Overlay {

	// increase health display by 20 points per second
	private static final float DISPLAY_INC_RATE = 0.04f;

	private Player player;
	private float displayedHealth;
	private Font font;

	public HeadsUpDisplay(Player player) {
		this.player = player;
		displayedHealth = 0;
	}

	public void update(long elapsedTime) {

		//System.out.println("[DEBUG] HeadsUpDisplay.update(): " + player.getHealth());

		// increase or descrease displayedHealth a small amount
		// at a time, instead of just setting it to the player's
		// health.
		float actualHealth = player.getHealth();
		if (actualHealth > displayedHealth) {
			displayedHealth = Math.min(actualHealth, displayedHealth + elapsedTime * DISPLAY_INC_RATE);
		} else if (actualHealth < displayedHealth) {
			displayedHealth = Math.max(actualHealth, displayedHealth - elapsedTime * DISPLAY_INC_RATE);
		}
	}

	public void draw(Graphics g, ViewWindow window) {

		Font font = Font.getDefaultFont();
		int fontHeight = font.getHeight();
		int spacing = fontHeight / 5;

		// draw health value (number)
		String str = Integer.toString((int) Math.floor(displayedHealth + 0.5d));
		g.setColor(Color.WHITE.getRGB());
		g.drawString(str, 0, 0, Graphics.TOP | Graphics.LEFT);

		// draw health bar
		int x = font.charsWidth(str.toCharArray(), 0, str.length()) + spacing * 2;
		int y = fontHeight / 2;
		int w = window.getWidth() / 4;
		int h = window.getHeight() / 60;
		g.setColor(Color.GRAY.getRGB());
		g.fillRect(x, y, w, h);

		//draw highlighted part of health bar
		w = (int) Math.floor((w * displayedHealth / player.getMaxHealth()) + 0.5d);
		g.setColor(Color.WHITE.getRGB());
		g.fillRect(x, y, w, h);

		//        // set the font (scaled for this view window)
		//        int fontHeight = Math.max(9, window.getHeight() / 20);
		//        int spacing = fontHeight / 5;
		//        if (font == null || fontHeight != font.getSize()) {
		//            font = new Font("Dialog", Font.PLAIN, fontHeight);
		//        }
		//        g.setFont(font);
		//        g.translate(window.getLeftOffset(), window.getTopOffset());
		//
		//        // draw health value (number)
		//        String str = Integer.toString((int)Math.floor(displayedHealth + 0.5d));
		//        Rectangle2D strBounds = font.getStringBounds(str,
		//            g.getFontRenderContext());
		//        g.setColor(Color.WHITE);
		//        g.drawString(str, spacing, (int)strBounds.getHeight());
		//
		//        // draw health bar
		//        Rectangle bar = new Rectangle(
		//            (int)strBounds.getWidth() + spacing * 2,
		//            (int)strBounds.getHeight() / 2,
		//            window.getWidth() / 4,
		//            window.getHeight() / 60);
		//        g.setColor(Color.GRAY);
		//        g.fill(bar);
		//
		//        // draw highlighted part of health bar
		//        bar.width = (int)Math.floor((bar.width *
		//            displayedHealth / player.getMaxHealth()) + 0.5d);
		//        g.setColor(Color.WHITE);
		//        g.fill(bar);
	}

	public boolean isEnabled() {
		return (player != null && (player.isAlive() || displayedHealth > 0));
	}
}
