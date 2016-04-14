/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import gui.Constants;

public class OrthoCamera extends OrthographicCamera {

        //Objects
	Vector3 tmp = new Vector3();
	Vector2 origin = new Vector2();
	VirtualViewport virtualViewport;
	Vector2 pos = new Vector2();

        //Constructor 1
	public OrthoCamera() 
        {
            this(new VirtualViewport(Constants.SCREENWIDTH, Constants.SCREENHEIGHT));
	}
	
        //Constructor2
	public OrthoCamera(VirtualViewport virtualViewport) 
        {
            this(virtualViewport, 0f, 0f);
	}

        //Constructor3
	public OrthoCamera(VirtualViewport virtualViewport, float cx, float cy) 
        {
            this.virtualViewport = virtualViewport;
            this.origin.set(cx, cy);
	}

	public void setVirtualViewport(VirtualViewport virtualViewport) 
        {
            this.virtualViewport = virtualViewport;
	}
	
	public void setPosition(float x, float y) 
        {
            position.set(x - viewportWidth * origin.x, y - viewportHeight * origin.y, 0f);
            pos.set(x, y);
	}
	
	public Vector2 getPos() 
        {
            return pos;
	}

	@Override
	public void update() 
        {
            float left = zoom * -viewportWidth / 2 + virtualViewport.getVirtualWidth() * origin.x;
            float right = zoom * viewportWidth / 2 + virtualViewport.getVirtualWidth() * origin.x;
            float top = zoom * viewportHeight / 2 + virtualViewport.getVirtualHeight() * origin.y;
            float bottom = zoom * -viewportHeight / 2 + virtualViewport.getVirtualHeight() * origin.y;

            projection.setToOrtho(left, right, bottom, top, Math.abs(near), Math.abs(far));
            view.setToLookAt(position, tmp.set(position).add(direction), up);
            combined.set(projection);
            Matrix4.mul(combined.val, view.val);
            invProjectionView.set(combined);
            Matrix4.inv(invProjectionView.val);
            frustum.update(invProjectionView);
	}

	/**
	 * This must be called in ApplicationListener.resize() in order to correctly update the camera viewport. 
	 */
	public void updateViewport() 
        {
            setToOrtho(false, virtualViewport.getWidth(), virtualViewport.getHeight());
	}
	
        public Vector2 unprojectCoordinates(float x, float y) 
        {
            Vector3 rawtouch = new Vector3(x, y,0);
            unproject(rawtouch); 
            return new Vector2(rawtouch.x, rawtouch.y);
        }

        public void resize() 
        {
            VirtualViewport virtualViewport = new VirtualViewport(Constants.SCREENWIDTH, Constants.SCREENHEIGHT);  
            setVirtualViewport(virtualViewport);  
            updateViewport();
        }
}
