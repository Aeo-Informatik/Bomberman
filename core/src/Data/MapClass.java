package Data;

/**
 *
 * @author phinix
 */
public class MapClass implements Map {
    private int width;//width of the map
    private int height;//height of the map
    private int[][] passable ;//if the position is passaple
    private int[][] blockposition;//if ther is a block on this position
    /**
     * Construktor
     * @param width of th map
     * @param height of the map
     * @param passable if the position is passaple
     * @param blockposition if ther is a block on this position
     */
    public MapClass(int width, int height, int[][] passable, int[][] blockposition) {
        this.width = width;
        this.height = height;
        this.passable = passable;
        this.blockposition = blockposition;
    }
    /**
     * get width of the map
     * @return width of the map
     */
    public int getWidth() {
        return width;
    }
    /**
     * set width of the map
     * @param width of the map
     */
    public void setWidth(int width) {
        this.width = width;
    }
    /**
     * get height of the map
     * @return height of the map
     */
    public int getHeight() {
        return height;
    }
    /**
     * set height of the map
     * @param height of the map
     */
    public void setHeight(int height) {
        this.height = height;
    }
    /**
     * get if the possition is passable
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return if th epossition is passable
     */
    public boolean getPassable(int x, int y) {
        if (passable[x][y]==1){
            System.out.println("true");
            return true;
        }else{
            System.out.println("false");
            return false;
        }
                
    }
    /**
     * set if the possition is passible
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param passable if the possition is passable
     */
    public void setPassable(int x,int y,int passable) {
        this.passable[x][y]=passable;
    }
    /**
     * get if there is a block on that position
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return if there is a block on that position
     */
    public boolean getBlockposition(int x , int y) {
        if(blockposition[x][y]==1){
            System.out.println("true");
            return true;
        }else{
            System.out.println("true");
            return false;
        }
    }
    /**
     * set if there is a block on that position
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param blockposition if there is a block on that position
     */
    public void setBlockposition(int x,int y,int blockposition) {
        this.blockposition[x][y]=blockposition;
    }
    
    

}
