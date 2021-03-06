/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui.entity.bomb;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.gdx.bomberman.Constants;

import gui.TextureManager;
import gui.entity.EntityManager;
import gui.entity.player.Player;
import gui.map.MapCellCoordinates;
import gui.map.MapLoader;
import gui.map.ThinGridCoordinates;

/**
 *
 * @author cb0703
 */
public abstract class CubicBomb extends Bomb{
    
    protected TextureRegion explosionCubic;
    protected int cubicRange;
    
    
    public CubicBomb(ThinGridCoordinates pos, int playerId, int cubicRange, float explosionTime, float explosionDuration, float delayExplodeAfterHitByBomb, MapLoader map, EntityManager entityManager) {
        super(pos, playerId, 0, explosionTime, explosionDuration, delayExplodeAfterHitByBomb, map, entityManager);
        this.cubicRange = cubicRange;
        setBombTexture();
        
    }
   
    @Override
    public abstract void render();
    
    private void setBombTexture()
    {
        switch(playerId)
        {
            case 0:
                this.bombAnim = TextureManager.p1NormalBombAnim;
                this.explosionCubic = TextureManager.p1ExplosionCubic;
                break;
            case 1:
                this.bombAnim = TextureManager.p1NormalBombAnim;
                this.explosionCubic = TextureManager.p1ExplosionCubic;
                break;
            case 2:
                this.bombAnim = TextureManager.p2NormalBombAnim;
                this.explosionCubic = TextureManager.p2ExplosionCubic;
                break;
            case 3:
                this.bombAnim = TextureManager.p3NormalBombAnim;
                this.explosionCubic = TextureManager.p3ExplosionCubic;
                break;
            case 4:
                this.bombAnim = TextureManager.p4NormalBombAnim;
                this.explosionCubic = TextureManager.p4ExplosionCubic;
                break;
            default:
                System.err.println("ERROR: Wrong playerId in bomb defined " + playerId + " using default p1 textures.");
                this.bombAnim = TextureManager.p1NormalBombAnim;
                this.explosionCubic = TextureManager.p1ExplosionCubic;
        }
    }
          
    @Override
    public void explode(Sound sound){
        //cubic explosion for barrel and dynamite
        //own constant cubic explosion range [1,2,3]
        //dynamite max 2
        
        
        //To Execute the sound only once
        if(ExplodeAudioId == -1 && sound != null)
        {
            ExplodeAudioId = sound.play();
        }
        
        Player player = entityManager.getPlayerManager().getCurrentPlayerObject();
        if(player != null && sound != null)
        {
            player.playSoundInDistance(sound, ExplodeAudioId, pos, Constants.CUBICBOMBEXPLOSIONMOD);
        }
        
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(explosionCubic));
        cell.getTile().getProperties().put("deadly", null);
        

        
        
        
        for (int i = super.cellPos.getX() - cubicRange; i <= super.cellPos.getX() + cubicRange; i++){
            for (int j = super.cellPos.getY() - cubicRange; j <= super.cellPos.getY() + cubicRange; j++){
                map.getBombLayer().setCell(i, j, cell);
            }
        }
        
    }
    
    
    protected void deleteCubicExplosionEffect()
    {
       
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(emptyBlock));
        
        for (int i = super.cellPos.getX() - cubicRange; i <= super.cellPos.getX() + cubicRange; i++){
            for (int j = super.cellPos.getY() - cubicRange; j <= super.cellPos.getY() + cubicRange; j++){
                
                if(map.isCellBlocked(new MapCellCoordinates(i, j)))
                {
                    
                    //Delete block
                    deleteBlock(new MapCellCoordinates(i, j));

                }
                map.getBombLayer().setCell(i, j, cell);

            }
        }
    
    }

    public void setCubicRange(int cubicRange) {
        this.cubicRange = cubicRange;
    }

    public int getCubicRange() {
        return cubicRange;
    }
    
    
}
