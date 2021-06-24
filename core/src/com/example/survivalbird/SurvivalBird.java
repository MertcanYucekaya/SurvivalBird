package com.example.survivalbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Date;
import java.util.Random;

public class SurvivalBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture backgorund;
    Texture bird;
    Texture bee1;
    Texture bee2;
    Texture bee3;
    float birdx = 0;
    float birdy = 0;
    int gamestate = 0;
    float velocity = 0;
    float gravity = 0.2f;
    float beeVelocity = 4;
    Random random;
    Circle birdCircle;
    long startSecond;
    long endSecond;
    boolean say = true;
    BitmapFont font;
    BitmapFont overFont;
    ShapeRenderer birdRender;
    Preferences preferences;
    boolean pref = true;


    int beeSet = 4;
    float[] beexArray = new float[beeSet];
    float distance = 0;
    float[] beeOffset1 = new float[beeSet];
    Circle[] beeCircle;


    @Override
    public void create() {
        preferences = Gdx.app.getPreferences("Best");
        batch = new SpriteBatch();
        bird = new Texture("bird.png");
        backgorund = new Texture("background.png");
        bee1 = new Texture("bee.png");
        bee2 = new Texture("bee.png");
        bee3 = new Texture("bee.png");
        birdx = Gdx.graphics.getHeight() / 3;
        birdy = Gdx.graphics.getHeight() / 2;
        distance = Gdx.graphics.getHeight() / 2;
        random = new Random();
        birdCircle = new Circle();
        beeCircle = new Circle[beeSet];
        birdRender = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(4);

        overFont = new BitmapFont();
        overFont.setColor(Color.PINK);
        overFont.getData().setScale(7);

        for (int i = 0; i < beeSet; i++) {
            beeOffset1[i] = (random.nextFloat()) * (Gdx.graphics.getHeight());

            beeCircle[i] = new Circle();


            beexArray[i] = Gdx.graphics.getHeight() - bee1.getWidth() / 2 + (i + 2) * distance;
            beexArray[i] = beexArray[i] + Gdx.graphics.getWidth() / 5;
        }

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(backgorund, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gamestate == 1) {
            if (say == true) {
                startSecond = System.currentTimeMillis();
                say = false;
            }
            endSecond = System.currentTimeMillis() - startSecond;

            font.draw(batch, String.valueOf(endSecond / 1000), 100, 200);

            if ((endSecond / 1000) % 10 == 0) {
                beeVelocity = beeVelocity + 0.02f;
            }
            if (Gdx.input.justTouched()) {
                velocity = -6;
            }
            for (int i = 0; i < beeSet; i++) {
                if (beexArray[i] < -100) {

                    beexArray[i] = beexArray[i] + beeSet * distance;
                    beeOffset1[i] = (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
                } else {
                    beexArray[i] = beexArray[i] - beeVelocity;
                }
                batch.draw(bee1, beexArray[i], Gdx.graphics.getHeight() / 2 + beeOffset1[i], Gdx.graphics.getHeight() / 10, Gdx.graphics.getHeight() / 10);

                beeCircle[i] = new Circle(beexArray[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getHeight() / 2 + beeOffset1[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getHeight() / 20);

                if (Intersector.overlaps(birdCircle, beeCircle[i])) {
                    gamestate = 2;
                }

            }
            if (birdy > 0 && birdy < Gdx.graphics.getHeight()-65) {
                velocity = velocity + gravity;
                birdy = birdy - velocity;
            } else {
                gamestate = 2;

            }

        } else if (gamestate == 0) {
            overFont.draw(batch, "Tap To Start", 200, 500);
            if (Gdx.input.justTouched()) {
                gamestate = 1;
            }
        } else if (gamestate == 2) {
            if (pref == true) {
                preferences.putLong("score", endSecond / 1000);
                pref = false;
            }
            if (preferences.getLong("score") < endSecond / 1000) {
                preferences.putLong("score", endSecond / 1000);
                preferences.flush();
            }
            font.draw(batch, "Best Score : " + preferences.getLong("score"), 200, 700);
            font.draw(batch, String.valueOf(endSecond / 1000), 100, 200);
            overFont.draw(batch, "Game Over Tap to Start Again", 200, 500);
            if (Gdx.input.justTouched()) {
                gamestate = 1;
                birdy = Gdx.graphics.getHeight() / 2;
                for (int i = 0; i < beeSet; i++) {
                    beeOffset1[i] = (random.nextFloat()) * (Gdx.graphics.getHeight());

                    beeCircle[i] = new Circle();

                    beexArray[i] = Gdx.graphics.getHeight() - bee1.getWidth() / 2 + (i + 2) * distance;
                    beexArray[i] = beexArray[i] + Gdx.graphics.getWidth() / 5;
                }

                say = true;
                velocity = 0;
                beeVelocity = 4;
            }

        }

        batch.draw(bird, birdx, birdy, Gdx.graphics.getHeight() / 10, Gdx.graphics.getHeight() / 10);

        batch.end();
        birdCircle.set(birdx + Gdx.graphics.getHeight() / 20, birdy + Gdx.graphics.getHeight() / 20, Gdx.graphics.getHeight() / 20);
        //birdRender.begin(ShapeRenderer.ShapeType.Filled);
        //birdRender.setColor(Color.BLACK);
        //birdRender.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        //for (int i = 0; i < beeSet; i++) {
        //birdRender.circle(beexArray[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getHeight()/2+beeOffset1[i]+Gdx.graphics.getHeight()/20,Gdx.graphics.getHeight()/20);

        //}
        //birdRender.end();

    }

    @Override
    public void dispose() {

    }
}
