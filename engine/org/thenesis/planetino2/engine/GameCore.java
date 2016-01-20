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
package org.thenesis.planetino2.engine;

import java.io.IOException;

import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Image;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics.Toolkit;


/**
    Simple abstract class used for testing. Subclasses should
    implement the draw() method.
*/
public abstract class GameCore implements Runnable {

    protected static final int DEFAULT_FONT_SIZE = 24;

    private boolean running;
    protected Screen screen;
    protected int fontSize = DEFAULT_FONT_SIZE;

    
    public GameCore(Screen screen) {
    	this.screen = screen;
    }
    
    /**
        Signals the game loop that it's time to quit
    */
    public void stop() {
        running = false;
    }
    
    public boolean isRunning() {
		return running;
	}

    /**
        Calls init() and gameLoop()
    */
    public void run() {
        try {
            init();
            gameLoop();
        }
        finally {
        	close();
            lazilyExit();
        }
    }
    
    /**
     * Close/free the resources used by the engine
     */
    public void close() {
    	 if (screen != null) {
             screen.restoreScreen();
         }
    }


    /**
        Exits the VM from a daemon thread. The daemon thread waits
        2 seconds then calls System.exit(0). Since the VM should
        exit when only daemon threads are running, this makes sure
        System.exit(0) is only called if neccesary. It's neccesary
        if the Java Sound system is running.
    */
    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // first, wait for the VM exit on its own.
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException ex) { }
                // system is still running, so force an exit
                System.exit(0);
            }
        };
        //thread.setDaemon(true);
        thread.start();
    }

    /**
        Sets full screen mode and initiates and objects.
    */
    public void init() {
        //screen = new Screen();
//      DisplayMode displayMode =
//          screen.findFirstCompatibleMode(possibleModes);
//      screen.setFullScreen(displayMode);
//
//      Window window = screen.getFullScreenWindow();
//      window.setFont(new Font("Dialog", Font.PLAIN, fontSize));
//      window.setBackground(Color.blue);
//      window.setForeground(Color.white);

      running = true;
    }


//    public Image loadImage(String fileName) throws IOException {
//        return Toolkit.getInstance().createImage(fileName);
//    }


    /**
        Runs through the game loop until stop() is called.
    */
    public void gameLoop() {
        long startTime = System.currentTimeMillis();
        long currTime = startTime;

        while (running) {
            long elapsedTime =
                System.currentTimeMillis() - currTime;
            currTime += elapsedTime;

           tick(elapsedTime);

            // don't take a nap! run as fast as possible
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ex) { }
        }
    }
    
    public void tick(long elapsedTime) {
    	 // update
        update(elapsedTime);

        // draw the screen
        Graphics g = screen.getGraphics();
        draw(g);
        //g.dispose();
        screen.update();
    }


    /**
        Updates the state of the game/animation based on the
        amount of elapsed time that has passed.
    */
    public void update(long elapsedTime) {
        // do nothing
    }


    /**
        Draws to the screen. Subclasses must override this
        method.
    */
    public abstract void draw(Graphics g);
}
