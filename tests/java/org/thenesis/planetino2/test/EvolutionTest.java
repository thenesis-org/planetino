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
package org.thenesis.planetino2.test;

import java.util.Enumeration;
import java.util.Vector;

import org.thenesis.planetino2.ai.EvolutionBot;
import org.thenesis.planetino2.ai.EvolutionGenePool;
import org.thenesis.planetino2.engine.shooter3D.HeadsUpDisplay;
import org.thenesis.planetino2.engine.shooter3D.MessageQueue;
import org.thenesis.planetino2.engine.shooter3D.Player;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.math3D.PolygonGroup;

public class EvolutionTest extends PathFindingTest {

	private EvolutionGenePool genePool;

	//    public static void main(String[] args) {
	//        new EvolutionTest(args, "../res/sample3.map").run();
	//    }
	//
	//    public EvolutionTest(String[] args, String defaultMap) {
	//        super(args, defaultMap);
	//    }

	public EvolutionTest(Screen screen, InputManager inputManager) {
		super(screen, inputManager, "sample3.map");
		this.inputManager = inputManager;
	}

	public void stop() {
		super.stop();

		// print information about the "brains" in the gene pool.
		System.out.println(genePool);
	}

	protected void createGameObjects(Vector mapObjects) {

		drawInstructions = false;
		MessageQueue queue = MessageQueue.getInstance();
		addOverlay(queue);
		addOverlay(new HeadsUpDisplay((Player) gameObjectManager.getPlayer()));
		queue.setDebug(false);
		queue.add("Use the mouse/arrow keys to move.");
		queue.add("Press Esc to exit.");

		genePool = new EvolutionGenePool(bspTree);

		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
			if (filename != null && filename.endsWith("bot.obj3d")) {

				EvolutionBot bot = new EvolutionBot(group, collisionDetection, genePool, botProjectileModel);
				bot.setRegenerating(true);
				gameObjectManager.add(bot);
			} else {
				// static object
				gameObjectManager.add(new GameObject(group));
			}
		}
	}

}
