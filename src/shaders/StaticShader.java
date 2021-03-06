package shaders;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.Maths;

import entities.Camera;
import entities.Light;


/*
 This class is just the implementation of the shader program................................. 
*/


public class StaticShader extends ShaderProgram{
	
	private static final int MAX_LIGHTS = 4;
	
	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";         //file path of vertex shader.................
	private static final String FRAGMENT_FILE = "src/shaders/fragmentShader.txt";     // file path of fragment shader................
	
	private int location_transformationMatrix;                                    // stores location of the transformation matrix............
	private int location_projectionMatrix;                                      // stores location of the projection matrix............
	private int location_viewMatrix;                                              // stores location of the view matrix............
	private int location_lightPosition[];                                        // stores the location positions of all the lights...........
	private int location_lightColour[];                                         // stores the location color of all the lights............
	private int location_attenuation[];                                         // stores the location attenuations of the light.............
	private int location_shineDamper;                                            // stores the location shine damper of the light.............
	private int location_reflectivity;                                           // stores the location reflectivity of the surface.............
	private int location_useFakeLighting;                                       // stores the location of boolean use fake lighting.............
	private int location_skyColour;                                              // stores the location of sky color.............
	private int location_numberOfRows;                                           
	private int location_offset;        


	
	
	
	
	//Constructor calls the  constructor of shader program and pass path of shaders..........
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	
	
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");             //this binds attribute 0 of our VAO which is the positions of our models.............
		super.bindAttribute(1, "textureCoordinates");       // this binds attribute 1 of vao that is texture in the vertex shader............
		super.bindAttribute(2, "normal");                   // this binds attribute 2 of vao that is texture in the vertex shader............
	}

	
	
	
	//This methods gets all the locations of the uniform variables...........................................
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");                       // get the location of transformation matrix.........
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");                              // get the location of projection matrix.........
		location_viewMatrix = super.getUniformLocation("viewMatrix");                                           // get the location of view matrix.......
	/*	location_lightPosition = super.getUniformLocation("lightPosition");
		location_lightColour = super.getUniformLocation("lightColour"); */
		location_shineDamper = super.getUniformLocation("shineDamper");                                         // get the location of shine damper......
		location_reflectivity = super.getUniformLocation("reflectivity");                                       // get the location of reflectivity......
		location_useFakeLighting = super.getUniformLocation("useFakeLighting");                                // get the location of boolean use fake lighting......
		location_skyColour = super.getUniformLocation("skyColour");                                          // get the location of sky color......  
		location_numberOfRows = super.getUniformLocation("numberOfRows");                                     // get the location of number of rows......  
		location_offset = super.getUniformLocation("offset");                                               // get the location of offset......  
		   
		
		
		//get the locations of light parameters that include positions, color and lights.........................................
		location_lightPosition = new int[MAX_LIGHTS];                         // List of locations of positions..................
		location_lightColour = new int[MAX_LIGHTS];                           // List of locations of lights..................
		location_attenuation = new int[MAX_LIGHTS];                           // List of locations of attenuations..................
		for(int i=0; i<MAX_LIGHTS; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition["+i+"]");                // get and add locations of positions..............
			location_lightColour[i] = super.getUniformLocation("lightColour["+i+"]");                    // get and add locations of colors..............
			location_attenuation[i] = super.getUniformLocation("attenuation["+i+"]");                    // get and add locations of attenuations..............
		}
		
	}
	
	
	
	
	
	
	
	// pass values to shader program to load it to the shaders............
	public void loadNumberOfRows(int numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);          //load to shader...................
	}
	
	
	
	
	
	// pass values to shader program to load it to the shaders............
	public void  loadOffset(float x, float y) {
		super.load2DVector(location_offset, new Vector2f(x,y));      //load to shader...................
	}
	
	
	
	
	
	// pass values to shader program to load it to the shaders............
	public void loadSkyColour(float r, float g, float b) {
		
		super.loadVector(location_skyColour, new Vector3f(r,g,b));      //load to shader...................
		
	}
	
	
	
	
	
	// pass values to shader program to load it to the shaders............
	public void loadFakeLightingVariable(boolean useFake) {
		
		super.loadBoolean(location_useFakeLighting, useFake);   //load to shader...................
		
	}
	
	
	
	
	
	
	// pass values to shader program to load it to the shaders............
	public void loadShineVariables(float damper,float reflectivity){                 //takes in damper and reflectivity.............
		super.loadFloat(location_shineDamper, damper);                                         // load damper to shader............
		super.loadFloat(location_reflectivity, reflectivity);                                  // load reflectivity to shader..............
	}
	
	
	
	 
	
	
	
	// This method loads the transformation matrix to the location by calling method from parent class....................
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);                       // load matrix by passing location and matrix..............
	}
	
	
	
	
	
	
	
	//This method is called from the master renderer class and it takes in lights array.................................................. 
	public void loadLights(List<Light> lights){
		/*
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColour, light.getColour());
	*/
		
		/// loop upto size of max number of lights...........................................................
		for(int i=0; i<MAX_LIGHTS;i++) {
			if(i<lights.size()) {                                                               //check if the size is less than lights array.............
				super.loadVector(location_lightPosition[i], lights.get(i).getPosition());           // add light positions to shader..................
				super.loadVector(location_lightColour[i], lights.get(i).getColour());               // add light colors to shader..................
				super.loadVector(location_attenuation[i], lights.get(i).getAttenuation());          // add light attenuations to shader..................
				
			}
			else {
				super.loadVector(location_lightPosition[i], new Vector3f(0,0,0));             // load empty position vector to shader..................
				super.loadVector(location_lightColour[i], new Vector3f(0,0,0));               // load empty color vector to shader.................
				super.loadVector(location_attenuation[i], new Vector3f(1,0,0));                // load empty attenuation vector to shader.................
				//we pass a 1 because we don't want to get a divide by zero exception........................
			}
			
		}
	}
	
	
	
	
	
	
	// This method loads the view matrix to the location by calling method from parent class....................
	public void loadViewMatrix(Camera camera){                                       // pass the camera object.................
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);                        // create the view matrix from method in maths class............
		super.loadMatrix(location_viewMatrix, viewMatrix);                            // load this matrix................
	}
	
	
	
	
	
	
	// This method loads the projection matrix to the location by calling method from parent class....................
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);                          // load matrix by passing location and matrix..............
	}
	
	

}
