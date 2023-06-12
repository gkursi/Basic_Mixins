package mixin.chatgpt_code;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class MethodMerger {
    /**
     * Mixin code by ChatGPT
     * @param method1 The base method
     * @param method2 The code that should be added
     * @param descriptor The descriptor for both of the methods
     * @param access The access for both of the methods
     * @throws Exception when something went wrong
     */
    public static void mixin(MethodNode method1, MethodNode method2, String descriptor, int access, String pos) throws Exception {
        // Create a new method with merged characteristics
        MethodNode mergedMethod = new MethodNode(
                access,
                "mergedMethod",
                descriptor,
                null,
                null
        );

        // Merge the instructions from both methods into the merged method
        switch (pos){
            case "HEAD" -> {
                mergedMethod.instructions.add(method2.instructions);
                mergedMethod.instructions.add(method1.instructions);

            }
            case "TAIL" -> {
                mergedMethod.instructions.add(method1.instructions);
                mergedMethod.instructions.add(method2.instructions);
            }
            default -> {
                throw new IllegalArgumentException("Invalid mixin point provided!");
            }
        }

        // Recalculate the stack size and max locals
        mergedMethod.maxStack = Math.max(method1.maxStack, method2.maxStack);
        mergedMethod.maxLocals = Math.max(method1.maxLocals, method2.maxLocals);

        // Generate the bytecode for the merged method
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        mergedMethod.accept(cw);

        byte[] mergedBytecode = cw.toByteArray();

        // Replace the bytecode of method1 with the merged bytecode
        Class<?> clazz = Class.forName("ClassWithMethod1");
        java.lang.reflect.Method method = clazz.getDeclaredMethod("method1");
        java.lang.reflect.Field field = method.getClass().getDeclaredField("accessFlags");
        field.setAccessible(true);
        field.set(method, mergedBytecode);

        // Now method1 contains the merged code
    }
}
