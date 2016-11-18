package awqatty.b.DrawMath.AssignParentheses;

/**
 * Created by beckerawqatty on 11/14/16.
 */

public enum BorderType {
    // Indicates drawable marked with a:
    NUMERIC,        // number
    ALPHABET,       // letter (i.e., cos, sin, x)
    OPERATION,      // operation (i.e., +, -)
    ODD_DIM_OBJ,    // object of an irregular dimension
                    // (i.e., superscript, stretched line in division, etc.)
    NONE            // no such object
}
