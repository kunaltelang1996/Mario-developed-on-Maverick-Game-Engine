package MaverickEngine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> component;
    public Transform trans;
    public GameObject(String name) {
        // Constructor logic here
        this.name = name;
        this.component = new ArrayList<>();
        this.trans = new Transform();
    }

    public GameObject(String name, Transform trans) {
        // Constructor logic here
        this.name = name;
        this.component = new ArrayList<>();
        this.trans = trans;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : component) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                }catch(ClassCastException e)
                {
                    assert false : "ClassCastException: " + e.getMessage();
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < component.size(); i++) {
            if (componentClass.isAssignableFrom(component.get(i).getClass())) {
                component.remove(i);
                return;
            }
        }
    }


    public void addComponent(Component component) {
        this.component.add(component);
        component.gameObject = this;
    }

    public  void update(float dt){
        for (int i=0;i<component.size();i++){
            component.get(i).update(dt);
        }
    }

    public void start(){
        for(int i=0;i<component.size();i++){
            component.get(i).start();
        }
    }
}
