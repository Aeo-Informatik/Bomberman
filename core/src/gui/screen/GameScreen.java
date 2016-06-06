/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import static com.gdx.bomberman.Main.client;
import static com.gdx.bomberman.Main.sb;
import gui.Constants;
import gui.camera.OrthoCamera;
import gui.entity.EntityManager;
import gui.map.MapManager;
import networkClient.Client;
import networkClient.ProcessData;
import networkServer.ServerStart;


/**
 *
 * @author qubasa
 */
public class GameScreen implements Screen{
    
    //Objects 
    private OrthoCamera camera;
    private EntityManager entityManager;
    private MapManager mapManager;
    private ProcessData processData;
    private Game game;
    
    
    /**------------------------CONSTRUCTOR------------------------**/
    public GameScreen(Game game)
    {
        this.game = game;
        this.camera = new OrthoCamera();
        this.mapManager = new MapManager(camera);
        this.entityManager = new EntityManager(camera, mapManager);
        this.processData = new ProcessData(entityManager);
        
        //Starts local server for 1 Player
        if(Constants.LOCALSERVER)
            new Thread(new ServerStart()).start();

        //Connect to server
        try 
        {
            client = new Client(Constants.CLIENTHOST, Constants.CLIENTPORT);
            client.receiveData();
            
        } catch (Exception e) 
        {
            System.err.println("ERROR: Client couldn't connect to server " + e);
            Gdx.app.exit();
        }
    }
    
    
    /**------------------------RENDER------------------------**/
    @Override
    public void render(float f)
    {
        //Takes the matrix and everything containing in it will be rendered. 
        //The exact functionality is really complex with lots of math.
        sb.setProjectionMatrix(camera.combined);
        
        //Map loading into screen
        mapManager.render(sb);
        
        sb.begin();
        
        //Render entities
        entityManager.render(sb);
        
        //Render incoming server instructions
        processData.start();
        
        sb.end();
        
        camera.update();
        entityManager.update();
    }
    
    
    /**------------------------RESIZE------------------------**/
    @Override
    public void resize(int width, int height) 
    {
       camera.resize();
       mapManager.resize(width, height);
       
       //If screen gets resized set camera to player position
       if(Constants.PLAYERSPAWNED)
       {
           if(entityManager.getMainPlayer() != null)
           {
                camera.setPosition(entityManager.getMainPlayer().getPosition().x, entityManager.getMainPlayer().getPosition().y);
           
           }else if(entityManager.getSpectator() != null)
           {
               camera.setPosition(entityManager.getSpectator().getPosition().x, entityManager.getSpectator().getPosition().y);
           }
       }
    }

    /**------------------------DISPOSE------------------------**/
    @Override
    public void dispose() 
    {
        mapManager.dispose();
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
    
}
