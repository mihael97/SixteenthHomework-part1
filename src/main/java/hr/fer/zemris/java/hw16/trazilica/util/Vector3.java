package hr.fer.zemris.java.hw16.trazilica.util;

import java.util.Objects;
import static java.lang.Math.pow;

/**
 * Class represents implementation of polynomial vector with mathematics
 * operations
 * 
 * @author Mihael
 *
 */
public class Vector3 {

	/**
	 * Coefficients
	 */
	private double[] coordinates;

	/**
	 * Constructor initializes new vector
	 * 
	 * @param coordinates
	 *            - coordinates
	 */
	public Vector3(double... coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Method returns vector module
	 * 
	 * @return vector module
	 */
	public double norm() {
		double sum = 0;

		for (double number : coordinates) {
			sum += pow(number, 2);
		}

		return Math.sqrt(sum);
	}

	/**
	 * Method calculates dot product of this vector and argument
	 * 
	 * @param other
	 *            - second vector
	 * @return double value of dot product between two vectors
	 * 
	 * @throws NullPointerException
	 *             - if argument is null
	 */
	public double dot(Vector3 other) {
		Objects.requireNonNull(other);
		double sum = 0;
		double[] coordinatesOther = other.toArray();

		for (int i = 0; i < coordinates.length; i++) {
			sum += coordinates[i] * coordinatesOther[i];
		}

		return sum;
	}

	/**
	 * Returns the cosine of the angle between this vector and the specified vector.
	 *
	 * @param other
	 *            the vector
	 * @return double cosine value
	 */
	public double cosAngle(Vector3 other) {
		return Math.cos(Math.acos(this.dot(other) / (this.norm() * other.norm())));
	}

	/**
	 * Method returns coefficients of vector
	 * 
	 * @return coefficients
	 */
	public double[] toArray() {
		return coordinates;
	}

	/**
	 * Static method multiplies two vector
	 * 
	 * @param vector1
	 *            - first vector coefficients
	 * @param vector2
	 *            - second vector coefficients
	 * @return new Vector which is made like multiply result of given vectors
	 */
	public static Vector3 multiply(double[] vector1, double[] vector2) {
		if (vector1.length != vector2.length) {
			throw new IllegalArgumentException("Vectors must have same numebr of coeffcients,but one has "
					+ vector1.length + ",and other " + vector2.length);
		}

		double[] array = new double[vector1.length];

		for (int i = 0, size = vector1.length; i < size; i++) {
			array[i] = vector1[i] * vector2[i];
		}

		return new Vector3(array);
	}
}
