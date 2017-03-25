package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by nic on 12/26/16.
 *
 * This class can only be accessed in a static context. This contains commonly used physics functions.
 * The purpose of this class is to put commonly used functions all in one place that can be accessed
 * throughout the physics package.
 */
public class PhysicsMath {

    // This function will clamp a vector to be greater in magnitude to clamp value.
    // How clamping is applied can be controlled with the clamp position flags
    public static Vector2 clampVectorAbove(Vector2 input, float clampValue, boolean clampXPos, boolean clampYPos, boolean clampXNeg, boolean clampYNeg) {
        if(clampXPos && input.x < clampValue && input.x > -clampValue) {
            input.x = clampValue;
        } else if(clampXNeg && input.x > -clampValue && input.x < clampValue) {
            input.x = -clampValue;
        }
        if(clampYPos && input.y < clampValue && input.y > -clampValue) {
            input.y = clampValue;
        } else if(clampYNeg && input.y > -clampValue && input.y < clampValue) {
            input.y = -clampValue;
        }

        return input;
    }

    public static Vector2 clampVectorBelow(Vector2 input, float clampValue, boolean clampXPos, boolean clampYPos, boolean clampXNeg, boolean clampYNeg) {
        if(clampXPos && input.x > clampValue) {
            input.x = clampValue;
        } else if(clampXNeg && input.x < -clampValue) {
            input.x = -clampValue;
        }
        if(clampYPos && input.y > clampValue) {
            input.y = clampValue;
        } else if(clampYNeg && input.y < -clampValue) {
            input.y = -clampValue;
        }

        return input;
    }

    public static float clampBelow(float input, float clampValue, boolean clampPos, boolean clampNeg) {
        if(clampPos && input > clampValue) {
            input = clampValue;
        } else if(clampNeg && input < -clampValue) {
            input = -clampValue;
        }

        return input;
    }

    public static boolean magIsBelowLimit(float input, float limit) {
        return input < limit && input > -limit;
    }

//    public static Vector2 zeroVectorIfAbove(Vector2 input, float limit) {
//        if(input.x > limit || input.x < -limit) {
//            input.x = 0;
//        }
//        if(input.y > limit || input.y < -limit) {
//            input.y = 0;
//        }
//        return input;
//    }
//
//    public static Vector2 zeroVectorIfUnder(Vector2 input, float limit) {
//        if(input.x < limit && input.x > -limit) {
//            input.x = 0;
//        }
//        if(input.y < limit && input.y > -limit) {
//            input.y = 0;
//        }
//        return input;
//    }
//
//    public static float zeroValueIfAbove(float input, float limit) {
//        if(input > limit || input < -limit) {
//            input = 0;
//        }
//        return input;
//    }
//
//    public static float zeroValueIfUnder(float input, float limit) {
//        if(input < limit && input > -limit) {
//            input = 0;
//        }
//        return input;
//    }
}
