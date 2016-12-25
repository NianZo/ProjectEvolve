package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
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

    private Vector2 cg;
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

    public BodyGroup(Vector2 position) {
        // Initialize the ArrayList of bodies so it can be added to later
        bodies = new ArrayList<Body>();
        numBodies = 0;

        // TODO I believe these also need to be dynamic based on modules
        // Initialize physical characteristics
        mass = 1;
        momentOfInertia = 1;

        // Initialize movement characteristics of the body group to zero
        rotation = 0;
        velocity = new Vector2(0, 0);
        angularVelocity = 0;

        // Initialize forces on the body group to zero
        totalLongitudinalForce = new Vector2(0, 0);
        totalTorque = 0;

        // TODO calculate cg based on bodies
        cg = position;

        // TODO these probably should be specified from game code and passed in instead of being hard coded
        maxVelocity = 2;
        maxAngularVelocity = 90;
    }

    public void addBody(Body body) {
        bodies.add(body);
        numBodies++;
    }

    public void update(float dt) {
        lastDt = dt;
        float xOffset;
        float yOffset;

        // Update velocities based on forces from last collisions / inputs
        velocity = new Vector2(totalLongitudinalForce.x / 60f / mass + velocity.x, totalLongitudinalForce.y / 60f / mass + velocity.y);
        // Clamp velocities within maxVelocity
        if(velocity.x > maxVelocity) {
            velocity.x = maxVelocity;
        } else if(velocity.x < -maxVelocity) {
            velocity.x = -maxVelocity;
        }
        if(velocity.y > maxVelocity) {
            velocity.y = maxVelocity;
        } else if(velocity.y < -maxVelocity) {
            velocity.y = -maxVelocity;
        }

        // Update angular velocity based on forces from last collisions / inputs
        angularVelocity += totalTorque * 1.5f / momentOfInertia;
        // Clamp angular velocity within maxAngularVelocity
        if(angularVelocity > maxAngularVelocity) {
            angularVelocity = maxAngularVelocity;
        } else if(angularVelocity < -maxAngularVelocity) {
            angularVelocity = -maxAngularVelocity;
        }

        // Decelerate the body group
        // TODO make this a percentage of maxVelocities? Also add normal velocity to this
        angularVelocity -= 1 * angularVelocity * dt;

        // Reset forces and torques
        totalLongitudinalForce = new Vector2(0, 0);
        totalTorque = 0;

        // Update position and cg with calculated velocity values
        cg.x += velocity.x * dt;
        cg.y += velocity.y * dt;

        // Update rotation with angularVelocity
        rotation += angularVelocity * dt;

        // Cosine and Sine of the player's rotation property; used for calculating positions of bodies
        xOffset = (float) Math.cos(Math.toRadians(rotation));
        yOffset = (float) Math.sin(Math.toRadians(rotation));

        // Update all modules
        for (int i = 0; i < numBodies; i++) {
            int index = bodies.get(i).getIndex();
            bodies.get(i).update(new Vector2(cg.x + (ProjectEvolve.MODULELOCATIONS[index][0] * xOffset - ProjectEvolve.MODULELOCATIONS[index][1] * yOffset) / ProjectEvolve.PPM, cg.y + (ProjectEvolve.MODULELOCATIONS[index][1] * xOffset + ProjectEvolve.MODULELOCATIONS[index][0] * yOffset) / ProjectEvolve.PPM), velocity, rotation);
        }
    }

    public void applyForce(Vector2 force, Vector2 pointOfApplication, Body bodyOfApplication) {
        // Calculate the vector from the center of gravity to the point of force application and its magnitude
        Vector2 rVector = new Vector2(bodyOfApplication.getPosition().x - cg.x + pointOfApplication.x, bodyOfApplication.getPosition().y - cg.y + pointOfApplication.y);
        float rVectorMag = rVector.len();

        // TODO still getting a clipping glitch, modules not being properly moved out of collision
        // TODO causes modules to be shot off at high speeds on some collision
        // UnUpdate the position of the body to be outside of collision
        if(rVectorMag != 0) {
            cg.x -= velocity.x * Math.abs(force.x) / force.len() * lastDt;
            cg.y -= velocity.y * Math.abs(force.y) / force.len() * lastDt;
            rotation += angularVelocity * lastDt; // I still don't know why this is add instead of subtract?
        }

        // If point of application is on cg then simply add the applied force
        if(rVectorMag == 0) {
            totalLongitudinalForce = new Vector2(totalLongitudinalForce.x + force.x, totalLongitudinalForce.y + force.y);
        }
        // If point of application is not cg then add the force in the direction of -rVector; then add torque (rVector X force)
        else {
            totalLongitudinalForce = new Vector2(totalLongitudinalForce.x + Math.abs(force.x) * -rVector.x / rVectorMag, totalLongitudinalForce.y + Math.abs(force.y) * -rVector.y / rVectorMag);
            totalTorque += rVector.x * force.y - rVector.y * force.x;
        }
    }

    public Vector2 getPosition() {
        return cg;
    }
}
