package Cockroach;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

public class Cockroach extends JPanel {

	static int xlen = 600, ylen = 600;
	static int[] mousePos = {300, 300};
	static double mouseSpeed = 0;
	public static ArrayList<Entity> entities = new ArrayList<>();
	public static ArrayList<int[]> crumbs = new ArrayList<>();  // {x, y, health}
	static ArrayList<int[]> splats = new ArrayList<>();          // {x, y, age}
	static int score = 0;
	static final int CRUMB_HEALTH = 10;
	static final int MAX_CRUMBS = 5;
	static Layout layout = new Layout();

	private int count1 = 0, count2 = 0;

	public Cockroach() {
		setPreferredSize(new Dimension(xlen, ylen));
		setBackground(new Color(220, 210, 190));
		Mouse mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		javax.swing.Timer refresh = new javax.swing.Timer(1000 / 50, e -> tick());
		refresh.start();
	}

	private void tick() {
		if (count1 == 5) {
			for (Entity entity : entities) {
				if (entity.dt == -1)
					entity.direction(crumbs);
			}
			if (count2 == 10) {
				if (entities.size() <= 20)
					entities.add(new Entity(35, 35));
				count2 = -1;
			}
			count2++;
			count1 = -1;
			mouseSpeed = 0;  // reset after each direction-update cycle
		}
		game();
		// Age splats and remove old ones
		for (int i = splats.size() - 1; i >= 0; i--) {
			splats.get(i)[2]++;
			if (splats.get(i)[2] > 250) splats.remove(i);
		}
		crumbs.removeIf(c -> c[2] <= 0);
		repaint();
		count1++;
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("Cockroach");
		f.add(new Cockroach(), BorderLayout.CENTER);
		f.setResizable(false);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public static void game() {
		for (int i = 0; i < entities.size(); i++) {
			Entity entity = entities.get(i);
			if (entity.dt != -1) {
				if (entity.dt == 100) {
					entities.remove(i);
					i--;
					continue;
				}
				entity.dt++;
			} else {
				entity.x += entity.xv;
				entity.y += entity.yv;
				// Eat crumbs: stopped and close enough → random bite each frame
				if (entity.xv == 0 && entity.yv == 0) {
					for (int[] crumb : crumbs) {
						if (crumb[2] > 0
								&& Math.abs(entity.x - crumb[0]) <= 5
								&& Math.abs(entity.y - crumb[1]) <= 5
								&& Math.random() < 0.03) {
							crumb[2]--;
						}
					}
				}
			}
			if (entity.x + entity.xl / 2 < 0 || entity.x - entity.xl / 2 >= xlen
					|| entity.y + entity.yl / 2 < 0 || entity.y - entity.yl / 2 >= ylen) {
				entities.remove(i);
				i--;
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Splat marks
		for (int[] splat : splats) {
			int alpha = Math.max(0, 160 - splat[2] * 160 / 250);
			g2.setColor(new Color(55, 12, 12, alpha));
			g2.fillOval(splat[0] - 16, splat[1] - 10, 32, 20);
		}

		// Crumbs — shrink visually as health depletes
		for (int[] crumb : crumbs) {
			int sz = 8 + crumb[2] * 14 / CRUMB_HEALTH;
			g2.setColor(new Color(200, 150, 80));
			g2.fillRoundRect(crumb[0] - sz / 2, crumb[1] - sz / 2, sz, sz, 5, 5);
			g2.setColor(new Color(150, 105, 50));
			g2.drawRoundRect(crumb[0] - sz / 2, crumb[1] - sz / 2, sz, sz, 5, 5);
		}

		// Entities
		for (Entity entity : entities)
			drawEntity(g2, entity);

		// HUD
		g2.setColor(new Color(55, 50, 45));
		g2.setFont(new Font("SansSerif", Font.BOLD, 15));
		g2.drawString("Squashed: " + score, 10, 22);
		g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
		g2.drawString("Left-click: squash  •  Drag: pick up  •  Right-click: place/remove crumb", 10, ylen - 10);
	}

	private void drawEntity(Graphics2D g2, Entity entity) {
		Graphics2D g2c = (Graphics2D) g2.create();
		g2c.translate(entity.x, entity.y);

		if (entity.dt == -1) {
			// Living cockroach — rotate to face direction of travel
			g2c.rotate(entity.facing + Math.PI / 2);

			// Legs (drawn first so body covers the roots)
			g2c.setColor(new Color(55, 48, 38));
			for (int row = 0; row < 3; row++) {
				int ly = -5 + row * 6;
				g2c.drawLine(-5, ly, -13, ly + 3);
				g2c.drawLine(5, ly, 13, ly + 3);
			}
			// Body
			g2c.setColor(new Color(40, 34, 26));
			g2c.fillOval(-5, -9, 10, 18);
			// Head
			g2c.fillOval(-4, -13, 8, 7);
			// Eyes
			g2c.setColor(new Color(210, 190, 60));
			g2c.fillOval(-3, -13, 2, 2);
			g2c.fillOval(1, -13, 2, 2);
			// Antennae
			g2c.setColor(new Color(70, 60, 48));
			g2c.drawLine(-2, -13, -7, -22);
			g2c.drawLine(2, -13, 7, -22);

		} else {
			// Death animation: rotate, squish outward, fade to dark red
			g2c.rotate(entity.facing + Math.PI / 2);
			float p = entity.dt / 100f;
			g2c.scale(1f + p * 2.2f, Math.max(0.05f, 1f - p));
			int alpha = Math.max(0, (int) (220 * (1 - p)));
			g2c.setColor(new Color(120, 15, 15, alpha));
			g2c.fillOval(-5, -9, 10, 18);
		}

		g2c.dispose();
	}
}
