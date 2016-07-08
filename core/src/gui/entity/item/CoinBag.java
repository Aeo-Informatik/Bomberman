/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.entity.item;


import com.badlogic.gdx.math.Vector2;
import com.gdx.bomberman.Constants;
import static com.gdx.bomberman.Main.client;
import gui.TextureManager;
import gui.entity.EntityManager;
import gui.map.MapManager;
import gui.AudioManager;
import gui.entity.MainPlayer;


/**
 *
 * @author Christian
 */
public class CoinBag extends Item{
    
    private int value;

    public CoinBag(int cellX, int cellY, MapManager map, EntityManager entityManager, int value) {
        super(cellX, cellY,TextureManager.coinBag, map, entityManager);
        this.value = value;
    }
   
    @Override
    public void render()
    {
        if(isMainPlayerCollectingItem() == true)
        {
            itemEffect();
        }
        
        if(getPlayerIdCollectingItem() != -1)
        {
            long id = AudioManager.singleCoin.play();
            AudioManager.singleCoin.setVolume(id, Constants.SOUNDVOLUME);
        }
    }
    
    @Override
    public void itemEffect()
    {
        MainPlayer mainP = entityManager.getMainPlayer();
        
        //Check if main player is alive
        if(mainP != null)
        {
            mainP.setCoins((mainP.getCoins() + value)); 
            client.sendData("enemyPlayerSetCoins|" + mainP.getPlayerId() + "|" + (mainP.getCoins()) + "|*");
        }
    }
    

    
}
