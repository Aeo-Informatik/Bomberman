/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.entity.bomb;

import com.badlogic.gdx.audio.Sound;
import com.gdx.bomberman.Constants;

import gui.AudioManager;
import gui.entity.EntityManager;
import gui.entity.player.MainPlayer;
import gui.map.MapCellCoordinates;
import gui.map.MapLoader;
import gui.map.ThinGridCoordinates;
import gui.screen.MainPlayerHud;

/**
 *
 * @author qubasa
 */
public class Teleport extends Bomb{

    //Sound
    private long soundId = -1;
    
    //Item
    private float teleportTimer = 0;
    private float teleportTimerEnd = 0.0f;
    
    //Objects
    MainPlayer mainP;
    
    public Teleport(ThinGridCoordinates pos, int playerId, MapLoader map, EntityManager entityManager) {
        super(pos, playerId, 0, 0, 0, 0, map, entityManager);
    }

    
    
    @Override
    protected void explode(Sound sound) 
    {
        for(int mapY=0; mapY < map.getFloorLayer().getHeight(); mapY++)
        {
            for(int mapX=0; mapX < map.getFloorLayer().getWidth(); mapX++)
            {
                try
                {
                    if(map.getFloorLayer().getCell(mapX, mapY).getTile().getProperties().containsKey("Spawn-P" + mainP.getPlayerId()))
                    {
                        //Execute sound
                        if(soundId == -1)
                        {
                            soundId = sound.play();
                            sound.setVolume(soundId, AudioManager.getSoundVolume() * 2);
                        }

                        mainP.setPosition(new ThinGridCoordinates(mapX, mapY));
                    }
                }catch(NullPointerException e)
                {

                }
            }
        }
        this.isExploded = true;
    }

    @Override
    public void render() 
    {
        mainP = entityManager.getPlayerManager().getMainPlayer();
        
        if(mainP != null)
        {
            if(new MapCellCoordinates(mainP.getPlaceItemLocation()).equalCoordinates(cellPos))
            {
                if(teleportTimer >= teleportTimerEnd)
                {
                    explode(AudioManager.getTeleport());
                }else
                {
                    teleportTimer += Constants.DELTATIME;
                    float printCountdown = teleportTimerEnd - teleportTimer;
                    if(printCountdown >= 0)
                    {
                        if(String.format("%.2f", printCountdown).equalsIgnoreCase("0,01"))
                            MainPlayerHud.printToScreen(("Don't move! : 0,00"));
                        else
                            MainPlayerHud.printToScreen(("Don't move! : " + String.format("%.2f", printCountdown)));
                    }
                }
            }else
            {
                MainPlayerHud.printToScreen("Teleportation aborted.");
                this.isExploded = true;
            }
        }else
        {
            this.isExploded = true;
        }
        
    }
    
}
