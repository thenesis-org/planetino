package org.thenesis.planetino2.demo;

import org.thenesis.planetino2.engine.shooter3D.HeadsUpDisplay;
import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.math3D.ViewWindow;

public class ShooterOverlay extends HeadsUpDisplay {

	ShooterPlayer shooterPlayer;
	
	public ShooterOverlay(ShooterPlayer player) {
		super(player);
		this.shooterPlayer = player;
	}
	
	@Override
	public void draw(Graphics g, ViewWindow window) {
		super.draw(g, window);
		
		Font font = g.getFont();
		int fontHeight = font.getHeight();
		int spacing = fontHeight / 5;

		int w = window.getWidth() / 4;
		int h = window.getHeight() / 60;
		int x =  window.getWidth() - window.getWidth() / 4 - spacing;
		int y = fontHeight / 2;
		g.setColor(Color.GRAY.getRGB());
		g.fillRect(x, y, w, h);

		// Draw highlighted part of health bar
		w = (int) Math.floor((w * shooterPlayer.getAmmo() / shooterPlayer.getMaxAmmo()) + 0.5d);
		g.setColor(Color.WHITE.getRGB());
		g.fillRect(x, y, w, h);
		
		// Draw health value (number)
		final char[] MAX_SIZE_STRING = new char[] {'M', 'M', 'M'};
		x = x - font.charsWidth(MAX_SIZE_STRING, 0, MAX_SIZE_STRING.length) + spacing;
		String str = Integer.toString((int) Math.floor(shooterPlayer.getAmmo() + 0.5d));
		g.setColor(Color.WHITE.getRGB());
		g.drawString(str, x, fontHeight);
		
		// Draw crosshair
		int rectSize = 5;
		int lineSize = 5;
		int spaceSize = 4;
		int rectX = window.getWidth() / 2;
		int rectY = window.getHeight() / 2;
		g.setColor(0xFFFF6633); //Color.RED.getRGB());
		g.fillRect(rectX - rectSize / 2, rectY - rectSize / 2, rectSize, rectSize);
		g.drawLine(rectX - rectSize / 2 - lineSize - spaceSize, rectY, rectX - rectSize / 2 - spaceSize, rectY);
		g.drawLine(rectX + rectSize / 2 + spaceSize, rectY, rectX + rectSize / 2 + spaceSize + lineSize, rectY);
		g.drawLine(rectX, rectY - rectSize / 2 - lineSize - spaceSize, rectX, rectY - rectSize / 2 - spaceSize);
		g.drawLine(rectX, rectY + rectSize / 2 + spaceSize, rectX, rectY + rectSize / 2 + spaceSize + lineSize);
		
	}

}
