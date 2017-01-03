package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

/**
 * Created by nic on 8/27/16.
 *
 * This list contains all collidable bodies in a scene. Currently those bodies are ordered by xPositions.
 * This will give an efficient way of testing for collisions (only test those surrounding the collided object
 * in the ordered list).
 *
 * Eventually I think I will save two separate lists (one ordered by xPositions and one ordered by yPositions).
 * This will allow me to choose which direction to test. For instance if the xRadius is greater than the yRadius
 * it will likely be faster to test the yPosition list.
 *
 * For now this class will also serve as the PhysicsEngine capable of resolving positions and velocities so that
 * objects will not intersect. Because of this the name of this class should probably change to PhysicsEngine
 */
public class BodyList {

    //private static final float separationDistance = 0.001f;
    private static float PPM = 1;

    private static final float root2over2 = (float) Math.sqrt(2) / 2;

    // It is EXTREMELY IMPORTANT that nothing directly access these lists
    // These MUST remain synchronized for the physics engine to not be nonsense
    private ArrayList<Float> positionsX;
    private ArrayList<Float> positionsY;
    private ArrayList<Body> bodies;

    public BodyList() {
        // Initialize ArrayLists in constructor
        positionsX = new ArrayList<Float>();
        positionsY = new ArrayList<Float>();
        bodies = new ArrayList<Body>();
    }

    // This function will add Body to the list and make sure the list stays ordered in terms of xPosition
    public void AddBody(Body newBody) {
        float newPositionX = newBody.getPosition().x;
        float newPositionY = newBody.getPosition().y;
        if (bodies.isEmpty()) {
            bodies.add(newBody);
            positionsX.add(newPositionX);
            positionsY.add(newPositionY);
        } else {
            int i = 1;
            boolean placed = false;
            while (!placed) {
                if (i >= bodies.size()) {
                    bodies.add(i, newBody);
                    positionsX.add(i, newPositionX);
                    positionsY.add(i, newPositionY);
                    placed = true;
                } else if (newPositionX < positionsX.get(i)) {
                    bodies.add(i, newBody);
                    positionsX.add(i, newPositionX);
                    positionsY.add(i, newPositionY);
                    placed = true;
                }
                i++;
            }
        }
        //System.out.println("Body Created");
    }

    public void RemoveBody(Body b) {
        if (bodies.contains(b)) {
            int index = bodies.indexOf(b);
            bodies.remove(index);
            positionsX.remove(index);
            positionsY.remove(index);
            bodies.trimToSize();
            positionsX.trimToSize();
            positionsY.trimToSize();
            //System.out.println("Body Destroyed");
        }
    }

    public void updatePosition(Body b) {
        if (bodies.contains(b)) {
            //int index = bodies.indexOf(b);
            RemoveBody(b);
            AddBody(b);
            //positionsX.set(index, x);
            //positionsY.set(index, y);
        }
        //System.out.println("Position Updated");
    }

    public void testCollision(Body testBody) {
        //int endingIndex = bodies.size() - 1;
        float testBodyPositionX = testBody.getPosition().x;
        float testBodyPositionY = testBody.getPosition().y;
        float testBodyRadiusX = testBody.getRadiusX();
        float testBodyRadiusY = testBody.getRadiusY();

        Vector2 relativePositionVector;
        float magRelativePosition;
        Vector2 unitNormal;

        boolean selfTestFlag;
        boolean collisionMaskFlag;
        boolean circleFlag = testBody.getIsCircle();
        // Iterate through all bodies
        int i;
        for(i = 0; i < bodies.size(); i++) {
            // Find body we are testing against
            Body currentBody = bodies.get(i);
            // Calculate and test flags
            selfTestFlag = i == bodies.indexOf(testBody);
            collisionMaskFlag = (testBody.getCollisionMask() & bodies.get(i).getCollisionIdentity()) != 0;
            // Test flags (Only want to test collision if flags tell us to)
            if(!selfTestFlag && collisionMaskFlag && circleFlag && !testBody.getBodyGroup().equals(currentBody.getBodyGroup())/*&& isPlayerFlag*/) {
                if(bodies.get(i).getIsCircle()) {
                    // Collided body is a circle
                    relativePositionVector = new Vector2(testBodyPositionX - positionsX.get(i), testBodyPositionY - positionsY.get(i));
                    magRelativePosition = getMagnitude(relativePositionVector.x, relativePositionVector.y);
                    if(magRelativePosition < testBodyRadiusX + currentBody.getRadiusX()) {
                        // Resolve Collision
                        unitNormal = new Vector2(relativePositionVector.x / magRelativePosition, relativePositionVector.y / magRelativePosition);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    }
                } else {
                    // Collided body is not a circle
                    // Test corners first, diagonal normals
                    if(getMagnitude(testBodyPositionX - (positionsX.get(i) + currentBody.getRadiusX()), testBodyPositionY - (positionsY.get(i) + currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Top-Right corner
                        unitNormal = new Vector2(root2over2, root2over2);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (positionsX.get(i) + currentBody.getRadiusX()), testBodyPositionY - (positionsY.get(i) - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Bottom-Right corner
                        unitNormal = new Vector2(root2over2, -root2over2);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (positionsX.get(i) - currentBody.getRadiusX()), testBodyPositionY - (positionsY.get(i) - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Bottom-Left corner
                        unitNormal = new Vector2(-root2over2, -root2over2);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (positionsX.get(i) + currentBody.getRadiusX()), testBodyPositionY - (positionsY.get(i) - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Top-Left corner
                        unitNormal = new Vector2(-root2over2, root2over2);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(testBodyPositionX - positionsX.get(i) < testBodyRadiusX + currentBody.getRadiusX()
                            && testBodyPositionX - positionsX.get(i) > -(testBodyRadiusX + currentBody.getRadiusX())
                            && testBodyPositionY - positionsY.get(i) < currentBody.getRadiusY()
                            && testBodyPositionY - positionsY.get(i) > -currentBody.getRadiusY()) {
                        unitNormal = new Vector2(-testBody.getVelocity().x / Math.abs(testBody.getVelocity().x), 0);
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(testBodyPositionX - positionsX.get(i) < currentBody.getRadiusX()
                            && testBodyPositionX - positionsX.get(i) > -(currentBody.getRadiusX())
                            && testBodyPositionY - positionsY.get(i) < (testBodyRadiusY + currentBody.getRadiusY())
                            && testBodyPositionY - positionsY.get(i) > -(testBodyRadiusY + currentBody.getRadiusY())) {
                        unitNormal = new Vector2(0, -testBody.getVelocity().y / Math.abs(testBody.getVelocity().y));
                        resolveCollisionTest2(testBody, currentBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    }
                }
            }
        }
    }

    // Helper function, should be moved to a math / geometry class
    private float getMagnitude(float dx, float dy) {
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float dotProduct(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    private void resolveCollisionTest2(Body testBody, Body collidedBody, Vector2 unitNormalOfAppliedForce, Vector2 pointOfForceApplication) {
        float magForce = Math.abs(2 * dotProduct(unitNormalOfAppliedForce, testBody.getVelocity()) * testBody.getBodyGroup().getMass() / (1/60f)); // The 1 should be replaced with mass
        Vector2 forceVector = new Vector2(magForce * unitNormalOfAppliedForce.x, magForce * unitNormalOfAppliedForce.y);

        testBody.giveForce(forceVector, pointOfForceApplication);

        ContactListener.contact(testBody, collidedBody);
    }

    public static void setPPM(float PPM) {
        BodyList.PPM = PPM;
    }

    public static float getPPM() {
        return PPM;
    }
}
