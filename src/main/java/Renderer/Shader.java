package Renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOError;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramId;
    private boolean isBeingUsed = false;
    private String fragmentSource, vertexSource, filePath;
    public Shader(String filePath){
        this.filePath = filePath;
        try{
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //Find the first pattern after #type
            int index = source.indexOf("#type")  + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //Find the second pattern after #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n",index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            }else if(firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            }else{
                throw new IOException("Error: Unknown shader type: " + firstPattern);
            }

            if(secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            }else if(secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            }else{
                throw new IOException("Error: Unknown shader type: " + secondPattern);
            }
        }catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not read shader file: " + filePath;
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void linkAndCompile(){

        //====================================================================
        // Compile and Link Shaders
        //===================================================================

        int vertexId, fragmentId;
        //First Load and Compile Vertex Shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        //Pass the shader source code
        glShaderSource(vertexId, vertexSource);
        glCompileShader(vertexId);

        //Check for Errors in compilation Process
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR::SHADER::VERTEX::COMPILATION_FAILED");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false: "";
        }

        //First Load and Compile fragment Shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        //Pass the shader source code
        glShaderSource(fragmentId, fragmentSource);
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
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);

        //Check for Errors in linking Process
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if(success == GL_FALSE){
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR::SHADER::PROGRAM::LINKING_FAILED");
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false: "";
        }
    }

    public void use(){
        if(!isBeingUsed){
        //Bind Shader program
        glUseProgram(shaderProgramId);
        isBeingUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        isBeingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        mat4.get(buffer);
        glUniformMatrix4fv(varLocation, false, buffer);
    }

    public void uploadMat3f(String varName, Matrix4f mat3){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        mat3.get(buffer);
        glUniformMatrix3fv(varLocation, false, buffer);
    }

    public void uploadVec4f(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform4f(varLocation,vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform3f(varLocation,vec.x, vec.y, vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform2f(varLocation,vec.x, vec.y);
    }

    public void uploadFloat(String varName, float value){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1f(varLocation, value);
    }

    public void uploadInt(String varName, int value){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1i(varLocation, value);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramId, varName);
        use();
        glUniform1i(varLocation, slot);
    }

}
