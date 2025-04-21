package MaverickEngine;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene{

    private boolean changingScene = false;

    private float timeToChangeScene = 2.0f;
    public LevelEditorScene(){
        System.out.println("Inside Level Editor Scene");
    }

    @Override
    public void update(float deltaTime){
        System.out.println(" "+(1.0f/deltaTime)+" FPS");

        if(!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)){
            changingScene = true;
        }
        if(changingScene && timeToChangeScene > 0){
            timeToChangeScene -= deltaTime;
            Window.get().r -= deltaTime * 0.5f;
            Window.get().g -= deltaTime * 0.5f;
            Window.get().b -= deltaTime * 0.5f;
        }else if(changingScene)
        {
            Window.changeScene(1);
        }
    }

}
