package MaverickEngine;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    public Scene(){

    }
    public void init(){}

    public void addGameObjectToScene(GameObject go){
        if(!isRunning) {
            gameObjects.add(go);
        }else {
            gameObjects.add(go);
            go.start();
        }
    }

    public void start(){
        for(GameObject gameObject : gameObjects){
            gameObject.start();
        }
        isRunning = true;
    }


    public abstract void update(float deltaTime);
}
