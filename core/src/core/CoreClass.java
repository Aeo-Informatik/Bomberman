/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

import java.awt.event.KeyEvent;
import java.util.regex.Pattern;
import sun.net.NetworkClient;

/**
 *
 * @author plonies_d
 */
public class CoreClass {
    
int[][]map = new int [20][30]; //creates a matrix as map
data.GameObjects [][] GameObjects = new data.GameObjects [20][30];// creates a matrix from GameObjects
data.PlayerClass p1 = new data.PlayerClass(100,0,50,0,0);
    public CoreClass() {
        
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public data.GameObjects getGameObject(int x, int y) {
        System.out.println(GameObjects [y][x]);
        return GameObjects[y][x];
    }

    public void addGameObject(int x,int y,data.GameObjects gameObject) {
        this.GameObjects[y][x] = gameObject;
    }
 
    /**
     * create an object map
     */    
    public void createMap(){
        data.MapClass m1 = new data.MapClass(20, 30);
        data.BlocksClass b1=new data.BlocksClass(true, 10); 
        data.BlocksClass b2=new data.BlocksClass(false, 10); 
        GameObjects[10][10]=b1;
        GameObjects[15][20]=b2;
        
        data.BlocksClass bb=new data.BlocksClass(false, -100); 
        
        //create the blocks that can be destroyed
        
        
        //create the blocks at the ends of the map
        for (int i=0;i<=30;i++){
           GameObjects[0][i]=bb;
           GameObjects[20][i]=bb;
        }
        
        for (int i=0;i<=20;i++){
           GameObjects[i][0]=bb;
           GameObjects[i][30]=bb;
        }
        
        //create the undestructible block raster
        for(int i=2;i<=18;i+=2){
            for(int e=2; e<=28;e+=2){
               GameObjects[e][i]=bb; 
            }
        }
            
        
        
        
        }   

    /**
     * updates the map 
     */
    public int[][] updateMap(){
        return null;
    }

    /**
     * player can place a bomb
     */
    public void placeBomb(){
    
    }

    /**
    * looks for players and blocks that would be destroyed
    * @param bomb 
     */
    public void explode(data.BombClass bomb){//Bumm
    
    }
    
    /**
     * moves the player
     * @param player which player
     * @param direction which direction
     * @return the coordinates of the player after his move
     */
    public int[] movePlayer(data.PlayerClass player, char direction, KeyEvent e){
               return null;      
    }        
    
    
    
    /**
     * checks how many coins are on a field
     * @param coordinates of the field
    * @return 
     */
    public data.CoinClass checkCoins(int[] coordinates){
    return null;
    }

    /**
     * looks for what the key that is pressed is used for
    * @param key 
    */
    public void hotkey(char key){
    
    }
 
    /**
     * cuts long Strings from NetworkClient into short Strings
     * @param data String from NetworkClient
     */
    public void analyseData(String data){
    
    }
    
    
    }
