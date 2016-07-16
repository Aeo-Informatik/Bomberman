/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.entity.player;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gdx.bomberman.Constants;
import gui.AnimEffects;
import gui.AudioManager;
import gui.TextureManager;
import gui.entity.EntityManager;
import gui.map.MapManager;

/**
 *
 * @author qubasa
 */
public class EnemyPlayer extends Player
{
    //Objects
    private AnimEffects animEffects = new AnimEffects();
    
    //Variables
    private String lastMovementKeyPressed = "UP";
    private boolean godmode = false;
    private float godModeTimer = 0;
    private int playerId = 0;
    private boolean isEnemyPlayerDead = false;
    
    //Server render variables
    private boolean executeMovePlayer = false;
    private boolean executeStopPlayer = true;
    private String moveDirection = "";
    private float stopX = pos.x; 
    private float stopY = pos.y;
    
    //Player animation when he is moving around
    private final Animation walkAnimUp;
    private final Animation walkAnimDown;
    private final Animation walkAnimRight;
    private final Animation walkAnimLeft;
    
    //Player settings
    private float godModeDuration = 2f;
    private int life = 3;
    private int bombRange = 2;
    private int coins = 0;
    private int maxBombPlacing = 2;
    
    //Constructor
    public EnemyPlayer(Vector2 pos, Vector2 direction, int playerId, MapManager map, EntityManager entityManager, OrthographicCamera camera) 
    {
        super(pos, direction, map, entityManager, camera);
        
        //Save variables & objects as global
        this.playerId = playerId;
        
        //Set player id textures
        switch(playerId)
        {
            case 1:
                this.walkAnimUp = TextureManager.p1WalkingUpAnim;
                this.walkAnimDown = TextureManager.p1WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p1WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p1WalkingRightAnim;
                break;

            case 2:
                this.walkAnimUp = TextureManager.p2WalkingUpAnim;
                this.walkAnimDown = TextureManager.p2WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p2WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p2WalkingRightAnim;
                break;

            case 3:
                this.walkAnimUp = TextureManager.p3WalkingUpAnim;
                this.walkAnimDown = TextureManager.p3WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p3WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p3WalkingRightAnim;
                break;

            case 4:
                this.walkAnimUp = TextureManager.p4WalkingUpAnim;
                this.walkAnimDown = TextureManager.p4WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p4WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p4WalkingRightAnim;
                break;
                
            default:
                System.err.println("ERROR: Wrong player number: " + playerId + " in EnemyPlayer. Using default p1");
                this.walkAnimUp = TextureManager.p1WalkingUpAnim;
                this.walkAnimDown = TextureManager.p1WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p1WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p1WalkingRightAnim;
        }
    }

    
    @Override
    public void render()
    {
        renderObject.setProjectionMatrix(camera.combined);
        renderObject.begin();
        
            //System.out.println("Enemy player is existing");
            if(this.executeStopPlayer)
            {
                stopPlayer(this.stopX, this.stopY, renderObject);
            }

            if(this.executeMovePlayer)
            {
                movePlayer(this.moveDirection, renderObject);
            }

            hitByBomb(renderObject);
            
        renderObject.end();
    }
    
    
    //Execute on player death
    public void onDeath(int cellX, int cellY)
    {
        System.out.println("---------------------Player " + playerId + " died!-------------------");
        
        //Spawn tomb stone
        entityManager.getItemManager().spawnTombstone(cellX, cellY, coins, playerId);
        
        //Set death to true to delete object in entity manager 
        isEnemyPlayerDead = true;
    }
    
    
        /**
     * Player gets hit by bomb
     */
    Thread flashThread;
    long id = -1;
    public void hitByBomb(SpriteBatch renderObject) 
    {  
        //If player touches explosion
        if(touchesDeadlyBlock() && godmode == false)
        {
            godmode = true;

            if(id == -1)
            {
                id = AudioManager.hit.play();
                AudioManager.hit.setVolume(id, Constants.SOUNDVOLUME);
            }
            
            if(Constants.CLIENTDEBUG)
            {
                System.out.println("Enemy " + playerId + ": Invulnerability activated");
            }

            //Lets the player flash and saves the thread object to be able to stop it manually
            flashThread = animEffects.flashing(godModeDuration, 3, renderObject);
        }
        
        //Timer for godmode length after hit
        if(godModeTimer < godModeDuration && godmode == true)
        {
            godModeTimer += Constants.DELTATIME;
            //System.out.println("Increasing timer by: " + godModeTimer);
        }else if(godmode == true)
        {
            godmode = false;
            godModeTimer = 0;
            id = -1;
            
            //Stops the blinkAnimation thread, it is more precise than using only the godModeDuration
            flashThread.interrupt();
            
            if(Constants.CLIENTDEBUG)
            {
                System.out.println("Enemy " + playerId + ": Invulnerability deactivated");
            }
        }
    }
    
    /**
     * Sets bomb on given position
     * @param pos
     * @param direction
     * @param bombType 
     */
    public void placeBomb(Vector2 pos, String bombType)
    {
        //Vector2 pos, Vector2 direction, MapManager map, int playerId, int range,  EntityManager entityManager)
        entityManager.getBombManager().spawnNormalBomb(pos, playerId, bombRange);
    }
    
    /**
     * Moves the player accordingly to the direction specified.
     * @param movement 
     */
    public void movePlayer(String movement, SpriteBatch renderObject)
    {
        //If triggered from outside it will be rendered/activated in the object till stopPlayer gets called
        this.moveDirection = movement;
        this.executeMovePlayer = true;
        this.executeStopPlayer = false;
        
        //Changes the position of the texture by adding the number of pixels 
        //in which the player should go to the position
        pos.add(this.direction);
        
        switch(this.moveDirection)
        {
            case "LEFT":
                if(!collidesLeft() && !collidesLeftBomb())
                {
                    //Set the speed the texture moves in x and y axis
                    //this is the method inherited from Entity.java class
                    goLeft();

                    //Draw the walking animation
                    renderObject.draw(animEffects.getFrame(walkAnimLeft), pos.x, pos.y);

                    //Sets the direction in which the still standing image will be rendered
                    lastMovementKeyPressed = "LEFT";
                }else
                {
                    //Stop player
                    stopMoving();
                    renderObject.draw(animEffects.getFrame(walkAnimLeft), pos.x, pos.y);
                    lastMovementKeyPressed = "LEFT";
                }
            break;

            case "RIGHT":
                if(!collidesRight() && !collidesRightBomb())
                {
                    goRight();
                    renderObject.draw(animEffects.getFrame(walkAnimRight), pos.x, pos.y);
                    lastMovementKeyPressed = "RIGHT";
                }else
                {
                    //Stop player
                    stopMoving();
                    renderObject.draw(animEffects.getFrame(walkAnimRight), pos.x, pos.y);
                    lastMovementKeyPressed = "RIGHT";
                }
            break;

            case "UP":
                if(!collidesTop() && !collidesTopBomb())
                {
                    goUp();
                    renderObject.draw(animEffects.getFrame(walkAnimUp), pos.x, pos.y);
                    lastMovementKeyPressed = "UP";
                }else
                {
                    //Stop player
                    stopMoving();
                    renderObject.draw(animEffects.getFrame(walkAnimUp), pos.x, pos.y);
                    lastMovementKeyPressed = "UP";
                }
            break;

            case "DOWN":
                if(!collidesBottom() && !collidesBottomBomb())
                {
                    goDown();
                    renderObject.draw(animEffects.getFrame(walkAnimDown), pos.x, pos.y);
                    lastMovementKeyPressed = "DOWN";
                }else
                {
                    //Stop player
                    stopMoving();
                    renderObject.draw(animEffects.getFrame(walkAnimDown), pos.x, pos.y);
                    lastMovementKeyPressed = "DOWN";
                }
            break;
           
        }
    }
    
    
    /**
     * Stops the player movements and render him on given positions
     * @param x int
     * @param y int
     */
    public void stopPlayer(float x, float y, SpriteBatch renderObject)
    {
        //If triggered from outside it will be rendered/activated in the object till movePlayer gets called
        this.executeStopPlayer = true;
        this.executeMovePlayer = false;
        this.stopX = x;
        this.stopY = y;
        
        //Sets the texture to no movement
        stopMoving();
            
        //Sets the positon where to draw the player.
        pos.set(x, y);
        
        //Draws the player if he stands still
        switch(lastMovementKeyPressed)
        {
            case "LEFT":
                renderObject.draw(walkAnimLeft.getKeyFrame(0), pos.x, pos.y);
                break;

            case "RIGHT":
                renderObject.draw(walkAnimRight.getKeyFrame(0), pos.x, pos.y);
            break;

            case "UP":
                renderObject.draw(walkAnimUp.getKeyFrame(0), pos.x, pos.y);
                break;

            case "DOWN":
                renderObject.draw(walkAnimDown.getKeyFrame(0), pos.x, pos.y);
                break;
        } 
    }

    
    /**--------------------GETTER & SETTER--------------------**/
    public boolean isEnemyDead()
    {
        return this.isEnemyPlayerDead;
    }
    
    public int getPlayerId()
    {
        return this.playerId;
    }
    
    public void setPlayerId(int playerId)
    {
        this.playerId = playerId;
    }
    
    public int getLife()
    {
        return this.life;
    }
    
    public void setLife(int life)
    {
        this.life = life;
    }
    
    public int getBombRange()
    {
        return this.bombRange;
    }
    
    public void setBombRange(int bombRange)
    {
        this.bombRange = bombRange;
    }
    
    public int getCoins()
    {
        return this.coins;
    }
    
    public void setCoins(int coins)
    {
        this.coins = coins;
    }
    
    public int getMaxBombPlacing() 
    {
        return maxBombPlacing;
    }

    public void setMaxBombPlacing(int maxBombPlacing) 
    {
        this.maxBombPlacing = maxBombPlacing;
    }
}