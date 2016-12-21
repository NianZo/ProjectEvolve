package com.nic.projectevolve;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nic.projectevolve.screens.MenuScreen;

public class ProjectEvolve extends Game {
	public static final int V_WIDTH = 800;
	public static final int V_HEIGHT = 400;
	public static final float PPM = 100;

	public static final int NUMMODULES = 2;

	public static final int[][] MODULELOCATIONS = {{0,0},{0,32},{28,16},{28,-16},{0,-32},{-28,-16},{-28,16},{0,64},{28,48},{56,32},{56,0},{56,-32},{28,-48},{0,-64},{-28,-48},{-56,-32},{-56,0},{-56,32},{-28,48}};

	public static GameState state;

	public static final String TITLE = "Evolve";

	public SpriteBatch batch;

	public static final short EDGE_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ATTACKING_BIT = 8;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		state = new GameState();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
