package com.codingee.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;


import java.util.Random;

import sun.rmi.runtime.Log;

public class flappybird2 extends ApplicationAdapter {
	SpriteBatch batch,batchextra;
	Texture background,bird,bottomtube,toptube,gameover,newhightexture,touchstarttexture,title;
	TextureRegion[] animatedFrame;
	float elapsedTime,birdY,velocity=0,gap=400,maxtubeOffset,tubeVelocity=4,distancebetweentubes;
	float[] tubeOffset=new float[4];
	float[] tubeX=new float[4];
	int numberoftubes=4;
	int gameState=0;
	float gravity=2;
	Random randomGenerator;
	Circle birdCircle;
	Rectangle[] toptubeRectangle,bottomtubeRectangle;
	//ShapeRenderer shapeRenderer;

    int score=0;
    int highScore=0;
    int scoringTube=0;


    BitmapFont font,scorecard,highscorecard;

    GlyphLayout glyphLayoutScore,glyphLayoutHighScore;

    Image newhigh,touchstart;



    Sound hit,wing,general,die,point;
    Music flappymusic;
    int diecounter,velocitycounter=1;
    Animation animation;

    Preferences highscorepref;







	
	@Override
	public void create () {
		batch = new SpriteBatch();
		batchextra=new SpriteBatch();
		background = new Texture("bg.png");
		bird=new Texture("bird.png");
		bottomtube=new Texture("bottomtube.png");
		toptube=new Texture("toptube.png");
		gameover=new Texture("gameover.png");
		newhightexture=new Texture("newhigh.png");
		touchstarttexture=new Texture("touchstart.png");


		birdY=Gdx.graphics.getHeight()/2-bird.getHeight()/2;
		maxtubeOffset=Gdx.graphics.getHeight()/2-gap/2-100;
		distancebetweentubes=Gdx.graphics.getWidth()*3/4;
		randomGenerator=new Random();
		//shapeRenderer=new ShapeRenderer();
		birdCircle=new Circle();
		toptubeRectangle=new Rectangle[numberoftubes];
		bottomtubeRectangle=new Rectangle[numberoftubes];
		font=new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().scale(10);

		scorecard=new BitmapFont();
		scorecard.setColor(Color.WHITE);
		scorecard.getData().scale(5);



		highscorecard=new BitmapFont();
		highscorecard.setColor(Color.ROYAL);
		highscorecard.getData().scale(5);

		glyphLayoutScore=new GlyphLayout();
		glyphLayoutHighScore=new GlyphLayout();

		newhigh=new Image(newhightexture);
		Float FADE_TIME=0.75f;
		newhigh.addAction(Actions.alpha(0));
		newhigh.act(0);
        newhigh.addAction(Actions.forever(Actions.sequence(Actions.fadeIn(FADE_TIME), Actions.fadeOut(FADE_TIME))));

        touchstart=new Image(touchstarttexture);
        touchstart.act(0);
        touchstart.addAction(Actions.forever(Actions.sequence(Actions.fadeIn(FADE_TIME),Actions.fadeOut(FADE_TIME))));


        highscorepref=Gdx.app.getPreferences("high score preferences");
        highScore=highscorepref.getInteger("highscore");





		startGame();


        hit=Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        die=Gdx.audio.newSound(Gdx.files.internal("die.wav"));
        point=Gdx.audio.newSound(Gdx.files.internal("point.wav"));
        wing=Gdx.audio.newSound(Gdx.files.internal("general.wav"));
        general=Gdx.audio.newSound(Gdx.files.internal("general.wav"));

        flappymusic=Gdx.audio.newMusic(Gdx.files.internal("flappymusic.mp3"));
        flappymusic.setLooping(true);
        flappymusic.play();



		TextureRegion[][] tmp;
		tmp = TextureRegion.split(new Texture("flappy.png"), 136, 92);
		int index = 0;
		animatedFrame = new TextureRegion[2];
		for (int i = 0; i < 2; i++) {
			animatedFrame[index++] = tmp[0][i];
		}

		animation=new Animation(0.15f,animatedFrame);
	}

	public void startGame(){
        birdY=Gdx.graphics.getHeight()/2-bird.getHeight()/2;
        for (int i=0;i<numberoftubes;i++){
            tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-700);
            tubeX[i]=Gdx.graphics.getWidth()/2-bottomtube.getWidth()/2+i*distancebetweentubes+Gdx.graphics.getWidth();
            toptubeRectangle[i]=new Rectangle();
            bottomtubeRectangle[i]=new Rectangle();




        }
    }

    public void blinkImage(Image image){

        batch.end();
        batchextra.begin();
        image.act(Gdx.graphics.getDeltaTime());
        image.draw(batchextra,1);
        batchextra.end();
        batch.begin();

    }

    public void showhighscore(){

        glyphLayoutHighScore.setText(highscorecard,"HIGH SCORE: "+String.valueOf(highScore));
        highscorecard.draw(batch,glyphLayoutHighScore,Gdx.graphics.getWidth()/2-glyphLayoutHighScore.width/2,Gdx.graphics.getHeight()*3/4);
    }

	@Override
	public void render () {

		batch.begin();
       // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());




		if (gameState==1) {

		    flappymusic.setVolume(0.35f);


		    if (tubeX[scoringTube]<Gdx.graphics.getWidth()/2){
		        score++;
		        point.play(1.0f);

		        if (scoringTube<numberoftubes-1){
		            scoringTube++;
                }else {
		            scoringTube=0;
                }


            }


            highScore=highscorepref.getInteger("highscore");
            if (score>highScore){
		        highscorepref.putInteger("highscore",score);
		        highscorepref.flush();
            }





			if (Gdx.input.justTouched()) {
				velocity = -25;
				wing.play(0.5f);

			}
			for (int i=0;i<numberoftubes;i++){
				if (tubeX[i]<-toptube.getWidth()){
					tubeX[i]+=numberoftubes*distancebetweentubes;
                    tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-700);





				}else {
					tubeX[i]-=tubeVelocity;

				}

				bottomtubeRectangle[i]=new Rectangle(tubeX[i]+8,Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight()+tubeOffset[i]-6,bottomtube.getWidth()-16,bottomtube.getHeight());
				toptubeRectangle[i]=new Rectangle(tubeX[i]+8,Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]+6,toptube.getWidth()-16,toptube.getHeight());


				batch.draw(bottomtube,tubeX[i],Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight()+tubeOffset[i]);
				batch.draw(toptube,tubeX[i],Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]);
			}




			if (birdY > 10 && birdY<(Gdx.graphics.getHeight()+80)) {

		            if (velocitycounter==1) {
		                general.play(0.5f);
		                velocitycounter=0;
                    }

				velocity += gravity;
				birdY -= velocity;
			}else {
		        gameState=2;
            }
		}
		else if (gameState==0){
            flappymusic.setVolume(1f);

		    blinkImage(touchstart);
		    touchstart.setPosition(Gdx.graphics.getWidth()/2-touchstarttexture.getWidth()/2,Gdx.graphics.getHeight()/4);
            highScore=highscorepref.getInteger("highscore");
		    showhighscore();


			if (Gdx.input.justTouched()){
				gameState=1;
			}
		}
		else if (gameState==2){
            flappymusic.setVolume(0.5f);
		    if (diecounter==1){
		        die.play(1.0f);
		        diecounter=0;

            }
            highScore=highscorepref.getInteger("highscore");
            if(highScore>score) {
                showhighscore();
            }
            else {
                newhigh.setPosition(Gdx.graphics.getWidth()/2-newhigh.getWidth()/2,Gdx.graphics.getHeight()/2+gameover.getHeight()/2);
                blinkImage(newhigh);

            }
            glyphLayoutScore.setText(scorecard,"YOUR SCORE: "+String.valueOf(score));
            scorecard.draw(batch,glyphLayoutScore,Gdx.graphics.getWidth()/2-glyphLayoutScore.width/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);


		    batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);


            blinkImage(touchstart);
            touchstart.setPosition(Gdx.graphics.getWidth()/2-touchstarttexture.getWidth()/2,Gdx.graphics.getHeight()/8);


		    //batch.draw(newhigh,Gdx.graphics.getWidth()/2-newhigh.getWidth()/2,Gdx.graphics.getHeight()/2+gameover.getHeight()/2);

		    velocity+=gravity;
		    birdY-=velocity;
            if (Gdx.input.justTouched()){
                diecounter=1;
                velocitycounter=1;
                gameState=1;
                startGame();
                velocity=0;
                score=0;
                scoringTube=0;
            }

        }



		elapsedTime+=Gdx.graphics.getDeltaTime();

		batch.draw((TextureRegion) animation.getKeyFrame(elapsedTime,true),Gdx.graphics.getWidth()/2-bird.getWidth()/2,birdY);

		if (gameState==1) {

            font.draw(batch, String.valueOf(score), 100, 200);
        }

		batch.end();

		//shapeRenderer.setColor(Color.GREEN);
		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+bird.getHeight()/2,(bird.getWidth()/2)-10);
		//shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);
		for (int i=0;i<numberoftubes;i++){
		  //  shapeRenderer.rect(tubeX[i]+6,Gdx.graphics.getHeight()/2-gap/2-bottomtube.getHeight()+tubeOffset[i]-8,bottomtube.getWidth()-16,bottomtube.getHeight());
		  // shapeRenderer.rect(tubeX[i]+6,Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i]+8,toptube.getWidth()-16,toptube.getHeight());

		    if (Intersector.overlaps(birdCircle,toptubeRectangle[i])||Intersector.overlaps(birdCircle,bottomtubeRectangle[i])){
		        if (gameState==1) {

		            hit.play(1.0f);

                    Gdx.input.vibrate(150);
                }
                gameState=2;

            }


        }
		//shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
		bottomtube.dispose();
		toptube.dispose();
		gameover.dispose();
		hit.dispose();
		point.dispose();
		newhightexture.dispose();
		touchstarttexture.dispose();

	}
}
