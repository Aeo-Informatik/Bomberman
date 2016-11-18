/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.entity.bomb;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.gdx.bomberman.Constants;
import gui.AudioManager;
import gui.entity.EntityManager;
import gui.map.MapLoader;

/**
 *
 * @author qubasa
 */
public class Horizontal extends Bomb
{
    
    public String Discription = "Only eplodes horizontal but has twice the range";    
    
    public Horizontal(Vector2 pos, Vector2 direction, int range, int playerId, MapLoader map, EntityManager entityManager) 
    {
        //Vector2 pos, Vector2 direction, int range, int explosionTime, float explosionDuration, 
        //float delayExplodeAfterHitByBomb, int playerId, MapLoader map, EntityManager entityManager
        super(pos, direction, range * 2, 2, 0.5f, 0.5f, playerId, map, entityManager);
        super.setExplosionCenter(super.getExplosionXMiddle());
    }
    
    @Override
    public void render()
    {
        this.cellX = (int) (pos.x / Constants.MAPTEXTUREWIDTH);
        this.cellY = (int) (pos.y / Constants.MAPTEXTUREHEIGHT);
        
        //To make sure no bomb gets placed into wall
        if(!map.isCellBlocked(pos.x, pos.y) && !isExploded)
        {
            //Check if bomb has been hit by deadly tile
            if(map.isCellDeadly(pos.x, pos.y) && hasBombTouchedDeadlyTile == false && timerTillExplosion < explosionTime)
            {
                //To delay the explosion after beeing hit from another bomb
                timerTillExplosion = explosionTime - delayExplodeAfterHitByBomb;

                hasBombTouchedDeadlyTile = true;
            }

            //If time to explode or deadly tile has been touched
            if(timerTillExplosion >= explosionTime)
            {
                explode();

                //Delete explosion effect after a while
                if(timerTillExplosionDelete >= explosionDuration)
                {
                    deleteExplosionEffect(0, 0, explosionRange, explosionRange);
                    
                    //Object gets delete only set if everything is done.
                    this.isExploded = true;
                }else
                {
                    //Add passed time to timer
                    timerTillExplosionDelete += Constants.DELTATIME;
                }

            }else if(!hasBombTouchedDeadlyTile)//Creates bomb animation
            {
                //Create new cell and set its animation texture
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(animEffects.getFrame(bombAnim)));
                cell.getTile().getProperties().put("bomb", null);

                //Set bomb into bomb layer
                map.getBombLayer().setCell(cellX, cellY, cell);
            }

            //Add passed time to timer
            timerTillExplosion += Constants.DELTATIME;
            
        }else //If bomb placed in wall
        {
            //Delete bomb object.
            this.isExploded = true;
        }
    }
    
    
    @Override
    protected void explode()
    {
        //To Execute the sound only once
        if(ExplodeAudioId == -1)
        {
            ExplodeAudioId = AudioManager.normalExplosion.play();
            AudioManager.normalExplosion.setVolume(ExplodeAudioId, Constants.SOUNDVOLUME);
        }
        
        //Create new cell and set texture
        TiledMapTileLayer.Cell cellCenter = new TiledMapTileLayer.Cell();
        cellCenter.setTile(new StaticTiledMapTile(explosionCenter));
        cellCenter.getTile().getProperties().put("deadly", null);
        
        //Explosion center, replaces bomb texture
        map.getBombLayer().setCell(cellX, cellY, cellCenter);
        
        
        //Explode RIGHT
        for(int x=1; x <= explosionRange; x++)
        {
            //If explosion hits block
            if(map.isCellBlocked((cellX +x) * Constants.MAPTEXTUREWIDTH, cellY * Constants.MAPTEXTUREHEIGHT))
            {
                //Set ending texture and break out of loop
                TiledMapTileLayer.Cell cellDown = new TiledMapTileLayer.Cell();
                cellDown.setTile(new StaticTiledMapTile(explosionRightEnd));
                cellDown.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX +x, cellY, cellDown);
                break;
            }
            
            if(x != explosionRange)  // If not end of explosion
            {
                //Set cell with middle explosion texture
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(explosionXMiddle));
                cell.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX + x, cellY, cell);
                
            }else
            {
                //Set end of explosion
                TiledMapTileLayer.Cell cellRight = new TiledMapTileLayer.Cell();
                cellRight.setTile(new StaticTiledMapTile(explosionRightEnd));
                cellRight.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX + x, cellY, cellRight);
            }
        }
        
        //Explode LEFT
        for(int x=1; x <= explosionRange; x++)
        {
            //If explosion hits block
            if(map.isCellBlocked((cellX -x) * Constants.MAPTEXTUREWIDTH, cellY * Constants.MAPTEXTUREHEIGHT))
            {
                //Set ending texture and break out of loop
                TiledMapTileLayer.Cell cellDown = new TiledMapTileLayer.Cell();
                cellDown.setTile(new StaticTiledMapTile(explosionLeftEnd));
                cellDown.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX -x, cellY, cellDown);
                break;
            }
            
            if(x != explosionRange)  // If not end of explosion
            {
                //Set cell with middle explosion texture
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(explosionXMiddle));
                cell.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX - x, cellY, cell);
                
            }else
            {
                TiledMapTileLayer.Cell cellLeft = new TiledMapTileLayer.Cell();
                cellLeft.setTile(new StaticTiledMapTile(explosionLeftEnd));
                cellLeft.getTile().getProperties().put("deadly", null);
                
                map.getBombLayer().setCell(cellX - x, cellY, cellLeft);
            }
        }
    }
}