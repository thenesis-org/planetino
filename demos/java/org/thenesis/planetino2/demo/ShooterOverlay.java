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

		//draw highlighted part of health bar
		w = (int) Math.floor((w * shooterPlayer.getAmmo() / shooterPlayer.getMaxAmmo()) + 0.5d);
		g.setColor(Color.WHITE.getRGB());
		g.fillRect(x, y, w, h);
		
		// draw health value (number)
		final char[] MAX_SIZE_STRING = new char[] {'M', 'M', 'M'};
		x = x - font.charsWidth(MAX_SIZE_STRING, 0, MAX_SIZE_STRING.length) + spacing;
		String str = Integer.toString((int) Math.floor(shooterPlayer.getAmmo() + 0.5d));
		g.setColor(Color.WHITE.getRGB());
		g.drawString(str, x, fontHeight);
		
	}

}
