package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.nic.projectevolve.ProjectEvolve;

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

    private static final float separationDistance = 0.001f;

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
        float newPositionX = newBody.getPositionX();
        float newPositionY = newBody.getPositionY();
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
        int endingIndex = bodies.size() - 1;
        float testBodyPositionX = testBody.getPositionX();
        float testBodyPositionY = testBody.getPositionY();
        float testBodyRadiusX = testBody.getRadiusX();
        float testBodyRadiusY = testBody.getRadiusY();

        Vector2 relativePositionVector;
        float magRelativePosition;
        Vector2 unitNormal;

        boolean selfTestFlag;
        boolean collisionMaskFlag;
        boolean circleFlag = testBody.getIsCircle();
        boolean isPlayerFlag;
        // Iterate through all bodies
        // TODO Find a smarter way to do this
        int i;
        for(i = 0; i < bodies.size(); i++) {
            // Find body we are testing against
            Body currentBody = bodies.get(i);
            // Calculate and test flags
            selfTestFlag = i == bodies.indexOf(testBody);
            collisionMaskFlag = (testBody.getCollisionMask() & bodies.get(i).getCollisionIdentity()) != 0;
            isPlayerFlag = testBody.getCollisionIdentity() == ProjectEvolve.PLAYER_BIT;
            // Test flags (Only want to test collision if flags tell us to)
            if(!selfTestFlag && collisionMaskFlag && circleFlag && isPlayerFlag) {
                if(bodies.get(i).getIsCircle()) {
                    // Collided body is a circle
                    relativePositionVector = new Vector2(testBodyPositionX - currentBody.getPositionX(), testBodyPositionY - currentBody.getPositionY());
                    magRelativePosition = getMagnitude(relativePositionVector.x, relativePositionVector.y);
                    if(magRelativePosition < testBodyRadiusX + currentBody.getRadiusX()) {
                        // Resolve Collision
                        unitNormal = new Vector2(relativePositionVector.x / magRelativePosition, relativePositionVector.y / magRelativePosition);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    }
                } else {
                    // Collided body is not a circle
                    // Test corners first, diagonal normals
                    if(getMagnitude(testBodyPositionX - (currentBody.getPositionX() + currentBody.getRadiusX()), testBodyPositionY - (currentBody.getPositionY() + currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Top-Right corner
                        unitNormal = new Vector2(root2over2, root2over2);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (currentBody.getPositionX() + currentBody.getRadiusX()), testBodyPositionY - (currentBody.getPositionY() - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Bottom-Right corner
                        unitNormal = new Vector2(root2over2, -root2over2);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (currentBody.getPositionX() - currentBody.getRadiusX()), testBodyPositionY - (currentBody.getPositionY() - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Bottom-Left corner
                        unitNormal = new Vector2(-root2over2, -root2over2);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(getMagnitude(testBodyPositionX - (currentBody.getPositionX() + currentBody.getRadiusX()), testBodyPositionY - (currentBody.getPositionY() - currentBody.getRadiusY())) < testBodyRadiusX) {
                        // Top-Left corner
                        unitNormal = new Vector2(-root2over2, root2over2);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(testBodyPositionX - currentBody.getPositionX() < testBodyRadiusX + currentBody.getRadiusX()
                            && testBodyPositionX - currentBody.getPositionX() > -(testBodyRadiusX + currentBody.getRadiusX())
                            && testBodyPositionY - currentBody.getPositionY() < currentBody.getRadiusY()
                            && testBodyPositionY - currentBody.getPositionY() > -currentBody.getRadiusY()) {
                        unitNormal = new Vector2(-testBody.getVelocity().x / Math.abs(testBody.getVelocity().x), 0);
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    } else if(testBodyPositionX - currentBody.getPositionX() < currentBody.getRadiusX()
                            && testBodyPositionX - currentBody.getPositionX() > -(currentBody.getRadiusX())
                            && testBodyPositionY - currentBody.getPositionY() < (testBodyRadiusY + currentBody.getRadiusY())
                            && testBodyPositionY - currentBody.getPositionY() > -(testBodyRadiusY + currentBody.getRadiusY())) {
                        unitNormal = new Vector2(0, -testBody.getVelocity().y / Math.abs(testBody.getVelocity().y));
                        resolveCollisionTest2(testBody, unitNormal, new Vector2(-unitNormal.x * testBodyRadiusX, -unitNormal.y * testBodyRadiusX));
                    }
                }
            }
        }
    }

//    // TODO change to protected? Only let Bodies access?
//    public void testCollision(Body testBody) {
//        int originIndex = bodies.indexOf(testBody);
//        int workingIndex = originIndex - 1;
//        boolean colliding = true;
//
//        // Make all calls once to save on function calls
//        float testBodyPositionX = testBody.getPositionX();
//        float testBodyPositionY = testBody.getPositionY();
//        float testBodyRadiusX = testBody.getRadiusX();
//        float testBodyRadiusY = testBody.getRadiusY();
//        //System.out.println(testBodyPositionX);
//
//        // Check for collision mask first
//        if (testBody.getCollisionMask() != 0) {
//
//            //System.out.println("Testing a collision");
//            // Test cases for not circle
//            if (!testBody.getIsCircle()) {
//                // Test objects to the left
//                while (colliding && workingIndex >= 0) {
//                    // Conditions for not circle with not circle
//
//                    // Test for collision mask first
//                    if ((testBody.getCollisionMask() | bodies.get(workingIndex).getCollisionIdentity()) != 0) {
//
//                        if (!bodies.get(workingIndex).getIsCircle()) {
//                            // Test x positions for intersection
//                            if (Math.abs(testBodyPositionX - positionsX.get(workingIndex)) < testBodyRadiusX + bodies.get(workingIndex).getRadiusX()) {
//                                // Colliding in x direction
//                                // Test y positions for intersection
//                                if (Math.abs(testBodyPositionY - positionsY.get(workingIndex)) < testBodyRadiusY + bodies.get(workingIndex).getRadiusY()) {
//                                    // Collides in both x and y directions, there is a collision occurring
//                                    // TODO call collision code here
//                                    //System.out.println("Collision not circle not circle");
//                                }
//                            } else {
//                                colliding = false;
//                            }
//                            //workingIndex--;
//                        }
//                    }
//                    workingIndex--;
//                }
//
//                // Test objects to the right
//                workingIndex = originIndex + 1;
//                while (colliding && workingIndex < bodies.size()) {
//                    // Conditions for not circle with not circle
//
//                    // Test for collision mask first
//                    if ((testBody.getCollisionMask() | bodies.get(workingIndex).getCollisionIdentity()) != 0) {
//
//                        if (!bodies.get(workingIndex).getIsCircle()) {
//                            // Test x positions for intersection
//                            if (positionsX.get(workingIndex) - testBodyPositionX < testBodyRadiusX + bodies.get(workingIndex).getRadiusX()) {
//                                // Colliding in x direction
//                                // Test y positions for intersection
//                                if (Math.abs(testBodyPositionY - positionsY.get(workingIndex)) < testBodyRadiusY + bodies.get(workingIndex).getRadiusY()) {
//                                    // Collides in both x and y directions, there is a collision occurring
//                                    // TODO call collision code here
//                                    //System.out.println("Collision not circle not circle");
//                                }
//                            } else {
//                                colliding = false;
//                            }
//                            //workingIndex++;
//                        }
//                    }
//                    workingIndex++;
//                }
//            } else {
//                // Test cases for colliding object being a circle
//                // Test objects to the left
//                while (/*colliding &&*/ workingIndex >= 0) {
//
//                    // Test for collision mask first
//                    if ((testBody.getCollisionMask() & bodies.get(workingIndex).getCollisionIdentity()) != 0) {
//                        //System.out.println(testBody.getCollisionMask() | bodies.get(workingIndex).getCollisionIdentity());
//
//
//                        // Conditions for circle with circle
//                        if (bodies.get(workingIndex).getIsCircle()) {
//                            // Test x positions for intersection
//                            if (getMagnitude(testBodyPositionX - positionsX.get(workingIndex), testBodyPositionY - positionsY.get(workingIndex)) < testBodyRadiusX + bodies.get(workingIndex).getRadiusX()) {
//                                // Colliding
//                                // TODO call collision code here
//                                //System.out.println("Collision circle circle");
//                                resolveCollision(testBody, bodies.get(workingIndex));
//                            }
//                            //workingIndex--;
//                        } else {
//                            // Conditions for circle with non-circle
//                            boolean horizontal = false;
//                            boolean vertical = false;
//                            boolean localColliding = true;
//                            Vector2 relativePosition = new Vector2(Math.abs(testBodyPositionX - bodies.get(workingIndex).getPositionX()), Math.abs(testBodyPositionY - bodies.get(workingIndex).getPositionY()));
////                            if (relativePosition.x <= bodies.get(workingIndex).getRadiusX() + testBodyRadiusX && relativePosition.y < bodies.get(workingIndex).getRadiusY() ||
////                                    relativePosition.x < bodies.get(workingIndex).getRadiusX() && relativePosition.y < bodies.get(workingIndex).getRadiusY() + testBodyRadiusY ||
////                                    getMagnitude(relativePosition.x - bodies.get(workingIndex).getRadiusX(), relativePosition.y - bodies.get(workingIndex).getRadiusY()) < testBodyRadiusX * 2) {
////                                System.out.println("Collision");
////                            }
//                            if (relativePosition.x > bodies.get(workingIndex).getRadiusX() + testBodyRadiusX || relativePosition.y > bodies.get(workingIndex).getRadiusY() + testBodyRadiusY) {
//                                localColliding = false;
//                            }
//                            if (relativePosition.x < bodies.get(workingIndex).getRadiusX() && relativePosition.y < bodies.get(workingIndex).getRadiusY()) {
//                                localColliding = true;
//                            }
//                            if (getMagnitude(relativePosition.x - bodies.get(workingIndex).getRadiusX(), relativePosition.y - bodies.get(workingIndex).getRadiusY()) < testBodyRadiusX) {
//                                localColliding = true;
//                            }
//                            if (localColliding && relativePosition.x > bodies.get(workingIndex).getRadiusX() && relativePosition.x < bodies.get(workingIndex).getRadiusX() + testBodyRadiusX) {
//                                horizontal = true;
//                            }
//                            if (localColliding && relativePosition.y > bodies.get(workingIndex).getRadiusY() && relativePosition.y < bodies.get(workingIndex).getRadiusY() + testBodyRadiusY) {
//                                vertical = true;
//                            }
//                            if (localColliding) {
//                                //resolveCollisionCircleRectangle(testBody, horizontal, vertical);
//                                Vector2 rhat = new Vector2(testBodyPositionX - bodies.get(workingIndex).getRadiusX(), testBodyPositionY - bodies.get(workingIndex).getRadiusY());
//                                //System.out.println(bodies.get(workingIndex).getRadiusX());
//                                //System.out.println(testBodyPositionX);
//                                resolveCollisionTEST(testBody, bodies.get(workingIndex), new Vector2(rhat.x, rhat.y));
//                                //System.out.println("Collision circle not circle");
//                            }
//                        }
//                    }
//                    workingIndex--;
//                }
//
//                // Test objects to the right
//                workingIndex = originIndex + 1;
//                while (/*colliding &&*/ workingIndex < bodies.size()) {
//                    // Conditions for circle with circle
//
//                    // Test for collision mask first
//                    if ((testBody.getCollisionMask() & bodies.get(workingIndex).getCollisionIdentity()) != 0) {
//
//
//                        if (bodies.get(workingIndex).getIsCircle()) {
//                            // Test distance between centers against sum of radius
//                            if (getMagnitude(testBodyPositionX - positionsX.get(workingIndex), testBodyPositionY - positionsY.get(workingIndex)) < testBodyRadiusX + bodies.get(workingIndex).getRadiusX()) {
//                                // Colliding
//                                // TODO call collision code here
//                                //System.out.println("Collision circle circle");
//                                resolveCollision(testBody, bodies.get(workingIndex));
//                            }
//                            //workingIndex++;
//                        } else {
//                            // Conditions for circle with non-circle
//                            boolean horizontal = false;
//                            boolean vertical = false;
//                            boolean localColliding = true;
//                            Vector2 relativePosition = new Vector2(Math.abs(testBodyPositionX - bodies.get(workingIndex).getPositionX()), Math.abs(testBodyPositionY - bodies.get(workingIndex).getPositionY()));
////                            if (relativePosition.x <= bodies.get(workingIndex).getRadiusX() + testBodyRadiusX && relativePosition.y < bodies.get(workingIndex).getRadiusY() ||
////                                    relativePosition.x < bodies.get(workingIndex).getRadiusX() && relativePosition.y < bodies.get(workingIndex).getRadiusY() + testBodyRadiusY ||
////                                    getMagnitude(relativePosition.x - bodies.get(workingIndex).getRadiusX(), relativePosition.y - bodies.get(workingIndex).getRadiusY()) < testBodyRadiusX * 2) {
////                                System.out.println("Collision");
////                            }
//                            if (relativePosition.x > bodies.get(workingIndex).getRadiusX() + testBodyRadiusX || relativePosition.y > bodies.get(workingIndex).getRadiusY() + testBodyRadiusY) {
//                                localColliding = false;
//                            }
//                            if (relativePosition.x < bodies.get(workingIndex).getRadiusX() && relativePosition.y < bodies.get(workingIndex).getRadiusY()) {
//                                localColliding = true;
//                            }
//                            if (getMagnitude(relativePosition.x - bodies.get(workingIndex).getRadiusX(), relativePosition.y - bodies.get(workingIndex).getRadiusY()) < testBodyRadiusX) {
//                                localColliding = true;
//                                horizontal = true;
//                                vertical = true;
//                            }
//                            if (localColliding && relativePosition.x > bodies.get(workingIndex).getRadiusX() && relativePosition.x < bodies.get(workingIndex).getRadiusX() + testBodyRadiusX) {
//                                horizontal = true;
//                            }
//                            if (localColliding && relativePosition.y > bodies.get(workingIndex).getRadiusY() && relativePosition.y < bodies.get(workingIndex).getRadiusY() + testBodyRadiusY) {
//                                vertical = true;
//                            }
//                            if (localColliding) {
//                                //resolveCollisionCircleRectangle(testBody, horizontal, vertical);
//                                Vector2 rhat = new Vector2(testBodyPositionX - bodies.get(workingIndex).getRadiusX(), testBodyPositionY - bodies.get(workingIndex).getRadiusY());
//                                //System.out.println(rhat.x);
//                                //System.out.println(rhat.y);
//                                resolveCollisionTEST(testBody, bodies.get(workingIndex), horizontal, vertical);//new Vector2(rhat.x / rhat.len(), rhat.y / rhat.len()));
//                                //System.out.println("Collision circle not circle");
//                            }
//                        }
//                    }
//
//                    workingIndex++;
//                }
//
//            }
//
//        }
//
//    }

    private void resolveCollision(Body a, Body b) {
        // BODY A IS THE ONE THAT MOVED AND COLLIDED, BODY B WAS COLLIDED WITH
        if (a.getIsCircle() && b.getIsCircle()) {
            // Case that both objects are circles
            float relativeX = a.getPositionX() - b.getPositionX();
            float relativeY = a.getPositionY() - b.getPositionY();
            //Vector2 normal = new Vector2(relativeX, relativeY);
            Vector2 unitNormal = new Vector2(relativeX / getMagnitude(relativeX, relativeY), relativeY / getMagnitude(relativeX, relativeY));
            Vector2 unitParallel = new Vector2(unitNormal.y, unitNormal.x);
            if (unitNormal.x >= 0 && unitNormal.y > 0) {
                unitParallel.x = -unitParallel.x;
            } else if (unitNormal.x < 0 && unitNormal.y > 0) {
                unitParallel.y = -unitParallel.y;
            } else if (unitNormal. x <= 0 && unitNormal.y < 0) {
                unitParallel.x = -unitParallel.x;
            } else if (unitNormal.x > 0 && unitNormal.y < 0) {
                unitParallel.y = -unitParallel.y;
            }

            float theta = (float) Math.atan((a.getPositionY() - b.getPositionY()) / (a.getPositionX() - b.getPositionX()));
            float minSeparationDistance = a.getRadiusX() + b.getRadiusX() + separationDistance;

            float newPositionXa = (float) Math.cos(theta) * minSeparationDistance;
            float newPositionYa = (float) Math.sin(theta) * minSeparationDistance;
            if (relativeX < 0) {
                newPositionXa = -newPositionXa;
                newPositionYa = -newPositionYa;
            }
            newPositionXa += b.getPositionX();
            newPositionYa += b.getPositionY();

            //a.setPosition(new Vector2(newPositionXa, newPositionYa));

            // Change perpendicular velocity to bounce off of object
            Vector2 velocityPerpendicular = new Vector2(unitNormal.x * dotProduct(unitNormal, a.getVelocity()), unitNormal.y * dotProduct(unitNormal, a.getVelocity()));
            velocityPerpendicular.scl(.5f);
            Vector2 velocityParallel = new Vector2(unitParallel.x * dotProduct(unitParallel, a.getVelocity()), unitParallel.y * dotProduct(unitParallel, a.getVelocity()));
            //float magVelocityPerpendicular = getMagnitude(velocityPerpendicular.x, velocityPerpendicular.y);
            // For now set body a's velocity to the perpendicular velocity
            a.setVelocity(new Vector2(-velocityPerpendicular.x + velocityParallel.x, -velocityPerpendicular.y + velocityParallel.y));
            //a.setVelocity(new Vector2(0, -1));

            ContactListener.contact(a, b);
        }
    }

    // Helper function, should be moved to a math / geometry class
    private float getMagnitude(float dx, float dy) {
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private float dotProduct(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }

    private void resolveCollisionCircleRectangle(Body a, boolean horizontal, boolean vertical) {
        // THIS CODE IS NOT PART OF THE PHYSICS ENGINE, THIS IS SPECIFIC TO THE PROJECT EVOLVE GAME
        if (horizontal) {
            //a.getPlayer().setVelocity(-a.getVelocity().x, a.getVelocity().y);
            a.setVelocity(new Vector2(-a.getVelocity().x, a.getVelocity().y));
        }
        if (vertical) {
            //a.getPlayer().setVelocity(a.getVelocity().x, -a.getVelocity().y);
            a.setVelocity(new Vector2(a.getVelocity().x, -a.getVelocity().y));
        }
        //a.getPlayer().setPosition(a.getPlayer().getPosition().add(a.getVelocity().scl(0.2f)));
        //a.getVelocity().scl(1/0.2f);
        //a.setPosition();

        //ContactListener.contact(a, b);
    }

    private void resolveCollisionTEST(Body a, Body b, boolean horiz, boolean vertical) {//Vector2 rHat) {
        float relativeVelocity = (float) Math.sqrt(Math.pow(a.getVelocity().x - b.getVelocity().x, 2) + Math.pow(a.getVelocity().y - b.getVelocity().y,2));
        //System.out.print("Relative Velocity: ");
        //System.out.println(rHat.x);
        //System.out.println(rHat.y);
        Vector2 rHat;
        if (horiz && !vertical) {
            rHat = new Vector2(1, 0);
        } else if (!horiz && vertical) {
            rHat = new Vector2(0, 1);
        } else {
            rHat = new Vector2();
        }
        float deltaMomentum = -2 * relativeVelocity * 1f;
        // TODO Time needed? if so make it a constant
        float force = deltaMomentum / 60f;
        a.unUpdate();
        //a.giveForce(new Vector2(force * rHat.x, force * rHat.y));

        System.out.println("Resolving Collision, test function");
    }

    private void resolveCollisionTest2(Body testBody, Vector2 unitNormalOfAppliedForce, Vector2 pointOfForceApplication) {
        System.out.println("Collision Detected");
//        System.out.print("Unit Normal: ");
//        System.out.print(pointOfForceApplication.x);
//        System.out.print(", ");
//        System.out.println(pointOfForceApplication.y);
        //Collisions so far being processed normally



        float magForce = Math.abs(2 * dotProduct(unitNormalOfAppliedForce, testBody.getVelocity()) * 1 / (1/60f)); // The 1 should be replaced with mass
        Vector2 forceVector = new Vector2(magForce * unitNormalOfAppliedForce.x, magForce * unitNormalOfAppliedForce.y);
//        System.out.print("Force x: ");
//        System.out.println(forceVector.x);
//        System.out.print("Force y: ");
//        System.out.println(forceVector.y);

//        float parallelScalar = dotProduct(unitNormalOfAppliedForce, new Vector2(-pointOfForceApplication.x, -pointOfForceApplication.y)) / (pointOfForceApplication.x * pointOfForceApplication.x + pointOfForceApplication.y * pointOfForceApplication.y);
//        Vector2 parallelForce = new Vector2(pointOfForceApplication.x * parallelScalar, pointOfForceApplication.y * parallelScalar);
//        Vector2 perpendicularForce = unitNormalOfAppliedForce.sub(parallelForce);
//        parallelForce = new Vector2(parallelForce.x * magForce, parallelForce.y * magForce);
//        perpendicularForce = new Vector2(perpendicularForce.x * magForce, perpendicularForce.y * magForce);
//        int directionPerpendicularForce = (int) ((perpendicularForce.x * perpendicularForce.y - perpendicularForce.y * perpendicularForce.x) /
//                Math.abs(perpendicularForce.x * perpendicularForce.y - perpendicularForce.y * perpendicularForce.x));

        testBody.giveForce(forceVector, pointOfForceApplication);
        //testBody.giveForce(parallelForce, directionPerpendicularForce * getMagnitude(perpendicularForce.x, perpendicularForce.y), pointOfForceApplication);
    }
}
