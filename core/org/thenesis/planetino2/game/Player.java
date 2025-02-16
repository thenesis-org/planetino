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

import org.thenesis.planetino2.ai.Projectile;
import org.thenesis.planetino2.math3D.PolygonGroup;
import org.thenesis.planetino2.math3D.PolygonGroupBounds;
import org.thenesis.planetino2.math3D.Vector3D;

/**
    A Player object.
*/
public class Player extends JumpingGameObject {

    protected static final float BULLET_HEIGHT = 75;
    public static final float DEFAULT_PLAYER_RADIUS = 32;
    public static final float DEFAULT_PLAYER_HEIGHT = 128;
    protected static final float DEFAULT_MAX_HEALTH = 100;
    protected static final float DEFAULT_HEAR_DISTANCE = 1000;

    protected PolygonGroup blastModel;
    protected float maxHealth;
    protected float health;
    
    protected float hearDistance = DEFAULT_HEAR_DISTANCE;

	public Player() {
        super(new PolygonGroup("player"));

        // set up player bounds
        PolygonGroupBounds playerBounds = getBounds();
        playerBounds.setTopHeight(DEFAULT_PLAYER_HEIGHT);
        playerBounds.setRadius(DEFAULT_PLAYER_RADIUS);
        playerBounds.setMinX(-DEFAULT_PLAYER_RADIUS);
        playerBounds.setMaxX(DEFAULT_PLAYER_RADIUS);
        playerBounds.setMinZ(-DEFAULT_PLAYER_RADIUS);
        playerBounds.setMaxZ(DEFAULT_PLAYER_RADIUS);

        // set up health
        maxHealth = DEFAULT_MAX_HEALTH;
        setHealth(maxHealth);
    }

    public void setBlastModel(PolygonGroup blastModel) {
        this.blastModel = blastModel;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void addHealth(float addition) {
        setHealth(health + addition);
    }

    public boolean isAlive() {
        return (health > 0);
    }

    public void fireProjectile() {

        //
        float x = -getTransform().getSinAngleY();
        float z = -getTransform().getCosAngleY();
        float cosX = getTransform().getCosAngleX();
        float sinX = getTransform().getSinAngleX();
        Projectile blast = new Projectile(
            (PolygonGroup)blastModel.clone(),
            new Vector3D(cosX*x, sinX, cosX*z),
            null,
            40, 60);
        float dist = getBounds().getRadius() +
            blast.getBounds().getRadius();
        // blast starting location needs work. looks like
        // the blast is coming out of your forehead when
        // you're shooting down.
        blast.getLocation().setTo(
            getX() + x*dist,
            getY() + BULLET_HEIGHT,
            getZ() + z*dist);

        // "spawns" the new game object
        addSpawn(blast);

        // make a "virtual" noise that bots can "hear"
        // (500 milliseconds)
        makeNoise(500);
    }
    
    public float getHearDistance() {
		return hearDistance;
	}

	public void setHearDistance(float hearDistance) {
		this.hearDistance = hearDistance;
	}

}
