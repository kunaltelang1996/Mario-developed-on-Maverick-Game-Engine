package MaverickEngine;

import Renderer.Shader;
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

    private float vertexArray[] ={
            100.5f,-100.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, //Bottom right
            -100.5f,100.5f,0.0f, 0.0f, 1.0f, 0.0f, 1.0f,  //Top Left
            100.5f, 100.5f,0.0f, 1.0f, 0.0f, 1.0f, 1.0f,  //Top right
            -100.5f, -100.5f,0.0f, 1.0f, 1.0f, 0.0f, 1.0f,  //Bottom left
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

        camera.position.x -= deltaTime * 50.0f;

        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
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
    }

    @Override
    public void init(){

        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.linkAndCompile();

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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
        glEnableVertexAttribArray(1);


    }

}
