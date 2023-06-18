import mixin.Registry;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        Registry.mixin(); // Setup method

        example.Main.main(args); // run example
    }
}
