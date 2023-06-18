package example.mixins;

import example.targets.Entity;
import mixin.Injectable;
import mixin.Mixin;

@Mixin(targetClass = Entity.class)
public class EntityMixin {

    @Injectable(targetMethod = "setEntitySpeed", position = "TAIL")
    public static void setEntitySpeed(){
        System.out.println(100);
    }

}
