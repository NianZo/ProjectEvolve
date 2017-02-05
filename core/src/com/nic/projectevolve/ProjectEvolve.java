package com.nic.projectevolve;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nic.projectevolve.screens.MenuScreen;

public class ProjectEvolve extends Game {
	public static final int V_WIDTH = 800;
	public static final int V_HEIGHT = 400;
	public static final float PPM = 100;

	public static final int MAP_TILE_WIDTH = 50;
	public static final int MAP_TILE_HEIGHT = 50;

	public static final int NUM_MODULES = 19;

	public static final float MAX_VELOCITY = 3;
	public static final float MAX_ANGULAR_VELOCITY = 90;

	public static final int[][] MODULE_LOCATIONS = {{0,0},{0,32},{28,16},{28,-16},{0,-32},{-28,-16},{-28,16},{0,64},{28,48},{56,32},{56,0},{56,-32},{28,-48},{0,-64},{-28,-48},{-56,-32},{-56,0},{-56,32},{-28,48}};

	public static final int[][] ENEMY_MODULE_DESIGNS = {
			{0,1,-1,1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{0,-1,0,-1,-1,-1,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1}
	};
	public static final int NUM_ENEMY_DESIGNS = 2;

	public static final int[][] UPGRADE_COSTS = {{0,2,2,3,4},{0,5,6,7,8},{0,9,10,11,12}};

	public static GameState state;

	public static final String TITLE = "Evolve";

	public SpriteBatch batch;

    public static final short NOTHING_BIT = 0;
	public static final short EDGE_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ATTACKING_BIT = 8;
	public static final short DEFENDING_BIT = 16;

	public static final String[] MODULE_TEXTURE_NAMES = {"normal_module.png", "attacking_module.png", "midgreenmodule.png"};

	public static final String[] UPGRADE_DESCRIPTIONS = {
			"Speed Module\nCost: ",
			"Attack Module\nCost: ",
			"Defense Module\nCost: "
	};

	@Override
	public void create () {
		GameState.geneticMaterial = 0;
		batch = new SpriteBatch();
		state = new GameState();
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
}
