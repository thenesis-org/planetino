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
package org.thenesis.planetino2.bsp2D;

import java.util.Hashtable;

import org.thenesis.planetino2.game.GameObjectManager;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.graphics.Screen;
import org.thenesis.planetino2.graphics3D.ScanConverter;
import org.thenesis.planetino2.graphics3D.SortedScanConverter;
import org.thenesis.planetino2.graphics3D.ZBufferedRenderer;
import org.thenesis.planetino2.graphics3D.texture.AnimatedRectangularSurface;
import org.thenesis.planetino2.graphics3D.texture.DisabledShadingSurface;
import org.thenesis.planetino2.graphics3D.texture.PowerOf2Texture;
import org.thenesis.planetino2.graphics3D.texture.PreShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.ShadedTexture;
import org.thenesis.planetino2.graphics3D.texture.SmallShadedSurface;
import org.thenesis.planetino2.graphics3D.texture.StretchedAnimatedRectangularSurface;
import org.thenesis.planetino2.graphics3D.texture.Texture;
import org.thenesis.planetino2.math3D.Rectangle3D;
import org.thenesis.planetino2.math3D.TexturedPolygon3D;
import org.thenesis.planetino2.math3D.Transform3D;
import org.thenesis.planetino2.math3D.ViewWindow;


/**
    The BSPRenderer class is a renderer capable of drawing
    polygons in a BSP tree and any polygon objects in the scene.
    When drawing BSP polygons, the BSPRenderer writes the BSP
    polygon depth to a z-buffer. Polygon objects use the z-buffer
    to determine their visibility within the scene on a per-pixel
    basis.
*/
public class BSPRenderer extends ZBufferedRenderer
    implements BSPTreeTraverseListener
{

    /**
        How many polygons to draw before checking if the view
        is filled.
    */
    private static final int FILLED_CHECK = 3;

    protected Hashtable bspScanRenderers;
    protected BSPTreeTraverser traverser;
    protected Graphics currentGraphics2D;
    protected boolean viewNotFilledFirstTime;
    protected int polygonCount;

    /**
        Creates a new BSP renderer with the specified camera
        object and view window.
    */
    public BSPRenderer(Transform3D camera,
        ViewWindow viewWindow)
    {
        super(camera, viewWindow, false);
        viewNotFilledFirstTime = true;
        traverser = new BSPTreeTraverser(this);
    }


    /**
        Sets the GamebjectManager. The BSP traverser sets the
        visibily of the objects.
    */
    public void setGameObjectManager(
        GameObjectManager gameObjectManager)
    {
        traverser.setGameObjectManager(gameObjectManager);
    }

    protected void init() {
        destPolygon = new TexturedPolygon3D();
        scanConverter = new SortedScanConverter(viewWindow);

		// create renderers for each texture (HotSpot optimization)
		scanRenderers = new Hashtable();
		scanRenderers.put(PowerOf2Texture.class, new PowerOf2TextureZRenderer());
		scanRenderers.put(ShadedTexture.class, new ShadedTextureZRenderer());
		scanRenderers.put(PreShadedSurface.class, new ShadedSurfaceZRenderer());
		scanRenderers.put(DisabledShadingSurface.class, new ShadedSurfaceZRenderer());
		scanRenderers.put(SmallShadedSurface.class, new SmallShadedSurfaceZRenderer());
		scanRenderers.put(AnimatedRectangularSurface.class, new AnimatedSurfaceZRenderer());
		scanRenderers.put(StretchedAnimatedRectangularSurface.class, new AnimatedSurfaceZRenderer());

		// same thing, for bsp tree polygons
		bspScanRenderers = new Hashtable();
		bspScanRenderers.put(PowerOf2Texture.class, new PowerOf2TextureRenderer());
		bspScanRenderers.put(ShadedTexture.class, new ShadedTextureRenderer());
		bspScanRenderers.put(PreShadedSurface.class, new ShadedSurfaceRenderer());
		bspScanRenderers.put(DisabledShadingSurface.class, new ShadedSurfaceRenderer());
    }


    public void startFrame(Screen screen) {
        super.startFrame(screen);
        ((SortedScanConverter)scanConverter).clear();
        polygonCount = 0;
    }


    public void endFrame(Screen screen) {
        super.endFrame(screen);
        if (!((SortedScanConverter)scanConverter).isFilled()) {
//            g.drawString("View not completely filled", 5,
//                viewWindow.getTopOffset() +
//                viewWindow.getHeight() - 5);
            if (viewNotFilledFirstTime) {
                viewNotFilledFirstTime = false;
                // print message to console in case user missed it
                System.out.println("View not completely filled.");
            }
            // clear the background next time
            clearViewEveryFrame = true;
        }
        else {
            clearViewEveryFrame = false;
        }
    }


    /**
        Draws the visible polygons in a BSP tree based on
        the camera location. The polygons are drawn front-to-back.
    */
    public void draw(Graphics g, BSPTree tree) {
        ((SortedScanConverter)scanConverter).setSortedMode(true);
        currentGraphics2D = g;
        traverser.traverse(tree, camera.getLocation());
        ((SortedScanConverter)scanConverter).setSortedMode(false);
    }


    // from the BSPTreeTraverseListener interface
    public boolean visitPolygon(BSPPolygon poly, boolean isBack) {
        SortedScanConverter scanConverter =
            (SortedScanConverter)this.scanConverter;

        draw(currentGraphics2D, poly);

        // check if view is filled every three polygons
        polygonCount++;
        if (polygonCount == FILLED_CHECK) {
            polygonCount = 0;
            return
                !((SortedScanConverter)scanConverter).isFilled();
        }
        return true;
    }


    protected void drawCurrentPolygon(Graphics g) {
        if (!(sourcePolygon instanceof BSPPolygon)) {
            super.drawCurrentPolygon(g);
            return;
        }
        buildSurface();
        SortedScanConverter scanConverter =
            (SortedScanConverter)this.scanConverter;
        TexturedPolygon3D poly = (TexturedPolygon3D)destPolygon;
        Texture texture = poly.getTexture();
        ScanRenderer scanRenderer = (ScanRenderer)
            bspScanRenderers.get(texture.getClass());
        scanRenderer.setTexture(texture);
        Rectangle3D textureBounds = poly.getTextureBounds();

        a.setToCrossProduct(textureBounds.getDirectionV(),
            textureBounds.getOrigin());
        b.setToCrossProduct(textureBounds.getOrigin(),
            textureBounds.getDirectionU());
        c.setToCrossProduct(textureBounds.getDirectionU(),
            textureBounds.getDirectionV());

        // w is used to compute depth at each pixel
        w = SCALE * MIN_DISTANCE * Short.MAX_VALUE /
            (viewWindow.getDistance() *
            c.getDotProduct(textureBounds.getOrigin()));

        int y = scanConverter.getTopBoundary();
        viewPos.y = viewWindow.convertFromScreenYToViewY(y);
        viewPos.z = -viewWindow.getDistance();

        while (y<=scanConverter.getBottomBoundary()) {
            for (int i=0; i<scanConverter.getNumScans(y); i++) {
                ScanConverter.Scan scan =
                    scanConverter.getScan(y, i);

                if (scan.isValid()) {
                    viewPos.x = viewWindow.
                        convertFromScreenXToViewX(scan.left);
                    int offset = (y - viewWindow.getTopOffset()) *
                        viewWindow.getWidth() +
                        (scan.left - viewWindow.getLeftOffset());

                    scanRenderer.render(offset, scan.left,
                        scan.right);
                    setScanDepth(offset, scan.right-scan.left+1);
                }
            }
            y++;
            viewPos.y--;
        }
    }


    /**
        Sets the z-depth for the current polygon scan.
    */
    private void setScanDepth(int offset, int width) {
        float z = c.getDotProduct(viewPos);
        float dz = c.x;
        int depth = (int)(w*z);
        int dDepth = (int)(w*dz);
        short[] depthBuffer = zBuffer.getArray();
        int endOffset = offset + width;

        // depth will be constant for many floors and ceilings
        if (dDepth == 0) {
            short d = (short)(depth >> SCALE_BITS);
            while (offset < endOffset) {
                depthBuffer[offset++] = d;
            }
        }
        else {
            while (offset < endOffset) {
                depthBuffer[offset++] =
                    (short)(depth >> SCALE_BITS);
                depth += dDepth;
            }
        }
    }

}
