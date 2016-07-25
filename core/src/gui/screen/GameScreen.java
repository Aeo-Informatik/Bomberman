/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.gdx.bomberman.Constants;
import gui.entity.EntityManager;
import gui.map.MapManager;
import client.ClientProcessData;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import static com.gdx.bomberman.Main.client;
import static com.gdx.bomberman.Main.game;
import gui.AudioManager;
import gui.hud.LifeHud;
import java.io.IOException;
import java.util.Random;


/**
 *
 * @author qubasa
 */
public class GameScreen implements Screen{
    
    //Objects 
    private OrthographicCamera camera;
    private EntityManager entityManager;
    private MapManager mapManager;
    private ClientProcessData processData;
    private SpriteBatch renderServer = new SpriteBatch();
    private Random random = new Random();
    private Stage stage;
    
    //Variables
    private int previousMusicIndex = -1;
    private float musicTimer = 0;
    private float musicStart = 15; //Seconds after game start or after music is finished
    
    //Main Player HUD
    private LifeHud counterHud;
    
    /**
     * Constructor
     * @param game 
     */
    public GameScreen()
    {
        this.camera = new OrthographicCamera();
        this.stage = new Stage(new StretchViewport(Constants.SCREENWIDTH, Constants.SCREENHEIGHT, camera));
        this.mapManager = new MapManager(camera);
        this.entityManager = new EntityManager(camera, mapManager);
        this.processData = new ClientProcessData(entityManager, mapManager);
        this.counterHud = new LifeHud(entityManager);
        
        this.camera.zoom = Constants.DEFAULTZOOM;
    }
    
    
    /**
     * Render the game
     * @param f: Don't know what it means
     */
    @Override
    public void render(float f)
    {
        //Set bacckground color
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        
        //Play next music title after last one finished and make sure the next one is unequal the last one
        if((AudioManager.currentIngameMusic == null || !AudioManager.currentIngameMusic.isPlaying()) && musicStart < musicTimer)
        {
            int nextMusic = random.nextInt(6);
            while(nextMusic == previousMusicIndex)
            {
                nextMusic = random.nextInt(6);
            }
            
            previousMusicIndex = nextMusic;
            
            AudioManager.currentIngameMusic = AudioManager.nextIngameMusic(nextMusic);
            AudioManager.currentIngameMusic.play();
            AudioManager.currentIngameMusic.setVolume(Constants.MUSICVOLUME);
            
            musicTimer = 0;
        }else if ((AudioManager.currentIngameMusic == null || !AudioManager.currentIngameMusic.isPlaying()))
        {
            musicTimer += Gdx.graphics.getDeltaTime();
        }
        
        //Check if client is connected to server
        if(client != null)
        {
            //If client has been disconnected from server
            if(!client.isConnectedToServer())
            {
                System.err.println("CLIENT: Connection lost to server.");
                game.setScreen(new MenuScreen());
            }
            
        }else //If error occured on creating connection to server
        {
            game.setScreen(new MenuScreen());
        }
        

        //Lets spriteBatch use the coordinate system specified by camera instead of the default one. This is because 
        //both of the coordinate systems are different and the camera.combined will do the maths for you.
        renderServer.setProjectionMatrix(camera.combined);
        
        //Draw stage
        stage.act(Constants.DELTATIME);
        stage.draw();
        
        mapManager.render();
           
        //Render incoming server instructions
        renderServer.begin();
            processData.start(renderServer);
        renderServer.end();
        
        //Render entities
        entityManager.render();
        entityManager.update();
        
        //Render LifeHud
        counterHud.stage.draw();
        counterHud.update();

        //Update camera
        camera.update();
        
        
        /*------------------QUIT GAME------------------*/
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
        {
            try 
            {
                //Close connection to server
                client.closeConnection();
                
            } catch (IOException ex) 
            {
                System.err.println("Unexpected exception in ESC Quit game in mainplayer on closing connetion with server.");
            }
            
            //Go to menuscreen
            game.setScreen(new MenuScreen());
        }
        
        /*------------------SWITCH TO FULLSCREEN AND BACK------------------*/
        if(Gdx.input.isKeyPressed(Input.Keys.F12))
        {
            if(Gdx.graphics.isFullscreen())
            {
                Gdx.graphics.setWindowedMode(Constants.SCREENWIDTH, Constants.SCREENHEIGHT);
            }else
            {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    }
    
    
    /**
     * Execute on window resize
     * @param width
     * @param height 
     */
    @Override
    public void resize(int width, int height) 
    {
       stage.getViewport().update(width, height, false);
       mapManager.resize(width, height);
       counterHud.resize(width, height);
       
       //If screen gets resized set camera to player position
       if(Constants.PLAYERSPAWNED)
       {
           if(entityManager.getPlayerManager().getMainPlayer() != null)
           {
                camera.position.set(entityManager.getPlayerManager().getMainPlayer().getPosition().x, entityManager.getPlayerManager().getMainPlayer().getPosition().y, 0);
           
           }else if(entityManager.getPlayerManager().getSpectator() != null)
           {
               camera.position.set(entityManager.getPlayerManager().getSpectator().getPosition().x, entityManager.getPlayerManager().getSpectator().getPosition().y, 0);
           }
       }
    }

    
    /**------------------------DISPOSE------------------------**/
    @Override
    public void dispose() 
    {
        mapManager.dispose();
        stage.dispose();
    }
    
    
    /**------------------------OTHER------------------------**/
    @Override
    public void pause() 
    {
    }

    
    @Override
    public void resume() 
    {
    }

    @Override
    public void show() {
        
    }

    @Override
    public void hide() 
    {
        
    }
 
    
    /**------------------------GETTER & SETTER-----------------------**/    
}
