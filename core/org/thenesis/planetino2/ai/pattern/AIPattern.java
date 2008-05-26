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

import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.game.GameObject;
import org.thenesis.planetino2.math3D.Vector3D;
import org.thenesis.planetino2.path.PathFinder;


/**
    Simple abstract PathFinder that implements the
    find(Vector3D, Vector3D) method to return null.
    Unimplemented ideas: AttackPatternSneak and DodgePatternHide
*/
public abstract class AIPattern implements PathFinder {


    protected BSPTree bspTree;

    /**
        The BSP tree is used to get correct y values for the
        world.
    */
    public AIPattern(BSPTree bspTree) {
        this.bspTree = bspTree;
    }

    public void setBSPTree(BSPTree bspTree) {
        this.bspTree = bspTree;
    }

    /**
        The method isn't implemented for AIPatterns
    */
    public Enumeration find(Vector3D start, Vector3D goal) {
        return null;
    }

    public abstract Enumeration find(GameObject bot,
        GameObject player);


    /**
        Calculates the floor for the location specified. If
        the floor cannot be defertmined, the specified default
        value is used.
    */
    protected void calcFloorHeight(Vector3D v, float defaultY) {
       BSPTree.Leaf leaf = bspTree.getLeaf(v.x, v.z);
       if (leaf == null || leaf.floorHeight == Float.MIN_VALUE) {
           v.y = defaultY;
       }
       else {
           v.y = leaf.floorHeight;
       }
    }


    /**
        Gets the location between the player and the bot
        that is the specified distance away from the player.
    */
    protected Vector3D getLocationFromPlayer(GameObject bot,
        GameObject player, float desiredDistSq)
    {
        // get actual distance (squared)
        float distSq = bot.getLocation().
            getDistanceSq(player.getLocation());

        // if within 5 units, we're close enough
        if (Math.abs(desiredDistSq - distSq) < 25) {
            return new Vector3D(bot.getLocation());
        }

        // calculate vector to player from the bot
        Vector3D goal = new Vector3D(bot.getLocation());
        goal.subtract(player.getLocation());

        // find the goal distance from the player
        goal.multiply((float)Math.sqrt(desiredDistSq / distSq));

        goal.add(player.getLocation());
        calcFloorHeight(goal, bot.getFloorHeight());

        return goal;
    }

    public String toString() {
        // return the class name (not including the package name)
        String fullName = getClass().getName();
        int index = fullName.lastIndexOf('.');
        return fullName.substring(index+1);
    }

}
