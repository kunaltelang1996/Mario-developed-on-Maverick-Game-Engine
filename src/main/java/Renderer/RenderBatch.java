package Renderer;

import Components.SpriteRenderer;
import MaverickEngine.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    //Vertex
    //======
    //pos                        color
    //float,float                float,float,flaot,float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET+ POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] spriteRenderers;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int vaoId,vboId;
    private int maxBatchSize;
    private Shader shader;


    public RenderBatch(int maxBatchSize){
        shader = new Shader("assets/shaders/default.glsl");
        shader.linkAndCompile();
        this.spriteRenderers = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        //4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }
    public void start(){
        //Generate and bind a vertex Array Object
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        //Allocate Space to vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        //Create and upload indices buffer
        int eboId = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

    }

    public void addSprites(SpriteRenderer spr){
        //Get index and add renderObject
        int index = this.numSprites;
        this.spriteRenderers[index] = spr;
        this.numSprites++;

        //Add properties to local vertices array
        loadVertexProperites(index);

        if(this.numSprites >= this.maxBatchSize){
            this.hasRoom = false;
        }

    }

    private void loadVertexProperites(int index){
        //Get the sprite renderer
        SpriteRenderer spr = this.spriteRenderers[index];

        //Get the offset
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = spr.getColor();

        //Add 4 vertices with appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        for(int i=0;i<4;i++){
            //Add the position
            if(i==1){
                yAdd = 0.0f;
            }else if(i==2) {
                xAdd = 0.0f;
            } else if(i==3){
                yAdd = 1.0f;
            }
        }

        //Load Position
        vertices[offset] = spr.gameObject.trans.position.x + (xAdd * spr.gameObject.trans.scale.x);
        vertices[offset + 1] = spr.gameObject.trans.position.y + (yAdd * spr.gameObject.trans.scale.y);

        //Load the color
        vertices[offset + 2] = color.x;
        vertices[offset + 3] = color.y;
        vertices[offset + 4] = color.z;
        vertices[offset + 5] = color.w;

        offset += VERTEX_SIZE;
    }

    public void render(){
        //For Now we will rebuffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //Use Shader
        shader.use();
        shader.uploadMat4f("projectionMatrix", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("viewMatrix", Window.getScene().camera().getViewMatrix());
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
    }


    private int[] generateIndices(){
        //6 indices per quad (3 per triangle)
        int[] elements = new int[maxBatchSize * 6];
        for(int i=0;i<maxBatchSize;i++){
            loadElementIndices(elements,1);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6* index;
        int offset = 4 * index;

        //First triangle
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        //Second triangle
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }


    public boolean hasRoom() {
        return this.hasRoom;
    }
}
