package mixin.chatgpt_code;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodMerger {
    /**
     * @param method1 The base method
     * @param method2 The code that should be added
     * @param descriptor The descriptor for both of the methods
     * @param access_level The access level for both of the methods (private, public, etc.)
     * @param pos The position to inject to
     * @param method1Class The base methods source class
     * @param method The base method as a Method object
     * @throws Exception when something went wrong
     */
    public static void mixin(MethodNode method1, MethodNode method2, String descriptor, int access_level, String pos, Class<?> method1Class, Method method) throws Exception {
        // Create a new method with merged characteristics
        MethodNode mergedMethod = new MethodNode(
                access_level,
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

        byte[] bytecode = cw.toByteArray();

        method.setAccessible(true);
        System.out.println(Arrays.toString(bytecode));

        MethodWriter.writeToMethod(bytecode, method1);

    }

//    public static byte[] modifyBytecode(byte[] bytecode, MethodNode methodNode) {
//        // Step 1: Create a ClassWriter object
//        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//
//        // Step 2: Create a ClassVisitor
//        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9, classWriter) {
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
//
//                // Modify the bytecode of the specific method
//                if (name.equals(methodNode.name) && descriptor.equals(methodNode.desc)) {
//                    // Get the instructions of the original method
//                    InsnList instructions = methodNode.instructions;
//
//                    // Create a new instruction list for the modified bytecode
//                    InsnList modifiedInstructions = new InsnList();
//
//                    // Example modification: Replace all method invocations with a print statement
//                    for (AbstractInsnNode instruction : instructions) {
//                        if (instruction instanceof MethodInsnNode) {
//                            MethodInsnNode methodInsn = (MethodInsnNode) instruction;
//                            modifiedInstructions.add(new MethodInsnNode(
//                                    Opcodes.INVOKESTATIC,
//                                    "java/lang/System",
//                                    "out",
//                                    "Ljava/io/PrintStream;",
//                                    false
//                            ));
//                            modifiedInstructions.add(new MethodInsnNode(
//                                    Opcodes.INVOKEVIRTUAL,
//                                    "java/io/PrintStream",
//                                    "println",
//                                    "(Ljava/lang/String;)V",
//                                    false
//                            ));
//                        } else {
//                            modifiedInstructions.add(instruction.clone(null));
//                        }
//                    }
//
//                    // Replace the instructions of the method node with the modified instructions
//                    methodNode.instructions = modifiedInstructions;
//                    methodNode.accept(methodVisitor);
//                    return null; // Skip further processing for this method
//                }
//
//                return methodVisitor;
//            }
//        };
//
//        // Step 4: Read the original class bytecode and accept the ClassVisitor
//        ClassReader classReader = new ClassReader(bytecode);
//        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
//
//        // Step 5: Get the modified bytecode from the ClassWriter
//        return classWriter.toByteArray();
//    }

//    public static MethodNode writeBytecodeToMethodNode(byte[] bytecode, MethodNode method) {
//        ClassReader reader = new ClassReader(bytecode);
//        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
//
//        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
//            @Override
//            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
//
//                if (name.equals(method.name) && descriptor.equals(method.desc)) {
//                    return method;
//                }
//
//                return methodVisitor;
//            }
//        };
//
//        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
//
//        return method;
//    }
}
