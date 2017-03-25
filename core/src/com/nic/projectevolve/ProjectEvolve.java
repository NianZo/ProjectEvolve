package com.nic.projectevolve;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nic.projectevolve.screens.MenuScreen;

public class ProjectEvolve extends Game {
	public static final int V_WIDTH = 960;
	public static final int V_HEIGHT = 540;
	public static final float PPM = 100;

	public static final int MAP_TILE_WIDTH = 50;
	public static final int MAP_TILE_HEIGHT = 50;

	public static final int NUM_MODULES = 19;

	public static final float MAX_VELOCITY = 3;
	public static final float MAX_ANGULAR_VELOCITY = 90;

	// Set based off of screen size when app initialized
	public static int scl = 540 / 8;
	private static int scl_x = (int) Math.sqrt(2.0) * scl;
	public static final int[][] MODULE_LOCATIONS = {{0,0},{0,32},{28,16},{28,-16},{0,-32},{-28,-16},{-28,16},{0,64},{28,48},{56,32},{56,0},{56,-32},{28,-48},{0,-64},{-28,-48},{-56,-32},{-56,0},{-56,32},{-28,48}};
	public static int[][] SOCKET_LOCATIONS = {{0,0},{0,scl},{scl_x,scl/2},{scl_x,-scl/2},{0,-scl},{-scl_x,-scl/2},{-scl_x,scl/2},{0,scl*2},{scl_x,scl*3/2},{scl_x *2,scl},{scl_x *2,0},{scl_x *2,-scl},{scl_x,-scl*3/2},{0,-scl*2},{-scl_x,-scl*3/2},{-scl*2,-scl},{-scl_x *2,0},{-scl_x *2,scl},{-scl_x,scl*3/2}};

	public static final int[][] ENEMY_MODULE_DESIGNS = {
			{0,1,-1,1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{0,-1,0,-1,-1,-1,0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{2,2,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{0,1,-1,-1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			{2,2,-1,-1,2,-1,-1,1,-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1},
			{1,-1,-1,1,-1,1,-1,-1,-1,-1,-1,0,-1,-1,-1,0,-1,-1,-1}
	};
	public static final int NUM_ENEMY_DESIGNS = 6;
	public static final int[][] BOSS_MODULE_DESIGN = {
			{0, 2, 2, 2, -1, 2, 2, 1, -1, 1, -1, 1, -1, -1, -1, 1, -1, 1, -1}
	};


	public static final int[][] UPGRADE_COSTS = {{1,2,3,4,5},{5,10,15,20,25},{10,20,30,40,50}};

	public static GameState state;

	public static final String TITLE = "Evolve";

	public SpriteBatch batch;

    public static final short NOTHING_BIT = 0;
	public static final short EDGE_BIT = 1;
	public static final short PLAYER_BIT = 2;
	public static final short ENEMY_BIT = 4;
	public static final short ATTACKING_BIT = 8;
	public static final short DEFENDING_BIT = 16;

	public static final String[] MODULE_TEXTURE_NAMES = {"speed_module.png", "attacking_module.png", "defense_module.png"};

	public static final String[] BACKGROUND_TEXTURE_NAMES = {
			"background_1.png",
			"background_2.png",
			"background_3.png",
			"gray_square.png",
			"info_screen_background.png"};

	public static final String[] UPGRADE_DESCRIPTIONS = {
			"Upgrade\nHasty Cell\nCost: ",
			"Upgrade\nThorny Cell\nCost: ",
			"Upgrade\nTough Cell\nCost: "
	};

	public static final String[] SPAWNER_DESCRIPTIONS = {
			"Hasty Cell\nLevel: ",
			"Thorny Cell\nLevel: ",
			"Tough Cell\nLevel: "
	};

	public static final String[] LEVEL_NAMES = {
			"level_1.tmx",
			"level_2.tmx",
			"level_3.tmx",
			"level_4.tmx",
			"level_5.tmx",
			"level_6.tmx",
			"level_7.tmx",
			"level_8.tmx",
			"level_9.tmx",
			"level_10.tmx",
			"level_11.tmx",
	};
	public static final int NUM_LEVELS = 11;

	public static final String[] LEVEL_SELECT_NAMES = {
			"Level 1",
			"Level 2",
			"Level 3",
			"Level 4",
			"Level 5",
			"Level 6",
			"Level 7",
			"Level 8",
			"Level 9",
			"Level 10",
			"Level 11"
	};

	public static AssetManager manager;

	@Override
	public void create () {
		GameState.geneticMaterial = 0;
		batch = new SpriteBatch();
		state = new GameState();
		manager = new AssetManager();
		manager.load("sounds/play_theme.ogg", Music.class);
		manager.load("sounds/water_sfx.ogg", Sound.class);
		manager.finishLoading();
		if(Gdx.files.local("game_state.txt").length() != 0) {
			state.readStateFromFile();
		}
		setScreen(new MenuScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	public static void setScl(int newScale) {
		scl = newScale;
		scl_x = (int) (scl * Math.sqrt(3) / 2);
		SOCKET_LOCATIONS = new int[][] {{0,0},{0,scl},{scl_x,scl/2},{scl_x,-scl/2},{0,-scl},{-scl_x,-scl/2},{-scl_x,scl/2},{0,scl*2},{scl_x,scl*3/2},{scl_x *2,scl},{scl_x *2,0},{scl_x *2,-scl},{scl_x,-scl*3/2},{0,-scl*2},{-scl_x,-scl*3/2},{-scl_x *2,-scl},{-scl_x *2,0},{-scl_x *2,scl},{-scl_x,scl*3/2}};
	}
}
