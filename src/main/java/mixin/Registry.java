package mixin;

import example.mixins.EntityMixin;
import mixin.chatgpt_code.MethodMerger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Registry {
    private static final List<Class> mixin_classes = new ArrayList<>();

    static {
        // Register all mixin classes here
        mixin_classes.add(EntityMixin.class);
    }

    private static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to traverse a type hierarchy in order to process methods from super types
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }

    // FIXME: 12/06/2023 What doesn't work: methods with arguments, everything
    public static void mixin() throws Exception {
        for(Class<?> c : mixin_classes){
            List<Method> annotatedMethods = getMethodsAnnotatedWith(c, Injectable.class);
            for(Method sourceMethod : annotatedMethods){
                Injectable annMethod = sourceMethod.getAnnotation(Injectable.class);
                System.out.println("Annotated target method: "+annMethod.targetMethod()+", target class: "+c.getAnnotation(Mixin.class).targetClass().toString());
                Method targetMethod = c.getAnnotation(Mixin.class).targetClass().getMethod(annMethod.targetMethod());

                try {
                    MethodNode sourceMethodNode = loadMethod(c.getName(), sourceMethod.getName());
                    MethodNode targetMethodNode = loadMethod(c.getAnnotation(Mixin.class).targetClass().getName(), targetMethod.getName());
                    MethodMerger.mixin(
                            targetMethodNode,
                            sourceMethodNode,
                            getMethodDescriptor(targetMethod),
                            getAccessLevel(targetMethod),
                            annMethod.position(),
                            c.getAnnotation(Mixin.class).targetClass(),
                            targetMethod
                    );
                } catch (Exception e){
                    Logger.getLogger("Mixin").warning("Ignoring "+c.getName()+"::"+sourceMethod.getName()+". Reason: loadMethod(classname, methodname) threw an exception." );
                    e.printStackTrace();
                }
            }
        }
    }

    private static MethodNode loadMethod(String className, String methodName) throws Exception {
        ClassReader cr = new ClassReader(className);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName)) {
                return method;
            }
        }

        throw new IllegalArgumentException("Method not found: " +className+"::"+ methodName);
    }

    private static String getMethodDescriptor(Method method){
        // Get the parameter types
        Class<?> returnType = method.getReturnType();

        // Get the parameter types
        Class<?>[] parameterTypes = method.getParameterTypes();

        // Get the descriptors of the parameter types
        String[] parameterDescriptors = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            parameterDescriptors[i] = Type.getDescriptor(parameterTypes[i]);
        }

        // Get the descriptor of the return type
        String returnDescriptor = Type.getDescriptor(returnType);

        // Build the full method descriptor
        return "(" + String.join("", parameterDescriptors) + ")" + returnDescriptor;
    }

    public static int getAccessLevel(Method method) {
        int modifiers = method.getModifiers();
        int accessLevel = 0;

        if (java.lang.reflect.Modifier.isPublic(modifiers)) {
            accessLevel = Opcodes.ACC_PUBLIC;
        } else if (java.lang.reflect.Modifier.isPrivate(modifiers)) {
            accessLevel = Opcodes.ACC_PRIVATE;
        } else if (java.lang.reflect.Modifier.isProtected(modifiers)) {
            accessLevel = Opcodes.ACC_PROTECTED;
        }
        return accessLevel;
    }

}
