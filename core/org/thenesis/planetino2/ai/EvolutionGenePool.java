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

import java.util.Vector;

import org.thenesis.planetino2.ai.pattern.AimPattern;
import org.thenesis.planetino2.ai.pattern.AttackPatternRush;
import org.thenesis.planetino2.ai.pattern.AttackPatternStrafe;
import org.thenesis.planetino2.ai.pattern.DodgePatternRandom;
import org.thenesis.planetino2.ai.pattern.DodgePatternZigZag;
import org.thenesis.planetino2.ai.pattern.RunAwayPattern;
import org.thenesis.planetino2.bsp2D.BSPTree;
import org.thenesis.planetino2.path.AStarSearchWithBSP;
import org.thenesis.planetino2.path.PathFinder;
import org.thenesis.planetino2.util.Comparable;
import org.thenesis.planetino2.util.MoreMath;
import org.thenesis.planetino2.util.QSortAlgorithm;


/**
    The EvolutionGenePool class keeps track of a collection of
    Brains. When an EvolutionBot is created, it requests a
    Brain from the gene pool. The Brain is either one of the
    brains in the collection, or a mutation of one of the brains.
    Only the top brains are collected, ranked by the amount of
    damage a brain's bot caused. There for, only the best brains
    reproduce.
*/

public class EvolutionGenePool {

    private static final int NUM_TOP_BRAINS = 5;
    private static final int NUM_TOTAL_BRAINS = 10;

    private class BrainStat extends Brain implements Comparable {

        long totalDamageCaused;
        int numBots;
        int generation;

        public BrainStat() {

        }

        public BrainStat(BrainStat brain) {
            super(brain);
            this.generation = brain.generation;
        }


        /**
            Gets the average damage this brain causes.
        */
        public float getAverageDamageCaused() {
            if (numBots == 0) {
                return 0;
            }
            else {
                return (float)totalDamageCaused / numBots;
            }
        }


        /**
            Reports damaged caused by a bot with this brain
            after the bot was destroyed.
        */
        public void report(long damageCaused) {
            totalDamageCaused+=damageCaused;
            numBots++;
        }


        /**
            Returns a smaller number if this brain caused more
            damage that the specified object, which should
            also be a brain.
        */
        public int compareTo(Object obj) {
            BrainStat other = (BrainStat)obj;
            float thisScore = this.getAverageDamageCaused();
            float otherScore = other.getAverageDamageCaused();
            if (thisScore == 0 && otherScore == 0) {
                // less number of bots is better
                return (this.numBots - other.numBots);
            }
            else {
                // more damage is better
                return (int)MoreMath.sign(otherScore - thisScore);
            }

        }


        /**
            Mutates this brain. The specified mutationProbability
            is the probability that each brain attribute
            becomes a different value, or "mutates".
        */
        public void mutate(float mutationProbability) {
            if (MoreMath.chance(mutationProbability)) {
                attackProbability = (float)MoreMath.random();
            }
            if (MoreMath.chance(mutationProbability)) {
                dodgeProbability = (float)MoreMath.random();
            }
            if (MoreMath.chance(mutationProbability)) {
                runAwayProbability = (float)MoreMath.random();
            }
            if (MoreMath.chance(mutationProbability)) {
                decisionTime = MoreMath.random(3000, 6000);
            }
            if (MoreMath.chance(mutationProbability)) {
                aimTime = MoreMath.random(300, 2000);
            }
            if (MoreMath.chance(mutationProbability)) {
                hearDistance = MoreMath.random(50, 2000);
            }
            if (MoreMath.chance(mutationProbability)) {
                attackPathFinder = (PathFinder)
                    MoreMath.random(attackPathFinders);
            }
            if (MoreMath.chance(mutationProbability)) {
                dodgePathFinder = (PathFinder)
                    MoreMath.random(dodgePathFinders);
            }
            if (MoreMath.chance(mutationProbability)) {
                aimPathFinder = (PathFinder)
                    MoreMath.random(aimPathFinders);
            }
            if (MoreMath.chance(mutationProbability)) {
                idlePathFinder = (PathFinder)
                    MoreMath.random(idlePathFinders);
            }
            if (MoreMath.chance(mutationProbability)) {
                chasePathFinder = (PathFinder)
                    MoreMath.random(chasePathFinders);
            }
            if (MoreMath.chance(mutationProbability)) {
                runAwayPathFinder = (PathFinder)
                    MoreMath.random(runAwayPathFinders);
            }

            fixProbabilites();
        }


        public Object clone() {
            BrainStat brain = new BrainStat(this);
            brain.generation++;
            return brain;
        }

        public String toString() {
            if (numBots == 0) {
                return "(Not Used)\n" + super.toString();
            }
            else {
                return "Average damage per bot: " +
                    getAverageDamageCaused() + " " +
                    "(" + numBots + " bots)\n" +
                    "Generation: " + generation + "\n" +
                    super.toString();
            }
        }
    }

    private Vector brains;

    private Vector attackPathFinders;
    private Vector aimPathFinders;
    private Vector dodgePathFinders;
    private Vector idlePathFinders;
    private Vector chasePathFinders;
    private Vector runAwayPathFinders;

    public EvolutionGenePool(BSPTree bspTree) {

        // create path finders
        attackPathFinders = new Vector();
        attackPathFinders.addElement(new AttackPatternRush(bspTree));
        attackPathFinders.addElement(new AttackPatternStrafe(bspTree));
            
        aimPathFinders = new Vector();
        aimPathFinders.addElement(new AimPattern(bspTree));
       
        dodgePathFinders =  new Vector();
        dodgePathFinders.addElement(new DodgePatternZigZag(bspTree));
        dodgePathFinders.addElement(new DodgePatternRandom(bspTree));
       
        idlePathFinders =  new Vector();
        
        chasePathFinders = new Vector();
        chasePathFinders.addElement(new AStarSearchWithBSP(bspTree));
        
        runAwayPathFinders = new Vector();
        runAwayPathFinders.addElement( new RunAwayPattern(bspTree));
        
        
//        //      create path finders
//        attackPathFinders = Arrays.asList(new Object[] {
//            new AttackPatternRush(bspTree),
//            new AttackPatternStrafe(bspTree)
//        });
//        aimPathFinders = Arrays.asList(new Object[] {
//            new AimPattern(bspTree)
//        });
//        dodgePathFinders = Arrays.asList(new Object[] {
//            new DodgePatternZigZag(bspTree),
//            new DodgePatternRandom(bspTree)
//        });
//        idlePathFinders = Arrays.asList(new Object[] {
//            null
//        });
//        chasePathFinders = Arrays.asList(new Object[] {
//            new AStarSearchWithBSP(bspTree)
//        });
//        runAwayPathFinders = Arrays.asList(new Object[] {
//            new RunAwayPattern(bspTree)
//        });

        // make a few random brains to start
        brains = new Vector();
        for (int i=0; i<NUM_TOTAL_BRAINS; i++) {
            BrainStat brain = new BrainStat();
            // randomize (mutate) all brain properties
            brain.mutate(1);
            brains.addElement(brain);
        }
    }

    /**
        The BSP tree used for certain patterns (like the
        shortest path alogirthm used for the chase pattern)
    */
    public void setBSPTree(BSPTree bspTree) {
        ((AStarSearchWithBSP)chasePathFinders.elementAt(0)).
            setBSPTree(bspTree);
    }


    public void resetEvolution() {
        brains.removeAllElements();
    }


    /**
        Gets a new brain from the gene pool. The brain will either
        be a "top" brain or a new, mutated "top" brain.
    */
    public Brain getNewBrain() {

        // 50% chance of creating a new, mutated a brain
        if (MoreMath.chance(.5f)) {
            BrainStat brain =
                (BrainStat)getRandomTopBrain().clone();

            // 10% to 25% chance of changing each attribute
            float p = MoreMath.random(0.10f, 0.25f);
            brain.mutate(p);
            return brain;
        }
        else {
            return getRandomTopBrain();
        }
    }


    /**
        Gets a random top-performing brain.
    */
    public Brain getRandomTopBrain() {
        int index = MoreMath.random(NUM_TOP_BRAINS-1);
        return (Brain)brains.elementAt(index);
    }


    /**
        Notify that a creature with the specified brain has
        been destroyed. The brain's stats aer recorded. If the
        brain's stats are within the top
    */
    public void notifyDead(Brain brain, long damageCaused) {
        // update statistics for this brain
        if (brain instanceof BrainStat) {
            BrainStat stat = (BrainStat)brain;

            // report the damage
            stat.report(damageCaused);

            // sort and trim the list
            if (!brains.contains(stat)) {
                brains.addElement(stat);
            }
            QSortAlgorithm.sort(brains);
            while (brains.size() > NUM_TOTAL_BRAINS) {
                brains.removeElementAt(NUM_TOTAL_BRAINS);
            }
        }
    }



    public String toString() {

        // display best brains
        String retVal = "Top " + NUM_TOP_BRAINS + " Brains:\n";
        for (int i=0; i<NUM_TOP_BRAINS; i++) {
            retVal+= (i+1) + ".\n";
            retVal+=brains.elementAt(i) + "\n";
        }

        return retVal;
    }
}
