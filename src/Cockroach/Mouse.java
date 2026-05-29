package Cockroach;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Mouse extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			for (Entity entity : Cockroach.entities) {
				if (entity.dt == -1
						&& entity.x + entity.xl / 2 >= e.getX() && entity.x - entity.xl / 2 <= e.getX()
						&& entity.y + entity.yl / 2 >= e.getY() && entity.y - entity.yl / 2 <= e.getY()) {
					entity.dt = 0;
					Cockroach.score++;
					Cockroach.splats.add(new int[]{entity.x, entity.y, 0});
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			for (Entity entity : Cockroach.entities) {
				if (!entity.clicked && entity.dt == -1
						&& entity.x + entity.xl / 2 >= e.getX() && entity.x - entity.xl / 2 <= e.getX()
						&& entity.y + entity.yl / 2 >= e.getY() && entity.y - entity.yl / 2 <= e.getY()) {
					entity.clicked = true;
					entity.x = e.getX();
					entity.y = e.getY();
				}
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			// Toggle crumb: remove if clicking near an existing one, otherwise place
			int mx = e.getX(), my = e.getY();
			for (int i = 0; i < Cockroach.crumbs.size(); i++) {
				int[] c = Cockroach.crumbs.get(i);
				if (Math.abs(c[0] - mx) < 20 && Math.abs(c[1] - my) < 20) {
					Cockroach.crumbs.remove(i);
					return;
				}
			}
			if (Cockroach.crumbs.size() < Cockroach.MAX_CRUMBS)
				Cockroach.crumbs.add(new int[]{mx, my, Cockroach.CRUMB_HEALTH});
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			for (Entity entity : Cockroach.entities)
				entity.clicked = false;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		double dx = e.getX() - Cockroach.mousePos[0];
		double dy = e.getY() - Cockroach.mousePos[1];
		// Keep the peak speed seen since the last direction-update cycle
		double spd = Math.sqrt(dx * dx + dy * dy);
		if (spd > Cockroach.mouseSpeed) Cockroach.mouseSpeed = spd;
		Cockroach.mousePos[0] = e.getX();
		Cockroach.mousePos[1] = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		double dx = e.getX() - Cockroach.mousePos[0];
		double dy = e.getY() - Cockroach.mousePos[1];
		double spd = Math.sqrt(dx * dx + dy * dy);
		if (spd > Cockroach.mouseSpeed) Cockroach.mouseSpeed = spd;
		Cockroach.mousePos[0] = e.getX();
		Cockroach.mousePos[1] = e.getY();
		for (Entity entity : Cockroach.entities)
			if (entity.clicked) {
				entity.x = e.getX();
				entity.y = e.getY();
			}
	}
}
