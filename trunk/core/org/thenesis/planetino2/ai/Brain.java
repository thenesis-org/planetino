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
package org.thenesis.planetino2.ai;

import org.thenesis.planetino2.path.PathFinder;

public class Brain { //implements Cloneable {

    public PathFinder attackPathFinder;
    public PathFinder dodgePathFinder;
    public PathFinder aimPathFinder;
    public PathFinder idlePathFinder;
    public PathFinder chasePathFinder;
    public PathFinder runAwayPathFinder;

    // probability of each battle state
    // (the sum should be 1)
    public float attackProbability;
    public float dodgeProbability;
    public float runAwayProbability;

    public long decisionTime;
    public long aimTime;
    public float hearDistance;


    public void fixProbabilites() {
        // make the sums of the odds == 1.
        float sum = attackProbability + dodgeProbability +
            runAwayProbability;
        if (sum > 0) {
            attackProbability /= sum;
            dodgeProbability /= sum;
            runAwayProbability /= sum;
        }
    }

    public Brain() {

    }

    /**
        Copy constructor.
    */
    public Brain(Brain brain) {
        attackPathFinder = brain.attackPathFinder;
        dodgePathFinder = brain.dodgePathFinder;
        aimPathFinder = brain.aimPathFinder;
        idlePathFinder = brain.idlePathFinder;
        chasePathFinder = brain.chasePathFinder;
        runAwayPathFinder = brain.runAwayPathFinder;

        attackProbability = brain.attackProbability;
        dodgeProbability = brain.dodgeProbability;
        runAwayProbability = brain.runAwayProbability;

        decisionTime = brain.decisionTime;
        hearDistance = brain.hearDistance;
        aimTime = brain.aimTime;
    }

    public Object clone() {
        return new Brain(this);
    }

    public String toString() {
        return
            "DecisionTime: " + decisionTime + "\n" +
            "AimTime: " + aimTime + "\n" +
            "HearDistance: " + hearDistance + "\n" +

            "AttackProbability: " + attackProbability + "\n" +
            "DodgeProbability: " + dodgeProbability + "\n" +
            "RunAwayProbability: " + runAwayProbability + "\n" +

            "attackPathFinder: " + attackPathFinder +  "\n" +
            "dodgePathFinder: " + dodgePathFinder +  "\n" +
            "aimPathFinder: " + aimPathFinder +  "\n" +
            "idlePathFinder: " + idlePathFinder +  "\n" +
            "runAwayPathFinder: " + runAwayPathFinder +  "\n" +
            "chasePathFinder: " + chasePathFinder;

    }
}
