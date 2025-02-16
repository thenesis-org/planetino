package org.thenesis.planetino2.shooter;

import org.thenesis.planetino2.graphics.Color;
import org.thenesis.planetino2.graphics.Font;
import org.thenesis.planetino2.graphics.Graphics;
import org.thenesis.planetino2.math3D.ViewWindow;

public class ShooterOverlay extends HeadsUpDisplay {

	private static final int COLOR1 = 0xFFFF6633;
	private static final int COLOR2 = 0xFFFF2233; //0xFFCA562C;
	
	private ShooterPlayer shooterPlayer;
	private ShooterObjectManager gameObjectManager;
	private int currentColor1 = COLOR1;
	private int currentColor2 = COLOR2;
	private int colorChangeTime = 0;

	public ShooterOverlay(ShooterPlayer player, ShooterObjectManager gameObjectManager) {
		super(player);
		this.shooterPlayer = player;
		this.gameObjectManager = gameObjectManager;
	}
	
	@Override
	public void update(long elapsedTime) {
		super.update(elapsedTime);
		
		// Change the crosshair color over the time 
		colorChangeTime += elapsedTime;
		if (shooterPlayer.isCurrentGravityGunProjectileAlive()) {
			currentColor1 = currentColor2 = Color.WHITE.getRGB();
		} else {
			if (colorChangeTime > 600) {
				if (currentColor1 == COLOR1) {
					currentColor1 = COLOR2;
					currentColor2 = COLOR1;
				} else {
					currentColor1 = COLOR1;
					currentColor2 = COLOR2;
				}
				colorChangeTime = 0;
			}
		}
	}
	
	@Override
	public void draw(Graphics g, ViewWindow window) {
		super.draw(g, window);

		Font font = g.getFont();
		int fontHeight = font.getHeight();
		int spacing = fontHeight / 5;

		/* Ammo bar */
		if (shooterPlayer.getWeapon() != null) {
			int w = window.getWidth() / 4;
			int h = window.getHeight() / 60;
			int x = window.getWidth() - window.getWidth() / 4 - spacing;
			int y = fontHeight / 2;
			g.setColor(Color.GRAY.getRGB());
			g.fillRect(x, y, w, h);

			// Draw highlighted part
			w = (int) Math.floor((w * shooterPlayer.getWeapon().getAmmo() / shooterPlayer.getWeapon().getMaxAmmo()) + 0.5d);
			g.setColor(Color.WHITE.getRGB());
			g.fillRect(x, y, w, h);

			// Draw ammo value (number)
			final char[] MAX_SIZE_STRING = new char[] { 'M', 'M', 'M' };
			x = x - font.charsWidth(MAX_SIZE_STRING, 0, MAX_SIZE_STRING.length) + spacing;
			String str = Integer.toString((int) Math.floor(shooterPlayer.getWeapon().getAmmo() + 0.5d));
			g.setColor(Color.WHITE.getRGB());
			g.drawString(str, x, fontHeight);
		}

		/* Enemies */

		int enemies = gameObjectManager.getAliveEnemyCount();
		String str = "Enemies: " + enemies;
		int w = window.getWidth() / 4;
		int h = window.getHeight() / 60;
		int x = window.getWidth() - spacing;
		x = x - font.charsWidth(str.toCharArray(), 0, str.length());
		int y = window.getHeight() - fontHeight - spacing;
		g.setColor(Color.WHITE.getRGB());
		g.drawString(str, x, y);

		/* Crosshair */

		if (shooterPlayer.getWeapon() != null) {
			if (shooterPlayer.getWeapon().getType() == Weapon.WEAPON_GRAVITY_GUN) {
				int rectSize = 12;
				int rectX = window.getWidth() / 2;
				int rectY = window.getHeight() / 2;
//				g.setColor(currentColor1);
//				g.drawLine(rectX, rectY, rectX, rectY);
				g.setColor(currentColor1);
				drawRect(g, rectX - rectSize / 2, rectY - rectSize / 2, rectSize, rectSize);
				rectSize *= 2;
				g.setColor(currentColor2);
				drawRect(g, rectX - rectSize / 2, rectY - rectSize / 2, rectSize, rectSize);
			} else {
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

	}

	public void drawRect(Graphics g, int x, int y, int width, int height) {
		g.drawLine(x, y, x + width, y);
		g.drawLine(x + width, y, x + width, y + height);
		g.drawLine(x + width, y + height, x, y + height);
		g.drawLine(x, y + height, x, y);
	}

}
