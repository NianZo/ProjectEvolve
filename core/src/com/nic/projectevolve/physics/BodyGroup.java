package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

/**
 * Created by nic on 12/23/16.
 *
 * A body group handles a set of bodies. All of a bodyGroup's bodies report forces to the bodyGroup
 * which resolves them and updates all bodies after collecting all forces.
 *
 * In terms of Box2D, think of this as a body and bodies as fixtures.
 */
public class BodyGroup {
    private ArrayList<Body> bodies;
    private int numBodies;

    private BodyList bodyList;

    private Vector2 cg; // position of cg relative to center of module zero
    private Vector2 relativeCG;
    private Vector2 position;
    private float rotation;
    private Vector2 velocity;
    private float maxVelocity;
    private float angularVelocity;
    private float maxAngularVelocity;
    private float mass;
    private float momentOfInertia; // technically a vector, but always in +z direction in two-space
    
    private Vector2 totalLongitudinalForce;
    private float totalTorque;

    private float lastDt;

    private float xOffset;
    private float yOffset;

//    private Vector2 lastPosition;
//    private float lastRotation;

    public BodyGroup(Vector2 position, BodyList bodyList, float maxVelocity, float maxAngularVelocity) {
        // Initialize the ArrayList of bodies so it can be added to later
        bodies = new ArrayList<Body>();
        numBodies = 0;

        this.bodyList = bodyList;

        // Initialize movement characteristics of the body group to zero
        rotation = 0;
        velocity = new Vector2(0, 0);
        angularVelocity = 0;

        // Initialize forces on the body group to zero
        totalLongitudinalForce = new Vector2(0, 0);
        totalTorque = 0;

        cg = new Vector2(0, 0);
        relativeCG = new Vector2(0, 0);
        this.position = position;

        // Initialize physical characteristics
        mass = 0;
        momentOfInertia = 1;
        this.maxVelocity = maxVelocity;
        this.maxAngularVelocity = maxAngularVelocity;
    }

    public void addBody(Body body) {
        bodies.add(body);
        numBodies++;

        // Update mass
        mass += body.getMass();

        // Calculate new cg as average of all modules' locations relative to position
        Vector2 cgRunningTotal = new Vector2(0, 0);
        for(int i = 0; i < numBodies; i++) {
            cgRunningTotal.x += bodies.get(i).getOffset().x;
            cgRunningTotal.y += bodies.get(i).getOffset().y;
        }
        relativeCG.x = cgRunningTotal.x / numBodies;
        relativeCG.y = cgRunningTotal.y / numBodies;

        // Update moment of inertia
        momentOfInertia = 0;
        for(int i = 0; i < numBodies; i++) {
            momentOfInertia += bodies.get(i).getMass() * (bodies.get(i).getOffset().len() - relativeCG.len()) / BodyList.getPPM();
        }
        // Make sure the moment of inertia is not zero since we divide by it
        if(momentOfInertia == 0) {
            momentOfInertia = 1;
        }
    }

    public void update(float dt) {
        // Store dt in case need to unUpdate when applying forces
        lastDt = dt;

        // Update velocities based on forces from last collisions / inputs
        velocity = new Vector2(totalLongitudinalForce.x / 60f / mass + velocity.x, totalLongitudinalForce.y / 60f / mass + velocity.y);
        // Clamp velocities within maxVelocity
         velocity = PhysicsMath.clampVectorBelow(velocity, maxVelocity, true, true, true, true);

        // Update angular velocity based on forces from last collisions / inputs
        angularVelocity += totalTorque * 1.5f / momentOfInertia;
        // Clamp angular velocity within maxAngularVelocity
        angularVelocity = PhysicsMath.clampBelow(angularVelocity, maxAngularVelocity, true, true);

        // Reset forces and torques after applying them
        totalLongitudinalForce = new Vector2(0, 0);
        totalTorque = 0;

        // Update position and cg with calculated velocity values
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // Update rotation with angularVelocity
        rotation += angularVelocity * dt;

        // Calculate the cg and offsets
        calculateCG();

        // Update all modules
        for (int i = 0; i < numBodies; i++) {
            bodies.get(i).update(new Vector2(position.x + (bodies.get(i).getOffset().x * xOffset - bodies.get(i).getOffset().y * yOffset) / BodyList.getPPM(), position.y + (bodies.get(i).getOffset().y * xOffset + bodies.get(i).getOffset().x * yOffset) / BodyList.getPPM()), velocity, rotation);
        }

        // Decelerate the body group
        angularVelocity -= angularVelocity * dt;
        velocity.x -= .1 * velocity.x * dt;
        velocity.y -= .1 * velocity.y * dt;
    }

    public void applyForce(Vector2 force, Vector2 pointOfApplication, Body bodyOfApplication) {

        if(pointOfApplication.len() != 0) {
            // Clamp the velocity to be above .1f, helps unUpdate out of collision
            velocity = PhysicsMath.clampVectorAbove(velocity, .2f, force.x > 0, force.y > 0, force.x < 0, force.y < 0);

            // UnUpdate the position of the body to be outside of collision
            position.x -= velocity.x * Math.abs(force.x) / force.len() * lastDt;
            position.y -= velocity.y * Math.abs(force.y) / force.len() * lastDt;
            rotation -= angularVelocity * lastDt;
            //This probably will work, but there are issues with applying force by touch
//            position = lastPosition;
//            rotation = lastRotation;

            // Calculate the cg and offsets
            calculateCG();

            // Calculate the vector from the center of gravity to the point of force application and its magnitude
            Vector2 rVector = new Vector2(bodyOfApplication.getPosition().x - position.x - cg.x + pointOfApplication.x, bodyOfApplication.getPosition().y - position.y - cg.y + pointOfApplication.y);
            float rVectorMag = rVector.len();

            totalLongitudinalForce = new Vector2(totalLongitudinalForce.x + Math.abs(force.x) * -rVector.x / rVectorMag, totalLongitudinalForce.y + Math.abs(force.y) * -rVector.y / rVectorMag);
            totalTorque += rVector.x * force.y - rVector.y * force.x;
        }
        // If point of application is on cg then simply add the applied force
        else {
            totalLongitudinalForce = new Vector2(totalLongitudinalForce.x + force.x, totalLongitudinalForce.y + force.y);
        }
    }

    private void calculateCG() {
        // Cosine and Sine of the player's rotation property; used for calculating positions of bodies
        xOffset = (float) Math.cos(Math.toRadians(rotation));
        yOffset = (float) Math.sin(Math.toRadians(rotation));

        // Update cg based on xOffset and yOffset
        cg.x = (relativeCG.x * xOffset - relativeCG.y * yOffset) / BodyList.getPPM();
        cg.y = (relativeCG.y * xOffset + relativeCG.x * yOffset) / BodyList.getPPM();
    }

    public Vector2 getPosition() {
        return position;
    }

    public BodyList getBodyList() {
        return bodyList;
    }

    public float getMass() {
        return mass;
    }

    public void setMaxVelocity(float maxSpeed) {
        maxVelocity = maxSpeed;
    }
}
