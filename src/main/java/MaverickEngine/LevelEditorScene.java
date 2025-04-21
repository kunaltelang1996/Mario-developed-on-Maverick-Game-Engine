package MaverickEngine;

import java.awt.event.KeyEvent;

import static org.lwjgl.opengl.GL20.*;

public class LevelEditorScene extends Scene{

    private String vertexShaderSrc = "#version 330 core\n" +
            "        layout(location = 0) in vec3 aPos;\n" +
            "        layout(location = 1) in vec4 aColor;\n" +
            "\n" +
            "        out vec4 fColor;\n" +
            "\n" +
            "        void main()\n" +
            "        {\n" +
            "            fColor = aColor;\n" +
            "            gl_Position = vec4(aPos, 1.0);\n" +
            "        }";

    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "        in vec4 fColor;\n" +
            "        out vec4 Color;\n" +
            "\n" +
            "        void main()\n" +
            "        {\n" +
            "            Color = fColor;\n" +
            "        }";

    private int vertexId, fragmentId, shaderProgram;


    private float timeToChangeScene = 2.0f;
    public LevelEditorScene(){

    }

    @Override
    public void update(float deltaTime){

    }

    @Override
    public void init(){

        //====================================================================
        // Compile and Link Shaders
        //====================================================================

        //First Load and Compile Vertex Shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        //Pass the shader source code
        glShaderSource(vertexId, vertexShaderSrc);
        glCompileShader(vertexId);

        //Check for Errors in compilation Process
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false: "";
        }



        //First Load and Compile Vertex Shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        //Pass the shader source code
        glShaderSource(fragmentId, fragmentShaderSrc);
        glCompileShader(fragmentId);

        //Check for Errors in compilation Process
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false: "";
        }


        //Link Shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        //Check for Errors in linking Process
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false: "";
        }


    }

}
