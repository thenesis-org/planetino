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

import java.util.Enumeration;
import org.thenesis.planetino2.util.Vector;

import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.util.MoreMath;


/**
    Direct aim at the player, with a random offset. Aim patterns
    return a direction to fire, rather than a location.
*/
public class AimPattern extends AIPattern {

    protected float accuracy;

    public AimPattern(BSPTree tree) {
        super(tree);
    }

    /**
        Sets the accuracy of the aim from 0 (worst) to 1 (best).
    */
    public void setAccuracy(float p) {
        this.accuracy = p;
    }


    public Enumeration find(GameObject bot, GameObject player) {
        Vector3D goal = new Vector3D(player.getLocation());
        goal.y += player.getBounds().getTopHeight() / 2;
        goal.subtract(bot.getLocation());

        // Rotate up to 10 degrees off y-axis
        // (This could use an up/down random offset as well.)
        if (accuracy < 1) {
            float maxAngle = 10 * (1-accuracy);
            float angle = MoreMath.random(-maxAngle, maxAngle);
            goal.rotateY((float)Math.toRadians(angle));
        }

        goal.normalize();

        // return an iterator
        //return Collections.singleton(goal).iterator();
        Vector v = new Vector();
        v.addElement(goal);
        return v.elements();
    }
}
