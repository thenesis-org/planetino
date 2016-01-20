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

import org.thenesis.planetino2.util.Vector;
import org.thenesis.planetino2.ai.AIBot;
import org.thenesis.planetino2.ai.Brain;
import org.thenesis.planetino2.ai.pattern.AimPattern;
import org.thenesis.planetino2.ai.pattern.AttackPatternRush;
import org.thenesis.planetino2.ai.pattern.AttackPatternStrafe;
import org.thenesis.planetino2.ai.pattern.DodgePatternRandom;
import org.thenesis.planetino2.ai.pattern.DodgePatternZigZag;
import org.thenesis.planetino2.ai.pattern.RunAwayPattern;
import org.thenesis.planetino2.engine.shooter3D.Bot;
import org.thenesis.planetino2.engine.shooter3D.HeadsUpDisplay;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.game.MessageQueue;
import org.thenesis.planetino2.game.Player;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.input.InputManager;
import org.thenesis.planetino2.loader.ResourceLoader;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.path.AStarSearchWithBSP;

public class AIBotTest extends PathFindingTest {

	private Brain averageBrain;
	private Brain aggressiveBrain;
	private Brain scaredBrain;

	//    public static void main(String[] args) {
	//        new AIBotTest(args, "../res/sample3.map").run();
	//    }
	//
	//    public AIBotTest(String[] args, String defaultMap) {
	//        super(args, defaultMap);
	//    }

	public AIBotTest(Screen screen, InputManager inputManager, ResourceLoader resourceLoader) {
		super(screen, inputManager, resourceLoader, "sample3.map");
		this.inputManager = inputManager;
	}

	protected void createBrains() {

		averageBrain = new Brain();
		averageBrain.attackPathFinder = new AttackPatternRush(bspTree);
		averageBrain.aimPathFinder = new AimPattern(bspTree);
		averageBrain.dodgePathFinder = new DodgePatternRandom(bspTree);
		averageBrain.idlePathFinder = null;
		averageBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		averageBrain.runAwayPathFinder = new RunAwayPattern(bspTree);

		averageBrain.attackProbability = 0.50f;
		averageBrain.dodgeProbability = 0.40f;
		averageBrain.runAwayProbability = 0.10f;

		averageBrain.decisionTime = 4000;
		averageBrain.aimTime = 1000;
		averageBrain.hearDistance = 1000;

		// aggresive brain
		aggressiveBrain = new Brain();
		aggressiveBrain.attackPathFinder = new AttackPatternStrafe(bspTree);
		aggressiveBrain.aimPathFinder = new AimPattern(bspTree);
		aggressiveBrain.dodgePathFinder = new DodgePatternZigZag(bspTree);
		aggressiveBrain.idlePathFinder = null;
		aggressiveBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		aggressiveBrain.runAwayPathFinder = null;

		aggressiveBrain.attackProbability = 0.8f;
		aggressiveBrain.dodgeProbability = 0.2f;
		aggressiveBrain.runAwayProbability = 0;

		aggressiveBrain.decisionTime = 2000;
		aggressiveBrain.aimTime = 300;
		aggressiveBrain.hearDistance = 1000;

		// scaredy brain
		scaredBrain = new Brain();
		scaredBrain.attackPathFinder = new AttackPatternRush(bspTree);
		scaredBrain.aimPathFinder = new AimPattern(bspTree);
		scaredBrain.dodgePathFinder = new DodgePatternZigZag(bspTree);
		scaredBrain.idlePathFinder = null;
		scaredBrain.chasePathFinder = new AStarSearchWithBSP(bspTree);
		scaredBrain.runAwayPathFinder = new RunAwayPattern(bspTree);

		scaredBrain.attackProbability = 0.20f;
		scaredBrain.dodgeProbability = 0.40f;
		scaredBrain.runAwayProbability = 0.40f;

		scaredBrain.decisionTime = 4000;
		scaredBrain.aimTime = 1000;
		scaredBrain.hearDistance = 2000;

	}

	protected void createGameObjects(Vector mapObjects) {

		drawInstructions = false;
		MessageQueue queue = MessageQueue.getInstance();
		addOverlay(queue);
		addOverlay(new HeadsUpDisplay((Player) gameObjectManager.getPlayer()));
		queue.setDebug(true);
		queue.add("Use the mouse/arrow keys to move.");
		queue.add("Press Esc to exit.");

		createBrains();

		Enumeration i = mapObjects.elements();
		while (i.hasMoreElements()) {
			PolygonGroup group = (PolygonGroup) i.nextElement();
			String filename = group.getFilename();
			if ("robot.obj3d".equals(filename)) {
				gameObjectManager.add(new Bot(group));
			} else if ("averagebot.obj3d".equals(filename)) {
				AIBot bot = new AIBot(group, collisionDetection, averageBrain, botProjectileModel);
				gameObjectManager.add(bot);
			} else if ("aggressivebot.obj3d".equals(filename)) {
				AIBot bot = new AIBot(group, collisionDetection, aggressiveBrain, botProjectileModel);
				gameObjectManager.add(bot);
			} else if ("scaredybot.obj3d".equals(filename)) {
				AIBot bot = new AIBot(group, collisionDetection, scaredBrain, botProjectileModel);
				gameObjectManager.add(bot);
			} else {
				// static object
				gameObjectManager.add(new GameObject(group));
			}
		}
	}

}
