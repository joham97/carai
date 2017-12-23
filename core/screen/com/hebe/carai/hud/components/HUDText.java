package com.hebe.carai.hud.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUDText extends HUDComponent{
	
	private String text;
	
	public HUDText(int x, int y, String text) {
		super(x, y);
		this.text = text;
	}
	
	@Override
	public void draw(SpriteBatch batch, BitmapFont font) {
		font.draw(batch, this.text, this.x, this.y);
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
}
