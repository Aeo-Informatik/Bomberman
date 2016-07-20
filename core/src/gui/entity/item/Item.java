/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.entity.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import gui.TextureManager;
import gui.entity.Entity;
import gui.entity.EntityManager;
import gui.map.MapManager;
import com.gdx.bomberman.Constants;
/**
 *
 * @author cb0703
 */
public class Item extends Entity
{
    
    //Variables
    protected boolean collected = false;
    protected TextureRegion emptyBlock;
    protected int cellX, cellY;
    
    protected float itemDespawnTime = 30; //Seconds
    protected float itemDespawnTimer = 0;
    
    protected float itemUndestroyableTime = 1f; //Seconds
    protected float itemUndestroyableTimer = 0;
    
    
    //Constructor
    public Item(int cellX, int cellY, TextureRegion itemTexture, MapManager map, EntityManager entityManager) 
    {
        super(new Vector2(cellX, cellY), new Vector2(0,0), map, entityManager);
        
        this.emptyBlock = TextureManager.emptyBlock;
        this.cellX = cellX;
        this.cellY = cellY;
        
        //Render Item once
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(itemTexture));
        map.getItemLayer().setCell(cellX, cellY, cell);
    }
    
    
    @Override
    public void render()
    {
        deleteItemThroughBomb();
        
        if(entityManager.getPlayerManager().getMainPlayer() != null)
        {
            //Check if mainPlayer has reached item max 
            if(canGetCollectedByMainPlayer() == true)
            {
                if(isMainPlayerCollectingItem() == true)
                {
                    itemEffect();
                }
            }
        }else //To make it possible for other players to despawn an item even after main player death
        {
            getPlayerIdCollectingItem();
        }
    }
    
    /**
     * Execute in render to let items despawn after some time.
     */
    protected void itemDespawnTimer()
    {
        if(itemDespawnTimer > itemDespawnTime)
        {
            //Delte item
            collected = true;
            
        }else
        {
            itemDespawnTimer += Constants.DELTATIME;
        }
    }
    
    /**
     * Check if a player is on this item field.
     * @return playerId or -1
     */
    protected int getPlayerIdCollectingItem()
    {
        if(entityManager.getPlayerManager().getPlayerIdOnCoordinates(cellX, cellY) != -1)
        {
            collected = true;
            return entityManager.getPlayerManager().getPlayerIdOnCoordinates(cellX, cellY);
        }
        return -1;
    }
    
    
    /**
     * Check if id is from main player.
     * @param ID player id
     * @return true or false
     */
    protected boolean isMainPlayerCollectingItem()
    {
        if(entityManager.getPlayerManager().getMainPlayer() != null)
        {
            if(entityManager.getPlayerManager().getMainPlayer().getPlayerId() == getPlayerIdCollectingItem())
            {
                return true;
            }
        }
        return false;
    }

    
    /**
     * Deletes item texture in cell.
     */
    public void deleteItem()
    {
        TiledMapTileLayer.Cell cellCenter = new TiledMapTileLayer.Cell();
        cellCenter.setTile(new StaticTiledMapTile(emptyBlock));
        map.getItemLayer().setCell(cellX, cellY, cellCenter);
        collected = true;
    }
    
    /**
     * delete item if it is hit by a bomb
     */
    public void deleteItemThroughBomb()
    {
        //Make the item after spawn for some time invulnerable to bombs to prevent instant delete
        if(itemUndestroyableTimer > itemUndestroyableTime)
        {
            //Check if item has been hit by deadly tile
            if(map.isCellDeadly(cellX * Constants.MAPTEXTUREWIDTH, cellY * Constants.MAPTEXTUREHEIGHT))
            {
                collected = true;
            }
            
        }else
        {
            System.out.println("Item undestroyable");
            itemUndestroyableTimer += Constants.DELTATIME;
        }
    }
    
    /**
     * check if item is usable for the player
     * @return true if it is collectable
     */
    public boolean canGetCollectedByMainPlayer()
    {
        return true;
    }
    
    /**
     * the effect of the item
     */
    public void itemEffect()
    {
        
    }

    
    /**--------------------GETTER & SETTER--------------------**/
    public boolean isCollected() 
    {
        return collected;
    }

    public void setCollected(boolean collected)
    {
        this.collected = collected;
    }
    
    public int getCellX() {
        return cellX;
    }

    public void setCellX(int cellX) {
        this.cellX = cellX;
    }

    public int getCellY() {
        return cellY;
    }

    public void setCellY(int cellY) {
        this.cellY = cellY;
    }
    
    
    
    
}
