package Cockroach;

import java.util.ArrayList;

public class Entity {

	int x, y, xv, yv, xl, yl, pd, dt = -1, still;
	int[][] directions = new int[][] {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
	boolean clicked = false;
	double facing = 0;  // radians, used for drawing rotation
	int fleeTimer = 0;

	public Entity(int xl, int yl) {
		this.xl = xl;
		this.yl = yl;
		int r = (int) (Math.random() * 4);
		if (r == 0) {
			x = 0;
			y = (int) (Math.random() * Cockroach.ylen);
			xv = 1; yv = 0;
			directions[1][0] = 0;
			still = 1;
		} else if (r == 1) {
			x = (int) (Math.random() * Cockroach.xlen);
			y = Cockroach.ylen;
			xv = 0; yv = -1;
			directions[3][1] = 0;
			still = 3;
		} else if (r == 2) {
			x = Cockroach.xlen;
			y = (int) (Math.random() * Cockroach.ylen);
			xv = -1; yv = 0;
			directions[5][0] = 0;
			still = 5;
		} else {
			x = (int) (Math.random() * Cockroach.xlen);
			y = 0;
			xv = 0; yv = 1;
			directions[7][1] = 0;
			still = 7;
		}
		pd = (r * 2 + 5) % 8;
		facing = Math.atan2(yv, xv);
	}

	public void direction(ArrayList<int[]> crumbs) {
		// Flee if cursor moved fast and is nearby
		if (x >= 0 && x < Cockroach.xlen && y >= 0 && y < Cockroach.ylen) {
			double mdx = x - Cockroach.mousePos[0];
			double mdy = y - Cockroach.mousePos[1];
			if (Cockroach.mouseSpeed > 15 && mdx * mdx + mdy * mdy < 100 * 100) {
				fleeTimer = 8;
			}
		}
		if (fleeTimer > 0) {
			fleeTimer--;
			// Steer directly away from cursor
			steerToward(2 * x - Cockroach.mousePos[0], 2 * y - Cockroach.mousePos[1]);
			return;
		}

		// Find nearest crumb with health remaining
		int[] nearest = null;
		double nearestDist = Double.MAX_VALUE;
		for (int[] crumb : crumbs) {
			if (crumb[2] <= 0) continue;
			double dx = crumb[0] - x, dy = crumb[1] - y;
			double d = dx * dx + dy * dy;
			if (d < nearestDist) {
				nearestDist = d;
				nearest = crumb;
			}
		}

		if (nearest != null && x >= 0 && x < Cockroach.xlen && y >= 0 && y < Cockroach.ylen) {
			if (Math.abs(x - nearest[0]) <= 3 && Math.abs(y - nearest[1]) <= 3) {
				// Arrived — stop and eat
				pd = still;
				xv = 0; yv = 0;
				return;
			}
			steerToward(nearest[0], nearest[1]);
		} else {
			// Random wander
			int i = (int) (Math.random() * 3) - 1;
			pd = (pd + i < 0 ? 7 : pd + i) % 8;
			xv = directions[pd][0];
			yv = directions[pd][1];
		}

		if (xv != 0 || yv != 0) facing = Math.atan2(yv, xv);
	}

	private void steerToward(int tx, int ty) {
		double dx = tx - x, dy = ty - y;
		double angle = Math.atan2(dy, dx);
		int bestDir = -1;
		double bestDot = -2;
		for (int d = 0; d < 8; d++) {
			double vx = directions[d][0], vy = directions[d][1];
			double len = Math.sqrt(vx * vx + vy * vy);
			if (len == 0) continue;
			double dot = (vx / len) * Math.cos(angle) + (vy / len) * Math.sin(angle);
			if (dot > bestDot) {
				bestDot = dot;
				bestDir = d;
			}
		}
		if (bestDir >= 0) {
			pd = bestDir;
			xv = directions[bestDir][0];
			yv = directions[bestDir][1];
			facing = Math.atan2(yv, xv);
		}
	}
}
