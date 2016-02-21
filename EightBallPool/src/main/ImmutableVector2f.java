package main;

import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.util.FastTrig;

/**
 * A two dimensional immutable vector. Intended to be a drop-in replacement for
 * Vector2f where immutability is required.
 * <p>
 * Works exactly like Vector2f, except that any operations that would normally
 * change this vector create and return a new vector instead. It offers most
 * methods in flavors that accept ImmutableVector2f or Vector2f as input.
 * <p>
 * 
 * 
 * @author Kevin Glass, Cameron seebach
 */
public strictfp class ImmutableVector2f {

   /** The x component of this vector */
   public final float x;
   /** The y component of this vector */
   public final float y;

   /**
    * Create an empty vector.
    */
   public ImmutableVector2f() {
      this(0, 0);
   }

   /**
    * Create a new vector based on an angle
    * 
    * @param theta
    *            The angle of the vector in degrees
    */
   public ImmutableVector2f(double theta) {
      this(new ImmutableVector2f(1, 0).setTheta(theta));
   }

   /**
    * Create a new vector
    * 
    * @param x
    *            The x component to assign
    * @param y
    *            The y component to assign
    */
   public ImmutableVector2f(float x, float y) {
      this.x = x;
      this.y = y;
   }

   /**
    * Create a vector based on the contents of a coordinate array
    * 
    * @param coords
    *            The coordinates array, index 0 = x, index 1 = y
    */
   public ImmutableVector2f(float[] coords) {
      this(coords[0], coords[1]);
   }

   /**
    * Create a new vector based on another
    * 
    * @param other
    *            The other vector to copy into this one
    */
   public ImmutableVector2f(ImmutableVector2f other) {
      this(other.getX(), other.getY());
   }

   /**
    * Create a new vector based on another
    * 
    * @param other
    *            The other vector to copy into this one.
    */
   public ImmutableVector2f(Vector2f other) {
      this(other.x, other.y);
   }

   /**
    * Adjust this vector by a given angle, and return a new vector with the
    * results.
    * 
    * @param theta
    *            The angle to adjust the angle by (in degrees)
    * @return a new vector with the results
    */
   public ImmutableVector2f add(double theta) {
      return setTheta(getTheta() + theta);
   }

   /**
    * Add a vector to this vector, and return a new vector containing the
    * result.
    * 
    * @param v
    *            The vector to add to this one
    * @return The result vector
    */
   public ImmutableVector2f add(ImmutableVector2f v) {
      float x = this.x + v.getX();
      float y = this.y + v.getY();
      return new ImmutableVector2f(x, y);
   }

   /**
    * Add a vector to this vector, and return a new vector containing the
    * result.
    * 
    * @param v
    *            The vector to add to this one
    * @return The result vector
    */
   public ImmutableVector2f add(Vector2f v) {
      float x = this.x + v.getX();
      float y = this.y + v.getY();
      return new ImmutableVector2f(x, y);
   }

   /**
    * Return a copy of this vector
    * 
    * @return The new instance that copies this vector
    */
   public ImmutableVector2f copy() {
      return new ImmutableVector2f(x, y);
   }

   /**
    * Get the distance from this point to another
    * 
    * @param other
    *            The other point we're measuring to
    * @return The distance to the other point
    */
   public float distance(ImmutableVector2f other) {
      float dx = other.getX() - getX();
      float dy = other.getY() - getY();

      return (float) Math.sqrt((dx * dx) + (dy * dy));
   }

   /**
    * Get the distance from this point to another
    * 
    * @param other
    *            The other point we're measuring to
    * @return The distance to the other point
    */
   public float distance(Vector2f other) {
      float dx = other.getX() - getX();
      float dy = other.getY() - getY();

      return (float) Math.sqrt((dx * dx) + (dy * dy));
   }

   /**
    * Dot this vector against another
    * 
    * @param other
    *            The other vector dot agianst
    * @return The dot product of the two vectors
    */
   public float dot(ImmutableVector2f other) {
      return (x * other.getX()) + (y * other.getY());
   }

   /**
    * Dot this vector against another
    * 
    * @param other
    *            The other vector dot agianst
    * @return The dot product of the two vectors
    */
   public float dot(Vector2f other) {
      return (x * other.getX()) + (y * other.getY());
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Object other) {
      if (other instanceof ImmutableVector2f) {
         ImmutableVector2f o = ((ImmutableVector2f) other);
         return (o.x == x) && (o.y == y);
      }

      return false;
   }

   /**
    * Compares this vector with a Vector2f and returns true if they currently
    * represent the same vector.
    * <p>
    * Note: <br>
    * Breaks the general contract for the hashCode() method. ImmutableVector2f
    * implements hashing differently from Vector2f. The hashCode() for this
    * ImmutableVector2f and the hashCode() for the other Vector2f will almost
    * never be the same, even though they may represent the same 2D vector.
    * 
    * @param other
    *            the vector to compare to this vector
    * 
    * @return true if the vectors are equal, false otherwise
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   public boolean equals(Vector2f other) {
      return (other.x == x) && (other.y == y);
   }

   /**
    * The normal of the vector
    * 
    * @return A unit vector with the same direction as the vector
    * 
    */
   public ImmutableVector2f getNormal() {
      return normalise();
   }

   /**
    * Get the angle this vector is at
    * 
    * @return The angle this vector is at (in degrees)
    */
   public double getTheta() {
      double theta = StrictMath.toDegrees(StrictMath.atan2(y, x));
      if ((theta < -360) || (theta > 360)) {
         theta = theta % 360;
      }
      if (theta < 0) {
         theta = 360 + theta;
      }

      return theta;
   }

   /**
    * Get the x component
    * 
    * @return The x component
    */
   public float getX() {
      return x;
   }

   /**
    * Get the y component
    * 
    * @return The y component
    */
   public float getY() {
      return y;
   }

   /**
    * Returns the hashCode() for this ImmutableVector2f.
    * <p>
    * Computes the hash code by these three steps:
    * 
    * <pre>
    * int bitsX = Float.floatToRawIntBits(x);
    * int bitsY = Float.floatToRawIntBits(y);
    * return 997 * bitsX + 991 * bitsY;s
    * </pre>
    * 
    * Returns values different from hashCode() of Vector2f. Appears to create a
    * pretty even spread across the entire range of floats, but has not been
    * tested or proven extensively.
    * 
    * @see java.lang.Object#hashCode()
    */
   public int hashCode() {
      int bitsX = Float.floatToRawIntBits(x);
      int bitsY = Float.floatToRawIntBits(y);
      return 997 * bitsX + 991 * bitsY;
   }

   /**
    * Get the length of this vector
    * 
    * @return The length of this vector
    */
   public float length() {
      return (float) Math.sqrt(lengthSquared());
   }

   /**
    * The length of the vector squared
    * 
    * @return The length of the vector squared
    */
   public float lengthSquared() {
      return (x * x) + (y * y);
   }

   /**
    * Returns a Vector2f that represents the same vector as this one.
    * 
    * @return a Vector2f that represents the same vector as this one
    */
   public Vector2f makeVector2f() {
      return new Vector2f(x, y);
   }

   /**
    * Returns a negated copy of this vector.
    * 
    * @return A copy of this vector negated
    */
   public ImmutableVector2f negate() {
      return new ImmutableVector2f(-x, -y);
   }

   /**
    * Returns a negated copy of this vector. Same as negate, kept here for
    * compatibility.
    * 
    * @return A copy of this vector negated
    * 
    * @see ImmutableVector2f#negate()
    */
   public ImmutableVector2f negateLocal() {
      return negate();
   }

   /**
    * Returns a normalised copy of this vector. Same as getNormal()
    * 
    * @return a normalised copy of this vector
    * 
    * @see ImmutableVector2f#getNormal()
    */
   public ImmutableVector2f normalise() {
      float l = length();

      float x = this.x / l;
      float y = this.y / l;
      return new ImmutableVector2f(x, y);
   }

   /**
    * Project this vector onto another. Returns a new vector with the results
    * of the projection.
    * 
    * @param b
    *            The vector to project onto
    * @return The projected vector
    */
   public ImmutableVector2f projectOntoUnit(ImmutableVector2f b) {
      float dp = b.dot(this);

      float x = dp * b.getX();
      float y = dp * b.getY();
      return new ImmutableVector2f(x, y);
   }

   /**
    * Project this vector onto another. Returns a new vector with the results
    * of the projection.
    * 
    * @param b
    *            The vector to project onto
    * 
    * @return The projected vector
    */
   public ImmutableVector2f projectOntoUnit(Vector2f b) {
      float dp = this.dot(b);

      float x = dp * b.getX();
      float y = dp * b.getY();
      return new ImmutableVector2f(x, y);
   }

   /**
    * Project this vector onto another. Writes the results to result.
    * 
    * @param b
    *            The vector to project onto
    * @param result
    *            The projected vector
    */
   public void projectOntoUnit(ImmutableVector2f b, Vector2f result) {
      float dp = b.dot(this);

      float x = dp * b.getX();
      float y = dp * b.getY();
      result.set(x, y);
   }

   /**
    * Project this vector onto another. Writes the results to result.
    * 
    * @param b
    *            The vector to project onto
    * @param result
    *            The projected vector
    */
   public void projectOntoUnit(Vector2f b, Vector2f result) {
      float dp = this.dot(b);

      float x = dp * b.getX();
      float y = dp * b.getY();
      result.set(x, y);
   }

   /**
    * Scale this vector by a value, and return the result.
    * 
    * @param a
    *            The value to scale this vector by
    * @return a new vector which holds the results of scaling this vector
    */
   public ImmutableVector2f scale(float a) {
      float x = this.x * a;
      float y = this.y * a;

      return new ImmutableVector2f(x, y);
   }

   /**
    * Make a new vector with the same length as this one, but with the angle
    * specified by theta. Return the new vector.
    * 
    * @param theta
    *            The angle to calculate the components from (in degrees)
    * @return a new vector with the same length as this one, but with the angle
    *         specified by theta.
    */
   public ImmutableVector2f setTheta(double theta) {
      // Next lines are to prevent numbers like -1.8369701E-16
      // when working with negative numbers
      if ((theta < -360) || (theta > 360)) {
         theta = theta % 360;
      }
      if (theta < 0) {
         theta = 360 + theta;
      }

      float len = length();
      float x = len * (float) FastTrig.cos(StrictMath.toRadians(theta));
      float y = len * (float) FastTrig.sin(StrictMath.toRadians(theta));
      return new ImmutableVector2f(x, y);
   }

   /**
    * Return a new vector which is this vector adjusted by a given angle. The
    * negative version of add(double theta).
    * 
    * @param theta
    *            The angle to adjust the angle by (in degrees)
    * @return a new vector with the results of adjustment
    */
   public ImmutableVector2f sub(double theta) {
      return setTheta(getTheta() - theta);
   }

   /**
    * Subtract a vector from this vector, and return a new vector with the
    * results.
    * 
    * @param v
    *            The vector to subtract from this one
    * @return a new vector with the results
    */
   public ImmutableVector2f sub(ImmutableVector2f v) {
      float x = this.x - v.getX();
      float y = this.y - v.getY();

      return new ImmutableVector2f(x, y);
   }
   
   /**
    * Subtract a vector from this vector, and return a new vector with the
    * results.
    * 
    * @param v
    *            The vector to subtract from this one
    * @return a new vector with the results
    */
   public ImmutableVector2f sub(Vector2f v) {
      float x = this.x - v.getX();
      float y = this.y - v.getY();

      return new ImmutableVector2f(x, y);
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return "[ImmutableVector2f " + x + "," + y + " (" + length() + ")]";
   }
}