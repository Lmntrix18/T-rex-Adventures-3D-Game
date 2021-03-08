package renderEngine;

import java.util.List;

import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;



/*
This class renders the terrain models from the vao to the screen.................
*/
public class TerrainRenderer {

	private TerrainShader shader;                                                                 // create a class attribute of static shader.............................
               
     
	// This is the constructor of the terrain renderer which takes in shader and projection matrix........................
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;                                                  // assign the shader to the shader variable....................................
		shader.start();                                                        //  start the shader..........................
		shader.loadProjectionMatrix(projectionMatrix);                           // load projection matrix to the shader........................
		shader.connectTextureUnits();                                            
		shader.stop();                                                          // stop the shader once we have loaded the matrix..................................
	}

	
	
	
	
	
	//method to render the terrains on the screen...........................................
	public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {                      //take in the list of terrains..............................
		
		shader.loadToShadowSpaceMatrix(toShadowSpace);
		for (Terrain terrain : terrains) {                            // loop through terrains....................
			prepareTerrain(terrain);                                      //call prepare terain method.........................
			loadModelMatrix(terrain);                                     //method to load transformation in shader...............
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(),
					GL11.GL_UNSIGNED_INT, 0);                                                //Functions to draw the terrain on the screen ..................
			unbindTexturedModel();                                                          // call the unbind textured model method.................
		}
	}
	
	
	
	
	
	
	//Method to prepare our textured models.......................................
	private void prepareTerrain(Terrain terrain) {                         // takes in terrain model.............................
		RawModel rawModel = terrain.getModel();                             // create object of raw model....................
		GL30.glBindVertexArray(rawModel.getVaoID());                       // bind the model........................
		GL20.glEnableVertexAttribArray(0);                                    // enable the vertex in the vao..............
		GL20.glEnableVertexAttribArray(1);                                    // enable the textures in the vao...............
		GL20.glEnableVertexAttribArray(2);                                   // enable the normals in the vao............... 
		bindTextures(terrain);                                               //call the bind textures method.....................
		shader.loadShineVariables(1, 0);                                    //load the two shine variables to the shader.......
		
	}

	
	
	
	
	//Method to bind all the textures
	private void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);                                                                  // activate the texture1.................
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID());              // bind texture1........................
		GL13.glActiveTexture(GL13.GL_TEXTURE1);                                                                 // activate the texture2.................
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID());                       // bind texture2........................
		GL13.glActiveTexture(GL13.GL_TEXTURE2);                                                                // activate the texture3.................
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID());                      // bind texture3........................
		GL13.glActiveTexture(GL13.GL_TEXTURE3);                                                               // activate the texture4................. 
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID());                      // bind texture4........................
		GL13.glActiveTexture(GL13.GL_TEXTURE4);                                                                // activate the blender map.................
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());                          // bind blender map........................
	}
	
	
	
	
	
	
	//method to disable and unbind the attribute arrays of the vao................................
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);                                 //disable all our attribute arrays in the vao................
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}
	
	
	
	
    //This method that creates and loads the transformation matrix for the terrain in to the shader..................................
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);                               // create the transformation matrix...............
		shader.loadTransformationMatrix(transformationMatrix);                                              //load it to shader.......................
	}

}