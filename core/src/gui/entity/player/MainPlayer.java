/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import com.gdx.bomberman.Constants;
import static com.gdx.bomberman.Main.client;
import gui.AnimEffects;
import gui.AudioManager;
import gui.TextureManager;
import gui.entity.EntityManager;
import gui.map.MapManager;

/**
 *
 * @author qubasa
 */
public class MainPlayer extends Player
{
    
    //Variables DO NOT CHANGE
    private String lastMovementKeyPressed = "UP";
    private boolean sendStopOnce = true;
    private String sendMoveOnce = "";
    private float godModeTimer = 0;
    private float sendStopTimer = 0;
    private boolean godMode = false;
    private int playerId = 0;
    private int chosenBomb = 1;
    
    //Objects
    private AnimEffects animEffects = new AnimEffects();
    
    //Player animation when he is moving around
    private final Animation walkAnimUp;
    private final Animation walkAnimDown;
    private final Animation walkAnimRight;
    private final Animation walkAnimLeft;
    
    //BombCosts
    private int bomb_0 = 250;
    private int bomb_1 = 0;
    private int bomb_2 = 5;
    private int bomb_3 = 75;
    private int bomb_4 = 100;
    private int bomb_5;
    private int bomb_6;
    private int bomb_7;
    private int bomb_8;
    private int bomb_9;
    
    
    //Player settings CAN BE CHANGED
    private float sendStopTime = 2f;
    private int life = 3;
    private float godModeDuration = 2f; // How long the player is invulnerable after beeing hit by a bomb
    private int coins = 0;
    private int maxBombPlacing = 2;
    private int bombRange = 2;
    private float maxZoomOut = 1.5f;
    private float maxZoomIn = 0.5f;
    
    //Constructor
    public MainPlayer(Vector2 pos, Vector2 direction, int playerId, OrthographicCamera camera, MapManager map, EntityManager entityManager) 
    {
        super(pos, direction, map, entityManager, camera);

        //Set camera position to player
        camera.position.set(pos.x, pos.y, 0);
        
        //Object setter
        this.playerId = playerId;
        
        //Get apropriate player texture based on player id
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
                System.err.println("ERROR: Wrong player number in MainPlayer. Using default p1");
                this.walkAnimUp = TextureManager.p1WalkingUpAnim;
                this.walkAnimDown = TextureManager.p1WalkingDownAnim;
                this.walkAnimLeft = TextureManager.p1WalkingLeftAnim;
                this.walkAnimRight = TextureManager.p1WalkingRightAnim;
        }
    }


    /**
     * Draws the player to screen
     * @param sb 
     */
    @Override
    public void render()
    {
        renderObject.setProjectionMatrix(camera.combined);
        renderObject.begin();
            
            //Adding direction to position
            pos.add(this.direction);
            
            //Set the camera to follow the player
            cameraFollowPlayer(pos);
            
            //Move player actions
            inputMovePlayer();
            
            //Special actions
            inputDoPlayer();

            //Hit detection
            hitByBomb();
        renderObject.end();
    }
    
    
    /**
     * Execute on player death
     */
    public void onDeath()
    {
        System.out.println("YOU DIED!");
        
        int cellX = (int) (pos.x / Constants.MAPTEXTUREWIDTH);
        int cellY = (int) (pos.y / Constants.MAPTEXTUREHEIGHT);
        entityManager.getItemManager().spawnTombstone(cellX, cellY, coins, playerId);
        
        client.sendData("enemyPlayerDied|" + playerId + "|" + cellX + "|" + cellY + "|*");
    }
    

    /**
     * Player gets hit by bomb
     */
    Thread flashThread;
    long id = -1;
    public void hitByBomb() 
    {  
        //If player touches explosion
        if(touchesDeadlyBlock() && godMode == false)
        {
            
            //Reduce player life
            life -= 1;
            
            //Set invulnerability to true
            godMode = true;
            
            //Send playerLife command to server
            client.sendData("enemyPlayerLife|" + Constants.PLAYERID + "|" + life + "|*");
            
            System.out.println("Life has been reduced to: " + life);
          
            //Execute hit sound
            if(id == -1)
            {
                id = AudioManager.hit.play();
                AudioManager.hit.setVolume(id, Constants.SOUNDVOLUME);
            }
               
            //Debug
            if(Constants.CLIENTDEBUG)
            {
                System.out.println("Invulnerability activated");
            }

            //Lets the player flash and saves the thread object to be able to stop it manually
            flashThread = animEffects.flashing(godModeDuration, 3, renderObject);
        }
        
        //Timer for godmode length after hit
        if(godModeTimer < godModeDuration && godMode == true)
        {
            godModeTimer += Constants.DELTATIME;
            
        }else if(godMode == true)
        {
            godMode = false;
            godModeTimer = 0;
            id = -1;
            
            //Stops the blinkAnimation thread, it is more precise than using only the godModeDuration
            if(flashThread != null)
            {
                flashThread.interrupt();
            }else
            {
                System.err.println("ERROR: Someone gave himself godMode!");
            }
            
            if(Constants.CLIENTDEBUG)
            {
                System.out.println("Invulnerability deactivated");
            }
        }
    }

    
    /**
     * Moves the player if keyboard input is received.
     */
    private void inputMovePlayer()
    {
        /*------------------WALKING LEFT------------------*/
        if((Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)))
        {
            if(!collidesLeft() && !collidesLeftBomb())
            {
                //Smoothly changes the direction
                goLeft();

                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimLeft), pos.x, pos.y);

                //Block of code that gets executed just once upon button press
                if(sendMoveOnce.equals("LEFT") == false)
                {
                    //Send current player position to reduce lag
                    client.sendData("stopEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|" + pos.x + "|" + pos.y + "|*");
                    
                    //Saves position so that the static image looks in the correct direction if player stands still
                    lastMovementKeyPressed = "LEFT";
                    
                    //Ensures that stop command gets send only once
                    sendStopOnce = true;

                    //Send data to server
                    client.sendData("moveEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|LEFT|*");
                }

                //Ensures that walk command gets send only once
                sendMoveOnce = "LEFT";
            }else
            {
                //Ensures that stop command gets send only once
                sendStopOnce = true;
                
                //Sets moving direction to 0
                stopMoving();
                
                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimLeft), pos.x, pos.y);
            }
             
        /*------------------WALKING RIGHT------------------*/
        }else if((Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)))
        {
            if(!collidesRight() && !collidesRightBomb())
            {
                //Smoothly changes the direction
                goRight();
                
                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimRight), pos.x, pos.y);
                
                //Block of code that gets executed just once upon button press
                if(sendMoveOnce.equals("RIGHT") == false)
                {
                    //Send current player position to reduce lag
                    client.sendData("stopEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|" + pos.x + "|" + pos.y + "|*");
                    
                    //Saves position so that the static image looks in the correct direction if player stands still
                    lastMovementKeyPressed = "RIGHT";
                    
                    //Ensures that stop command gets send only once
                    sendStopOnce = true;
                    
                    //Send walk command to server
                    client.sendData("moveEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|RIGHT|*");
                }
                
                //Ensures that walk command gets send only once
                sendMoveOnce = "RIGHT";
            }else
            {
                //Ensures that stop command gets send only once
                sendStopOnce = true;
                
                //Sets moving direction to 0
                stopMoving();
                
                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimRight), pos.x, pos.y);
            }
            
        /*------------------WALKING UP------------------*/
        }else if((Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)))
        {
            if(!collidesTop() && !collidesTopBomb())
            {
                //Smoothly changes the direction
                goUp();

                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimUp), pos.x, pos.y);

                //Block of code that gets executed just once upon button press
                if(sendMoveOnce.equals("UP") == false)
                {
                    //Send current player position to reduce lag
                    client.sendData("stopEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|" + pos.x + "|" + pos.y + "|*");
                    
                    //Saves position so that the static image looks in the correct direction if player stands still
                    lastMovementKeyPressed = "UP";
                    
                    //Ensures that stop command gets send only once
                    sendStopOnce = true;

                    //Send walk command to server
                    client.sendData("moveEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|UP|*");
                }
                
                //Ensures that walk command gets send only once
                sendMoveOnce = "UP";
            }else
            {
                //Ensures that stop command gets send only once
                sendStopOnce = true;
                
                //Sets moving direction to 0
                stopMoving();
                
                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimUp), pos.x, pos.y);
            }
            
        /*------------------WALKING DOWN------------------*/
        }else if((Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)))
        {
            if(!collidesBottom() && !collidesBottomBomb())
            {
                goDown();
                
                renderObject.draw(animEffects.getFrame(walkAnimDown), pos.x, pos.y);
                
                //Block of code that gets executed just once upon button press
                if(sendMoveOnce.equals("DOWN") == false)
                {
                    //Send current player position to reduce lag
                    client.sendData("stopEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|" + pos.x + "|" + pos.y + "|*");
                    
                    //Saves position so that the static image looks in the correct direction if player stands still
                    lastMovementKeyPressed = "DOWN";
                    
                    //Ensures that stop command gets send only once
                    sendStopOnce = true;
                    
                    //Send walk command to server
                    client.sendData("moveEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|DOWN|*");
                }
                
                //Ensures that walk command gets send only once
                sendMoveOnce = "DOWN";
            }else
            {
                //Ensures that stop command gets send only once
                sendStopOnce = true;
                
                //Sets moving direction to 0
                stopMoving();
                
                //Draw the player with animation
                renderObject.draw(animEffects.getFrame(walkAnimDown), pos.x, pos.y);
            }
            
        /*------------------NO MOVEMENT------------------*/    
        }else
        {
            if(sendStopOnce == true)
            {
                //Send stop command to server
                client.sendData("stopEnemyPlayer|" + Integer.toString(Constants.PLAYERID) + "|" + pos.x + "|" + pos.y + "|*");
                
                //Ensures that stop command gets send only once
                sendStopOnce = false;
                
                //Reset walk command to be able to walk in every direction after stopping player
                sendMoveOnce = "";
            }

            if(sendStopTimer >= sendStopTime)
            {
                //Send command again
                sendStopOnce = true;
                
                //Reset timer
                sendStopTimer = 0;
            }else
            {
                sendStopTimer += Constants.DELTATIME;
            }
            
            //Sets moving direction to 0
            stopMoving();
            
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
                
                default:
                    System.err.println("ERROR: In MainPlayer inputMovePlayer() wrong value in lastMovementKeyPressed: " + lastMovementKeyPressed);
            }
        }
    }
    
    
    /**
     * Action the player can make like placing a bomb
     */
    private void inputDoPlayer()
    {        
        /*------------------PLACE BOMB------------------*/
        if (Gdx.input.isKeyJustPressed(Keys.SPACE))
        {
            float x = pos.x + Constants.PLAYERWIDTH / 2;
            float y = pos.y + Constants.PLAYERHEIGHT / 3;
            String bombType;
            
            switch (chosenBomb)
            {
                case(0):
                    
                    if(coins >= bomb_0)
                    {
                        if(life < Constants.MAXLIFE)
                        {
                            coins -= bomb_0;
                            life += 1;
                        }                
                    }
                    break;
                
                case(1):

                    bombType = "default";
                    
                    //Checks if there is already a bomb
                    if(!map.isBombPlaced(x, y) && maxBombPlacing > entityManager.getBombManager().getBombArrayMain().size && coins >= bomb_1)
                    {
                        coins -= bomb_1;
                            
                        //Send bomb command to server
                        client.sendData("placeEnemyBomb|" + Float.toString(x) + "|" + Float.toString(y) + "|" + Integer.toString(Constants.PLAYERID) + "|" + bombType + "|*");
                
                        //Create Bomb Object (Add always a new Vector2 object or else it will constantly update the position to the player position)
                        entityManager.getBombManager().spawnNormalBomb(new Vector2(x, y), playerId, bombRange);
                    }
                    break;
                    
                case(2):
                    
                    bombType = "dynamite";
                    
                    //Checks if there is already a bomb
                    if(!map.isBombPlaced(x, y) && maxBombPlacing > entityManager.getBombManager().getBombArrayMain().size && coins>= bomb_2)
                    {
                        coins -= bomb_2;
                        
                        //Send bomb command to server
                        client.sendData("placeEnemyBomb|" + Float.toString(x) + "|" + Float.toString(y) + "|" + Integer.toString(Constants.PLAYERID) + "|" + bombType + "|*");
                            
                        //Create Bomb Object (Add always a new Vector2 object or else it will constantly update the position to the player position)
                        entityManager.getBombManager().spawnDynamite(new Vector2(x, y), playerId, bombRange);
                    }
                    break;
                    
                case(3):
                    
                    bombType = "infinity";
                    
                    //Checks if there is already a bomb
                    if(!map.isBombPlaced(x, y) && maxBombPlacing > entityManager.getBombManager().getBombArrayMain().size && coins>= bomb_3)
                    {
                        coins -= bomb_3;
                        
                        //Send bomb command to server
                        client.sendData("placeEnemyBomb|" + Float.toString(x) + "|" + Float.toString(y) + "|" + Integer.toString(Constants.PLAYERID) + "|" + bombType + "|*");
                            
                        //Create Bomb Object (Add always a new Vector2 object or else it will constantly update the position to the player position)
                        entityManager.getBombManager().spawnInfinity(new Vector2(x, y), playerId, bombRange, 6);
                    }
                    break;
                        
                case(4):
                    
                    bombType = "X3";
            
                    //Checks if there is already a bomb
                    if(!map.isBombPlaced(x, y) && maxBombPlacing > entityManager.getBombManager().getBombArrayMain().size && coins>= bomb_4)
                    {
                        coins -= bomb_4;
                        
                        //Send bomb command to server
                        client.sendData("placeEnemyBomb|" + Float.toString(x) + "|" + Float.toString(y) + "|" + Integer.toString(Constants.PLAYERID) + "|" + bombType + "|*");
                            
                        //Create Bomb Object (Add always a new Vector2 object or else it will constantly update the position to the player position)
                        entityManager.getBombManager().spawnX3(new Vector2(x, y), playerId, bombRange, 1);
                    }
                    break;
                        
                default:
                    System.out.println("no Bomb chosen");
            }
 
        }
        
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_0) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_0))
        {
            chosenBomb = 0;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_1) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_1))
        {
            chosenBomb = 1;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_2) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_2))
        {
            chosenBomb = 2;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_3) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_3))
        {
            chosenBomb = 3;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_4) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_4))
        {
            chosenBomb = 4;
        }
         
        if (Gdx.input.isKeyJustPressed(Keys.NUM_5) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_5))
        {
            chosenBomb = 5;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_6) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_6))
        {
            chosenBomb = 6;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_7) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_7))
        {
            chosenBomb = 7;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_8) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_8))
        {
            chosenBomb = 8;
        }
        
        if (Gdx.input.isKeyJustPressed(Keys.NUM_9) || Gdx.input.isKeyJustPressed(Keys.NUMPAD_9))
        {
            chosenBomb = 9;
        }
        
        /*------------------ZOOM OUT OF GAME------------------*/
        if (Gdx.input.isKeyPressed(Keys.O))
        {
            if(camera.zoom < maxZoomOut)
            {
                camera.zoom += 0.02;
                camera.position.set(pos.x, pos.y, 0);
            }
        }

        /*------------------ZOOM INTO GAME------------------*/
        if (Gdx.input.isKeyPressed(Keys.I))
        {
            if(camera.zoom > maxZoomIn)
            {
                camera.zoom -= 0.02;
                camera.position.set(pos.x, pos.y, 0);
            }
        }
    }

    /*------------------ GETTER & SETTER ------------------*/  
    public float getMaxZoomIn()
    {
        return maxZoomIn;
    }
    
    public void setMaxZoomIn(float maxZoomIn)
    {
        this.maxZoomIn = maxZoomIn;
    }
    
    public float getMaxZoomOut()
    {
        return maxZoomIn;
    }
    
    public void setMaxZoomOut(float maxZoomOut)
    {
        this.maxZoomOut = maxZoomOut;
    }
    
    public int getLife()
    { 
        return this.life;
    }
    
    public void setLife(int life)
    {
        this.life = life;
    }
    
    public int getMaxBombs()
    {
        return this.maxBombPlacing;
    }

    public int getCoins()
    {
        return this.coins;
    }

    public int getPlayerId() 
    {
        return playerId;
    }

    public void setPlayerId(int playerId) 
    {
        this.playerId = playerId;
    }

    public int getMaxBombPlacing() 
    {
        return maxBombPlacing;
    }

    public void setMaxBombPlacing(int maxBombPlacing) 
    {
        this.maxBombPlacing = maxBombPlacing;
    }

    public int getBombRange() 
    {
        return bombRange;
    }

    public void setBombRange(int BombRange) 
    {
        this.bombRange = BombRange;
    }

    public void setCoins(int coins) 
    {
        this.coins = coins;
    }
}
