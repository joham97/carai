package com.hebe.carai.hud;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebe.carai.hud.components.HUDComponent;

public class HUD extends ArrayList<HUDComponent>{

	private static final long serialVersionUID = 3738895674136464452L;

	public void draw(SpriteBatch batch, BitmapFont font) {
		for(HUDComponent component : this){
			component.draw(batch, font);
		}
	}
	
}
