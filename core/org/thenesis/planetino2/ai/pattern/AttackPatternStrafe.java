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
package org.thenesis.planetino2.ai.pattern;

import java.util.*;

import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.util.MoreMath;

/**
    An "attack" pattern to strafe around the player in a
    circle with the specified radius from the player.
*/
public class AttackPatternStrafe extends AIPattern {

    private float radiusSq;

    public AttackPatternStrafe(BSPTree tree) {
        this(tree, 250);
    }

    public AttackPatternStrafe(BSPTree tree, float radius) {
        super(tree);
        this.radiusSq = radius * radius;
    }


    public Enumeration find(GameObject bot, GameObject player) {

       Vector path = new Vector();

        // find first location within desired radius
        Vector3D firstGoal = getLocationFromPlayer(bot, player,
            radiusSq);
        if (!firstGoal.equals(bot.getLocation())) {
            path.addElement(firstGoal);
        }

        // make a counter-clockwise circle around the player
        // (since circle movement is not available, it's actually
        // an octagon).
        int numPoints = 8;
        float angle = (float)(2 * Math.PI / numPoints);
        if (MoreMath.chance(.5f)) {
            angle*=-1;
        }
        float lastY = bot.getFloorHeight();
        for (int i=1; i<numPoints; i++) {
            Vector3D goal = new Vector3D(firstGoal);
            goal.subtract(player.getLocation());
            goal.rotateY(angle * i);
            goal.add(player.getLocation());
            calcFloorHeight(goal, lastY);
            lastY = goal.y;
            path.addElement(goal);
        }

        // add last location (back to start)
        path.addElement(firstGoal);


        return path.elements();
    }
}
