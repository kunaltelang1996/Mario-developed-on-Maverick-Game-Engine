package MaverickEngine;

import Components.FontRenderer;
import Components.SpriteRenderer;
import Renderer.Shader;
import Renderer.Texture;
import Util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene{

    private int vaoId, vboId, eboId;

    private Shader defaultShader;
    private Texture texture;

    GameObject testObject;
    private boolean firstTime = false;

    private float vertexArray[] ={
            100.5f,-100.5f, 0.0f,  1.0f, 0.0f, 0.0f, 1.0f,  1,1,  //Bottom right
            -100.5f,100.5f,0.0f,   0.0f, 1.0f, 0.0f, 1.0f,  0,0,  //Top Left
            100.5f, 100.5f,0.0f,   1.0f, 0.0f, 1.0f, 1.0f,  1,0,  //Top right
            -100.5f, -100.5f,0.0f, 1.0f, 1.0f, 0.0f, 1.0f,  0,1   //Bottom left
    };

    //Must be in counter-clockwise direction
    private int elementArray[] = {
            2,1,0, //Top Right Triangle
            0,1,3  //Bottom Left Triangle
    };


    private float timeToChangeScene = 2.0f;
    public LevelEditorScene(){

    }

    @Override
    public void update(float deltaTime){

//        camera.position.x -= deltaTime * 50.0f;
//        camera.position.y -= deltaTime * 20.0f;

        defaultShader.use();

        //Upload texture to the shader
        defaultShader.uploadTexture("tex_sampler",0);
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        //Bind VAO
        glBindVertexArray(vaoId);

        //Enable the vertex attribute Pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //Unbind Everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        defaultShader.detach();
        if(!firstTime) {
            System.out.println("Creating Game object");
            GameObject go = new GameObject("Game Test 2");
            go.addComponent(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }

        for(GameObject gameObject : this.gameObjects){
            gameObject.update(deltaTime);
        }
    }

    @Override
    public void init(){
        System.out.println("Creatng Test Object");
        this.testObject = new GameObject("Test Object");
        this.testObject.addComponent(new SpriteRenderer());
        this.testObject.addComponent(new FontRenderer());
        this.addGameObjectToScene(this.testObject);

        this.camera = new Camera(new Vector2f(-600,-300));

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.linkAndCompile();

        this.texture = new Texture("assets/Images/download.jpg");

        //==========================================================================
        //Generate VAO, VBO and EBO buffer objects, and send data to GPU
        //==========================================================================

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //Create VBO upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //Create the Indices and uplaod
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        //Creating the ebo
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2,uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);

    }

}
